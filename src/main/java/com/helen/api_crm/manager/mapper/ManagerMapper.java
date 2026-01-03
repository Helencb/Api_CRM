package com.helen.api_crm.manager.mapper;

import com.helen.api_crm.manager.dto.ManagerRequestDTO;
import com.helen.api_crm.manager.dto.ManagerResponseDTO;
import com.helen.api_crm.manager.model.Manager;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ManagerMapper {
    Manager toEntity(ManagerRequestDTO dto);
    ManagerResponseDTO toDTO(Manager manager);
}
