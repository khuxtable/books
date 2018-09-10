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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVRecord;

/**
 * Represent a data record parsed from a CSV or tab-delimited text file.
 */
public class DataRecord {

	private String authorName;
	private String nationality;
	private String birthPlace;
	private LocalDate birthDate;
	private LocalDate deathDate;
	private String authorNote;

	private String title;
	private String category;
	private String form;
	private String publicationYear;
	private boolean haveRead;
	private String titleNote;
	private String contents;

	private String binding;
	private String publisher;
	private String publicationDate;
	private String isbn;
	private String libraryOfCongress;
	private String asin;
	private String volumeNote;

	private String borrowerName;
	private String checkOutDate;
	private String borrowerNote;

	public DataRecord() {
		authorName = "";
		nationality = "";
		birthPlace = "";
		birthDate = null;
		deathDate = null;
		authorNote = "";

		title = "";
		category = "";
		form = "";
		publicationYear = "";
		haveRead = false;
		titleNote = "";
		contents = "";

		binding = "";
		publisher = "";
		publicationDate = "";
		isbn = "";
		libraryOfCongress = "";
		asin = "";
		volumeNote = "";

		borrowerName = "";
		checkOutDate = "";
		borrowerNote = "";
	}

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

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getBirthPlace() {
		return birthPlace;
	}

	public void setBirthPlace(String birthPlace) {
		this.birthPlace = birthPlace;
	}

	public LocalDate getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}

	public LocalDate getDeathDate() {
		return deathDate;
	}

	public void setDeathDate(LocalDate deathDate) {
		this.deathDate = deathDate;
	}

	public String getAuthorNote() {
		return authorNote;
	}

	public void setAuthorNote(String authorNote) {
		this.authorNote = authorNote;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getForm() {
		return form;
	}

	public void setForm(String form) {
		this.form = form;
	}

	public String getPublicationYear() {
		return publicationYear;
	}

	public void setPublicationYear(String publicationYear) {
		this.publicationYear = publicationYear;
	}

	public boolean isHaveRead() {
		return haveRead;
	}

	public void setHaveRead(boolean haveRead) {
		this.haveRead = haveRead;
	}

	public String getTitleNote() {
		return titleNote;
	}

	public void setTitleNote(String titleNote) {
		this.titleNote = titleNote;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public String getBinding() {
		return binding;
	}

	public void setBinding(String binding) {
		this.binding = binding;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getPublicationDate() {
		return publicationDate;
	}

	public void setPublicationDate(String publicationDate) {
		this.publicationDate = publicationDate;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getLibraryOfCongress() {
		return libraryOfCongress;
	}

	public void setLibraryOfCongress(String libraryOfCongress) {
		this.libraryOfCongress = libraryOfCongress;
	}

	public String getAsin() {
		return asin;
	}

	public void setAsin(String asin) {
		this.asin = asin;
	}

	public String getVolumeNote() {
		return volumeNote;
	}

	public void setVolumeNote(String volumeNote) {
		this.volumeNote = volumeNote;
	}

	public String getBorrowerName() {
		return borrowerName;
	}

	public void setBorrowerName(String borrowerName) {
		this.borrowerName = borrowerName;
	}

	public String getCheckOutDate() {
		return checkOutDate;
	}

	public void setCheckOutDate(String checkOutDate) {
		this.checkOutDate = checkOutDate;
	}

	public String getBorrowerNote() {
		return borrowerNote;
	}

	public void setBorrowerNote(String borrowerNote) {
		this.borrowerNote = borrowerNote;
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
		return str != null && (str.toLowerCase().startsWith("y") || str.toLowerCase().startsWith("t"));
	}

	// Methods to format file output.

	public List<String> getValues() {
		List<String> result = new ArrayList<>();

		result.add(getAuthorName());
		result.add(getNationality());
		result.add(getBirthPlace());
		result.add(getBirthDate() == null ? "" : DateTimeFormatter.ISO_LOCAL_DATE.format(getBirthDate()));
		result.add(getDeathDate() == null ? "" : DateTimeFormatter.ISO_LOCAL_DATE.format(getDeathDate()));
		result.add(getAuthorNote());

		result.add(getTitle());
		result.add(getCategory());
		result.add(getForm());
		result.add(getPublicationYear());
		result.add(isHaveRead() ? "Y" : "N");
		result.add(getTitleNote());
		result.add(getContents());

		result.add(getBinding());
		result.add(getPublisher());
		result.add(getPublicationDate());
		result.add(getIsbn());
		result.add(getLibraryOfCongress());
		result.add(getAsin());
		result.add(getVolumeNote());

		result.add(getBorrowerName());
		result.add(getCheckOutDate());
		result.add(getBorrowerNote());

		return result;
	}
}