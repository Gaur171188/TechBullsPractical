package info.softweb.techbullspractical.network

import info.softweb.techbullspractical.models.Movie
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface MovieApi {
    @GET("/")
    suspend fun getMovies(
        @QueryMap queries: Map<String, String>
      // @Query("s")movie : String,@Query("apikey" )apikey:String
    ): Response<Movie>

    @GET("/")
    suspend fun searchMovies(
        @QueryMap queries: Map<String, String>
        // @Query("s")movie : String,@Query("apikey" )apikey:String
    ): Response<Movie>

}