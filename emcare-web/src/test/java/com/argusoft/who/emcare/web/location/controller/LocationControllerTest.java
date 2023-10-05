package com.argusoft.who.emcare.web.location.controller;

import com.argusoft.who.emcare.web.location.dao.HierarchyMasterDao;
import com.argusoft.who.emcare.web.location.dto.HierarchyMasterDto;
import com.argusoft.who.emcare.web.location.dto.LocationMasterDto;
import com.argusoft.who.emcare.web.location.dto.LocationaListDto;
import com.argusoft.who.emcare.web.location.mapper.HierarchyMasterMapper;
import com.argusoft.who.emcare.web.location.mapper.LocationMasterMapper;
import com.argusoft.who.emcare.web.location.model.HierarchyMaster;
import com.argusoft.who.emcare.web.location.model.LocationMaster;
import com.argusoft.who.emcare.web.location.service.LocationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Nested
    class testCreateHierarchyMaster {
        @Test
        public void success() {
            HierarchyMasterDto expectedhierarchyMasterDto = getMockHierarchyMasterDto("CODE", "NAME", "TYPE");
            HierarchyMasterDto hierarchyMasterDto = getMockHierarchyMasterDto("CODE", "NAME", "TYPE");

            when(locationService.createHierarchyMaster(any(HierarchyMasterDto.class)))
                    .thenAnswer(i ->
                            new ResponseEntity<>(HierarchyMasterMapper.dtoToEntityForHierarchyMasterCreate(i.getArgument(0)), HttpStatus.CREATED)
                    );

            ResponseEntity<Object> response = locationController.createHierarchyMaster(hierarchyMasterDto);

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            HierarchyMaster actualDao = (HierarchyMaster) response.getBody();
            assertNotNull(actualDao);
            assertEquals(expectedhierarchyMasterDto.getCode(), actualDao.getCode());
            assertEquals(expectedhierarchyMasterDto.getName(), actualDao.getName());
            assertEquals(expectedhierarchyMasterDto.getHierarchyType(), actualDao.getHierarchyType());
            verify(locationService, times(1)).createHierarchyMaster(hierarchyMasterDto);
        }

        @Test
        public void failure() {
            HierarchyMasterDto hierarchyMasterDto = new HierarchyMasterDto();

            when(locationService.createHierarchyMaster(any(HierarchyMasterDto.class)))
                    .thenReturn(new ResponseEntity<>("Error creating hierarchy.", HttpStatus.BAD_REQUEST));

            ResponseEntity<Object> response = locationController.createHierarchyMaster(hierarchyMasterDto);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("Error creating hierarchy.", response.getBody());
            verify(locationService, times(1)).createHierarchyMaster(hierarchyMasterDto);
        }
    }

    @Nested
    class testUpdateHierarchyMaster {
        @Test
        public void success(){
            HierarchyMasterDto expectedhierarchyMasterDto = getMockHierarchyMasterDto("CODE", "NAME", "TYPE");
            HierarchyMasterDto hierarchyMasterDto = getMockHierarchyMasterDto("CODE", "NAME", "TYPE");

            when(locationService.updateHierarchyMaster(any(HierarchyMasterDto.class))).thenAnswer(
                    i -> new ResponseEntity<>(HierarchyMasterMapper.dtoToEntityForHierarchyMasterCreate(i.getArgument(0)), HttpStatus.CREATED)
            );
            ResponseEntity<Object> response = locationController.updateHierarchyMaster(hierarchyMasterDto);

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            HierarchyMaster actualDao = (HierarchyMaster) response.getBody();
            assertNotNull(actualDao);
            assertEquals(expectedhierarchyMasterDto.getCode(), actualDao.getCode());
            assertEquals(expectedhierarchyMasterDto.getName(), actualDao.getName());
            assertEquals(expectedhierarchyMasterDto.getHierarchyType(), actualDao.getHierarchyType());
            verify(locationService, times(1)).updateHierarchyMaster(hierarchyMasterDto);
        }

        @Test
        void failure() {
            HierarchyMasterDto hierarchyMasterDto = new HierarchyMasterDto();

            when(locationService.updateHierarchyMaster(any(HierarchyMasterDto.class)))
                    .thenReturn(new ResponseEntity<>("Error updating hierarchy.", HttpStatus.BAD_REQUEST));

            ResponseEntity<Object> response = locationController.updateHierarchyMaster(hierarchyMasterDto);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("Error updating hierarchy.", response.getBody());
            verify(locationService, times(1)).updateHierarchyMaster(hierarchyMasterDto);
        }
    }

    @Nested
    class testDeleteHierarchyMaster {
        @Test
        public void success() {
            String validHierarchyId = "1";

            locationController.deleteHierarchyMaster(validHierarchyId);

            verify(locationService, times(1)).deleteHierarchyMaster(validHierarchyId);
        }
    }

    @Nested
    class testRetrieveHierarchyMasterById {
        @Test
        public void validIdType() {
            String validType = "employee";

            when(locationService.getHierarchyMasterById(validType)).thenReturn(new ResponseEntity<>("Success", HttpStatus.OK));

            ResponseEntity<Object> responseEntity = locationController.retriveHierarchyMasterById(validType);

            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
            assertNotNull(responseEntity.getBody());
        }

        @Test
        public void invalidIdType() {
            String invalidType = "department";

            when(locationService.getHierarchyMasterById(invalidType)).thenReturn(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));

            ResponseEntity<Object> responseEntity = locationController.retriveHierarchyMasterById(invalidType);

            assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
            assertNull(responseEntity.getBody());
        }

        @Test
        public void emptyIdType() {
            String emptyType = "";

            ResponseEntity<Object> responseEntity = locationController.retriveHierarchyMasterById(emptyType);

            assertNull(responseEntity);
        }
    }

    @Nested
    class testRetrieveAllHierarchyMaster {
        @Test
        public void success() {
            List<HierarchyMasterDto> hierarchyMasterList = new ArrayList<>();
            hierarchyMasterList.add(getMockHierarchyMasterDto("1", "Name1", "Type1"));
            hierarchyMasterList.add(getMockHierarchyMasterDto("2", "Name2", "Type2"));
            List<HierarchyMasterDto> expectedHierarchyMasterList = new ArrayList<>(hierarchyMasterList);

            when(locationService.getAllHierarchyMaster()).thenReturn(new ResponseEntity<>(hierarchyMasterList, HttpStatus.OK));

            ResponseEntity<Object> responseEntity = locationController.retriveAllHierarchyMaster();

            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
            List<HierarchyMasterDto> actualHierarchyMasterList = (List<HierarchyMasterDto>) responseEntity.getBody();
            assertNotNull(actualHierarchyMasterList);
            assertEquals(expectedHierarchyMasterList.size(), actualHierarchyMasterList.size());
        }

        @Test
        public void emptyResult() {
            List<HierarchyMasterDto> emptyHierarchyMasterList = new ArrayList<>();
            when(locationService.getAllHierarchyMaster()).thenReturn(new ResponseEntity<>(emptyHierarchyMasterList, HttpStatus.OK));

            ResponseEntity<Object> responseEntity = locationController.retriveAllHierarchyMaster();

            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
            assertNotNull(responseEntity.getBody());
            assertTrue(((List<?>) responseEntity.getBody()).isEmpty());
        }

        @Test
        public void error() {
            when(locationService.getAllHierarchyMaster()).thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));

            ResponseEntity<Object> responseEntity = locationController.retriveAllHierarchyMaster();

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
            assertNull(responseEntity.getBody());
        }
    }

    @Nested
    class testCreateOrUpdate {
        @Test
        public void createNewLocation() {
            LocationMasterDto locationDtoToCreate = getMockLocationMasterDto(1, "LM1", "T1", null);
            LocationMasterDto expectedLocation = getMockLocationMasterDto(1, "LM1", "T1", null);

            when(locationService.createOrUpdate(locationDtoToCreate))
                    .thenAnswer(
                            i -> ResponseEntity.ok(LocationMasterMapper.dtoToEntityForLocationMasterCreate(i.getArgument(0)))
                    );

            ResponseEntity<Object> responseEntity = locationController.createOrUpdate(locationDtoToCreate);

            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

            LocationMaster actualLocation = (LocationMaster) responseEntity.getBody();
            assertNotNull(actualLocation);
            assertEquals(expectedLocation.getId(), actualLocation.getId());
            assertEquals(expectedLocation.getName(), actualLocation.getName());
            assertEquals(expectedLocation.getType(), actualLocation.getType());
            assertEquals(0, actualLocation.getParent());
        }

        @Test
        public void updateExistingLocation() {
            LocationMasterDto existingLocationDto = new LocationMasterDto();
            existingLocationDto.setId(1);
            existingLocationDto.setName("Updated Location");

            when(locationService.createOrUpdate(existingLocationDto)).thenReturn(new ResponseEntity<>("Success", HttpStatus.OK));

            ResponseEntity<Object> responseEntity = locationController.createOrUpdate(existingLocationDto);

            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
            assertNotNull(responseEntity.getBody());
        }

        @Test
        public void invalidInput() {
            LocationMasterDto invalidLocationDto = new LocationMasterDto();

            ResponseEntity<Object> responseEntity = locationController.createOrUpdate(invalidLocationDto);

            assertNull(responseEntity);
        }
    }

    @Nested
    class testGetAllLocation {
        @Test
        public void success() {
            List<LocationMaster> locationList = new ArrayList<>();
            locationList.add(getMockLocationMaster(1, "LN", "T1", 0));

            when(locationService.getAllLocation()).thenReturn(new ResponseEntity<>(locationList, HttpStatus.OK));

            ResponseEntity<Object> responseEntity = locationController.getAllLocation();

            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
            assertNotNull(responseEntity.getBody());
        }

        @Test
        public void emptyResult() {
            List<LocationMaster> emptyLocationList = new ArrayList<>();
            when(locationService.getAllLocation()).thenReturn(new ResponseEntity<>(emptyLocationList, HttpStatus.OK));

            ResponseEntity<Object> responseEntity = locationController.getAllLocation();

            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
            assertNotNull(responseEntity.getBody());
            assertTrue(((List<?>) responseEntity.getBody()).isEmpty());
        }

        @Test
        public void error() {
            when(locationService.getAllLocation()).thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));

            ResponseEntity<Object> responseEntity = locationController.getAllLocation();

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
            assertNull(responseEntity.getBody());
        }
    }

    @Nested
    class testGetLocationPage {
        @Test
        public void success() {
            int pageNo = 1;
            String orderBy = "name";
            String order = "asc";
            String searchString = "Location";
            List<LocationMaster> locationPage = new ArrayList<>();

            LocationMaster locationMaster = getMockLocationMaster(1, "LN", "T1", 0);
            locationPage.add(locationMaster);
            when(locationService.getLocationPage(pageNo, orderBy, order, searchString)).thenReturn(new ResponseEntity<>(locationPage, HttpStatus.OK));

            ResponseEntity<Object> responseEntity = locationController.getLocationPage(pageNo, orderBy, order, searchString);

            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
            assertNotNull(responseEntity.getBody());
        }

        @Test
        public void defaultOrderBy() {
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
        public void error() {
            int pageNo = 1;
            String orderBy = "name";
            String order = "asc";
            String searchString = "Location";

            when(locationService.getLocationPage(pageNo, orderBy, order, searchString)).thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));

            ResponseEntity<Object> responseEntity = locationController.getLocationPage(pageNo, orderBy, order, searchString);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
            assertNull(responseEntity.getBody());
        }
    }

    @Nested
    class testGetLocationPageByLocationFilter {
        @Test
        public void success() {
            int pageNo = 1;
            int locationId = 1;
            String searchString = "Location";

            List<LocationMaster> locationPage = new ArrayList<>();
            locationPage.add(getMockLocationMaster(1, "LN", "T1", 0));

            when(locationService.getLocationByLocationFilter(pageNo, locationId, searchString)).thenReturn(new ResponseEntity<>(locationPage, HttpStatus.OK));

            ResponseEntity<Object> responseEntity = locationController.getLocationPageByLocationFilter(pageNo, locationId, searchString);

            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
            assertNotNull(responseEntity.getBody());
        }

        @Test
        public void nullLocationId() {
            int pageNo = 1;
            String searchString = "Location";

            List<LocationMaster> locationPage = new ArrayList<>();
            locationPage.add(getMockLocationMaster(1, "LN", "T1", 0));

            when(locationService.getLocationByLocationFilter(pageNo, null, searchString)).thenReturn(new ResponseEntity<>(locationPage, HttpStatus.OK));

            ResponseEntity<Object> responseEntity = locationController.getLocationPageByLocationFilter(pageNo, null, searchString);

            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
            assertNotNull(responseEntity.getBody());
        }

        @Test
        public void error() {
            int pageNo = 1;
            int locationId = 1;
            String searchString = "Location";

            when(locationService.getLocationByLocationFilter(pageNo, locationId, searchString)).thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));

            ResponseEntity<Object> responseEntity = locationController.getLocationPageByLocationFilter(pageNo, locationId, searchString);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
            assertNull(responseEntity.getBody());
        }
    }

    @Nested
    class testUpdate {
        @Test
        public void existingLocation() {
            LocationMasterDto existingLocationDto = new LocationMasterDto();
            existingLocationDto.setId(1);
            existingLocationDto.setName("Updated Location");

            when(locationService.updateLocation(existingLocationDto)).thenReturn(new ResponseEntity<>("Success", HttpStatus.OK));

            ResponseEntity<Object> responseEntity = locationController.updateLocation(existingLocationDto);

            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
            assertNotNull(responseEntity.getBody());
        }

        @Test
        public void invalidInput() {
            LocationMasterDto invalidLocationDto = new LocationMasterDto();

            ResponseEntity<Object> responseEntity = locationController.updateLocation(invalidLocationDto);

            assertNull(responseEntity);
        }
    }

    @Nested
    class testDelete {
        @Test
        public void existingLocation() {
            LocationMasterDto existingLocationDto = getMockLocationMasterDto(1, "Updated Location", null, null);

            when(locationService.deleteLocationById(existingLocationDto.getId())).thenReturn(new ResponseEntity<>("Success", HttpStatus.OK));

            ResponseEntity<Object> responseEntity = locationController.deleteLocation(existingLocationDto.getId());

            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
            assertNotNull(responseEntity.getBody());
        }

        @Test
        public void invalidInput() {
            LocationMasterDto invalidLocationDto = new LocationMasterDto();

            ResponseEntity<Object> responseEntity = locationController.deleteLocation(invalidLocationDto.getId());

            assertNull(responseEntity);
        }
    }

    @Nested
    class testGetLocationById {
        @Test
        public void validLocationId() {
            int validLocationId = 1;

            LocationMaster locationMaster = getMockLocationMaster(1, "Location A", null, null);

            when(locationService.getLocationMasterById(validLocationId)).thenReturn(locationMaster);

            LocationMaster response = locationController.getLocationById(validLocationId);

            assertNotNull(response);
            assertEquals(validLocationId, response.getId());
            assertEquals("Location A", response.getName());
        }

        @Test
        public void invalidLocationId() {
            int invalidLocationId = 100;

            when(locationService.getLocationMasterById(invalidLocationId)).thenReturn(null);

            LocationMaster response = locationController.getLocationById(invalidLocationId);

            assertNull(response);
        }
    }

    @Nested
    class testGetLocationByType {
        @Test
        public void validType() {
            String validLocationType = "department";

            List<LocationMaster> locationPage = new ArrayList<>();
            locationPage.add(getMockLocationMaster(1, "LN", "department", 0));

            when(locationService.getLocationByType(validLocationType)).thenReturn(locationPage);

            List<LocationMaster> response = locationController.getLocationByType(validLocationType);

            assertNotNull(response);
            assertFalse(response.isEmpty());
        }

        @Test
        public void invalidType() {
            String invalidLocationType = "invalid_type"; // Replace with an invalid location type that does not exist in your system

            List<LocationMaster> emptyList = new ArrayList<>();
            when(locationService.getLocationByType(invalidLocationType)).thenReturn(emptyList);

            List<LocationMaster> response = locationController.getLocationByType(invalidLocationType);

            assertNotNull(response);
            assertTrue(response.isEmpty());
        }

        @Test
        public void emptyType() {
            String emptyLocationType = "";

            List<LocationMaster> response = locationController.getLocationByType(emptyLocationType);

            assertNotNull(response);
            assertTrue(response.isEmpty());
        }
    }

    @Test
    public void validLocationId() {
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

    @Nested
    class testGetChildLocation {
        @Test
        public void invalidLocationId() {
            int invalidLocationId = 100;

            List<LocationaListDto> emptyList = new ArrayList<>();
            when(locationService.getChildLocation(invalidLocationId)).thenReturn(emptyList);

            List<LocationaListDto> response = locationController.getChildLocation(invalidLocationId);

            assertNotNull(response);
            assertTrue(response.isEmpty());
        }

        @Test
        public void negativeLocationId() {
            int negativeLocationId = -1;

            List<LocationaListDto> response = locationController.getChildLocation(negativeLocationId);

            assertNotNull(response);
            assertTrue(response.isEmpty());
        }
    }

    @Nested
    class testGetAllParent {
        @Test
        public void validLocationId() {
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
        public void invalidLocationId() {
            int invalidLocationId = 100;

            List<LocationaListDto> emptyList = new ArrayList<>();
            when(locationService.getAllParent(invalidLocationId)).thenReturn(emptyList);

            List<LocationaListDto> response = locationController.getAllParent(invalidLocationId);

            assertNotNull(response);
            assertTrue(response.isEmpty());
        }
    }

    // Mock data generating functions
    HierarchyMasterDto getMockHierarchyMasterDto (String c, String n, String t) {
        HierarchyMasterDto hierarchyMasterDto = new HierarchyMasterDto();
        hierarchyMasterDto.setCode(c);
        hierarchyMasterDto.setName(n);
        hierarchyMasterDto.setHierarchyType(t);
        return hierarchyMasterDto;
    }

    LocationMasterDto getMockLocationMasterDto (Integer id, String name, String type, Integer parent) {
        LocationMasterDto mock = new LocationMasterDto();
        mock.setId(id);
        mock.setName(name);
        mock.setType(type);
        mock.setParent(parent == null ? null : parent.longValue());
        return mock;
    }

    LocationMaster getMockLocationMaster (Integer id, String name, String type, Integer parent) {
        LocationMaster mock = new LocationMaster();
        mock.setId(id);
        mock.setName(name);
        mock.setType(type);
        mock.setParent(parent == null ? null : parent.longValue());
        mock.setActive(true);
        return mock;
    }
}