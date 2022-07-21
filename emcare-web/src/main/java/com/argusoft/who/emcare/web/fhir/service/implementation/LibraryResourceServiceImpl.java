package com.argusoft.who.emcare.web.fhir.service.implementation;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.fhir.dao.LibraryResourceRepository;
import com.argusoft.who.emcare.web.fhir.dto.LibraryDto;
import com.argusoft.who.emcare.web.fhir.mapper.EmcareResourceMapper;
import com.argusoft.who.emcare.web.fhir.model.LibraryResource;
import com.argusoft.who.emcare.web.fhir.service.LibraryResourceService;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Library;
import org.hl7.fhir.r4.model.Meta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class LibraryResourceServiceImpl implements LibraryResourceService {

    @Autowired
    LibraryResourceRepository libraryResourceRepository;

    private final FhirContext fhirCtx = FhirContext.forR4();
    private final IParser parser = fhirCtx.newJsonParser().setPrettyPrint(true);

    @Override
    public Library saveResource(Library library) {
        Meta m = new Meta();
        m.setVersionId("1");
        m.setLastUpdated(new Date());
        library.setMeta(m);

        String libraryId = UUID.randomUUID().toString();
        library.setId(libraryId);

        String libraryString = parser.encodeResourceToString(library);

        LibraryResource libraryResource = new LibraryResource();
        libraryResource.setText(libraryString);
        libraryResource.setResourceId(libraryId);

        libraryResourceRepository.save(libraryResource);

        return library;
    }

    @Override
    public Library getResourceById(String id) {
        LibraryResource libraryResource = libraryResourceRepository.findByResourceId(id);
        Library library = null;
        if (libraryResource != null) {
            library = parser.parseResource(Library.class, libraryResource.getText());
        }
        return library;
    }

    @Override
    public MethodOutcome updateLibraryResource(IdType idType, Library library) {
        Meta m = new Meta();
        m.setVersionId("1");
        m.setLastUpdated(new Date());
        library.setMeta(m);


        String encodeResource = parser.encodeResourceToString(library);
        LibraryResource libraryResource = libraryResourceRepository.findByResourceId(idType.getIdPart());
        libraryResource.setText(encodeResource);

        libraryResourceRepository.save(libraryResource);

        MethodOutcome retVal = new MethodOutcome();
        retVal.setId(new IdType(CommonConstant.LIBRARY, library.getId(), "1"));
        retVal.setResource(library);
        return retVal;
    }

    @Override
    public List<Library> getAllLibrary() {
        List<Library> libraries = new ArrayList<>();

        List<LibraryResource> libraryResources = libraryResourceRepository.findAll();

        for (LibraryResource libraryResource : libraryResources) {
            Library library = parser.parseResource(Library.class, libraryResource.getText());
            libraries.add(library);
        }
        return libraries;
    }

    @Override
    public PageDto getLibraryPage(Integer pageNo, String searchString) {
        List<LibraryDto> libraryDtos = new ArrayList<>();
        Page<LibraryResource> libraryResources = null;
        Pageable page = PageRequest.of(pageNo, CommonConstant.PAGE_SIZE);
        Long count = 0L;

        if (searchString != null && !searchString.isEmpty()) {
            libraryResources = libraryResourceRepository.findByTextContainingIgnoreCase(searchString, page);
            count = Long.valueOf(libraryResourceRepository.findByTextContainingIgnoreCase(searchString).size());
        } else {
            libraryResources = libraryResourceRepository.findAll(page);
            count = Long.valueOf(libraryResourceRepository.findAll().size());
        }


        for (LibraryResource libraryResource : libraryResources) {
            Library library = parser.parseResource(Library.class, libraryResource.getText());
            libraryDtos.add(EmcareResourceMapper.getLibraryDto(library));
        }
        PageDto pageDto = new PageDto();
        pageDto.setList(libraryDtos);
        pageDto.setTotalCount(count);

        return pageDto;
    }
}
