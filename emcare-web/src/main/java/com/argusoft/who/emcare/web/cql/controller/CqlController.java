package com.argusoft.who.emcare.web.cql.controller;

import com.argusoft.who.emcare.web.cql.EmCareCqlEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@CrossOrigin(origins = "**")
@RestController
@RequestMapping("/api/cql")
public class CqlController {

    @Autowired
    EmCareCqlEngine emCareCqlEngine;

    @GetMapping("/execute")
    public Object executeCql() throws IOException {
        String str = "library Test version '1.0.0'\ndefine X:\n5+5";
        return EmCareCqlEngine.execute(str);
    }

    @GetMapping("/execute/patient")
    public Object executeCqlForPatient() throws IOException {
        String str = "\n" +
                "library emcaredt01\n" +
                "using FHIR version 4.0.1\n" +
                "define function ToInterval(period FHIR.Period):\n" +
                "    if period is null then\n" +
                "        null\n" +
                "    else\n" +
                "        if period.\"start\" is null then\n" +
                "            Interval(period.\"start\".value, period.\"end\".value]\n" +
                "        else\n" +
                "            Interval[period.\"start\".value, period.\"end\".value]\n" +
                "\n" +
                "define function ToCalendarUnit(unit System.String):\n" +
                "    case unit\n" +
                "        when 'ms' then 'millisecond'\n" +
                "        when 's' then 'second'\n" +
                "        when 'min' then 'minute'\n" +
                "        when 'h' then 'hour'\n" +
                "        when 'd' then 'day'\n" +
                "        when 'wk' then 'week'\n" +
                "        when 'mo' then 'month'\n" +
                "        when 'a' then 'year'\n" +
                "        else unit\n" +
                "    end\n" +
                "\n" +
                "define function ToQuantity(quantity FHIR.Quantity):\n" +
                "    case\n" +
                "        when quantity is null then null\n" +
                "        when quantity.value is null then null\n" +
                "        when quantity.comparator is not null then\n" +
                "            Message(null, true, 'FHIRHelpers.ToQuantity.ComparatorQuantityNotSupported', 'Error', 'FHIR Quantity value has a comparator and cannot be converted to a System.Quantity value.')\n" +
                "        when quantity.system is null or quantity.system.value = 'http://unitsofmeasure.org'\n" +
                "              or quantity.system.value = 'http://hl7.org/fhirpath/CodeSystem/calendar-units' then\n" +
                "            System.Quantity { value: quantity.value.value, unit: ToCalendarUnit(Coalesce(quantity.code.value, quantity.unit.value, '1')) }\n" +
                "        else\n" +
                "            Message(null, true, 'FHIRHelpers.ToQuantity.InvalidFHIRQuantity', 'Error', 'Invalid FHIR Quantity code: ' & quantity.unit.value & ' (' & quantity.system.value & '|' & quantity.code.value & ')')\n" +
                "    end\n" +
                "\n" +
                "define function ToQuantityIgnoringComparator(quantity FHIR.Quantity):\n" +
                "    case\n" +
                "        when quantity is null then null\n" +
                "        when quantity.value is null then null\n" +
                "        when quantity.system is null or quantity.system.value = 'http://unitsofmeasure.org'\n" +
                "              or quantity.system.value = 'http://hl7.org/fhirpath/CodeSystem/calendar-units' then\n" +
                "            System.Quantity { value: quantity.value.value, unit: ToCalendarUnit(Coalesce(quantity.code.value, quantity.unit.value, '1')) }\n" +
                "        else\n" +
                "            Message(null, true, 'FHIRHelpers.ToQuantity.InvalidFHIRQuantity', 'Error', 'Invalid FHIR Quantity code: ' & quantity.unit.value & ' (' & quantity.system.value & '|' & quantity.code.value & ')')\n" +
                "    end\n" +
                "\n" +
                "define function ToInterval(quantity FHIR.Quantity):\n" +
                "    if quantity is null then null else\n" +
                "        case quantity.comparator.value\n" +
                "            when '<' then\n" +
                "                Interval[\n" +
                "                    null,\n" +
                "                    ToQuantityIgnoringComparator(quantity)\n" +
                "                )\n" +
                "            when '<=' then\n" +
                "                Interval[\n" +
                "                    null,\n" +
                "                    ToQuantityIgnoringComparator(quantity)\n" +
                "                ]\n" +
                "            when '>=' then\n" +
                "                Interval[\n" +
                "                    ToQuantityIgnoringComparator(quantity),\n" +
                "                    null\n" +
                "                ]\n" +
                "            when '>' then\n" +
                "                Interval(\n" +
                "                    ToQuantityIgnoringComparator(quantity),\n" +
                "                    null\n" +
                "                ]\n" +
                "            else\n" +
                "                Interval[ToQuantity(quantity), ToQuantity(quantity)]\n" +
                "        end\n" +
                "\n" +
                "define function ToRatio(ratio FHIR.Ratio):\n" +
                "    if ratio is null then\n" +
                "        null\n" +
                "    else\n" +
                "        System.Ratio { numerator: ToQuantity(ratio.numerator), denominator: ToQuantity(ratio.denominator) }\n" +
                "\n" +
                "define function ToInterval(range FHIR.Range):\n" +
                "    if range is null then\n" +
                "        null\n" +
                "    else\n" +
                "        Interval[ToQuantity(range.low), ToQuantity(range.high)]\n" +
                "\n" +
                "define function ToCode(coding FHIR.Coding):\n" +
                "    if coding is null then\n" +
                "        null\n" +
                "    else\n" +
                "        System.Code {\n" +
                "          code: coding.code.value,\n" +
                "          system: coding.system.value,\n" +
                "          version: coding.version.value,\n" +
                "          display: coding.display.value\n" +
                "        }\n" +
                "\n" +
                "define function ToConcept(concept FHIR.CodeableConcept):\n" +
                "    if concept is null then\n" +
                "        null\n" +
                "    else\n" +
                "        System.Concept {\n" +
                "            codes: concept.coding C return ToCode(C),\n" +
                "            display: concept.text.value\n" +
                "        }\n" +
                "\n" +
                "define function reference(reference String):\n" +
                "    if reference is null then\n" +
                "        null\n" +
                "    else\n" +
                "        Reference { reference: string { value: reference } }\n" +
                "\n" +
                "define function resolve(reference String) returns Resource: external\n" +
                "define function resolve(reference Reference) returns Resource: external\n" +
                "define function reference(resource Resource) returns Reference: external\n" +
                "define function extension(element Element, url String) returns List<Element>: external\n" +
                "define function extension(resource Resource, url String) returns List<Element>: external\n" +
                "define function hasValue(element Element) returns Boolean: external\n" +
                "define function getValue(element Element) returns Any: external\n" +
                "define function ofType(identifier String) returns List<Any>: external\n" +
                "define function is(identifier String) returns Boolean: external\n" +
                "define function as(identifier String) returns Any: external\n" +
                "define function elementDefinition(element Element) returns ElementDefinition: external\n" +
                "define function slice(element Element, url String, name String) returns List<Element>: external\n" +
                "define function checkModifiers(resource Resource) returns Resource: external\n" +
                "define function checkModifiers(resource Resource, modifier String) returns Resource: external\n" +
                "define function checkModifiers(element Element) returns Element: external\n" +
                "define function checkModifiers(element Element, modifier String) returns Element: external\n" +
                "define function conformsTo(resource Resource, structure String) returns Boolean: external\n" +
                "define function memberOf(code code, valueSet String) returns Boolean: external\n" +
                "define function memberOf(coding Coding, valueSet String) returns Boolean: external\n" +
                "define function memberOf(concept CodeableConcept, valueSet String) returns Boolean: external\n" +
                "define function subsumes(coding Coding, subsumedCoding Coding) returns Boolean: external\n" +
                "define function subsumes(concept CodeableConcept, subsumedConcept CodeableConcept) returns Boolean: external\n" +
                "define function subsumedBy(coding Coding, subsumingCoding Coding) returns Boolean: external\n" +
                "define function subsumedBy(concept CodeableConcept, subsumingConcept CodeableConcept) returns Boolean: external\n" +
                "define function htmlChecks(element Element) returns Boolean: external\n" +
                "\n" +
                "define function ToString(value AccountStatus): value.value\n" +
                "define function ToString(value ActionCardinalityBehavior): value.value\n" +
                "define function ToString(value ActionConditionKind): value.value\n" +
                "define function ToString(value ActionGroupingBehavior): value.value\n" +
                "define function ToString(value ActionParticipantType): value.value\n" +
                "define function ToString(value ActionPrecheckBehavior): value.value\n" +
                "define function ToString(value ActionRelationshipType): value.value\n" +
                "define function ToString(value ActionRequiredBehavior): value.value\n" +
                "define function ToString(value ActionSelectionBehavior): value.value\n" +
                "define function ToString(value ActivityDefinitionKind): value.value\n" +
                "define function ToString(value ActivityParticipantType): value.value\n" +
                "define function ToString(value AddressType): value.value\n" +
                "define function ToString(value AddressUse): value.value\n" +
                "define function ToString(value AdministrativeGender): value.value\n" +
                "define function ToString(value AdverseEventActuality): value.value\n" +
                "define function ToString(value AggregationMode): value.value\n" +
                "define function ToString(value AllergyIntoleranceCategory): value.value\n" +
                "define function ToString(value AllergyIntoleranceCriticality): value.value\n" +
                "define function ToString(value AllergyIntoleranceSeverity): value.value\n" +
                "define function ToString(value AllergyIntoleranceType): value.value\n" +
                "define function ToString(value AppointmentStatus): value.value\n" +
                "define function ToString(value AssertionDirectionType): value.value\n" +
                "define function ToString(value AssertionOperatorType): value.value\n" +
                "define function ToString(value AssertionResponseTypes): value.value\n" +
                "define function ToString(value AuditEventAction): value.value\n" +
                "define function ToString(value AuditEventAgentNetworkType): value.value\n" +
                "define function ToString(value AuditEventOutcome): value.value\n" +
                "define function ToString(value BindingStrength): value.value\n" +
                "define function ToString(value BiologicallyDerivedProductCategory): value.value\n" +
                "define function ToString(value BiologicallyDerivedProductStatus): value.value\n" +
                "define function ToString(value BiologicallyDerivedProductStorageScale): value.value\n" +
                "define function ToString(value BundleType): value.value\n" +
                "define function ToString(value CapabilityStatementKind): value.value\n" +
                "define function ToString(value CarePlanActivityKind): value.value\n" +
                "define function ToString(value CarePlanActivityStatus): value.value\n" +
                "define function ToString(value CarePlanIntent): value.value\n" +
                "define function ToString(value CarePlanStatus): value.value\n" +
                "define function ToString(value CareTeamStatus): value.value\n" +
                "define function ToString(value CatalogEntryRelationType): value.value\n" +
                "define function ToString(value ChargeItemDefinitionPriceComponentType): value.value\n" +
                "define function ToString(value ChargeItemStatus): value.value\n" +
                "define function ToString(value ClaimResponseStatus): value.value\n" +
                "define function ToString(value ClaimStatus): value.value\n" +
                "define function ToString(value ClinicalImpressionStatus): value.value\n" +
                "define function ToString(value CodeSearchSupport): value.value\n" +
                "define function ToString(value CodeSystemContentMode): value.value\n" +
                "define function ToString(value CodeSystemHierarchyMeaning): value.value\n" +
                "define function ToString(value CommunicationPriority): value.value\n" +
                "define function ToString(value CommunicationRequestStatus): value.value\n" +
                "define function ToString(value CommunicationStatus): value.value\n" +
                "define function ToString(value CompartmentCode): value.value\n" +
                "define function ToString(value CompartmentType): value.value\n" +
                "define function ToString(value CompositionAttestationMode): value.value\n" +
                "define function ToString(value CompositionStatus): value.value\n" +
                "define function ToString(value ConceptMapEquivalence): value.value\n" +
                "define function ToString(value ConceptMapGroupUnmappedMode): value.value\n" +
                "define function ToString(value ConditionalDeleteStatus): value.value\n" +
                "define function ToString(value ConditionalReadStatus): value.value\n" +
                "define function ToString(value ConsentDataMeaning): value.value\n" +
                "define function ToString(value ConsentProvisionType): value.value\n" +
                "define function ToString(value ConsentState): value.value\n" +
                "define function ToString(value ConstraintSeverity): value.value\n" +
                "define function ToString(value ContactPointSystem): value.value\n" +
                "define function ToString(value ContactPointUse): value.value\n" +
                "define function ToString(value ContractPublicationStatus): value.value\n" +
                "define function ToString(value ContractStatus): value.value\n" +
                "define function ToString(value ContributorType): value.value\n" +
                "define function ToString(value CoverageStatus): value.value\n" +
                "define function ToString(value CurrencyCode): value.value\n" +
                "define function ToString(value DayOfWeek): value.value\n" +
                "define function ToString(value DaysOfWeek): value.value\n" +
                "define function ToString(value DetectedIssueSeverity): value.value\n" +
                "define function ToString(value DetectedIssueStatus): value.value\n" +
                "define function ToString(value DeviceMetricCalibrationState): value.value\n" +
                "define function ToString(value DeviceMetricCalibrationType): value.value\n" +
                "define function ToString(value DeviceMetricCategory): value.value\n" +
                "define function ToString(value DeviceMetricColor): value.value\n" +
                "define function ToString(value DeviceMetricOperationalStatus): value.value\n" +
                "define function ToString(value DeviceNameType): value.value\n" +
                "define function ToString(value DeviceRequestStatus): value.value\n" +
                "define function ToString(value DeviceUseStatementStatus): value.value\n" +
                "define function ToString(value DiagnosticReportStatus): value.value\n" +
                "define function ToString(value DiscriminatorType): value.value\n" +
                "define function ToString(value DocumentConfidentiality): value.value\n" +
                "define function ToString(value DocumentMode): value.value\n" +
                "define function ToString(value DocumentReferenceStatus): value.value\n" +
                "define function ToString(value DocumentRelationshipType): value.value\n" +
                "define function ToString(value EligibilityRequestPurpose): value.value\n" +
                "define function ToString(value EligibilityRequestStatus): value.value\n" +
                "define function ToString(value EligibilityResponsePurpose): value.value\n" +
                "define function ToString(value EligibilityResponseStatus): value.value\n" +
                "define function ToString(value EnableWhenBehavior): value.value\n" +
                "define function ToString(value EncounterLocationStatus): value.value\n" +
                "define function ToString(value EncounterStatus): value.value\n" +
                "define function ToString(value EndpointStatus): value.value\n" +
                "define function ToString(value EnrollmentRequestStatus): value.value\n" +
                "define function ToString(value EnrollmentResponseStatus): value.value\n" +
                "define function ToString(value EpisodeOfCareStatus): value.value\n" +
                "define function ToString(value EventCapabilityMode): value.value\n" +
                "define function ToString(value EventTiming): value.value\n" +
                "define function ToString(value EvidenceVariableType): value.value\n" +
                "define function ToString(value ExampleScenarioActorType): value.value\n" +
                "define function ToString(value ExplanationOfBenefitStatus): value.value\n" +
                "define function ToString(value ExposureState): value.value\n" +
                "define function ToString(value ExtensionContextType): value.value\n" +
                "define function ToString(value FHIRAllTypes): value.value\n" +
                "define function ToString(value FHIRDefinedType): value.value\n" +
                "define function ToString(value FHIRDeviceStatus): value.value\n" +
                "define function ToString(value FHIRResourceType): value.value\n" +
                "define function ToString(value FHIRSubstanceStatus): value.value\n" +
                "define function ToString(value FHIRVersion): value.value\n" +
                "define function ToString(value FamilyHistoryStatus): value.value\n" +
                "define function ToString(value FilterOperator): value.value\n" +
                "define function ToString(value FlagStatus): value.value\n" +
                "define function ToString(value GoalLifecycleStatus): value.value\n" +
                "define function ToString(value GraphCompartmentRule): value.value\n" +
                "define function ToString(value GraphCompartmentUse): value.value\n" +
                "define function ToString(value GroupMeasure): value.value\n" +
                "define function ToString(value GroupType): value.value\n" +
                "define function ToString(value GuidanceResponseStatus): value.value\n" +
                "define function ToString(value GuidePageGeneration): value.value\n" +
                "define function ToString(value GuideParameterCode): value.value\n" +
                "define function ToString(value HTTPVerb): value.value\n" +
                "define function ToString(value IdentifierUse): value.value\n" +
                "define function ToString(value IdentityAssuranceLevel): value.value\n" +
                "define function ToString(value ImagingStudyStatus): value.value\n" +
                "define function ToString(value ImmunizationEvaluationStatus): value.value\n" +
                "define function ToString(value ImmunizationStatus): value.value\n" +
                "define function ToString(value InvoicePriceComponentType): value.value\n" +
                "define function ToString(value InvoiceStatus): value.value\n" +
                "define function ToString(value IssueSeverity): value.value\n" +
                "define function ToString(value IssueType): value.value\n" +
                "define function ToString(value LinkType): value.value\n" +
                "define function ToString(value LinkageType): value.value\n" +
                "define function ToString(value ListMode): value.value\n" +
                "define function ToString(value ListStatus): value.value\n" +
                "define function ToString(value LocationMode): value.value\n" +
                "define function ToString(value LocationStatus): value.value\n" +
                "define function ToString(value MeasureReportStatus): value.value\n" +
                "define function ToString(value MeasureReportType): value.value\n" +
                "define function ToString(value MediaStatus): value.value\n" +
                "define function ToString(value MedicationAdministrationStatus): value.value\n" +
                "define function ToString(value MedicationDispenseStatus): value.value\n" +
                "define function ToString(value MedicationKnowledgeStatus): value.value\n" +
                "define function ToString(value MedicationRequestIntent): value.value\n" +
                "define function ToString(value MedicationRequestPriority): value.value\n" +
                "define function ToString(value MedicationRequestStatus): value.value\n" +
                "define function ToString(value MedicationStatementStatus): value.value\n" +
                "define function ToString(value MedicationStatus): value.value\n" +
                "define function ToString(value MessageSignificanceCategory): value.value\n" +
                "define function ToString(value Messageheader_Response_Request): value.value\n" +
                "define function ToString(value MimeType): value.value\n" +
                "define function ToString(value NameUse): value.value\n" +
                "define function ToString(value NamingSystemIdentifierType): value.value\n" +
                "define function ToString(value NamingSystemType): value.value\n" +
                "define function ToString(value NarrativeStatus): value.value\n" +
                "define function ToString(value NoteType): value.value\n" +
                "define function ToString(value NutritiionOrderIntent): value.value\n" +
                "define function ToString(value NutritionOrderStatus): value.value\n" +
                "define function ToString(value ObservationDataType): value.value\n" +
                "define function ToString(value ObservationRangeCategory): value.value\n" +
                "define function ToString(value ObservationStatus): value.value\n" +
                "define function ToString(value OperationKind): value.value\n" +
                "define function ToString(value OperationParameterUse): value.value\n" +
                "define function ToString(value OrientationType): value.value\n" +
                "define function ToString(value ParameterUse): value.value\n" +
                "define function ToString(value ParticipantRequired): value.value\n" +
                "define function ToString(value ParticipantStatus): value.value\n" +
                "define function ToString(value ParticipationStatus): value.value\n" +
                "define function ToString(value PaymentNoticeStatus): value.value\n" +
                "define function ToString(value PaymentReconciliationStatus): value.value\n" +
                "define function ToString(value ProcedureStatus): value.value\n" +
                "define function ToString(value PropertyRepresentation): value.value\n" +
                "define function ToString(value PropertyType): value.value\n" +
                "define function ToString(value ProvenanceEntityRole): value.value\n" +
                "define function ToString(value PublicationStatus): value.value\n" +
                "define function ToString(value QualityType): value.value\n" +
                "define function ToString(value QuantityComparator): value.value\n" +
                "define function ToString(value QuestionnaireItemOperator): value.value\n" +
                "define function ToString(value QuestionnaireItemType): value.value\n" +
                "define function ToString(value QuestionnaireResponseStatus): value.value\n" +
                "define function ToString(value ReferenceHandlingPolicy): value.value\n" +
                "define function ToString(value ReferenceVersionRules): value.value\n" +
                "define function ToString(value ReferredDocumentStatus): value.value\n" +
                "define function ToString(value RelatedArtifactType): value.value\n" +
                "define function ToString(value RemittanceOutcome): value.value\n" +
                "define function ToString(value RepositoryType): value.value\n" +
                "define function ToString(value RequestIntent): value.value\n" +
                "define function ToString(value RequestPriority): value.value\n" +
                "define function ToString(value RequestStatus): value.value\n" +
                "define function ToString(value ResearchElementType): value.value\n" +
                "define function ToString(value ResearchStudyStatus): value.value\n" +
                "define function ToString(value ResearchSubjectStatus): value.value\n" +
                "define function ToString(value ResourceType): value.value\n" +
                "define function ToString(value ResourceVersionPolicy): value.value\n" +
                "define function ToString(value ResponseType): value.value\n" +
                "define function ToString(value RestfulCapabilityMode): value.value\n" +
                "define function ToString(value RiskAssessmentStatus): value.value\n" +
                "define function ToString(value SPDXLicense): value.value\n" +
                "define function ToString(value SearchComparator): value.value\n" +
                "define function ToString(value SearchEntryMode): value.value\n" +
                "define function ToString(value SearchModifierCode): value.value\n" +
                "define function ToString(value SearchParamType): value.value\n" +
                "define function ToString(value SectionMode): value.value\n" +
                "define function ToString(value SequenceType): value.value\n" +
                "define function ToString(value ServiceRequestIntent): value.value\n" +
                "define function ToString(value ServiceRequestPriority): value.value\n" +
                "define function ToString(value ServiceRequestStatus): value.value\n" +
                "define function ToString(value SlicingRules): value.value\n" +
                "define function ToString(value SlotStatus): value.value\n" +
                "define function ToString(value SortDirection): value.value\n" +
                "define function ToString(value SpecimenContainedPreference): value.value\n" +
                "define function ToString(value SpecimenStatus): value.value\n" +
                "define function ToString(value Status): value.value\n" +
                "define function ToString(value StrandType): value.value\n" +
                "define function ToString(value StructureDefinitionKind): value.value\n" +
                "define function ToString(value StructureMapContextType): value.value\n" +
                "define function ToString(value StructureMapGroupTypeMode): value.value\n" +
                "define function ToString(value StructureMapInputMode): value.value\n" +
                "define function ToString(value StructureMapModelMode): value.value\n" +
                "define function ToString(value StructureMapSourceListMode): value.value\n" +
                "define function ToString(value StructureMapTargetListMode): value.value\n" +
                "define function ToString(value StructureMapTransform): value.value\n" +
                "define function ToString(value SubscriptionChannelType): value.value\n" +
                "define function ToString(value SubscriptionStatus): value.value\n" +
                "define function ToString(value SupplyDeliveryStatus): value.value\n" +
                "define function ToString(value SupplyRequestStatus): value.value\n" +
                "define function ToString(value SystemRestfulInteraction): value.value\n" +
                "define function ToString(value TaskIntent): value.value\n" +
                "define function ToString(value TaskPriority): value.value\n" +
                "define function ToString(value TaskStatus): value.value\n" +
                "define function ToString(value TestReportActionResult): value.value\n" +
                "define function ToString(value TestReportParticipantType): value.value\n" +
                "define function ToString(value TestReportResult): value.value\n" +
                "define function ToString(value TestReportStatus): value.value\n" +
                "define function ToString(value TestScriptRequestMethodCode): value.value\n" +
                "define function ToString(value TriggerType): value.value\n" +
                "define function ToString(value TypeDerivationRule): value.value\n" +
                "define function ToString(value TypeRestfulInteraction): value.value\n" +
                "define function ToString(value UDIEntryType): value.value\n" +
                "define function ToString(value UnitsOfTime): value.value\n" +
                "define function ToString(value Use): value.value\n" +
                "define function ToString(value VariableType): value.value\n" +
                "define function ToString(value VisionBase): value.value\n" +
                "define function ToString(value VisionEyes): value.value\n" +
                "define function ToString(value VisionStatus): value.value\n" +
                "define function ToString(value XPathUsageType): value.value\n" +
                "define function ToString(value base64Binary): value.value\n" +
                "define function ToBoolean(value boolean): value.value\n" +
                "define function ToDate(value date): value.value\n" +
                "define function ToDateTime(value dateTime): value.value\n" +
                "define function ToDecimal(value decimal): value.value\n" +
                "define function ToDateTime(value instant): value.value\n" +
                "define function ToInteger(value integer): value.value\n" +
                "define function ToString(value string): value.value\n" +
                "define function ToTime(value time): value.value\n" +
                "define function ToString(value uri): value.value\n" +
                "define function ToString(value xhtml): value.value" +
                "//i nclude EmCareBase called Base\n" +
                "//i nclude EmCareConcepts called Cx\n" +
                "//i nclude EmCareDataElements called Dx\n" +
                "context Patient\n" +
                "\n" +
                "\n" +
                "/* EmCareDT01 : Register a child < 5 years*/\n" +
                "define \"EmCareDT01\":\n" +
                "    difference in years between today() and  Patient.BirthDate < 5\n" +
                "\n" +
                "/* Register a child < 5 years : */\n" +
                "define \"Register a child < 5 years\":\n" +
                "    \"EmCareDT01\"\n" +
                "\n" +
                "/* EmCareDT02 : Register the child in the encounter*/\n" +
                "define \"EmCareDT02\":\n" +
                "    difference in years between today() and  Patient.BirthDate < 5\n"
                ;
        return emCareCqlEngine.executePatient(str);
    }
}
