package com.argusoft.who.emcare.web.indicators.indicator.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;

public class IndicatorTest {

    @Test
    public void testEqualsAndHashCode() {
        //Test Date 1
        IndicatorNumeratorEquation numeratorEquation1 = new IndicatorNumeratorEquation();
        numeratorEquation1.setNumeratorId(1L);
        numeratorEquation1.setCodeId(123L);
        numeratorEquation1.setCode("CODE123");
        numeratorEquation1.setCondition("CONDITION1");
        numeratorEquation1.setValue("VALUE1");
        numeratorEquation1.setValueType("ValueType1");
        numeratorEquation1.setEqIdentifier("Identifier1");

        IndicatorDenominatorEquation denominatorEquation1 = new IndicatorDenominatorEquation();
        denominatorEquation1.setDenominatorId(1L);
        denominatorEquation1.setCodeId(123L);
        denominatorEquation1.setCode("CODE123");
        denominatorEquation1.setCondition("CONDITION1");
        denominatorEquation1.setValue("VALUE1");
        denominatorEquation1.setValueType("ValueType1");
        denominatorEquation1.setEqIdentifier("Identifier1");

        Indicator indicator1 = new Indicator();
        indicator1.setIndicatorId(1L);
        indicator1.setIndicatorCode("IND123");
        indicator1.setIndicatorName("Test");
        indicator1.setDescription("This is a Test Indicator");
        indicator1.setFacilityId("1445");
        indicator1.setNumeratorIndicatorEquation("10/15");
        indicator1.setDenominatorIndicatorEquation("5/10");
        indicator1.setNumeratorEquationString("10/15");
        indicator1.setDenominatorEquationString("5/10");
        indicator1.setDisplayType("Pie Chart");
        indicator1.setNumeratorEquation(Collections.singletonList(numeratorEquation1));
        indicator1.setDenominatorEquation(Collections.singletonList(denominatorEquation1));
        indicator1.setColourSchema("RGB");
        indicator1.setAge("<12");
        indicator1.setGender("Male");
        indicator1.setQueryConfigure(true);

        //Test data 2
        IndicatorNumeratorEquation numeratorEquation2 = new IndicatorNumeratorEquation();
        numeratorEquation2.setNumeratorId(1L);
        numeratorEquation2.setCodeId(123L);
        numeratorEquation2.setCode("CODE123");
        numeratorEquation2.setCondition("CONDITION1");
        numeratorEquation2.setValue("VALUE1");
        numeratorEquation2.setValueType("ValueType1");
        numeratorEquation2.setEqIdentifier("Identifier1");

        IndicatorDenominatorEquation denominatorEquation2 = new IndicatorDenominatorEquation();
        denominatorEquation2.setDenominatorId(1L);
        denominatorEquation2.setCodeId(123L);
        denominatorEquation2.setCode("CODE123");
        denominatorEquation2.setCondition("CONDITION1");
        denominatorEquation2.setValue("VALUE1");
        denominatorEquation2.setValueType("ValueType1");
        denominatorEquation2.setEqIdentifier("Identifier1");

        Indicator indicator2 = new Indicator();
        indicator2.setIndicatorId(1L);
        indicator2.setIndicatorCode("IND123");
        indicator2.setIndicatorName("Test");
        indicator2.setDescription("This is a Test Indicator");
        indicator2.setFacilityId("1445");
        indicator2.setNumeratorIndicatorEquation("10/15");
        indicator2.setDenominatorIndicatorEquation("5/10");
        indicator2.setNumeratorEquationString("10/15");
        indicator2.setDenominatorEquationString("5/10");
        indicator2.setDisplayType("Pie Chart");
        indicator2.setNumeratorEquation(Collections.singletonList(numeratorEquation2));
        indicator2.setDenominatorEquation(Collections.singletonList(denominatorEquation2));
        indicator2.setColourSchema("RGB");
        indicator2.setAge("<12");
        indicator2.setGender("Male");
        indicator2.setQueryConfigure(true);

        //test data 3

        IndicatorNumeratorEquation numeratorEquation3 = new IndicatorNumeratorEquation();
        numeratorEquation3.setNumeratorId(1L);
        numeratorEquation3.setCodeId(123L);
        numeratorEquation3.setCode("CODE123");
        numeratorEquation3.setCondition("CONDITION1");
        numeratorEquation3.setValue("VALUE1");
        numeratorEquation3.setValueType("ValueType1");
        numeratorEquation3.setEqIdentifier("Identifier1");

        IndicatorDenominatorEquation denominatorEquation3 = new IndicatorDenominatorEquation();
        denominatorEquation3.setDenominatorId(1L);
        denominatorEquation3.setCodeId(123L);
        denominatorEquation3.setCode("CODE123");
        denominatorEquation3.setCondition("CONDITION1");
        denominatorEquation3.setValue("VALUE1");
        denominatorEquation3.setValueType("ValueType1");
        denominatorEquation3.setEqIdentifier("Identifier1");

        Indicator indicator3 = new Indicator();
        indicator3.setIndicatorId(10L);
        indicator3.setIndicatorCode("IND");
        indicator3.setIndicatorName("Test123");
        indicator3.setDescription("This is a Test123 Indicator");
        indicator3.setFacilityId("1234");
        indicator3.setNumeratorIndicatorEquation("10/15");
        indicator3.setDenominatorIndicatorEquation("5/10");
        indicator3.setNumeratorEquationString("10/15");
        indicator3.setDenominatorEquationString("5/10");
        indicator3.setDisplayType("Bar Chart");
        indicator3.setNumeratorEquation(Collections.singletonList(numeratorEquation2));
        indicator3.setDenominatorEquation(Collections.singletonList(denominatorEquation2));
        indicator3.setColourSchema("RGB");
        indicator3.setAge(">5");
        indicator3.setGender("Female");
        indicator3.setQueryConfigure(false);


        assertTrue(indicator1.equals(indicator2));
        assertEquals(indicator1.hashCode(), indicator2.hashCode());
        assertFalse(indicator1.equals(indicator3));
        assertNotEquals(indicator1.hashCode(),indicator3.hashCode());
    }
}
