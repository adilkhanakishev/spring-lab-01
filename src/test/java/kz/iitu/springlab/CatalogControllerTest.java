package kz.iitu.springlab;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CatalogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetItem() throws Exception {
        mockMvc.perform(get("/api/lab4/item/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("Item no. 1"));
    }

    @Test
    void testGetItems() throws Exception {
        mockMvc.perform(get("/api/lab4/items?limit=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.limit").value(3))
                .andExpect(jsonPath("$.items[0]").value("Item no. 1"));
    }

    @Test
    void testRemoveItemValid() throws Exception {
        mockMvc.perform(delete("/api/lab4/item/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("Removed item no. 5"));
    }

    @Test
    void testRemoveItemInvalidException() throws Exception {
        mockMvc.perform(delete("/api/lab4/item/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid identifier: 0"));
    }

    @Test
    void testProxyInfo() throws Exception {
        mockMvc.perform(get("/api/lab4/proxy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.className", containsString("CatalogService")))
                .andExpect(jsonPath("$.superClass").value("CatalogService"))
                .andExpect(jsonPath("$.isAopProxy").value("true"))
                .andExpect(jsonPath("$.isCglib").value("true"));
    }

    @Test
    void testRemoveTwiceEndpoints() throws Exception {
        mockMvc.perform(get("/api/lab4/remove-twice/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("Removed item no. 5; Removed item no. 6"));

        mockMvc.perform(get("/api/lab4/remove-twice-fixed/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("Removed item no. 5; Removed item no. 6"));
    }

    @Test
    void testSlowMethodsEndpoint() throws Exception {
        mockMvc.perform(get("/api/lab4/slow-methods"))
                .andExpect(status().isOk());
    }
}
