package com.argusoft.who.emcare.web.indicators.codes.mapper;

import com.argusoft.who.emcare.web.indicators.codes.dto.CustomCodeRequestDto;
import com.argusoft.who.emcare.web.indicators.codes.entity.EmCareCustomCodeSystem;

/**
 * <h1> Add heading here </h1>
 * <p>
 * Add Description here.
 * </p>
 *
 * @author - jaykalariya
 * @since - 27/12/22  11:48 am
 */
public class CustomCodeMapper {

    public static EmCareCustomCodeSystem getEmCareCustomCodeSystem(CustomCodeRequestDto customCodeRequestDto) {
        EmCareCustomCodeSystem emCareCustomCodeSystem = new EmCareCustomCodeSystem();
        emCareCustomCodeSystem.setCodeId(customCodeRequestDto.getCodeId());
        emCareCustomCodeSystem.setCodeDescription(customCodeRequestDto.getCodeDescription());
        emCareCustomCodeSystem.setCode(customCodeRequestDto.getCode());
        return emCareCustomCodeSystem;
    }
}
