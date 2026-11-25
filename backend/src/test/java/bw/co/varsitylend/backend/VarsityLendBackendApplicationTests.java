
package bw.co.varsitylend.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class VarsityLendBackendApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
    }

    @Test
    void unauthenticatedUsersCannotAccessAdminEndpoints() throws Exception {
        mockMvc.perform(get("/api/admin/applications"))
            .andExpect(status().isForbidden());
    }

    @Test
    void unknownEndpointsAreBlocked() throws Exception {
        mockMvc.perform(get("/not-a-protected-route"))
            .andExpect(status().isForbidden());
    }
}
