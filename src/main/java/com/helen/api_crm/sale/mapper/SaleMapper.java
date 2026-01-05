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

    /**
     * Converte DTO + Entidades carregadas do banco em uma nova Venda.
     * Resolvemos a ambiguidade definindo explicitamente a 'source'.
     */
    @Mapping(target = "id", ignore = true) // ID é gerado pelo banco
    @Mapping(target = "name", source = "dto.name") // Resolve ambiguidade (usa o nome do DTO, não do cliente/vendedor)
    @Mapping(target = "amount", source = "dto.amount")
    @Mapping(target = "totalValue", source = "dto.value")
    @Mapping(target = "description", source = "dto.description")

    // Mapeamento dos relacionamentos
    @Mapping(target = "client", source = "client")
    @Mapping(target = "seller", source = "seller")

    // Definição de campos de controle
    @Mapping(target = "date", expression = "java(LocalDateTime.now())") // Define data atual
    @Mapping(target = "completed", constant = "false") // Venda começa não concluída
    @Mapping(target = "failureReason", ignore = true)
    Sale toEntity(SaleRequestDTO dto, Client client, Seller seller);


    /**
     * Converte a Venda para DTO de resposta.
     * Navega pelos objetos aninhados para pegar os nomes.
     */
    @Mapping(target = "clientNome", source = "client.name")
    @Mapping(target = "sellerNome", source = "seller.name")
    SaleResponseDTO toDTO(Sale sale);
}
