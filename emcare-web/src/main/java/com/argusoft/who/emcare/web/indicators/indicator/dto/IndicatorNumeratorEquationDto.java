package com.argusoft.who.emcare.web.indicators.indicator.dto;

/**
 * <h1> Add heading here </h1>
 * <p>
 * Add Description here.
 * </p>
 *
 * @author - jaykalariya
 * @since - 28/12/22  12:08 pm
 */
public class IndicatorNumeratorEquationDto {

    private Long numeratorId;
    private Long codeId;
    private String code;
    private String condition;
    private String value;
    private String valueType;
    private String eqIdentifier;


    public Long getNumeratorId() {
        return numeratorId;
    }

    public void setNumeratorId(Long numeratorId) {
        this.numeratorId = numeratorId;
    }

    public Long getCodeId() {
        return codeId;
    }

    public void setCodeId(Long codeId) {
        this.codeId = codeId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String getEqIdentifier() {
        return eqIdentifier;
    }

    public void setEqIdentifier(String eqIdentifier) {
        this.eqIdentifier = eqIdentifier;
    }
}
