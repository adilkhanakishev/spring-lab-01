package kz.iitu.springlab;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import kz.iitu.springlab.catalog.BookService;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.jpa.properties.hibernate.generate_statistics=true")
@AutoConfigureMockMvc
@Transactional
class Lab08ApiIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired EntityManager entityManager;
    @Autowired EntityManagerFactory entityManagerFactory;
    @Autowired BookService bookService;

    @Test
    void pageAndSliceReturnTagsAndSliceDoesNotRunCountQuery() throws Exception {
        Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        entityManager.clear();
        statistics.clear();
        mvc.perform(get("/api/books").param("page", "0").param("size", "3").param("sort", "id,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(8))
                .andExpect(jsonPath("$.content[0].tags", hasItems("Java", "BestPractices")));
        long pageStatements = statistics.getPrepareStatementCount();

        entityManager.clear();
        statistics.clear();
        mvc.perform(get("/api/books/slice").param("page", "0").param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.last").value(false));
        long sliceStatements = statistics.getPrepareStatementCount();

        // One query for the slice, one for tags; Page additionally counts rows.
        assertThat(sliceStatements).isEqualTo(2);
        assertThat(pageStatements).isEqualTo(3);
    }

    @Test
    void fetchJoinRemovesTheExtraAuthorQueries() {
        Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        entityManager.clear();
        statistics.clear();
        assertThat(bookService.loadAuthorsWithoutFetchJoin()).hasSize(8);
        assertThat(statistics.getPrepareStatementCount()).isEqualTo(4);

        entityManager.clear();
        statistics.clear();
        assertThat(bookService.loadAuthorsWithFetchJoin()).hasSize(8);
        assertThat(statistics.getPrepareStatementCount()).isEqualTo(1);
    }

    @Test
    void unpagedAndDemoEndpointsKeepAuthorAndTags() throws Exception {
        mvc.perform(get("/api/books/all").param("author", "Joshua Bloch"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].author").value("Joshua Bloch"))
                .andExpect(jsonPath("$[0].tags", hasItems("Java", "BestPractices")));
        mvc.perform(get("/api/demo/queries/by-title").param("fragment", "Effective Java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].author").value("Joshua Bloch"))
                .andExpect(jsonPath("$[0].tags", hasItems("Java", "BestPractices")));
    }
}
