package com.helen.api_crm.sales.service;

import com.helen.api_crm.clients.model.Client;
import com.helen.api_crm.clients.repository.ClientRepository;
import com.helen.api_crm.sales.dto.SaleMapper;
import com.helen.api_crm.sales.dto.SaleRequestDTO;
import com.helen.api_crm.sales.dto.SaleResponseDTO;
import com.helen.api_crm.sales.model.Sale;
import com.helen.api_crm.sales.repository.SaleRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SaleService {
    private final SaleRepository saleRepository;

    private final ClientRepository clientRepository;

    public SaleService(SaleRepository saleRepository, ClientRepository clientRepository) {
        this.saleRepository = saleRepository;
        this.clientRepository = clientRepository;
    }

    public SaleResponseDTO createSale(@Valid SaleRequestDTO request) {
        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new RuntimeException("Client not found with id: " + request.getClientId()));

        Sale sale = new Sale();
        sale.setNome(request.getNome());
        sale.setClient(client);
        sale.setAmount(request.getAmount());
        sale.setDescription(request.getDescription());
        sale.setDate(LocalDateTime.now());

        Sale response = saleRepository.save(sale);

        return SaleMapper.mapToSaleResponseDTO(response);
    }

    public List<Sale> listSalesByClient(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found with id: " + clientId));

        return saleRepository.findByClient(clientId);
    }

}
