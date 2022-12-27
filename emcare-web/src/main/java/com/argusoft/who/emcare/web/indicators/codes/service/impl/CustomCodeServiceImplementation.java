package com.argusoft.who.emcare.web.indicators.codes.service.impl;

import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.common.response.Response;
import com.argusoft.who.emcare.web.indicators.codes.CustomCodeRequestDto;
import com.argusoft.who.emcare.web.indicators.codes.entity.EmCareCustomCodeSystem;
import com.argusoft.who.emcare.web.indicators.codes.mapper.CustomCodeMapper;
import com.argusoft.who.emcare.web.indicators.codes.repository.EmCareCustomCodeSystemRepository;
import com.argusoft.who.emcare.web.indicators.codes.service.CustomCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * <h1> Custom Code Service Implementation </h1>
 * <p>
 * We can add, update, and get data of EmCare Custom Code System.
 * </p>
 *
 * @author - jaykalariya
 * @since - 27/12/22  11:42 am
 */
@Service
public class CustomCodeServiceImplementation implements CustomCodeService {

    @Autowired
    EmCareCustomCodeSystemRepository emCareCustomCodeSystemRepository;

    @Override
    public ResponseEntity<Object> createCustomCode(CustomCodeRequestDto customCodeRequestDto) {
        EmCareCustomCodeSystem emCareCustomCodeSystem = emCareCustomCodeSystemRepository.findByCode(customCodeRequestDto.getCode());
        if (emCareCustomCodeSystem != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(customCodeRequestDto.getCode() + " Already Exist", HttpStatus.BAD_REQUEST.value()));
        }

        emCareCustomCodeSystem = emCareCustomCodeSystemRepository.save(CustomCodeMapper.getEmCareCustomCodeSystem(customCodeRequestDto));
        return ResponseEntity.status(HttpStatus.OK).body(emCareCustomCodeSystem);
    }

    @Override
    public ResponseEntity<Object> updateCustomCodeSystem(CustomCodeRequestDto customCodeRequestDto) {

        EmCareCustomCodeSystem emCareCustomCodeSystem = emCareCustomCodeSystemRepository.findByCodeAndCodeIdNot(customCodeRequestDto.getCode(), customCodeRequestDto.getCodeId());
        if (emCareCustomCodeSystem != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(customCodeRequestDto.getCode() + " Already Exist", HttpStatus.BAD_REQUEST.value()));
        }

        emCareCustomCodeSystem = emCareCustomCodeSystemRepository.save(CustomCodeMapper.getEmCareCustomCodeSystem(customCodeRequestDto));
        return ResponseEntity.status(HttpStatus.OK).body(emCareCustomCodeSystem);
    }

    @Override
    public ResponseEntity<Object> getAllCustomCodeSystem() {
        List<EmCareCustomCodeSystem> emCareCustomCodeSystems = emCareCustomCodeSystemRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(emCareCustomCodeSystems);
    }

    @Override
    public ResponseEntity<Object> getCustomCodeById(Long codeId) {
        Optional<EmCareCustomCodeSystem> emCareCustomCodeSystem = emCareCustomCodeSystemRepository.findById(codeId);
        if (emCareCustomCodeSystem.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(" Code is Not Found", HttpStatus.BAD_REQUEST.value()));

        }
        return ResponseEntity.status(HttpStatus.OK).body(emCareCustomCodeSystem.get());
    }

    @Override
    public PageDto getCustomCodeWithPagination(Integer pageNo, String searchString) {
        Sort sort = Sort.by("createdOn").descending();
        Pageable page = PageRequest.of(pageNo, CommonConstant.PAGE_SIZE, sort);
        Long totalCount;
        Page<EmCareCustomCodeSystem> emCareCustomCodeSystems;
        if (searchString != null && !searchString.isEmpty()) {
            totalCount = (long) emCareCustomCodeSystemRepository
                    .findByCodeContainingIgnoreCaseOrCodeDescriptionContainingIgnoreCase(
                            searchString,
                            searchString
                    ).size();
            emCareCustomCodeSystems = emCareCustomCodeSystemRepository
                    .findByCodeContainingIgnoreCaseOrCodeDescriptionContainingIgnoreCase(
                            searchString,
                            searchString,
                            page);
        } else {
            totalCount = emCareCustomCodeSystemRepository.count();
            emCareCustomCodeSystems = emCareCustomCodeSystemRepository.findAll(page);
        }
        PageDto pageDto = new PageDto();
        pageDto.setList(emCareCustomCodeSystems.getContent());
        pageDto.setTotalCount(totalCount);
        return pageDto;
    }
}
