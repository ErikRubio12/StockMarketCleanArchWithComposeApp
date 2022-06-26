package com.eriklabs.stockmarketcleanarchwithcomposeapp.data.repository

import com.eriklabs.stockmarketcleanarchwithcomposeapp.data.csv.CSVParser
import com.eriklabs.stockmarketcleanarchwithcomposeapp.data.local.database.StockDatabase
import com.eriklabs.stockmarketcleanarchwithcomposeapp.data.mappers.toCompanyListing
import com.eriklabs.stockmarketcleanarchwithcomposeapp.data.mappers.toCompanyListingEntity
import com.eriklabs.stockmarketcleanarchwithcomposeapp.data.remote.StockApi
import com.eriklabs.stockmarketcleanarchwithcomposeapp.domain.model.CompanyListing
import com.eriklabs.stockmarketcleanarchwithcomposeapp.domain.repository.StockRepository
import com.eriklabs.stockmarketcleanarchwithcomposeapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImpl @Inject constructor(
    private val api: StockApi,
    private val db: StockDatabase,
    private val companyListingParser: CSVParser<CompanyListing>
) : StockRepository {

    private val stockDao = db.stockDao

    override suspend fun getCompanyListing(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>> {
        return flow {
            emit(Resource.Loading(true))
            val localListing = stockDao.searchCompanyListing(query = query)
            emit(Resource.Success(data = localListing.map {
                it.toCompanyListing()
            }))
            val isDbEmpty = localListing.isEmpty() && query.isBlank()
            val shouldJustLoadFromCache = isDbEmpty.not() && fetchFromRemote.not()
            if (shouldJustLoadFromCache) {
                emit(Resource.Loading(false))
                return@flow
            } else {
                val remoteListing = try {
                    val response = api.getListings()
                    companyListingParser.parse(response.byteStream())
                } catch (e: IOException){
                    emit(Resource.Error(
                        message = "Couldn't load data"
                    ))
                    null
                } catch (e: HttpException){
                    emit(Resource.Error(
                        message = "Couldn't load data"
                    ))
                    null
                }
                remoteListing?.let { listings ->
                    with(stockDao){
                        clearCompanyListings()
                        insertCompanyListings(
                            listings.map {
                                it.toCompanyListingEntity()
                            }
                        )
                    }
                    emit(Resource.Success(
                        data = stockDao.searchCompanyListing(
                            query = ""
                        ).map {
                            it.toCompanyListing()
                        }
                    ))
                    emit(Resource.Loading(false))
                }
            }
        }
    }
}