package com.helen.api_crm.seller.mapper;

import com.helen.api_crm.seller.dto.SellerRequestDTO;
import com.helen.api_crm.seller.dto.SellerResponseDTO;
import com.helen.api_crm.seller.model.Seller;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring")
public interface SellerMapper {

    SellerMapper INSTANCE = Mappers.getMapper(SellerMapper.class);

    // RequestDTO -> Entity
    Seller toEntity(SellerRequestDTO dto);

    //Entity -> ResponseDTO
    SellerResponseDTO toDTO(Seller seller);
}
