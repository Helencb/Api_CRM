package com.helen.api_crm.manager.mapper;

import com.helen.api_crm.manager.dto.ManagerRequestDTO;
import com.helen.api_crm.manager.dto.ManagerResponseDTO;
import com.helen.api_crm.manager.model.Manager;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ManagerMapper {

    @Mapping(target = "user", ignore = true)
    Manager toEntity(ManagerRequestDTO dto);

    @Mapping(target = "email", source = "user.email")
    ManagerResponseDTO toDTO(Manager manager);
}
