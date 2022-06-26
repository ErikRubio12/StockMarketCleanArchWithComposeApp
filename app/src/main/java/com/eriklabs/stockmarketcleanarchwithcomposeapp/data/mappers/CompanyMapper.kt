package com.eriklabs.stockmarketcleanarchwithcomposeapp.data.mappers

import com.eriklabs.stockmarketcleanarchwithcomposeapp.data.local.entities.CompanyListingEntity
import com.eriklabs.stockmarketcleanarchwithcomposeapp.domain.model.CompanyListing

fun CompanyListingEntity.toCompanyListing(): CompanyListing =
    CompanyListing(
        symbol = symbol,
        name = name,
        exchange = exchange
    )

fun CompanyListing.toCompanyListingEntity(): CompanyListingEntity =
    CompanyListingEntity(
        symbol = symbol,
        name = name,
        exchange = exchange
    )