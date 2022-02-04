package info.softweb.techbullspractical.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import info.softweb.techbullspractical.models.Movie
import info.softweb.techbullspractical.network.Repository
import info.softweb.techbullspractical.utils.Constants.Companion.API_KEY
import info.softweb.techbullspractical.utils.Constants.Companion.DEFAULT_QUERY_SEARCH_VALUE
import info.softweb.techbullspractical.utils.Constants.Companion.QUERY_API_KEY
import info.softweb.techbullspractical.utils.Constants.Companion.QUERY_SEARCH_KEY_DEFAULT
import info.softweb.techbullspractical.utils.NetworkResult
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {
    var moviesResponse: MutableLiveData<NetworkResult<Movie>> = MutableLiveData()
    var searchedRecipesResponse: MutableLiveData<NetworkResult<Movie>> = MutableLiveData()
    var isLoading:MutableLiveData<Boolean> = MutableLiveData()

    @RequiresApi(Build.VERSION_CODES.M)
    fun getMovies(queries: Map<String, String>) = viewModelScope.launch {
        getMoviesSafeCall(queries)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun searchMovies(searchQuery: Map<String, String>) = viewModelScope.launch {
        searchMoviesSafeCall(searchQuery)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private suspend fun getMoviesSafeCall(queries: Map<String, String>) {
        moviesResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getMovies(queries)
                moviesResponse.value = handleMoviesResponse(response)
                val foodRecipe = moviesResponse.value!!.data
            } catch (e: Exception) {
                moviesResponse.value = NetworkResult.Error("Movies not found.")
            }
        } else {
            moviesResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private suspend fun searchMoviesSafeCall(searchQuery: Map<String, String>) {
        searchedRecipesResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.searchMovies(searchQuery)
                searchedRecipesResponse.value = handleMoviesResponse(response)
            } catch (e: Exception) {
                searchedRecipesResponse.value = NetworkResult.Error("Recipes not found.")
            }
        } else {
            searchedRecipesResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }

    fun applyQueries(): HashMap<String, String> {
        val queries: HashMap<String, String> = HashMap()

        queries[QUERY_SEARCH_KEY_DEFAULT] = DEFAULT_QUERY_SEARCH_VALUE
        queries[QUERY_API_KEY] = API_KEY
        return queries
    }

    fun applySearchQuery(searchQuery: String): HashMap<String, String> {
        val queries: HashMap<String, String> = HashMap()
        queries[QUERY_SEARCH_KEY_DEFAULT] = searchQuery
        queries[QUERY_API_KEY] = API_KEY
        return queries
    }



    private fun handleMoviesResponse(response: Response<Movie>): NetworkResult<Movie>? {
        when {
            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error("Timeout")
            }
            response.code() == 402 -> {
                return NetworkResult.Error("API Key Limited.")
            }
            response.body()!!.search.isNullOrEmpty() -> {
                return NetworkResult.Error("Movies not found.")
            }
            response.isSuccessful -> {
                val foodRecipes = response.body()
                return NetworkResult.Success(foodRecipes!!)
            }
            else -> {
                return NetworkResult.Error(response.message())
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

}