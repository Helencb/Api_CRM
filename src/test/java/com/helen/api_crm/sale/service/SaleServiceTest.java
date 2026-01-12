package com.helen.api_crm.sale.service;

import com.helen.api_crm.clients.model.Client;
import com.helen.api_crm.clients.repository.ClientRepository;
import com.helen.api_crm.exception.ResourceNotFoundException;
import com.helen.api_crm.sale.dto.SaleRequestDTO;
import com.helen.api_crm.sale.mapper.SaleMapper;
import com.helen.api_crm.sale.model.Sale;
import com.helen.api_crm.sale.repository.SaleRepository;
import com.helen.api_crm.seller.model.Seller;
import com.helen.api_crm.seller.repository.SellerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

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

    //Teste cliente não encontrado
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

    //Teste vendedor não encontrado
    @Test
    void shouldFailWhenSellerNotFound() {
        SaleRequestDTO request = createSaleRequest();

        when(clientRepository.findById(1L))
                .thenReturn(Optional.of(new Client()));
        when(sellerRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                saleService.createSale(request));

        verify(clientRepository).findById(1L);
        verify(sellerRepository).findById(1L);
        verifyNoInteractions(saleRepository, saleMapper);
    }

    // Venda criada com sucesso
    @Test
    void shouldCreateSaleSuccessfully() {
        SaleRequestDTO request = new SaleRequestDTO();
        request.setClientId(1L);
        request.setSellerId(1L);

        Client client = new Client();
        Seller seller = new Seller();
        Sale sale = new Sale();

        when(clientRepository.findById(1L))
                .thenReturn(Optional.of(client));
        when(sellerRepository.findById(1L))
                .thenReturn(Optional.of(seller));
        when(saleMapper.toEntity(request, client, seller)).thenReturn(sale);
        when(saleRepository.save(sale)).thenReturn(sale);

        saleService.createSale(request);

        verify(clientRepository).findById(1L);
        verify(sellerRepository).findById(1L);
        verify(saleMapper).toEntity(request, client, seller);
        verify(saleRepository).save(sale);
    }

    private SaleRequestDTO createSaleRequest() {
        SaleRequestDTO request = new SaleRequestDTO();
        request.setName("Computador");
        request.setTotalValue(new BigDecimal("1500.00"));
        request.setAmount(100);
        request.setDescription("Computador GAMER");
        request.setClientId(1L);
        request.setSellerId(1L);
        return request;
    }
}
