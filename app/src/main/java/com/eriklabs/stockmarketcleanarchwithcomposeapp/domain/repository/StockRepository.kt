package com.eriklabs.stockmarketcleanarchwithcomposeapp.domain.repository

import com.eriklabs.stockmarketcleanarchwithcomposeapp.domain.model.CompanyListing
import com.eriklabs.stockmarketcleanarchwithcomposeapp.util.Resource
import kotlinx.coroutines.flow.Flow

interface StockRepository {
    suspend fun getCompanyListing(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>>
}