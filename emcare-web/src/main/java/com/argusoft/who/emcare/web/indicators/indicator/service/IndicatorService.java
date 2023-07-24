package com.argusoft.who.emcare.web.indicators.indicator.service;

import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.indicators.indicator.dto.IndicatorDto;
import com.argusoft.who.emcare.web.indicators.indicator.dto.IndicatorFilterDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * <h1> Add heading here </h1>
 * <p>
 * Add Description here.
 * </p>
 *
 * @author - jaykalariya
 * @since - 28/12/22  10:38 am
 */
public interface IndicatorService {

    public ResponseEntity<Object> addOrUpdateIndicator(IndicatorDto indicatorDto);

    public ResponseEntity<Object> getAllIndicatorData();

    public ResponseEntity<Object> getIndicatorById(Long indicatorId);

    public PageDto getIndicatorDataPage(Integer pageNo, String searchText);

    public ResponseEntity<Object> getIndicatorsCompileValue(List<Long> indicatorIds);
    public ResponseEntity<Object> getIndicatorFilteredCompileValue(IndicatorFilterDto indicatorFilterDto);

    public ResponseEntity<Object> getChartIndicatorsCompileValue(List<Long> indicatorIds);
    public ResponseEntity<Object> getChartIndicatorsFilteredCompileValue(IndicatorFilterDto indicatorFilterDto);

}
