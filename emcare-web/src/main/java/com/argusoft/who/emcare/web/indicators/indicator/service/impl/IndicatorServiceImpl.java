package com.argusoft.who.emcare.web.indicators.indicator.service.impl;

import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.fhir.dao.ObservationCustomResourceRepository;
import com.argusoft.who.emcare.web.fhir.dao.ObservationResourceRepository;
import com.argusoft.who.emcare.web.indicators.indicator.dto.IndicatorDto;
import com.argusoft.who.emcare.web.indicators.indicator.entity.Indicator;
import com.argusoft.who.emcare.web.indicators.indicator.entity.IndicatorDenominatorEquation;
import com.argusoft.who.emcare.web.indicators.indicator.entity.IndicatorNumeratorEquation;
import com.argusoft.who.emcare.web.indicators.indicator.mapper.IndicatorMapper;
import com.argusoft.who.emcare.web.indicators.indicator.query_builder.IndicatorQueryBuilder;
import com.argusoft.who.emcare.web.indicators.indicator.repository.IndicatorDenominatorEquationRepository;
import com.argusoft.who.emcare.web.indicators.indicator.repository.IndicatorNumeratorEquationRepository;
import com.argusoft.who.emcare.web.indicators.indicator.repository.IndicatorRepository;
import com.argusoft.who.emcare.web.indicators.indicator.service.IndicatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import javax.transaction.Transactional;
import java.util.*;

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

    @Autowired
    IndicatorQueryBuilder indicatorQueryBuilder;

    Logger logger = LoggerFactory.getLogger(IndicatorServiceImpl.class);


    /**
     * @param indicatorDto
     * @return
     */
    @Transactional
    @Override
    public ResponseEntity<Object> addOrUpdateIndicator(IndicatorDto indicatorDto) {
        if (Objects.nonNull(indicatorDto.getIndicatorId())) {
            indicatorNumeratorEquationRepository.deleteByNumeratorIndicator(indicatorDto.getIndicatorId());
            indicatorDenominatorEquationRepository.deleteByDenominatorIndicator(indicatorDto.getIndicatorId());
        }
        Indicator indicator = indicatorRepository.save(IndicatorMapper.getIndicator(indicatorDto));
        indicatorNumeratorEquationRepository.saveAll(indicator.getNumeratorEquation());
        indicatorDenominatorEquationRepository.saveAll(indicator.getDenominatorEquation());
        return ResponseEntity.status(HttpStatus.OK).body(indicator);
    }

    /**
     * @return All Indicator
     */
    @Override
    @Transactional
    public ResponseEntity<Object> getAllIndicatorData() {
        return ResponseEntity.status(HttpStatus.OK).body(indicatorRepository.findAll());
    }

    @Override
    @Transactional
    public ResponseEntity<Object> getIndicatorById(Long indicatorId) {
        return ResponseEntity.status(HttpStatus.OK).body(indicatorRepository.findById(indicatorId));
    }

    /**
     * @param pageNo     Integer
     * @param searchText String
     * @return PageDto Model
     */
    @Override
    @Transactional
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
    @Transactional
    public ResponseEntity<Object> getIndicatorsCompileValue(List<Long> indicatorIds) {
        List<Indicator> indicators = indicatorRepository.findAll();

        List<Map<String, Object>> responseList = new ArrayList<>();
        for (Indicator indicator : indicators) {
            Map<String, Long> numerator = new HashMap<>();
            Map<String, Long> denominator = new HashMap<>();
            if (indicator.getDisplayType().equalsIgnoreCase(CommonConstant.INDICATOR_DISPLAY_TYPE_COUNT)) {
                getCountIndicatorValue(indicator, numerator, denominator, responseList);
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    private void getCountIndicatorValue(Indicator indicator, final Map<String, Long> numerator, final Map<String, Long> denominator, final List<Map<String, Object>> responseList) {
        for (IndicatorNumeratorEquation indicatorNumeratorEquation : indicator.getNumeratorEquation()) {

            String query = indicatorQueryBuilder.getQueryForIndicatorNumeratorEquation(indicatorNumeratorEquation, indicator.getFacilityId());
            List<Map<String, Object>> observationResources = observationCustomResourceRepository.findByPublished(query);
            numerator.put(indicatorNumeratorEquation.getEqIdentifier(), (long) observationResources.size());
        }

        for (IndicatorDenominatorEquation indicatorDenominatorEquation : indicator.getDenominatorEquation()) {
            String query = indicatorQueryBuilder.getQueryForIndicatorDenominatorEquation(indicatorDenominatorEquation, indicator.getFacilityId());
            List<Map<String, Object>> observationResources = observationCustomResourceRepository.findByPublished(query);
            denominator.put(indicatorDenominatorEquation.getEqIdentifier(), (long) observationResources.size());
        }

        Integer numeratorResult = replaceValueToEquationAndResolve(indicator.getNumeratorIndicatorEquation(), numerator);
        Integer denominatorResult = replaceValueToEquationAndResolve(indicator.getDenominatorIndicatorEquation(), denominator);
        Double finalValue = (numeratorResult.doubleValue() / denominatorResult.doubleValue()) * 100;

        if (finalValue.isInfinite() || finalValue.isNaN()) {
            finalValue = 0D;
        }
        Map<String, Object> stringObjectMap = new HashMap<>();

        stringObjectMap.put("indicatorCode", indicator.getIndicatorCode());
        stringObjectMap.put("indicatorName", indicator.getIndicatorName());
        stringObjectMap.put("IndicatorType", indicator.getDisplayType());
        stringObjectMap.put("indicatorValue", finalValue.intValue());
        responseList.add(stringObjectMap);
    }

    private Integer replaceValueToEquationAndResolve(String equation, Map<String, Long> data) {
        String eq = equation;
        for (Map.Entry<String, Long> key : data.entrySet()) {
            eq = eq.replace(key.getKey(), data.get(key.getKey()).toString());
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
            logger.error(ex.getLocalizedMessage());
        }
        return result;
    }
}
