package info.softweb.techbullspractical.models


import com.google.gson.annotations.SerializedName

data class Search(
    @SerializedName("imdbID")
    var imdbID: String?,
    @SerializedName("Poster")
    var poster: String?,
    @SerializedName("Title")
    var title: String?,
    @SerializedName("Type")
    var type: String?,
    @SerializedName("Year")
    var year: String?
)