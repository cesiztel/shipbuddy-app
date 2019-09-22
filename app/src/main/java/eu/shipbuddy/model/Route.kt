package eu.shipbuddy.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Route(
    @SerializedName("coordinates")
    @Expose
    var coordinates: List<Coordinate>? = mutableListOf()
)