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
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.SortComparator;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import org.kathrynhuxtable.books.persistence.domain.converter.BooleanToYNConverter;
import org.kathrynhuxtable.books.service.DocumentType;
import org.kathrynhuxtable.books.service.TitlePropertyComparator;

@Entity
@Indexed
@Table(schema = "APP", name = "ENTRIES")
public class Title implements DomainObject, Cloneable, Comparable<Title>, Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ENTRY_ID")
	private Long id = new Long(-1);

	private long version;
	@Field
	@Field(name = "objectName", store = Store.YES)
	private String title;
	@Field
	private String category;
	@Field
	private String form;
	@Field
	private int publicationYear;
	@Convert(converter = BooleanToYNConverter.class)
	@Field
	private boolean haveRead;
	@Field
	private String note;

	@ManyToMany
	@JoinTable(name = "ENTRY_AUTHORS", joinColumns = { @JoinColumn(name = "ENTRY_ID") }, inverseJoinColumns = { @JoinColumn(name = "AUTHOR_ID") })
	@OrderColumn(name = "SEQUENCE")
	private List<Author> authors = new ArrayList<Author>();

	@ManyToMany
	@JoinTable(name = "CONTENTS", joinColumns = { @JoinColumn(name = "ELEMENT_ID") }, inverseJoinColumns = { @JoinColumn(name = "COLLECTION_ID") })
	@SortComparator(value = TitlePropertyComparator.TitleComparator.class)
	private SortedSet<Title> collectedIn = new TreeSet<Title>();

	@ManyToMany
	@JoinTable(name = "CONTENTS", joinColumns = { @JoinColumn(name = "COLLECTION_ID") }, inverseJoinColumns = { @JoinColumn(name = "ELEMENT_ID") })
	@OrderColumn(name = "SEQUENCE")
	private List<Title> contents = new ArrayList<Title>();

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "entry")
	@SortComparator(value = TitlePropertyComparator.VolumeIdComparator.class)
	private SortedSet<Volume> volumes = new TreeSet<Volume>();

	@Override
	@Transient
	@Field(store = Store.YES)
	public DocumentType getDocumentType() {
		return DocumentType.TITLE;
	}

	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @param category
	 *            the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	public List<Author> getAuthors() {
		return authors;
	}

	public void setAuthors(List<Author> authors) {
		this.authors = authors;
	}

	public void addAuthor(Author author) {
		// Don't add author twice to list.
		if (!authors.contains(author)) {
			authors.add(author);
		}
		author.addTitle(this);
	}

	public void removeAuthor(Author author) {
		authors.remove(author);
		author.removeTitle(this);
	}

	public SortedSet<Title> getCollectedIn() {
		return collectedIn;
	}

	public void setCollectedIn(SortedSet<Title> collectedIn) {
		this.collectedIn = collectedIn;
	}

	public void addCollectedIn(Title title) {
		collectedIn.add(title);
	}

	public void removeCollectedIn(Title title) {
		collectedIn.remove(title);
	}

	public List<Title> getContents() {
		return contents;
	}

	public void setContents(List<Title> content) {
		this.contents = content;
	}

	public void addContent(Title title) {
		// Don't add element twice to contents list.
		if (!contents.contains(title)) {
			contents.add(title);
		}
	}

	public void removeContent(Title title) {
		contents.remove(title);
	}

	/**
	 * @return the form
	 */
	public String getForm() {
		return form;
	}

	/**
	 * @param form
	 *            the form to set
	 */
	public void setForm(String form) {
		this.form = form;
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
	 * @return the haveRead
	 */
	public boolean isHaveRead() {
		return haveRead;
	}

	/**
	 * @param haveRead
	 *            the haveRead to set
	 */
	public void setHaveRead(boolean read) {
		this.haveRead = read;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the publicationYear
	 */
	public int getPublicationYear() {
		return publicationYear;
	}

	/**
	 * @param publicationYear
	 *            the publicationYear to set
	 */
	public void setPublicationYear(int year) {
		this.publicationYear = year;
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

	public SortedSet<Volume> getVolumes() {
		return volumes;
	}

	public void setVolumes(SortedSet<Volume> volumes) {
		this.volumes = volumes;
	}

	public void addVolume(Volume volume) {
		volumes.add(volume);
		volume.setEntry(this);
	}

	public void removeVolume(Volume volume) {
		volumes.remove(volume);
		volume.setEntry(null);
	}

	public String toString() {
		return title;
	}
	
	public String getShortDescription() {
		StringBuffer result = new StringBuffer();
		if (getCategory() != null && !getCategory().isEmpty()) {
			result.append(getCategory());
		}
		if (getForm() != null && !getForm().isEmpty()) {
			if (result.length() > 0) {
				result.append(", ");
				result.append(getForm());
			}
		}
		if (getPublicationYear() != 0) {
			if (result.length() > 0) {
				result.append(", ");
				result.append(getPublicationYear());
			}
		}
		if (getNote() != null && !getNote().isEmpty()) {
			if (result.length() > 0) {
				result.append(", ");
				result.append(getNote());
			}
		}
		return result.toString();
	}

	public boolean equals(Object o) {
		if (o == null || !(o instanceof Title)) {
			return false;
		}
		Title e = (Title) o;
		return id == e.id;
	}

	public int compareTo(Title that) {
		if (that == null) {
			throw new ClassCastException("Unable to compare Title with null");
		}

		String thisTitle = this.title.toLowerCase();
		String thatTitle = that.title.toLowerCase();
		int result = thisTitle.compareTo(thatTitle);
		if (result == 0) {
			result = this.id == that.id ? 0 : this.id < that.id ? -1 : +1;
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public Title clone() {
		try {
			return (Title) super.clone();
		} catch (CloneNotSupportedException e) {
			// Won't happen.
			return null;
		}
	}
}
