package com.argusoft.who.emcare.web.indicators.indicator.service.impl;

import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.common.response.Response;
import com.argusoft.who.emcare.web.fhir.dao.ObservationCustomResourceRepository;
import com.argusoft.who.emcare.web.fhir.dao.ObservationResourceRepository;
import com.argusoft.who.emcare.web.fhir.dto.FacilityDto;
import com.argusoft.who.emcare.web.fhir.service.LocationResourceService;
import com.argusoft.who.emcare.web.indicators.indicator.dto.IndicatorDto;
import com.argusoft.who.emcare.web.indicators.indicator.dto.IndicatorFilterDto;
import com.argusoft.who.emcare.web.indicators.indicator.entity.Indicator;
import com.argusoft.who.emcare.web.indicators.indicator.entity.IndicatorDenominatorEquation;
import com.argusoft.who.emcare.web.indicators.indicator.entity.IndicatorNumeratorEquation;
import com.argusoft.who.emcare.web.indicators.indicator.mapper.IndicatorMapper;
import com.argusoft.who.emcare.web.indicators.indicator.query_builder.IndicatorQueryBuilder;
import com.argusoft.who.emcare.web.indicators.indicator.repository.IndicatorDenominatorEquationRepository;
import com.argusoft.who.emcare.web.indicators.indicator.repository.IndicatorNumeratorEquationRepository;
import com.argusoft.who.emcare.web.indicators.indicator.repository.IndicatorRepository;
import com.argusoft.who.emcare.web.indicators.indicator.service.IndicatorService;
import com.argusoft.who.emcare.web.user.service.UserService;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
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

