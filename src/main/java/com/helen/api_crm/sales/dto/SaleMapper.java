package com.helen.api_crm.sales.dto;

import com.helen.api_crm.sales.model.Sale;

public class SaleMapper {

    public static SaleResponseDTO mapToSaleResponseDTO(Sale sale) {
        SaleResponseDTO responseDTO = new SaleResponseDTO();
        responseDTO.setId(sale.getId());
        responseDTO.setNome(sale.getNome());
        responseDTO.setClientId(sale.getClient().getId());
        responseDTO.setAmount(sale.getAmount());
        responseDTO.setDescription(sale.getDescription());
        responseDTO.setDate(sale.getDate().toString());
        return responseDTO;
    }
}
