package com.helen.api_crm.clients.service;

import com.helen.api_crm.clients.dto.ClientRequestDTO;
import com.helen.api_crm.clients.dto.ClientResponseDTO;
import com.helen.api_crm.clients.mapper.ClientMapper;
import com.helen.api_crm.clients.model.Client;
import com.helen.api_crm.clients.repository.ClientRepository;
import com.helen.api_crm.exception.ClientNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {

    @InjectMocks
    private ClientService clientService;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    // Criar clientes com sucesso
    @Test
    void shouldCreateClientSuccessfully() {
    ClientRequestDTO request = new ClientRequestDTO("Helen", "helen@crm.com", "18996067534");
    Client client = new Client();
        client.setId(1L);
        client.setName("Helen");
        client.setEmail("helen@crm.com");
        client.setPhone("18996067534");
    Client savedClient = new Client();
        savedClient.setId(1L);
        savedClient.setName("Helen");
        savedClient.setEmail("helen@crm.com");
        savedClient.setPhone("18996067534");
    ClientResponseDTO response = new ClientResponseDTO(1L, "Helen", "helen@crm.com", "18996067534" );

    when(clientMapper.toEntity(request)).thenReturn(client);
    when(clientRepository.save(client)).thenReturn(savedClient);
    when(clientMapper.toDTO(savedClient)).thenReturn(response);

    ClientResponseDTO result = clientService.createClient(request);

    assertNotNull(result);
    assertEquals("Helen", result.getName());
    assertEquals("helen@crm.com", result.getEmail());
    assertEquals("18996067534", result.getPhone());

    verify(clientRepository).save(client);
    verify(clientMapper).toEntity(request);
    verify(clientMapper).toDTO(savedClient);
    }

    // Teste para LANÇAR EXCEÇÃO QUANDO O CLIENTE NÃO FOR ENCONTRADO
    @Test
    void shouldThrowExceptionWhenClientNotFound() {
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        ClientNotFoundException exception = assertThrows(
                ClientNotFoundException.class,
                () -> clientService.getClientById(1L)
        );

        assertTrue(exception.getMessage().contains("Client not found"));

        verify(clientRepository).findById(1L);

    }
}