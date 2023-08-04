package com.argusoft.who.emcare.web.location.controller;

import com.argusoft.who.emcare.web.location.dto.HierarchyMasterDto;
import com.argusoft.who.emcare.web.location.dto.LocationMasterDto;
import com.argusoft.who.emcare.web.location.dto.LocationaListDto;
import com.argusoft.who.emcare.web.location.model.LocationMaster;
import com.argusoft.who.emcare.web.location.service.LocationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = LocationController.class)
@RunWith(MockitoJUnitRunner.class)
class LocationControllerTest {

    @Mock
    LocationService locationService;

    @InjectMocks
    private LocationController locationController;

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
    public void testCreateHierarchyMaster_Success() {
        HierarchyMasterDto hierarchyMasterDto = new HierarchyMasterDto();

        when(locationService.createHierarchyMaster(any(HierarchyMasterDto.class)))
                .thenReturn(new ResponseEntity<>("Hierarchy created successfully!", HttpStatus.CREATED));

        ResponseEntity<Object> response = locationController.createHierarchyMaster(hierarchyMasterDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Hierarchy created successfully!", response.getBody());
        verify(locationService, times(1)).createHierarchyMaster(hierarchyMasterDto);
    }

    @Test
    public void testCreateHierarchyMaster_Failure() {
        HierarchyMasterDto hierarchyMasterDto = new HierarchyMasterDto();

        when(locationService.createHierarchyMaster(any(HierarchyMasterDto.class)))
                .thenReturn(new ResponseEntity<>("Error creating hierarchy.", HttpStatus.BAD_REQUEST));

        ResponseEntity<Object> response = locationController.createHierarchyMaster(hierarchyMasterDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error creating hierarchy.", response.getBody());
        verify(locationService, times(1)).createHierarchyMaster(hierarchyMasterDto);
    }

    @Test
    public void testUpdateHierarchyMaster_Success(){
        HierarchyMasterDto hierarchyMasterDto = new HierarchyMasterDto();

        when(locationService.updateHierarchyMaster(any(HierarchyMasterDto.class))).thenReturn(new ResponseEntity<>("Hierarchy updated successfully!",HttpStatus.OK));

        ResponseEntity<Object> response = locationController.updateHierarchyMaster(hierarchyMasterDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Hierarchy updated successfully!", response.getBody());
        verify(locationService, times(1)).updateHierarchyMaster(hierarchyMasterDto);
}

    @Test
    void testUpdateHierarchyMaster_Failure() {
        HierarchyMasterDto hierarchyMasterDto = new HierarchyMasterDto();

        when(locationService.updateHierarchyMaster(any(HierarchyMasterDto.class)))
                .thenReturn(new ResponseEntity<>("Error updating hierarchy.", HttpStatus.BAD_REQUEST));

        ResponseEntity<Object> response = locationController.updateHierarchyMaster(hierarchyMasterDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error updating hierarchy.", response.getBody());
        verify(locationService, times(1)).updateHierarchyMaster(hierarchyMasterDto);
    }

    @Test
    public void testDeleteHierarchyMaster_Success() {
        String validHierarchyId = "1";

        locationController.deleteHierarchyMaster(validHierarchyId);

        verify(locationService, times(1)).deleteHierarchyMaster(validHierarchyId);
    }

    @Test
    public void testRetrieveHierarchyMasterById_ValidType() {
        String validType = "employee";

        when(locationService.getHierarchyMasterById(validType)).thenReturn(new ResponseEntity<>("Success", HttpStatus.OK));

        ResponseEntity<Object> responseEntity = locationController.retriveHierarchyMasterById(validType);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

    @Test
    public void testRetrieveHierarchyMasterById_InvalidType() {
        String invalidType = "department";

        when(locationService.getHierarchyMasterById(invalidType)).thenReturn(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));

        ResponseEntity<Object> responseEntity = locationController.retriveHierarchyMasterById(invalidType);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    public void testRetrieveHierarchyMasterById_EmptyType() {
        String emptyType = "";

        ResponseEntity<Object> responseEntity = locationController.retriveHierarchyMasterById(emptyType);

        assertNull(responseEntity);
    }

    @Test
    public void testRetrieveAllHierarchyMaster_Success() {

        List<HierarchyMasterDto> hierarchyMasterList = new ArrayList<>();
        HierarchyMasterDto hierarchyMasterDto1 = new HierarchyMasterDto();
        hierarchyMasterDto1.setHierarchyType("Type1");
        hierarchyMasterDto1.setCode("1");
        hierarchyMasterDto1.setName("Name1");
        hierarchyMasterList.add(hierarchyMasterDto1);
        when(locationService.getAllHierarchyMaster()).thenReturn(new ResponseEntity<>(hierarchyMasterList, HttpStatus.OK));

        ResponseEntity<Object> responseEntity = locationController.retriveAllHierarchyMaster();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

    @Test
    public void testRetrieveAllHierarchyMaster_EmptyResult() {

        List<HierarchyMasterDto> emptyHierarchyMasterList = new ArrayList<>();
        when(locationService.getAllHierarchyMaster()).thenReturn(new ResponseEntity<>(emptyHierarchyMasterList, HttpStatus.OK));

        ResponseEntity<Object> responseEntity = locationController.retriveAllHierarchyMaster();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertTrue(((List<?>) responseEntity.getBody()).isEmpty());
    }

    @Test
    public void testRetrieveAllHierarchyMaster_Error() {

        when(locationService.getAllHierarchyMaster()).thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));

        ResponseEntity<Object> responseEntity = locationController.retriveAllHierarchyMaster();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    public void testCreateOrUpdate_CreateNewLocation() {
        LocationMasterDto locationDtoToCreate = new LocationMasterDto();
        locationDtoToCreate.setName("New Location");


        when(locationService.createOrUpdate(locationDtoToCreate)).thenReturn(new ResponseEntity<>("Success", HttpStatus.CREATED));

        ResponseEntity<Object> responseEntity = locationController.createOrUpdate(locationDtoToCreate);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

    @Test
    public void testCreateOrUpdate_UpdateExistingLocation() {
        LocationMasterDto existingLocationDto = new LocationMasterDto();
        existingLocationDto.setId(1);
        existingLocationDto.setName("Updated Location");


        when(locationService.createOrUpdate(existingLocationDto)).thenReturn(new ResponseEntity<>("Success", HttpStatus.OK));

        ResponseEntity<Object> responseEntity = locationController.createOrUpdate(existingLocationDto);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

    @Test
    public void testCreateOrUpdate_InvalidInput() {
        LocationMasterDto invalidLocationDto = new LocationMasterDto();

        ResponseEntity<Object> responseEntity = locationController.createOrUpdate(invalidLocationDto);

        assertNull(responseEntity);
    }

    @Test
    public void testGetAllLocation_Success() {

        List<LocationMaster> locationList = new ArrayList<>();
        LocationMaster locationMaster = new LocationMaster();
        locationMaster.setId(1);
        locationMaster.setName("location name");
        locationMaster.setParent(0L);
        locationMaster.setType("Type 1");
        locationList.add(locationMaster);
        when(locationService.getAllLocation()).thenReturn(new ResponseEntity<>(locationList, HttpStatus.OK));

        ResponseEntity<Object> responseEntity = locationController.getAllLocation();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

    @Test
    public void testGetAllLocation_EmptyResult() {
        List<LocationMaster> emptyLocationList = new ArrayList<>();
        when(locationService.getAllLocation()).thenReturn(new ResponseEntity<>(emptyLocationList, HttpStatus.OK));

        ResponseEntity<Object> responseEntity = locationController.getAllLocation();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertTrue(((List<?>) responseEntity.getBody()).isEmpty());
    }

    @Test
    public void testGetAllLocation_Error() {
        when(locationService.getAllLocation()).thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));

        ResponseEntity<Object> responseEntity = locationController.getAllLocation();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    public void testGetLocationPage_Success() {
        int pageNo = 1;
        String orderBy = "name";
        String order = "asc";
        String searchString = "Location";
        List<LocationMaster> locationPage = new ArrayList<>();

        LocationMaster locationMaster = new LocationMaster();
        locationMaster.setId(1);
        locationMaster.setName("location name");
        locationMaster.setParent(0L);
        locationMaster.setType("Type 1");
        locationPage.add(locationMaster);
        when(locationService.getLocationPage(pageNo, orderBy, order, searchString)).thenReturn(new ResponseEntity<>(locationPage, HttpStatus.OK));

        ResponseEntity<Object> responseEntity = locationController.getLocationPage(pageNo, orderBy, order, searchString);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

    @Test
    public void testGetLocationPage_DefaultOrderBy() {
        int pageNo = 1;
        String order = "asc";
        String searchString = "Location";

        List<LocationMaster> locationPage = new ArrayList<>();
        LocationMaster locationMaster = new LocationMaster();
        locationMaster.setId(1);
        locationMaster.setName("location name");
        locationMaster.setParent(0L);
        locationMaster.setType("Type 1");
        locationPage.add(locationMaster);
        when(locationService.getLocationPage(pageNo, "name", order, searchString)).thenReturn(new ResponseEntity<>(locationPage, HttpStatus.OK));

        ResponseEntity<Object> responseEntity = locationController.getLocationPage(pageNo, "name", order, searchString);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

    @Test
    public void testGetLocationPage_Error() {
        int pageNo = 1;
        String orderBy = "name";
        String order = "asc";
        String searchString = "Location";

        when(locationService.getLocationPage(pageNo, orderBy, order, searchString)).thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));

        ResponseEntity<Object> responseEntity = locationController.getLocationPage(pageNo, orderBy, order, searchString);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    public void testGetLocationPageByLocationFilter_Success() {
        int pageNo = 1;
        int locationId = 1;
        String searchString = "Location";

        List<LocationMaster> locationPage = new ArrayList<>();
        LocationMaster locationMaster = new LocationMaster();
        locationMaster.setId(1);
        locationMaster.setName("location name");
        locationMaster.setParent(0L);
        locationMaster.setType("Type 1");
        locationPage.add(locationMaster);
        when(locationService.getLocationByLocationFilter(pageNo, locationId, searchString)).thenReturn(new ResponseEntity<>(locationPage, HttpStatus.OK));

        ResponseEntity<Object> responseEntity = locationController.getLocationPageByLocationFilter(pageNo, locationId, searchString);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

    @Test
    public void testGetLocationPageByLocationFilter_NullLocationId() {
        int pageNo = 1;
        String searchString = "Location";

        List<LocationMaster> locationPage = new ArrayList<>();
        LocationMaster locationMaster = new LocationMaster();
        locationMaster.setId(1);
        locationMaster.setName("location name");
        locationMaster.setParent(0L);
        locationMaster.setType("Type 1");
        locationPage.add(locationMaster);
        when(locationService.getLocationByLocationFilter(pageNo, null, searchString)).thenReturn(new ResponseEntity<>(locationPage, HttpStatus.OK));

        ResponseEntity<Object> responseEntity = locationController.getLocationPageByLocationFilter(pageNo, null, searchString);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

    @Test
    public void testGetLocationPageByLocationFilter_Error() {
        int pageNo = 1;
        int locationId = 1;
        String searchString = "Location";

        when(locationService.getLocationByLocationFilter(pageNo, locationId, searchString)).thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));

        ResponseEntity<Object> responseEntity = locationController.getLocationPageByLocationFilter(pageNo, locationId, searchString);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }



    @Test
    public void testUpdateExistingLocation() {
        LocationMasterDto existingLocationDto = new LocationMasterDto();
        existingLocationDto.setId(1);
        existingLocationDto.setName("Updated Location");


        when(locationService.updateLocation(existingLocationDto)).thenReturn(new ResponseEntity<>("Success", HttpStatus.OK));

        ResponseEntity<Object> responseEntity = locationController.updateLocation(existingLocationDto);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

    @Test
    public void testUpdateInvalidInput() {
        LocationMasterDto invalidLocationDto = new LocationMasterDto();

        ResponseEntity<Object> responseEntity = locationController.updateLocation(invalidLocationDto);

        assertNull(responseEntity);
    }

    @Test
    public void testDeleteExistingLocation() {
        LocationMasterDto existingLocationDto = new LocationMasterDto();
        existingLocationDto.setId(1);
        existingLocationDto.setName("Updated Location");


        when(locationService.deleteLocationById(existingLocationDto.getId())).thenReturn(new ResponseEntity<>("Success", HttpStatus.OK));

        ResponseEntity<Object> responseEntity = locationController.deleteLocation(existingLocationDto.getId());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

    @Test
    public void testDeleteInvalidInput() {
        LocationMasterDto invalidLocationDto = new LocationMasterDto();

        ResponseEntity<Object> responseEntity = locationController.deleteLocation(invalidLocationDto.getId());

        assertNull(responseEntity);
    }

    @Test
    public void testGetLocationById_ValidLocationId() {
        int validLocationId = 1;

        LocationMaster locationMaster = new LocationMaster();
        locationMaster.setId(1);
        locationMaster.setName("Location A");
        when(locationService.getLocationMasterById(validLocationId)).thenReturn(locationMaster);

        LocationMaster response = locationController.getLocationById(validLocationId);

        assertNotNull(response);
        assertEquals(validLocationId, response.getId());
        assertEquals("Location A", response.getName());
    }

    @Test
    public void testGetLocationById_InvalidLocationId() {
        int invalidLocationId = 100;

        when(locationService.getLocationMasterById(invalidLocationId)).thenReturn(null);

        LocationMaster response = locationController.getLocationById(invalidLocationId);

        assertNull(response);
    }

    @Test
    public void testGetLocationByType_ValidType() {
        String validLocationType = "department";

        List<LocationMaster> locationPage = new ArrayList<>();
        LocationMaster locationMaster = new LocationMaster();
        locationMaster.setId(1);
        locationMaster.setName("location name");
        locationMaster.setParent(0L);
        locationMaster.setType("department");
        locationPage.add(locationMaster);
        when(locationService.getLocationByType(validLocationType)).thenReturn(locationPage);

        List<LocationMaster> response = locationController.getLocationByType(validLocationType);

        assertNotNull(response);
        assertFalse(response.isEmpty());
    }

    @Test
    public void testGetLocationByType_InvalidType() {
        String invalidLocationType = "invalid_type"; // Replace with an invalid location type that does not exist in your system

        List<LocationMaster> emptyList = new ArrayList<>();
        when(locationService.getLocationByType(invalidLocationType)).thenReturn(emptyList);

        List<LocationMaster> response = locationController.getLocationByType(invalidLocationType);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    public void testGetLocationByType_EmptyType() {
        String emptyLocationType = "";

        List<LocationMaster> response = locationController.getLocationByType(emptyLocationType);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    public void testGetChildLocation_ValidLocationId() {
        int validLocationId = 1;

        List<LocationaListDto> childLocationList = new ArrayList<>();

        LocationaListDto locationaListDto = new LocationaListDto();
        locationaListDto.setId(1);
        locationaListDto.setParent(1L);
        locationaListDto.setName("Name 1");
        locationaListDto.setType("Type 1");
        locationaListDto.setActive(true);
        locationaListDto.setParentName("Parent 1");
        childLocationList.add(locationaListDto);

        when(locationService.getChildLocation(validLocationId)).thenReturn(childLocationList);

        List<LocationaListDto> response = locationController.getChildLocation(validLocationId);

        assertNotNull(response);
        assertFalse(response.isEmpty());
    }

    @Test
    public void testGetChildLocation_InvalidLocationId() {
        int invalidLocationId = 100;

        List<LocationaListDto> emptyList = new ArrayList<>();
        when(locationService.getChildLocation(invalidLocationId)).thenReturn(emptyList);

        List<LocationaListDto> response = locationController.getChildLocation(invalidLocationId);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    public void testGetChildLocation_NegativeLocationId() {
        int negativeLocationId = -1;

        List<LocationaListDto> response = locationController.getChildLocation(negativeLocationId);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    public void testGetAllParent_ValidLocationId() {
        int validLocationId = 1;

        List<LocationaListDto> parentLocationList = new ArrayList<>();
        LocationaListDto locationaListDto = new LocationaListDto();
        locationaListDto.setId(1);
        locationaListDto.setParent(1L);
        locationaListDto.setName("Name 1");
        locationaListDto.setType("Type 1");
        locationaListDto.setActive(true);
        locationaListDto.setParentName("Parent 1");
        parentLocationList.add(locationaListDto);
        when(locationService.getAllParent(validLocationId)).thenReturn(parentLocationList);

        List<LocationaListDto> response = locationController.getAllParent(validLocationId);

        assertNotNull(response);
        assertFalse(response.isEmpty());
    }

    @Test
    public void testGetAllParent_InvalidLocationId() {
        int invalidLocationId = 100;

        List<LocationaListDto> emptyList = new ArrayList<>();
        when(locationService.getAllParent(invalidLocationId)).thenReturn(emptyList);

        List<LocationaListDto> response = locationController.getAllParent(invalidLocationId);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

}