package com.argusoft.who.emcare.web.indicators.indicator.query_builder;

import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.indicators.indicator.dto.IndicatorFilterDto;
import com.argusoft.who.emcare.web.indicators.indicator.entity.Indicator;
import com.argusoft.who.emcare.web.indicators.indicator.entity.IndicatorDenominatorEquation;
import com.argusoft.who.emcare.web.indicators.indicator.entity.IndicatorNumeratorEquation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IndicatorQueryBuilderTest {
    // =============================================================
    // === @TODO: These test cases are not thoroughly checked ======
    // =============================================================

    public static IndicatorQueryBuilder queryBuilder;

    @BeforeAll
    public static void setUp() {
        queryBuilder = new IndicatorQueryBuilder();
    }

    @Test
    void testGetQueryForIndicatorNumeratorEquation() throws ParseException {
        Date mockDate = new SimpleDateFormat("yyyy-MM-dd").parse("2023-09-14");
        IndicatorNumeratorEquation indicatorNumeratorEquation = new IndicatorNumeratorEquation();
        indicatorNumeratorEquation.setEqIdentifier("EQ");
        indicatorNumeratorEquation.setNumeratorId(1L);
        indicatorNumeratorEquation.setNumeratorIndicator(null);
        indicatorNumeratorEquation.setCode("C");
        indicatorNumeratorEquation.setCondition("1 == 1");
        indicatorNumeratorEquation.setCodeId(2L);
        indicatorNumeratorEquation.setValue("V");
        indicatorNumeratorEquation.setValueType("tt");
        String facilityId = "F1";
        IndicatorFilterDto indicatorFilterDto = new IndicatorFilterDto();
        indicatorFilterDto.setGender("M");
        indicatorFilterDto.setIndicatorId(1L);
        indicatorFilterDto.setAge("0-5");
        indicatorFilterDto.setStartDate(mockDate);
        indicatorFilterDto.setEndDate(mockDate);
        indicatorFilterDto.setFacilityIds(List.of("F1"));
        assertEquals(
                "with custom_code as (select obr.code as code, emr.resource_id as patient, obr.modified_on  , cast(EXTRACT('year' from age(now(), TO_DATE(cast(cast(emr.text AS json)->>'birthDate' as text),'YYYY-MM-DD'))) * 12 as INTEGER) +\n" +
                        "   cast(EXTRACT('mons' from age(now(), TO_DATE(cast(cast(emr.text AS json)->>'birthDate' as text),'YYYY-MM-DD'))) as INTEGER) as age ,emr.gender as gender from observation_resource as obr left join emcare_resources as emr on obr.subject_id = emr.resource_id  where emr.facility_id in ('F1')) select distinct(patient) from custom_code where code = 'C'  and age 0-5 and gender = 'M' and modified_on between 'Thu Sep 14 00:00:00 IST 2023' AND 'Thu Sep 14 00:00:00 IST 2023'",
                queryBuilder.getQueryForIndicatorNumeratorEquation(indicatorNumeratorEquation, facilityId, null, indicatorFilterDto)
        );
    }

    @Test
    void testGetQueryForIndicatorDenominatorEquation() throws ParseException {
        Date mockDate = new SimpleDateFormat("yyyy-MM-dd").parse("2023-09-14");
        IndicatorDenominatorEquation indicatorDenominatorEquation = new IndicatorDenominatorEquation();
        indicatorDenominatorEquation.setEqIdentifier("EQ");
        indicatorDenominatorEquation.setDenominatorId(1L);
        indicatorDenominatorEquation.setDenominatorIndicator(null);
        indicatorDenominatorEquation.setCode("C");
        indicatorDenominatorEquation.setCondition("1 == 1");
        indicatorDenominatorEquation.setCodeId(2L);
        indicatorDenominatorEquation.setValue("V");
        indicatorDenominatorEquation.setValueType("tt");
        String facilityId = "F1";
        IndicatorFilterDto indicatorFilterDto = new IndicatorFilterDto();
        indicatorFilterDto.setGender("M");
        indicatorFilterDto.setIndicatorId(1L);
        indicatorFilterDto.setAge("0-5");
        indicatorFilterDto.setStartDate(mockDate);
        indicatorFilterDto.setEndDate(mockDate);
        indicatorFilterDto.setFacilityIds(List.of("F1"));
        assertEquals("with custom_code as (select obr.code as code, emr.resource_id as patient,  obr.modified_on  , cast(EXTRACT('year' from age(now(), TO_DATE(cast(cast(emr.text AS json)->>'birthDate' as text),'YYYY-MM-DD'))) * 12 as INTEGER) +\n" +
                        "   cast(EXTRACT('mons' from age(now(), TO_DATE(cast(cast(emr.text AS json)->>'birthDate' as text),'YYYY-MM-DD'))) as INTEGER) as age ,emr.gender as gender from emcare_resources as emr left join observation_resource as obr on obr.subject_id = emr.resource_id  where emr.facility_id in ('F1')) select distinct(patient) from custom_code where 1=1and code = 'C'  and age 0-5 and gender = 'M' and modified_on between 'Thu Sep 14 00:00:00 IST 2023' AND 'Thu Sep 14 00:00:00 IST 2023'",
                queryBuilder.getQueryForIndicatorDenominatorEquation(indicatorDenominatorEquation, facilityId, null, indicatorFilterDto)
        );
    }

    @Test
    void changeQueryBasedOnFilterValueReplace() {
        Indicator indicator = new Indicator();
        indicator.setQuery("SELECT * FROM test WHERE age = :age AND gender = :gender AND endDate = :endDate AND facilityId IN (:facilityId)");
        String facilityId = "F1";
        IndicatorFilterDto indicatorFilterDto = new IndicatorFilterDto();
        indicatorFilterDto.setGender("M");
        indicatorFilterDto.setAge("0-5");
        indicatorFilterDto.setEndDate(null);
        assertEquals("SELECT * FROM test WHERE age = 0-5 AND gender = M AND endDate = 'null' AND facilityId IN (F1)",
                queryBuilder.changeQueryBasedOnFilterValueReplace(facilityId, indicator, indicatorFilterDto)
        );
    }

    @Nested
    class testGetTypeKey {
        @Test
        void FHIR_TYPE_BOOLEAN_CONDITION() {
            assertEquals(CommonConstant.FHIR_TYPE_BOOLEAN_KEY, queryBuilder.getTypeKey(CommonConstant.FHIR_TYPE_BOOLEAN_CONDITION));
        }

        @Test
        void FHIR_TYPE_INTEGER_CONDITION() {
            assertEquals(CommonConstant.FHIR_TYPE_INTEGER_KEY, queryBuilder.getTypeKey(CommonConstant.FHIR_TYPE_INTEGER_CONDITION));
        }

        @Test
        void OTHERS() {
            assertEquals("TEXT", queryBuilder.getTypeKey("OTHER"));
        }
    }

    @Nested
    class testGetTypeValue {
        @Test
        void FHIR_TYPE_BOOLEAN_CONDITION() {
            assertEquals(CommonConstant.FHIR_TYPE_BOOLEAN_VALUE, queryBuilder.getTypeValue(CommonConstant.FHIR_TYPE_BOOLEAN_CONDITION));
        }

        @Test
        void FHIR_TYPE_INTEGER_CONDITION() {
            assertEquals(CommonConstant.FHIR_TYPE_INTEGER_VALUE, queryBuilder.getTypeValue(CommonConstant.FHIR_TYPE_INTEGER_CONDITION));
        }

        @Test
        void OTHERS() {
            assertEquals("TEXT", queryBuilder.getTypeValue("OTHER"));
        }
    }
}