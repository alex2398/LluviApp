package com.avalladares.lluviapp;


import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 1500;
    private final static String REQUEST_WEATHER = "weather";
    private final static String REQUEST_CITY = "city";
    private final static String REQUEST_BACKGROUND="background";
    public static final String TAG = MainActivity.class.getSimpleName();

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private double currentLongitude;
    private double currentLatitude;

    private CurrentWeather mCurrentWeather;
    private CurrentLocation mCurrentLocation;
    private FlickrImages mFlickrImages;

    public String weatherTag=null;
    public String cityTag=null;
    String pictureSizeUrl = "url_l";

    private Location location;

    // New ButterKnife method

    @InjectView(R.id.temperatureLabel)
    TextView mTemperatureValue;
    @InjectView(R.id.humidityValue)
    TextView mHumidityValue;
    @InjectView(R.id.precipValue)
    TextView mPrecipValue;
    @InjectView(R.id.summaryLabel)
    TextView mSummaryLabel;
    @InjectView(R.id.iconImageView)
    ImageView mIconImageView;
    @InjectView(R.id.refreshImageView)
    ImageView mRefreshImageView;
    @InjectView(R.id.progressBar)
    ProgressBar mProgressBar;
    @InjectView(R.id.locationLabel)
    TextView mLocationLabel;
    @InjectView(R.id.lastUpdateLabel)
    TextView mLastUpdateLabel;
    @InjectView(R.id.backgroundLayout)
    RelativeLayout mBackgroundLayout;
    @InjectView(R.id.cityLabel)
    TextView mCityLabel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        // Create Google API Client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(2 * 1000); // 2 second, in milliseconds

        // By default, set progressbar invisible
        mProgressBar.setVisibility(View.INVISIBLE);

        // Method for updating data on image click
        mRefreshImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                applyAnimation(Techniques.BounceIn, 200, R.id.refreshImageView);
                getDataAllData();


            }
        });

    }

    // Method for getting forecast and location data
    private void getDataAllData() {

        getForecast();
        getLocation(currentLatitude, currentLongitude);

    }

    // Method for getting location using google maps
    private void getLocation(double lat, double lon) {
        //String apiKey = "27974c4bc33201748eaf542a6769c3b7";
        String locationUrl = "http://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lon + "&sensor=false";
        getJSONData(locationUrl, "location");
    }

    // Method for getting forecast data from forecast.io
    private void getForecast() {
        String apiKey = "27974c4bc33201748eaf542a6769c3b7";
        String forecastUrl = "https://api.forecast.io/forecast/" + apiKey +
                "/" + currentLatitude + "," + currentLongitude + "?units=auto&lang=es";
        getJSONData(forecastUrl, "weather");
    }
