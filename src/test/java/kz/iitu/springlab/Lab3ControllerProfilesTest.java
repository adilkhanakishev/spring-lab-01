package kz.iitu.springlab;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class Lab3ControllerProfilesTest {

    @Nested
    @SpringBootTest
    @AutoConfigureMockMvc
    @ActiveProfiles("dev")
    class DevProfileTest {

        @Autowired
        private MockMvc mockMvc;

        @Test
        void testDevProfileConfig() throws Exception {
            mockMvc.perform(get("/api/lab3/config"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.mailRetryCount").value(1))
                    .andExpect(jsonPath("$.mailEnabled").value(false))
                    .andExpect(jsonPath("$.paginationDefaultSize").value(10))
                    .andExpect(jsonPath("$.paginationMaxSize").value(50))
                    .andExpect(jsonPath("$.banner").value("DEVELOPMENT: the data is test data"));
        }
    }

    @Nested
    @SpringBootTest
    @AutoConfigureMockMvc
    @ActiveProfiles("prod")
    class ProdProfileTest {

        @Autowired
        private MockMvc mockMvc;

        @Test
        void testProdProfileConfig() throws Exception {
            mockMvc.perform(get("/api/lab3/config"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.mailRetryCount").value(5))
                    .andExpect(jsonPath("$.mailEnabled").value(true))
                    .andExpect(jsonPath("$.paginationDefaultSize").value(25))
                    .andExpect(jsonPath("$.paginationMaxSize").value(200))
                    .andExpect(jsonPath("$.banner").value("PRODUCTION: handle with care"));
        }
    }
}
