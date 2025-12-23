package com.helen.api_crm.controller;

import com.helen.api_crm.dto.ClientRequestDTO;
import com.helen.api_crm.dto.ClientResponseDTO;
import com.helen.api_crm.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClientController{

    private final ClientService clientService;

    @PostMapping
    public ClientResponseDTO create(@RequestBody @Valid ClientRequestDTO dto){
        return clientService.save(dto);
    }

    @GetMapping("/paginado")
    public Page<ClientResponseDTO> findAllPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "nome") String sortBy){
        return clientService.findAllPaginated(page, size, sortBy);
    }

    @GetMapping("/{id}")
    public ClientResponseDTO search(@PathVariable Long id){
        return clientService.findById(id);
    }
}