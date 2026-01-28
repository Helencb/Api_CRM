package com.helen.api_crm.sale.mapper;

import com.helen.api_crm.clients.model.Client;
import com.helen.api_crm.sale.dto.SaleResponseDTO;
import com.helen.api_crm.sale.model.Sale;
import com.helen.api_crm.sale.model.SaleItem;
import com.helen.api_crm.seller.model.Seller;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface SaleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "subtotal", ignore = true)
    @Mapping(target = "discount", ignore = true)
    @Mapping(target = "totalValue", ignore = true)
    @Mapping(target = "paymentMethod", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "client", source = "client")
    @Mapping(target = "seller", source = "seller")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "failureReason", ignore = true)
    @Mapping(target = "status", ignore = true)
    Sale toEntity(Client client, Seller seller);

    @Mapping(target = "clientId", source = "client.id")
    @Mapping(target = "clientName", source = "client.name")
    @Mapping(target = "sellerId", source = "seller.id")
    @Mapping(target = "sellerName", source = "seller.name")
    @Mapping(target = "items", source = "items", qualifiedByName = "mapItems")
    SaleResponseDTO toDTO(Sale sale);

    @Named("mapItems")
    default List<SaleResponseDTO.SaleItemResponse> mapItems(List<SaleItem> items) {
        if (items == null) return List.of();
        return items.stream()
                .map(item -> new SaleResponseDTO.SaleItemResponse(
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getTotalPrice()))
                .collect(Collectors.toList());
    }
}