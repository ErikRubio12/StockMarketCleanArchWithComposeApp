package com.eriklabs.stockmarketcleanarchwithcomposeapp.presentation.company_listings.state

import com.eriklabs.stockmarketcleanarchwithcomposeapp.domain.model.CompanyListing

data class CompanyListingsState(
    val companies: List<CompanyListing> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val searchQuery: String = ""
)
