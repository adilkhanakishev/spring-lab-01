package kz.iitu.springlab.catalog;

import kz.iitu.springlab.web.Genre;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Task 2.2:
 * Seed data on startup: three authors, eight books, four tags,
 * with at least one book carrying two tags and at least one author owning three books.
 */
@Component
@Order(1)
public class DatabaseInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseInitializer.class);

    private final AuthorRepository authorRepository;
    private final TagRepository tagRepository;
    private final BookRepository bookRepository;

    public DatabaseInitializer(AuthorRepository authorRepository,
                               TagRepository tagRepository,
                               BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.tagRepository = tagRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (authorRepository.count() > 0) {
            log.info("Database already seeded, skipping initialization.");
            return;
        }

        log.info("Seeding database with authors, tags, and books according to Lab 08 Task 2.2...");

        // 1. Create 3 Authors
        Author bloch = authorRepository.save(new Author("Joshua Bloch", "USA"));
        Author martin = authorRepository.save(new Author("Robert Martin", "USA"));
        Author walls = authorRepository.save(new Author("Craig Walls", "USA"));

        // 2. Create 4 Tags
        Tag tagJava = tagRepository.save(new Tag("Java"));
        Tag tagArch = tagRepository.save(new Tag("Architecture"));
        Tag tagBest = tagRepository.save(new Tag("BestPractices"));
        Tag tagSpring = tagRepository.save(new Tag("Spring"));

        // 3. Create 8 Books and associate with authors and tags
        // Author 1: Joshua Bloch (owns 3 books)
        Book b1 = new Book("Effective Java", Genre.SCIENCE, 2018);
        bloch.addBook(b1);
        b1.addTag(tagJava);
        b1.addTag(tagBest); // 2 tags!
        bookRepository.save(b1);

        Book b2 = new Book("Java Puzzlers", Genre.SCIENCE, 2005);
        bloch.addBook(b2);
        b2.addTag(tagJava);
        bookRepository.save(b2);

        Book b3 = new Book("Effective Java 2nd Edition", Genre.SCIENCE, 2008);
        bloch.addBook(b3);
        b3.addTag(tagJava);
        bookRepository.save(b3);

        // Author 2: Robert Martin (owns 3 books)
        Book b4 = new Book("Clean Code", Genre.NON_FICTION, 2008);
        martin.addBook(b4);
        b4.addTag(tagArch);
        b4.addTag(tagBest); // 2 tags!
        bookRepository.save(b4);

        Book b5 = new Book("Clean Architecture: Craftsman Guide", Genre.NON_FICTION, 2017);
        martin.addBook(b5);
        b5.addTag(tagArch);
        bookRepository.save(b5);

        Book b6 = new Book("The Clean Coder", Genre.NON_FICTION, 2011);
        martin.addBook(b6);
        b6.addTag(tagBest);
        bookRepository.save(b6);

        // Author 3: Craig Walls (owns 2 books)
        Book b7 = new Book("Spring in Action", Genre.FICTION, 2022);
        walls.addBook(b7);
        b7.addTag(tagSpring);
        b7.addTag(tagJava); // 2 tags!
        bookRepository.save(b7);

        Book b8 = new Book("Spring Boot in Practice", Genre.FICTION, 2020);
        walls.addBook(b8);
        b8.addTag(tagSpring);
        bookRepository.save(b8);

        log.info("Database seeding completed: {} authors, {} tags, {} books.",
                authorRepository.count(), tagRepository.count(), bookRepository.count());
    }
}
