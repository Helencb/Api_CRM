package com.helen.api_crm.sale.service;

import com.helen.api_crm.clients.model.Client;
import com.helen.api_crm.clients.repository.ClientRepository;
import com.helen.api_crm.exception.BusinessException;
import com.helen.api_crm.exception.ResourceNotFoundException;
import com.helen.api_crm.sale.mapper.SaleMapper;
import com.helen.api_crm.sale.dto.SaleRequestDTO;
import com.helen.api_crm.sale.dto.SaleResponseDTO;
import com.helen.api_crm.sale.model.Sale;
import com.helen.api_crm.sale.repository.SaleRepository;
import com.helen.api_crm.seller.model.Seller;
import com.helen.api_crm.seller.repository.SellerRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class SaleService {

    private final SaleRepository saleRepository;

    private final ClientRepository clientRepository;

    private final SellerRepository sellerRepository;

    private final SaleMapper saleMapper;

    public SaleService(SaleRepository saleRepository, ClientRepository clientRepository, SellerRepository sellerRepository, SaleMapper saleMapper) {
        this.saleRepository = saleRepository;
        this.clientRepository = clientRepository;
        this.sellerRepository = sellerRepository;
        this.saleMapper = saleMapper;
    }
    // Criar venda
    public SaleResponseDTO createSale (SaleRequestDTO dto) {

        // Buscar Cliente
        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + dto.getClientId()));

        //Buscar Vendedor
        Seller seller = sellerRepository.findById(dto.getSellerId())
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found with id: " + dto.getSellerId()));

        //Converter DTO -> Entity usando Mapper
        Sale sale = saleMapper.toEntiy(dto, client, seller);

        // Salvar no banco
        Sale savedSale = saleRepository.save(sale);

        //Converter Entity -> DTO de resposta
        return saleMapper.toDTO(savedSale);
    }

    // Listar todas as vendas
    public List<SaleResponseDTO> getAllSales() {
        return saleRepository.findAll()
                .stream()
                .map(saleMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Buscar venda por ID
    public SaleResponseDTO getSaleById(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with id: " + id));

        String emailLogado = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        boolean isManager = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"));
        if (!isManager && !sale.getSeller().getEmail().equals(emailLogado)) {
            throw new BusinessException("Invalid email or password");
        }

        return saleMapper.toDTO(sale);
    }

    // Concluir Venda
    public SaleResponseDTO completeSale(Long id) {

        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with id: " + id));

        if (sale.isCompleted()){
            throw new BusinessException("Sale is already completed");
        }

        if (sale.getFailureReason() != null) {
            throw new BusinessException("Sale failure reason: " + sale.getFailureReason());
        }

        sale.setCompleted(true);

        Sale savedSale = saleRepository.save(sale);

        return saleMapper.toDTO(savedSale);
    }

    //Cancelar Venda
    public SaleResponseDTO cancelSale(Long id, String failureReason) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with id: " + id));

        if (sale.isCompleted()){
            throw new BusinessException("Sale is already completed");
        }
        if (failureReason != null && failureReason.isBlank()) {
            throw new BusinessException("Sale failure reason: " + failureReason);
        }

        sale.setFailureReason(failureReason);
        sale.setCompleted(false);

        Sale savedSale = saleRepository.save(sale);

        return saleMapper.toDTO(savedSale);
    }
}
