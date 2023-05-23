package com.argusoft.who.emcare.web.fhir.service.implementation;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateParam;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.fhir.dao.BinaryResourceRepository;
import com.argusoft.who.emcare.web.fhir.model.BinaryResource;
import com.argusoft.who.emcare.web.fhir.service.BinaryResourceService;
import org.hl7.fhir.r4.model.Binary;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Meta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class BinaryResourceServiceImpl implements BinaryResourceService {

    @Autowired
    BinaryResourceRepository binaryResourceRepository;

    private final FhirContext fhirCtx = FhirContext.forR4();
    private final IParser parser = fhirCtx.newJsonParser().setPrettyPrint(true);


    @Override
    public Binary saveResource(Binary binary) {
        Meta m = new Meta();
        m.setVersionId("1");
        m.setLastUpdated(new Date());
        binary.setMeta(m);

        String binaryId = null;
        if (binary.getId() != null) {
            binaryId = binary.getIdElement().getIdPart();
        } else {
            binaryId = UUID.randomUUID().toString();
        }
        binary.setId(binaryId);

        String codeSystemString = parser.encodeResourceToString(binary);

        BinaryResource binaryResource = new BinaryResource();
        binaryResource.setText(codeSystemString);
        binaryResource.setResourceId(binaryId);
        binaryResourceRepository.save(binaryResource);

        return binary;
    }

    @Override
    public Binary getResourceById(String id) {
        BinaryResource binaryResource = binaryResourceRepository.findByResourceId(id);
        Binary binary = null;
        if (binaryResource != null) {
            binary = parser.parseResource(Binary.class, binaryResource.getText());
        }
        return binary;
    }

    @Override
    public MethodOutcome updateBinaryResource(IdType theId, Binary binary) {
        Meta m = new Meta();
        m.setVersionId("1");
        m.setLastUpdated(new Date());
        binary.setMeta(m);


        String encodeResource = parser.encodeResourceToString(binary);
        BinaryResource binaryResource = binaryResourceRepository.findByResourceId(theId.getIdPart());
        binaryResource.setText(encodeResource);

        binaryResourceRepository.save(binaryResource);

        MethodOutcome retVal = new MethodOutcome();
        retVal.setId(new IdType(CommonConstant.BINARY_TYPE_STRING, binary.getId(), "1"));
        retVal.setResource(binary);
        return retVal;
    }

    @Override
    public List<Binary> getAllBinaryResource(DateParam theDate) {
        List<Binary> binaries = new ArrayList<>();

        List<BinaryResource> binaryResources;

        if (theDate == null) {
            binaryResources = binaryResourceRepository.findAll();
        } else {
            binaryResources = binaryResourceRepository.findByModifiedOnGreaterThanOrCreatedOnGreaterThan(theDate.getValue(), theDate.getValue());
        }


        for (BinaryResource binaryResource : binaryResources) {
            Binary binary = parser.parseResource(Binary.class, binaryResource.getText());
            binaries.add(binary);
        }
        return binaries;

    }
}
