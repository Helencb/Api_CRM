package com.helen.api_crm.sale.mapper;

import com.helen.api_crm.clients.model.Client;
import com.helen.api_crm.sale.dto.SaleRequestDTO;
import com.helen.api_crm.sale.dto.SaleResponseDTO;
import com.helen.api_crm.sale.model.Sale;
import com.helen.api_crm.seller.model.Seller;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", imports = {LocalDateTime.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SaleMapper {

    // Converte DTO + Entidades carregadas do banco em uma nova Venda.
    @Mapping(target = "id", ignore = true) //
    @Mapping(target = "name", source = "dto.name")
    @Mapping(target = "amount", source = "dto.amount")
    @Mapping(target = "totalValue", source = "dto.totalValue")
    @Mapping(target = "description", source = "dto.description")
    @Mapping(target = "client", source = "client")
    @Mapping(target = "seller", source = "seller")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "failureReason", ignore = true)
    @Mapping(target = "status", ignore = true)
    Sale toEntity(SaleRequestDTO dto, Client client, Seller seller, LocalDateTime createdAt);


    // Converte a Venda para DTO de resposta.
    @Mapping(target = "clientId", source = "client.id")
    @Mapping(target = "sellerId", source = "seller.id")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "totalValue", source = "totalValue")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "failureReason", source = "failureReason")
    @Mapping(target = "createdAt", source = "createdAt")
    SaleResponseDTO toDTO(Sale sale);
}
