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
import java.util.stream.Collectors;

import org.kathrynhuxtable.books.persistence.dao.AuthorDAO;
import org.kathrynhuxtable.books.persistence.dao.BorrowerDAO;
import org.kathrynhuxtable.books.persistence.dao.TitleDAO;
import org.kathrynhuxtable.books.persistence.domain.Author;
import org.kathrynhuxtable.books.persistence.domain.Borrower;
import org.kathrynhuxtable.books.persistence.domain.Title;
import org.kathrynhuxtable.books.persistence.domain.Volume;
import org.kathrynhuxtable.books.service.DataLoaderResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataExporter {

	@Autowired
	private AuthorDAO authorDao;
	@Autowired
	private TitleDAO titleDao;
	@Autowired
	private BorrowerDAO borrowerDao;

	public List<DataLoaderResult> export(File file) {
		try {
			DataFileHandler handler = new DataFileHandler(file, true);

			List<DataLoaderResult> messages = new ArrayList<>();

			for (Title title : titleDao.findAll()) {
				if (title.getVolumes().isEmpty()) {
					handler.write(exportTitle(title));
					messages.add(DataLoaderResult.Success("Exported title " + title.getTitle() + " [" + title.getForm() + "]"));
				} else {
					for (Volume volume : title.getVolumes()) {
						handler.write(exportVolume(title, volume));
						messages.add(DataLoaderResult.Success("Exported title/volume " + title.getTitle() + " [" + title.getForm() + "], [" + volume + "]"));
					}
				}
			}

			for (Author author : authorDao.findAll()) {
				if (author.getTitles().isEmpty()) {
					handler.write(exportAuthor(author));
					messages.add(DataLoaderResult.Success("Exported author " + author.getName()));
				}
			}

			for (Borrower borrower : borrowerDao.findAll()) {
				if (borrower.getVolumes().isEmpty()) {
					handler.write(exportBorrower(borrower));
					messages.add(DataLoaderResult.Success("Exported borrower " + borrower.getName()));
				}
			}
			
			handler.close();

			return messages;
		} catch (FileHandlerDataException e) {
			return e.getErrors();
		} catch (IOException e) {
			e.printStackTrace();
			List<DataLoaderResult> results = new ArrayList<>();
			results.add(DataLoaderResult.Error("Error writing data: " + e.getMessage()));
			return results;
		}
	}

	private DataRecord exportAuthor(Author author) {
		DataRecord record = new DataRecord();

		record.setAuthorName(author.getName());
		record.setNationality(author.getNationality());
		record.setBirthPlace(author.getBirthPlace());
		record.setBirthDate(author.getBirthDate());
		record.setDeathDate(author.getDeathDate());
		record.setAuthorNote(author.getNote());

		return record;
	}

	private DataRecord exportTitle(Title title) {
		DataRecord record = title.getAuthors() == null || title.getAuthors().isEmpty() ? new DataRecord() : exportAuthor(title.getAuthors().get(0));

		record.setAuthorName(formatAuthorName(title.getAuthors()));
		record.setTitle(title.getTitle());
		record.setCategory(title.getCategory());
		record.setForm(title.getForm());
		record.setPublicationYear(Integer.toString(title.getPublicationYear()));
		record.setHaveRead(title.isHaveRead());
		record.setTitleNote(title.getNote());
		record.setContents(formatContents(title.getContents()));

		return record;
	}

	private DataRecord exportVolume(Title title, Volume volume) {
		DataRecord record = exportTitle(title);

		record.setBinding(volume.getBinding());
		record.setPublisher(volume.getPublisher());
		record.setPublicationDate(volume.getPublicationDate());
		record.setIsbn(volume.getIsbn());
		record.setLibraryOfCongress(volume.getLibraryOfCongress());
		record.setAsin(volume.getAsin());
		record.setVolumeNote(volume.getNote());

		if (volume.getBorrower() != null) {
			Borrower borrower = volume.getBorrower();
			record.setBorrowerName(borrower.getName());
			record.setCheckOutDate(borrower.getCheckOutDate());
			record.setBorrowerNote(borrower.getNote());
		}
		return record;
	}

	private DataRecord exportBorrower(Borrower borrower) {
		DataRecord record = new DataRecord();

		record.setBorrowerName(borrower.getName());
		record.setCheckOutDate(borrower.getCheckOutDate());
		record.setBorrowerNote(borrower.getNote());

		return record;
	}

	private String formatAuthorName(List<Author> authors) {
		return String.join(";", authors.stream().map(author -> author.getName()).collect(Collectors.toList()));
	}

	private String formatContents(List<Title> contents) {
		return String.join("//", contents.stream().map(title -> (title.getTitle() + "::" + title.getForm())).collect(Collectors.toList()));
	}
}
