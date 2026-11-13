package kz.iitu.springlab.catalog;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.Objects;

/**
 * Individual Variant 2:
 * A @ManyToMany with attributes: A BookTag entity with an added addedAt column instead of a plain join table.
 */
@Entity
@Table(name = "book_tag")
public class BookTag {

    @EmbeddedId
    private BookTagId id = new BookTagId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("bookId")
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tagId")
    @JoinColumn(name = "tag_id")
    private Tag tag;

    @Column(name = "added_at", nullable = false)
    private Instant addedAt = Instant.now();

    protected BookTag() {
    }

    public BookTag(Book book, Tag tag) {
        this.book = book;
        this.tag = tag;
        this.addedAt = Instant.now();
        if (book != null && book.getId() != null && tag != null && tag.getId() != null) {
            this.id = new BookTagId(book.getId(), tag.getId());
        }
    }

    public BookTag(Book book, Tag tag, Instant addedAt) {
        this.book = book;
        this.tag = tag;
        this.addedAt = addedAt != null ? addedAt : Instant.now();
        if (book != null && book.getId() != null && tag != null && tag.getId() != null) {
            this.id = new BookTagId(book.getId(), tag.getId());
        }
    }

    public BookTagId getId() {
        return id;
    }

    public void setId(BookTagId id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public Instant getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(Instant addedAt) {
        this.addedAt = addedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookTag bookTag)) return false;
        return Objects.equals(book, bookTag.book) && Objects.equals(tag, bookTag.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(book, tag);
    }
}
