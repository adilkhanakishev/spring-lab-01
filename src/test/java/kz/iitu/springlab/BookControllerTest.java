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
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Order(1)
    @DisplayName("GET /api/books returns 200 OK and list of books")
    void testListBooks() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].title").value("Effective Java"))
                .andExpect(jsonPath("$[0].author").value("Joshua Bloch"));
    }

    @Test
    @Order(2)
    @DisplayName("GET /api/books?author=Bloch&limit=1 returns 200 OK and filtered book")
    void testListFiltered() throws Exception {
        mockMvc.perform(get("/api/books")
                        .param("author", "Bloch")
                        .param("limit", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].author").value("Joshua Bloch"));
    }

    @Test
    @Order(3)
    @DisplayName("GET /api/books/1 returns 200 OK and book")
    void testFindExisting() throws Exception {
        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Effective Java"));
    }

    @Test
    @Order(4)
    @DisplayName("GET /api/books/999 returns 404 Not Found")
    void testFindMissing() throws Exception {
        mockMvc.perform(get("/api/books/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(5)
    @DisplayName("POST /api/books returns 201 Created and Location header")
    void testCreateBook() throws Exception {
        String json = "{\"title\":\"Pro Spring 6\",\"author\":\"Cosmina\",\"year\":2023}";
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.title").value("Pro Spring 6"))
                .andExpect(jsonPath("$.author").value("Cosmina"));
    }

    @Test
    @Order(6)
    @DisplayName("PUT /api/books/1 returns 200 OK and replaces book")
    void testUpdateBook() throws Exception {
        String json = "{\"title\":\"Effective Java 3rd Ed\",\"author\":\"Joshua Bloch\",\"year\":2018}";
        mockMvc.perform(put("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Effective Java 3rd Ed"));
    }

    @Test
    @Order(7)
    @DisplayName("DELETE /api/books/2 returns 204 then 404")
    void testDeleteTwice() throws Exception {
        mockMvc.perform(delete("/api/books/2"))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/api/books/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(8)
    @DisplayName("Variant 2: GET /api/books/by-year/2022 matches regex and returns 200 OK")
    void testVariant2Match() throws Exception {
        mockMvc.perform(get("/api/books/by-year/2022"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Spring in Action"));
    }

    @Test
    @Order(9)
    @DisplayName("Variant 2: GET /api/books/by-year/abc non-matching regex returns 404")
    void testVariant2NonMatch() throws Exception {
        mockMvc.perform(get("/api/books/by-year/abc"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(10)
    @DisplayName("Error status: GET /api/books/abc returns 400 Bad Request (type mismatch)")
    void testError400() throws Exception {
        mockMvc.perform(get("/api/books/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(11)
    @DisplayName("Error status: GET /api/nothing returns 404 Not Found (unmapped)")
    void testError404() throws Exception {
        mockMvc.perform(get("/api/nothing"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(12)
    @DisplayName("Error status: DELETE /api/books returns 405 Method Not Allowed")
    void testError405() throws Exception {
        mockMvc.perform(delete("/api/books"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @Order(13)
    @DisplayName("Error status: POST /api/books with Content-Type text/plain returns 415")
    void testError415() throws Exception {
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("hello world"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @Order(14)
    @DisplayName("Error status: GET /api/books with Accept application/xml returns 406")
    void testError406() throws Exception {
        mockMvc.perform(get("/api/books")
                        .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @Order(15)
    @DisplayName("MVC Page: GET /books renders list view and fills model")
    void testBooksPage() throws Exception {
        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/list"))
                .andExpect(model().attributeExists("books", "title"))
                .andExpect(content().string(containsString("Catalogue")));
    }

    @Test
    @Order(16)
    @DisplayName("MVC Post/Redirect/Get: POST /books redirects to /books with flash attribute")
    void testBooksPostRedirectGet() throws Exception {
        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "Java Precisely")
                        .param("author", "Sestoft")
                        .param("year", "2016"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"))
                .andExpect(flash().attribute("message", "Book «Java Precisely» has been added"));
    }

    @Test
    @Order(17)
    @DisplayName("Static Resources: GET /css/app.css returns 200 OK")
    void testStaticCss() throws Exception {
        mockMvc.perform(get("/css/app.css"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/css"));
    }

    @Test
    @Order(18)
    @DisplayName("Static Resources: GET / returns static index.html")
    void testStaticIndexHtml() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("index.html"));

        mockMvc.perform(get("/index.html"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Welcome to Book Catalog Application")));
    }
}
