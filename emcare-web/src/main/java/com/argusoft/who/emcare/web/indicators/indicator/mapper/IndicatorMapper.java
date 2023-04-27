package com.argusoft.who.emcare.web.indicators.indicator.mapper;

import com.argusoft.who.emcare.web.indicators.indicator.dto.IndicatorDenominatorEquationDto;
import com.argusoft.who.emcare.web.indicators.indicator.dto.IndicatorDto;
import com.argusoft.who.emcare.web.indicators.indicator.dto.IndicatorNumeratorEquationDto;
import com.argusoft.who.emcare.web.indicators.indicator.entity.Indicator;
import com.argusoft.who.emcare.web.indicators.indicator.entity.IndicatorDenominatorEquation;
import com.argusoft.who.emcare.web.indicators.indicator.entity.IndicatorNumeratorEquation;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1> Add heading here </h1>
 * <p>
 * Add Description here.
 * </p>
 *
 * @author - jaykalariya
 * @since - 28/12/22  12:21 pm
 */
public class IndicatorMapper {

    private IndicatorMapper() {
    }

    public static Indicator getIndicator(IndicatorDto indicatorDto) {
        Indicator indicator = new Indicator();
        indicator.setIndicatorId(indicatorDto.getIndicatorId());
        indicator.setIndicatorCode(indicatorDto.getIndicatorCode());
        indicator.setIndicatorName(indicatorDto.getIndicatorName());
        indicator.setDescription(indicatorDto.getDescription());
        indicator.setFacilityId(indicatorDto.getFacilityId());
        indicator.setNumeratorIndicatorEquation(indicatorDto.getNumeratorIndicatorEquation());
        indicator.setDenominatorIndicatorEquation(indicatorDto.getDenominatorIndicatorEquation());
        indicator.setDenominatorEquationString(indicatorDto.getDenominatorEquationString());
        indicator.setNumeratorEquationString(indicatorDto.getNumeratorEquationString());
        indicator.setDisplayType(indicatorDto.getDisplayType());
        indicator.setNumeratorEquation(getIndicatorEquationList(indicatorDto.getNumeratorEquations(), indicator));
        indicator.setDenominatorEquation(getDenominatorIndicatorEquationList(indicatorDto.getDenominatorEquations(), indicator));
        indicator.setColourSchema(indicatorDto.getColourSchema());
        indicator.setAge(indicatorDto.getAge());
        indicator.setGender(indicatorDto.getGender());
        return indicator;
    }

    public static List<IndicatorNumeratorEquation> getIndicatorEquationList(List<IndicatorNumeratorEquationDto> indicatorNumeratorEquationDtos, Indicator indicator) {
        List<IndicatorNumeratorEquation> equations = new ArrayList<>();
        for (IndicatorNumeratorEquationDto indicatorNumeratorEquationDto : indicatorNumeratorEquationDtos) {
            equations.add(getNumeratorIndicatorEquation(indicatorNumeratorEquationDto, indicator));
        }
        return equations;
    }

    public static List<IndicatorDenominatorEquation> getDenominatorIndicatorEquationList(List<IndicatorDenominatorEquationDto> indicatorDenominatorEquationDtos, Indicator indicator) {
        List<IndicatorDenominatorEquation> equations = new ArrayList<>();
        for (IndicatorDenominatorEquationDto indicatorDenominatorEquationDto : indicatorDenominatorEquationDtos) {
            equations.add(getDenominatorIndicatorEquation(indicatorDenominatorEquationDto, indicator));
        }
        return equations;
    }

    public static IndicatorNumeratorEquation getNumeratorIndicatorEquation(IndicatorNumeratorEquationDto indicatorNumeratorEquationDto, Indicator indicator) {
        IndicatorNumeratorEquation indicatorNumeratorEquation = new IndicatorNumeratorEquation();
        indicatorNumeratorEquation.setNumeratorId(indicatorNumeratorEquationDto.getNumeratorId());
        indicatorNumeratorEquation.setNumeratorIndicator(indicator);
        indicatorNumeratorEquation.setCodeId(indicatorNumeratorEquationDto.getCodeId());
        indicatorNumeratorEquation.setCode(indicatorNumeratorEquationDto.getCode());
        indicatorNumeratorEquation.setCondition(indicatorNumeratorEquationDto.getCondition());
        indicatorNumeratorEquation.setValue(indicatorNumeratorEquationDto.getValue());
        indicatorNumeratorEquation.setValueType(indicatorNumeratorEquationDto.getValueType());
        indicatorNumeratorEquation.setEqIdentifier(indicatorNumeratorEquationDto.getEqIdentifier());
        return indicatorNumeratorEquation;
    }

    public static IndicatorDenominatorEquation getDenominatorIndicatorEquation(IndicatorDenominatorEquationDto indicatorDenominatorEquationDto, Indicator indicator) {
        IndicatorDenominatorEquation indicatorDenominatorEquation = new IndicatorDenominatorEquation();
        indicatorDenominatorEquation.setDenominatorId(indicatorDenominatorEquationDto.getDenominatorId());
        indicatorDenominatorEquation.setDenominatorIndicator(indicator);
        indicatorDenominatorEquation.setCodeId(indicatorDenominatorEquationDto.getCodeId());
        indicatorDenominatorEquation.setCode(indicatorDenominatorEquationDto.getCode());
        indicatorDenominatorEquation.setCondition(indicatorDenominatorEquationDto.getCondition());
        indicatorDenominatorEquation.setValue(indicatorDenominatorEquationDto.getValue());
        indicatorDenominatorEquation.setValueType(indicatorDenominatorEquationDto.getValueType());
        indicatorDenominatorEquation.setEqIdentifier(indicatorDenominatorEquationDto.getEqIdentifier());
        return indicatorDenominatorEquation;
    }
}
