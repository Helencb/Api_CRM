package com.helen.api_crm.clients.service;

import com.helen.api_crm.clients.dto.ClientRequestDTO;
import com.helen.api_crm.clients.dto.ClientResponseDTO;
import com.helen.api_crm.clients.mapper.ClientMapper;
import com.helen.api_crm.clients.model.Client;
import com.helen.api_crm.clients.repository.ClientRepository;
import com.helen.api_crm.common.enums.Role;
import com.helen.api_crm.exception.BusinessException;
import com.helen.api_crm.exception.ClientNotFoundException;
import com.helen.api_crm.exception.ResourceNotFoundException;
import com.helen.api_crm.security.model.SecurityUser;
import com.helen.api_crm.seller.model.Seller;
import com.helen.api_crm.seller.repository.SellerRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final SellerRepository sellerRepository;

    public ClientService(ClientRepository clientRepository, ClientMapper clientMapper, SellerRepository sellerRepository) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
        this.sellerRepository = sellerRepository;
    }

    private SecurityUser getLoggedUser() {
        return (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /**
     * Valida se o email já está em uso por outro cliente.
     * @param email O email a ser verificado.
     * @param currentClientId O ID do cliente sendo atualizado (null se for criação).
     */
    private void validateEmailUniqueness(String email, Long currentClientId) {
        clientRepository.findByEmail(email).ifPresent(existingClient -> {
            if (currentClientId == null || !existingClient.getId().equals(currentClientId)) {
                throw new BusinessException("Email already in use by another client.");
            }
        });
    }

    private Client findClientByAccess(Long id) {
        SecurityUser userLogado = getLoggedUser();

        if (userLogado.getRole() == Role.SELLER) {
            return clientRepository.findByIdAndSellerIdAndActiveTrue(id, userLogado.getId())
                    .orElseThrow(() -> new ClientNotFoundException("Client not found or access denied"));
        }
        return clientRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ClientNotFoundException("Client not found"));
    }

    private Seller findActiveSellerById(Long sellerId) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found with ID: " + sellerId));
        if (!seller.isActive()) {
            throw new BusinessException("The specified seller is not active.");
        }
        return seller;
    }

    @Transactional
    public ClientResponseDTO createClient(ClientRequestDTO dto) {
        validateEmailUniqueness(dto.getEmail(), null);

        SecurityUser userLogado = getLoggedUser();
        Client client = clientMapper.toEntity(dto);

        if (userLogado.getRole() == Role.SELLER) {
            Seller seller = sellerRepository.findById(userLogado.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Seller profile not found for logged user"));
            client.setSeller(seller);
        }  else if (userLogado.getRole() == Role.MANAGER) {
            if (dto.getSellerId() != null) {
                Seller seller = sellerRepository.findById(dto.getSellerId())
                        .orElseThrow(() -> new ResourceNotFoundException("Vendedor indicado não encontrado."));
                client.setSeller(seller);
            } else {
                throw new BusinessException("Seller ID must be provided when creating a client as a manager.");
            }
        }
            Client savedClient = clientRepository.save(client);
            return clientMapper.toDTO(savedClient);
    }


    @Transactional(readOnly = true)
    public Page<ClientResponseDTO> getAllClients(Pageable pageable) {
       SecurityUser userLogado = getLoggedUser();

       if(userLogado.getRole() == Role.SELLER) {
           return clientRepository.findAllBySellerIdAndActiveTrue(userLogado.getId(), pageable)
                   .map(clientMapper::toDTO);
       }

        return clientRepository.findAllByActiveTrue(pageable)
                .map(clientMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public ClientResponseDTO getClientById(Long id) {
        Client client = findClientByAccess(id);
        return clientMapper.toDTO(client);
    }

    @Transactional
    public ClientResponseDTO updateClient(Long id, ClientRequestDTO dto) {
        Client client = findClientByAccess(id);
        SecurityUser userLogado = getLoggedUser();

        if (!client.getEmail().equals(dto.getEmail())) {
            validateEmailUniqueness(dto.getEmail(), id);
        }

        client.setName(dto.getName());
        client.setEmail(dto.getEmail());

        if(dto.getPhone() != null && !dto.getPhone().isBlank()) {
            client.setPhone(dto.getPhone());
        }
        if (userLogado.getRole() == Role.MANAGER && dto.getSellerId() != null) {
            if (!client.getSeller().getId().equals(dto.getSellerId())) {
                Seller newSeller = findActiveSellerById(dto.getSellerId());
                client.setSeller(newSeller);
            }
        }
        return clientMapper.toDTO(clientRepository.save(client));
    }

    @Transactional
    public void deleteClient(Long id) {
        Client client = findClientByAccess(id);
        client.setActive(false);
        clientRepository.save(client);
    }
}

