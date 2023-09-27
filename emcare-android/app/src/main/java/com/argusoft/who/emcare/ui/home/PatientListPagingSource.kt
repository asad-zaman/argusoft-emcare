package com.argusoft.who.emcare.ui.home

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.argusoft.who.emcare.ui.common.model.PatientItem
import com.argusoft.who.emcare.ui.home.patient.PatientRepository

class PatientListPagingSource(
    private val patientRepository: PatientRepository,
    private val facilityId: String? = null,
    private val searchText: String? = null
) : PagingSource<Int, PatientItem>() {
    private val pageSize: Int = 10
    private var nextPageNumber = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PatientItem> {
        try {
            // Start refresh at page 1 if undefined.
            var patientsArrayList: MutableList<PatientItem> = mutableListOf()
            patientRepository.getPatients(searchText, facilityId, pageSize, nextPageNumber * pageSize).collect {
                patientsArrayList = it.data as MutableList<PatientItem>
            }.apply {
                nextPageNumber += 1
                return LoadResult.Page(
                    data = patientsArrayList,
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

    override fun getRefreshKey(state: PagingState<Int, PatientItem>): Int? {
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