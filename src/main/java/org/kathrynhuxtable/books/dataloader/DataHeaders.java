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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kathrynhuxtable.books.service.DataLoaderResult;

/**
 * Manage headers from a CSV or tab-delimited text file.
 */
public class DataHeaders {

	public static final String AUTHOR_NAME = "authorName";
	public static final String NATIONALITY = "nationality";
	public static final String BIRTH_PLACE = "birthPlace";
	public static final String BIRTH_DATE = "birthDate";
	public static final String DEATH_DATE = "deathDate";
	public static final String AUTHOR_NOTE = "authorNote";

	public static final String TITLE = "title";
	public static final String CATEGORY = "category";
	public static final String FORM = "form";
	public static final String PUBLICATION_YEAR = "publicationYear";
	public static final String HAVE_READ = "haveRead";
	public static final String TITLE_NOTE = "titleNote";
	public static final String CONTENTS = "contents";

	public static final String BINDING = "binding";
	public static final String PUBLISHER = "publisher";
	public static final String PUBLICATION_DATE = "publicationDate";
	public static final String ISBN = "isbn";
	public static final String LIBRARY_OF_CONGRESS = "libraryOfCongress";
	public static final String ASIN = "asin";
	public static final String VOLUME_NOTE = "volumeNote";

	public static final String BORROWER_NAME = "borrowerName";
	public static final String CHECK_OUT_DATE = "checkOutDate";
	public static final String BORROWER_NOTE = "borrowerNote";

// @formatter:off
	private static final List<String> AUTHOR_FIELDS_LIST = Arrays.asList(new String[] {
		AUTHOR_NAME, NATIONALITY, BIRTH_PLACE, BIRTH_DATE, DEATH_DATE, AUTHOR_NOTE
	});
	private static final List<String> TITLE_FIELDS_LIST = Arrays.asList(new String[] {
		TITLE, CATEGORY, FORM, PUBLICATION_YEAR, HAVE_READ, TITLE_NOTE, CONTENTS
	});
	private static final List<String> VOLUME_FIELDS_LIST = Arrays.asList(new String[] {
		BINDING, PUBLISHER, PUBLICATION_DATE, ISBN, LIBRARY_OF_CONGRESS, ASIN, VOLUME_NOTE
	});
	private static final List<String> BORROWER_FIELDS_LIST = Arrays.asList(new String[] {
		BORROWER_NAME, CHECK_OUT_DATE, BORROWER_NOTE
	});
// @formatter:on

	public static final Set<String> AUTHOR_FIELDS = new HashSet<>(AUTHOR_FIELDS_LIST);
	public static final Set<String> TITLE_FIELDS = new HashSet<>(TITLE_FIELDS_LIST);
	public static final Set<String> VOLUME_FIELDS = new HashSet<>(VOLUME_FIELDS_LIST);
	public static final Set<String> BORROWER_FIELDS = new HashSet<>(BORROWER_FIELDS_LIST);

	public static final Set<String> ALLOWED_FIELDS = new HashSet<>();
	public static final List<String> ALLOWED_FIELDS_LIST = new ArrayList<>();
	static {
		ALLOWED_FIELDS.addAll(AUTHOR_FIELDS);
		ALLOWED_FIELDS.addAll(TITLE_FIELDS);
		ALLOWED_FIELDS.addAll(VOLUME_FIELDS);
		ALLOWED_FIELDS.addAll(BORROWER_FIELDS);

		ALLOWED_FIELDS_LIST.addAll(AUTHOR_FIELDS_LIST);
		ALLOWED_FIELDS_LIST.addAll(TITLE_FIELDS_LIST);
		ALLOWED_FIELDS_LIST.addAll(VOLUME_FIELDS_LIST);
		ALLOWED_FIELDS_LIST.addAll(BORROWER_FIELDS_LIST);
	}

	// Headers from the first line of the file.
	private Set<String> headers;

	public DataHeaders(Collection<String> headerSet) throws FileHandlerDataException {
		this.headers = new HashSet<>();
		this.headers.addAll(headerSet);
		validateHeaders();
	}

	public boolean canImportAuthors() {
		return contains(AUTHOR_NAME);
	}

	public boolean canImportTitles() {
		return contains(TITLE);
	}

	public boolean canImportVolumes() {
		return containsVolumeFields();
	}

	public boolean canImportBorrowers() {
		return contains(BORROWER_NAME);
	}

	public boolean contains(String header) {
		return headers.contains(header);
	}

	public boolean containsAuthorFields() {
		return containsOneOf(headers, AUTHOR_FIELDS);
	}

	public boolean containsTitleFields() {
		return containsOneOf(headers, TITLE_FIELDS);
	}

	public boolean containsVolumeFields() {
		return containsOneOf(headers, VOLUME_FIELDS);
	}

	public boolean containsBorrowerFields() {
		return containsOneOf(headers, BORROWER_FIELDS);
	}

	private void validateHeaders() throws FileHandlerDataException {
		List<DataLoaderResult> errors = new ArrayList<>();

		for (String header : headers) {
			if (!ALLOWED_FIELDS.contains(header)) {
				errors.add(DataLoaderResult.Error("Unknown field \"" + header + "\" will be ignored"));
			}
		}

		if (!headers.contains(TITLE) && !headers.contains(AUTHOR_NAME)) {
			errors.add(DataLoaderResult.Error("Unable to load file that contains neither authorName nor title fields"));
		}

		if (containsAuthorFields() && !headers.contains(AUTHOR_NAME)) {
			errors.add(DataLoaderResult.Error("Unable to load file that contains author fields without authorName"));
		}

		if (containsTitleFields() && !headers.contains(TITLE)) {
			errors.add(DataLoaderResult.Error("Unable to load file that contains title fields without title"));
		}

		if (containsBorrowerFields() && !headers.contains(BORROWER_NAME)) {
			errors.add(DataLoaderResult.Error("Unable to load file that contains borrower fields without borrowerName"));
		}

		if (!errors.isEmpty()) {
			throw new FileHandlerDataException(errors);
		}
	}

	private boolean containsOneOf(Set<String> headers, Set<String> typeFields) {
		for (String field : typeFields) {
			if (headers.contains(field)) {
				return true;
			}
		}

		return false;
	}
}
