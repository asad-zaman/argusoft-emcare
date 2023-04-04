package com.argusoft.who.emcare.web.indicators.codes.entity;

import com.argusoft.who.emcare.web.common.model.EntityAuditInfo;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * <h1> EmCare Custom Code System </h1>
 * <p>
 * Store all EmCare custom codes .
 * </p>
 *
 * @author - jaykalariya
 * @since - 27/12/22  10:58 am
 */
@Entity
@Table(name = "emcare_custom_code_system")
public class EmCareCustomCodeSystem extends EntityAuditInfo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "code_id", nullable = false)
    private Long codeId;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "code_description")
    private String codeDescription;

    @Column(name = "value_type")
    private String valueType;

    @Column(name = "condition")
    private String [] condition;

    @Column(name = "value")
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

    public String getValueType() { return valueType; }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String [] getCondition() {
        return condition;
    }

    public void setCondition(String [] condition) {
        this.condition = condition;
    }

    public String getValue() { return value; }

    public void setValue(String value) { this.value = value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmCareCustomCodeSystem)) return false;
        if (!super.equals(o)) return false;
        EmCareCustomCodeSystem that = (EmCareCustomCodeSystem) o;
        return Objects.equals(getCodeId(), that.getCodeId()) && Objects.equals(getCode(), that.getCode()) && Objects.equals(getCodeDescription(), that.getCodeDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getCodeId(), getCode(), getCodeDescription());
    }
}
