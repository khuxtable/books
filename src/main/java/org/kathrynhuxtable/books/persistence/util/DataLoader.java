package org.kathrynhuxtable.books.persistence.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.kathrynhuxtable.books.persistence.dao.AuthorDAO;
import org.kathrynhuxtable.books.persistence.dao.BorrowerDAO;
import org.kathrynhuxtable.books.persistence.dao.TitleDAO;
import org.kathrynhuxtable.books.persistence.dao.VolumeDAO;
import org.kathrynhuxtable.books.persistence.domain.Author;
import org.kathrynhuxtable.books.persistence.domain.Title;
import org.kathrynhuxtable.books.persistence.domain.Volume;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataLoader {

	@Autowired
	private AuthorDAO authorDao;
	@Autowired
	private TitleDAO titleDao;
	@Autowired
	private VolumeDAO volumeDao;
	@Autowired
	private BorrowerDAO borrowerDao;

	public void load(String filename) {
		boolean heading = false;
		try {
			for (String line : Files.readAllLines(Paths.get(filename))) {
				if (!heading) {
					heading = true;
					String[] headings = line.split("\t");
					// TODO Process headings.
					continue;
				}

//				String asin = "";
//				String name = "";
//				String category = "";
//				String form = "";
//				String title = "";
//				String note = "";
//				String[] data = line.split("\t");
//				if (data.length > 0) {
//					asin = data[0].trim();
//				}
//				if (data.length > 1) {
//					name = data[1].trim();
//				}
//				if (data.length > 2) {
//					category = data[2].trim();
//				}
//				if (data.length > 3) {
//					form = data[3].trim();
//				}
//				if (data.length > 4) {
//					title = data[4].trim();
//				}
//				if (data.length > 5) {
//					note = data[5].trim();
//				}
//
//				List<Author> authors = findAuthors(name);
//				if (authors.isEmpty()) {
//					// Create new author
//					authors = new ArrayList<>();
//					authors.add(saveAuthor(name));
//				} else if (authors.size() > 1) {
//					System.out.println("Found multiple Author matches for \"" + name + "\": cannot load " + title);
//					continue;
//				}
//
//				List<Title> titles = titleDao.findByTitleAndAuthors(title, authors);
//				Title entry;
//				if (titles.isEmpty()) {
//					// Create new title
//					entry = saveTitle(title, authors, category, form, note);
//				} else if (titles.size() > 1) {
//					System.out.println("Found multiple title matches for \"" + title + "\": cannot load");
//					continue;
//				} else {
//					entry = titles.get(0);
//				}
//
//				saveVolume(entry, "Kindle", asin);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private List<Author> findAuthors(String name) {
		String lastName;
		String firstName;
		if (name.contains(",")) {
			String[] nameParts = name.split(",\\s*", 2);
			lastName = nameParts[0];
			firstName = nameParts[1];
		} else {
			lastName = name;
			firstName = "";
		}

		return authorDao.findByLastNameAndFirstName(lastName, firstName);
	}

	private Author saveAuthor(String name) {
		String lastName;
		String firstName;
		if (name.contains(",")) {
			String[] nameParts = name.split(",\\s*", 2);
			lastName = nameParts[0];
			firstName = nameParts[1];
		} else {
			lastName = name;
			firstName = "";
		}

		Author author = new Author();
		author.setLastName(lastName);
		author.setFirstName(firstName);
		author.setBirthPlace("");
		author.setNationality("");
		author.setNote("");

		return authorDao.save(author);
	}

	private Title saveTitle(String title, List<Author> authors, String category, String form, String note) {
		Title entry = new Title();
		entry.setTitle(title);
		entry.setAuthors(authors);
		entry.setCategory(category);
		entry.setForm(form);
		entry.setNote(note);
		return titleDao.save(entry);
	}

	private void saveVolume(Title entry, String binding, String asin) {
		Volume volume = new Volume();
		volume.setEntry(entry);
		volume.setBinding(binding);
		volume.setAsin(asin);
		volume.setIsbn("");
		volume.setLibraryOfCongress("");
		volume.setNote("");
		volume.setPublisher("");
		volumeDao.save(volume);
	}
}
