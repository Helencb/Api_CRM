package com.helen.api_crm.clients.service;

import com.helen.api_crm.clients.dto.ClientRequestDTO;
import com.helen.api_crm.clients.dto.ClientResponseDTO;
import com.helen.api_crm.clients.mapper.ClientMapper;
import com.helen.api_crm.clients.model.Client;
import com.helen.api_crm.clients.repository.ClientRepository;
import com.helen.api_crm.common.enums.Role;
import com.helen.api_crm.exception.BusinessException;
import com.helen.api_crm.exception.ClientNotFoundException;
import com.helen.api_crm.security.model.SecurityUser;
import com.helen.api_crm.seller.model.Seller;
import com.helen.api_crm.seller.repository.SellerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {

    @InjectMocks
    private ClientService clientService;

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityUser securityUser;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void mockLoggedUser(Long id, Role role) {
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getPrincipal()).thenReturn(securityUser);
        lenient().when(securityUser.getId()).thenReturn(id);
        lenient().when(securityUser.getRole()).thenReturn(role);
    }

    @Test
    void shouldCreateClientSuccessfully_WhenManager() {
        mockLoggedUser(1L, Role.MANAGER);

        ClientRequestDTO request = new ClientRequestDTO("Helen", "helen@crm.com", "123", 1L);
        Client client = new Client();
        client.setId(1L);

        when(clientRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty()); // Email livre
        when(clientMapper.toEntity(request)).thenReturn(client);
        when(clientRepository.save(client)).thenReturn(client);
        when(clientMapper.toDTO(client)).thenReturn(new ClientResponseDTO());

        clientService.createClient(request);

        verify(clientRepository).save(client);
        verify(sellerRepository, never()).findById(any()); // Manager não vincula vendedor automaticamente neste fluxo
    }

    @Test
    void shouldCreateClientSuccessfully_WhenSeller() {
        mockLoggedUser(10L, Role.SELLER);

        ClientRequestDTO request = new ClientRequestDTO("Helen", "helen@crm.com", "123", 1L);
        Client client = new Client();
        Seller seller = new Seller();
        seller.setId(10L);

        when(clientRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(clientMapper.toEntity(request)).thenReturn(client);
        when(sellerRepository.findById(10L)).thenReturn(Optional.of(seller));
        when(clientRepository.save(client)).thenReturn(client);
        when(clientMapper.toDTO(client)).thenReturn(new ClientResponseDTO());

        clientService.createClient(request);

        verify(client).setSeller(seller);
        verify(clientRepository).save(client);
    }

    @Test
    void shouldThrowException_WhenCreatingWithDuplicateEmail() {
        // Não importa quem está logado, validação ocorre antes
        ClientRequestDTO request = new ClientRequestDTO("Helen", "exists@crm.com", "123", 1L);
        Client existingClient = new Client();
        existingClient.setId(99L); // ID diferente

        when(clientRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(existingClient));

        assertThrows(BusinessException.class, () -> clientService.createClient(request));

        verify(clientRepository, never()).save(any());
    }

    @Test
    void shouldGetAllClients_AsManager_ReturnsAll() {
        mockLoggedUser(1L, Role.MANAGER);
        Pageable pageable = PageRequest.of(0, 10);

        when(clientRepository.findAllByActiveTrue(pageable)).thenReturn(new PageImpl<>(List.of()));

        clientService.getAllClients(pageable);

        verify(clientRepository).findAllByActiveTrue(pageable);
        verify(clientRepository, never()).findAllBySellerIdAndActiveTrue(any(), any());
    }

    @Test
    void shouldGetAllClients_AsSeller_ReturnsOnlyHisClients() {
        mockLoggedUser(10L, Role.SELLER);
        Pageable pageable = PageRequest.of(0, 10);

        when(clientRepository.findAllBySellerIdAndActiveTrue(10L, pageable)).thenReturn(new PageImpl<>(List.of()));

        clientService.getAllClients(pageable);

        verify(clientRepository).findAllBySellerIdAndActiveTrue(10L, pageable);
        verify(clientRepository, never()).findAllByActiveTrue(pageable);
    }

    @Test
    void shouldGetClientById_AsSeller_WhenClientIsHis() {
        mockLoggedUser(10L, Role.SELLER);
        Client client = new Client();
        client.setId(5L);

        when(clientRepository.findByIdAndSellerIdAndActiveTrue(5L, 10L)).thenReturn(Optional.of(client));
        when(clientMapper.toDTO(client)).thenReturn(new ClientResponseDTO());

        clientService.getClientById(5L);

        verify(clientRepository).findByIdAndSellerIdAndActiveTrue(5L, 10L);
    }

    @Test
    void shouldThrowException_AsSeller_WhenClientIsNotHis() {
        mockLoggedUser(10L, Role.SELLER);

        when(clientRepository.findByIdAndSellerIdAndActiveTrue(5L, 10L)).thenReturn(Optional.empty());

        assertThrows(ClientNotFoundException.class, () -> clientService.getClientById(5L));
    }

    @Test
    void shouldUpdateClient_WithUniqueEmailCheck() {
        mockLoggedUser(1L, Role.MANAGER); // Manager editando

        Long clientId = 5L;
        ClientRequestDTO request = new ClientRequestDTO("New Name", "new@email.com", "123", 1L);

        Client existingClient = new Client();
        existingClient.setId(clientId);
        existingClient.setEmail("old@email.com"); // Email mudou

        Client anotherClient = new Client();
        anotherClient.setId(99L); // Outro dono do email novo

        // Mock findClientByAccess
        when(clientRepository.findByIdAndActiveTrue(clientId)).thenReturn(Optional.of(existingClient));

        // Mock validação email (encontrou outro cliente com o email novo)
        when(clientRepository.findByEmail("new@email.com")).thenReturn(Optional.of(anotherClient));

        // Deve falhar
        assertThrows(BusinessException.class, () -> clientService.updateClient(clientId, request));
    }

    @Test
    void shouldUpdateClient_WhenEmailIsUnchanged_Success() {
        mockLoggedUser(1L, Role.MANAGER);

        Long clientId = 5L;
        ClientRequestDTO request = new ClientRequestDTO("New Name", "same@email.com", "123", 1L);

        Client existingClient = new Client();
        existingClient.setId(clientId);
        existingClient.setEmail("same@email.com"); // Email igual

        when(clientRepository.findByIdAndActiveTrue(clientId)).thenReturn(Optional.of(existingClient));
        when(clientRepository.save(existingClient)).thenReturn(existingClient);
        when(clientMapper.toDTO(existingClient)).thenReturn(new ClientResponseDTO());

        clientService.updateClient(clientId, request);

        // Não deve chamar findByEmail se o email não mudou
        verify(clientRepository, never()).findByEmail(any());
        verify(clientRepository).save(existingClient);
    }
}