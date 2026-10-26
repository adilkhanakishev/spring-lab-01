package kz.iitu.springlab;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Order(1)
    @DisplayName("Task 4: GET /api/books returns 200 OK and paged BookResponse DTOs")
    void testListBooks() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(3))))
                .andExpect(jsonPath("$.content[0].title").exists())
                .andExpect(jsonPath("$.content[0].author").exists())
                .andExpect(jsonPath("$.content[0].year").exists())
                .andExpect(jsonPath("$.content[0].genre").exists())
                .andExpect(jsonPath("$.content[0].createdAt").exists())
                .andExpect(jsonPath("$.totalElements").exists());
    }

    @Test
    @Order(2)
    @DisplayName("Task 4: GET /api/books?genre=SCIENCE returns 200 OK and filtered page")
    void testListFilteredByGenre() throws Exception {
        mockMvc.perform(get("/api/books").param("genre", "SCIENCE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[0].genre").value("SCIENCE"));
    }

    @Test
    @Order(3)
    @DisplayName("Task 2: GET /api/books/1 returns 200 OK and BookResponse")
    void testFindExisting() throws Exception {
        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Effective Java"))
                .andExpect(jsonPath("$.genre").value("SCIENCE"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @Order(4)
    @DisplayName("Task 3: GET /api/books/999 returns 404 Not Found and ProblemDetail")
    void testFindMissing() throws Exception {
        mockMvc.perform(get("/api/books/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.type").value("https://api.iitu.kz/errors/book-not-found"))
                .andExpect(jsonPath("$.title").value("Book not found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("Book 999 not found"));
    }

    @Test
    @Order(5)
    @DisplayName("Task 2: POST /api/books returns 201 Created and Location header")
    void testCreateBook() throws Exception {
        String json = """
                {
                    "title": "Pro Spring 6",
                    "author": "Iuliana Cosmina",
                    "year": 2023,
                    "genre": "SCIENCE"
                }
                """;
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/books/")))
                .andExpect(jsonPath("$.title").value("Pro Spring 6"))
                .andExpect(jsonPath("$.author").value("Iuliana Cosmina"))
                .andExpect(jsonPath("$.genre").value("SCIENCE"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @Order(6)
    @DisplayName("Task 2: PUT /api/books/1 returns 200 OK and updated BookResponse")
    void testUpdateBook() throws Exception {
        String json = """
                {
                    "title": "Effective Java 3rd Edition",
                    "author": "Joshua Bloch",
                    "year": 2018,
                    "genre": "SCIENCE"
                }
                """;
        mockMvc.perform(put("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Effective Java 3rd Edition"));
    }

    @Test
    @Order(7)
    @DisplayName("Task 2: DELETE /api/books/2 returns 204 first time, then 404 second time")
    void testDeleteTwice() throws Exception {
        mockMvc.perform(delete("/api/books/2"))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/api/books/2"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @Order(8)
    @DisplayName("Task 3: POST /api/books with blank title returns 400 Bad Request ProblemDetail")
    void testValidationFailure() throws Exception {
        String invalidJson = """
                {
                    "title": "",
                    "author": "Martin Fowler",
                    "year": 2002,
                    "genre": "SCIENCE"
                }
                """;
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @Order(9)
    @DisplayName("Task 3: POST /api/books duplicate title and author returns 409 Conflict ProblemDetail")
    void testDuplicateConflict() throws Exception {
        String duplicateJson = """
                {
                    "title": "Effective Java 3rd Edition",
                    "author": "Joshua Bloch",
                    "year": 2018,
                    "genre": "SCIENCE"
                }
                """;
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(duplicateJson))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.detail").value("A book with title 'Effective Java 3rd Edition' already exists"));
    }

    @Test
    @Order(10)
    @DisplayName("Task 4: GET /api/books?sort=year,desc&genre=SCIENCE returns paged and sorted books")
    void testPaginationAndSorting() throws Exception {
        mockMvc.perform(get("/api/books")
                        .param("sort", "year,desc")
                        .param("genre", "SCIENCE"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @Order(11)
    @DisplayName("Task 4: GET /api/books?sort=nosuchfield returns 400 Bad Request ProblemDetail")
    void testUnknownSortField() throws Exception {
        mockMvc.perform(get("/api/books")
                        .param("sort", "nosuchfield,desc"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Invalid sort field"))
                .andExpect(jsonPath("$.field").value("nosuchfield"));
    }

    @Test
    @Order(12)
    @DisplayName("Task 4: GET /v3/api-docs returns valid OpenAPI JSON document")
    void testOpenApiDocs() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.openapi").exists())
                .andExpect(jsonPath("$.paths['/api/books']").exists())
                .andExpect(jsonPath("$.components.schemas.BookRequest").exists())
                .andExpect(jsonPath("$.components.schemas.BookResponse").exists());
    }
}
