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

import java.util.ArrayList;
import java.util.List;

import org.kathrynhuxtable.books.persistence.domain.Author;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorDAO extends CrudRepository<Author, Long> {

	default List<Author> findByName(String name, boolean fetchFields) {
		String[] nameParts = null;
		name = name.trim();
		if (name.contains(",")) {
			nameParts = name.split(", *", 2);
			for (int i = 0; i < nameParts.length; i++) {
				if (nameParts[i] != null && nameParts[i].isEmpty()) {
					nameParts[i] = null;
				}
			}

			return findByName(nameParts[0], nameParts[1], fetchFields);
		} else {
			return findByName(name, null, fetchFields);
		}
	}

	default List<Author> findByName(String lastName, String firstName, boolean fetchFields) {
		Sort sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "lastName").ignoreCase(), new Sort.Order(Sort.Direction.ASC, "firstName").ignoreCase());

		List<Author> result = null;
		if (firstName != null && !firstName.isEmpty() && lastName != null && !lastName.isEmpty()) {
			result = findByLastNameStartingWithAndFirstNameStartingWithAllIgnoreCase(lastName, firstName, sort);
		} else if (lastName != null && !lastName.isEmpty()) {
			result = findByLastNameStartingWithAllIgnoreCase(lastName, sort);
		} else if (firstName != null && !firstName.isEmpty()) {
			result = findByFirstNameStartingWithAllIgnoreCase(firstName, sort);
		} else {
			result = new ArrayList<>();
			result = findByLastNameStartingWithAllIgnoreCase("", sort);
		}

		if (fetchFields) {
			result.stream().forEach(author -> author.getTitles().stream().forEach(title -> title.getTitle()));
		}

		return result;
	}

	List<Author> findByLastNameStartingWithAllIgnoreCase(String lastName, Sort sort);

	List<Author> findByFirstNameStartingWithAllIgnoreCase(String lastName, Sort sort);

	List<Author> findByLastNameStartingWithAndFirstNameStartingWithAllIgnoreCase(String lastName, String firstName, Sort sort);

	List<Author> findByLastNameAndFirstName(String lastName, String firstName);

}
