package com.argusoft.who.emcare.web.indicators.codes.dto;

/**
 * <h1> Add heading here </h1>
 * <p>
 * Add Description here.
 * </p>
 *
 * @author - jaykalariya
 * @since - 27/12/22  11:45 am
 */
public class CustomCodeRequestDto {

    private Long codeId;
    private String code;
    private String codeDescription;
    private String valueType;
    private String [] condition;
    private String value;

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

    public String getCodeDescription() {
        return codeDescription;
    }

    public void setCodeDescription(String codeDescription) {
        this.codeDescription = codeDescription;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) { this.valueType = valueType; }

    public String [] getCondition() {
        return condition;
    }

    public void setCondition(String [] condition) {
        this.condition = condition;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
