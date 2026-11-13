package kz.iitu.springlab;

import kz.iitu.springlab.catalog.Author;
import kz.iitu.springlab.catalog.AuthorRepository;
import kz.iitu.springlab.catalog.Book;
import kz.iitu.springlab.catalog.BookRepository;
import kz.iitu.springlab.catalog.BookTag;
import kz.iitu.springlab.catalog.BookTagRepository;
import kz.iitu.springlab.catalog.Tag;
import kz.iitu.springlab.catalog.TagRepository;
import kz.iitu.springlab.web.Genre;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class Lab08DataJpaTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private BookTagRepository bookTagRepository;

    @Test
    @Transactional
    @DisplayName("Task 1 & 2: Database seeding and associations check")
    void testInitialEntitiesAndAssociations() {
        assertThat(authorRepository.count()).isGreaterThanOrEqualTo(3);
        assertThat(bookRepository.count()).isGreaterThanOrEqualTo(8);
        assertThat(tagRepository.count()).isGreaterThanOrEqualTo(4);

        // Check author owns books
        Author bloch = authorRepository.findByName("Joshua Bloch").orElseThrow();
        assertThat(bloch.getBooks().size()).isGreaterThanOrEqualTo(3);

        // Check book owns author (foreign key in book)
        Book effectiveJava = bookRepository.findByTitleContainingIgnoreCase("Effective Java").getFirst();
        assertThat(effectiveJava.getAuthor()).isNotNull();
        assertThat(effectiveJava.getAuthor().getName()).isEqualTo("Joshua Bloch");

        // Check Variant 2: BookTag has attributes
        List<BookTag> tags = bookTagRepository.findByBookId(effectiveJava.getId());
        assertThat(tags).isNotEmpty();
        assertThat(tags.getFirst().getAddedAt()).isNotNull();
    }

    @Test
    @DisplayName("Task 3.1: Five derived queries return non-empty results")
    void testFiveDerivedQueries() {
        // Query 1: findByGenre
        List<Book> scienceBooks = bookRepository.findByGenre(Genre.SCIENCE);
        assertThat(scienceBooks).isNotEmpty();

        // Query 2: findByTitleContainingIgnoreCase
        List<Book> cleanBooks = bookRepository.findByTitleContainingIgnoreCase("clean");
        assertThat(cleanBooks).isNotEmpty();
        assertThat(cleanBooks).allMatch(b -> b.getTitle().toLowerCase().contains("clean"));

        // Query 3: findByYearBetweenOrderByYearDesc
        List<Book> yearRange = bookRepository.findByYearBetweenOrderByYearDesc(2005, 2020);
        assertThat(yearRange).isNotEmpty();
        assertThat(yearRange.getFirst().getYear()).isGreaterThanOrEqualTo(yearRange.getLast().getYear());

        // Query 4: findTop3ByGenreOrderByYearDesc
        List<Book> top3 = bookRepository.findTop3ByGenreOrderByYearDesc(Genre.SCIENCE);
        assertThat(top3).hasSizeLessThanOrEqualTo(3);

        // Query 5: countByAuthorName (nested property)
        long blochCount = bookRepository.countByAuthorName("Joshua Bloch");
        assertThat(blochCount).isGreaterThanOrEqualTo(3);
    }

    @Test
    @DisplayName("Task 3.2: JPQL search with named parameters")
    void testJpqlSearch() {
        List<Book> result = bookRepository.search(Genre.SCIENCE, 2008);
        assertThat(result).isNotEmpty();
        assertThat(result).allMatch(b -> b.getGenre() == Genre.SCIENCE && b.getYear() >= 2008);
    }

    @Test
    @Transactional
    @DisplayName("Task 3.2: Bulk update archiveOlderThan with @Modifying")
    void testBulkUpdateArchive() {
        int updated = bookRepository.archiveOlderThan(2010);
        assertThat(updated).isGreaterThan(0);

        List<Book> oldBooks = bookRepository.findByYearBetweenOrderByYearDesc(1900, 2009);
        assertThat(oldBooks).allMatch(Book::isArchived);
    }

    @Test
    @DisplayName("Task 3.4: N+1 cure with JOIN FETCH")
    void testFetchJoinAuthor() {
        List<Book> books = bookRepository.findAllWithAuthor();
        assertThat(books).isNotEmpty();
        for (Book b : books) {
            assertThat(b.getAuthor()).isNotNull();
            assertThat(b.getAuthor().getName()).isNotBlank();
        }
    }

    @Test
    @DisplayName("Task 4: Pagination (Page) vs Slice")
    void testPaginationAndSlice() {
        // Pageable
        Page<Book> page0 = bookRepository.findAll(PageRequest.of(0, 3, Sort.by("title").ascending()));
        assertThat(page0.getContent()).hasSize(3);
        assertThat(page0.getTotalElements()).isGreaterThanOrEqualTo(8);
        assertThat(page0.getTotalPages()).isGreaterThanOrEqualTo(3);

        // Slice
        Slice<Book> slice0 = bookRepository.findSliceByGenre(Genre.SCIENCE, PageRequest.of(0, 2));
        assertThat(slice0.getContent()).hasSize(2);
        assertThat(slice0.hasNext()).isTrue();
    }
}