import javax.transaction.Transactional;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    LocationResourceService locationResourceService;

    @Autowired
    UserService userService;

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
        if (!indicator.getNumeratorEquation().isEmpty()) {
            indicatorNumeratorEquationRepository.saveAll(indicator.getNumeratorEquation());
        }
        if (!indicator.getDenominatorEquation().isEmpty()) {
            indicatorDenominatorEquationRepository.saveAll(indicator.getDenominatorEquation());
        }
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
            totalCount = (long) indicatorRepository.findByIndicatorCodeContainingIgnoreCaseOrIndicatorNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(searchText, searchText, searchText).size();
            indicators = indicatorRepository.findByIndicatorCodeContainingIgnoreCaseOrIndicatorNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(searchText, searchText, searchText, page);
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
            IndicatorFilterDto indicatorFilterDto = new IndicatorFilterDto();
            indicatorFilterDto.setAge(indicator.getAge());
            indicatorFilterDto.setGender(indicator.getGender());
            if (indicator.getDisplayType().equalsIgnoreCase(CommonConstant.INDICATOR_DISPLAY_TYPE_COUNT)) {
                if (Boolean.TRUE.equals(indicator.getIsQueryConfigure())) {
                    getCountByDirectQuery(indicator, responseList, indicatorFilterDto, false);
                } else {
                    getCountIndicatorValue(indicator, numerator, denominator, responseList, indicatorFilterDto, false);
                }
            }
        }
        responseList.sort((ind1, ind2) -> ind2.get(CommonConstant.INDICATOR_VALUE).toString().compareTo(ind1.get(CommonConstant.INDICATOR_VALUE).toString()));
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @Override
    public ResponseEntity<Object> getIndicatorFilteredCompileValue(IndicatorFilterDto indicatorFilterDto) {
        Optional<Indicator> optionalIndicator = indicatorRepository.findById(indicatorFilterDto.getIndicatorId());
        if (optionalIndicator.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("Indicator Not Found", HttpStatus.BAD_REQUEST.value()));
        }
        Indicator indicator = optionalIndicator.get();
        List<Map<String, Object>> responseList = new ArrayList<>();
        Map<String, Long> numerator = new HashMap<>();
        Map<String, Long> denominator = new HashMap<>();
        if (indicator.getDisplayType().equalsIgnoreCase(CommonConstant.INDICATOR_DISPLAY_TYPE_COUNT)) {
            if (Boolean.TRUE.equals(indicator.getIsQueryConfigure())) {
                getCountByDirectQuery(indicator, responseList, indicatorFilterDto, true);
            } else {
                getCountIndicatorValue(indicator, numerator, denominator, responseList, indicatorFilterDto, true);
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    private void getCountIndicatorValue(Indicator indicator, final Map<String, Long> numerator, final Map<String, Long> denominator, final List<Map<String, Object>> responseList, IndicatorFilterDto indicatorFilterDto, Boolean isFilter) {
        List<String> facilityIds;
        if (Boolean.FALSE.equals(isFilter)) {
            facilityIds = userService.getCurrentUserFacility();
            if (facilityIds.isEmpty() || Objects.isNull(facilityIds) || Objects.isNull(facilityIds.get(0))) {
                facilityIds = locationResourceService.getActiveFacility().stream().map(FacilityDto::getFacilityId).collect(Collectors.toList());
            }
        } else {
            facilityIds = indicatorFilterDto.getFacilityIds();
        }
        for (IndicatorNumeratorEquation indicatorNumeratorEquation : indicator.getNumeratorEquation()) {
            String query = indicatorQueryBuilder.getQueryForIndicatorNumeratorEquation(indicatorNumeratorEquation, getCommaSepratedFacilityIds(facilityIds), indicator, indicatorFilterDto);
            List<Map<String, Object>> observationResources = observationCustomResourceRepository.findByPublished(query);
            numerator.put(indicatorNumeratorEquation.getEqIdentifier(), (long) observationResources.size());
        }

        for (IndicatorDenominatorEquation indicatorDenominatorEquation : indicator.getDenominatorEquation()) {
            String query = indicatorQueryBuilder.getQueryForIndicatorDenominatorEquation(indicatorDenominatorEquation, getCommaSepratedFacilityIds(facilityIds), indicator, indicatorFilterDto);
            List<Map<String, Object>> observationResources = observationCustomResourceRepository.findByPublished(query);
            denominator.put(indicatorDenominatorEquation.getEqIdentifier(), (long) observationResources.size());
        }

        Integer numeratorResult = replaceValueToEquationAndResolve(indicator.getNumeratorIndicatorEquation(), numerator);
        Integer denominatorResult = replaceValueToEquationAndResolve(indicator.getDenominatorIndicatorEquation(), denominator);
        if (indicator.getDenominatorEquation().size() == 1 && indicator.getDenominatorEquation().get(0).getCode().equalsIgnoreCase(CommonConstant.ALL_CODE)) {
            denominatorResult = denominatorResult * indicator.getNumeratorEquation().size();
        }
        Double finalValue = (numeratorResult.doubleValue() / denominatorResult.doubleValue()) * 100;

        if (finalValue.isInfinite() || finalValue.isNaN()) {
            finalValue = 0D;
        }
        Map<String, Object> stringObjectMap = new HashMap<>();
        DecimalFormat df = new DecimalFormat("0.0");
        stringObjectMap.put("indicatorCode", indicator.getIndicatorCode());
        stringObjectMap.put("indicatorId", indicator.getIndicatorId());
        stringObjectMap.put("age", indicatorFilterDto.getAge());
        stringObjectMap.put("facilityIds", indicatorFilterDto.getFacilityIds());
        stringObjectMap.put("gender", indicatorFilterDto.getGender());
        stringObjectMap.put("startDate", indicatorFilterDto.getStartDate());
        stringObjectMap.put("endDate", indicatorFilterDto.getEndDate());
        stringObjectMap.put("indicatorName", indicator.getIndicatorName());
        stringObjectMap.put("indicatorType", indicator.getDisplayType());
        stringObjectMap.put("colorSchema", indicator.getColourSchema());
        stringObjectMap.put(CommonConstant.INDICATOR_VALUE, df.format(finalValue));
        responseList.add(stringObjectMap);
        responseList.sort((ind1, ind2) -> ind2.get(CommonConstant.INDICATOR_VALUE).toString().compareTo(ind1.get(CommonConstant.INDICATOR_VALUE).toString()));
    }

    private void getCountByDirectQuery(Indicator indicator,
                                       final List<Map<String, Object>> responseList,
                                       IndicatorFilterDto indicatorFilterDto,
                                       Boolean isFilter) {
        List<String> facilityIds;
        if (Boolean.FALSE.equals(isFilter)) {
            facilityIds = userService.getCurrentUserFacility();
            if (facilityIds.isEmpty() || Objects.isNull(facilityIds) || Objects.isNull(facilityIds.get(0))) {
                facilityIds = locationResourceService.getActiveFacility().stream().map(FacilityDto::getFacilityId).collect(Collectors.toList());
            }
        } else {
            facilityIds = indicatorFilterDto.getFacilityIds();
        }
        String facilityId = getCommaSepratedFacilityIdsWithFullString(facilityIds);
        String query = indicatorQueryBuilder.changeQueryBasedOnFilterValueReplace(facilityId, indicator, indicatorFilterDto);
        List<Map<String, Object>> observationResources = observationCustomResourceRepository.findByPublished(query);

        Map<String, Object> stringObjectMap = new HashMap<>();
        DecimalFormat df = new DecimalFormat("0.0");
        stringObjectMap.put("indicatorCode", indicator.getIndicatorCode());
        stringObjectMap.put("indicatorId", indicator.getIndicatorId());
        stringObjectMap.put("age", indicatorFilterDto.getAge());
        stringObjectMap.put("facilityIds", indicatorFilterDto.getFacilityIds());
        stringObjectMap.put("gender", indicatorFilterDto.getGender());
        stringObjectMap.put("startDate", indicatorFilterDto.getStartDate());
        stringObjectMap.put("endDate", indicatorFilterDto.getEndDate());
        stringObjectMap.put("indicatorName", indicator.getIndicatorName());
        stringObjectMap.put("indicatorType", indicator.getDisplayType());
        stringObjectMap.put("colorSchema", indicator.getColourSchema());
        stringObjectMap.put(CommonConstant.INDICATOR_VALUE, observationResources.size() > 0 ? df.format(observationResources.get(0).get("finalvalue")) : df.format(0));
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
        int result = 0;
        try {
            Expression expression = new ExpressionBuilder(equation).build();
            result = (int) expression.evaluate();
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.getLocalizedMessage());
        }
        return result;
    }

    public String getCommaSepratedFacilityIds(List<String> facilityIds) {
        if (facilityIds == null || facilityIds.isEmpty()) {
            return null;
        } else {
            return String.join("','", facilityIds);
        }
    }

    public String getCommaSepratedFacilityIdsWithFullString(List<String> facilityIds) {
        if (facilityIds == null || facilityIds.isEmpty()) {
            return null;
        } else {
            return "'" + String.join("','", facilityIds) + "'";
        }
    }

    @Override
    public ResponseEntity<Object> getIndicatorBarChartData(IndicatorFilterDto indicatorFilterDto) {
        Optional<Indicator> optionalIndicator = indicatorRepository.findById(indicatorFilterDto.getIndicatorId());
        if (optionalIndicator.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("Indicator Not Found", HttpStatus.BAD_REQUEST.value()));
        }
        Indicator indicator = optionalIndicator.get();
        List<Map<String, Object>> responseList = new ArrayList<>();
        Map<String, List<Map<String, Object>>> numeratorChart = new HashMap<>();
        Map<String, List<Map<String, Object>>> denominatorChart = new HashMap<>();
            if (Boolean.TRUE.equals(indicator.getIsQueryConfigure())) {
                getChartDataByDirectQuery(indicator, responseList, indicatorFilterDto, true);
            } else {
                getChartIndicatorValue(indicator, numeratorChart, denominatorChart, responseList, indicatorFilterDto, true);
            }
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @Override
    @Transactional
    public ResponseEntity<Object> getChartIndicatorsCompileValue(List<Long> indicatorIds) {
        List<Indicator> indicators = indicatorRepository.findAll();

        List<Map<String, Object>> responseList = new ArrayList<>();
        for (Indicator indicator : indicators) {
            Map<String, List<Map<String, Object>>> numeratorChart = new HashMap<>();
            Map<String, List<Map<String, Object>>> denominatorChart = new HashMap<>();
            IndicatorFilterDto indicatorFilterDto = new IndicatorFilterDto();
            indicatorFilterDto.setAge(indicator.getAge());
            indicatorFilterDto.setGender(indicator.getGender());
            if (indicator.getDisplayType().equalsIgnoreCase(CommonConstant.INDICATOR_DISPLAY_TYPE_COUNT)) {
                if (Boolean.TRUE.equals(indicator.getIsQueryConfigure())) {
                    getChartDataByDirectQuery(indicator, responseList, indicatorFilterDto, false);
                } else {
                    getChartIndicatorValue(indicator, numeratorChart, denominatorChart, responseList, indicatorFilterDto, false);
                }
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    private void getChartIndicatorValue(Indicator indicator, final Map<String, List<Map<String, Object>>> numeratorChart, final Map<String, List<Map<String, Object>>> denominatorChart, final List<Map<String, Object>> responseList, IndicatorFilterDto indicatorFilterDto, Boolean isFilter) {
        List<String> facilityIds;

        if (Boolean.FALSE.equals(isFilter)) {
            facilityIds = userService.getCurrentUserFacility();
            if (facilityIds.isEmpty() || Objects.isNull(facilityIds) || Objects.isNull(facilityIds.get(0))) {
                facilityIds = locationResourceService.getActiveFacility().stream().map(FacilityDto::getFacilityId).collect(Collectors.toList());
            }
        } else {
            facilityIds = indicatorFilterDto.getFacilityIds();
        }
        for (IndicatorNumeratorEquation indicatorNumeratorEquation : indicator.getNumeratorEquation()) {
            String query = indicatorQueryBuilder.getQueryForIndicatorNumeratorEquation(indicatorNumeratorEquation, getCommaSepratedFacilityIds(facilityIds), indicator, indicatorFilterDto);
            List<Map<String, Object>> observationResources = observationCustomResourceRepository.findByPublished(query);
            numeratorChart.put(indicatorNumeratorEquation.getEqIdentifier(), observationResources);
        }

        for (IndicatorDenominatorEquation indicatorDenominatorEquation : indicator.getDenominatorEquation()) {
            String query = indicatorQueryBuilder.getQueryForIndicatorDenominatorEquation(indicatorDenominatorEquation, getCommaSepratedFacilityIds(facilityIds), indicator, indicatorFilterDto);
            List<Map<String, Object>> observationResources = observationCustomResourceRepository.findByPublished(query);
            denominatorChart.put(indicatorDenominatorEquation.getEqIdentifier(), observationResources);
        }

        Map<String, Object> chartData = new HashMap<>();;
        chartData.put(CommonConstant.NUMERATOR_DATA,numeratorChart);
        chartData.put(CommonConstant.DENOMINATOR_DATA,denominatorChart);

        Map<String, Object> stringObjectMap = new HashMap<>();
        DecimalFormat df = new DecimalFormat("0.0");
        stringObjectMap.put("indicatorCode", indicator.getIndicatorCode());
        stringObjectMap.put("indicatorId", indicator.getIndicatorId());
        stringObjectMap.put("age", indicatorFilterDto.getAge());
        stringObjectMap.put("facilityIds", indicatorFilterDto.getFacilityIds());
        stringObjectMap.put("gender", indicatorFilterDto.getGender());
        stringObjectMap.put("startDate", indicatorFilterDto.getStartDate());
        stringObjectMap.put("endDate", indicatorFilterDto.getEndDate());
        stringObjectMap.put("indicatorName", indicator.getIndicatorName());
        stringObjectMap.put("indicatorType", indicator.getDisplayType());
        stringObjectMap.put("colorSchema", indicator.getColourSchema());
        stringObjectMap.put(CommonConstant.CHART_DATA, chartData);
        responseList.add(stringObjectMap);
    }

    private void getChartDataByDirectQuery(Indicator indicator,
                                       final List<Map<String, Object>> responseList,
                                       IndicatorFilterDto indicatorFilterDto,
                                       Boolean isFilter) {
        List<String> facilityIds;
        if (Boolean.FALSE.equals(isFilter)) {
            facilityIds = userService.getCurrentUserFacility();
            if (facilityIds.isEmpty() || Objects.isNull(facilityIds) || Objects.isNull(facilityIds.get(0))) {
                facilityIds = locationResourceService.getActiveFacility().stream().map(FacilityDto::getFacilityId).collect(Collectors.toList());
            }
        } else {
            facilityIds = indicatorFilterDto.getFacilityIds();
        }
        System.out.println(indicator.getIndicatorName());
        String facilityId = getCommaSepratedFacilityIdsWithFullString(facilityIds);
        String query = indicatorQueryBuilder.changeQueryBasedOnFilterValueReplace(facilityId, indicator, indicatorFilterDto);
        System.out.println("====="+query);
        List<Map<String, Object>> observationResources = observationCustomResourceRepository.findByPublished(query);

        Map<String, Object> stringObjectMap = new HashMap<>();
        DecimalFormat df = new DecimalFormat("0.0");
        stringObjectMap.put("indicatorCode", indicator.getIndicatorCode());
        stringObjectMap.put("indicatorId", indicator.getIndicatorId());
        stringObjectMap.put("age", indicatorFilterDto.getAge());
        stringObjectMap.put("facilityIds", indicatorFilterDto.getFacilityIds());
        stringObjectMap.put("gender", indicatorFilterDto.getGender());
        stringObjectMap.put("startDate", indicatorFilterDto.getStartDate());
        stringObjectMap.put("endDate", indicatorFilterDto.getEndDate());
        stringObjectMap.put("indicatorName", indicator.getIndicatorName());
        stringObjectMap.put("indicatorType", indicator.getDisplayType());
        stringObjectMap.put("colorSchema", indicator.getColourSchema());
        stringObjectMap.put(CommonConstant.CHART_DATA, observationResources);
        responseList.add(stringObjectMap);
    }
}
