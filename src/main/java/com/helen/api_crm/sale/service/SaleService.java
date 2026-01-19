package com.helen.api_crm.sale.service;

import com.helen.api_crm.clients.model.Client;
import com.helen.api_crm.clients.repository.ClientRepository;
import com.helen.api_crm.exception.BusinessException;
import com.helen.api_crm.exception.ResourceNotFoundException;
import com.helen.api_crm.sale.mapper.SaleMapper;
import com.helen.api_crm.sale.dto.SaleRequestDTO;
import com.helen.api_crm.sale.dto.SaleResponseDTO;
import com.helen.api_crm.sale.model.Sale;
import com.helen.api_crm.sale.model.SaleStatus;
import com.helen.api_crm.sale.repository.SaleRepository;
import com.helen.api_crm.seller.model.Seller;
import com.helen.api_crm.seller.repository.SellerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;




import java.time.LocalDateTime;
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
    @Transactional
    public SaleResponseDTO createSale (SaleRequestDTO dto) {
        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + dto.getClientId()));

        Seller seller = sellerRepository.findById(dto.getSellerId())
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found with id: " + dto.getSellerId()));

        LocalDateTime now = LocalDateTime.now();
        Sale sale = saleMapper.toEntity(dto, client, seller, now);
        sale.setStatus(SaleStatus.PENDING);

        Sale savedSale = saleRepository.save(sale);
        return saleMapper.toDTO(savedSale);
    }

    @Transactional(readOnly = true)
    public List<SaleResponseDTO> getAllSales() {
        return saleRepository.findAll()
                .stream()
                .map(saleMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SaleResponseDTO getSaleById(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with id: " + id));
        return saleMapper.toDTO(sale);
    }

    @Transactional
    public SaleResponseDTO completeSale(Long id) {

        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with id: " + id));

        if (sale.getStatus() == SaleStatus.COMPLETED){
            throw new BusinessException("Sale is already completed");
        }

        if (sale.getStatus() == SaleStatus.CANCELED) {
            throw new BusinessException("Canceled sale cannot be completed: " + sale.getFailureReason());
        }

        sale.setStatus(SaleStatus.COMPLETED);
        sale.setFailureReason(null);

        Sale savedSale = saleRepository.save(sale);

        return saleMapper.toDTO(savedSale);
    }


    @Transactional
    public SaleResponseDTO cancelSale(Long id, String failureReason) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with id: " + id));

        if (sale.getStatus() == SaleStatus.COMPLETED){
            throw new BusinessException("Completed sale cannot be canceled");
        }
        if (failureReason != null && failureReason.isBlank()) {
            throw new BusinessException("Sale failure reason: " + failureReason);
        }

        sale.setStatus(SaleStatus.CANCELED);
        sale.setFailureReason(failureReason);

        Sale savedSale = saleRepository.save(sale);
        return saleMapper.toDTO(savedSale);
    }
}
