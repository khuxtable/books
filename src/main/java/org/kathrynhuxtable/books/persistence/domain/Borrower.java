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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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
@Table(schema = "APP", name = "CHECK_OUTS")
public class Borrower implements DomainObject, Cloneable, Comparable<Borrower>, Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "CHECK_OUT_ID")
	private Long id = new Long(-1);

	private long version;
	@Field
	private String lastName;
	@Field
	private String firstName;
	@Field
	private String checkOutDate;
	@Field
	private String note;

	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="borrower")
	@SortComparator(value = TitlePropertyComparator.VolumeComparator.class)
	private List<Volume> volumes = new ArrayList<Volume>();

	@Override
	@Transient
	@Field(store = Store.YES)
	public DocumentType getDocumentType() {
		return DocumentType.BORROWER;
	}

	/**
	 * @return the checkOutDate
	 */
	public String getCheckOutDate() {
		return checkOutDate;
	}

	/**
	 * @param checkOutDate
	 *            the checkOutDate to set
	 */
	public void setCheckOutDate(String checkOutDate) {
		this.checkOutDate = checkOutDate;
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
	 * @return the volume
	 */
	public List<Volume> getVolumes() {
		return volumes;
	}

	/**
	 * @param volumes
	 *            the volume to set
	 */
	public void setVolumes(List<Volume> volumes) {
		this.volumes = volumes;
	}

	public void addVolume(Volume volume) {
		volumes.add(volume);
		volume.setBorrower(this);
	}

	public void removeVolume(Volume volume) {
		volumes.remove(volume);
		volume.setBorrower(null);
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

	public String getDetails() {
		return toString();
	}

	@Field(name = "objectName", store = Store.YES)
    public String getName() {
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

	public String getShortDescription() {
		return getName();
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return lastName + ", " + firstName + " [" + checkOutDate + "]";
	}

	public int compareTo(Borrower o) {
		if (o == null) {
			throw new ClassCastException("Unable to compare Borrower with null");
		}

		Borrower that = (Borrower) o;
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
	public Borrower clone() {
		try {
			return (Borrower) super.clone();
		} catch (CloneNotSupportedException e) {
			// Won't happen.
			return null;
		}
	}
}
