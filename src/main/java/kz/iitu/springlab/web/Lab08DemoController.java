package kz.iitu.springlab.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kz.iitu.springlab.catalog.Book;
import kz.iitu.springlab.catalog.BookRepository;
import kz.iitu.springlab.catalog.BookService;
import kz.iitu.springlab.web.dto.BookMapper;
import kz.iitu.springlab.web.dto.BookResponse;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Tag(name = "Lab 08 Demonstration", description = "Endpoints for demonstrating Lab 08 queries, N+1, and Variant 2")
@RestController
@RequestMapping("/api/demo")
@Transactional(readOnly = true)
public class Lab08DemoController {

    private final BookRepository bookRepository;
    private final BookService bookService;
    private final BookMapper mapper;

    public Lab08DemoController(BookRepository bookRepository,
                              BookService bookService,
                              BookMapper mapper) {
        this.bookRepository = bookRepository;
        this.bookService = bookService;
        this.mapper = mapper;
    }

    @Operation(summary = "Task 3.1: Derived query findByGenre")
    @GetMapping("/queries/by-genre")
    public List<BookResponse> findByGenre(@RequestParam(defaultValue = "SCIENCE") Genre genre) {
        return mapper.toResponseList(bookRepository.findByGenre(genre));
    }

    @Operation(summary = "Task 3.1: Derived query findByTitleContainingIgnoreCase")
    @GetMapping("/queries/by-title")
    public List<BookResponse> findByTitle(@RequestParam(defaultValue = "clean") String fragment) {
        return mapper.toResponseList(bookRepository.findByTitleContainingIgnoreCase(fragment));
    }

    @Operation(summary = "Task 3.1: Derived query findByYearBetweenOrderByYearDesc")
    @GetMapping("/queries/by-year-range")
    public List<BookResponse> findByYearBetween(@RequestParam(defaultValue = "2005") int from,
                                                @RequestParam(defaultValue = "2020") int to) {
        return mapper.toResponseList(bookRepository.findByYearBetweenOrderByYearDesc(from, to));
    }

    @Operation(summary = "Task 3.1: Derived query findTop3ByGenreOrderByYearDesc")
    @GetMapping("/queries/top3-by-genre")
    public List<BookResponse> findTop3ByGenre(@RequestParam(defaultValue = "SCIENCE") Genre genre) {
        return mapper.toResponseList(bookRepository.findTop3ByGenreOrderByYearDesc(genre));
    }

    @Operation(summary = "Task 3.1: Derived query countByAuthorName (nested property)")
    @GetMapping("/queries/count-by-author")
    public Map<String, Object> countByAuthor(@RequestParam(defaultValue = "Joshua Bloch") String name) {
        long count = bookRepository.countByAuthorName(name);
        return Map.of("authorName", name, "bookCount", count);
    }

    @Operation(summary = "Task 3.2: JPQL search with named parameters")
    @GetMapping("/queries/jpql-search")
    public List<BookResponse> jpqlSearch(@RequestParam(defaultValue = "SCIENCE") Genre genre,
                                         @RequestParam(defaultValue = "2008") int year) {
        return mapper.toResponseList(bookRepository.search(genre, year));
    }

    @Operation(summary = "Task 3.2: Bulk update archiveOlderThan")
    @PostMapping("/queries/archive-older-than")
    @Transactional
    public Map<String, Object> archiveOlderThan(@RequestParam(defaultValue = "2010") int year) {
        int updatedCount = bookService.archiveOlderThan(year);
        return Map.of("yearThreshold", year, "archivedRowsCount", updatedCount);
    }

    @Operation(summary = "Task 3.4: N+1 demonstration without fetch join")
    @GetMapping("/n-plus-one/without-fetch-join")
    public List<String> nPlusOneWithoutFetch() {
        return bookService.loadAuthorsWithoutFetchJoin();
    }

    @Operation(summary = "Task 3.4: N+1 demonstration with fetch join")
    @GetMapping("/n-plus-one/with-fetch-join")
    public List<String> nPlusOneWithFetch() {
        return bookService.loadAuthorsWithFetchJoin();
    }

    @Operation(summary = "Individual Variant 2: BookTag associations with addedAt attribute")
    @GetMapping("/variant2/book-tags")
    public List<Map<String, Object>> variant2BookTags() {
        return bookService.getVariant2BookTags();
    }
}
