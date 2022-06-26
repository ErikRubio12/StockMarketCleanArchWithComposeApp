package com.eriklabs.stockmarketcleanarchwithcomposeapp.data.repository

import com.eriklabs.stockmarketcleanarchwithcomposeapp.data.local.database.StockDatabase
import com.eriklabs.stockmarketcleanarchwithcomposeapp.data.mappers.toCompanyListing
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
    val api: StockApi,
    val db: StockDatabase
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
                    response.byteStream()
                } catch (e: IOException){
                    emit(Resource.Error(
                        message = "Couldn't load data"
                    ))
                } catch (e: HttpException){
                    emit(Resource.Error(
                        message = "Couldn't load data"
                    ))
                }
            }
        }
    }
}