package com.argusoft.who.emcare.web.indicators.indicator.query_builder;

import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.indicators.indicator.dto.IndicatorFilterDto;
import com.argusoft.who.emcare.web.indicators.indicator.entity.Indicator;
import com.argusoft.who.emcare.web.indicators.indicator.entity.IndicatorDenominatorEquation;
import com.argusoft.who.emcare.web.indicators.indicator.entity.IndicatorNumeratorEquation;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

/**
 * <h1> Query Builder For Indicator </h1>
 * <p>
 * Dynamic query builder based on indicator configuration.
 * </p>
 *
 * @author - jaykalariya
 * @since - 06/01/23  10:12 am
 */
@Component
public class IndicatorQueryBuilder {

    public String getQueryForIndicatorNumeratorEquation(IndicatorNumeratorEquation indicatorNumeratorEquation,
                                                        String facilityId,
                                                        Indicator indicator,
                                                        IndicatorFilterDto indicatorFilterDto) {
        StringBuilder query = new StringBuilder("with custom_code as ");
        query = query.append("(select obr.code as code, emr.resource_id as patient,");
        query = query.append(" obr.modified_on ");
        if (Objects.nonNull(indicatorNumeratorEquation.getValueType())) {
            if (indicatorNumeratorEquation.getValueType().equalsIgnoreCase("Boolean")) {
                query = query.append(", cast(cast(cast(obr.text AS json)->>'" + getTypeValue(indicatorNumeratorEquation.getValueType()) + "' as text) AS "
                    + getTypeKey(indicatorNumeratorEquation.getValueType()) + ") as valueText");
            }
            if (indicatorNumeratorEquation.getValueType().equalsIgnoreCase("Number")) {
                query = query.append(", cast(cast(cast(obr.text AS json)->>'" + getTypeValue(indicatorNumeratorEquation.getValueType()) + "' as INTEGER) AS "
                    + getTypeKey(indicatorNumeratorEquation.getValueType()) + ") as valueText");
            }
        }
        if (Objects.nonNull(indicatorFilterDto.getAge())) {
            query = query.append(" , cast(EXTRACT('year' from age(now(), TO_DATE(cast(cast(emr.text AS json)->>'birthDate' as text),'YYYY-MM-DD'))) * 12 as INTEGER) +\n" +
                "   cast(EXTRACT('mons' from age(now(), TO_DATE(cast(cast(emr.text AS json)->>'birthDate' as text),'YYYY-MM-DD'))) as INTEGER) as age");
        }
        if (Objects.nonNull(indicatorFilterDto.getGender())) {
            query = query.append(" ,emr.gender as gender");
        }
        query = query.append(" from observation_resource as obr left join emcare_resources as emr on obr.subject_id = emr.resource_id ");
        if (facilityId != null && !facilityId.isEmpty()) {
            query = query.append(" where emr.facility_id in ('" + facilityId + "')");
        }
        query = query.append(") select distinct(patient) from custom_code ");
        if (!indicatorNumeratorEquation.getCode().equalsIgnoreCase(CommonConstant.ALL_CODE)) {
            query = query.append("where code = '" + indicatorNumeratorEquation.getCode() + "' ");
        }
        if (Objects.nonNull(indicatorFilterDto.getAge())) {
            query = query.append(" and age " + indicatorFilterDto.getAge());
        }
        if (Objects.nonNull(indicatorFilterDto.getGender())) {
            query = query.append(" and gender = '" + indicatorFilterDto.getGender() + "'");
        }

        if (Objects.nonNull(indicatorFilterDto.getStartDate()) && Objects.nonNull(indicatorFilterDto.getEndDate())) {
            query = query.append(" and modified_on between '" + indicatorFilterDto.getStartDate() + "' AND '" + indicatorFilterDto.getEndDate() + "'");
        }
        if (Objects.nonNull(indicatorFilterDto.getStartDate()) && Objects.isNull(indicatorFilterDto.getEndDate())) {
            query = query.append(" and modified_on >= '" + indicatorFilterDto.getStartDate() + "'");
        }
        if (Objects.isNull(indicatorFilterDto.getStartDate()) && Objects.nonNull(indicatorFilterDto.getEndDate())) {
            query = query.append(" and modified_on <= '" + indicatorFilterDto.getEndDate() + "'");
        }


        if (Objects.nonNull(indicatorNumeratorEquation.getCondition()) || Objects.nonNull(indicatorNumeratorEquation.getValue())) {
            if (!indicatorNumeratorEquation.getCode().equalsIgnoreCase(CommonConstant.ALL_CODE)) {
                if (indicatorNumeratorEquation.getValueType().equalsIgnoreCase("Boolean")) {
                    query = query.append(" and valueText" + indicatorNumeratorEquation.getCondition() + " '" + indicatorNumeratorEquation.getValue() + "'");
                }
                if (indicatorNumeratorEquation.getValueType().equalsIgnoreCase("Number")) {
                    query = query.append(" and valueText" + indicatorNumeratorEquation.getCondition() + " " + indicatorNumeratorEquation.getValue());
                }
            } else {
                query = query.append(" where valueText" + indicatorNumeratorEquation.getCondition() + " '" + indicatorNumeratorEquation.getValue() + "'");
            }
        }
        return query.toString();
    }

