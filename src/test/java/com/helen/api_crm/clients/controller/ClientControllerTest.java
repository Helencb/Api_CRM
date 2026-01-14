package com.helen.api_crm.clients.controller;

import com.helen.api_crm.clients.dto.ClientResponseDTO;
import com.helen.api_crm.clients.service.ClientService;
import com.helen.api_crm.security.jwt.JwtFilter;
import com.helen.api_crm.security.jwt.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@WebMvcTest(ClientController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientService clientService;

    @MockBean
    JwtService jwtService;

    @MockBean
    JwtFilter jwtFilter;

    //Teste para criar clientes
    @Test
    void shouldCreateClient() throws Exception {
        ClientResponseDTO response =
                new ClientResponseDTO(1L, "Helen", "helen@crm.com", "18996067534");

        when(clientService.createClient(any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Helen\",\"email\":\"helen@crm.com\",\"phone\":\"18996067534\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Helen"))
                .andExpect(jsonPath("$.email").value("helen@crm.com"))
                .andExpect(jsonPath("$.phone").value("18996067534"));
    }

    // Teste para listar todos os clientes
    @Test
    void shouldGetAllClients() throws Exception {
        List<ClientResponseDTO> clientsList = List.of(
                new ClientResponseDTO(1L, "Helen", "helen@crm.com", "18996067534"),
                new ClientResponseDTO(2L, "Adam", "adam@crm.com", "18999999999")
        );

        Page<ClientResponseDTO> clientPage = new PageImpl<>(clientsList);

        when(clientService.getAllClients(any(Pageable.class)))
                .thenReturn(clientPage);

        mockMvc.perform(get("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Helen"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Adam"));
    }

    // Teste para buscar cliente por ID
    @Test
    void shouldGetClientById() throws Exception {
        ClientResponseDTO response =
                new ClientResponseDTO(1L, "Helen", "helen@crm.com", "18996067534");
        when(clientService.getClientById(1L)).thenReturn(response);
        mockMvc.perform(get("/api/clients/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Helen"))
                .andExpect(jsonPath("$.email").value("helen@crm.com"))
                .andExpect(jsonPath("$.phone").value("18996067534"));
    }

}
