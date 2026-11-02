package kz.iitu.springlab.web.dto;

import kz.iitu.springlab.catalog.Book;
import kz.iitu.springlab.catalog.BookTag;
import kz.iitu.springlab.catalog.Tag;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BookMapper {

    public Book toDomain(BookRequest request) {
        return new Book(request.title(), request.genre(), request.year());
    }

    public BookResponse toResponse(Book book) {
        String authorName = book.getAuthor() == null ? null : book.getAuthor().getName();
        Set<String> tagNames = book.getBookTags().stream()
                .map(BookTag::getTag)
                .filter(Objects::nonNull)
                .map(Tag::getName)
                .collect(Collectors.toSet());

        return new BookResponse(
                book.getId(),
                book.getTitle(),
                authorName,
                book.getYear(),
                book.getGenre(),
                book.getCreatedAt(),
                book.isArchived(),
                tagNames
        );
    }

    public List<BookResponse> toResponseList(List<Book> books) {
        return books.stream().map(this::toResponse).toList();
    }
}
