package com.argusoft.who.emcare.web.location.service.impl;

import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.common.response.Response;
import com.argusoft.who.emcare.web.exception.EmCareException;
import com.argusoft.who.emcare.web.location.dao.HierarchyMasterDao;
import com.argusoft.who.emcare.web.location.dao.LocationMasterDao;
import com.argusoft.who.emcare.web.location.dto.HierarchyMasterDto;
import com.argusoft.who.emcare.web.location.dto.LocationMasterDto;
import com.argusoft.who.emcare.web.location.dto.LocationaListDto;
import com.argusoft.who.emcare.web.location.mapper.HierarchyMasterMapper;
import com.argusoft.who.emcare.web.location.mapper.LocationMasterMapper;
import com.argusoft.who.emcare.web.location.model.HierarchyMaster;
import com.argusoft.who.emcare.web.location.model.LocationMaster;
import com.argusoft.who.emcare.web.location.service.LocationService;
import com.argusoft.who.emcare.web.secuirty.EmCareSecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author jay
 */
@Service
public class LocationServiceImpl implements LocationService {

    @Autowired
    HierarchyMasterDao hierarchyMasterDao;

    @Autowired
    LocationMasterDao locationMasterDao;

    @Autowired
    EmCareSecurityUser emCareSecurityUser;

    @Override
    public ResponseEntity<Object> createHierarchyMaster(HierarchyMasterDto hierarchyMasterDto) {
        return ResponseEntity.ok(hierarchyMasterDao.save(HierarchyMasterMapper.dtoToEntityForHierarchyMasterCreate(hierarchyMasterDto)));
    }

    @Override
    public ResponseEntity<Object> updateHierarchyMaster(HierarchyMasterDto hierarchyMasterDto) {
        return ResponseEntity.ok(hierarchyMasterDao.save(HierarchyMasterMapper.dtoToEntityForHierarchyMasterCreate(hierarchyMasterDto)));
    }

    @Override
    public void deleteHierarchyMaster(String id) {
        hierarchyMasterDao.deleteById(id);
    }

    @Override
    public ResponseEntity<Object> getAllHierarchyMaster() {
        List<HierarchyMaster> hierarchyMasterList = hierarchyMasterDao.findAll();
        return ResponseEntity.ok(hierarchyMasterList);
    }

