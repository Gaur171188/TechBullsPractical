package info.softweb.techbullspractical.di

import info.softweb.techbullspractical.models.Movie
import info.softweb.techbullspractical.network.MovieApi
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val movieApi: MovieApi
) {

    suspend fun getMovies(queries: Map<String, String>): Response<Movie> {
        return movieApi.getMovies(queries)
    }

    suspend fun searchMovies(searchQuery: Map<String, String>): Response<Movie> {
        return movieApi.searchMovies(searchQuery)
    }

}