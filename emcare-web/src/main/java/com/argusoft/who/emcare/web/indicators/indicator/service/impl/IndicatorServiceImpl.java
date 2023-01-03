package com.argusoft.who.emcare.web.indicators.indicator.service.impl;

import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.fhir.dao.ObservationCustomResourceRepository;
import com.argusoft.who.emcare.web.fhir.dao.ObservationResourceRepository;
import com.argusoft.who.emcare.web.fhir.model.ObservationResource;
import com.argusoft.who.emcare.web.indicators.indicator.dto.IndicatorDto;
import com.argusoft.who.emcare.web.indicators.indicator.entity.Indicator;
import com.argusoft.who.emcare.web.indicators.indicator.entity.IndicatorDenominatorEquation;
import com.argusoft.who.emcare.web.indicators.indicator.entity.IndicatorNumeratorEquation;
import com.argusoft.who.emcare.web.indicators.indicator.mapper.IndicatorMapper;
import com.argusoft.who.emcare.web.indicators.indicator.repository.IndicatorDenominatorEquationRepository;
import com.argusoft.who.emcare.web.indicators.indicator.repository.IndicatorNumeratorEquationRepository;
import com.argusoft.who.emcare.web.indicators.indicator.repository.IndicatorRepository;
import com.argusoft.who.emcare.web.indicators.indicator.service.IndicatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <h1> Indicator Service like Add, Update, and Get.</h1>
 * <p>
 * Apis.
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
    IndicatorNumeratorEquationRepository indicatorNumeratorEquationRepository;

    @Autowired
    IndicatorDenominatorEquationRepository indicatorDenominatorEquationRepository;

    @Autowired
    ObservationResourceRepository observationResourceRepository;

    @Autowired
    ObservationCustomResourceRepository observationCustomResourceRepository;

    /**
     * @param indicatorDto
     * @return
     */
    @Override
    public ResponseEntity<Object> addOrUpdateIndicator(IndicatorDto indicatorDto) {
        Indicator indicator = indicatorRepository.save(IndicatorMapper.getIndicator(indicatorDto));
        indicatorNumeratorEquationRepository.saveAll(indicator.getNumeratorEquation());
        indicatorDenominatorEquationRepository.saveAll(indicator.getDenominatorEquation());
        return ResponseEntity.status(HttpStatus.OK).body(indicator);
    }

    /**
     * @return All Indicator
     */
    @Override
    public ResponseEntity<Object> getAllIndicatorData() {
        return ResponseEntity.status(HttpStatus.OK).body(indicatorRepository.findAll());
    }

    /**
     * @param pageNo     Integer
     * @param searchText String
     * @return PageDto Model
     */
    @Override
    public PageDto getIndicatorDataPage(Integer pageNo, String searchText) {
        Sort sort = Sort.by("createdOn").descending();
        Pageable page = PageRequest.of(pageNo, CommonConstant.PAGE_SIZE, sort);
        Long totalCount;
        Page<Indicator> indicators;
        if (searchText != null && !searchText.isEmpty()) {
            totalCount = (long) indicatorRepository
                    .findByIndicatorCodeContainingIgnoreCaseOrIndicatorNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                            searchText,
                            searchText,
                            searchText
                    ).size();
            indicators = indicatorRepository
                    .findByIndicatorCodeContainingIgnoreCaseOrIndicatorNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                            searchText,
                            searchText,
                            searchText,
                            page);
        } else {
            totalCount = indicatorRepository.count();
            indicators = indicatorRepository.findAll(page);
        }
        PageDto pageDto = new PageDto();
        pageDto.setList(indicators.getContent());
        pageDto.setTotalCount(totalCount);
        return pageDto;
    }

    @Override
    public ResponseEntity<Object> getIndicatorsCompileValue(List<Long> indicatorIds) {
        // System.out.println("=======================");
        // observationCustomResourceRepository.findByPublished();
        // System.out.println("=======================");
        List<Indicator> indicators = indicatorRepository.findAllById(indicatorIds);
        List<Map<String, Object>> responseList = new ArrayList<>();
        for (Indicator indicator : indicators) {
            Map<String, Long> numerator = new HashMap<>();
            Map<String, Long> denominator = new HashMap<>();
            if (indicator.getDisplayType().equals(CommonConstant.INDICATOR_DISPLAY_TYPE_COUNT)) {
                for (IndicatorNumeratorEquation indicatorNumeratorEquation : indicator.getNumeratorEquation()) {
                    List<ObservationResource> observationResources = observationResourceRepository.fetchByCustomCode(
                            indicator.getFacilityId(),
                            indicatorNumeratorEquation.getCode());
                    numerator.put(indicatorNumeratorEquation.getEqIdentifier(), Long.valueOf(observationResources.size()));
                }

                for (IndicatorDenominatorEquation indicatorDenominatorEquation : indicator.getDenominatorEquation()) {
                    List<ObservationResource> observationResources = observationResourceRepository.fetchByCustomCode(
                            indicator.getFacilityId(),
                            indicatorDenominatorEquation.getCode());
                    denominator.put(indicatorDenominatorEquation.getEqIdentifier(), Long.valueOf(observationResources.size()));
                }

                Integer numeratorResult = replaceValueToEquationAndResolve(indicator.getNumeratorIndicatorEquation(), numerator);
                Integer denominatorResult = replaceValueToEquationAndResolve(indicator.getDenominatorIndicatorEquation(), denominator);
                Double finalValue =(numeratorResult.doubleValue()/denominatorResult.doubleValue())*100;
                Map<String, Object> stringObjectMap = new HashMap<>();

                stringObjectMap.put("indicatorCode", indicator.getIndicatorCode());
                stringObjectMap.put("indicatorName", indicator.getIndicatorName());
                stringObjectMap.put("indicatorValue", finalValue);
                responseList.add(stringObjectMap);
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    private Integer replaceValueToEquationAndResolve(String equation, Map<String, Long> data) {
        String eq = equation;
        for (String key : data.keySet()) {
            eq = eq.replace(key, data.get(key).toString());
        }
        return resolveEquation(eq);
    }

    public Integer resolveEquation(String equation) {
        Integer result = 0;
        try {
            ScriptEngineManager mgr = new ScriptEngineManager();
            ScriptEngine engine = mgr.getEngineByName("JavaScript");
            result = (Integer) engine.eval(equation);
        } catch (Exception ex) {
            System.out.println(ex.getLocalizedMessage());
        }
        return result;
    }
}
