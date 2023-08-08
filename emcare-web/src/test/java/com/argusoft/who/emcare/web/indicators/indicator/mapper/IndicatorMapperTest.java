    package com.argusoft.who.emcare.web.indicators.indicator.mapper;

    import com.argusoft.who.emcare.web.indicators.indicator.dto.IndicatorDenominatorEquationDto;
    import com.argusoft.who.emcare.web.indicators.indicator.dto.IndicatorDto;
    import com.argusoft.who.emcare.web.indicators.indicator.dto.IndicatorNumeratorEquationDto;
    import com.argusoft.who.emcare.web.indicators.indicator.entity.Indicator;
    import com.argusoft.who.emcare.web.indicators.indicator.entity.IndicatorDenominatorEquation;
    import com.argusoft.who.emcare.web.indicators.indicator.entity.IndicatorNumeratorEquation;
    import org.junit.jupiter.api.Test;
    import static org.junit.jupiter.api.Assertions.assertEquals;
    import static org.junit.jupiter.api.Assertions.assertNotNull;

    import java.util.ArrayList;
    import java.util.Arrays;
    import java.util.List;

    class IndicatorMapperTest {

        @Test
        void testGetIndicator() {
            IndicatorDto dto = new IndicatorDto();
            dto.setIndicatorId(1L);
            dto.setIndicatorCode("Code123");
            dto.setIndicatorName("Indicator Name");
            dto.setDescription("Description");
            dto.setFacilityId("Facility123");
            dto.setNumeratorIndicatorEquation("NumEquation");
            dto.setDenominatorIndicatorEquation("DenomEquation");
            dto.setNumeratorEquations(new ArrayList<>());
            dto.setDenominatorEquations(new ArrayList<>());
            dto.setDisplayType("Display Type");
            dto.setNumeratorEquationString("NumEquationString");
            dto.setDenominatorEquationString("DenomEquationString");
            dto.setColourSchema("Color Schema");
            dto.setAge("Age");
            dto.setGender("Gender");
            dto.setQueryConfigure(true);
            dto.setQuery("Query");

            Indicator indicator = IndicatorMapper.getIndicator(dto);

            assertNotNull(indicator);
            assertEquals(dto.getIndicatorId(), indicator.getIndicatorId());
            assertEquals(dto.getIndicatorCode(), indicator.getIndicatorCode());
            assertEquals(dto.getIndicatorName(), indicator.getIndicatorName());
            assertEquals(dto.getDescription(), indicator.getDescription());
            assertEquals(dto.getFacilityId(), indicator.getFacilityId());
            assertEquals(dto.getNumeratorIndicatorEquation(), indicator.getNumeratorIndicatorEquation());
            assertEquals(dto.getDenominatorIndicatorEquation(), indicator.getDenominatorIndicatorEquation());
            assertEquals(dto.getDenominatorEquationString(), indicator.getDenominatorEquationString());
            assertEquals(dto.getNumeratorEquationString(), indicator.getNumeratorEquationString());
            assertEquals(dto.getDisplayType(), indicator.getDisplayType());
            assertEquals(dto.getColourSchema(), indicator.getColourSchema());
            assertEquals(dto.getAge(), indicator.getAge());
            assertEquals(dto.getGender(), indicator.getGender());
            assertEquals(dto.getQueryConfigure(), indicator.getQueryConfigure());
            assertEquals(dto.getQuery(), indicator.getQuery());
        }

        @Test
        void testGetIndicatorEquationList() {
            IndicatorNumeratorEquationDto dto = new IndicatorNumeratorEquationDto();
            dto.setNumeratorId(1L);
            dto.setCodeId(2L);
            dto.setCode("NUM_CODE");
            dto.setCondition("NUM_CONDITION");
            dto.setValue("NUM_VALUE");
            dto.setValueType("NUM_VALUE_TYPE");
            dto.setEqIdentifier("NUM_EQ_IDENTIFIER");

            Indicator indicator = new Indicator();

            List<IndicatorNumeratorEquation> equations = IndicatorMapper.getIndicatorEquationList(Arrays.asList(dto), indicator);

            assertNotNull(equations);
            assertEquals(1, equations.size());
            assertEquals(dto.getNumeratorId(), equations.get(0).getNumeratorId());
        }

        @Test
        void testGetDenominatorIndicatorEquationList() {
            IndicatorDenominatorEquationDto dto = new IndicatorDenominatorEquationDto();
            dto.setDenominatorId(1L);
            dto.setCodeId(2L);
            dto.setCode("Code123");
            dto.setCondition("Condition123");
            dto.setValue("Value123");
            dto.setValueType("ValueType");
            dto.setEqIdentifier("EquatiationIdentifier");
            Indicator indicator = new Indicator();

            List<IndicatorDenominatorEquation> equations = IndicatorMapper.getDenominatorIndicatorEquationList(Arrays.asList(dto), indicator);

            assertNotNull(equations);
            assertEquals(1, equations.size());

            IndicatorDenominatorEquation equation = equations.get(0);

            assertNotNull(equation);
            assertEquals(dto.getDenominatorId(), equation.getDenominatorId());
            assertEquals(dto.getCodeId(), equation.getCodeId());
            assertEquals(dto.getCode(), equation.getCode());
            assertEquals(dto.getCondition(), equation.getCondition());
            assertEquals(dto.getValue(), equation.getValue());
            assertEquals(dto.getValueType(), equation.getValueType());
            assertEquals(dto.getEqIdentifier(), equation.getEqIdentifier());
        }


        @Test
        void testGetNumeratorIndicatorEquation() {
            IndicatorNumeratorEquationDto dto = new IndicatorNumeratorEquationDto();
            dto.setNumeratorId(1L);
            dto.setCodeId(2L);
            dto.setCode("NUM_CODE");
            dto.setCondition("NUM_CONDITION");
            dto.setValue("NUM_VALUE");
            dto.setValueType("NUM_VALUE_TYPE");
            dto.setEqIdentifier("NUM_EQ_IDENTIFIER");
            Indicator indicator = new Indicator();

            IndicatorNumeratorEquation equation = IndicatorMapper.getNumeratorIndicatorEquation(dto, indicator);

            assertNotNull(equation);

            // Assert each property of the DTO matches the corresponding property of the entity
            assertEquals(dto.getNumeratorId(), equation.getNumeratorId());
            assertEquals(dto.getCodeId(), equation.getCodeId());
            assertEquals(dto.getCode(), equation.getCode());
            assertEquals(dto.getCondition(), equation.getCondition());
            assertEquals(dto.getValue(), equation.getValue());
            assertEquals(dto.getValueType(), equation.getValueType());
            assertEquals(dto.getEqIdentifier(), equation.getEqIdentifier());
        }


        @Test
        void testGetDenominatorIndicatorEquation() {
            IndicatorDenominatorEquationDto dto = new IndicatorDenominatorEquationDto();
            dto.setDenominatorId(1L);
            dto.setCodeId(2L);
            dto.setCode("Code123");
            dto.setCondition("Condition123");
            dto.setValue("Value123");
            dto.setValueType("ValueType");
            dto.setEqIdentifier("EquatiationIdentifier");

            Indicator indicator = new Indicator();

            IndicatorDenominatorEquation equation = IndicatorMapper.getDenominatorIndicatorEquation(dto, indicator);

            assertNotNull(equation);
            assertEquals(dto.getDenominatorId(), equation.getDenominatorId());
            assertEquals(dto.getCodeId(), equation.getCodeId());
            assertEquals(dto.getCode(), equation.getCode());
            assertEquals(dto.getCondition(), equation.getCondition());
            assertEquals(dto.getValue(), equation.getValue());
            assertEquals(dto.getValueType(), equation.getValueType());
            assertEquals(dto.getEqIdentifier(), equation.getEqIdentifier());
        }

    }
