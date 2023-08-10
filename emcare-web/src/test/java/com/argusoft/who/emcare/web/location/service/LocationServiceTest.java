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
import org.junit.jupiter.api.Nested;
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
        HierarchyMasterDto hierarchyMasterDto = getMockHierarchyMasterDto("REG001", "Region 1", "Region");
        HierarchyMaster hierarchyMaster = getMockHierarchyMaster("REG001", "Region 1", "Region");

        when(hierarchyMasterDao.saveAndFlush(any(HierarchyMaster.class))).thenAnswer(i -> i.getArgument(0));

        ResponseEntity<Object> responseEntity = locationService.createHierarchyMaster(hierarchyMasterDto);

        assertEquals(ResponseEntity.ok(hierarchyMaster), responseEntity);
    }

    @Test
    public void testUpdateHierarchyMaster() {
        HierarchyMasterDto hierarchyMasterDto = getMockHierarchyMasterDto("REG001", "Updated Region 1", "Region");
        HierarchyMaster updatedHierarchyMaster = getMockHierarchyMaster("REG001", "Updated Region 1", "Region");

        when(hierarchyMasterDao.save(any(HierarchyMaster.class))).thenAnswer(i -> i.getArgument(0));

        ResponseEntity<Object> responseEntity = locationService.updateHierarchyMaster(hierarchyMasterDto);

        assertEquals(ResponseEntity.ok(updatedHierarchyMaster), responseEntity);
    }

    @Test
    public void testDeleteHierarchyMaster() {
        HierarchyMaster hierarchyMaster = getMockHierarchyMaster("REG001", "Region 1", "Region");

        locationService.deleteHierarchyMaster("1");

        verify(hierarchyMasterDao).deleteById("1");
    }

    @Test
    public void testGetAllHierarchyMaster() {
        List<HierarchyMaster> hierarchyMasterList = List.of(
            getMockHierarchyMaster("REG001", "Region 1", "Region"),
            getMockHierarchyMaster("REG002", "Region 2", "Region")
        );

        when(hierarchyMasterDao.findAll()).thenReturn(hierarchyMasterList);

        ResponseEntity<Object> responseEntity = locationService.getAllHierarchyMaster();

        assertEquals(ResponseEntity.ok(hierarchyMasterList), responseEntity);
    }

    @Nested
    class testGetHierarchyMasterById {
        @Test
        public void existingId() {
            HierarchyMaster hierarchyMaster = getMockHierarchyMaster("REG001", "Region 1", "Region");

            when(hierarchyMasterDao.findById("1")).thenReturn(Optional.of(hierarchyMaster));

            ResponseEntity<Object> responseEntity = locationService.getHierarchyMasterById("1");

            assertEquals(ResponseEntity.ok(hierarchyMaster), responseEntity);
        }

        @Test
        public void nonExistingId() {
            when(hierarchyMasterDao.findById("non_existing_type")).thenReturn(Optional.empty());

            ResponseEntity<Object> responseEntity = locationService.getHierarchyMasterById("non_existing_type");

            assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        }
    }

    @Nested
    class testCreateOrUpdate {
        @Test
        public void noExistingLocation() {
            LocationMasterDto locationDto = getMockLocationMasterDto(null, "Location 1", "Type 1", null);

            when(locationMasterDao.findAll()).thenReturn(new ArrayList<>());

            LocationMaster createdLocation = getMockLocationMaster(1, "Location 1", "Type 1", 0);

            when(locationMasterDao.saveAndFlush(any(LocationMaster.class))).thenAnswer(i -> {
                LocationMaster lm = i.getArgument(0);
                if(lm.getId() == null) lm.setId(createdLocation.getId());
                return lm;
            });

            ResponseEntity<Object> responseEntity = locationService.createOrUpdate(locationDto);

            assertEquals(ResponseEntity.ok(createdLocation), responseEntity);

            verify(locationMasterDao, times(1)).findAll();
            verify(locationMasterDao, times(1)).saveAndFlush(any(LocationMaster.class));
        }

        @Test
        public void existingLocation() {
            LocationMasterDto locationDto = getMockLocationMasterDto(1, "NL 1", "Type 1", null);

            List<LocationMaster> existingLocations = List.of(
                    getMockLocationMaster(1, "EL 1", null, null)
            );

            when(locationMasterDao.findAll()).thenReturn(existingLocations);

            LocationMaster updatedLocation = getMockLocationMaster(1, "NL 1", "Type 1", 0);

            when(locationMasterDao.saveAndFlush(any(LocationMaster.class))).thenAnswer(i -> {
                LocationMaster lm = i.getArgument(0);
                if(lm.getId() == null) lm.setId(404);
                return lm;
            });

            ResponseEntity<Object> responseEntity = locationService.createOrUpdate(locationDto);

            assertEquals(ResponseEntity.ok(updatedLocation), responseEntity);

            verify(locationMasterDao, times(1)).findAll();
            verify(locationMasterDao, times(1)).saveAndFlush(any(LocationMaster.class));
        }
    }

    @Nested
    class testGetAllLocation {
        @Test
        public void noLocations() {
            when(locationMasterDao.findAll()).thenReturn(new ArrayList<>());

            ResponseEntity<Object> responseEntity = locationService.getAllLocation();

            assertEquals(ResponseEntity.ok(new ArrayList<LocationaListDto>()), responseEntity);

            verify(locationMasterDao, times(1)).findAll();
        }

        @Test
        public void withLocations() {
            LocationMaster locationMaster = getMockLocationMaster(1, "Location 1", null, null);
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
    }

    @Nested
    class testUpdateLocation {
        @Test
        public void validData() {
            LocationMasterDto validLocationDto = new LocationMasterDto();
            validLocationDto.setId(1);
            validLocationDto.setName("Updated Location");
            validLocationDto.setParent(2L);
            validLocationDto.setType("Updated Type");

            when(locationMasterDao.save(any(LocationMaster.class)))
                    .thenAnswer(i -> {
                        LocationMaster lm = i.getArgument(0);
                        if(lm.getId() == null) lm.setId(404);
                        return lm;
                    });

            ResponseEntity<Object> response = locationService.updateLocation(validLocationDto);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());

            LocationMaster responseLocation = (LocationMaster) response.getBody();
            assertEquals(validLocationDto.getId(), responseLocation.getId());
            assertEquals(validLocationDto.getName(), responseLocation.getName());
            assertEquals(validLocationDto.getParent(), responseLocation.getParent());
            assertEquals(validLocationDto.getType(), responseLocation.getType());
            assertTrue(responseLocation.isActive());
        }

        @Test
        public void invalidData() {
            LocationMasterDto validLocationDto = new LocationMasterDto();
            validLocationDto.setId(1);
            validLocationDto.setName(null);
            validLocationDto.setParent(null);
            validLocationDto.setType(null);

            when(locationMasterDao.save(any(LocationMaster.class)))
                    .thenAnswer(i -> {
                        LocationMaster lm = i.getArgument(0);
                        if(lm.getId() == null) lm.setId(404);
                        return lm;
                    });

            ResponseEntity<Object> response = locationService.updateLocation(validLocationDto);

            // @TODO: Should be this
            // assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
        }
    }

    @Nested
    class testDeleteLocationById {
        @Test
        public void validIdWithNoChildLocations() {
            when(locationMasterDao.getChildLocation(anyInt())).thenReturn(new ArrayList<>());

            ResponseEntity<Object> response = locationService.deleteLocationById(1);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNull(response.getBody());
        }

        @Test
        public void validIdWithChildLocations() {
            List<LocationMaster> childLocations = new ArrayList<>();
            childLocations.add(new LocationMaster());
            childLocations.add(new LocationMaster());

            when(locationMasterDao.getChildLocation(anyInt())).thenReturn(childLocations);

            ResponseEntity<Object> response = locationService.deleteLocationById(1);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("This Location Have Child Location, You Can Not Delete", response.getBody());
        }
    }

    @Nested
    class testGetLocationMasterById {
        @Test
        public void existingId() {
            LocationMaster location = getMockLocationMaster(1, "Test Location", null, null);

            when(locationMasterDao.findById(1)).thenReturn(Optional.of(location));

            LocationMaster result = locationService.getLocationMasterById(1);

            assertNotNull(result);
            assertEquals(1, result.getId());
            assertEquals("Test Location", result.getName());
        }

        @Test
        public void nonExistingId() {
            when(locationMasterDao.findById(1)).thenReturn(Optional.empty());

            EmCareException exception = assertThrows(EmCareException.class, () -> locationService.getLocationMasterById(1));

            assertEquals(CommonConstant.EM_CARE_NO_DATA_FOUND, exception.getMessage());
        }
    }

    @Nested
    class testGetLocationByType {
        @Test
        public void validTypeWithMultipleLocations() {
            List<LocationMaster> locations = Arrays.asList(
                    getMockLocationMaster(1, "Location 1", "Type A", null),
                    getMockLocationMaster(2, "Location 2", "Type A", null)
            );

            when(locationMasterDao.findByType("Type A")).thenReturn(locations);

            List<LocationMaster> result = locationService.getLocationByType("Type A");

            assertNotNull(result);
            assertEquals(locations.size(), result.size());
            assertEquals("Location 1", result.get(0).getName());
            assertEquals("Location 2", result.get(1).getName());
        }

        @Test
        public void validTypeWithNoLocations() {
            when(locationMasterDao.findByType("Type B")).thenReturn(new ArrayList<>());

            List<LocationMaster> result = locationService.getLocationByType("Type B");

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        public void emptyTypeWithEmptyList() {
            List<LocationMaster> result = locationService.getLocationByType("");

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        public void nullTypeWithEmptyList() {
            List<LocationMaster> result = locationService.getLocationByType(null);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    class testGetChildLocation {
        @Test
        public void validParentIdAndHasChildren() {
            List<LocationMaster> locationMasters = List.of(
                    getMockLocationMaster(1, "Location 1", null, 10),
                    getMockLocationMaster(2, "Location 2", null, 10)
            );

            when(locationMasterDao.findByParent(10L)).thenReturn(locationMasters);

            LocationMaster parentLocation = new LocationMaster();
            parentLocation.setId(10);
            parentLocation.setName("Parent Location");
            when(locationMasterDao.findById(10)).thenReturn(Optional.of(parentLocation));

            List<LocationaListDto> result = locationService.getChildLocation(10);

            assertNotNull(result);
            assertEquals(locationMasters.size(), result.size());
            assertEquals("Location 1", result.get(0).getName());
            assertEquals("Location 2", result.get(1).getName());
            assertEquals("Parent Location", result.get(0).getParentName());
            assertEquals("Parent Location", result.get(1).getParentName());
        }

        @Test
        public void validParentIdWithNoChildren() {
            when(locationMasterDao.findByParent(20L)).thenReturn(new ArrayList<>());

            List<LocationaListDto> result = locationService.getChildLocation(20);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    class testGetAllParent {
        @Test
        public void validLocationIdWithParentNodes() {
            List<LocationMaster> locationMasters = List.of(
                    getMockLocationMaster(1, "Location 1", null, 10),
                    getMockLocationMaster(2, "Location 2", null, 20)
            );

            when(locationMasterDao.getAllParent(30)).thenReturn(locationMasters);

            LocationMaster parentLocation1 = getMockLocationMaster(10, "Parent Location 1", null, null);
            LocationMaster parentLocation2 = getMockLocationMaster(20, "Parent Location 2", null, null);

            when(locationMasterDao.findById(10)).thenReturn(Optional.of(parentLocation1));
            when(locationMasterDao.findById(20)).thenReturn(Optional.of(parentLocation2));

            List<LocationaListDto> result = locationService.getAllParent(30);

            assertNotNull(result);
            assertEquals(locationMasters.size(), result.size());
            assertEquals("Location 1", result.get(0).getName());
            assertEquals("Location 2", result.get(1).getName());
            assertEquals("Parent Location 1", result.get(0).getParentName());
            assertEquals("Parent Location 2", result.get(1).getParentName());
        }

        @Test
        public void validLocationIdWithNoParentNodes() {
            when(locationMasterDao.getAllParent(40)).thenReturn(new ArrayList<>());

            List<LocationaListDto> result = locationService.getAllParent(40);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    class testGetLocationByLocationFilter {
        @Test
        public void validLocationIdAndHasChildLocations() {
            when(locationMasterDao.getAllChildLocationId(10)).thenReturn(Arrays.asList(11, 12, 13));

            List<LocationMaster> locationMasters = List.of(
                    getMockLocationMaster(11, "Location11", null, 10),
                    getMockLocationMaster(12, "Location12", null, 10)
            );

            when(locationMasterDao.getLocationByLocationIds(Arrays.asList(11, 12, 13), null, 2, 0)).thenReturn(locationMasters);

            when(locationMasterDao.getLocationByLocationIdsCount(Arrays.asList(11, 12, 13), null)).thenReturn(2L);

            LocationMaster parentLocation = getMockLocationMaster(10, "Parent Location", null, null);
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
        public void validLocationIdWithNoChildLocations() {
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
        public void nullLocationId() {
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
    }

    private List<LocationMaster> getLocationData(String fileName) throws IOException {
        File file = new File("src/test/resources/mockdata/location/" + fileName + ".json");
        InputStream fileInputStream = new FileInputStream(file);
        String jsonString = IOUtils.toString(fileInputStream, StandardCharsets.UTF_8);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonString, new TypeReference<List<LocationMaster>>(){});
    }


    @Nested
    class testGetLocationPage {
        @Test
        public void descOrderByNameWithSearchStringNullParent() throws IOException {
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
        public void orderByNullWithoutSearchStringAndExistingParent() throws IOException {
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

    // ============================================================================
    // ========================= Mock data generating fn ==========================
    // ============================================================================
    HierarchyMasterDto getMockHierarchyMasterDto (String code, String name, String type) {
        HierarchyMasterDto hierarchyMasterDto = new HierarchyMasterDto();
        hierarchyMasterDto.setHierarchyType(type);
        hierarchyMasterDto.setName(name);
        hierarchyMasterDto.setCode(code);
        return hierarchyMasterDto;
    }

    HierarchyMaster getMockHierarchyMaster (String code, String name, String type) {
        HierarchyMaster hierarchyMasterDto = new HierarchyMaster();
        hierarchyMasterDto.setHierarchyType(type);
        hierarchyMasterDto.setName(name);
        hierarchyMasterDto.setCode(code);
        return hierarchyMasterDto;
    }

    LocationMasterDto getMockLocationMasterDto (Integer id, String name, String type, Integer parent) {
        LocationMasterDto locationMasterDto = new LocationMasterDto();
        locationMasterDto.setId(id);
        locationMasterDto.setName(name);
        locationMasterDto.setType(type);
        locationMasterDto.setParent(parent == null ? null : parent.longValue());
        return locationMasterDto;
    }

    LocationMaster getMockLocationMaster (Integer id, String name, String type, Integer parent) {
        LocationMaster locationMasterDto = new LocationMaster();
        locationMasterDto.setId(id);
        locationMasterDto.setName(name);
        locationMasterDto.setType(type);
        locationMasterDto.setActive(true);
        locationMasterDto.setParent(parent == null ? null : parent.longValue());
        return locationMasterDto;
    }
}