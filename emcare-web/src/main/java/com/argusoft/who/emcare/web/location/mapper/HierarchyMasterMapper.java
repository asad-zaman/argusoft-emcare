package com.argusoft.who.emcare.web.location.mapper;

import com.argusoft.who.emcare.web.location.dto.HierarchyMasterDto;
import com.argusoft.who.emcare.web.location.model.HierarchyMaster;
import java.util.Date;

/**
 *
 * @author jay
 */
public class HierarchyMasterMapper {

    public static HierarchyMaster dtoToEntityForHierarchyMasterCreate(HierarchyMasterDto hierarchyMasterDto, String userId) {

        HierarchyMaster hierarchyMaster = new HierarchyMaster();

        hierarchyMaster.setCode(hierarchyMasterDto.getCode());
        hierarchyMaster.setHierarchyType(hierarchyMasterDto.getHierarchyType());
        hierarchyMaster.setName(hierarchyMasterDto.getName());
        hierarchyMaster.setCreatedBy(userId);
        hierarchyMaster.setCreatedOn(new Date());

        return hierarchyMaster;
    }

    public static HierarchyMaster dtoToEntityForHierarchyMasterUpdate(HierarchyMasterDto hierarchyMasterDto, HierarchyMaster hm, String userId) {

        HierarchyMaster hierarchyMaster = new HierarchyMaster();

        hierarchyMaster.setCode(hierarchyMasterDto.getCode());
        hierarchyMaster.setHierarchyType(hierarchyMasterDto.getHierarchyType());
        hierarchyMaster.setName(hierarchyMasterDto.getName());
        hierarchyMaster.setCreatedBy(hm.getCreatedBy());
        hierarchyMaster.setCreatedOn(hm.getCreatedOn());
        hierarchyMaster.setModifiedBy(userId);
        hierarchyMaster.setModifiedOn(new Date());

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
