package com.helen.api_crm.sale.service;

import com.helen.api_crm.clients.model.Client;
import com.helen.api_crm.clients.repository.ClientRepository;
import com.helen.api_crm.common.enums.Role;
import com.helen.api_crm.exception.BusinessException;
import com.helen.api_crm.exception.ResourceNotFoundException;
import com.helen.api_crm.product.model.Product;
import com.helen.api_crm.product.repository.ProductRepository;
import com.helen.api_crm.sale.dto.SaleItemRequestDTO;
import com.helen.api_crm.sale.mapper.SaleMapper;
import com.helen.api_crm.sale.dto.SaleRequestDTO;
import com.helen.api_crm.sale.dto.SaleResponseDTO;
import com.helen.api_crm.sale.model.Sale;
import com.helen.api_crm.sale.model.SaleItem;
import com.helen.api_crm.sale.model.SaleStatus;
import com.helen.api_crm.sale.repository.SaleRepository;
import com.helen.api_crm.security.model.SecurityUser;
import com.helen.api_crm.seller.model.Seller;
import com.helen.api_crm.seller.repository.SellerRepository;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final ClientRepository clientRepository;
    private final SellerRepository sellerRepository;
    private final ProductRepository productRepository;
    private final SaleMapper saleMapper;

    public SaleService(SaleRepository saleRepository, ClientRepository clientRepository, SellerRepository sellerRepository, ProductRepository productRepository, SaleMapper saleMapper) {
        this.saleRepository = saleRepository;
        this.clientRepository = clientRepository;
        this.sellerRepository = sellerRepository;
        this.productRepository = productRepository;
        this.saleMapper = saleMapper;
    }

    @Transactional
    public SaleResponseDTO createSale(SaleRequestDTO dto) {
        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));

        Seller seller = sellerRepository.findById(dto.getSellerId())
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        SecurityUser userLogado = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (userLogado.getRole() == Role.SELLER && !userLogado.getId().equals(dto.getSellerId())) {
            throw new BusinessException("Access denied");
        }

        Sale sale = saleMapper.toEntity(client, seller);
        sale.setCreatedAt(LocalDateTime.now());
        sale.setStatus(SaleStatus.PENDING);
        sale.setDescription(dto.getDescription());
        sale.setPaymentMethod(dto.getPaymentMethod());
        sale.setItems(new ArrayList<>());

        BigDecimal subtotal = BigDecimal.ZERO;

        for (SaleItemRequestDTO itemDto : dto.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            if (!product.isActive()) {
                throw new BusinessException("Product inactive");
            }

            if (product.getStockQuantity() < itemDto.getQuantity()) {
                throw new BusinessException("Insufficient stock for: " + product.getName());
            }

            SaleItem saleItem = new SaleItem();
            saleItem.setSale(sale);
            saleItem.setProduct(product);
            saleItem.setQuantity(itemDto.getQuantity());
            saleItem.setUnitPrice(product.getPrice());
            saleItem.setTotalPrice(product.getPrice().multiply(new BigDecimal(itemDto.getQuantity())));

            sale.getItems().add(saleItem);
            subtotal = subtotal.add(saleItem.getTotalPrice());
        }

        BigDecimal discount = dto.getDiscount() != null ? dto.getDiscount() : BigDecimal.ZERO;
        BigDecimal finalValue = subtotal.subtract(discount);

        sale.setSubtotal(subtotal);
        sale.setDiscount(discount);
        sale.setTotalValue(finalValue);

        return saleMapper.toDTO(saleRepository.save(sale));
    }

    @Transactional(readOnly = true)
    public Page<SaleResponseDTO> getAllSales(Pageable pageable) {
        SecurityUser userLogado = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userLogado.getRole() == Role.MANAGER) {
            return saleRepository.findAll(pageable).map(saleMapper::toDTO);
        }
        return saleRepository.findBySellerId(userLogado.getId(), pageable).map(saleMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public SaleResponseDTO getSaleById(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found"));
        return saleMapper.toDTO(sale);
    }

    @Transactional
    public SaleResponseDTO completeSale(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with id: " + id));

        if (sale.getStatus() != SaleStatus.PENDING) {
            throw new BusinessException("Only PENDING sales can be completed.");
        }

        for (SaleItem item : sale.getItems()) {
            Product product = productRepository.findByIdWithLock(item.getProduct().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found during completion"));

            if (product.getStockQuantity() < item.getQuantity()) {
                throw new BusinessException("Insufficient stock to complete sale for product: " + product.getName());
            }

            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepository.save(product);
        }
        sale.setStatus(SaleStatus.COMPLETED);
        return saleMapper.toDTO(saleRepository.save(sale));
    }

    @Transactional
    public SaleResponseDTO cancelSale(Long id, String failureReason) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found"));

        if (sale.getStatus() == SaleStatus.CANCELED){
            throw new BusinessException("Already canceled");
        }

        if (sale.getStatus() == SaleStatus.COMPLETED) {
            for (SaleItem item : sale.getItems()) {
                Product product = productRepository.findByIdWithLock(item.getProduct().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found during cancellation"));

                product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
                productRepository.save(product);
            }
        }

        sale.setStatus(SaleStatus.CANCELED);
        sale.setFailureReason(failureReason);
        return saleMapper.toDTO(saleRepository.save(sale));
    }
}