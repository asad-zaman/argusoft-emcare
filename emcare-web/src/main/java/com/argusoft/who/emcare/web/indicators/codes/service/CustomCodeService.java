package com.argusoft.who.emcare.web.indicators.codes.service;

import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.indicators.codes.CustomCodeRequestDto;
import org.springframework.http.ResponseEntity;

/**
 * <h1> Add heading here </h1>
 * <p>
 * Add Description here.
 * </p>
 *
 * @author - jaykalariya
 * @since - 27/12/22  11:41 am
 */
public interface CustomCodeService {

    public ResponseEntity<Object> createCustomCode(CustomCodeRequestDto customCodeRequestDto);

    public ResponseEntity<Object> updateCustomCodeSystem(CustomCodeRequestDto customCodeRequestDto);

    public ResponseEntity<Object> getAllCustomCodeSystem();

    public ResponseEntity<Object> getCustomCodeById(Long codeId);

    public PageDto getCustomCodeWithPagination(Integer pageNo, String searchString);
}
