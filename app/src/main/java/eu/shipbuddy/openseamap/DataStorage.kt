package pl.tsluszniak.frascati_andro_osm

import eu.shipbuddy.model.Coordinate
import org.osmdroid.util.GeoPoint

class DataStorage{

    companion object LocalData{

        val mapPrimaryOverlays = ArrayList<OverlayTile>()
        val mapOtherOverlays = ArrayList<OverlayTile>()
        val routeWaypoints = ArrayList<GeoPoint>()
        val markers = ArrayList<MapMarker>()
        var routeCoordinates:MutableList<Coordinate> = mutableListOf()
        var currentLocation:Coordinate = Coordinate(49.532032812572936, 8.44423770904541)
    }
}