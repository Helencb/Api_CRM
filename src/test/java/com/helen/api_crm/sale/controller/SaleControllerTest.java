package com.helen.api_crm.sale.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.helen.api_crm.sale.dto.SaleCancelRequestDTO;
import com.helen.api_crm.sale.dto.SaleItemRequestDTO;
import com.helen.api_crm.sale.dto.SaleRequestDTO;
import com.helen.api_crm.sale.dto.SaleResponseDTO;
import com.helen.api_crm.sale.model.PaymentMethod;
import com.helen.api_crm.sale.model.SaleStatus;
import com.helen.api_crm.sale.service.SaleService;
import com.helen.api_crm.security.jwt.JwtFilter;
import com.helen.api_crm.security.jwt.JwtService;
import com.helen.api_crm.security.service.AuthorizationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(SaleController.class)
public class SaleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SaleService saleService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtFilter jwtFilter;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockBean
    private AuthorizationService authorizationService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() throws ServletException, IOException {
        // Mock do filtro JWT para não bloquear os testes
        doAnswer(invocation -> {
            ServletRequest request = invocation.getArgument(0);
            ServletResponse response = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(request, response);
            return null;
        }).when(jwtFilter).doFilter(any(ServletRequest.class), any(ServletResponse.class), any(FilterChain.class));
    }

    // Teste de Criar venda
    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    void shouldCreateSaleSuccessfully() throws Exception {
        SaleRequestDTO request = createSaleRequest();
        // Usando o helper que retorna valores de 2500.00
        SaleResponseDTO response = createSaleResponse(SaleStatus.PENDING);

        when(saleService.createSale(any(SaleRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/sales")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                // Correção: Verifica se o description bate com o helper
                .andExpect(jsonPath("$.description").value("Venda de teste"))
                // Correção: O valor total no helper é 2500, não 1000
                .andExpect(jsonPath("$.totalValue").value(2500.00))
                .andExpect(jsonPath("$.clientId").value(1L))
                .andExpect(jsonPath("$.sellerId").value(1L))
                .andExpect(jsonPath("$.status").value("PENDING"))
                // Correção: Itens ficam dentro de uma lista, não na raiz
                .andExpect(jsonPath("$.items[0].productName").value("Notebook Gamer"))
                .andExpect(jsonPath("$.items[0].quantity").value(1));
    }

    // Teste Venda Completa
    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    void shouldCompleteSaleSuccessfully() throws Exception {
        SaleResponseDTO response = createCompletedSaleResponse();

        when(saleService.completeSale(1L))
                .thenReturn(response);

        mockMvc.perform(put("/api/sales/1/complete")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    // Teste de venda cancelada
    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    void shouldCancelSaleSuccessfully() throws Exception {
        SaleCancelRequestDTO request = createSaleCancelRequest();
        SaleResponseDTO response = createCanceledSaleResponse();

        when(saleService.cancelSale(eq(1L), eq(request.getFailureReason())))
                .thenReturn(response);

        mockMvc.perform(put("/api/sales/{id}/cancel", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELED"))
                .andExpect(jsonPath("$.failureReason").value("Cliente desistiu"));
    }

    // Teste de vendas por ID
    @Test
    @WithMockUser(username = "seller@test.com", roles = {"SELLER"})
    void shouldGetSalesByIdSuccessfully() throws Exception {
        SaleResponseDTO response = createSaleResponse(SaleStatus.PENDING);

        // Correção de possível typo: canAcessSale -> canAccessSale (ajuste conforme o nome real no seu service)
        when(authorizationService.canAcessSale(eq(1L), any()))
                .thenReturn(true);
        when(saleService.getSaleById(1L))
                .thenReturn(response);

        mockMvc.perform(get("/api/sales/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.clientName").value("Maria Cliente"));
    }

    // --- Helpers ---

    private SaleRequestDTO createSaleRequest() {
        SaleRequestDTO request = new SaleRequestDTO();
        request.setClientId(1L);
        request.setSellerId(1L);
        request.setDescription("Venda de teste");
        request.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        request.setDiscount(BigDecimal.ZERO);

        SaleItemRequestDTO item = new SaleItemRequestDTO();
        item.setProductId(10L);
        item.setQuantity(1);

        request.setItems(List.of(item));
        return request;
    }

    private SaleResponseDTO createSaleResponse(SaleStatus status) {
        SaleResponseDTO response = new SaleResponseDTO();
        response.setId(1L);
        response.setClientId(1L);
        response.setClientName("Maria Cliente");
        response.setSellerId(1L);
        response.setSellerName("João Vendedor");
        response.setStatus(status);
        response.setDescription("Venda de teste");
        response.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        response.setSubtotal(new BigDecimal("2500.00"));
        response.setDiscount(BigDecimal.ZERO);
        response.setTotalValue(new BigDecimal("2500.00"));
        response.setCreatedAt(LocalDateTime.now());

        SaleResponseDTO.SaleItemResponse itemResponse = new SaleResponseDTO.SaleItemResponse(
                "Notebook Gamer",
                1,
                new BigDecimal("2500.00"),
                new BigDecimal("2500.00")
        );
        response.setItems(List.of(itemResponse));
        return response;
    }

    private SaleResponseDTO createCompletedSaleResponse() {
        // Reutilizando o método de criação via setter para consistência,
        // ou você pode manter o construtor se ele existir na sua classe DTO.
        // Aqui ajustei para bater com o objeto esperado no teste.
        SaleResponseDTO response = new SaleResponseDTO();
        response.setId(1L);
        response.setStatus(SaleStatus.COMPLETED);
        response.setTotalValue(BigDecimal.valueOf(1000));
        response.setDescription("Venda completa");

        SaleResponseDTO.SaleItemResponse item = new SaleResponseDTO.SaleItemResponse(
                "Computador", 1, BigDecimal.valueOf(1000), BigDecimal.valueOf(1000)
        );
        response.setItems(List.of(item));
        return response;
    }

    private SaleResponseDTO createCanceledSaleResponse() {
        SaleResponseDTO response = new SaleResponseDTO();
        response.setId(1L);
        response.setStatus(SaleStatus.CANCELED);
        response.setFailureReason("Cliente desistiu");
        response.setTotalValue(BigDecimal.valueOf(1000));

        return response;
    }

    private SaleCancelRequestDTO createSaleCancelRequest() {
        SaleCancelRequestDTO request = new SaleCancelRequestDTO();
        request.setFailureReason("Cliente desistiu");
        return request;
    }
}
