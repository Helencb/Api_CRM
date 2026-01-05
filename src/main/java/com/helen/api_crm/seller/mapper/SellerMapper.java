package com.helen.api_crm.seller.mapper;

import com.helen.api_crm.common.enums.Role;
import com.helen.api_crm.seller.dto.SellerRequestDTO;
import com.helen.api_crm.seller.dto.SellerResponseDTO;
import com.helen.api_crm.seller.model.Seller;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", imports = {Role.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SellerMapper {

    SellerResponseDTO toDTO(Seller seller);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", expression = "java(Role.SELLER)")
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "sales", ignore = true)
    Seller toEntity(SellerRequestDTO dto);
}
