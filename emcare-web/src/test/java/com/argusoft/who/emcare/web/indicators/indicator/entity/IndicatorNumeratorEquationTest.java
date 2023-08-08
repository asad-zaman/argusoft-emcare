package com.argusoft.who.emcare.web.indicators.indicator.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class IndicatorNumeratorEquationTest {
    @Test
    public void testEqualsAndHashCode() {
        IndicatorNumeratorEquation equation1 = new IndicatorNumeratorEquation();
        equation1.setNumeratorId(1L);
        equation1.setCodeId(123L);
        equation1.setCode("CODE123");
        equation1.setCondition("CONDITION1");
        equation1.setValue("VALUE1");
        equation1.setValueType("ValueType1");
        equation1.setEqIdentifier("Identifier1");

        IndicatorNumeratorEquation equation2 = new IndicatorNumeratorEquation();
        equation2.setNumeratorId(1L);
        equation2.setCodeId(123L);
        equation2.setCode("CODE123");
        equation2.setCondition("CONDITION1");
        equation2.setValue("VALUE1");
        equation2.setValueType("ValueType1");
        equation2.setEqIdentifier("Identifier1");

        IndicatorNumeratorEquation equation3 = new IndicatorNumeratorEquation();
        equation3.setNumeratorId(11L);
        equation3.setCodeId(12L);
        equation3.setCode("CODE");
        equation3.setCondition("CONDITION1");
        equation3.setValue("VALUE1");
        equation3.setValueType("ValueType1");
        equation3.setEqIdentifier("Identifier1");

        assertTrue(equation1.equals(equation2));
        assertEquals(equation1.hashCode(), equation2.hashCode());
        assertFalse(equation1.equals(equation3));
        assertNotEquals(equation1.hashCode(),equation3.hashCode());
    }
}
