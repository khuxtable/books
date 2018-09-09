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

public class DataLoaderResult {

	public enum DLRStatus {
		SUCCESS, ERROR
	};

	public final DLRStatus status;
	public final String text;

	public DataLoaderResult(DLRStatus status, String text) {
		this.status = status;
		this.text = text;
	}

	public static DataLoaderResult Success(String text) {
		return new DataLoaderResult(DLRStatus.SUCCESS, text);
	}

	public static DataLoaderResult Error(String text) {
		return new DataLoaderResult(DLRStatus.ERROR, text);
	}
}
