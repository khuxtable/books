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
package org.kathrynhuxtable.books.persistence.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;
import org.kathrynhuxtable.books.service.DocumentType;

@Entity
@Indexed
@Table(schema = "APP", name = "VOLUMES")
public class Volume implements DomainObject, Cloneable, Comparable<Volume>, Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "VOLUME_ID")
	private Long id;

	private long version;
	@Field
	private String binding;
	@Field
	private String publisher;
	@Field
	private String publicationDate;
	@Field
	private String isbn;
	@Field
	private String libraryOfCongress;
	@Field
	private String note;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CHECK_OUT_ID")
	private Borrower borrower;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ENTRY_ID")
	@IndexedEmbedded(depth = 1)
	private Title entry;

	@Override
	@Transient
	@Field(store = Store.YES)
	public DocumentType getDocumentType() {
		return DocumentType.VOLUME;
	}

	/**
	 * @return the binding
	 */
	public String getBinding() {
		return binding;
	}

	/**
	 * @param binding
	 *            the binding to set
	 */
	public void setBinding(String binding) {
		this.binding = binding;
	}

	/**
	 * @return the borrower
	 */
	public Borrower getBorrower() {
		return borrower;
	}

	/**
	 * @param borrower
	 *            the borrower to set
	 */
	public void setBorrower(Borrower checkOut) {
		this.borrower = checkOut;
	}

	/**
	 * @return the entry
	 */
	public Title getEntry() {
		return entry;
	}

	@Field(store = Store.YES)
	@Transient
	public String getObjectName() {
		Title entry = getEntry();
		return entry == null ? "<<<unknown>>>" : entry.getTitle();
	}

	/**
	 * @param entry
	 *            the entry to set
	 */
	public void setEntry(Title title) {
		this.entry = title;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the isbn
	 */
	public String getIsbn() {
		return isbn;
	}

	/**
	 * @param isbn
	 *            the isbn to set
	 */
	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	/**
	 * @return the libraryOfCongress
	 */
	public String getLibraryOfCongress() {
		return libraryOfCongress;
	}

	/**
	 * @param libraryOfCongress
	 *            the libraryOfCongress to set
	 */
	public void setLibraryOfCongress(String libraryOfCongress) {
		this.libraryOfCongress = libraryOfCongress;
	}

	/**
	 * @return the note
	 */
	public String getNote() {
		return note;
	}

	/**
	 * @param note
	 *            the note to set
	 */
	public void setNote(String note) {
		this.note = note;
	}

	/**
	 * @return the publicationDate
	 */
	public String getPublicationDate() {
		return publicationDate;
	}

	/**
	 * @param publicationDate
	 *            the publicationDate to set
	 */
	public void setPublicationDate(String publicationDate) {
		this.publicationDate = publicationDate;
	}

	/**
	 * @return the publisher
	 */
	public String getPublisher() {
		return publisher;
	}

	/**
	 * @param publisher
	 *            the publisher to set
	 */
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	/**
	 * @return the version
	 */
	public long getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(long version) {
		this.version = version;
	}

	/**
	 * @return
	 */
	public String toString() {
		String title = entry == null ? "<<<no entry>>>" : entry.getTitle();
		return title + " [" + getShortDescription() + "]";
	}

	public String getDetails() {
		return binding + " " + publisher + " " + publicationDate;
	}

	public String getShortDescription() {
		StringBuffer result = new StringBuffer();
		if (getBinding() != null && !getBinding().isEmpty()) {
			result.append(getBinding());
		}
		if (getPublisher() != null && !getPublisher().isEmpty()) {
			if (result.length() > 0) {
				result.append(" ");
			}
			result.append(getPublisher());
		}
		if (getPublicationDate() != null && !getPublicationDate().isEmpty()) {
			if (result.length() > 0) {
				result.append(" ");
			}
			result.append(getPublicationDate());
		}
		return result.toString();
	}

	public int compareTo(Volume o) {
		if (o == null) {
			throw new ClassCastException("Unable to compare Volume with null");
		}

		Volume that = (Volume) o;
		String thisVolume = this.toString().toLowerCase();
		String thatVolume = that.toString().toLowerCase();
		int result = thisVolume.compareTo(thatVolume);
		if (result == 0) {
			result = this.id == that.id ? 0 : this.id < that.id ? -1 : +1;
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public Volume clone() {
		try {
			return (Volume) super.clone();
		} catch (CloneNotSupportedException e) {
			// Won't happen.
			return null;
		}
	}
}
