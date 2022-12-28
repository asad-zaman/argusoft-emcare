package com.argusoft.who.emcare.web.indicators.indicator.service.impl;

import com.argusoft.who.emcare.web.indicators.indicator.dto.IndicatorDto;
import com.argusoft.who.emcare.web.indicators.indicator.entity.Indicator;
import com.argusoft.who.emcare.web.indicators.indicator.mapper.IndicatorMapper;
import com.argusoft.who.emcare.web.indicators.indicator.repository.IndicatorEquationRepository;
import com.argusoft.who.emcare.web.indicators.indicator.repository.IndicatorRepository;
import com.argusoft.who.emcare.web.indicators.indicator.service.IndicatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * <h1> Add heading here </h1>
 * <p>
 * Add Description here.
 * </p>
 *
 * @author - jaykalariya
 * @since - 28/12/22  10:43 am
 */
@Service
public class IndicatorServiceImpl implements IndicatorService {

    @Autowired
    IndicatorRepository indicatorRepository;

    @Autowired
    IndicatorEquationRepository indicatorEquationRepository;

    @Override
    public ResponseEntity<Object> addNewIndicator(IndicatorDto indicatorDto) {
        Indicator indicator = indicatorRepository.save(IndicatorMapper.getIndicator(indicatorDto));
        indicatorEquationRepository.saveAll(indicator.getEquations());
        return ResponseEntity.status(HttpStatus.OK).body(indicator);
    }
}
