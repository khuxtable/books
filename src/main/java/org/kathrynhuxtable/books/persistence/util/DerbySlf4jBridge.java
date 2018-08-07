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
package org.kathrynhuxtable.books.persistence.util;

import java.io.Writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Funnels Derby log outputs into an SLF4J logger. This current implementation logs all errors at the <code>ERROR</code>
 * threshold.
 */
public final class DerbySlf4jBridge {

	private static final Logger logger = LoggerFactory.getLogger(DerbySlf4jBridge.class);

	/**
	 * A basic adapter that funnels Derby's logs through an SLF4J logger.
	 */
	public static final class LoggingWriter extends Writer {
		@Override
		public void write(final char[] cbuf, final int off, final int len) {
			// Don't bother with empty lines.
			if (len > 1) {
				logger.info(new String(cbuf, off, len));
			}
		}

		@Override
		public void flush() {
			// noop.
		}

		@Override
		public void close() {
			// noop.
		}
	}

	public static Writer bridge() {
		return new LoggingWriter();
	}
}