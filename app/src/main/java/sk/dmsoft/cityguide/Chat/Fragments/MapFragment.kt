package sk.dmsoft.cityguide.Chat.Fragments

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import com.google.android.gms.maps.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_map.*
import sk.dmsoft.cityguide.Commons.MapMode
import sk.dmsoft.cityguide.Commons.Services.LocationService
import sk.dmsoft.cityguide.Commons.Services.LocationUpdateCallback
import sk.dmsoft.cityguide.Models.Chat.Message
import sk.dmsoft.cityguide.Models.Chat.MessageType

import sk.dmsoft.cityguide.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import sk.dmsoft.cityguide.Commons.AccountManager
import sk.dmsoft.cityguide.Commons.EAccountType


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MapFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 */
class MapFragment : Fragment(), LocationUpdateCallback {

    lateinit var locationService: LocationService
    var serviceBounded = false

    var meetingPoint: Marker? = null

    var mapMode: MapMode = MapMode.SetMeetingPoint
    var myPosition = LatLng(0.0, 0.0)
    var userPosition = LatLng(0.0, 0.0)
    var meetingPointPosition = LatLng(0.0, 0.0)
    var myMarker: Marker? = null
    var userMarker: Marker? = null

    var userId = ""
    var proposalId = 0

    private var mListener: OnFragmentInteractionListener? = null
    var googleMap: GoogleMap? = null

    fun init(proposalId: Int, userId: String){
        this.userId = userId
        this.proposalId = proposalId
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        // To-do("Logic when start location service")
        if (mapMode == MapMode.GoToMeetingPoint) {

        }
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (google_map as MapView).onCreate(savedInstanceState)

        MapsInitializer.initialize(activity)

        google_map.getMapAsync {
            googleMap = it
            meetingPoint = googleMap?.addMarker(MarkerOptions()
                    .position(meetingPointPosition)
                    .title("Meeting point")
                    .draggable(true))
            initCamera()
            if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                googleMap?.isMyLocationEnabled = true

            setMarkerCallback()
        }
        if (mapMode == MapMode.GoToMeetingPoint)
            meeting_point_controls.visibility = View.GONE
        else {
            meeting_point_confirm.setOnClickListener {
                mListener?.setMeetingPoint(googleMap!!.cameraPosition.target)
                meetingPoint?.position = googleMap!!.cameraPosition.target
            }
            cancel.setOnClickListener {
                mListener?.hideMap()
            }
        }

        change_meeting_point_card.setOnClickListener {
            updateMode(MapMode.SetMeetingPoint)
        }

        map_switcher.setTintColor(activity!!.resources.getColor(R.color.colorPrimary))

        map_switcher.setOnCheckedChangeListener { _, i ->
            if (i == R.id.normal_map)
                showNormalMap()

            else
                showSatelliteMap()
        }
    }

    fun updateUserPosition(position: LatLng){
        userPosition = position
        if (userMarker == null)
            userMarker = googleMap?.addMarker(
                    MarkerOptions()
                            .position(position)
                            .icon(BitmapDescriptorFactory.defaultMarker(if (AccountManager.accountType == EAccountType.guide) BitmapDescriptorFactory.HUE_BLUE else BitmapDescriptorFactory.HUE_GREEN))
                            .title(if (AccountManager.accountType == EAccountType.guide) "Tourist" else "Guide"))

        userMarker?.position = position
    }

    fun showNormalMap(){
        googleMap?.mapType = GoogleMap.MAP_TYPE_NORMAL
    }

    fun showSatelliteMap(){
        googleMap?.mapType = GoogleMap.MAP_TYPE_SATELLITE
    }

    fun updateMode(mode: MapMode = mapMode){
        when (mode) {
            MapMode.SetMeetingPoint -> {
                meeting_point_controls.visibility = View.VISIBLE
                meetingPoint?.alpha = 0f
                marker_icon.visibility = View.VISIBLE
                change_meeting_point_card.visibility = View.GONE
            }
            MapMode.GoToMeetingPoint -> {
                meeting_point_controls.visibility = View.GONE
                val serviceIntent = Intent(activity, LocationService::class.java)
                activity?.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
                meetingPoint?.alpha = 1f
                marker_icon.visibility = View.GONE
                change_meeting_point_card.visibility = View.VISIBLE
            }
        }

        mapMode = mode

    }

    fun updateMeetingPointPosition(position: LatLng){
        meetingPoint?.position = position
        meetingPointPosition = position
        if (googleMap != null)
            initCamera()
    }

    fun moveToUser(): Boolean{
        if (userPosition.latitude == 0.0 && userPosition.longitude == 0.0){
            Snackbar.make(activity!!.findViewById(android.R.id.content), "Position is not available", Snackbar.LENGTH_LONG).show()
            return false
        }
        else {
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(userPosition, 15f)
            googleMap?.animateCamera(cameraUpdate)
            return true
        }
    }

    fun initCamera(){
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(meetingPointPosition, 15f)
        googleMap?.animateCamera(cameraUpdate)
    }

    fun setMarkerCallback(){
        googleMap?.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDragStart(marker: Marker) {
                Log.e("mapDrag", "DragStart : " + marker.position)
            }

            override fun onMarkerDrag(marker: Marker) {
                Log.e("mapDrag", "Drag : " + marker.position)
            }

            override fun onMarkerDragEnd(marker: Marker) {
                meetingPointPosition = marker.position
            }
        })
    }

    fun hideChangeMeetingPoint(){
        change_meeting_point_card.visibility = View.GONE
    }

    private val serviceConnection = object: ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // cast the IBinder and get MyService instance
            val binder = service as LocationService.LocalBinder
            locationService = binder.service
            serviceBounded = true
            locationService.setCallbacks(this@MapFragment)
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            serviceBounded = false
        }
    }

    override fun updateLocation(position: Location) {
        myPosition = LatLng(position.latitude, position.longitude)
        val locationUpdateModel = Message()
        locationUpdateModel.ConversationId = proposalId
        locationUpdateModel.To = userId
        locationUpdateModel.Text = Gson().toJson(myPosition)
        locationUpdateModel.MessageType = MessageType.Map.value
        mListener?.updateMyLocation(Gson().toJson(locationUpdateModel))

        if (myMarker == null){
            myMarker = googleMap?.addMarker(
                    MarkerOptions()
                            .position(myPosition)
                            .icon(BitmapDescriptorFactory.defaultMarker(if (AccountManager.accountType == EAccountType.guide) BitmapDescriptorFactory.HUE_GREEN else BitmapDescriptorFactory.HUE_BLUE))
                            .title("You"))
        }

        if (meetingPointPosition.latitude == 0.0 && meetingPointPosition.longitude == 0.0){
            updateMeetingPointPosition(myPosition)
        }

        myMarker?.position = myPosition

        Log.e("Chat activity", Gson().toJson(locationUpdateModel))
    }

    fun enableLocationSharing(){

    }

    fun disableLocationSharing(){

    }


    override fun onResume() {
        super.onResume()
        google_map.onResume()
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        fun setMeetingPoint(position: LatLng)
        fun updateMyLocation(model: String)
        fun hideMap()
    }
}// Required empty public constructor
