package com.helen.api_crm.clients.mapper;

import com.helen.api_crm.clients.dto.ClientRequestDTO;
import com.helen.api_crm.clients.dto.ClientResponseDTO;
import com.helen.api_crm.clients.model.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    @Mapping(target = "id", ignore = true)      // ID é gerado pelo banco
    @Mapping(target = "sales", ignore = true)
    Client toEntity(ClientRequestDTO dto);

    ClientResponseDTO toDTO(Client client);
}
