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

    @Column(name = "facility_id")
    private String facilityId;

    @Column(name = "numerator_indicator_equation")
    private String numeratorIndicatorEquation;

    @Column(name = "denominator_indicator_equation")
    private String denominatorIndicatorEquation;

    @Column(name = "numerator_equation_string", columnDefinition = "TEXT")
    private String numeratorEquationString;

    @Column(name = "denominator_equation_string", columnDefinition = "TEXT")
    private String denominatorEquationString;

    @Column(name = "display_type")
    private String displayType;

    @OneToMany(mappedBy = "numeratorIndicator", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<IndicatorNumeratorEquation> numeratorEquation;

    @OneToMany(mappedBy = "denominatorIndicator", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<IndicatorDenominatorEquation> denominatorEquation;

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

    public List<IndicatorNumeratorEquation> getNumeratorEquation() {
        return numeratorEquation;
    }

    public void setNumeratorEquation(List<IndicatorNumeratorEquation> numeratorEquation) {
        this.numeratorEquation = numeratorEquation;
    }

    public List<IndicatorDenominatorEquation> getDenominatorEquation() {
        return denominatorEquation;
    }

    public void setDenominatorEquation(List<IndicatorDenominatorEquation> denominatorEquation) {
        this.denominatorEquation = denominatorEquation;
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
}
