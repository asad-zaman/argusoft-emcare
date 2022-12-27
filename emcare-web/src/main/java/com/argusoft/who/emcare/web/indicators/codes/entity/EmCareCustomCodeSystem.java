package com.argusoft.who.emcare.web.indicators.codes.entity;

import com.argusoft.who.emcare.web.common.model.EntityAuditInfo;

import javax.persistence.*;
import java.io.Serializable;

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
}
