package com.argusoft.who.emcare.web.indicators.indicator.entity;

import com.argusoft.who.emcare.web.common.model.EntityAuditInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

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
@JsonIgnoreProperties
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

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "numeratorIndicator", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<IndicatorNumeratorEquation> numeratorEquation;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "denominatorIndicator", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<IndicatorDenominatorEquation> denominatorEquation;

    @Column(name = "colour_schema")
    private String colourSchema;

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

    public String getColourSchema() {
        return colourSchema;
    }

    public void setColourSchema(String colourSchema) {
        this.colourSchema = colourSchema;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Indicator)) return false;
        if (!super.equals(o)) return false;
        Indicator indicator = (Indicator) o;
        return Objects.equals(getIndicatorId(), indicator.getIndicatorId()) && Objects.equals(getIndicatorCode(), indicator.getIndicatorCode()) && Objects.equals(getIndicatorName(), indicator.getIndicatorName()) && Objects.equals(getDescription(), indicator.getDescription()) && Objects.equals(getFacilityId(), indicator.getFacilityId()) && Objects.equals(getNumeratorIndicatorEquation(), indicator.getNumeratorIndicatorEquation()) && Objects.equals(getDenominatorIndicatorEquation(), indicator.getDenominatorIndicatorEquation()) && Objects.equals(getNumeratorEquationString(), indicator.getNumeratorEquationString()) && Objects.equals(getDenominatorEquationString(), indicator.getDenominatorEquationString()) && Objects.equals(getDisplayType(), indicator.getDisplayType()) && Objects.equals(getNumeratorEquation(), indicator.getNumeratorEquation()) && Objects.equals(getDenominatorEquation(), indicator.getDenominatorEquation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getIndicatorId(), getIndicatorCode(), getIndicatorName(), getDescription(), getFacilityId(), getNumeratorIndicatorEquation(), getDenominatorIndicatorEquation(), getNumeratorEquationString(), getDenominatorEquationString(), getDisplayType(), getNumeratorEquation(), getDenominatorEquation());
    }
}
