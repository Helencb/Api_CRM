package com.helen.api_crm.manager.controller;

import com.helen.api_crm.manager.dto.ManagerRequestDTO;
import com.helen.api_crm.manager.dto.ManagerResponseDTO;
import com.helen.api_crm.manager.service.ManagerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/managers")
@PreAuthorize("hasRole('MANAGER')")
@Tag(name = "Gerentes", description = "Gerenciamento de Gerentes.")
public class ManagerController {

    private final ManagerService service;

    public ManagerController(ManagerService service) {
        this.service = service;
    }

    @Operation(summary = "Criar um novo gerente", description = "Registra um novo gerente")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "Gerente criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
    })
    @PostMapping
    public ResponseEntity<ManagerResponseDTO> create(
            @RequestBody  @Valid ManagerRequestDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createManager(dto));
    }

    @Operation(summary = "Listar gerentes", description = "Retorna uma lista de gerentes.")
    @GetMapping
    public ResponseEntity<List<ManagerResponseDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @Operation(summary = "Buscar gerente por ID", description = "Retorna detalhes de um gerente específico")
    @GetMapping("/{id}")
    public ResponseEntity<ManagerResponseDTO> findById(@PathVariable Long id){
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Atualiza o gerente", description = "Retorna um gerente específico para atualizar.")
    @PutMapping("/{id}")
    public ResponseEntity<ManagerResponseDTO> updateManager(@PathVariable Long id, @Valid @RequestBody ManagerRequestDTO dto) {
        return ResponseEntity.ok(service.updateManager(id, dto));
    }

    @Operation(summary = "Desativa o gerente", description = "Desativa o gerente.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable Long id){
        service.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
