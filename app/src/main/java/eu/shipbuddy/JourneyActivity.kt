package eu.shipbuddy

import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.data.geojson.GeoJsonLayer
import eu.shipbuddy.model.Coordinate
import kotlinx.android.synthetic.main.activity_journey.*
import kotlinx.android.synthetic.main.activity_journey.view.*
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.GroundOverlay2
import org.osmdroid.views.overlay.Polyline
import pl.tsluszniak.frascati_andro_osm.BBox
import pl.tsluszniak.frascati_andro_osm.DataStorage
import pl.tsluszniak.frascati_andro_osm.MapMarker
import pl.tsluszniak.frascati_andro_osm.OverlayTile
import android.preference.PreferenceManager
import org.osmdroid.config.Configuration


class JourneyActivity : AppCompatActivity() {

    var map: MapView? = null
    val localData = DataStorage.LocalData
    var currentLocationMarker:org.osmdroid.views.overlay.Marker? = null

    //private lateinit var mMap: GoogleMap
    //private lateinit var shipMarker: Marker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val ctx = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))

        setContentView(R.layout.activity_journey)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        map = findViewById(R.id.map) as MapView
        mapSetup()

        start_route.setOnClickListener { startRoute() }
    }

    public override fun onResume() {
        super.onResume()
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map?.onResume() //needed for compass, my location overlays, v6.0.0 and up
    }

    public override fun onPause() {
        super.onPause()
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map?.onPause()  //needed for compass, my location overlays, v6.0.0 and up
    }

    fun setStartLocation(){

        val mapController = map?.getController()
        mapController?.setZoom(14)
        val startPoint = GeoPoint(49.532032812572936, 8.44423770904541)
        mapController?.setCenter(startPoint)
    }

    fun mapSetup(){

        map?.setTileSource(TileSourceFactory.MAPNIK)
        //TileSourceFactory.OPEN_SEAMAP displays the objects/POIs for a map, but no land?
        //MAPNIK displays map as in OpenStreetMat it seems

        map?.setMultiTouchControls(true)
        map?.setBuiltInZoomControls(false)

        setStartLocation()
        generateMapContents()
        addImageOverlay()
        addMarkers()
        addPolygon()
        addPolyline()
        refreshMapRendering()
    }

   /* override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val departure = com.google.android.gms.maps.model.LatLng(49.532032812572936, 8.44423770904541)
        val markerOptions = com.google.android.gms.maps.model.MarkerOptions().position(departure).title("Marker in Sydney")
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ship_marker))

        shipMarker = mMap.addMarker(markerOptions)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(departure, 13f))

        addRoutePolyline()
    }*/

    /*private fun addRoutePolyline() {
        val layer = GeoJsonLayer(mMap, R.raw.map, applicationContext)
        layer.addLayerToMap()
    }*/

    fun addPolyline(){

        for (coor in localData.routeCoordinates){

            localData.routeWaypoints.add(GeoPoint(coor.latitude, coor.longitude))
        }

        val routeLine = Polyline()
        routeLine.setPoints(localData.routeWaypoints)
        map?.getOverlayManager()?.add(routeLine)
    }

    fun addMarkers(){

        for (marker in localData.markers){

            val mapMarker = org.osmdroid.views.overlay.Marker(map)
            mapMarker.position = marker.coordinates
            mapMarker.setAnchor(org.osmdroid.views.overlay.Marker.ANCHOR_CENTER, org.osmdroid.views.overlay.Marker.ANCHOR_BOTTOM)
            map?.getOverlays()?.add(mapMarker)
            mapMarker.setIcon(marker.icon);
            mapMarker.setTitle(marker.title);
        }

        currentLocationMarker = org.osmdroid.views.overlay.Marker(map)
        currentLocationMarker?.position = GeoPoint(localData.currentLocation.latitude, localData.currentLocation.longitude)
        currentLocationMarker?.setAnchor(org.osmdroid.views.overlay.Marker.ANCHOR_CENTER, org.osmdroid.views.overlay.Marker.ANCHOR_BOTTOM)
        map?.getOverlays()?.add(currentLocationMarker)
        currentLocationMarker?.setIcon(resources.getDrawable(R.drawable.ic_directions_boat_black_24dp));
        currentLocationMarker?.setTitle("Your ship");
    }

    fun addPolygon(){

        /*//val geoPoints = ArrayList<GeoPoint>()
        var geoPoints:MutableList<GeoPoint> = ArrayList<GeoPoint>()

        //add your points here
        geoPoints.add(GeoPoint(53.095634, 8.759768))
        geoPoints.add(GeoPoint(53.099634, 8.759768))
        geoPoints.add(GeoPoint(53.095634, 8.750768))


        val polygon = Polygon()    //see note below
        polygon.setFillColor(Color.argb(75, 200, 100, 30))
        geoPoints.plus(geoPoints.get(0))    //forces the loop to close
        polygon.setPoints(geoPoints)
        polygon.setTitle("Mud in the water")

        //polygons supports holes too, points should be in a counter-clockwise order
        var holes:MutableList<List<GeoPoint>> = ArrayList()
        holes.add(geoPoints)
        polygon.setHoles(holes)

        map?.getOverlayManager()?.add(polygon)*/
    }

    fun addImageOverlay(){

        for (tile in localData.mapPrimaryOverlays){

            val overlay = GroundOverlay2()
            overlay.transparency = 0.5f
            val bitmap = tile.image
            overlay.setImage(bitmap)
            overlay.setPosition(GeoPoint(tile.bbox.bottomLat.toDouble(), tile.bbox.rightLon.toDouble()), GeoPoint(tile.bbox.topLat.toDouble(), tile.bbox.leftLon.toDouble()))
            map?.getOverlayManager()?.add(overlay)
        }

        for (tile in localData.mapOtherOverlays){

            val overlay = GroundOverlay2()
            overlay.transparency = 0.7f
            val bitmap = tile.image
            overlay.setImage(bitmap)
            overlay.setPosition(GeoPoint(tile.bbox.bottomLat.toDouble(), tile.bbox.rightLon.toDouble()), GeoPoint(tile.bbox.topLat.toDouble(), tile.bbox.leftLon.toDouble()))
            map?.getOverlayManager()?.add(overlay)
        }
    }

    //prepares overlays, markers, route from preprogrammed resources, not online API
    fun generateMapContents(){

        //primary true color map tiles
        val trueColorImageRhein = BitmapFactory.decodeResource(resources, R.drawable.mannheim_true_color)
        val trueColorMapTileRhein = OverlayTile(BBox(49.169134f, 49.744006f, 7.554474f, 9.321899f), trueColorImageRhein)
        localData.mapPrimaryOverlays.add(trueColorMapTileRhein)

        //other map overlay tiles

        //markers of events etc
        val mapMarker1 = MapMarker(GeoPoint(49.528105376477484, 8.429088592529297), "Heavy storm", resources.getDrawable(R.drawable.ic_error_outline_black_24dp))
        val mapMarker2 = MapMarker(GeoPoint(49.51088778512465, 8.435955047607422), "Increased current", resources.getDrawable(R.drawable.ic_error_outline_black_24dp))
        val mapMarker3 = MapMarker(GeoPoint(49.470424891880455, 8.4639573097229), "Alien invasion", resources.getDrawable(R.drawable.ic_error_outline_black_24dp))
        localData.markers.add(mapMarker1)
        localData.markers.add(mapMarker2)
        localData.markers.add(mapMarker3)

        //marker of cuurrent ship position
        /*val currentPositionMarker = MapMarker(GeoPoint(53.89042920541724, 9.742744873046875), "current position", resources.getDrawable(R.drawable.ic_directions_boat_black_24dp))
        localData.markers.add(currentPositionMarker)*/

        //route waypoints
        /*localData.routeWaypoints.add(GeoPoint(53.58842920541724, 9.154744873046875))
        localData.routeWaypoints.add(GeoPoint(53.59842920542724, 9.149744873046875))
        localData.routeWaypoints.add(GeoPoint(53.60842920543724, 9.244744873046875))
        localData.routeWaypoints.add(GeoPoint(53.61842920544724, 9.264744873046875))
        localData.routeWaypoints.add(GeoPoint(53.92842920545724, 9.264744873046875))*/

        localData.routeCoordinates?.addAll(listOf(
            Coordinate(49.532032812572936, 8.44423770904541),
            Coordinate(49.5345116700754, 8.441362380981445),
            Coordinate(49.53665619816734,8.434281349182129),
            Coordinate(49.53980319239489, 8.426170349121094),
            Coordinate(49.53955255426448, 8.424239158630371),
            Coordinate(49.53245060635386, 8.426942825317383),
            Coordinate(49.528105376477484, 8.429088592529297),
            Coordinate(49.52314688596823, 8.431148529052734),
            Coordinate(49.51662765581162, 8.433809280395508),
            Coordinate(49.51239247656423, 8.435697555541992 ),
            Coordinate(49.51239247656423, 8.435697555541992 ),
            Coordinate(49.51088778512465, 8.435955047607422),
            Coordinate(49.49957325239878, 8.44076156616211),
            Coordinate(49.495726830156215, 8.441362380981445),
            Coordinate(49.4918801055978, 8.44325065612793),
            Coordinate(49.49015176860387, 8.444795608520508),
            Coordinate(49.48697369938796, 8.448829650878906),
            Coordinate(49.484227569928734, 8.452520370483398),
            Coordinate(49.48280565557542, 8.45417261123657),
            Coordinate(49.47957134366563, 8.457777500152588),
            Coordinate(49.477772862299794, 8.459472656249998),
            Coordinate(49.47711758523117, 8.460009098052977),
            Coordinate(49.475291128402944, 8.461318016052246),
            Coordinate(49.47193082761651, 8.46342086791992),
            Coordinate(49.470424891880455, 8.4639573097229),
            Coordinate(49.46950457502841, 8.4639573097229),
            Coordinate(49.468138012048314, 8.463764190673828),
            Coordinate(49.46554422645411, 8.46170425415039),
            Coordinate(49.46441463174106, 8.459815979003906),
            Coordinate(49.46388468944422, 8.458313941955566),
            Coordinate(49.4622390426202, 8.451511859893799),
            Coordinate(49.46182064902241, 8.449795246124268),
            Coordinate(49.46096990435652, 8.44724178314209),
            Coordinate(49.459951780669854, 8.445439338684082),
            Coordinate(49.45855705635253, 8.443765640258789),
            Coordinate(49.45748309157832, 8.442821502685547),
            Coordinate(49.45604645255071, 8.44200611114502),
            Coordinate(49.453996030707316, 8.441512584686278),
            Coordinate(49.45274062805976, 8.441619873046875),
            Coordinate(49.45169443462494, 8.441920280456541),
            Coordinate(49.4507598162731, 8.442327976226807),
            Coordinate(49.449197429385855, 8.443529605865479),
            Coordinate(49.44759314103577, 8.44524621963501),
            Coordinate(49.44651893601394, 8.447070121765137),
            Coordinate(49.44555631671314, 8.449108600616455),
            Coordinate(49.4442867464167, 8.452842235565186),
            Coordinate(49.44377053810456, 8.455266952514648),
            Coordinate(49.44324037255945, 8.461661338806152),
            Coordinate(49.44321246895094, 8.465738296508789),
            Coordinate(49.4434077938772, 8.470888137817383),
            Coordinate(49.443686828136414, 8.476037979125975),
            Coordinate(49.44399376398752, 8.482303619384766),
            Coordinate(49.44399376398752, 8.485779762268065),
            Coordinate(49.443644973098785, 8.491873741149902),
            Coordinate(49.44326827615212, 8.495221138000488),
            Coordinate(49.44248696955547, 8.499534130096436),
            Coordinate(49.44110570064942, 8.50288152694702),
            Coordinate(49.43941743025901, 8.505327701568604),
            Coordinate(49.437603521269345, 8.506958484649658),
            Coordinate(49.43583140693091, 8.507859706878662),
            Coordinate(49.43371036700827, 8.508353233337402),
            Coordinate(49.43008205976997, 8.507537841796875),
            Coordinate(49.428351541798584, 8.50642204284668),
            Coordinate(49.42678844083925, 8.505327701568604),
            Coordinate(49.42469492404094, 8.503975868225098),
            Coordinate(49.42194530294639, 8.502130508422852),
            Coordinate(49.41831612552712, 8.500220775604248),
            Coordinate(49.4128858165943, 8.497796058654785),
            Coordinate(49.40943746795513, 8.49693775177002),
            Coordinate(49.40751075473673, 8.496487140655518),
            Coordinate(49.40587717777724, 8.496272563934326)
        ))

        //polygons

    }

    fun refreshMapRendering(){

        map?.invalidate()
    }


    private fun startRoute() {
        start_route.visibility = View.GONE

        val mainHandler = Handler(Looper.getMainLooper())

        var counter = 0
        mainHandler.post(object : Runnable {
            override fun run() {
                if (counter <= localData.routeCoordinates.size - 1) {
                    val coordinate = localData.routeCoordinates.get(counter)
                    moveShip(coordinate)
                    showInformation(counter)
                    counter++
                    mainHandler.postDelayed(this, 2000)
                }
            }
        })
    }

    private fun showInformation(counter: Int) {
        when {
            counter == 0 -> {
                eta_cardview.visibility = View.VISIBLE
            }
            counter == 3 -> {
                eta_cardview.visibility = View.GONE
                // Park
                point_of_interest_cardview.visibility = View.VISIBLE
                point_of_interest_image.setImageDrawable(getDrawable(R.drawable.park))
                point_of_interest_title.text = "Near by you"
                point_of_interest_information.text = "Rheinstrand Mannheim"
            }
            counter == 6 -> {
                eta_cardview.visibility = View.GONE
                // Restaurant
                point_of_interest_cardview.visibility = View.VISIBLE
                point_of_interest_image.setImageDrawable(getDrawable(R.drawable.restaurant))
                point_of_interest_title.text = "Near by you"
                point_of_interest_information.text = "Restaurant Heimat"
            }
            counter == 9 -> {
                eta_cardview.visibility = View.GONE
                point_of_interest_cardview.visibility = View.GONE
                // Incident
                incident_cardview.visibility = View.VISIBLE
            }
            counter == 13 -> {
                eta_cardview.visibility = View.GONE
                incident_cardview.visibility = View.GONE
                // Hotel
                point_of_interest_cardview.visibility = View.VISIBLE
                point_of_interest_image.setImageDrawable(getDrawable(R.drawable.hotel))
                point_of_interest_title.text = "Suggested stop"
                point_of_interest_information.text = "Heim am Rhein Speyer"
            }
            (counter > 15) -> {
                point_of_interest_cardview.visibility = View.GONE
                incident_cardview.visibility = View.GONE

                eta_cardview.visibility = View.VISIBLE
                eta_cardview.destination_eta_delay.text = "+30"
            }
        }
    }

    private fun moveShip(coordinate: Coordinate) {

        //val departure = com.google.android.gms.maps.model.LatLng(coordinate.latitude, coordinate.longitude)
        //val markerOptions = com.google.android.gms.maps.model.MarkerOptions().position(departure).title("My ship")
        //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ship_marker))

        localData.currentLocation = coordinate
        currentLocationMarker?.position = GeoPoint(localData.currentLocation.latitude, localData.currentLocation.longitude)

        refreshMapRendering()
    }
}
