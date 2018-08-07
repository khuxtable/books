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
import java.time.LocalDate;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.SortComparator;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import org.kathrynhuxtable.books.service.DocumentType;
import org.kathrynhuxtable.books.service.TitlePropertyComparator;

@Entity
@Indexed
@Table(name = "AUTHORS")
public class Author implements DomainObject, Cloneable, Comparable<Author>, Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "AUTHOR_ID")
	private Long id;

	private long version;
	@Column(name = "LAST_NAME", nullable = false)
	@Field
	private String lastName;
	@Field
	private String firstName;
	@Field
	private String nationality;
	@Field
	private String birthPlace;
	private LocalDate birthDate;
	private LocalDate deathDate;
	@Field
	private String note;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "ENTRY_AUTHORS", joinColumns = { @JoinColumn(name = "AUTHOR_ID") }, inverseJoinColumns = { @JoinColumn(name = "ENTRY_ID") })
	@SortComparator(value = TitlePropertyComparator.TitleComparator.class)
	private SortedSet<Title> titles = new TreeSet<Title>();

	@Override
	@Transient
	@Field(store = Store.YES)
	public DocumentType getDocumentType() {
		return DocumentType.AUTHOR;
	}

	/**
	 * @return the birthDate
	 */
	public LocalDate getBirthDate() {
		return birthDate;
	}

	/**
	 * @param birthDate
	 *            the birthDate to set
	 */
	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}

	/**
	 * @return the birthPlace
	 */
	public String getBirthPlace() {
		return birthPlace;
	}

	/**
	 * @param birthPlace
	 *            the birthPlace to set
	 */
	public void setBirthPlace(String birthPlace) {
		this.birthPlace = birthPlace;
	}

	/**
	 * @return the deathDate
	 */
	public LocalDate getDeathDate() {
		return deathDate;
	}

	/**
	 * @param deathDate
	 *            the deathDate to set
	 */
	public void setDeathDate(LocalDate deathDate) {
		this.deathDate = deathDate;
	}

	public SortedSet<Title> getTitles() {
		return titles;
	}

	public void setTitles(SortedSet<Title> titles) {
		this.titles = titles;
	}

	public void addTitle(Title title) {
		titles.add(title);
	}

	public void removeTitle(Title title) {
		titles.remove(title);
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName
	 *            the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
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
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName
	 *            the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the nationality
	 */
	public String getNationality() {
		return nationality;
	}

	/**
	 * @param nationality
	 *            the nationality to set
	 */
	public void setNationality(String nationality) {
		this.nationality = nationality;
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

	@Field(name = "objectName", store = Store.YES)
	public String getName() {
		return toString();
	}

	public String getShortDescription() {
		StringBuffer result = new StringBuffer();
		result.append(getTitles().size() + " Titles");
		if (getNote() != null && !getNote().isEmpty()) {
			result.append(", ");
			result.append(getNote());
		}
		return result.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		if (lastName != null && lastName.length() > 0 && firstName != null && firstName.length() > 0) {
			return lastName + ", " + firstName;
		} else if (lastName != null && lastName.length() > 0) {
			return lastName;
		} else if (firstName != null && firstName.length() > 0) {
			return firstName;
		} else {
			return "<<<no name>>>";
		}
	}

	public int compareTo(Author o) {
		if (o == null) {
			throw new ClassCastException("Unable to compare Author with null");
		}

		Author that = (Author) o;
		String thisName = this.toString().toLowerCase();
		String thatName = that.toString().toLowerCase();
		int result = thisName.compareTo(thatName);
		if (result == 0) {
			result = this.id == that.id ? 0 : this.id < that.id ? -1 : +1;
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public Author clone() {
		try {
			return (Author) super.clone();
		} catch (CloneNotSupportedException e) {
			// Won't happen.
			return null;
		}
	}
}
