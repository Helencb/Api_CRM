package com.helen.api_crm.clients.mapper;

import com.helen.api_crm.clients.dto.ClientRequestDTO;
import com.helen.api_crm.clients.dto.ClientResponseDTO;
import com.helen.api_crm.clients.model.Client;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    ClientMapper INSTANCE = Mappers.getMapper(ClientMapper.class);

    Client toEntity(ClientRequestDTO dto);

    ClientResponseDTO toDTO(Client client);
}
