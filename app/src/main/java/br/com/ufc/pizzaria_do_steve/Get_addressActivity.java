package br.com.ufc.pizzaria_do_steve;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Get_addressActivity extends AppCompatActivity implements OnMapReadyCallback {

    FusedLocationProviderClient client;
    AddressResultReceiver resultReceiver;
    GoogleMap mMap;


    List<Address> endereços;
    String rua = "";
    String cidade = "";
    String estado = "";
    String pais = "";
    String endereço = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getSupportActionBar().setTitle("Selecione sua rua:");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        client = LocationServices.getFusedLocationProviderClient(this);
        resultReceiver = new AddressResultReceiver(null);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMinZoomPreference(0.6f);
        mMap.setMaxZoomPreference(20.0f);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                LatLng mappoint;
                mappoint = latLng;

                if(mappoint == null)
                    return;

                try{
                    Geocoder geocoder = new Geocoder(Get_addressActivity.this, Locale.getDefault());
                    endereços = geocoder.getFromLocation(mappoint.latitude,mappoint.longitude,1);

                }catch (IOException e) {
                    return;
                }

                if(endereços.isEmpty() == true || endereços == null) {
                    Toast.makeText(Get_addressActivity.this, "Informações da zona não encontradas", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(endereços.get(0).getAddressLine(0).isEmpty() || endereços.get(0).getAddressLine(0) == null) {
                    Toast.makeText(Get_addressActivity.this, "Informações da zona não encontradas 1", Toast.LENGTH_SHORT).show();
                    return;
                }

                endereço = "(Número) " + endereços.get(0).getAddressLine(0);
                //Toast.makeText(Get_addressActivity.this,endereço,Toast.LENGTH_SHORT).show();

                Bundle bundle = new Bundle();
                bundle.putString("endereco",endereço);

                Intent resultIntent = new Intent();
                resultIntent.putExtras(bundle);
                setResult(RESULT_OK, resultIntent);
                finishActivity(1);
                onBackPressed();
            }
        });

    }



    @Override
    protected void onResume() {
        super.onResume();
        //verificando se o play services está habilitado e atualizado
        int errorCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        switch (errorCode) {
            case ConnectionResult
                    .SERVICE_MISSING:
                break;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                break;
            case ConnectionResult.SERVICE_DISABLED:
                Log.d("teste", "show dialog");
                GoogleApiAvailability.getInstance().getErrorDialog(this, errorCode,
                        0, new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                finish();
                            }
                        }).show();
                break;
            case ConnectionResult.SUCCESS:
                Log.d("teste", "Gplay habilitado e Atualizado");
                break;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(this,"Sem permissões de localização atribuidas",Toast.LENGTH_SHORT).show();
            return;
        }
        client.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location != null){
                            Log.d("teste", "latitude: " + location.getLatitude() + " longitude: " + location.getLongitude());
                            // Add a marker in Sydney and move the camera
                            LatLng origem = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(origem).title("Minha posição"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(origem));
                        }
                        if(!Geocoder.isPresent()){
                            return;
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        LocationRequest locationRequest = LocationRequest.create();
        //atualizacaos de location a cada 15 segundos
        locationRequest.setInterval(15 * 1000);
        //devolve location quando outro app usa a location
        locationRequest.setFastestInterval(5 * 1000);
        //gera uma precisao alta, embora consuma muita bateria
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(builder.build())
                .addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        //locationSettingsResponse.getLocationSettingsStates().isGpsPresent();
                        Log.d("teste","network present: " + locationSettingsResponse.getLocationSettingsStates().isNetworkLocationPresent());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Recomendacoes de falha do google
                        if(e instanceof ResolvableApiException){
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            try {
                                resolvable.startResolutionForResult(Get_addressActivity.this, 10);
                            } catch (IntentSender.SendIntentException e1){
                            }
                        }
                    }
                });

        final LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if(locationResult == null){
                    Log.d("teste", "local is null");
                    return;
                }
                for(Location location : locationResult.getLocations()){
                    Log.d("teste", "latitude callback: "+ location.getLatitude() + " longitude callback: " + location.getLongitude());
                    if(!Geocoder.isPresent()){
                        return;
                    }
                    else{
                        //startIntentService(location);
                    }
                }
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                Log.d("teste", "location avaliable: " + locationAvailability.isLocationAvailable());
            }
        };

        client.requestLocationUpdates(locationRequest,locationCallback,null);
    }

    public void startIntentService(Location location){
        Intent intent = new Intent(this, FetchAddressService.class);
        intent.putExtra(Constants.RECEIVER, resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        startService(intent);
    }


    private class AddressResultReceiver extends ResultReceiver {

        /**
         * Create a new ResultReceive to receive results.  Your
         * {@link #onReceiveResult} method will be called from the thread running
         * <var>handler</var> if given, or from an arbitrary thread if null.
         *
         * @param handler
         */
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if(resultData == null)
                return;

            final String adressOutput = resultData.getString(Constants.RESULT_DATA_KEY);

            if(adressOutput == null){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Get_addressActivity.this,adressOutput,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}