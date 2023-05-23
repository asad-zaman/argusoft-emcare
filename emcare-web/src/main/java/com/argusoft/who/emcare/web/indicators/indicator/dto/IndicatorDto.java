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
    private String facilityId;
    private String numeratorIndicatorEquation;
    private String numeratorEquationString;
    private String denominatorEquationString;
    private String denominatorIndicatorEquation;
    private String colourSchema;
    private String age;
    private String gender;

    private String displayType;
    private List<IndicatorNumeratorEquationDto> numeratorEquations;
    private List<IndicatorDenominatorEquationDto> denominatorEquations;

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

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public String getNumeratorIndicatorEquation() {
        return numeratorIndicatorEquation;
    }

    public void setNumeratorIndicatorEquation(String numeratorIndicatorEquation) {
        this.numeratorIndicatorEquation = numeratorIndicatorEquation;
    }

    public String getDenominatorIndicatorEquation() {
        return denominatorIndicatorEquation;
    }

    public void setDenominatorIndicatorEquation(String denominatorIndicatorEquation) {
        this.denominatorIndicatorEquation = denominatorIndicatorEquation;
    }

    public List<IndicatorNumeratorEquationDto> getNumeratorEquations() {
        return numeratorEquations;
    }

    public void setNumeratorEquations(List<IndicatorNumeratorEquationDto> numeratorEquations) {
        this.numeratorEquations = numeratorEquations;
    }

    public List<IndicatorDenominatorEquationDto> getDenominatorEquations() {
        return denominatorEquations;
    }

    public void setDenominatorEquations(List<IndicatorDenominatorEquationDto> denominatorEquations) {
        this.denominatorEquations = denominatorEquations;
    }

    public String getDisplayType() {
        return displayType;
    }

    public void setDisplayType(String displayType) {
        this.displayType = displayType;
    }

    public String getNumeratorEquationString() {
        return numeratorEquationString;
    }

    public void setNumeratorEquationString(String numeratorEquationString) {
        this.numeratorEquationString = numeratorEquationString;
    }

    public String getDenominatorEquationString() {
        return denominatorEquationString;
    }

    public void setDenominatorEquationString(String denominatorEquationString) {
        this.denominatorEquationString = denominatorEquationString;
    }

    public String getColourSchema() {
        return colourSchema;
    }

    public void setColourSchema(String colourSchema) {
        this.colourSchema = colourSchema;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