    public String getQueryForIndicatorDenominatorEquation(IndicatorDenominatorEquation indicatorDenominatorEquation,
                                                          String facilityId,
                                                          Indicator indicator,
                                                          IndicatorFilterDto indicatorFilterDto) {
        StringBuilder query = new StringBuilder("with custom_code as ");
        query = query.append("(select obr.code as code, emr.resource_id as patient, ");
        query = query.append(" obr.modified_on ");
        if (Objects.nonNull(indicatorDenominatorEquation.getValueType())) {
            if (indicatorDenominatorEquation.getValueType().equalsIgnoreCase("Boolean")) {
                query = query.append(", cast(cast(cast(obr.text AS json)->>'" + getTypeValue(indicatorDenominatorEquation.getValueType()) + "' as text) AS "
                    + getTypeKey(indicatorDenominatorEquation.getValueType()) + ") as valueText");
            }
            if (indicatorDenominatorEquation.getValueType().equalsIgnoreCase("Number")) {
                query = query.append(", cast(cast(cast(obr.text AS json)->>'" + getTypeValue(indicatorDenominatorEquation.getValueType()) + "' as INTEGER) AS "
                    + getTypeKey(indicatorDenominatorEquation.getValueType()) + ") as valueText");
            }
        }
        if (Objects.nonNull(indicatorFilterDto.getAge())) {
            query = query.append(" , cast(EXTRACT('year' from age(now(), TO_DATE(cast(cast(emr.text AS json)->>'birthDate' as text),'YYYY-MM-DD'))) * 12 as INTEGER) +\n" +
                "   cast(EXTRACT('mons' from age(now(), TO_DATE(cast(cast(emr.text AS json)->>'birthDate' as text),'YYYY-MM-DD'))) as INTEGER) as age");
        }
        if (Objects.nonNull(indicatorFilterDto.getGender())) {
            query = query.append(" ,emr.gender as gender");
        }
        query = query.append(" from emcare_resources as emr left join observation_resource as obr on obr.subject_id = emr.resource_id ");
        if (facilityId != null && !facilityId.isEmpty()) {
            query = query.append(" where emr.facility_id in ('" + facilityId + "')");
        }
        query = query.append(") select distinct(patient) from custom_code where 1=1");
        if (!indicatorDenominatorEquation.getCode().equalsIgnoreCase(CommonConstant.ALL_CODE)) {
            query = query.append("and code = '" + indicatorDenominatorEquation.getCode() + "' ");
        }
        if (Objects.nonNull(indicatorFilterDto.getAge())) {
            query = query.append(" and age " + indicatorFilterDto.getAge());
        }
        if (Objects.nonNull(indicatorFilterDto.getGender())) {
            query = query.append(" and gender = '" + indicatorFilterDto.getGender() + "'");
        }
        if (Objects.nonNull(indicatorFilterDto.getStartDate()) && Objects.nonNull(indicatorFilterDto.getEndDate())) {
            query = query.append(" and modified_on between '" + indicatorFilterDto.getStartDate() + "' AND '" + indicatorFilterDto.getEndDate() + "'");
        }
        if (Objects.nonNull(indicatorFilterDto.getStartDate()) && Objects.isNull(indicatorFilterDto.getEndDate())) {
            query = query.append(" and modified_on >= '" + indicatorFilterDto.getStartDate() + "'");
        }
        if (Objects.isNull(indicatorFilterDto.getStartDate()) && Objects.nonNull(indicatorFilterDto.getEndDate())) {
            query = query.append(" and modified_on <= '" + indicatorFilterDto.getEndDate() + "'");
        }
        if (Objects.nonNull(indicatorDenominatorEquation.getCondition()) || Objects.nonNull(indicatorDenominatorEquation.getValue())) {
            if (!indicatorDenominatorEquation.getCode().equalsIgnoreCase(CommonConstant.ALL_CODE)) {
                if (indicatorDenominatorEquation.getValueType().equalsIgnoreCase("Boolean")) {
                    query = query.append(" and valueText" + indicatorDenominatorEquation.getCondition() + " '" + indicatorDenominatorEquation.getValue() + "'");
                }
                if (indicatorDenominatorEquation.getValueType().equalsIgnoreCase("Number")) {
                    query = query.append(" and valueText" + indicatorDenominatorEquation.getCondition() + " " + indicatorDenominatorEquation.getValue());
                }
            } else {
                query = query.append(" and valueText" + indicatorDenominatorEquation.getCondition() + " '" + indicatorDenominatorEquation.getValue() + "'");
            }
        }
        return query.toString();
    }

    public String changeQueryBasedOnFilterValueReplace(String facilityId,
                                                       Indicator indicator,
                                                       IndicatorFilterDto indicatorFilterDto) {

        String query = indicator.getQuery();
        ObjectMapper oMapper = new ObjectMapper();

        Map<String, Object> filterDtoMap = oMapper.convertValue(indicatorFilterDto, Map.class);
        for (var entry : filterDtoMap.entrySet()) {
            String value = "'null'";
            if (Objects.nonNull(entry.getValue())) {
                value = entry.getValue().toString();
            }
            query = query.replaceAll(":" + entry.getKey(), value);

        }
        query = query.replaceAll(":facilityId", facilityId);
        return query;
    }

    public String getTypeKey(String valueType) {
        String type = "TEXT";
        if (valueType.equals(CommonConstant.FHIR_TYPE_BOOLEAN_CONDITION)) {
            type = CommonConstant.FHIR_TYPE_BOOLEAN_KEY;
        } else if (valueType.equals(CommonConstant.FHIR_TYPE_INTEGER_CONDITION)) {
            type = CommonConstant.FHIR_TYPE_INTEGER_KEY;
        }
        return type;
    }

    public String getTypeValue(String valueType) {
        String type = "TEXT";
        if (valueType.equalsIgnoreCase(CommonConstant.FHIR_TYPE_BOOLEAN_CONDITION)) {
            type = CommonConstant.FHIR_TYPE_BOOLEAN_VALUE;
        } else if (valueType.equalsIgnoreCase(CommonConstant.FHIR_TYPE_INTEGER_CONDITION)) {
            type = CommonConstant.FHIR_TYPE_INTEGER_VALUE;
        }
        return type;
    }
}
