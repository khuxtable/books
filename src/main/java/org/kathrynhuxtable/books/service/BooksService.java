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
package org.kathrynhuxtable.books.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.kathrynhuxtable.books.persistence.dao.AuthorDAO;
import org.kathrynhuxtable.books.persistence.dao.BorrowerDAO;
import org.kathrynhuxtable.books.persistence.dao.SearchDAO;
import org.kathrynhuxtable.books.persistence.dao.TitleDAO;
import org.kathrynhuxtable.books.persistence.dao.VolumeDAO;
import org.kathrynhuxtable.books.persistence.domain.Author;
import org.kathrynhuxtable.books.persistence.domain.Borrower;
import org.kathrynhuxtable.books.persistence.domain.DomainObject;
import org.kathrynhuxtable.books.persistence.domain.Title;
import org.kathrynhuxtable.books.persistence.domain.Volume;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.MutableSortDefinition;
import org.springframework.stereotype.Service;

@Service
public class BooksService {

	@Autowired
	private AuthorDAO authorDao;
	@Autowired
	private TitleDAO titleDao;
	@Autowired
	private VolumeDAO volumeDao;
	@Autowired
	private BorrowerDAO borrowerDao;
	@Autowired
	private SearchDAO searchDao;

	public Optional<Author> getAuthorById(Long id) {
		return authorDao.findById(id);
	}

	public List<Author> findAuthorByName(String name, boolean fetchFields) {
		return authorDao.findByName(name, fetchFields);
	}

	public Optional<Title> getTitleById(Long id) {
		return titleDao.findById(id);
	}

	public List<Title> findTitleByName(String title) {
		return titleDao.findByTitleContainsIgnoreCaseOrderByTitleAsc(title);
	}

	public Optional<Volume> getVolumeById(Long id) {
		return volumeDao.findById(id);
	}

	public List<Volume> findVolumeByName(String title) {
		return volumeDao.findByTitle(title, true, false);
	}

	public Optional<Borrower> getBorrowerById(Long id) {
		return borrowerDao.findById(id);
	}

	public List<Borrower> findCheckOutByName(String name, boolean fetchFields) {
		return borrowerDao.findByName(name, fetchFields);
	}

	public Map<DocumentType, List<SearchResult>> searchAll(String searchField) {
		Map<DocumentType, List<SearchResult>> map = new TreeMap<>();

		List<Author> authors = searchDao.searchAuthors(searchField);
		if (authors.size() > 0) {
			List<SearchResult> responses = new ArrayList<>();
			map.put(DocumentType.AUTHOR, responses);
			authors.forEach(author -> responses.add(new SearchResult(DocumentType.AUTHOR, author.getName(), author.getId(), author.getShortDescription())));
		}

		List<Title> titles = searchDao.searchTitles(searchField);
		if (titles.size() > 0) {
			Collections.sort(titles, new TitlePropertyComparator<Title>(new MutableSortDefinition("title", true, true)));
			List<SearchResult> responses = new ArrayList<>();
			map.put(DocumentType.TITLE, responses);
			titles.forEach(title -> responses.add(new SearchResult(DocumentType.TITLE, title.getTitle(), title.getId(), title.getShortDescription())));
		}

		List<Volume> volumes = searchDao.searchVolumes(searchField);
		if (volumes.size() > 0) {
			Collections.sort(volumes, new TitlePropertyComparator<Volume>(new MutableSortDefinition("entry.title", true, true)));
			List<SearchResult> responses = new ArrayList<>();
			map.put(DocumentType.VOLUME, responses);
			volumes.forEach(
					volume -> responses.add(new SearchResult(DocumentType.VOLUME, volume.getEntry().getTitle(), volume.getId(), volume.getShortDescription())));
		}

		List<Borrower> checkOuts = searchDao.searchBorrowers(searchField);
		if (checkOuts.size() > 0) {
			List<SearchResult> responses = new ArrayList<>();
			map.put(DocumentType.BORROWER, responses);
			checkOuts.forEach(borrower -> responses.add(new SearchResult(DocumentType.BORROWER, borrower.getName(), borrower.getId(), borrower.getDetails())));
		}

		return map;
	}

	public void rebuildIndexes() {
		searchDao.rebuildIndexes();
	}

	@SuppressWarnings("unchecked")
	public <T extends DomainObject> T save(T t) {
		if (t instanceof Author) {
			return (T) authorDao.save((Author) t);
		} else if (t instanceof Title) {
			return (T) titleDao.save((Title) t);
		} else if (t instanceof Volume) {
			return (T) volumeDao.save((Volume) t);
		} else if (t instanceof Borrower) {
			return (T) borrowerDao.save((Borrower) t);
		} else {
			return null;
		}
	}

	public <T extends DomainObject> void delete(T t) {
		if (t instanceof Author) {
			authorDao.delete((Author) t);
		} else if (t instanceof Title) {
			titleDao.delete((Title) t);
		} else if (t instanceof Volume) {
			volumeDao.delete((Volume) t);
		} else if (t instanceof Borrower) {
			borrowerDao.delete((Borrower) t);
		}
	}
}
