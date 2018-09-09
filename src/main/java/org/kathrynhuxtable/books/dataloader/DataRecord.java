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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.apache.commons.csv.CSVRecord;

/**
 * Represent a data record parsed from a CSV or tab-delimited text file.
 */
public class DataRecord {

	private final String authorName;
	private final String nationality;
	private final String birthPlace;
	private final LocalDate birthDate;
	private final LocalDate deathDate;
	private final String authorNote;

	private final String title;
	private final String category;
	private final String form;
	private final String publicationYear;
	private final boolean haveRead;
	private final String titleNote;
	private final String contents;

	private final String binding;
	private final String publisher;
	private final String publicationDate;
	private final String isbn;
	private final String libraryOfCongress;
	private final String asin;
	private final String volumeNote;

	private final String borrowerName;
	private final String checkOutDate;
	private final String borrowerNote;

	/**
	 * Load a record from a CSV file.
	 * 
	 * @param record
	 *            the CSVRecord from Apache Commons CSV.
	 * @param headers
	 *            the parsed header values.
	 */
	public DataRecord(CSVRecord record, DataHeaders headers) {
		authorName = getCsvValue(record, headers, DataHeaders.AUTHOR_NAME);
		nationality = getCsvValue(record, headers, DataHeaders.NATIONALITY);
		birthPlace = getCsvValue(record, headers, DataHeaders.BIRTH_PLACE);
		birthDate = getCsvValue(record, headers, DataHeaders.BIRTH_DATE) == null ? null : parseDate(getCsvValue(record, headers, DataHeaders.BIRTH_DATE));
		deathDate = getCsvValue(record, headers, DataHeaders.DEATH_DATE) == null ? null : parseDate(getCsvValue(record, headers, DataHeaders.DEATH_DATE));
		authorNote = getCsvValue(record, headers, DataHeaders.AUTHOR_NOTE);

		title = getCsvValue(record, headers, DataHeaders.TITLE);
		category = getCsvValue(record, headers, DataHeaders.CATEGORY);
		form = getCsvValue(record, headers, DataHeaders.FORM);
		publicationYear = getCsvValue(record, headers, DataHeaders.PUBLICATION_YEAR);
		haveRead = getCsvValue(record, headers, DataHeaders.HAVE_READ) == null ? false : parseBoolean(getCsvValue(record, headers, DataHeaders.HAVE_READ));
		titleNote = getCsvValue(record, headers, DataHeaders.TITLE_NOTE);
		contents = getCsvValue(record, headers, DataHeaders.CONTENTS);

		binding = getCsvValue(record, headers, DataHeaders.BINDING);
		publisher = getCsvValue(record, headers, DataHeaders.PUBLISHER);
		publicationDate = getCsvValue(record, headers, DataHeaders.PUBLICATION_DATE);
		isbn = getCsvValue(record, headers, DataHeaders.ISBN);
		libraryOfCongress = getCsvValue(record, headers, DataHeaders.LIBRARY_OF_CONGRESS);
		asin = getCsvValue(record, headers, DataHeaders.ASIN);
		volumeNote = getCsvValue(record, headers, DataHeaders.VOLUME_NOTE);

		borrowerName = getCsvValue(record, headers, DataHeaders.BORROWER_NAME);
		checkOutDate = getCsvValue(record, headers, DataHeaders.CHECK_OUT_DATE);
		borrowerNote = getCsvValue(record, headers, DataHeaders.BORROWER_NOTE);
	}

