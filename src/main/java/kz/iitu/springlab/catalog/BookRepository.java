package kz.iitu.springlab.catalog;

import kz.iitu.springlab.web.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    // ---------------------------------------------------------
    // Task 3.1: Five derived queries
    // ---------------------------------------------------------

    List<Book> findByGenre(Genre genre);

    List<Book> findByTitleContainingIgnoreCase(String fragment);

    List<Book> findByYearBetweenOrderByYearDesc(int from, int to);

    List<Book> findTop3ByGenreOrderByYearDesc(Genre genre);

    long countByAuthorName(String name); // nested property query traversing Book -> Author -> name

    // ---------------------------------------------------------
    // Task 3.2: JPQL search and bulk update
    // ---------------------------------------------------------

    @Query("""
           select b from Book b
           where b.genre = :genre and b.year >= :year
           order by b.title""")
    List<Book> search(@Param("genre") Genre genre, @Param("year") int year);

    @Modifying(clearAutomatically = true)
    @Query("update Book b set b.archived = true where b.year < :year")
    int archiveOlderThan(@Param("year") int year);

    // ---------------------------------------------------------
    // Task 3.4: The N+1 exercise and fetch join cure
    // ---------------------------------------------------------

    @Query("select b from Book b join fetch b.author")
    List<Book> findAllWithAuthor();

    @EntityGraph(attributePaths = "author")
    List<Book> findWithGraphByGenre(Genre genre);

    // ---------------------------------------------------------
    // Task 4: Pagination and sorting
    // ---------------------------------------------------------

    @EntityGraph(attributePaths = "author")
    Page<Book> findByGenre(Genre genre, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = "author")
    Page<Book> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "author")
    Slice<Book> findSliceByGenre(Genre genre, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = "author")
    Optional<Book> findById(Long id);

    boolean existsByTitleIgnoreCaseAndAuthorNameIgnoreCase(String title, String authorName);
}
