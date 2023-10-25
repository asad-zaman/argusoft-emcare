package com.argusoft.who.emcare.utils

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import ca.uhn.fhir.context.support.DefaultProfileValidationSupport
import java.text.SimpleDateFormat
import java.util.Date
import org.hl7.fhir.r4.hapi.ctx.HapiWorkerContext
import org.hl7.fhir.r4.model.Base
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.DecimalType
import org.hl7.fhir.r4.model.Enumeration
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.InstantType
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.Quantity
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.TimeType
import org.hl7.fhir.r4.utils.FHIRPathEngine

object FhirPathEvaluator {

    private val fhirPathEngine: FHIRPathEngine =
        with(FhirContext.forCached(FhirVersionEnum.R4)) {
            FHIRPathEngine(HapiWorkerContext(this, DefaultProfileValidationSupport(this)))
        }

    fun getReportPropertyListFromResource(resource: Resource, fhirPathItemList: List<FhirPathItem>) : List<ReportProperty> {
        return fhirPathItemList.mapNotNull {
            val result = extractValueFromResource(resource, it.fhirPath)
            if (result.isNotEmpty()) ReportProperty(it.title, result) else null
        }
    }


    fun extractValueFromResource(resource: Resource, expression: String): String {
        return fhirPathEngine.evaluate(resource, expression).joinToString { it.asStringValue() }
    }

}

data class FhirPathItem(
    val title: String,
    val fhirPath: String,
)

data class ReportProperty(
    val title: String,
    val value: String,
)

fun Base.asStringValue(): String {
    return when (this) {
        is StringType -> valueAsString
        is BooleanType ->
            when (value) {
                true -> "Yes"
                false -> "No"
                null -> ""
            }
        is DateType -> value.formattedDateString()
        is DateTimeType -> value.formattedDateTimeString()
        is InstantType -> value.formattedDateTimeString()
        is TimeType -> valueAsString
        is Quantity -> "$value ${code ?: unit}"
        is IntegerType -> valueAsString
        is DecimalType -> valueAsString
        is CodeableConcept -> codingFirstRep.display ?: codingFirstRep.code ?: text ?: ""
        is Coding -> display
        is HumanName -> nameAsSingleString
        is Enumeration<*> -> valueAsString
        else -> toString()
    }
}

fun Date.formattedDateTimeString(): String {
    return SimpleDateFormat.getDateTimeInstance().format(this)
}

fun Date.formattedDateString(): String {
    return SimpleDateFormat.getDateInstance().format(this)
}