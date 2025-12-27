package com.helen.api_crm.service;

import com.helen.api_crm.clients.service.ClientService;
import com.helen.api_crm.clients.dto.ClientRequestDTO;
import com.helen.api_crm.clients.dto.ClientResponseDTO;
import com.helen.api_crm.exception.ClientNotFoundException;
import com.helen.api_crm.clients.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.util.List;

@SpringBootTest
@RequiredArgsConstructor
public class ClientServiceTest {

    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientRepository clientRepository;

    @BeforeEach
    void setup() {
        clientRepository.deleteAll();
    }

    @Test
    void deveSalvarClienteComSucesso() {
        ClientRequestDTO dto = new ClientRequestDTO(
                "Jonatas Batista",
                "JonatasBatista@Gmail.com",
                "18999638858"
        );
        ClientResponseDTO responseDTO = clientService.save(dto);

        Assertions.assertNotNull(responseDTO.getId());
        Assertions.assertEquals(dto.getNome(), responseDTO.getNome());
        Assertions.assertEquals(dto.getEmail(), responseDTO.getEmail());
        Assertions.assertEquals(dto.getTelefone(), responseDTO.getTelefone());
    }

    @Test
    void deveListarTodosClientes() {
        clientService.save(new ClientRequestDTO("Médele Vitória", "MedeleVitoria@gmail.com", "18997456789"));
        clientService.save(new ClientRequestDTO("Adriana Paula", "AdrianaPaula@gmail.com", "18996543210"));

        List<ClientResponseDTO> clients = clientService.findAllPaginated(0, 10, "nome").getContent();

        Assertions.assertEquals(2, clients.size());

    }

    @Test
    void DeveLancarExcecaoQuandoClienteNaoExistir(){
        Long idInexistente = 999L;

        Exception exception = Assertions.assertThrows(ClientNotFoundException.class,
                () -> clientService.findById(idInexistente));

        Assertions.assertEquals("Cliente não encontrado", exception.getMessage());
    }

    @Test
    void deveRetornarClientesPaginados() {
        for (int i = 1; i <= 6; i++) {
            clientService.save(new ClientRequestDTO("Cliente " + i, "c" + i + "@email.com", "11111111" + i));
        }

        Page<ClientResponseDTO> pagina = clientService.findAllPaginated(0, 5,"nome");

        Assertions.assertEquals(5, pagina.getContent().size());
        Assertions.assertEquals(5, pagina.getTotalElements());
    }
}

