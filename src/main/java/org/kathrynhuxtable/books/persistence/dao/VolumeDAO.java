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

import org.kathrynhuxtable.books.persistence.domain.Volume;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VolumeDAO extends CrudRepository<Volume, Long> {

	default List<Volume> findByTitle(String title, boolean wildFlag, boolean fetchFields) {
		if (title == null) {
			title = "";
		}

		List<Volume> result;
		if (wildFlag) {
			result = findByEntryTitleContainsIgnoreCaseOrderByEntryTitleAsc(title);
		} else {
			result = findByEntryTitleIgnoreCaseOrderByEntryTitleAsc(title);
		}

		if (fetchFields) {
			result.stream().forEach(volume -> volume.getEntry());
		}

		return result;
	}

	List<Volume> findByEntryTitleIgnoreCaseOrderByEntryTitleAsc(String title);

	List<Volume> findByEntryTitleContainsIgnoreCaseOrderByEntryTitleAsc(String title);

}
