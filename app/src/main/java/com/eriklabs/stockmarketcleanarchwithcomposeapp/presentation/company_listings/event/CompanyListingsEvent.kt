package com.eriklabs.stockmarketcleanarchwithcomposeapp.presentation.company_listings.event

sealed class CompanyListingsEvent {
    object Refresh : CompanyListingsEvent()
    data class OnSearchQueryChange(val query: String) : CompanyListingsEvent()
}
