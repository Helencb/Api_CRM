package com.helen.api_crm.manager.mapper;

import com.helen.api_crm.common.enums.Role;
import com.helen.api_crm.manager.dto.ManagerRequestDTO;
import com.helen.api_crm.manager.dto.ManagerResponseDTO;
import com.helen.api_crm.manager.model.Manager;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", imports = {Role.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ManagerMapper {

    ManagerResponseDTO toDTO(Manager manager);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", expression = "java(Role.MANAGER)")
    @Mapping(target = "active", constant = "true")
    Manager toEntity(ManagerRequestDTO dto);
}
