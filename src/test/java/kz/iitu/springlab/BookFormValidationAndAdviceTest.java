package kz.iitu.springlab;

import kz.iitu.springlab.web.BookForm;
import kz.iitu.springlab.web.Genre;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
public class BookFormValidationAndAdviceTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Task 1.4: GET /books/new returns 200, view books/form, empty form & genres in model")
    void testGetNewForm() throws Exception {
        mockMvc.perform(get("/books/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/form"))
                .andExpect(model().attributeExists("form", "genres"))
                .andExpect(model().attribute("form", org.hamcrest.Matchers.isA(BookForm.class)));
    }

    @Test
    @DisplayName("Task 2.5 Case 1: Empty title fails validation and redisplays form")
    void testEmptyTitleRejected() throws Exception {
        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "")
                        .param("author", "Martin Fowler")
                        .param("year", "2002")
                        .param("genre", "SCIENCE"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/form"))
                .andExpect(model().attributeHasFieldErrors("form", "title"));
    }

    @Test
    @DisplayName("Task 2.5 Case 2: Year 1200 fails range validation (1450-2100)")
    void testYearOutOfRangeRejected() throws Exception {
        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "Refactoring")
                        .param("author", "Martin Fowler")
                        .param("year", "1200")
                        .param("genre", "SCIENCE"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/form"))
                .andExpect(model().attributeHasFieldErrors("form", "year"));
    }

    @Test
    @DisplayName("Task 2.5 Case 3: No genre selected fails @NotNull validation")
    void testNoGenreRejected() throws Exception {
        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "Refactoring")
                        .param("author", "Martin Fowler")
                        .param("year", "2002")
                        .param("genre", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("books/form"))
                .andExpect(model().attributeHasFieldErrors("form", "genre"));
    }

    @Test
    @DisplayName("Task 4.1-4.2: Custom constraint @NoDigits rejects author with digits")
    void testCustomConstraintNoDigits() throws Exception {
        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "Effective Java")
                        .param("author", "Bloch 2018")
                        .param("year", "2018")
                        .param("genre", "SCIENCE"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/form"))
                .andExpect(model().attributeHasFieldErrors("form", "author"));
    }

    @Test
    @DisplayName("Individual Variant 2: Class-level @ValidReprintYear rejects reprintYear < year")
    void testVariant2ClassLevelConstraintFails() throws Exception {
        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "Design Patterns")
                        .param("author", "Gamma")
                        .param("year", "1994")
                        .param("reprintYear", "1990")
                        .param("genre", "SCIENCE"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/form"))
                .andExpect(model().hasErrors());
    }

    @Test
    @DisplayName("Task 2.5 Case 4: Correct submission redirects to /books with flash attribute")
    void testValidFormSubmissionPRG() throws Exception {
        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "Clean Architecture")
                        .param("author", "Robert Martin")
                        .param("year", "2017")
                        .param("reprintYear", "2020")
                        .param("genre", "SCIENCE")
                        .param("available", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"))
                .andExpect(flash().attribute("message", containsString("Clean Architecture")));
    }

    @Test
    @DisplayName("Task 3: Switching locale to Russian (?lang=ru)")
    void testLocaleSwitchingRu() throws Exception {
        mockMvc.perform(get("/books/new").param("lang", "ru"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Новая книга")))
                .andExpect(content().string(containsString("Название")))
                .andExpect(content().string(containsString("Автор")));
    }

    @Test
    @DisplayName("Task 3: Switching locale to Kazakh (?lang=kk)")
    void testLocaleSwitchingKk() throws Exception {
        mockMvc.perform(get("/books/new").param("lang", "kk"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Жаңа кітап")))
                .andExpect(content().string(containsString("Атауы")))
                .andExpect(content().string(containsString("Авторы")));
    }

    @Test
    @DisplayName("Task 4.3-4.4: PageExceptionHandler handles BookNotFoundException for GET /books/999")
    void testPageExceptionHandler() throws Exception {
        mockMvc.perform(get("/books/999"))
                .andExpect(status().isOk())
                .andExpect(view().name("error/not-found"))
                .andExpect(model().attribute("message", "Book 999 not found"))
                .andExpect(content().string(containsString("Book 999 not found")));
    }

    @Test
    @DisplayName("Task 4.3-4.4: ApiExceptionHandler handles BookNotFoundException for GET /api/books/999")
    void testApiExceptionHandler() throws Exception {
        mockMvc.perform(get("/api/books/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Book not found"))
                .andExpect(jsonPath("$.detail").value("Book 999 not found"));
    }
}
