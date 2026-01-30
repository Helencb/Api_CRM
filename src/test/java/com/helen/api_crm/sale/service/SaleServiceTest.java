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
import com.helen.api_crm.sale.model.SaleItem;
import com.helen.api_crm.sale.model.SaleStatus;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

    // --- TESTES DE CREATE SALE ---

    @Test
    void shouldFailWhenClientNotFound() {
        SaleRequestDTO request = createSaleRequest();
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> saleService.createSale(request));
    }

    @Test
    void shouldFailWhenSellerNotFound() {
        SaleRequestDTO request = createSaleRequest();
        when(clientRepository.findById(request.getClientId())).thenReturn(Optional.of(new Client()));
        when(sellerRepository.findById(request.getSellerId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> saleService.createSale(request));
    }

    @Test
    void shouldFailWhenProductNotFound() {
        SaleRequestDTO request = createSaleRequest();
        mockLoggedUser(99L, Role.MANAGER);

        when(clientRepository.findById(any())).thenReturn(Optional.of(new Client()));
        when(sellerRepository.findById(any())).thenReturn(Optional.of(new Seller()));
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
        product.setStockQuantity(10); // Estoque suficiente
        product.setActive(true);

        Sale sale = new Sale();
        sale.setItems(new ArrayList<>());

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(saleMapper.toEntity(client, seller)).thenReturn(sale);
        when(saleRepository.save(any(Sale.class))).thenReturn(sale);
        when(saleMapper.toDTO(sale)).thenReturn(new SaleResponseDTO());

        SaleResponseDTO result = saleService.createSale(request);

        assertNotNull(result);
        verify(saleRepository).save(sale);
    }

    @Test
    void shouldFailWhenCreatingWithInsufficientStock() {
        SaleRequestDTO request = createSaleRequest();
        mockLoggedUser(1L, Role.MANAGER);

        Product product = new Product();
        product.setId(10L);
        product.setStockQuantity(1); // Estoque menor que o solicitado (2)
        product.setActive(true);

        when(clientRepository.findById(any())).thenReturn(Optional.of(new Client()));
        when(sellerRepository.findById(any())).thenReturn(Optional.of(new Seller()));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));

        assertThrows(BusinessException.class, () -> saleService.createSale(request));
    }

    // --- TESTES DE COMPLETE SALE (Onde usamos o LOCK) ---

    @Test
    void shouldCompleteSaleSuccessfully_WithLock() {
        Long saleId = 1L;
        Sale sale = new Sale();
        sale.setId(saleId);
        sale.setStatus(SaleStatus.PENDING);

        // Configurando Item da venda
        Product product = new Product();
        product.setId(10L);
        product.setStockQuantity(10);
        product.setName("Product Lock");

        SaleItem item = new SaleItem();
        item.setProduct(product);
        item.setQuantity(2);

        // Lista mutável para permitir o sort() no service
        List<SaleItem> items = new ArrayList<>();
        items.add(item);
        sale.setItems(items);

        // Mocks
        when(saleRepository.findById(saleId)).thenReturn(Optional.of(sale));
        // AQUI ESTÁ A MUDANÇA: Mockando o findByIdWithLock em vez de decrementStock
        when(productRepository.findByIdWithLock(10L)).thenReturn(Optional.of(product));
        when(saleRepository.save(sale)).thenReturn(sale);
        when(saleMapper.toDTO(sale)).thenReturn(new SaleResponseDTO());

        // Execução
        saleService.completeSale(saleId);

        // Verificações
        assertEquals(8, product.getStockQuantity()); // 10 - 2 = 8
        assertEquals(SaleStatus.COMPLETED, sale.getStatus());
        verify(productRepository).save(product); // Garante que salvou o produto atualizado
    }

    @Test
    void shouldFailCompleteSale_WhenStockInsufficient_DuringLock() {
        Long saleId = 1L;
        Sale sale = new Sale();
        sale.setId(saleId);
        sale.setStatus(SaleStatus.PENDING);

        Product product = new Product();
        product.setId(10L);
        product.setStockQuantity(1); // Estoque insuficiente para demanda de 2
        product.setName("Product Low Stock");

        SaleItem item = new SaleItem();
        item.setProduct(product);
        item.setQuantity(2);

        List<SaleItem> items = new ArrayList<>();
        items.add(item);
        sale.setItems(items);

        when(saleRepository.findById(saleId)).thenReturn(Optional.of(sale));
        when(productRepository.findByIdWithLock(10L)).thenReturn(Optional.of(product));

        assertThrows(BusinessException.class, () -> saleService.completeSale(saleId));

        // Garante que não salvou alteração de estoque nem mudou status da venda
        verify(productRepository, never()).save(product);
        assertEquals(SaleStatus.PENDING, sale.getStatus());
    }

    // --- TESTES DE CANCEL SALE ---

    @Test
    void shouldCancelCompletedSale_AndRestoreStock() {
        Long saleId = 1L;
        Sale sale = new Sale();
        sale.setId(saleId);
        sale.setStatus(SaleStatus.COMPLETED); // Venda já finalizada

        Product product = new Product();
        product.setId(10L);
        product.setStockQuantity(8); // Estoque atual

        SaleItem item = new SaleItem();
        item.setProduct(product);
        item.setQuantity(2);

        List<SaleItem> items = new ArrayList<>();
        items.add(item);
        sale.setItems(items);

        when(saleRepository.findById(saleId)).thenReturn(Optional.of(sale));
        when(productRepository.findByIdWithLock(10L)).thenReturn(Optional.of(product));
        when(saleRepository.save(sale)).thenReturn(sale);
        when(saleMapper.toDTO(sale)).thenReturn(new SaleResponseDTO());

        saleService.cancelSale(saleId, "Customer regret");

        assertEquals(10, product.getStockQuantity()); // 8 + 2 = 10 (Restaurado)
        assertEquals(SaleStatus.CANCELED, sale.getStatus());
        assertEquals("Customer regret", sale.getFailureReason());
        verify(productRepository).save(product);
    }

    // --- HELPER METHODS ---

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