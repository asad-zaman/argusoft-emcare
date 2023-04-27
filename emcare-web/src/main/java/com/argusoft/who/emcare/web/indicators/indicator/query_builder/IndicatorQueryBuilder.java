package com.argusoft.who.emcare.web.indicators.indicator.query_builder;

import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.indicators.indicator.entity.Indicator;
import com.argusoft.who.emcare.web.indicators.indicator.entity.IndicatorDenominatorEquation;
import com.argusoft.who.emcare.web.indicators.indicator.entity.IndicatorNumeratorEquation;
import org.springframework.stereotype.Component;

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

    public String getQueryForIndicatorNumeratorEquation(IndicatorNumeratorEquation indicatorNumeratorEquation, String facilityId, Indicator indicator) {
        StringBuilder query = new StringBuilder("with custom_code as ");
        query = query.append("(select cast(obr.text AS json)->'code'->'coding'->0->>'code' as code");
        if (Objects.nonNull(indicatorNumeratorEquation.getValueType())) {
            query = query.append(", cast(cast(cast(obr.text AS json)->>'" + getTypeValue(indicatorNumeratorEquation.getValueType()) + "' as text) AS "
                    + getTypeKey(indicatorNumeratorEquation.getValueType()) + ") as valueText");
        }
        if (Objects.nonNull(indicator.getAge())) {
            query = query.append(" , cast(EXTRACT('year' from age(now(), TO_DATE(cast(cast(emr.text AS json)->>'birthDate' as text),'YYYY-MM-DD'))) * 12 as INTEGER) +\n" +
                    "   cast(EXTRACT('mons' from age(now(), TO_DATE(cast(cast(emr.text AS json)->>'birthDate' as text),'YYYY-MM-DD'))) as INTEGER) as age");
        }
        if (Objects.nonNull(indicator.getGender())) {
            query = query.append(" , cast(cast(emr.text AS json)->>'gender' as text) as gender");
        }
        query = query.append(" from observation_resource as obr left join emcare_resources as emr on obr.subject_id = emr.resource_id " +
                " left join location_resources as lor on emr.facility_id = lor.resource_id " +
                " where lor.resource_id = '" + facilityId + "')");
        query = query.append(" select * from custom_code ");
        if (!indicatorNumeratorEquation.getCode().equalsIgnoreCase(CommonConstant.ALL_CODE)) {
            query = query.append("where code = '" + indicatorNumeratorEquation.getCode() + "' ");
        }
        if (Objects.nonNull(indicator.getAge())) {
            query = query.append(" and age " + indicator.getAge());
        }
        if (Objects.nonNull(indicator.getGender())) {
            query = query.append(" and gender = '" + indicator.getGender() + "'");
        }
        if (Objects.nonNull(indicatorNumeratorEquation.getCondition()) || Objects.nonNull(indicatorNumeratorEquation.getValue())) {
            if (!indicatorNumeratorEquation.getCode().equalsIgnoreCase(CommonConstant.ALL_CODE)) {
                query = query.append(" and valueText" + indicatorNumeratorEquation.getCondition() + " '" + indicatorNumeratorEquation.getValue() + "'");
            } else {
                query = query.append(" where valueText" + indicatorNumeratorEquation.getCondition() + " '" + indicatorNumeratorEquation.getValue() + "'");
            }
        }
        return query.toString();
    }

    public String getQueryForIndicatorDenominatorEquation(IndicatorDenominatorEquation indicatorDenominatorEquation, String facilityId, Indicator indicator) {
        StringBuilder query = new StringBuilder("with custom_code as ");
        query = query.append("(select cast(obr.text AS json)->'code'->'coding'->0->>'code' as code");
        if (Objects.nonNull(indicatorDenominatorEquation.getValueType())) {
            query = query.append(", cast(cast(cast(obr.text AS json)->>'" + getTypeValue(indicatorDenominatorEquation.getValueType()) + "' as text) AS "
                    + getTypeKey(indicatorDenominatorEquation.getValueType()) + ") as valueText");
        }
        if (Objects.nonNull(indicator.getAge())) {
            query = query.append(" , cast(EXTRACT('year' from age(now(), TO_DATE(cast(cast(emr.text AS json)->>'birthDate' as text),'YYYY-MM-DD'))) * 12 as INTEGER) +\n" +
                    "   cast(EXTRACT('mons' from age(now(), TO_DATE(cast(cast(emr.text AS json)->>'birthDate' as text),'YYYY-MM-DD'))) as INTEGER) as age");
        }
        if (Objects.nonNull(indicator.getGender())) {
            query = query.append(" , cast(cast(emr.text AS json)->>'gender' as text) as gender");
        }
        query = query.append(" from observation_resource as obr left join emcare_resources as emr on obr.subject_id = emr.resource_id " +
                " left join location_resources as lor on emr.facility_id = lor.resource_id " +
                " where lor.resource_id = '" + facilityId + "')");
        query = query.append(" select * from custom_code ");
        if (!indicatorDenominatorEquation.getCode().equalsIgnoreCase(CommonConstant.ALL_CODE)) {
            query = query.append("where code = '" + indicatorDenominatorEquation.getCode() + "' ");
        }
        if (Objects.nonNull(indicator.getAge())) {
            if(!indicatorDenominatorEquation.getCode().equalsIgnoreCase(CommonConstant.ALL_CODE)){
                query = query.append(" and age " + indicator.getAge());
            }else{
                query = query.append(" where age " + indicator.getAge());
            }
        }
        if (Objects.nonNull(indicator.getGender())) {
            if(Objects.nonNull(indicator.getAge())){
                query = query.append(" and gender = '" + indicator.getGender() + "'");
            }else {
                query = query.append(" where gender = '" + indicator.getGender() + "'");
            }
        }
        if (Objects.nonNull(indicatorDenominatorEquation.getCondition()) || Objects.nonNull(indicatorDenominatorEquation.getValue())) {
            if (!indicatorDenominatorEquation.getCode().equalsIgnoreCase(CommonConstant.ALL_CODE)) {
                query = query.append(" and valueText" + indicatorDenominatorEquation.getCondition() + " '" + indicatorDenominatorEquation.getValue() + "'");
            } else {
                query = query.append(" where valueText" + indicatorDenominatorEquation.getCondition() + " '" + indicatorDenominatorEquation.getValue() + "'");
            }
        }
        return query.toString();
    }

    public String getTypeKey(String valueType) {
        String type = "TEXT";
        if (valueType.equals(CommonConstant.FHIR_TYPE_BOOLEAN_CONDITION)) {
            type = CommonConstant.FHIR_TYPE_BOOLEAN_KEY;
        }
        return type;
    }

    public String getTypeValue(String valueType) {
        String type = "TEXT";
        if (valueType.equalsIgnoreCase(CommonConstant.FHIR_TYPE_BOOLEAN_CONDITION)) {
            type = CommonConstant.FHIR_TYPE_BOOLEAN_VALUE;
        }
        return type;
    }
}
