package com.helen.api_crm.product.mapper;

import com.helen.api_crm.product.dto.ProductRequestDTO;
import com.helen.api_crm.product.dto.ProductResponseDTO;
import com.helen.api_crm.product.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductResponseDTO toDTO(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createAt", ignore = true)
    @Mapping(target = "updateAt", ignore = true)
    Product toEntity(ProductRequestDTO dto);
}
