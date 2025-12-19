package com.helen.api_crm.controller;

import com.helen.api_crm.dto.ClientRequestDTO;
import com.helen.api_crm.dto.ClientResponseDTO;
import com.helen.api_crm.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClientController{

    private final ClientService clientService;

    @PostMapping
    public ClientResponseDTO create(@RequestBody ClientRequestDTO dto){
        return clientService.save(dto);
    }

    @GetMapping
    public List<ClientResponseDTO> findAll(){
        return clientService.findAll();
    }
}