package kz.iitu.springlab.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kz.iitu.springlab.catalog.Book;
import kz.iitu.springlab.catalog.BookNotFoundException;
import kz.iitu.springlab.catalog.BookService;
import kz.iitu.springlab.web.dto.BookMapper;
import kz.iitu.springlab.web.dto.BookRequest;
import kz.iitu.springlab.web.dto.BookResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@Tag(name = "Books", description = "The catalogue of books")
@RestController
@RequestMapping("/api/books")
public class BookApiController {

    private final BookService service;
    private final BookMapper mapper;

    public BookApiController(BookService service, BookMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    /**
     * Task 4.1: Paged and sorted collection endpoint.
     * Accessible via:
     * - GET /api/books?page=0&size=3
     * - GET /api/books?page=1&size=3&sort=year,desc
     */
    @Operation(summary = "List books with pagination and sorting")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Page of books successfully retrieved"),
        @ApiResponse(responseCode = "400", description = "Invalid sort field",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping
    public Page<BookResponse> list(
            @Parameter(description = "Filter by genre", example = "SCIENCE")
            @RequestParam(required = false) Genre genre,
            @PageableDefault(size = 5, sort = "title") Pageable pageable) {

        return service.find(genre, pageable);
    }

    /**
     * Task 4.3: Slice version to demonstrate SQL query differences (no COUNT query).
     */
    @Operation(summary = "List books as Slice without total count overhead")
    @GetMapping("/slice")
    public Slice<BookResponse> listSlice(
            @RequestParam(required = false) Genre genre,
            @PageableDefault(size = 5, sort = "title") Pageable pageable) {

        return service.findSlice(genre, pageable);
    }

    /**
     * Legacy endpoint to support non-paged list queries if needed.
     */
    @Operation(summary = "List all books unpaged")
    @GetMapping("/all")
    public List<BookResponse> listAll(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) Genre genre,
            @RequestParam(required = false) String sort) {
        return mapper.toResponseList(service.findAll(author, genre, sort));
    }

    @Operation(summary = "Get a single book by identifier")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Book found"),
        @ApiResponse(responseCode = "404", description = "Book not found",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getById(@PathVariable long id) {
        BookResponse response = service.findResponseById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Create a new book")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Book successfully created"),
        @ApiResponse(responseCode = "400", description = "Validation error",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "409", description = "Duplicate book title for the same author",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    public ResponseEntity<BookResponse> create(@Valid @RequestBody BookRequest request) {
        Book toCreate = mapper.toDomain(request);
        BookResponse created = service.createResponse(toCreate, request.author());
        URI location = URI.create("/api/books/" + created.id());
        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Update an existing book")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Book successfully updated"),
        @ApiResponse(responseCode = "400", description = "Validation error",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "404", description = "Book not found",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "409", description = "Duplicate book title for the same author",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> update(@PathVariable long id, @Valid @RequestBody BookRequest request) {
        Book toUpdate = mapper.toDomain(request);
        BookResponse updated = service.updateResponse(id, toUpdate, request.author());
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Delete a book by identifier")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Book successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Book not found",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
