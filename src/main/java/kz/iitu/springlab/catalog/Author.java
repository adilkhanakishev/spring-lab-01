package kz.iitu.springlab.catalog;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "author")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 100)
    private String country;

    // The inverse side of the association: mappedBy references the 'author' field on Book.
    // It carries no foreign key column of its own.
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Book> books = new ArrayList<>();

    protected Author() {
        // Required by JPA
    }

    public Author(String name, String country) {
        this.name = name;
        this.country = country;
    }

    /**
     * Helper method to keep bidirectional relationship in sync in memory.
     * Prevents author_id = null on insert.
     */
    public void addBook(Book book) {
        books.add(book);
        book.setAuthor(this);
    }

    public void removeBook(Book book) {
        books.remove(book);
        book.setAuthor(null);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<Book> getBooks() {
        return books;
    }

    // Exclude 'books' collection from toString to prevent StackOverflowError
    @Override
    public String toString() {
        return "Author{id=" + id + ", name='" + name + "', country='" + country + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Author author)) return false;
        return Objects.equals(id, author.id) || (id == null && Objects.equals(name, author.name));
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : Objects.hashCode(name);
    }
}
