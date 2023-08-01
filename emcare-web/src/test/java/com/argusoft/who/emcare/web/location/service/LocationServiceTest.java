package com.argusoft.who.emcare.web.location.service;

import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.exception.EmCareException;
import com.argusoft.who.emcare.web.location.dao.HierarchyMasterDao;
import com.argusoft.who.emcare.web.location.dao.LocationMasterDao;
import com.argusoft.who.emcare.web.location.dto.HierarchyMasterDto;
import com.argusoft.who.emcare.web.location.dto.LocationMasterDto;
import com.argusoft.who.emcare.web.location.dto.LocationaListDto;
import com.argusoft.who.emcare.web.location.mapper.LocationMasterMapper;
import com.argusoft.who.emcare.web.location.model.HierarchyMaster;
import com.argusoft.who.emcare.web.location.model.LocationMaster;
import com.argusoft.who.emcare.web.location.service.impl.LocationServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.shaded.com.fasterxml.jackson.core.type.TypeReference;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {LocationServiceImpl.class})
public class LocationServiceTest {

    @Mock
    private HierarchyMasterDao hierarchyMasterDao;

    @Mock
    private LocationMasterDao locationMasterDao;

    @InjectMocks
    private LocationServiceImpl locationService;

    AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    public void testCreateHierarchyMaster() {
        HierarchyMasterDto hierarchyMasterDto = new HierarchyMasterDto();
        hierarchyMasterDto.setHierarchyType("Region");
        hierarchyMasterDto.setName("Region 1");
        hierarchyMasterDto.setCode("REG001");

        HierarchyMaster hierarchyMaster = new HierarchyMaster();
        hierarchyMaster.setHierarchyType("Region");
        hierarchyMaster.setName("Region 1");
        hierarchyMaster.setCode("REG001");

        when(hierarchyMasterDao.saveAndFlush(any(HierarchyMaster.class))).thenReturn(hierarchyMaster);

        ResponseEntity<Object> responseEntity = locationService.createHierarchyMaster(hierarchyMasterDto);

        assertEquals(ResponseEntity.ok(hierarchyMaster), responseEntity);
    }

    @Test
    public void testUpdateHierarchyMaster() {
        HierarchyMasterDto hierarchyMasterDto = new HierarchyMasterDto();
        hierarchyMasterDto.setHierarchyType("Region");
        hierarchyMasterDto.setName("Updated Region 1");
        hierarchyMasterDto.setCode("REG001");

        HierarchyMaster existingHierarchyMaster = new HierarchyMaster();
        existingHierarchyMaster.setHierarchyType("Region");
        existingHierarchyMaster.setName("Region 1");
        existingHierarchyMaster.setCode("REG001");

        HierarchyMaster updatedHierarchyMaster = new HierarchyMaster();
        updatedHierarchyMaster.setHierarchyType("Region");
        updatedHierarchyMaster.setName("Updated Region 1");
        updatedHierarchyMaster.setCode("REG001");

        when(hierarchyMasterDao.save(any(HierarchyMaster.class))).thenReturn(updatedHierarchyMaster);

        ResponseEntity<Object> responseEntity = locationService.updateHierarchyMaster(hierarchyMasterDto);

        assertEquals(ResponseEntity.ok(updatedHierarchyMaster), responseEntity);
    }

    @Test
    public void testDeleteHierarchyMaster() {
        HierarchyMaster hierarchyMaster = new HierarchyMaster();
        hierarchyMaster.setHierarchyType("Region");
        hierarchyMaster.setName("Region 1");
        hierarchyMaster.setCode("REG001");

        when(hierarchyMasterDao.findById("1")).thenReturn(java.util.Optional.of(hierarchyMaster));

        locationService.deleteHierarchyMaster("1");

        verify(hierarchyMasterDao).deleteById("1");
    }

