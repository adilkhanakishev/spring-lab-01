package kz.iitu.springlab.catalog;

import kz.iitu.springlab.web.Genre;
import kz.iitu.springlab.web.dto.BookMapper;
import kz.iitu.springlab.web.dto.BookResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@Transactional
public class BookService {

    private final BookRepository repository;
    private final AuthorRepository authorRepository;
    private final TagRepository tagRepository;
    private final BookTagRepository bookTagRepository;
    private final BookMapper mapper;

    public BookService(BookRepository repository,
                       AuthorRepository authorRepository,
                       TagRepository tagRepository,
                       BookTagRepository bookTagRepository,
                       BookMapper mapper) {
        this.repository = repository;
        this.authorRepository = authorRepository;
        this.tagRepository = tagRepository;
        this.bookTagRepository = bookTagRepository;
        this.mapper = mapper;
    }

    // ---------------------------------------------------------
    // Task 4: Paged and sorted queries
    // ---------------------------------------------------------

    @Transactional(readOnly = true)
    public Page<BookResponse> find(Genre genre, Pageable pageable) {
        Page<Book> page = (genre != null)
                ? repository.findByGenre(genre, pageable)
                : repository.findAll(pageable);
        return page.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Slice<BookResponse> findSlice(Genre genre, Pageable pageable) {
        Slice<Book> slice = (genre != null)
                ? repository.findSliceByGenre(genre, pageable)
                : repository.findAll(pageable);
        return slice.map(mapper::toResponse);
    }

    // ---------------------------------------------------------
    // Task 3: Derived queries and JPQL
    // ---------------------------------------------------------

    @Transactional(readOnly = true)
    public List<Book> search(Genre genre, int year) {
        return repository.search(genre, year);
    }

    @Transactional
    public int archiveOlderThan(int year) {
        return repository.archiveOlderThan(year);
    }

    @Transactional(readOnly = true)
    public List<Book> findByGenre(Genre genre) {
        return repository.findByGenre(genre);
    }

    @Transactional(readOnly = true)
    public List<Book> findByTitleContainingIgnoreCase(String fragment) {
        return repository.findByTitleContainingIgnoreCase(fragment);
    }

    @Transactional(readOnly = true)
    public List<Book> findByYearBetweenOrderByYearDesc(int from, int to) {
        return repository.findByYearBetweenOrderByYearDesc(from, to);
    }

    @Transactional(readOnly = true)
    public List<Book> findTop3ByGenreOrderByYearDesc(Genre genre) {
        return repository.findTop3ByGenreOrderByYearDesc(genre);
    }

    @Transactional(readOnly = true)
    public long countByAuthorName(String name) {
        return repository.countByAuthorName(name);
    }

    // ---------------------------------------------------------
    // Task 3.4: N+1 demonstration
    // ---------------------------------------------------------

    @Transactional(readOnly = true)
    public List<String> loadAuthorsWithoutFetchJoin() {
        List<Book> books = repository.findAll();
        return books.stream()
                .map(b -> b.getTitle() + " by " + (b.getAuthor() != null ? b.getAuthor().getName() : "Unknown"))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<String> loadAuthorsWithFetchJoin() {
        List<Book> books = repository.findAllWithAuthor();
        return books.stream()
                .map(b -> b.getTitle() + " by " + (b.getAuthor() != null ? b.getAuthor().getName() : "Unknown"))
                .toList();
    }

    // ---------------------------------------------------------
    // Individual Variant 2: BookTag associations
    // ---------------------------------------------------------

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getVariant2BookTags() {
        List<BookTag> all = bookTagRepository.findAll();
        return all.stream().map(bt -> {
            Map<String, Object> map = new HashMap<>();
            map.put("bookId", bt.getBook().getId());
            map.put("bookTitle", bt.getBook().getTitle());
            map.put("tagId", bt.getTag().getId());
            map.put("tagName", bt.getTag().getName());
            map.put("addedAt", bt.getAddedAt().toString());
            return map;
        }).toList();
    }

    // ---------------------------------------------------------
    // Standard CRUD and backwards-compatible methods
    // ---------------------------------------------------------

    @Transactional(readOnly = true)
    public List<Book> findAll(String author) {
        return findAll(author, null, null);
    }

    @Transactional(readOnly = true)
    public List<Book> findAll(String author, Genre genre, String sort) {
        List<Book> all = repository.findAllWithAuthor();
        Stream<Book> stream = all.stream();

        if (author != null && !author.isBlank()) {
            stream = stream.filter(book -> book.getAuthor() != null
                    && book.getAuthor().getName().toLowerCase().contains(author.toLowerCase().trim()));
        }

        if (genre != null) {
            stream = stream.filter(book -> book.getGenre() == genre);
        }

        if (sort != null && !sort.isBlank()) {
            String[] parts = sort.split(",");
            String field = parts[0].trim();
            String direction = parts.length > 1 ? parts[1].trim().toLowerCase() : "asc";

            Comparator<Book> comparator = switch (field.toLowerCase()) {
                case "year" -> Comparator.comparingInt(Book::getYear);
                case "title" -> Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER);
                case "author" -> Comparator.comparing(b -> b.getAuthor() != null ? b.getAuthor().getName() : "", String.CASE_INSENSITIVE_ORDER);
                case "id" -> Comparator.comparing(Book::getId);
                case "genre" -> Comparator.comparing(b -> b.getGenre() != null ? b.getGenre().name() : "");
                case "createdat" -> Comparator.comparing(Book::getCreatedAt);
                default -> throw new InvalidSortFieldException(field);
            };

            if ("desc".equals(direction)) {
                comparator = comparator.reversed();
            }
            stream = stream.sorted(comparator);
        }

        return stream.toList();
    }

    @Transactional(readOnly = true)
    public Optional<Book> findById(long id) {
        return repository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<BookResponse> findResponseById(long id) {
        return repository.findById(id).map(mapper::toResponse);
    }

    @Transactional
    public Book create(Book book, String authorName) {
        String cleanAuthor = (authorName != null && !authorName.isBlank()) ? authorName.trim() : null;
        if (cleanAuthor != null) {
            if (repository.existsByTitleIgnoreCaseAndAuthorNameIgnoreCase(book.getTitle().trim(), cleanAuthor)) {
                throw new DuplicateTitleException(book.getTitle());
            }
            Author author = authorRepository.findByName(cleanAuthor)
                    .orElseGet(() -> authorRepository.save(new Author(cleanAuthor, "Unknown")));
            author.addBook(book);
        }

        return repository.save(book);
    }

    @Transactional
    public BookResponse createResponse(Book book, String authorName) {
        Book created = create(book, authorName);
        return mapper.toResponse(created);
    }

    @Transactional
    public Book create(Book book) {
        String authorName = book.getAuthor() != null ? book.getAuthor().getName() : null;
        return create(book, authorName);
    }

    @Transactional
    public Book update(long id, Book book, String authorName) {
        Book existing = repository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        String cleanAuthor = (authorName != null && !authorName.isBlank()) ? authorName.trim() : null;
        if (cleanAuthor != null) {
            boolean duplicate = repository.findAll().stream()
                    .anyMatch(b -> !Objects.equals(b.getId(), id)
                            && b.getTitle() != null && b.getTitle().equalsIgnoreCase(book.getTitle().trim())
                            && b.getAuthor() != null && b.getAuthor().getName().equalsIgnoreCase(cleanAuthor));
            if (duplicate) {
                throw new DuplicateTitleException(book.getTitle());
            }

            Author author = authorRepository.findByName(cleanAuthor)
                    .orElseGet(() -> authorRepository.save(new Author(cleanAuthor, "Unknown")));
            author.addBook(existing);
        }

        existing.setTitle(book.getTitle());
        existing.setGenre(book.getGenre());
        existing.setYear(book.getYear());
        existing.setArchived(book.isArchived());

        return existing;
    }

    @Transactional
    public BookResponse updateResponse(long id, Book book, String authorName) {
        Book updated = update(id, book, authorName);
        return mapper.toResponse(updated);
    }

    public Book update(long id, Book book) {
        String authorName = book.getAuthor() != null ? book.getAuthor().getName() : null;
        return update(id, book, authorName);
    }

    @Transactional
    public void delete(long id) {
        if (!repository.existsById(id)) {
            throw new BookNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
