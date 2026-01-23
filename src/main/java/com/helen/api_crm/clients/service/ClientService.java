package com.helen.api_crm.clients.service;

import com.helen.api_crm.clients.dto.ClientRequestDTO;
import com.helen.api_crm.clients.dto.ClientResponseDTO;
import com.helen.api_crm.clients.mapper.ClientMapper;
import com.helen.api_crm.clients.model.Client;
import com.helen.api_crm.clients.repository.ClientRepository;
import com.helen.api_crm.exception.ClientNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    private final ClientMapper clientMapper;

    public ClientService(ClientRepository clientRepository, ClientMapper clientMapper) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
    }

    @Transactional
    public ClientResponseDTO createClient(ClientRequestDTO dto) {
        Client client = clientMapper.toEntity(dto);
        Client savedClient = clientRepository.save(client);
        return clientMapper.toDTO(savedClient);
    }

    @Transactional(readOnly = true)
    public Page<ClientResponseDTO> getAllClients(Pageable pageable) {
        return clientRepository.findAllByActiveTrue(pageable)
                .map(clientMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public ClientResponseDTO getClientById(Long id) {
        Client client = clientRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ClientNotFoundException("Client not found"));
        return clientMapper.toDTO(client);
    }

    @Transactional
    public ClientResponseDTO updateClient(Long id, ClientRequestDTO dto) {
        Client client = clientRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ClientNotFoundException("Client not found"));

        client.setName(dto.getName());
        client.setEmail(dto.getEmail());
        client.setPhone(dto.getPhone());

        return clientMapper.toDTO(clientRepository.save(client));
    }

    @Transactional
    public void deleteClient(Long id) {
        Client client = clientRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ClientNotFoundException("Client not found"));
        client.setActive(false);
        clientRepository.save(client);
    }
}

