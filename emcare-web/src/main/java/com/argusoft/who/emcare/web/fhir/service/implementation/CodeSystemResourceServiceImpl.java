package com.argusoft.who.emcare.web.fhir.service.implementation;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateParam;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.fhir.dao.CodeSystemResourceRepository;
import com.argusoft.who.emcare.web.fhir.dto.CodeSystemDto;
import com.argusoft.who.emcare.web.fhir.mapper.EmcareResourceMapper;
import com.argusoft.who.emcare.web.fhir.model.CodeSystemResource;
import com.argusoft.who.emcare.web.fhir.service.CodeSystemResourceService;
import org.hl7.fhir.r4.model.*;
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
public class CodeSystemResourceServiceImpl implements CodeSystemResourceService {

    @Autowired
    CodeSystemResourceRepository codeSystemResourceRepository;

    private final FhirContext fhirCtx = FhirContext.forR4();
    private final IParser parser = fhirCtx.newJsonParser().setPrettyPrint(true);

    @Override
    public CodeSystem saveResource(CodeSystem codeSystem) {
        Meta m = new Meta();
        m.setVersionId("1");
        m.setLastUpdated(new Date());
        codeSystem.setMeta(m);

        String codeSystemId = null;
        if (codeSystem.getId() != null) {
            codeSystemId = codeSystem.getIdElement().getIdPart();
        } else {
            codeSystemId = UUID.randomUUID().toString();
        }
        codeSystem.setId(codeSystemId);

        String codeSystemString = parser.encodeResourceToString(codeSystem);

        CodeSystemResource codeSystemResource = new CodeSystemResource();
        codeSystemResource.setText(codeSystemString);
        codeSystemResource.setResourceId(codeSystemId);
        codeSystemResourceRepository.save(codeSystemResource);

        return codeSystem;
    }

    @Override
    public CodeSystem getResourceById(String id) {
        CodeSystemResource codeSystemResource = codeSystemResourceRepository.findByResourceId(id);
        CodeSystem codeSystem = null;
        if (codeSystemResource != null) {
            codeSystem = parser.parseResource(CodeSystem.class, codeSystemResource.getText());
        }
        return codeSystem;
    }

    @Override
    public MethodOutcome updateCodeSystem(IdType theId, CodeSystem codeSystem) {
        Meta m = new Meta();
        m.setVersionId("1");
        m.setLastUpdated(new Date());
        codeSystem.setMeta(m);


        String encodeResource = parser.encodeResourceToString(codeSystem);
        CodeSystemResource codeSystemResource = codeSystemResourceRepository.findByResourceId(theId.getIdPart());
        codeSystemResource.setText(encodeResource);

        codeSystemResourceRepository.save(codeSystemResource);

        MethodOutcome retVal = new MethodOutcome();
        retVal.setId(new IdType(CommonConstant.CODE_SYSTEM, codeSystem.getId(), "1"));
        retVal.setResource(codeSystem);
        return retVal;
    }

    @Override
    public List<CodeSystem> getAllCodeSystem(DateParam theDate) {
        List<CodeSystem> codeSystems = new ArrayList<>();

        List<CodeSystemResource> codeSystemResources;

        if (theDate == null) {
            codeSystemResources =  codeSystemResourceRepository.findAll();
        } else {
            codeSystemResources = codeSystemResourceRepository.findByModifiedOnGreaterThanOrCreatedOnGreaterThan(theDate.getValue(), theDate.getValue());
        }


        for (CodeSystemResource codeSystemResource : codeSystemResources) {
            CodeSystem codeSystem = parser.parseResource(CodeSystem.class, codeSystemResource.getText());
            codeSystems.add(codeSystem);
        }
        return codeSystems;
    }

    @Override
    public PageDto getCodeSystemPage(Integer pageNo, String searchString) {
        List<CodeSystemDto> codeSystemDtos = new ArrayList<>();
        Page<CodeSystemResource> codeSystemResources = null;
        Pageable page = PageRequest.of(pageNo, CommonConstant.PAGE_SIZE);
        Long count;

        if (searchString != null && !searchString.isEmpty()) {
            codeSystemResources = codeSystemResourceRepository.findByTextContainingIgnoreCase(searchString, page);
            count = Long.valueOf(codeSystemResourceRepository.findByTextContainingIgnoreCase(searchString).size());
        } else {
            codeSystemResources = codeSystemResourceRepository.findAll(page);
            count = Long.valueOf(codeSystemResourceRepository.findAll().size());
        }


        for (CodeSystemResource codeSystemResource : codeSystemResources) {
            CodeSystem codeSystem = parser.parseResource(CodeSystem.class, codeSystemResource.getText());
            codeSystemDtos.add(EmcareResourceMapper.getCodeSystemDto(codeSystem));
        }
        PageDto pageDto = new PageDto();
        pageDto.setList(codeSystemDtos);
        pageDto.setTotalCount(count);

        return pageDto;
    }

    public Bundle getCodeSystemDataForGoogleFhirDataPipes(String summaryType, Integer count, String total) {
        Bundle bundle = new Bundle();
        switch(summaryType) {
            case "count":
                bundle.setTotal((int)codeSystemResourceRepository.count());
                return bundle;
            case "data":
                List<CodeSystem> codeSystems = getAllCodeSystem(null);
                bundle.setTotal(Math.min(count, codeSystems.size()));

                for(int i = 0; i < Math.min(count, codeSystems.size()); i++) {
                    bundle.addEntry(
                            new Bundle.BundleEntryComponent()
                                    .setResource(codeSystems.get(i))
                                    .setFullUrl("http://localhost:8080/fhir/" + codeSystems.get(i).getId().split("/_history")[0])
                    );
                }
                return bundle;
        }
        return null;
    }
}