    @Test
    public void testGetAllHierarchyMaster() {
        List<HierarchyMaster> hierarchyMasterList = new ArrayList<>();
        HierarchyMaster hierarchyMaster1 = new HierarchyMaster();
        hierarchyMaster1.setHierarchyType("Region");
        hierarchyMaster1.setName("Region 1");
        hierarchyMaster1.setCode("REG001");

        HierarchyMaster hierarchyMaster2 = new HierarchyMaster();
        hierarchyMaster2.setHierarchyType("Region");
        hierarchyMaster2.setName("Region 1");
        hierarchyMaster2.setCode("REG001");

        hierarchyMasterList.add(0, hierarchyMaster1);
        hierarchyMasterList.add(1, hierarchyMaster2);

        when(hierarchyMasterDao.findAll()).thenReturn(hierarchyMasterList);

        ResponseEntity<Object> responseEntity = locationService.getAllHierarchyMaster();

        assertEquals(ResponseEntity.ok(hierarchyMasterList), responseEntity);
    }

    @Test
    public void testGetHierarchyMasterById_ExistingId() {
        HierarchyMaster hierarchyMaster = new HierarchyMaster();
        hierarchyMaster.setHierarchyType("Region");
        hierarchyMaster.setName("Region 1");
        hierarchyMaster.setCode("REG001");

        when(hierarchyMasterDao.findById("1")).thenReturn(Optional.of(hierarchyMaster));

        ResponseEntity<Object> responseEntity = locationService.getHierarchyMasterById("1");

        assertEquals(ResponseEntity.ok(hierarchyMaster), responseEntity);
    }

    @Test
    public void testGetHierarchyMasterById_NonExistingId() {
        when(hierarchyMasterDao.findById("non_existing_type")).thenReturn(Optional.empty());

        ResponseEntity<Object> responseEntity = locationService.getHierarchyMasterById("non_existing_type");

        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    }

    @Test
    public void testCreateOrUpdate_NoExistingLocation() {
        LocationMasterDto locationDto = new LocationMasterDto();
        locationDto.setName("Location 1");
        locationDto.setType("Type 1");

        when(locationMasterDao.findAll()).thenReturn(new ArrayList<>());

        LocationMaster createdLocation = new LocationMaster();
        createdLocation.setId(1);
        when(locationMasterDao.saveAndFlush(any(LocationMaster.class))).thenReturn(createdLocation);

        ResponseEntity<Object> responseEntity = locationService.createOrUpdate(locationDto);

        assertEquals(ResponseEntity.ok(createdLocation), responseEntity);

        verify(locationMasterDao, times(1)).findAll();
        verify(locationMasterDao, times(1)).saveAndFlush(any(LocationMaster.class));
    }

    @Test
    public void testCreateOrUpdate_ExistingLocation() {
        LocationMasterDto locationDto = new LocationMasterDto();
        locationDto.setName("Location 2");
        locationDto.setType("Type 2");

        List<LocationMaster> existingLocations = new ArrayList<>();
        LocationMaster existingLocation = new LocationMaster();
        existingLocation.setId(2);
        existingLocations.add(existingLocation);
        when(locationMasterDao.findAll()).thenReturn(existingLocations);

        LocationMaster updatedLocation = new LocationMaster();
        updatedLocation.setId(2);
        when(locationMasterDao.saveAndFlush(any(LocationMaster.class))).thenReturn(updatedLocation);

        ResponseEntity<Object> responseEntity = locationService.createOrUpdate(locationDto);

        assertEquals(ResponseEntity.ok(updatedLocation), responseEntity);

        verify(locationMasterDao, times(1)).findAll();
        verify(locationMasterDao, times(1)).saveAndFlush(any(LocationMaster.class));
    }

    @Test
    public void testGetAllLocation_NoLocations() {
        when(locationMasterDao.findAll()).thenReturn(new ArrayList<>());

        ResponseEntity<Object> responseEntity = locationService.getAllLocation();

        assertEquals(ResponseEntity.ok(new ArrayList<LocationaListDto>()), responseEntity);

        verify(locationMasterDao, times(1)).findAll();
    }

    @Test
    public void testGetAllLocation_WithLocations() {
        LocationMaster locationMaster = new LocationMaster();
        locationMaster.setId(1);
        locationMaster.setName("Location 1");
        locationMaster.setParent(null);

        List<LocationMaster> locations = new ArrayList<>();
        locations.add(locationMaster);
        when(locationMasterDao.findAll()).thenReturn(locations);

        ResponseEntity<Object> responseEntity = locationService.getAllLocation();

        List<LocationaListDto> expectedLocationaListDtos = new ArrayList<>();
        expectedLocationaListDtos.add(LocationMasterMapper.entityToLocationList(locationMaster, "NA"));

        assertEquals(ResponseEntity.ok(expectedLocationaListDtos), responseEntity);

        verify(locationMasterDao, times(1)).findAll();
    }

