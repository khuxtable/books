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
package org.kathrynhuxtable.books.dataloader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 * Handle reading a CSV or tab-delimited text file.
 */
public class DataFileHandler {

	private File file;
	private DataHeaders headers;
	private List<DataRecord> results;
	private boolean isCsv;

	private BufferedWriter writer;

	public DataFileHandler(File file) throws FileNotFoundException, IOException, FileHandlerDataException {
		this.file = file;
		isCsv = file.getName().toLowerCase().endsWith(".csv");

		results = new ArrayList<>();

		if (isCsv) {
			try (Reader in = new FileReader(file)) {
				CSVParser parser = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);

				headers = new DataHeaders(parser.getHeaderMap().keySet());

				for (CSVRecord record : parser) {
					results.add(new DataRecord(record, headers));
				}
			}
		} else {
			List<String> lines = Files.readAllLines(file.toPath());
			if (lines.size() > 0) {
				String[] headings = lines.get(0).split("\t");

				headers = new DataHeaders(Arrays.asList(headings));

				boolean gotHeader = false;
				for (String line : lines) {
					if (!gotHeader) {
						gotHeader = true;
					} else {
						results.add(new DataRecord(arrayToMap(line, headings), headers));
					}
				}
			}
		}
	}

	public DataFileHandler(File file, boolean exportFlag) throws FileHandlerDataException, IOException {
		this.file = file;
		isCsv = file.getName().toLowerCase().endsWith(".csv");
		headers = new DataHeaders(DataHeaders.ALLOWED_FIELDS);
		writer = new BufferedWriter(new FileWriter(file));
		write(DataHeaders.ALLOWED_FIELDS_LIST);
	}

	public File getFile() {
		return file;
	}

	public DataHeaders getHeaders() {
		return headers;
	}

	public List<DataRecord> getResults() {
		return results;
	}

	public void write(DataRecord record) throws IOException {
		List<String> values = record.getValues();
		write(values);
	}

	private void write(List<String> values) throws IOException {
		if (isCsv) {
			writer.write(String.join(",", values.stream().map(str -> formatCsvValue(str)).collect(Collectors.toList())));
		} else {
			writer.write(String.join("\t", values));
		}
		writer.newLine();
	}

	private String formatCsvValue(String str) {
		if (str == null) {
			return "\"\"";
		}
		str = str.replaceAll("\"", "\"\"");
		return "\"" + str + "\"";
	}

	public void close() throws IOException {
		writer.close();
	}

	private Map<String, String> arrayToMap(String line, String[] headings) {
		Map<String, String> map = new HashMap<>();
		String[] fields = line.split("\t");
		for (int i = 0; i < headings.length; i++) {
			if (fields.length > i) {
				map.put(headings[i], fields[i] == null ? "" : cleanupTxtField(fields[i]));
			}
		}
		return map;
	}

	private String cleanupTxtField(String field) {
		if (field.startsWith("\"") && field.endsWith("\"")) {
			field = field.substring(1, field.length() - 1);
			// Replace any \" or "" with ".
			field = field.replaceAll("[\\\\\"]\"", "\"");
		}
		return field.trim();
	}
}