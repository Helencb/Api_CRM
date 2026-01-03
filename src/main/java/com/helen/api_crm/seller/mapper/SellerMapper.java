package com.helen.api_crm.seller.mapper;

import com.helen.api_crm.seller.dto.SellerRequestDTO;
import com.helen.api_crm.seller.dto.SellerResponseDTO;
import com.helen.api_crm.seller.model.Seller;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface SellerMapper {

    @Mapping(target = "user", ignore = true)
    Seller toEntity(SellerRequestDTO dto);

    @Mapping(target = "email", source = "user.email")
    SellerResponseDTO toDTO(Seller seller);
}
