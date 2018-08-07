/*
 * Copyright 2002-2018 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kathrynhuxtable.books.service;

import java.util.Comparator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kathrynhuxtable.books.persistence.domain.Title;
import org.kathrynhuxtable.books.persistence.domain.Volume;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.support.MutableSortDefinition;
import org.springframework.beans.support.SortDefinition;

/**
 * PropertyComparator performs a comparison of two beans, evaluating the specified bean property via a BeanWrapper.
 *
 * @author Juergen Hoeller
 * @author Jean-Pierre Pawlak
 * @since 19.05.2003
 * @see org.springframework.beans.BeanWrapper
 */
public class TitlePropertyComparator<T> implements Comparator<T> {

	protected final Log logger = LogFactory.getLog(getClass());

	private final SortDefinition sortDefinition;

	private final BeanWrapperImpl beanWrapper = new BeanWrapperImpl(false);

	/**
	 * Create a new PropertyComparator for the given SortDefinition.
	 * 
	 * @see MutableSortDefinition
	 */
	public TitlePropertyComparator(SortDefinition sortDefinition) {
		this.sortDefinition = sortDefinition;
	}

	/**
	 * Create a PropertyComparator for the given settings.
	 * 
	 * @param property
	 *            the property to compare
	 * @param ignoreCase
	 *            whether upper and lower case in String values should be ignored
	 * @param ascending
	 *            whether to sort ascending (true) or descending (false)
	 */
	public TitlePropertyComparator(String property, boolean ignoreCase, boolean ascending) {
		this.sortDefinition = new MutableSortDefinition(property, ignoreCase, ascending);
	}

	/**
	 * Return the SortDefinition that this comparator uses.
	 */
	public final SortDefinition getSortDefinition() {
		return sortDefinition;
	}

	@SuppressWarnings("unchecked")
	public int compare(T o1, T o2) {
		Object v1 = getPropertyValue(o1);
		Object v2 = getPropertyValue(o2);
		if (v1 instanceof String) {
			v1 = fixTitle((String) v1);
		}
		if (v2 instanceof String) {
			v2 = fixTitle((String) v2);
		}
		if (this.sortDefinition.isIgnoreCase() && (v1 instanceof String) && (v2 instanceof String)) {
			v1 = ((String) v1).toLowerCase();
			v2 = ((String) v2).toLowerCase();
		}

		int result;

		// Put an object with null property at the end of the sort result.
		try {
			if (v1 != null) {
				result = (v2 != null ? ((Comparable<Object>) v1).compareTo(v2) : -1);
			} else {
				result = (v2 != null ? 1 : 0);
			}
		} catch (RuntimeException ex) {
			if (logger.isWarnEnabled()) {
				logger.warn("Could not sort objects [" + o1 + "] and [" + o2 + "]", ex);
			}
			return 0;
		}

		return (this.sortDefinition.isAscending() ? result : -result);
	}

	/**
	 * @param v
	 * @return
	 */
	private String fixTitle(String s) {
		s = s.replaceAll("\\W+", " ").trim();
		if (s.toLowerCase().startsWith("a ")) {
			s = s.substring(2) + ", " + s.substring(0, 1);
		} else if (s.toLowerCase().startsWith("an ")) {
			s = s.substring(3) + ", " + s.substring(0, 2);
		} else if (s.toLowerCase().startsWith("the ")) {
			s = s.substring(4) + ", " + s.substring(0, 3);
		}
		return s;
	}

	/**
	 * Get the SortDefinition's property value for the given object.
	 * 
	 * @param obj
	 *            the object to get the property value for
	 * @return the property value
	 */
	private Object getPropertyValue(Object obj) {
		// If a nested property cannot be read, simply return null
		// (similar to JSTL EL). If the property doesn't exist in the
		// first place, let the exception through.
		try {
			this.beanWrapper.setWrappedInstance(obj);
			return this.beanWrapper.getPropertyValue(this.sortDefinition.getProperty());
		} catch (BeansException ex) {
			logger.info("PropertyComparator could not access property - treating as null for sorting", ex);
			return null;
		}
	}

	public static class TitleComparator extends TitlePropertyComparator<Title> {
		public TitleComparator() {
			super(new MutableSortDefinition("title", true, true));
		}
	}

	public static class VolumeComparator extends TitlePropertyComparator<Volume> {
		public VolumeComparator() {
			super(new MutableSortDefinition("entry.title", true, true));
		}
	}

	public static class VolumeIdComparator extends TitlePropertyComparator<Volume> {
		public VolumeIdComparator() {
			super(new MutableSortDefinition("id", true, true));
		}
	}
}