/*
    private void getBackgroundFlickr() {
        String apiKey = "b800034851ef22708d4bf96f2df557f2";
        Log.d(TAG, "ALEX");

        String flickrUrl="https://api.flickr.com/services/rest/?&method=flickr.groups.pools.getPhotos&api_key=" + apiKey +
                "&group_id=1463451@N25&tags=" + cityTag + "&extras=" + pictureSizeUrl +"&format=json&nojsoncallback=1";


        getJSONData(flickrUrl,"background");

    }
*/


    // Method for getting JSON data
    // Parameters:
    // Url (String) : url for retrieving JSON data
    // dataType (String) :
    //          "weather" for forecast
    //          "city" for location

    private void getJSONData(String Url, final String method) {


        // Check network availability
        if (isNetworkAvailable()) {

            // All interactivities with UI must go on main thread
            // We toggle progressbar visibility
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    toggleRefresh();
                }
            });

            // OkHttpClient for making http requests
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(Url)
                    .build();

            Call call = client.newCall(request);


            // We make the request in another thread using enqueue() method

            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
                    alertUserAboutError(getString(R.string.error_message));
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
                    try {
                        String jsondata = response.body().string();

                        // Response OK
                        if (response.isSuccessful()) {
                            if (method.equals("weather")) {
                                mCurrentWeather = getCurrentWeather(jsondata);



                            }
                            if (method.equals("location")) {
                                mCurrentLocation = getCurrentLocation(jsondata);

                            }

                            if (method.equals("background")) {

                                mFlickrImages = getFlickrImages(jsondata);
                            }


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if (method.equals("weather")) {
                                        updateWeatherUI();
                                    }
                                    if (method.equals("location")) {
                                        updateLocationUI();
                                    }

                                    if (method.equals("background")) {
                                        updateBackgroundUIFlickr();
                                    }
                                }
                            });
                            // Response FAILS

                        } else {
                            alertUserAboutError(getString(R.string.error_Message));

                        }
                    } catch (IOException | JSONException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    }
                }
            });
            // If network is not available
        } else {
            if (method.equals("weather")) {
                alertUserAboutError("Error de red al obtener clima");
                mSummaryLabel.setText("");
            }
            if (method.equals("location")) {
                alertUserAboutError("Error de red al obtener ubicacion");
                mSummaryLabel.setText("");
            }
        }
    }

    // Methods for Google API Client
    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // We remove all location updates (for what??)
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }


    // Method for setting long and lat using location object
    private void getLongLat(Location location) {

        currentLongitude = location.getLongitude();
        currentLatitude = location.getLatitude();
    }

    // Method for getting longitude and latitude
    // Parameters:
    // Longitude (double)
    // Latitude (double)
    // Deprecated: obtained with google services

    private void getlonglat_deprecated() {

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
    }


    // Method for toggling progress bar visibility
    private void toggleRefresh() {

        if (mProgressBar.getVisibility() == (View.INVISIBLE)) {
            mProgressBar.setVisibility(View.VISIBLE);
            mRefreshImageView.setVisibility(View.INVISIBLE);
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
            mRefreshImageView.setVisibility(View.VISIBLE);
        }

    }

    // Method for updating display weather data
    // Animations added
    private void updateWeatherUI() {

        mTemperatureValue.setText(mCurrentWeather.getTemperature() + "");
        applyAnimation(Techniques.ZoomIn, 500, R.id.temperatureLabel);

        mLastUpdateLabel.setText(getString(R.string.last_update) + " " + mCurrentWeather.getFormattedTime());
        mHumidityValue.setText(mCurrentWeather.getHumidity() + "%");
        mPrecipValue.setText(mCurrentWeather.getPrecipChance() + "%");
        mSummaryLabel.setText(mCurrentWeather.getSummary() + "");

        Drawable drawable = getResources().getDrawable(mCurrentWeather.getIconId());
        mIconImageView.setImageDrawable(drawable);
        applyAnimation(Techniques.FadeIn, 1500, R.id.iconImageView);

        Drawable draw = getResources().getDrawable(mCurrentWeather.getBgId());
        mBackgroundLayout.setBackground(draw);
        applyAnimation(Techniques.FadeIn, 500, R.id.backgroundLayout);

    }

    // Method for updating display location data
    private void updateLocationUI() {
        mLocationLabel.setText(mCurrentLocation.getStreet() + "");
        applyAnimation(Techniques.FadeIn, 400, R.id.locationLabel);

        mCityLabel.setText(mCurrentLocation.getCity() + "");
        applyAnimation(Techniques.FadeIn, 400, R.id.cityLabel);

        }

    private void updateBackgroundUIFlickr() {
        AQuery aq = new AQuery(this);

        String url = mFlickrImages.getUrlImage();
        boolean memCache = false;
        boolean fileCache = false;

        //aq.id(mBackgroundLayout).image(url);

        aq.ajax(url.trim(), Bitmap.class, 0, new AjaxCallback<Bitmap>() {
            @Override
            public void callback(String url, Bitmap object, AjaxStatus status) {
                super.callback(url, object, status);
                mBackgroundLayout.setBackground(new BitmapDrawable(object));

            }
        });

        applyAnimation(Techniques.FadeIn, 500, R.id.backgroundLayout);

    }

    // Method for using animations in a resource
    // Parameters:
    // technique (Technique)
    // duration (int) animation duration (in ms)
    // view (int) resource id
    private void applyAnimation(Techniques technique, int duration, int view) {
        YoYo.with(technique)
                .duration(duration)
                .playOn(findViewById(view));

    }
    // Method getCurrentLocation: Obtains location data using JSON returned string
    // Returns CurrentLocation object

    private CurrentLocation getCurrentLocation(String jsondata) throws JSONException {

        JSONObject location = new JSONObject(jsondata);
        String address = location.getJSONArray("results").getJSONObject(0).getJSONArray("address_components").getJSONObject(0).getString("short_name");
        String street = location.getJSONArray("results").getJSONObject(0).getJSONArray("address_components").getJSONObject(1).getString("short_name");
        String city = location.getJSONArray("results").getJSONObject(0).getJSONArray("address_components").getJSONObject(2).getString("short_name");
        cityTag=city;
        String country = location.getJSONArray("results").getJSONObject(0).getJSONArray("address_components").getJSONObject(4).getString("long_name");

        CurrentLocation currentLocation = new CurrentLocation();

        currentLocation.setCity(city);
        currentLocation.setAddress(address);
        currentLocation.setCountry(country);
        currentLocation.setStreet(street);

        return currentLocation;
    }

    // Method getCurrentWeather: Obtains forecast data using JSON returned string
    // Returns CurrenWeather object

    private CurrentWeather getCurrentWeather(String jsondata) throws JSONException {

        JSONObject forecast = new JSONObject(jsondata);
        String timezone = forecast.getString("timezone");
        JSONObject currently = forecast.getJSONObject("currently");
        String icon = currently.getString("icon");
        long time = currently.getLong("time");
        double humidity = currently.getDouble("humidity");
        double precipChance = currently.getDouble("precipProbability");
        String summary = currently.getString("summary");
        double temperature = Math.round(currently.getDouble("temperature"));

        CurrentWeather currentWeather = new CurrentWeather();

        currentWeather.setIcon(icon);
        currentWeather.setHumidity(humidity);
        currentWeather.setTime(time);
        currentWeather.setPrecipChance(precipChance);
        currentWeather.setSummary(summary);
        currentWeather.setTemperature(temperature);
        currentWeather.setTimeZone(timezone);


        weatherTag = summary;

        return currentWeather;
    }

    private FlickrImages getFlickrImages(String jsondata) throws JSONException{

        Random randomgenerator = new Random();

        JSONObject bg = new JSONObject(jsondata);
        int totalPictures = Integer.parseInt(bg.getJSONObject("photos").getString("total"));
        int valor = randomgenerator.nextInt(totalPictures);

        String photo = bg.getJSONObject("photos").getJSONArray("photo").getJSONObject(valor).getString(pictureSizeUrl);


        FlickrImages flickrImages = new FlickrImages();

        flickrImages.setUrlImage(photo);

        return  flickrImages;
    }

    // Method for check network availability
    // Returns boolean true(available) or false (unavailable)

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        // If network exists and is connected return true
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }

        return isAvailable;
    }

    // Method for showing messages on display
    // Parameters: error (String) Text to be shown in message body
    // Changed to use Toast instead
    private void alertUserAboutError(String error) {
        /* AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.setText(error);
        dialog.show(getFragmentManager(), "error_dialog");
        */
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            getLongLat(location);
            getDataAllData();

        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        getLongLat(location);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
         /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                     /*
                      * Thrown if Google Play services canceled the original
                      * PendingIntent
                      */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();

            }
        } else {
         /*
          * If no resolution is available, display a dialog to the
          * user with the error.
          */
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }




}