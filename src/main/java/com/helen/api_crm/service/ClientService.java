package com.helen.api_crm.service;

import com.helen.api_crm.dto.ClientRequestDTO;
import com.helen.api_crm.dto.ClientResponseDTO;
import com.helen.api_crm.entity.Client;
import com.helen.api_crm.exception.ClientNotFoundException;
import com.helen.api_crm.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
@Component
@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientResponseDTO save(ClientRequestDTO dto) {

        Client client = new Client();
        client.setNome(dto.getNome());
        client.setEmail(dto.getEmail());
        client.setTelefone(dto.getTelefone());

        Client savedClient = clientRepository.save(client);

        return new ClientResponseDTO(
                savedClient.getId(),
                savedClient.getNome(),
                savedClient.getEmail(),
                savedClient.getTelefone()
        );
    }

    public ClientResponseDTO findById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("Cliente não encontrado"));

        return new ClientResponseDTO(
                client.getId(),
                client.getNome(),
                client.getEmail(),
                client.getTelefone()
        );
    }

    public Page<ClientResponseDTO> findAllPaginated(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return clientRepository.findAll(pageable)
                .map(client -> new ClientResponseDTO(
                client.getId(),
                client.getNome(),
                client.getEmail(),
                client.getTelefone()
        ));
    }
}
