package com.argusoft.who.emcare.web.indicators.indicator.queryBuilder;

import com.argusoft.who.emcare.web.common.constant.CommonConstant;
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

    public String getQueryForIndicatorNumeratorEquation(IndicatorNumeratorEquation indicatorNumeratorEquation, String facilityId) {
        StringBuilder query = new StringBuilder("with custom_code as ");

        if (Objects.nonNull(indicatorNumeratorEquation.getCondition())) {
            if (Objects.nonNull(indicatorNumeratorEquation.getValueType())) {
                query = query.append("(select cast(obr.text AS json)->'code'->'coding'->0->>'code' as code, " +
                        " cast(cast(cast(obr.text AS json)->>'" + getTypeValue(indicatorNumeratorEquation.getValueType()) + "' as text) AS "
                        + getTypeKey(indicatorNumeratorEquation.getValueType()) + ") as valueText" +
                        " from observation_resource as obr left join emcare_resources as emr on obr.subject_id = emr.resource_id " +
                        " left join location_resources as lor on emr.facility_id = lor.resource_id " +
                        " where lor.resource_id = '" + facilityId + "')");
            }
            query = query.append("select * from custom_code where code = '" + indicatorNumeratorEquation.getCode() + "' ");
            if (Objects.nonNull(indicatorNumeratorEquation.getCondition())) {
                query = query.append(" and valueText" + indicatorNumeratorEquation.getCondition() + " " + indicatorNumeratorEquation.getValue());
            }

        }

        return query.toString();
    }

    public String getQueryForIndicatorDenominatorEquation(IndicatorDenominatorEquation indicatorDenominatorEquation, String facilityId) {
        StringBuilder query = new StringBuilder("with custom_code as ");

        if (Objects.nonNull(indicatorDenominatorEquation.getCondition())) {
            if (Objects.nonNull(indicatorDenominatorEquation.getValueType())) {
                query = query.append("(select cast(obr.text AS json)->'code'->'coding'->0->>'code' as code, " +
                        " cast(cast(cast(obr.text AS json)->>'" + getTypeValue(indicatorDenominatorEquation.getValueType()) + "' as text) AS "
                        + getTypeKey(indicatorDenominatorEquation.getValueType()) + ") as valueText" +
                        " from observation_resource as obr left join emcare_resources as emr on obr.subject_id = emr.resource_id " +
                        " left join location_resources as lor on emr.facility_id = lor.resource_id " +
                        " where lor.resource_id = '" + facilityId + "')");
            }
            query = query.append(" select * from custom_code where code = '" + indicatorDenominatorEquation.getCode() + "' ");
            if (Objects.nonNull(indicatorDenominatorEquation.getCondition())) {
                query = query.append(" and valueText" + indicatorDenominatorEquation.getCondition() + " " + indicatorDenominatorEquation.getValue());
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
        if (valueType.equals(CommonConstant.FHIR_TYPE_BOOLEAN_CONDITION)) {
            type = CommonConstant.FHIR_TYPE_BOOLEAN_VALUE;
        }
        return type;
    }
}
