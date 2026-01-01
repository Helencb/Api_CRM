package com.helen.api_crm.seller.dto;

import jakarta.transaction.Transactional;
import lombok.*;

@Transactional
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SellerResponseDTO {

    private Long id;
    private String name;
    private String email;
    private String phone;

}
