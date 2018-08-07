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
package org.kathrynhuxtable.books.persistence.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.hibernate.CacheMode;
import org.hibernate.Session;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.SearchFactory;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.kathrynhuxtable.books.persistence.domain.Author;
import org.kathrynhuxtable.books.persistence.domain.Borrower;
import org.kathrynhuxtable.books.persistence.domain.DomainObject;
import org.kathrynhuxtable.books.persistence.domain.Title;
import org.kathrynhuxtable.books.persistence.domain.Volume;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SearchDAO {
	@PersistenceContext
	private EntityManager em;

	@Transactional
	public void rebuildIndexes() {
		Session session = em.unwrap(Session.class);
		FullTextSession fullTextSession = Search.getFullTextSession(session);

		try {
			fullTextSession.createIndexer(Author.class, Title.class, Volume.class, Borrower.class).typesToIndexInParallel(2).batchSizeToLoadObjects(25)
					.cacheMode(CacheMode.NORMAL).threadsToLoadObjects(5).idFetchSize(150)
					// .progressMonitor( new MassInd ) //a MassIndexerProgressMonitor implementation
					.startAndWait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Transactional
	public List<Author> searchAuthors(String queryString) {
		return search(queryString, Author.class, "lastName", "firstName", "nationality", "birthPlace", "note");
	}

	@Transactional
	public List<Title> searchTitles(String queryString) {
		return search(queryString, Title.class, "title", "category", "form", "haveRead", "note");
	}

	@Transactional
	public List<Volume> searchVolumes(String queryString) {
		return search(queryString, Volume.class, "title", "binding", "publisher", "publicationDate", "isbn", "libraryOfCongress", "note");
	}

	@Transactional
	public List<Borrower> searchBorrowers(String queryString) {
		return search(queryString, Borrower.class, "lastName", "firstName", "checkOutDate", "note");
	}

	private <T extends DomainObject> List<T> search(String queryString, Class<T> clazz, String... fields) {
		Session session = em.unwrap(Session.class);
		FullTextSession fullTextSession = Search.getFullTextSession(session);

		org.apache.lucene.search.Query luceneQuery = buildLuceneQuery(queryString, fullTextSession, clazz, fields);

		@SuppressWarnings("unchecked")
		org.hibernate.query.Query<T> fullTextQuery = fullTextSession.createFullTextQuery(luceneQuery, clazz);
		return fullTextQuery.list(); // return a list of managed objects
	}

	private org.apache.lucene.search.Query buildLuceneQuery(String queryString, FullTextSession fullTextSession, Class<? extends DomainObject> clazz,
			String... fields) {
		SearchFactory searchFactory = fullTextSession.getSearchFactory();
		QueryBuilder qb = searchFactory.buildQueryBuilder().forEntity(Title.class).get();

		if (queryString.matches("[a-zA-Z0-9 ']*")) {
			return qb.simpleQueryString().onFields("objectName", fields).withAndAsDefaultOperator().matching(queryString + "*").createQuery();
		} else {
			org.apache.lucene.queryparser.classic.QueryParser parser = new QueryParser("objectName", searchFactory.getAnalyzer(clazz));
			try {
				return parser.parse(queryString);
			} catch (ParseException e) {
				return qb.keyword().wildcard().onField("objectName").matching(queryString + "*").createQuery();
			}
		}
	}
}
