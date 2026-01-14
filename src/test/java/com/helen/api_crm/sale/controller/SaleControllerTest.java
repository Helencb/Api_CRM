package com.helen.api_crm.sale.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.helen.api_crm.sale.dto.SaleCancelRequestDTO;
import com.helen.api_crm.sale.dto.SaleRequestDTO;
import com.helen.api_crm.sale.dto.SaleResponseDTO;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.math.BigDecimal;
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
    private AuthorizationService authorizationService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() throws ServletException, IOException {
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
        SaleResponseDTO response = createSaleResponse();

        when(saleService.createSale(any(SaleRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/sales")
                        .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Computador"))
                .andExpect(jsonPath("$.totalValue").value(BigDecimal.valueOf(1000)))
                .andExpect(jsonPath("$.amount").value(1))
                .andExpect(jsonPath("$.clientId").value(1L))
                .andExpect(jsonPath("$.sellerId").value(1L))
                .andExpect(jsonPath("$.status").value("PENDING"));
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
            .andExpect(jsonPath("$.status").value("CANCELED"));
    }

    // Teste de todas as vendas
    @Test
    @WithMockUser(roles = {"SELLER"})
    void shouldGetAllSalesSuccessfully() throws Exception {
        when(saleService.getAllSales())
                .thenReturn(List.of(createSaleResponse()));

        mockMvc.perform(get("/api/sales"))
                .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1L));
    }

    //Teste de compras por ID
    @Test
    @WithMockUser(username = "seller@test.com", roles = {"SELLER"})
    void shouldGetSalesByIdSuccessfully() throws Exception {
        SaleResponseDTO response = createSaleResponse();

        when(authorizationService.canAcessSale(eq(1L), any()))
                .thenReturn(true);
        when(saleService.getSaleById(1L))
                .thenReturn(response);
        mockMvc.perform(get("/api/sales/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    private SaleRequestDTO createSaleRequest() {
        return new SaleRequestDTO(
                "Computador",
                BigDecimal.valueOf(1000),
                1,
                "Descrição do produto",
                1L,
                1L
        );
    }

    private SaleResponseDTO createSaleResponse() {
        return new SaleResponseDTO(
                1L,
                "Computador",
                BigDecimal.valueOf(1000),
                1,
                "Descrição do produto",
                SaleStatus.PENDING,
                null,
                1L,
                1L,
                java.time.LocalDateTime.now()
        );
    }

    private SaleResponseDTO createCompletedSaleResponse() {
        return new  SaleResponseDTO(
                1L,
                "Computador",
                BigDecimal.valueOf(1000),
                1,
                "Descrição do produto",
                SaleStatus.COMPLETED,
                null,
                1L,
                1L,
                java.time.LocalDateTime.now());
    }

    private SaleResponseDTO createCanceledSaleResponse() {
        return new SaleResponseDTO(
                1L,
                "Computador",
                BigDecimal.valueOf(1000),
                1,
                "Descrição do produto",
                SaleStatus.CANCELED,
                "Cliente desistiu",
                1L,
                1L,
                java.time.LocalDateTime.now());
    }

    private SaleCancelRequestDTO createSaleCancelRequest() {
        SaleCancelRequestDTO request = new SaleCancelRequestDTO();
        request.setFailureReason("Cliente desistiu");
        return request;
    }
}
