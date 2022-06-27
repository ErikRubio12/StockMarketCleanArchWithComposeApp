package com.eriklabs.stockmarketcleanarchwithcomposeapp.presentation.company_listings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eriklabs.stockmarketcleanarchwithcomposeapp.domain.repository.StockRepository
import com.eriklabs.stockmarketcleanarchwithcomposeapp.presentation.company_listings.event.CompanyListingsEvent
import com.eriklabs.stockmarketcleanarchwithcomposeapp.presentation.company_listings.state.CompanyListingsState
import com.eriklabs.stockmarketcleanarchwithcomposeapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompanyListingsViewModel @Inject constructor(
    private val stockRepository: StockRepository
) : ViewModel() {

    var state by mutableStateOf(CompanyListingsState())

    private var searchJob: Job? = null

    fun onEvent(companyListingsEvent: CompanyListingsEvent) {
        when (companyListingsEvent) {
            is CompanyListingsEvent.OnSearchQueryChange -> {
                state = state.copy(searchQuery = companyListingsEvent.query)
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    delay(500L)
                    getCompanyListings()
                }
            }
            CompanyListingsEvent.Refresh -> {
                getCompanyListings(fetchRemote = true)
            }
        }
    }

    private fun getCompanyListings(
        query: String = state.searchQuery.lowercase(),
        fetchRemote: Boolean = false
    ) {
        viewModelScope.launch {
            stockRepository.getCompanyListing(fetchFromRemote = fetchRemote, query = query)
                .collect { result ->
                    when (result) {
                        is Resource.Error -> TODO()
                        is Resource.Loading -> {
                            state = state.copy(isLoading = result.isLoading)
                        }
                        is Resource.Success -> {
                            result.data?.let { listings ->
                                state = state.copy(
                                    companies = listings
                                )
                            }
                        }
                    }
                }
        }
    }

}