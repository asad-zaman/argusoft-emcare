package com.argusoft.who.emcare.web.indicators.indicator.controller;

import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.indicators.indicator.dto.IndicatorDto;
import com.argusoft.who.emcare.web.indicators.indicator.dto.IndicatorFilterDto;
import com.argusoft.who.emcare.web.indicators.indicator.service.IndicatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
import java.util.List;

/**
 * <h1> Add heading here </h1>
 * <p>
 * Add Description here.
 * </p>
 *
 * @author - jaykalariya
 * @since - 28/12/22  10:36 am
 */
@CrossOrigin(origins = "**")
@RestController
@RequestMapping("/api/indicator")
public class IndicatorController {

    @Autowired
    IndicatorService indicatorService;

    @PostMapping("/add")
    public ResponseEntity<Object> addNewCodeSystem(@RequestBody IndicatorDto indicatorDto) {
        return indicatorService.addOrUpdateIndicator(indicatorDto);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllIndicator() {
        return indicatorService.getAllIndicatorData();
    }

    @GetMapping("/{indicatorId}")
    public ResponseEntity<Object> getAllIndicator(@PathVariable(value = "indicatorId") Long indicatorId) {
        return indicatorService.getIndicatorById(indicatorId);
    }

    @GetMapping("/page")
    public PageDto getIndicatorPage(@RequestParam(value = "pageNo") Integer pageNo,
                                    @Nullable @RequestParam(value = "search", required = false) String searchString) {
        return indicatorService.getIndicatorDataPage(pageNo, searchString);
    }

    @PostMapping("/compile/value")
    public ResponseEntity<Object> getIndicatorCompileValue(@RequestBody List<Long> indicatorIds) {
        return indicatorService.getIndicatorsCompileValue(indicatorIds);
    }

    @PostMapping("/filter/value")
    public ResponseEntity<Object> getIndicatorFilteredCompileValue(@RequestBody IndicatorFilterDto indicatorFilterDto) {
        return indicatorService.getIndicatorFilteredCompileValue(indicatorFilterDto);
    }

    @PostMapping("/chart/value")
    public ResponseEntity<Object> getChartIndicatorsCompileValue(@RequestBody List<Long> indicatorIds) {
        return indicatorService.getChartIndicatorsCompileValue(indicatorIds);
    }

    @PostMapping("/chart")
    public ResponseEntity<Object> getIndicatorBarChartData(@RequestBody IndicatorFilterDto indicatorFilterDto) {
        return indicatorService.getIndicatorBarChartData(indicatorFilterDto);
    }
}
