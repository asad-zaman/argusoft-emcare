package com.argusoft.who.emcare.web.indicators.indicator.mapper;

import com.argusoft.who.emcare.web.indicators.indicator.dto.IndicatorDto;
import com.argusoft.who.emcare.web.indicators.indicator.dto.IndicatorEquationDto;
import com.argusoft.who.emcare.web.indicators.indicator.entity.Indicator;
import com.argusoft.who.emcare.web.indicators.indicator.entity.IndicatorEquation;

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

    public static Indicator getIndicator(IndicatorDto indicatorDto) {
        Indicator indicator = new Indicator();
        indicator.setIndicatorId(indicatorDto.getIndicatorId());
        indicator.setIndicatorCode(indicatorDto.getIndicatorCode());
        indicator.setIndicatorName(indicatorDto.getIndicatorName());
        indicator.setDescription(indicatorDto.getDescription());
        indicator.setEquations(getIndicatorEquationList(indicatorDto.getEquations(), indicator));
        return indicator;
    }

    public static List<IndicatorEquation> getIndicatorEquationList(List<IndicatorEquationDto> indicatorEquationDtos, Indicator indicator) {
        List<IndicatorEquation> equations = new ArrayList<>();
        for (IndicatorEquationDto indicatorEquationDto : indicatorEquationDtos) {
            equations.add(getIndicatorEquation(indicatorEquationDto, indicator));
        }
        return equations;
    }

    public static IndicatorEquation getIndicatorEquation(IndicatorEquationDto indicatorEquationDto, Indicator indicator) {
        IndicatorEquation indicatorEquation = new IndicatorEquation();
        indicatorEquation.setEquationId(indicatorEquationDto.getEquationId());
        indicatorEquation.setIndicator(indicator);
        indicatorEquation.setCodeId(indicatorEquationDto.getCodeId());
        indicatorEquation.setCode(indicatorEquationDto.getCode());
        indicatorEquation.setCondition(indicatorEquationDto.getCondition());
        indicatorEquation.setValue(indicatorEquationDto.getValue());
        indicatorEquation.setValueType(indicatorEquationDto.getValueType());
        return indicatorEquation;
    }
}
