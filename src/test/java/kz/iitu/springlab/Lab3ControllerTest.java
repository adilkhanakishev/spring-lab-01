package kz.iitu.springlab;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class Lab3ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testConfigEndpointDefaultProfile() throws Exception {
        mockMvc.perform(get("/api/lab3/config"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.owner").value("Akishev Adilkhan"))
                .andExpect(jsonPath("$.group").value("IS-2406"))
                .andExpect(jsonPath("$.mailFrom").value("no-reply@iitu.kz"))
                .andExpect(jsonPath("$.mailRetryCount").value(3))
                .andExpect(jsonPath("$.mailEnabled").value(true))
                .andExpect(jsonPath("$.paginationDefaultSize").value(20))
                .andExpect(jsonPath("$.paginationMaxSize").value(100))
                .andExpect(jsonPath("$.banner").value("no profile is active"));
    }
}
