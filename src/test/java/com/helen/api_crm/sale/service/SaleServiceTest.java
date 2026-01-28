package com.helen.api_crm.sale.service;

import com.helen.api_crm.clients.model.Client;
import com.helen.api_crm.clients.repository.ClientRepository;
import com.helen.api_crm.common.enums.Role;
import com.helen.api_crm.exception.BusinessException;
import com.helen.api_crm.exception.ResourceNotFoundException;
import com.helen.api_crm.product.model.Product;
import com.helen.api_crm.product.repository.ProductRepository;
import com.helen.api_crm.sale.dto.SaleItemRequestDTO;
import com.helen.api_crm.sale.dto.SaleRequestDTO;
import com.helen.api_crm.sale.dto.SaleResponseDTO;
import com.helen.api_crm.sale.mapper.SaleMapper;
import com.helen.api_crm.sale.model.PaymentMethod;
import com.helen.api_crm.sale.model.Sale;
import com.helen.api_crm.sale.repository.SaleRepository;
import com.helen.api_crm.security.model.SecurityUser;
import com.helen.api_crm.seller.model.Seller;
import com.helen.api_crm.seller.repository.SellerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SaleServiceTest {

    @InjectMocks
    private SaleService saleService;

    @Mock
    private SaleMapper saleMapper;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }
    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void mockLoggedUser(Long userId, Role role) {
        SecurityUser user = new SecurityUser(userId, "test@crm.com", role.name());
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
    }

    @Test
    void shouldFailWhenClientNotFound() {
        SaleRequestDTO request = createSaleRequest();

        when(clientRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                saleService.createSale(request));

        verify(clientRepository).findById(1L);
        verifyNoInteractions(sellerRepository, saleRepository, saleMapper);
    }

    @Test
    void shouldFailWhenSellerNotFound() {
        SaleRequestDTO request = createSaleRequest();

        when(clientRepository.findById(request.getClientId()))
                .thenReturn(Optional.of(new Client()));
        when(sellerRepository.findById(request.getSellerId()))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                saleService.createSale(request));

        verify(sellerRepository).findById(request.getSellerId());
    }

    @Test
    void shouldFailWhenProductNotFound() {
        SaleRequestDTO request = createSaleRequest();

        mockLoggedUser(99L, Role.MANAGER);

        when(clientRepository.findById(request.getClientId())).thenReturn(Optional.of(new Client()));
        when(sellerRepository.findById(request.getSellerId())).thenReturn(Optional.of(new Seller()));

        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> saleService.createSale(request));
    }

    @Test
    void shouldCreateSaleSuccessfully() {
        SaleRequestDTO request = createSaleRequest();

        mockLoggedUser(1L, Role.SELLER);

        Client client = new Client();
        client.setId(1L);

        Seller seller = new Seller();
        seller.setId(1L);

        Product product = new Product();
        product.setId(10L);
        product.setName("Notebook");
        product.setPrice(new BigDecimal("2000.00"));
        product.setStockQuantity(10);
        product.setActive(true);

        Sale sale = new Sale();
        sale.setItems(new ArrayList<>());

        SaleResponseDTO responseDTO = new SaleResponseDTO();

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));

        when(saleMapper.toEntity(client, seller)).thenReturn(sale);

        when(saleRepository.save(any(Sale.class))).thenReturn(sale);
        when(saleMapper.toDTO(sale)).thenReturn(responseDTO);

        SaleResponseDTO result = saleService.createSale(request);

        assertNotNull(result);

        verify(clientRepository).findById(1L);
        verify(sellerRepository).findById(1L);
        verify(productRepository).findById(10L);
        verify(saleMapper).toEntity(client, seller);
        verify(saleRepository).save(sale);
    }

    @Test
    void shouldFailWhenSellerCreatesSaleForAnother() {
        SaleRequestDTO request = createSaleRequest();
        request.setSellerId(2L);

        mockLoggedUser(1L, Role.SELLER);

        when(clientRepository.findById(any())).thenReturn(Optional.of(new Client()));
        when(sellerRepository.findById(any())).thenReturn(Optional.of(new Seller()));

        assertThrows(BusinessException.class, () -> saleService.createSale(request));
    }

    private SaleRequestDTO createSaleRequest() {
        SaleRequestDTO request = new SaleRequestDTO();
        request.setClientId(1L);
        request.setSellerId(1L);
        request.setDescription("Venda Teste");
        request.setPaymentMethod(PaymentMethod.CREDIT_CARD);

        SaleItemRequestDTO item = new SaleItemRequestDTO();
        item.setProductId(10L);
        item.setQuantity(2);

        request.setItems(List.of(item));

        return request;
    }
}
