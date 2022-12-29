package com.argusoft.who.emcare.web.indicators.indicator.controller;

import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.indicators.indicator.dto.IndicatorDto;
import com.argusoft.who.emcare.web.indicators.indicator.service.IndicatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;

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
        return indicatorService.addNewIndicator(indicatorDto);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllIndicator() {
        return indicatorService.getAllIndicatorData();
    }

    @GetMapping("/page")
    public PageDto getIndicatorPage(@RequestParam(value = "pageNo") Integer pageNo,
                                    @Nullable @RequestParam(value = "search", required = false) String searchString) {
        return indicatorService.getIndicatorDataPage(pageNo, searchString);
    }
}
