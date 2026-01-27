package com.helen.api_crm.clients.controller;

import com.helen.api_crm.clients.service.ClientService;
import com.helen.api_crm.clients.dto.ClientRequestDTO;
import com.helen.api_crm.clients.dto.ClientResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "Gestão d carteira de clientes")
public class ClientController {

    private final ClientService clientService;

    @Operation(summary = "Criar um novo cliente", description = "Registra um novo cliente")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "Cliente criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Vendedor não encontrado"),
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'SELLER')")
    public ResponseEntity<ClientResponseDTO> createClient(@RequestBody @Valid ClientRequestDTO dto) {
        ClientResponseDTO response = clientService.createClient(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Listar clientes", description = "Retorna uma lista paginada de todos os clientes.")
    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'SELLER')")
    public ResponseEntity<Page<ClientResponseDTO>> getAllClients(
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC)
            Pageable pageable
            ) {
        return ResponseEntity.ok(clientService.getAllClients(pageable));
    }

    @Operation(summary = "Buscar cliente por ID", description = "Retorna detalhes de um cliente específico")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'SELLER')")
    public ResponseEntity<ClientResponseDTO> getClientById(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.getClientById(id));
    }

    @Operation(summary = "Atualiza o cliente", description = "Retorna um cliente específico para atualizar.")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'SELLER')")
    public ResponseEntity<ClientResponseDTO> updateClient(@PathVariable Long id, @RequestBody  @Valid ClientRequestDTO dto) {
        return ResponseEntity.ok(clientService.updateClient(id, dto));
    }

    @Operation(summary = "Desativa o cliente", description = "Desativa o cliente para não perder o histórico de vendas.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER')")
    public ResponseEntity<ClientResponseDTO> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build(); //Retorna 204
    }
}