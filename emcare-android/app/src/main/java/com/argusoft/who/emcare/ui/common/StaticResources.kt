package com.argusoft.who.emcare.ui.common


val PATIENT_STATIC_RESOURCE = """{
                        "resourceType": "Patient",
                        "id": "<PATIENT_ID>",
                        "meta":{
                            "profile": [
                                "http://worldhealthorganization.github.io/ddcc/StructureDefinition/Patient-uv-ips"
                            ]
                        },
                        "name": [
                            {
                                "text": "NoVaxeninfant-f",
                                "use": "official"
                            }
                        ],
                        "birthDate": "2023-03-02",
                        "gender": "female"
                    }""".trimIndent()

val CAREPLAN_PATIENT_REGISTRATION = """{
  "resourceType": "CarePlan",
  "id": "IMMZD2DTMeasles",
  "contained": [
    {
      "resourceType": "RequestGroup",
      "id": "IMMZD2DTMeasles",
      "instantiatesCanonical": [
        "http://fhir.org/guides/who/smart-immunization/PlanDefinition/IMMZD2DTMeasles%7C0.1.0"
      ],
      "status": "draft",
      "intent": "proposal",
      "subject": {
        "reference": "IMMZ-Patient-NoVaxeninfant-f"
      },
      "action": [
        {
          "title": "Immunize patient for Measles",
          "description": "Provide measles immunization",
          "condition": [
            {
              "kind": "applicability",
              "expression": {
                "description": "Provision of the MCV dose",
                "language": "text/cql-identifier",
                "expression": "Provision of the MCV dose"
              }
            }
          ],
          "resource": {
            "reference": "MedicationRequest/IMMZD2DTMeaslesMR"
          }
        }
      ]
    },
    {
      "resourceType": "MedicationRequest",
      "id": "IMMZD2DTMeaslesMR",
      "meta": {
        "profile": [
          "http://hl7.org/fhir/uv/cpg/StructureDefinition/cpg-immunizationrequest"
        ]
      },
      "status": "draft",
      "intent": "proposal",
      "doNotPerform": false,
      "medicationCodeableConcept": {
        "coding": [
          {
            "system": "http://hl7.org/fhir/sid/icd-11",
            "code": "XM28X5",
            "display": "Measles vaccines"
          }
        ]
      },
      "subject": {
        "reference": "IMMZ-Patient-NoVaxeninfant-f"
      },
      "instantiatesCanonical": [
        "http://fhir.org/guides/who/smart-immunization/ActivityDefinition/IMMZD2DTMeaslesMR"
      ],
      "dispenseRequest": {
        "validityPeriod": {
          "start": "2024-03-02T00:00:00.000Z"
        }
      }
    }
  ],
  "instantiatesCanonical": [
    "http://fhir.org/guides/who/smart-immunization/PlanDefinition/IMMZD2DTMeasles%7C0.1.0"
  ],
  "status": "draft",
  "intent": "proposal",
  "subject": {
    "reference": "IMMZ-Patient-NoVaxeninfant-f"
  },
  "activity": [
    {
      "reference": {
        "reference": "#RequestGroup/IMMZD2DTMeasles"
      }
    }
  ]
}""".trimIndent()