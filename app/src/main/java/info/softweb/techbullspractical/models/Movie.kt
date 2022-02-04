package info.softweb.techbullspractical.models

import com.google.gson.annotations.SerializedName


data class Movie(
    @SerializedName("Response")
    var response: String?,
    @SerializedName("Search")
    var search: List<Search>?= listOf(),
    @SerializedName("totalResults")
    var totalResults: String?
)