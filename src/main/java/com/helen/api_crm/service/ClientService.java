package com.helen.api_crm.service;

import com.helen.api_crm.dto.ClientRequestDTO;
import com.helen.api_crm.dto.ClientResponseDTO;
import com.helen.api_crm.entity.Client;
import com.helen.api_crm.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<ClientResponseDTO> findAll() {
        return clientRepository.findAll()
                .stream()
                .map(client -> new ClientResponseDTO(
                        client.getId(),
                        client.getNome(),
                        client.getEmail(),
                        client.getTelefone()
                ))
                .toList();
    }
}
