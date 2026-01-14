package com.helen.api_crm.exception;

import com.helen.api_crm.clients.controller.ClientController;
import com.helen.api_crm.clients.service.ClientService;
import com.helen.api_crm.security.jwt.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClientController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
public class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientService clientService;

    @MockBean
    private JwtService jwtService;

    @Test
    @WithMockUser(username = "admin", roles = {"MANAGER"})
    void shouldReturn404WhenNotFound() throws Exception {

        when(clientService.getClientById(1L))
                .thenThrow(new ResourceNotFoundException("Client not found"));

        mockMvc.perform(get("/api/clients/1"))
                .andExpect(status().isNotFound());
    }
}