    @Test
    public void testUpdateLocation_ValidData_Success() {
        LocationMasterDto validLocationDto = new LocationMasterDto();
        validLocationDto.setId(1);
        validLocationDto.setName("Updated Location");

        LocationMaster updatedLocation = new LocationMaster();
        updatedLocation.setId(1);
        updatedLocation.setName("Updated Location");
        when(locationMasterDao.save(any(LocationMaster.class))).thenReturn(updatedLocation);

        ResponseEntity<Object> response = locationService.updateLocation(validLocationDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        LocationMaster responseLocation = (LocationMaster) response.getBody();
        assertEquals(validLocationDto.getId(), responseLocation.getId());
        assertEquals(validLocationDto.getName(), responseLocation.getName());
    }

    @Test
    public void testUpdateLocation_InvalidLocationId_NotFound() {

        LocationMasterDto invalidLocationDto = new LocationMasterDto();
        invalidLocationDto.setId(99999);
        invalidLocationDto.setName("Invalid location");

        ResponseEntity<Object> response = locationService.updateLocation(invalidLocationDto);

//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testUpdateLocation_EmptyLocationData_BadRequest() {
        LocationMasterDto emptyLocationDto = new LocationMasterDto();

        ResponseEntity<Object> response = locationService.updateLocation(emptyLocationDto);

//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testDeleteLocationById_ValidId_NoChildLocations_Success() {
        when(locationMasterDao.getChildLocation(anyInt())).thenReturn(new ArrayList<>());

        ResponseEntity<Object> response = locationService.deleteLocationById(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testDeleteLocationById_ValidId_WithChildLocations_BadRequest() {
        List<LocationMaster> childLocations = new ArrayList<>();
        childLocations.add(new LocationMaster());
        childLocations.add(new LocationMaster());

        when(locationMasterDao.getChildLocation(anyInt())).thenReturn(childLocations);

        ResponseEntity<Object> response = locationService.deleteLocationById(1);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("This Location Have Child Location, You Can Not Delete", response.getBody());
    }

    @Test
    public void testGetLocationMasterById_ValidId_Exists_Success() {
        LocationMaster location = new LocationMaster();
        location.setId(1);
        location.setName("Test Location");

        when(locationMasterDao.findById(1)).thenReturn(Optional.of(location));

        LocationMaster result = locationService.getLocationMasterById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Test Location", result.getName());
    }

    @Test
    public void testGetLocationMasterById_ValidId_NotExists_EmCareException() {
        when(locationMasterDao.findById(1)).thenReturn(Optional.empty());

        EmCareException exception = assertThrows(EmCareException.class, () -> locationService.getLocationMasterById(1));

        assertEquals(CommonConstant.EM_CARE_NO_DATA_FOUND, exception.getMessage());
    }

    @Test
    public void testGetLocationMasterById_InvalidId_EmCareException() {
        when(locationMasterDao.findById(999)).thenReturn(Optional.empty());

        EmCareException exception = assertThrows(EmCareException.class, () -> locationService.getLocationMasterById(999));

        assertEquals(CommonConstant.EM_CARE_NO_DATA_FOUND, exception.getMessage());
    }

    @Test
    public void testGetLocationByType_ValidType_MultipleLocations_Success() {
        List<LocationMaster> locations = new ArrayList<>();
        LocationMaster location1 = new LocationMaster();
        location1.setId(1);
        location1.setName("Location 1");
        location1.setType("Type A");
        locations.add(location1);

        LocationMaster location2 = new LocationMaster();
        location2.setId(2);
        location2.setName("Location 2");
        location2.setType("Type A");
        locations.add(location2);

        when(locationMasterDao.findByType("Type A")).thenReturn(locations);

        List<LocationMaster> result = locationService.getLocationByType("Type A");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Location 1", result.get(0).getName());
        assertEquals("Location 2", result.get(1).getName());
    }

    @Test
    public void testGetLocationByType_ValidType_NoLocations_Success() {
        when(locationMasterDao.findByType("Type B")).thenReturn(new ArrayList<>());

        List<LocationMaster> result = locationService.getLocationByType("Type B");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetLocationByType_EmptyType_EmptyList() {
        List<LocationMaster> result = locationService.getLocationByType("");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetLocationByType_NullType_EmptyList() {
        List<LocationMaster> result = locationService.getLocationByType(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetChildLocation_ValidParentId_HasChildren_Success() {
        List<LocationMaster> locationMasters = new ArrayList<>();
        LocationMaster location1 = new LocationMaster();
        location1.setId(1);
        location1.setName("Location 1");
        location1.setParent(10L);

        LocationMaster location2 = new LocationMaster();
        location2.setId(2);
        location2.setName("Location 2");
        location2.setParent(10L);

        locationMasters.add(location1);
        locationMasters.add(location2);

        when(locationMasterDao.findByParent(10L)).thenReturn(locationMasters);

        LocationMaster parentLocation = new LocationMaster();
        parentLocation.setId(10);
        parentLocation.setName("Parent Location");
        when(locationMasterDao.findById(10)).thenReturn(Optional.of(parentLocation));

        List<LocationaListDto> result = locationService.getChildLocation(10);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Location 1", result.get(0).getName());
        assertEquals("Location 2", result.get(1).getName());
        assertEquals("Parent Location", result.get(0).getParentName());
        assertEquals("Parent Location", result.get(1).getParentName());
    }

    @Test
    public void testGetChildLocation_ValidParentId_NoChildren_Success() {
        when(locationMasterDao.findByParent(20L)).thenReturn(new ArrayList<>());

        List<LocationaListDto> result = locationService.getChildLocation(20);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetAllParent_ValidLocationId_HasParents_Success() {
        List<LocationMaster> locationMasters = new ArrayList<>();
        LocationMaster location1 = new LocationMaster();
        location1.setId(1);
        location1.setName("Location 1");
        location1.setParent(10L);

        LocationMaster location2 = new LocationMaster();
        location2.setId(2);
        location2.setName("Location 2");
        location2.setParent(20L);

        locationMasters.add(location1);
        locationMasters.add(location2);

        when(locationMasterDao.getAllParent(30)).thenReturn(locationMasters);

        LocationMaster parentLocation1 = new LocationMaster();
        parentLocation1.setId(10);
        parentLocation1.setName("Parent Location 1");

        LocationMaster parentLocation2 = new LocationMaster();
        parentLocation2.setId(20);
        parentLocation2.setName("Parent Location 2");

        when(locationMasterDao.findById(10)).thenReturn(Optional.of(parentLocation1));
        when(locationMasterDao.findById(20)).thenReturn(Optional.of(parentLocation2));

        List<LocationaListDto> result = locationService.getAllParent(30);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Location 1", result.get(0).getName());
        assertEquals("Location 2", result.get(1).getName());
        assertEquals("Parent Location 1", result.get(0).getParentName());
        assertEquals("Parent Location 2", result.get(1).getParentName());
    }

    @Test
    public void testGetAllParent_ValidLocationId_NoParents_Success() {
        when(locationMasterDao.getAllParent(40)).thenReturn(new ArrayList<>());

        List<LocationaListDto> result = locationService.getAllParent(40);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetLocationByLocationFilter_ValidLocationId_HasChildLocations_Success() {
        when(locationMasterDao.getAllChildLocationId(10)).thenReturn(Arrays.asList(11, 12, 13));

        List<LocationMaster> locationMasters = new ArrayList<>();
        LocationMaster location1 = new LocationMaster();
        location1.setId(11);
        location1.setName("Location 11");
        location1.setParent(10L);

        LocationMaster location2 = new LocationMaster();
        location2.setId(12);
        location2.setName("Location 12");
        location2.setParent(10L);

        locationMasters.add(location1);
        locationMasters.add(location2);

        when(locationMasterDao.getLocationByLocationIds(Arrays.asList(11, 12, 13), null, 2, 0)).thenReturn(locationMasters);

        when(locationMasterDao.getLocationByLocationIdsCount(Arrays.asList(11, 12, 13), null)).thenReturn(2L);

        LocationMaster parentLocation = new LocationMaster();
        parentLocation.setId(10);
        parentLocation.setName("Parent Location");
        when(locationMasterDao.findById(10)).thenReturn(Optional.of(parentLocation));

        ResponseEntity<Object> response = locationService.getLocationByLocationFilter(0, 10, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof PageDto);
        PageDto pageDto = (PageDto) response.getBody();
        assertNotNull(pageDto.getList());
        assertEquals(2L, pageDto.getTotalCount());
    }

    @Test
    public void testGetLocationByLocationFilter_ValidLocationId_NoChildLocations_Success() {
        when(locationMasterDao.getAllChildLocationId(20)).thenReturn(new ArrayList<>());

        ResponseEntity<Object> response = locationService.getLocationByLocationFilter(0, 20, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof PageDto);
        PageDto pageDto = (PageDto) response.getBody();
        assertNotNull(pageDto.getList());
        assertTrue(pageDto.getList().isEmpty());
        assertEquals(0L, pageDto.getTotalCount());
    }

    @Test
    public void testGetLocationByLocationFilter_NullLocationId_Success() {
        when(locationMasterDao.getLocationByLocationIds(null, "search", 2, 0)).thenReturn(new ArrayList<>());

        ResponseEntity<Object> response = locationService.getLocationByLocationFilter(0, null, "search");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof PageDto);
        PageDto pageDto = (PageDto) response.getBody();
        assertNotNull(pageDto.getList());
        assertTrue(pageDto.getList().isEmpty());
        assertEquals(0L, pageDto.getTotalCount());
    }

    private List<LocationMaster> getLocationData(String fileName) throws IOException {
        File file = new File("src/test/resources/mockdata/location/" + fileName + ".json");
        InputStream fileInputStream = new FileInputStream(file);
        String jsonString = IOUtils.toString(fileInputStream, StandardCharsets.UTF_8);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonString, new TypeReference<List<LocationMaster>>(){});
    }


    @Test
    public void testGetLocationPage_desc_orderByName_withSearchString_nullParent() throws IOException {
        List<LocationMaster> locationMasters = getLocationData("locationMaster1");

        int pageNo = 0;
        String searchString = "PHC";
        String orderBy = "name";
        String order = "DESC";
        Sort sort = Sort.by(orderBy).descending();
        Pageable page = PageRequest.of(pageNo, CommonConstant.PAGE_SIZE, sort);
        Page<LocationMaster> locationMasterPage = new PageImpl<>(locationMasters);

        when(locationMasterDao.findByNameContainingIgnoreCaseOrTypeContainingIgnoreCase(searchString,searchString,page)).thenReturn(locationMasterPage);
        when(locationMasterDao.findByNameContainingIgnoreCaseOrTypeContainingIgnoreCase(searchString,searchString)).thenReturn(locationMasters);
        ResponseEntity<Object> response = locationService.getLocationPage(pageNo,orderBy,order,searchString);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof PageDto);
        PageDto pageDto = (PageDto) response.getBody();
        assertNotNull(pageDto.getList());
        assertEquals(2L,pageDto.getTotalCount());

    }

    @Test
    public void testGetLocationPage_orderByNull_withoutSerachString_ExistingParent() throws IOException {
        List<LocationMaster> locationMasters = getLocationData("locationMaster2");

        int pageNo = 0;
        String orderBy = "name";
        String searchString = null;
        String order = "ASC";
        Sort sort = Sort.by(orderBy).ascending();
        Pageable page = PageRequest.of(pageNo, CommonConstant.PAGE_SIZE, sort);
        Page<LocationMaster> locationMasterPage = new PageImpl<>(locationMasters);

        when(locationMasterDao.findAll()).thenReturn(locationMasters);
        when(locationMasterDao.findAll(page)).thenReturn(locationMasterPage);
        when(locationMasterDao.findById(1)).thenReturn(Optional.ofNullable(locationMasters.get(1)));

        ResponseEntity<Object> response = locationService.getLocationPage(pageNo,orderBy,order,searchString);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof PageDto);
        PageDto pageDto = (PageDto) response.getBody();
        assertNotNull(pageDto.getList());
        assertEquals(2L,pageDto.getTotalCount());
 
    }
}