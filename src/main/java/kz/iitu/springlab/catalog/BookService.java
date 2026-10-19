package kz.iitu.springlab.catalog;

import kz.iitu.springlab.web.Genre;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class BookService {

    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    public List<Book> findAll(String author) {
        return findAll(author, null, null);
    }

    /**
     * Individual Variant 2: Sorting and filtering.
     * Supports:
     * - ?genre=NOVEL (filtering by genre)
     * - ?author=Bloch (filtering by author)
     * - ?sort=year,desc (sorting by year, title, author, id, genre, createdAt)
     * An unknown sort field throws InvalidSortFieldException (handled as 400 ProblemDetail).
     */
    public List<Book> findAll(String author, Genre genre, String sort) {
        Stream<Book> stream = repository.findAll().stream();

        if (author != null && !author.isBlank()) {
            stream = stream.filter(book -> book.author() != null
                    && book.author().toLowerCase().contains(author.toLowerCase().trim()));
        }

        if (genre != null) {
            stream = stream.filter(book -> book.genre() == genre);
        }

        if (sort != null && !sort.isBlank()) {
            String[] parts = sort.split(",");
            String field = parts[0].trim();
            String direction = parts.length > 1 ? parts[1].trim().toLowerCase() : "asc";

            Comparator<Book> comparator = switch (field.toLowerCase()) {
                case "year" -> Comparator.comparingInt(Book::year);
                case "title" -> Comparator.comparing(Book::title, String.CASE_INSENSITIVE_ORDER);
                case "author" -> Comparator.comparing(Book::author, String.CASE_INSENSITIVE_ORDER);
                case "id" -> Comparator.comparing(Book::id);
                case "genre" -> Comparator.comparing(b -> b.genre() != null ? b.genre().name() : "");
                case "createdat" -> Comparator.comparing(Book::createdAt);
                default -> throw new InvalidSortFieldException(field);
            };

            if ("desc".equals(direction)) {
                comparator = comparator.reversed();
            }
            stream = stream.sorted(comparator);
        }

        return stream.toList();
    }

    public Optional<Book> findById(long id) {
        return repository.findById(id);
    }

    public Book create(Book book) {
        boolean duplicate = repository.findAll().stream()
                .anyMatch(b -> b.title() != null && b.title().equalsIgnoreCase(book.title().trim())
                        && b.author() != null && b.author().equalsIgnoreCase(book.author().trim()));
        if (duplicate) {
            throw new DuplicateTitleException(book.title());
        }
        return repository.save(book);
    }

    public Book update(long id, Book book) {
        Book existing = repository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        boolean duplicate = repository.findAll().stream()
                .anyMatch(b -> !b.id().equals(id)
                        && b.title() != null && b.title().equalsIgnoreCase(book.title().trim())
                        && b.author() != null && b.author().equalsIgnoreCase(book.author().trim()));
        if (duplicate) {
            throw new DuplicateTitleException(book.title());
        }

        Book updated = new Book(id, book.title(), book.author(), book.year(), book.genre(), existing.createdAt());
        return repository.save(updated);
    }

    public void delete(long id) {
        if (!repository.deleteById(id)) {
            throw new BookNotFoundException(id);
        }
    }

    public List<Book> findByYear(int year) {
        return repository.findAll().stream()
                .filter(book -> book.year() == year)
                .toList();
    }
}
