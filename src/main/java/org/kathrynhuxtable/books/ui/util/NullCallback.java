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
package org.kathrynhuxtable.books.ui.util;

/**
 * The NullCallback interface allows for a common, reusable interface for defining APIs that require a call back with no
 * argument.
 * <p>
 * NullCallback is defined with one generic parameter, specifying the return type of the method.
 *
 * @param <R>
 *            The type of the return type of the <code>call</code> method.
 */
@FunctionalInterface
public interface NullCallback<R> {
	/**
	 * The <code>call</code> method is called when required, with a requirement that an object of type R is returned.
	 *
	 * @return An object of type R.
	 */
	public R call();
}
