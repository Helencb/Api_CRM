package com.helen.api_crm.sales.mapper;

import com.helen.api_crm.clients.model.Client;
import com.helen.api_crm.sales.dto.SaleRequestDTO;
import com.helen.api_crm.sales.dto.SaleResponseDTO;
import com.helen.api_crm.sales.model.Sale;
import com.helen.api_crm.seller.model.Seller;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring")
public interface SaleMapper {

     SaleMapper INSTANCE = Mappers.getMapper(SaleMapper.class);

     Sale toEntiy(SaleRequestDTO dto, Client client, Seller seller);

     SaleResponseDTO toDTO(Sale sale);
}
