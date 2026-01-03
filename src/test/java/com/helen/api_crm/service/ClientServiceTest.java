package com.helen.api_crm.service;

import com.helen.api_crm.clients.service.ClientService;
import com.helen.api_crm.clients.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
@RequiredArgsConstructor
public class ClientServiceTest {

    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientRepository clientRepository;

    @BeforeEach
    void setup() {
        clientRepository.deleteAll();
    }}