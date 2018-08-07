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
package org.kathrynhuxtable.books.service;

public class SearchResult {
	private final DocumentType documentType;
	private final String name;
	private final Long id;
	private final String shortDescription;

	public SearchResult(final DocumentType documentType, final String name, final Long id, String shortDescription) {
		this.documentType = documentType;
		this.name = name;
		this.id = id;
		this.shortDescription = shortDescription;
	}

	public DocumentType getDocumentType() {
		return documentType;
	}

	public String getName() {
		return name;
	}

	public Long getId() {
		return id;
	}

	public String getShortDescription() {
		return shortDescription;
	}
}