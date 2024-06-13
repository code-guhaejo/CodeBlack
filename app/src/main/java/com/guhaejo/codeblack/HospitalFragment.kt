package com.guhaejo.codeblack

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.guhaejo.codeblack.data.remote.map.LocationApi
import com.guhaejo.codeblack.view.HospitalDetailActivity
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest

class HospitalFragment : Fragment() {

    private lateinit var listView: ListView
    private lateinit var searchInput: EditText
    private lateinit var searchButton: ImageView
    private lateinit var locationApi: LocationApi
    private lateinit var locationPermissionRequest: ActivityResultLauncher<Array<String>>
    private lateinit var placesClient: PlacesClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_hospital, container, false)

        val hospitalArrowLeft = view.findViewById<ImageView>(R.id.arrow_left)
        listView = view.findViewById(R.id.listView)
        searchInput = view.findViewById(R.id.input_search)
        searchButton = view.findViewById(R.id.search)
        locationApi = LocationApi(requireContext())

        // Places API 초기화
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), BuildConfig.API_KEY)
        }
        placesClient = Places.createClient(requireContext())

        locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    getCurrentLocationAndUpdateHospitals()
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    getCurrentLocationAndUpdateHospitals()
                }
                else -> {
                    Toast.makeText(requireContext(), "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocationAndUpdateHospitals()
        } else {
            locationPermissionRequest.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION))
        }

        searchButton.setOnClickListener {
            val query = searchInput.text.toString()
            searchHospitals(query)
        }

        hospitalArrowLeft.setOnClickListener {
            val intent = Intent(requireContext(), BottomNavActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    private fun getCurrentLocationAndUpdateHospitals() {
        locationApi.getCurrentLocation { location ->
            if (location != null) {
                updateHospitalList(location)
            } else {
                Toast.makeText(requireContext(), "현재 위치를 받아올 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateHospitalList(location: Location) {
        val hospitals = listOf(
            Hospital("충북대학교병원 응급의료센터", "충북 청주시 서원구 개신동 62", "043-269-6114", LatLng(location.latitude, location.longitude)),
            Hospital("하나병원", "충북 청주시 서원구 사직동 472", "043-222-2119", LatLng(location.latitude, location.longitude)),
            Hospital("충청북도 청주의료원", "충북 청주시 상당구 용담동 68", "043-221-1114", LatLng(location.latitude, location.longitude)),
            Hospital("마이크로병원", "충북 청주시 흥덕구 복대동 2887", "043-276-8114", LatLng(location.latitude, location.longitude)),
            Hospital("청주현대병원", "충북 청주시 흥덕구 복대동 2888", "043-278-8114", LatLng(location.latitude, location.longitude)),
            Hospital("한국병원", "충북 청주시 상당구 남일면 123", "043-280-8114", LatLng(location.latitude, location.longitude)),
            Hospital("효성병원", "충북 청주시 서원구 성화동 456", "043-290-8114", LatLng(location.latitude, location.longitude))
        )

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, hospitals.map { it.name })
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedHospital = hospitals[position]
            searchHospitals(selectedHospital.name)
        }
    }

    private fun searchHospitals(query: String) {
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .setCountries("KR") // 국적 제한 추가
            .setSessionToken(AutocompleteSessionToken.newInstance())
            .build()

        placesClient.findAutocompletePredictions(request).addOnSuccessListener { response ->
            val hospitals = response.autocompletePredictions.map { prediction ->
                Hospital(
                    name = prediction.getPrimaryText(null).toString(),
                    address = prediction.getSecondaryText(null).toString(),
                    phoneNumber = prediction.placeId, // 임시로 placeId를 phoneNumber로 사용 (fetchPlaceDetails에서 실제 번호를 가져옴)
                    latLng = LatLng(0.0, 0.0) // 초기값 설정
                )
            }

            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, hospitals.map { it.name })
            listView.adapter = adapter

            listView.setOnItemClickListener { _, _, position, _ ->
                val selectedHospital = hospitals[position]
                fetchPlaceDetails(selectedHospital)
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(requireContext(), "병원 검색 실패: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchPlaceDetails(hospital: Hospital) {
        val placeFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.PHONE_NUMBER)
        val request = FetchPlaceRequest.builder(hospital.phoneNumber, placeFields).build()

        placesClient.fetchPlace(request).addOnSuccessListener { response ->
            val place = response.place
            val intent = Intent(activity, HospitalDetailActivity::class.java)
            intent.putExtra("HOSPITAL_NAME", place.name)
            intent.putExtra("LATITUDE", place.latLng?.latitude ?: 0.0)
            intent.putExtra("LONGITUDE", place.latLng?.longitude ?: 0.0)
            intent.putExtra("ADDRESS", place.address)
            intent.putExtra("PHONE_NUMBER", place.phoneNumber)
            startActivity(intent)
        }.addOnFailureListener { exception ->
            Toast.makeText(requireContext(), "병원 정보 불러오기 실패: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }

    data class Hospital(
        val name: String,
        val address: String,
        val phoneNumber: String,
        val latLng: LatLng
    )
}