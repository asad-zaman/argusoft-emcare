package com.argusoft.who.emcare.web.indicators.indicator.entity;

import com.argusoft.who.emcare.web.common.model.EntityAuditInfo;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * <h1> Add heading here </h1>
 * <p>
 * Add Description here.
 * </p>
 *
 * @author - jaykalariya
 * @since - 28/12/22  11:39 am
 */
@Entity
@Table(name = "indicator")
public class Indicator extends EntityAuditInfo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "indicator_id", nullable = false)
    private Long indicatorId;

    @Column(name = "indicator_code")
    private String indicatorCode;

    @Column(name = "indicator_name")
    private String indicatorName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "indicator", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<IndicatorEquation> equations;


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

    public List<IndicatorEquation> getEquations() {
        return equations;
    }

    public void setEquations(List<IndicatorEquation> equations) {
        this.equations = equations;
    }
}
