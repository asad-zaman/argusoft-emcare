package com.argusoft.who.emcare.web.location.mapper;

import com.argusoft.who.emcare.web.location.dto.HierarchyMasterDto;
import com.argusoft.who.emcare.web.location.model.HierarchyMaster;

/**
 *
 * @author jay
 */
public class HierarchyMasterMapper {

    public static HierarchyMaster dtoToEntityForHierarchyMaster(HierarchyMasterDto hierarchyMasterDto) {

        HierarchyMaster hierarchyMaster = new HierarchyMaster();

        hierarchyMaster.setCode(hierarchyMasterDto.getCode());
        hierarchyMaster.setHierarchyType(hierarchyMasterDto.getHierarchyType());
        hierarchyMaster.setName(hierarchyMasterDto.getName());

        return hierarchyMaster;
    }

    public static HierarchyMasterDto entityToDtoForHierarchyMaster(HierarchyMaster hierarchyMaster) {

        HierarchyMasterDto hierarchyMasterDto = new HierarchyMasterDto();
        hierarchyMasterDto.setCode(hierarchyMaster.getCode());
        hierarchyMasterDto.setHierarchyType(hierarchyMaster.getHierarchyType());
        hierarchyMasterDto.setName(hierarchyMaster.getName());

        return hierarchyMasterDto;
    }
}
