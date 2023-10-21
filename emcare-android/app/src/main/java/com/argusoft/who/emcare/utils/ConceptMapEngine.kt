package com.argusoft.who.emcare.utils

import org.hl7.fhir.exceptions.FHIRException
import org.hl7.fhir.r4.context.IWorkerContext
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.ConceptMap
import org.hl7.fhir.utilities.CanonicalPair


class ConceptMapEngine(private val context: IWorkerContext) {
    @Throws(FHIRException::class)
    fun translate(source: Coding, url: String): Coding? {
        val cm = context.fetchResource(
            ConceptMap::class.java, url
        )
            ?: throw FHIRException("Unable to find ConceptMap '$url'")
        return if (source.hasSystem()) translateBySystem(
            cm,
            source.system,
            source.code
        ) else translateByJustCode(cm, source.code)
    }

    @Throws(FHIRException::class)
    private fun translateByJustCode(cm: ConceptMap, code: String): Coding? {
        var ct: ConceptMap.SourceElementComponent? = null
        var cg: ConceptMap.ConceptMapGroupComponent? = null
        for (g in cm.group) {
            for (e in g.element) {
                if (code == e!!.code) {
                    if (e != null) throw FHIRException("Unable to process translate " + code + " because multiple candidate matches were found in concept map " + cm.url)
                    ct = e
                    cg = g
                }
            }
        }
        if (ct == null) return null
        var tt: ConceptMap.TargetElementComponent? = null
        for (t in ct.target) {
            if (!t.hasDependsOn() && !t.hasProduct()) {
                if (tt != null) throw FHIRException("Unable to process translate " + code + " because multiple targets were found in concept map " + cm.url)
                tt = t
            }
        }
        if (tt == null) return null
        val cp = CanonicalPair(cg!!.target)
        return Coding().setSystem(cp.url).setVersion(cp.version).setCode(tt.code)
            .setDisplay(tt.display)
    }

    //    private boolean isOkRelationship(ConceptMapRelationship relationship) {
    //        return relationship != null && relationship != ConceptMapRelationship.NOTRELATEDTO;
    //    }
    private fun translateBySystem(cm: ConceptMap, system: String, code: String): Coding {
        throw Error("Not done yet")
    }
}