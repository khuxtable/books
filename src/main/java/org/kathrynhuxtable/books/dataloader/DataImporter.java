/*
 * Copyright 2002-2018 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kathrynhuxtable.books.dataloader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.kathrynhuxtable.books.persistence.dao.AuthorDAO;
import org.kathrynhuxtable.books.persistence.dao.BorrowerDAO;
import org.kathrynhuxtable.books.persistence.dao.TitleDAO;
import org.kathrynhuxtable.books.persistence.dao.VolumeDAO;
import org.kathrynhuxtable.books.persistence.domain.Author;
import org.kathrynhuxtable.books.persistence.domain.Borrower;
import org.kathrynhuxtable.books.persistence.domain.Title;
import org.kathrynhuxtable.books.persistence.domain.Volume;
import org.kathrynhuxtable.books.service.DataLoaderResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataImporter {

	@Autowired
	private AuthorDAO authorDao;
	@Autowired
	private TitleDAO titleDao;
	@Autowired
	private VolumeDAO volumeDao;
	@Autowired
	private BorrowerDAO borrowerDao;

	public List<DataLoaderResult> load(File file) {
		DataFileHandler handler;
		try {
			handler = new DataFileHandler(file);
		} catch (FileHandlerDataException e) {
			return e.getErrors();
		} catch (IOException e) {
			e.printStackTrace();
			List<DataLoaderResult> results = new ArrayList<>();
			results.add(DataLoaderResult.Error("Error reading data: " + e.getMessage()));
			return results;
		}

		List<DataLoaderResult> messages = new ArrayList<>();
		DataHeaders headers = handler.getHeaders();

		for (DataRecord record : handler.getResults()) {
			List<Author> authors = null;
			if (headers.canImportAuthors() && !record.getAuthorName().isEmpty()) {
				authors = addAuthors(record, messages);
				if (authors == null) {
					continue;
				}
			}

			Title entry = null;
			if (headers.canImportTitles() && !record.getTitle().isEmpty()) {
				entry = addTitle(record, authors, messages);
				if (entry == null) {
					continue;
				}
			}

			Volume volume = null;
			if (entry != null && headers.canImportVolumes()) {
				volume = addVolume(record, entry, messages);
				if (volume == null) {
					continue;
				}
			}

			Borrower borrower = null;
			if (volume != null && headers.canImportBorrowers() && !record.getBorrowerName().isEmpty()) {
				borrower = addBorrower(record, volume, messages);
				if (borrower == null) {
					continue;
				}
			}
		}

		return messages;
	}

	private List<Author> addAuthors(DataRecord record, List<DataLoaderResult> messages) {
		List<Author> authors = new ArrayList<>();

		boolean error = false;
		for (String name : record.getAuthorName().split(";")) {
			DataName dataName = new DataName(name);
			List<Author> found = findAuthors(dataName);
			if (found.isEmpty()) {
				// Create new author
				Author author = saveAuthor(record, dataName);
				messages.add(DataLoaderResult.Success("Added author " + author.getName()));
				authors.add(author);
			} else if (found.size() > 1) {
				messages.add(DataLoaderResult.Error("Found multiple Author matches for \"" + name + "\""));
				error = true;
			} else {
				authors.add(found.get(0));
			}
		}

		return error ? null : authors;
	}

	private Title addTitle(DataRecord record, List<Author> authors, List<DataLoaderResult> messages) {
		List<Title> titles = findTitle(record, authors);
		if (titles.isEmpty()) {
			// Create new title
			List<Title> contents = findContents(record, messages);
			Title entry = saveTitle(record, authors, contents);
			messages.add(DataLoaderResult.Success("Added title " + entry.getTitle()));
			return entry;
		} else if (titles.size() > 1) {
			messages.add(DataLoaderResult.Error("Found multiple title matches for \"" + record.getTitle() + "\""));
			return null;
		} else {
			return titles.get(0);
		}
	}

	private Volume addVolume(DataRecord record, Title entry, List<DataLoaderResult> messages) {
		Volume volume;
		volume = saveVolume(record, entry);
		messages.add(DataLoaderResult.Success("Added volume for " + volume));
		return volume;
	}

	private Borrower addBorrower(DataRecord record, Volume volume, List<DataLoaderResult> messages) {
		DataName dataName = new DataName(record.getBorrowerName());
		List<Borrower> borrowers = findBorrowers(dataName);
		if (borrowers.isEmpty()) {
			// Create new borrower
			Borrower borrower = saveBorrower(record, volume, dataName);
			messages.add(DataLoaderResult.Success("Added borrower " + borrower.getName()));
			return borrower;
		} else if (borrowers.size() > 1) {
			messages.add(DataLoaderResult.Error("Found multiple Borrower matches for \"" + record.getBorrowerName() + "\""));
			return null;
		} else {
			return borrowers.get(0);
		}
	}

	private List<Author> findAuthors(DataName dataName) {
		return authorDao.findByLastNameAndFirstName(dataName.getLastName(), dataName.getFirstName());
	}

	private List<Title> findTitle(DataRecord record, List<Author> authors) {
		return titleDao.findByTitleAndAuthors(record.getTitle(), authors);
	}

	private List<Title> findContents(DataRecord record, List<DataLoaderResult> messages) {
		List<Title> contents = new ArrayList<>();
		String contentsText = record.getContents();
		if (contentsText != null && !contentsText.isEmpty()) {
			for (String titleForm : contentsText.split("//")) {
				String title;
				String form;
				if (titleForm.contains("::")) {
					String[] parts = titleForm.split("::", 2);
					title = parts[0].trim();
					form = parts[1].trim();
				} else {
					title = titleForm.trim();
					form = "";
				}

				List<Title> entries = titleDao.findByTitleAndForm(title, form);
				if (entries.isEmpty()) {
					messages.add(DataLoaderResult.Error("No match for contents " + title + "[" + form + "] in title " + title));
				} else if (entries.size() > 1) {
					messages.add(DataLoaderResult.Error("Multiple matches for contents " + title + " [" + form + "] in title " + record.getTitle()));
				} else {
					contents.add(entries.get(0));
				}
			}
		}
		return contents;
	}

	private List<Borrower> findBorrowers(DataName dataName) {
		return borrowerDao.findByLastNameAndFirstName(dataName.getLastName(), dataName.getFirstName());
	}

	private Author saveAuthor(DataRecord record, DataName dataName) {
		Author author = new Author();
		author.setLastName(dataName.getLastName());
		author.setFirstName(dataName.getFirstName());
		author.setBirthPlace(record.getBirthPlace());
		author.setBirthDate(record.getBirthDate());
		author.setDeathDate(record.getDeathDate());
		author.setNationality(record.getNationality());
		author.setNote(record.getAuthorNote());

		return authorDao.save(author);
	}

	private Title saveTitle(DataRecord record, List<Author> authors, List<Title> contents) {
		Title entry = new Title();
		entry.setTitle(record.getTitle());
		entry.setAuthors(authors);
		entry.setCategory(record.getCategory());
		entry.setForm(record.getForm());
		entry.setHaveRead(record.isHaveRead());
		entry.setNote(record.getTitleNote());
		contents.forEach(title -> entry.addContent(title));
		return titleDao.save(entry);
	}

	private Volume saveVolume(DataRecord record, Title entry) {
		Volume volume = new Volume();
		volume.setEntry(entry);
		volume.setBinding(record.getBinding());
		volume.setAsin(record.getAsin());
		volume.setIsbn(record.getIsbn());
		volume.setLibraryOfCongress(record.getLibraryOfCongress());
		volume.setNote(record.getVolumeNote());
		volume.setPublisher(record.getPublisher());
		volume.setPublicationDate(record.getPublicationDate());
		return volumeDao.save(volume);
	}

	private Borrower saveBorrower(DataRecord record, Volume volume, DataName dataName) {
		Borrower borrower = new Borrower();
		borrower.setLastName(dataName.getLastName());
		borrower.setFirstName(dataName.getFirstName());
		borrower.addVolume(volume);
		borrower.setCheckOutDate(record.getCheckOutDate());
		borrower.setNote(record.getBorrowerNote());
		return borrowerDao.save(borrower);
	}
}