    @Override
    public ResponseEntity<Object> getHierarchyMasterById(String type) {
        Optional<HierarchyMaster> optionalHierarchyMaster = hierarchyMasterDao.findById(type);
        if (!optionalHierarchyMaster.isEmpty()) {
            return ResponseEntity.ok(optionalHierarchyMaster.get());
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new Response(CommonConstant.EM_CARE_NO_DATA_FOUND, HttpStatus.NO_CONTENT.value()));
        }
    }

    @Override
    public ResponseEntity<Object> createOrUpdate(LocationMasterDto locationMasterDto) {
        List<LocationMaster> locations = locationMasterDao.findAll();
        if (locations.isEmpty()) {
            return ResponseEntity.ok(locationMasterDao.save(LocationMasterMapper.firstEntity(locationMasterDto)));
        }
        LocationMaster locationMaster = LocationMasterMapper.dtoToEntityForLocationMasterCreate(locationMasterDto);
        LocationMaster lm = locationMasterDao.save(locationMaster);
        return ResponseEntity.ok(lm);
    }

    @Override
    public ResponseEntity<Object> getAllLocation() {
        List<LocationMaster> locationMasters = locationMasterDao.findAll();
        List<LocationaListDto> locationaListDtos = new ArrayList<>();
        for (LocationMaster locationMaster : locationMasters) {
            if (locationMaster.getParent() == null || locationMaster.getParent() == 0) {
                locationaListDtos.add(LocationMasterMapper.entityToLocationList(locationMaster, "NA"));
            } else {
                locationaListDtos.add(LocationMasterMapper.entityToLocationList(locationMaster, locationMasterDao.findById(locationMaster.getParent().intValue()).get().getName()));
            }
        }
        return ResponseEntity.ok(locationaListDtos);
    }

    @Override
    public ResponseEntity<Object> getLocationPage(Integer pageNo, String orderBy, String order, String searchString) {
        if (orderBy.equalsIgnoreCase("null")) {
            orderBy = "name";
        }
        Long totalCount;
        Sort sort = order.equalsIgnoreCase(CommonConstant.DESC) ? Sort.by(orderBy).descending() : Sort.by(orderBy).ascending();
        Pageable page;
        if (!sort.isEmpty()) {
            page = PageRequest.of(pageNo, CommonConstant.PAGE_SIZE, sort);
        } else {
            page = PageRequest.of(pageNo, CommonConstant.PAGE_SIZE);
        }
        Page<LocationMaster> locationMasters;
        if (searchString != null && !searchString.isEmpty()) {
            totalCount = Long.valueOf(locationMasterDao.findByNameContainingIgnoreCaseOrTypeContainingIgnoreCase(searchString, searchString).size());
            locationMasters = locationMasterDao.findByNameContainingIgnoreCaseOrTypeContainingIgnoreCase(searchString, searchString, page);
        } else {
            totalCount = Long.valueOf(locationMasterDao.findAll().size());
            locationMasters = locationMasterDao.findAll(page);
        }
        List<LocationaListDto> locationList = new ArrayList<>();
        for (LocationMaster locationMaster : locationMasters) {
            if (locationMaster.getParent() == null || locationMaster.getParent() == 0) {
                locationList.add(LocationMasterMapper.entityToLocationList(locationMaster, "NA"));
            } else {
                locationList.add(LocationMasterMapper.entityToLocationList(locationMaster, locationMasterDao.findById(locationMaster.getParent().intValue()).get().getName()));
            }
        }
        PageDto pageDto = new PageDto();
        pageDto.setList(locationList);
        pageDto.setTotalCount(totalCount);
        return ResponseEntity.ok(pageDto);
    }

    @Override
    public ResponseEntity<Object> getLocationByLocationFilter(Integer pageNo, Integer locationId) {
        List<Integer> childIds = locationMasterDao.getAllChildLocationId(locationId);
        Integer offset = (pageNo - 1) * CommonConstant.PAGE_SIZE;
        Integer limit = CommonConstant.PAGE_SIZE;
        List<LocationMaster> locationMasters = locationMasterDao.getLocationByLocationIds(childIds, limit, offset);
        Long totalCount = locationMasterDao.getLocationByLocationIdsCount(childIds);

        List<LocationaListDto> locationList = new ArrayList<>();
        for (LocationMaster locationMaster : locationMasters) {
            if (locationMaster.getParent() == null || locationMaster.getParent() == 0) {
                locationList.add(LocationMasterMapper.entityToLocationList(locationMaster, "NA"));
            } else {
                locationList.add(LocationMasterMapper.entityToLocationList(locationMaster, locationMasterDao.findById(locationMaster.getParent().intValue()).get().getName()));
            }
        }

        PageDto pageDto = new PageDto();
        pageDto.setList(locationList);
        pageDto.setTotalCount(totalCount);
        return ResponseEntity.ok(pageDto);

    }

    @Override
    public ResponseEntity<Object> updateLocation(LocationMasterDto locationMasterDto) {
        LocationMaster locationMaster = LocationMasterMapper.dtoToEntityForLocationMasterUpdate(locationMasterDto);
        LocationMaster updatedLocation = locationMasterDao.save(locationMaster);
        return ResponseEntity.ok(updatedLocation);
    }

    @Override
    public ResponseEntity<Object> deleteLocationById(Integer locationId) {
        List<LocationMaster> childLocations = locationMasterDao.getChildLocation(locationId);
        if (!childLocations.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This Location Have Child Location, You Can Not Delete");
        } else {
            locationMasterDao.deleteById(locationId);
        }
        return ResponseEntity.ok(null);
    }

    @Override
    public LocationMaster getLocationMasterById(Integer locationId) {
        Optional<LocationMaster> locationMaster = locationMasterDao.findById(locationId);
        if (!locationMaster.isEmpty()) {
            return locationMaster.get();
        } else {
            throw new EmCareException(CommonConstant.EM_CARE_NO_DATA_FOUND, HttpStatus.NO_CONTENT.value());
        }
    }

    @Override
    public List<LocationMaster> getLocationByType(String type) {
        List<LocationMaster> locations = locationMasterDao.findByType(type);
        Collections.sort(locations, Comparator.comparing(LocationMaster::getName));
        return locations;
    }

    @Override
    public List<LocationaListDto> getChildLocation(Integer locationId) {
        List<LocationMaster> locationMasters = locationMasterDao.findByParent(locationId.longValue());
        List<LocationaListDto> locationaListDtos = new ArrayList<>();
        for (LocationMaster locationMaster : locationMasters) {
            if (locationMaster.getParent() == 0 || locationMaster.getParent() == null) {
                locationaListDtos.add(LocationMasterMapper.entityToLocationList(locationMaster, "NA"));
            } else {
                locationaListDtos.add(LocationMasterMapper.entityToLocationList(locationMaster, locationMasterDao.findById(locationMaster.getParent().intValue()).get().getName()));
            }
        }
        return locationaListDtos;
    }

    @Override
    public List<LocationaListDto> getAllParent(Integer locationId) {
        List<LocationMaster> locationMasters = locationMasterDao.getAllParent(locationId);
        List<LocationaListDto> locationaListDtos = new ArrayList<>();
        for (LocationMaster locationMaster : locationMasters) {
            if (locationMaster.getParent() == 0 || locationMaster.getParent() == null) {
                locationaListDtos.add(LocationMasterMapper.entityToLocationList(locationMaster, "NA"));
            } else {
                locationaListDtos.add(LocationMasterMapper.entityToLocationList(locationMaster, locationMasterDao.findById(locationMaster.getParent().intValue()).get().getName()));
            }
        }
        return locationaListDtos;
    }

}
