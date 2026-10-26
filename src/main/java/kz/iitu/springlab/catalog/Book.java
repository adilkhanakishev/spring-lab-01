package kz.iitu.springlab.catalog;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import kz.iitu.springlab.web.Genre;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Genre genre;

    private int year;

    private boolean archived;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Version
    private long version;

    // Owning side of the Book-Author relationship: carries the foreign key column 'author_id'
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Author author;

    // Association with attributes (Variant 2): BookTag entity mapping to book_tag table with added_at
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BookTag> bookTags = new HashSet<>();

    protected Book() {
        // JPA requires a no-argument constructor (at least protected)
    }

    public Book(String title, Genre genre, int year) {
        this.title = title;
        this.genre = genre != null ? genre : Genre.FICTION;
        this.year = year;
        this.createdAt = Instant.now();
        this.archived = false;
    }

    public Book(String title, Author author, int year, Genre genre) {
        this.title = title;
        this.author = author;
        this.year = year;
        this.genre = genre != null ? genre : Genre.FICTION;
        this.createdAt = Instant.now();
        this.archived = false;
    }

    public Book(Long id, String title, Author author, int year, Genre genre, Instant createdAt) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
        this.genre = genre != null ? genre : Genre.FICTION;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.archived = false;
    }

    public void addTag(Tag tag) {
        BookTag bookTag = new BookTag(this, tag);
        bookTags.add(bookTag);
    }

    public void removeTag(Tag tag) {
        bookTags.removeIf(bt -> Objects.equals(bt.getTag(), tag));
    }

    public Set<Tag> getTags() {
        return bookTags.stream().map(BookTag::getTag).collect(Collectors.toSet());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public long getVersion() {
        return version;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Set<BookTag> getBookTags() {
        return bookTags;
    }

    public void setBookTags(Set<BookTag> bookTags) {
        this.bookTags = bookTags;
    }

    // Association fields omitted to prevent infinite recursion and StackOverflowError
    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", genre=" + genre +
                ", year=" + year +
                ", archived=" + archived +
                ", createdAt=" + createdAt +
                ", version=" + version +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book book)) return false;
        return Objects.equals(id, book.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
