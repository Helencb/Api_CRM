package com.helen.api_crm.seller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.helen.api_crm.security.SecurityConfig;
import com.helen.api_crm.security.jwt.JwtFilter;
import com.helen.api_crm.security.jwt.JwtService;
import com.helen.api_crm.security.service.AuthorizationService;
import com.helen.api_crm.seller.controller.SellerController;
import com.helen.api_crm.seller.dto.SellerRequestDTO;
import com.helen.api_crm.seller.dto.SellerResponseDTO;
import com.helen.api_crm.seller.service.SellerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SellerController.class)
@Import(SecurityConfig.class)
public class SellerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtFilter jwtFilter;

    @MockBean
    private AuthorizationService authorizationService;

    @MockBean
    private SellerService sellerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "admin", roles = {"MANAGER"})
    void shouldCreateSellerSuccessfully() throws Exception {

        SellerRequestDTO request = createRequest();
        SellerResponseDTO response = createResponse();

        when(sellerService.createSeller(any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/sellers")
                        .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("HelenSeller"))
                .andExpect(jsonPath("$.email").value("helen@crm.com"))
                .andExpect(jsonPath("$.password").value("12345"))
                .andExpect(jsonPath("$.phone").value("1234567890"));
    }

    private SellerRequestDTO createRequest() {
        return new SellerRequestDTO(
               "HelenSeller",
                "helen@crm.com",
                "12345",
                "1234567890"

        );
    }

    private SellerResponseDTO createResponse() {
        return new SellerResponseDTO(
                1L,
                "HelenSeller",
                "helen@crm.com",
                "1234567890"
        );
    }
}
