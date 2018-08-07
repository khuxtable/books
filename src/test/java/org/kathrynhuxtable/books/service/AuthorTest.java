package org.kathrynhuxtable.books.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kathrynhuxtable.books.persistence.dao.AuthorDAO;
import org.kathrynhuxtable.books.persistence.domain.Author;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class AuthorTest {

	@Autowired
	private TestEntityManager em;

	@Autowired
	private AuthorDAO authorDao;

	@Test
	public void testFind() {
		Author author = new Author();
		author.setLastName("Bogus");
		author.setFirstName("Joe");
		Author a = em.persist(author);

		Optional<Author> found = authorDao.findById(a.getId());
		assertThat(found.get().getLastName()).isEqualTo(a.getLastName());
	}
}