	/**
	 * Load a record from a tab-delimited text file.
	 * 
	 * @param record
	 *            the Map of headers to fields from the parsed file record.
	 * @param headers
	 *            the parsed header values.
	 */
	public DataRecord(Map<String, String> record, DataHeaders headers) {
		authorName = getTxtValue(record, headers, DataHeaders.AUTHOR_NAME);
		nationality = getTxtValue(record, headers, DataHeaders.NATIONALITY);
		birthPlace = getTxtValue(record, headers, DataHeaders.BIRTH_PLACE);
		birthDate = record.get(DataHeaders.BIRTH_DATE) == null ? null : parseDate(record.get(DataHeaders.BIRTH_DATE));
		deathDate = record.get(DataHeaders.DEATH_DATE) == null ? null : parseDate(record.get(DataHeaders.DEATH_DATE));
		authorNote = getTxtValue(record, headers, DataHeaders.AUTHOR_NOTE);

		title = getTxtValue(record, headers, DataHeaders.TITLE);
		category = getTxtValue(record, headers, DataHeaders.CATEGORY);
		form = getTxtValue(record, headers, DataHeaders.FORM);
		publicationYear = getTxtValue(record, headers, DataHeaders.PUBLICATION_YEAR);
		haveRead = record.get(DataHeaders.HAVE_READ) == null ? false : parseBoolean(record.get(DataHeaders.HAVE_READ));
		titleNote = getTxtValue(record, headers, DataHeaders.TITLE_NOTE);
		contents = getTxtValue(record, headers, DataHeaders.CONTENTS);

		binding = getTxtValue(record, headers, DataHeaders.BINDING);
		publisher = getTxtValue(record, headers, DataHeaders.PUBLISHER);
		publicationDate = getTxtValue(record, headers, DataHeaders.PUBLICATION_DATE);
		isbn = getTxtValue(record, headers, DataHeaders.ISBN);
		libraryOfCongress = getTxtValue(record, headers, DataHeaders.LIBRARY_OF_CONGRESS);
		asin = getTxtValue(record, headers, DataHeaders.ASIN);
		volumeNote = getTxtValue(record, headers, DataHeaders.VOLUME_NOTE);

		borrowerName = getTxtValue(record, headers, DataHeaders.BORROWER_NAME);
		checkOutDate = getTxtValue(record, headers, DataHeaders.CHECK_OUT_DATE);
		borrowerNote = getTxtValue(record, headers, DataHeaders.BORROWER_NOTE);
	}

	public String getAuthorName() {
		return authorName;
	}

	public String getNationality() {
		return nationality;
	}

	public String getBirthPlace() {
		return birthPlace;
	}

	public LocalDate getBirthDate() {
		return birthDate;
	}

	public LocalDate getDeathDate() {
		return deathDate;
	}

	public String getAuthorNote() {
		return authorNote;
	}

	public String getTitle() {
		return title;
	}

	public String getCategory() {
		return category;
	}

	public String getForm() {
		return form;
	}

	public String getPublicationYear() {
		return publicationYear;
	}

	public boolean isHaveRead() {
		return haveRead;
	}

	public String getTitleNote() {
		return titleNote;
	}

	public String getContents() {
		return contents;
	}

	public String getBinding() {
		return binding;
	}

	public String getPublisher() {
		return publisher;
	}

	public String getPublicationDate() {
		return publicationDate;
	}

	public String getIsbn() {
		return isbn;
	}

	public String getLibraryOfCongress() {
		return libraryOfCongress;
	}

	public String getAsin() {
		return asin;
	}

	public String getVolumeNote() {
		return volumeNote;
	}

	public String getBorrowerName() {
		return borrowerName;
	}

	public String getCheckOutDate() {
		return checkOutDate;
	}

	public String getBorrowerNote() {
		return borrowerNote;
	}

	// Methods to parse file input.

	private String getCsvValue(CSVRecord record, DataHeaders headers, String header) {
		return !headers.contains(header) ? "" : record.get(header) == null ? "" : record.get(header).trim();
	}

	private String getTxtValue(Map<String, String> record, DataHeaders headers, String header) {
		return !headers.contains(header) ? "" : record.get(header) == null ? "" : record.get(header).trim();
	}

	private static LocalDate parseDate(String str) {
		if (str == null || str.isEmpty()) {
			return null;
		} else if (str.matches("\\d\\d\\d\\d-\\d\\d-\\d\\d")) {
			return LocalDate.parse(str, DateTimeFormatter.ISO_LOCAL_DATE);
		} else if (str.matches("\\d\\d/\\d\\d/\\d\\d\\d\\d")) {
			return LocalDate.parse(str, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
		} else {
			return null;
		}
	}

	private static boolean parseBoolean(String str) {
		return str != null && ("y".equalsIgnoreCase(str) || "t".equalsIgnoreCase(str) || "true".equalsIgnoreCase(str));
	}
}