/*
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.argusoft.who.emcare.sync

import android.util.Log
import com.argusoft.who.emcare.data.local.pref.Preference
import com.google.android.fhir.sync.DownloadWorkManager
import com.google.android.fhir.sync.Request
import com.google.android.fhir.sync.SyncDataParams
import org.hl7.fhir.exceptions.FHIRException
import org.hl7.fhir.r4.model.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class DownloadWorkManagerImpl constructor(
  private val preference: Preference
): DownloadWorkManager {
  private val resourceTypeList = ResourceType.values().map { it.name }
  private val urls = LinkedList(listOf("PlanDefinition","Library", "StructureMap", "Questionnaire", "StructureDefinition", "ValueSet", "OperationDefinition", "Patient", "Encounter", "Observation", "RelatedPerson", "Binary","AuditEvent"))

//  private val formatString1: SimpleDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
  var formatStringGmt: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  var formatStringLocal: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm:ss a")
  var lastUpdatedTime: String = ""

  private fun getGmtTimeFromLastSyncTime(){
    if(preference.getLastSyncTimestamp().isNotEmpty()) {
      formatStringGmt.timeZone = TimeZone.getTimeZone("gmt")
      lastUpdatedTime = formatStringGmt.format(formatStringLocal.parse(preference.getLastSyncTimestamp()))
    }
  }

  override suspend fun getNextRequest(): Request? {
    Log.d("Sync Called","Inside getNextRequest")
    getGmtTimeFromLastSyncTime()
    var url = urls.poll() ?: return null
    if(url.contains("Patient", true)){
      url = url.plus("?_id=${preference.getFacilityId()}&_query=bundle")
    }else if(url.contains("RelatedPerson", true) || url.contains("Observation", true) || url.contains("Encounter", true)){
      url = url.plus("?_facilityId=${preference.getFacilityId()}")
    }
    if(preference.getLastSyncTimestamp().isNotEmpty()){
      url = affixLastUpdatedTimestamp(url, lastUpdatedTime, !url.contains("?_id")
              && !url.contains("?_facilityId"))
//      context.getLatestTimestampFor(ResourceType.fromCode(url.findAnyOf(resourceTypeList, ignoreCase = true)!!.second))?.let {
//        url = affixLastUpdatedTimestamp(url, it, !url.contains("?_id") || !url.contains("?_facilityId"))
//      }
    }

    return Request.of(url)
  }

  override suspend fun getSummaryRequestUrls(): Map<ResourceType, String> {
    getGmtTimeFromLastSyncTime()
    return urls.associate { urlString ->
      val stringWithCount = urlString.plus("?${SyncDataParams.SUMMARY_KEY}=${SyncDataParams.SUMMARY_COUNT_VALUE}")
      val stringWithFacilityId = if(stringWithCount.contains("Patient", true) || stringWithCount.contains("RelatedPerson", true) || stringWithCount.contains("Observation", true) || stringWithCount.contains("Encounter", true))  stringWithCount.plus("&_facilityId=${preference.getFacilityId()}&_query=summary") else stringWithCount
      var stringWithTimeStamp = stringWithFacilityId
      if(preference.getLastSyncTimestamp().isNotEmpty()){
        stringWithTimeStamp = affixLastUpdatedTimestamp(stringWithFacilityId, lastUpdatedTime, false)
//        context.getLatestTimestampFor(ResourceType.fromCode(stringWithFacilityId.substringBefore("?")))?.let {
//          stringWithTimeStamp = affixLastUpdatedTimestamp(stringWithFacilityId, it, false)
//        }
      }
      ResourceType.fromCode(urlString.substringBefore("?")) to
              stringWithTimeStamp
    }
  }

  override suspend fun processResponse(response: Resource): Collection<Resource> {
    // As per FHIR documentation :
    // If the search fails (cannot be executed, not that there are no matches), the
    // return value SHALL be a status code 4xx or 5xx with an OperationOutcome.
    // See https://www.hl7.org/fhir/http.html#search for more details.
    if (response is OperationOutcome) {
      throw FHIRException(response.issueFirstRep.diagnostics)
    }

    // If the resource returned is a List containing Patients, extract Patient references and fetch
    // all resources related to the patient using the $everything operation.
    if (response is ListResource) {
      for (entry in response.entry) {
        val reference = Reference(entry.item.reference)
        if (reference.referenceElement.resourceType.equals("Patient")) {
          val patientUrl = "${entry.item.reference}/\$everything"
          urls.add(patientUrl)
        }
      }
    }

    // If the resource returned is a Bundle, check to see if there is a "next" relation referenced
    // in the Bundle.link component, if so, append the URL referenced to list of URLs to download.
    if (response is Bundle) {
      val nextUrl = response.link.firstOrNull { component -> component.relation == "next" }?.url
      if (nextUrl != null) {
        urls.add(nextUrl)
      }
    }

    // Finally, extract the downloaded resources from the bundle.
    var bundleCollection: Collection<Resource> = mutableListOf()
    if (response is Bundle && response.type == Bundle.BundleType.SEARCHSET) {
      bundleCollection = response.entry.map { it.resource }
    }
    return bundleCollection
  }

  /**
   * Affixes the last updated timestamp to the request URL.
   *
   * If the request URL includes the `$everything` parameter, the last updated timestamp will be
   * attached using the `_since` parameter. Otherwise, the last updated timestamp will be attached
   * using the `_lastUpdated` parameter.
   */
  private fun affixLastUpdatedTimestamp(url: String, lastUpdated: String?, isFirstQueryParam: Boolean): String {
    var downloadUrl = url

    // Affix lastUpdate to non-$everything queries as per:
    // https://hl7.org/fhir/operation-patient-everything.html
    if (!downloadUrl.contains("\$everything") && lastUpdated != null) {
      downloadUrl = if(isFirstQueryParam) "$downloadUrl?_lastUpdated=gt$lastUpdated"
      else "$downloadUrl&_lastUpdated=gt$lastUpdated"
    }

    return downloadUrl
  }
}