package com.argusoft.who.emcare.web.location.mapper;

import com.argusoft.who.emcare.web.location.dto.HierarchyMasterDto;
import com.argusoft.who.emcare.web.location.model.HierarchyMaster;

/**
 * @author jay
 */
public class HierarchyMasterMapper {

    private HierarchyMasterMapper() {
    }

    public static HierarchyMaster dtoToEntityForHierarchyMasterCreate(HierarchyMasterDto hierarchyMasterDto) {

        HierarchyMaster hierarchyMaster = new HierarchyMaster();

        hierarchyMaster.setCode(hierarchyMasterDto.getCode());
        hierarchyMaster.setHierarchyType(hierarchyMasterDto.getHierarchyType());
        hierarchyMaster.setName(hierarchyMasterDto.getName());

        return hierarchyMaster;
    }
}
