package com.argusoft.who.emcare.ui.home

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.argusoft.who.emcare.ui.common.DATE_FORMAT
import com.argusoft.who.emcare.ui.common.model.ConsultationItemData
import com.argusoft.who.emcare.ui.common.stageToBadgeMap
import com.argusoft.who.emcare.ui.common.stageToIconMap
import com.argusoft.who.emcare.ui.home.patient.PatientRepository
import com.argusoft.who.emcare.utils.extention.orEmpty
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ConsultationListPagingSource(
    private val consultationFlowRepository: ConsultationFlowRepository,
    private val patientRepository: PatientRepository,
    private val searchText: String? = null
) : PagingSource<Int, ConsultationItemData>() {
    private val pageSize: Int = 10
    private var nextPageNumber = 0

    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, ConsultationItemData> {
        try {
            // Start refresh at page 1 if undefined.
            var consultationsArrayList: MutableList<ConsultationItemData> = mutableListOf()
            consultationFlowRepository.getAllLatestActiveConsultationsPaginated(pageSize, nextPageNumber * pageSize).collect{
                it.data?.forEach { consultationFlowItem ->
                    consultationFlowRepository.getConsultationSyncState(consultationFlowItem)
                        .collect { isSynced ->
                            patientRepository.getPatientById(consultationFlowItem.patientId)
                                .collect { patientResponse ->
                                    val patientItem = patientResponse.data
                                    if (patientItem != null) {
                                        consultationsArrayList.add(
                                            ConsultationItemData(
                                                name = patientItem.nameFirstRep.nameAsSingleString.orEmpty {
                                                    patientItem.identifierFirstRep.value
                                                        ?: "#${patientItem.id?.take(9)}"
                                                },
                                                gender = patientItem.genderElement?.valueAsString,
                                                identifier = patientItem.identifierFirstRep.value,
                                                dateOfBirth = patientItem.birthDateElement.valueAsString
                                                    ?: "Not Provided",
                                                dateOfConsultation = ZonedDateTime.parse(
                                                    consultationFlowItem.consultationDate?.substringBefore(
                                                        "+"
                                                    ).plus("Z[UTC]")
                                                ).format(DateTimeFormatter.ofPattern(DATE_FORMAT)),
                                                badgeText = stageToBadgeMap[consultationFlowItem.consultationStage],
                                                header = stageToBadgeMap[consultationFlowItem.consultationStage],
                                                consultationIcon = stageToIconMap[consultationFlowItem.consultationStage],
                                                consultationFlowItemId = consultationFlowItem.id,
                                                patientId = consultationFlowItem.patientId,
                                                encounterId = consultationFlowItem.encounterId,
                                                questionnaireId = consultationFlowItem.questionnaireId,
                                                structureMapId = consultationFlowItem.structureMapId,
                                                consultationStage = consultationFlowItem.consultationStage,
                                                questionnaireResponseText = consultationFlowItem.questionnaireResponseText,
                                                isActive = consultationFlowItem.isActive,
                                                isSynced = isSynced.data ?: true
                                            )
                                        )
                                    }
                                }
                        }
                }
            }.apply {
                nextPageNumber += 1
                if(searchText != null){
                    consultationsArrayList = consultationsArrayList.filter { consultationItemData ->
                        if (consultationItemData.identifier != null) {
                            searchText?.let { it1 ->
                                consultationItemData.name?.contains(
                                    it1,
                                    ignoreCase = true
                                )!! || consultationItemData.identifier.equals(
                                    it1,
                                    ignoreCase = true
                                )
                            }!!
                        } else {
                            searchText?.let { it1 ->
                                consultationItemData.name?.contains(
                                    it1,
                                    ignoreCase = true
                                )
                            }!!
                        }
                    } as MutableList<ConsultationItemData>
                }
                return LoadResult.Page(
                    data = consultationsArrayList,
                    prevKey = null,
                    nextKey = nextPageNumber
                )
            }
        } catch (e: Exception) {
            print(e.stackTrace)
            // Handle errors in this block and return LoadResult.Error for
            // expected errors (such as a network failure).
            return LoadResult.Error(throw Exception())
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ConsultationItemData>): Int? {
        // Try to find the page key of the closest page to anchorPosition from
        // either the prevKey or the nextKey; you need to handle nullability
        // here.
        //  * prevKey == null -> anchorPage is the first page.
        //  * nextKey == null -> anchorPage is the last page.
        //  * both prevKey and nextKey are null -> anchorPage is the
        //    initial page, so return null.
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
