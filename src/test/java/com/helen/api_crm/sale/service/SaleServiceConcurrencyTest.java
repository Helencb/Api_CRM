package com.helen.api_crm.sale.service;

import com.helen.api_crm.clients.model.Client;
import com.helen.api_crm.clients.repository.ClientRepository;
import com.helen.api_crm.common.enums.Role;
import com.helen.api_crm.exception.BusinessException;
import com.helen.api_crm.manager.model.Manager;
import com.helen.api_crm.manager.repository.ManagerRepository;
import com.helen.api_crm.product.model.Product;
import com.helen.api_crm.product.repository.ProductRepository;
import com.helen.api_crm.sale.model.PaymentMethod;
import com.helen.api_crm.sale.model.Sale;
import com.helen.api_crm.sale.model.SaleItem;
import com.helen.api_crm.sale.model.SaleStatus;
import com.helen.api_crm.sale.repository.SaleRepository;
import com.helen.api_crm.seller.model.Seller;
import com.helen.api_crm.seller.repository.SellerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Exercises the real database pessimistic lock (not a mocked repository) to prove
 * that two sales completed concurrently against the same product can't oversell it.
 */
@SpringBootTest
class SaleServiceConcurrencyTest {

    @Autowired
    private SaleService saleService;
    @Autowired
    private SaleRepository saleRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private SellerRepository sellerRepository;
    @Autowired
    private ManagerRepository managerRepository;

    @Test
    void completingTwoSalesConcurrently_neverOversellsTheSameProduct() throws InterruptedException {
        Manager manager = new Manager();
        manager.setEmail("concurrency-manager@crm.com");
        manager.setPassword("x");
        manager.setRole(Role.MANAGER);
        manager.setActive(true);
        manager.setName("Concurrency Manager");
        managerRepository.save(manager);

        Seller seller = new Seller();
        seller.setEmail("concurrency-seller@crm.com");
        seller.setPassword("x");
        seller.setRole(Role.SELLER);
        seller.setActive(true);
        seller.setName("Concurrency Seller");
        seller.setManager(manager);
        sellerRepository.save(seller);

        Client client = new Client();
        client.setName("Concurrency Client");
        client.setEmail("concurrency-client@crm.com");
        client.setActive(true);
        client.setSeller(seller);
        clientRepository.save(client);

        Product product = new Product();
        product.setName("Limited Stock Item");
        product.setPrice(new BigDecimal("100.00"));
        product.setStockQuantity(5); // Só há estoque para UMA das duas vendas (3 + 3 > 5)
        product.setActive(true);
        productRepository.save(product);

        Sale sale1 = buildPendingSale(client, seller, product, 3);
        Sale sale2 = buildPendingSale(client, seller, product, 3);
        saleRepository.save(sale1);
        saleRepository.save(sale2);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch readyLatch = new CountDownLatch(2);
        CountDownLatch startLatch = new CountDownLatch(1);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        AtomicReference<Exception> unexpectedError = new AtomicReference<>();

        Runnable completeSale1 = () -> {
            try {
                readyLatch.countDown();
                startLatch.await();
                saleService.completeSale(sale1.getId());
                successCount.incrementAndGet();
            } catch (BusinessException e) {
                failureCount.incrementAndGet();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                unexpectedError.set(e);
            }
        };

        Runnable completeSale2 = () -> {
            try {
                readyLatch.countDown();
                startLatch.await();
                saleService.completeSale(sale2.getId());
                successCount.incrementAndGet();
            } catch (BusinessException e) {
                failureCount.incrementAndGet();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                unexpectedError.set(e);
            }
        };

        executor.submit(completeSale1);
        executor.submit(completeSale2);

        readyLatch.await(5, TimeUnit.SECONDS);
        startLatch.countDown();

        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS), "Threads did not finish in time");

        if (unexpectedError.get() != null) {
            throw new AssertionError("Unexpected exception during concurrent completion", unexpectedError.get());
        }

        // O lock pessimista deve serializar as duas tentativas: só há estoque para uma delas.
        assertEquals(1, successCount.get(), "Exactly one sale should complete successfully");
        assertEquals(1, failureCount.get(), "Exactly one sale should fail due to insufficient stock");

        Product finalProduct = productRepository.findById(product.getId()).orElseThrow();
        assertEquals(2, finalProduct.getStockQuantity(), "Stock must reflect only ONE deduction (5 - 3), never oversold or double-deducted");
        assertTrue(finalProduct.getStockQuantity() >= 0, "Stock must never go negative");
    }

    private Sale buildPendingSale(Client client, Seller seller, Product product, int quantity) {
        Sale sale = new Sale();
        sale.setClient(client);
        sale.setSeller(seller);
        sale.setStatus(SaleStatus.PENDING);
        sale.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        sale.setDescription("Concurrency test sale");
        sale.setCreatedAt(LocalDateTime.now());

        BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(quantity));

        SaleItem item = new SaleItem();
        item.setSale(sale);
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setUnitPrice(product.getPrice());
        item.setTotalPrice(totalPrice);

        List<SaleItem> items = new ArrayList<>();
        items.add(item);
        sale.setItems(items);

        sale.setSubtotal(totalPrice);
        sale.setDiscount(BigDecimal.ZERO);
        sale.setTotalValue(totalPrice);

        return sale;
    }
}
