package com.argusoft.who.emcare.web.indicators.indicator.dto;

import java.util.List;

/**
 * <h1> Add heading here </h1>
 * <p>
 * Add Description here.
 * </p>
 *
 * @author - jaykalariya
 * @since - 28/12/22  12:07 pm
 */
public class IndicatorDto {

    private Long indicatorId;

    private String indicatorCode;

    private String indicatorName;

    private String description;

    private List<IndicatorEquationDto> equations;

    public Long getIndicatorId() {
        return indicatorId;
    }

    public void setIndicatorId(Long indicatorId) {
        this.indicatorId = indicatorId;
    }

    public String getIndicatorCode() {
        return indicatorCode;
    }

    public void setIndicatorCode(String indicatorCode) {
        this.indicatorCode = indicatorCode;
    }

    public String getIndicatorName() {
        return indicatorName;
    }

    public void setIndicatorName(String indicatorName) {
        this.indicatorName = indicatorName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<IndicatorEquationDto> getEquations() {
        return equations;
    }

    public void setEquations(List<IndicatorEquationDto> equations) {
        this.equations = equations;
    }
}
