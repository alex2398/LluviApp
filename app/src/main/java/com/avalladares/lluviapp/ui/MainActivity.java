package com.avalladares.lluviapp.ui;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.avalladares.lluviapp.R;
import com.avalladares.lluviapp.location.CurrentLocation;
import com.avalladares.lluviapp.weather.Current;
import com.avalladares.lluviapp.weather.Day;
import com.avalladares.lluviapp.weather.Forecast;
import com.avalladares.lluviapp.weather.Hour;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MainActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public final static String DAILY_FORECAST = "DAILY_FORECAST";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1500;
    private final static String REQUEST_WEATHER = "weather";
    private final static String REQUEST_CITY = "city";
    private final static String REQUEST_BACKGROUND="background";
    public static final String TAG = MainActivity.class.getSimpleName();
    public static int background;

    // Boolean for selecting background images from flickr weather project pool
    public boolean getPicturesFlickr  = false;
    // Booleans for changing background just on startup
    public boolean changeBgJustOnce = true;
    public int changeBg_count=0;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private String cityTag = null;

    private double currentLongitude;
    private double currentLatitude;


    private CurrentLocation mCurrentLocation;
    private Forecast mForecast;
    private FlickrImages mFlickrImages;

    private String lang = Locale.getDefault().getLanguage();

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
    @InjectView(R.id.backgroundLayout)
    RelativeLayout mBackgroundLayout;
    @InjectView(R.id.cityLabel)
    TextView mCityLabel;
    @InjectView(R.id.linearLayout)
    LinearLayout mLinearLayout;
    @InjectView(R.id.degreeImageView)
    ImageView mDegreeImageView;




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
        mRefreshImageView.setVisibility(View.INVISIBLE);

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
        if (getPicturesFlickr == true) {
            getBackgroundFlickr();
        }
    }

    // Method for getting location using google maps
    private void getLocation(double lat, double lon) {
        //String apiKey = "27974c4bc33201748eaf542a6769c3b7";
        String locationUrl = "http://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lon + "&sensor=false&language=" + lang;
        getJSONData(locationUrl, "location");
    }

    // Method for getting forecast data from forecast.io
    private void getForecast() {
        String apiKey = "27974c4bc33201748eaf542a6769c3b7";
        String forecastUrl = "https://api.forecast.io/forecast/" + apiKey +
                "/" + currentLatitude + "," + currentLongitude + "?units=auto&lang=" + lang;
        getJSONData(forecastUrl, "weather");
    }

    private void getBackgroundFlickr() {

        String apiKey = "b800034851ef22708d4bf96f2df557f2";
        String flickrUrl="https://api.flickr.com/services/rest/?&method=flickr.groups.pools.getPhotos&api_key=" + apiKey +
                "&group_id=1463451@N25&tags=" + cityTag + "&extras=" + pictureSizeUrl +"&format=json&nojsoncallback=1";

        getJSONData(flickrUrl,"background");
    }



    private void getJSONData(String Url, final String method) {

        /*
        Method for getting JSON data
        Parameters:
        Url (String) : url for retrieving JSON data
        dataType (String) :
        "weather" for forecast
        "city" for location
        */

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
                                mForecast = parseForeCastDetails(jsondata);
                            }
                            if (method.equals("location")) {
                                mCurrentLocation = getCurrentLocation(jsondata);
                            }
                            if (method.equals("background")) {
                                if (changeBgJustOnce == true) {
                                    if (changeBg_count < 1) {
                                        mFlickrImages = getFlickrImages(jsondata);
                                    }
                                }
                            }
                            // For updating UI we get back to the main thread
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
                                        if (changeBgJustOnce == true) {
                                            if (changeBg_count < 1) {
                                                updateBackgroundUIFlickr();
                                                //setViewsVisible();
                                                changeBg_count++;
                                            }
                                        }
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

    private CurrentLocation getCurrentLocation(String jsondata) throws JSONException {
        // Method getCurrentLocation: Obtains location data using JSON returned string
        // Returns CurrentLocation object
        JSONObject location = new JSONObject(jsondata);
        String address = location.getJSONArray("results").getJSONObject(0).getJSONArray("address_components").getJSONObject(0).getString("short_name");
        String street = location.getJSONArray("results").getJSONObject(0).getJSONArray("address_components").getJSONObject(1).getString("short_name");
        String city = location.getJSONArray("results").getJSONObject(0).getJSONArray("address_components").getJSONObject(2).getString("short_name");
        String country = location.getJSONArray("results").getJSONObject(0).getJSONArray("address_components").getJSONObject(4).getString("long_name");

        CurrentLocation currentLocation = new CurrentLocation();
        cityTag=city;
        currentLocation.setCity(city);
        currentLocation.setAddress(address);
        currentLocation.setCountry(country);
        currentLocation.setStreet(street);

        return currentLocation;
    }

    private Forecast parseForeCastDetails(String jsondata) throws JSONException {

        Forecast forecast = new Forecast();

        forecast.setCurrent(getCurrentDetails(jsondata));
        forecast.setDailyForecast(getDailyForecast(jsondata));
        forecast.setHourlyForecast(getHourlyForecast(jsondata));




        return forecast;

    }

    private Current getCurrentDetails(String jsondata) throws JSONException {

        /*
        Method getCurrentDetails: Obtains forecast data using JSON returned string
        Returns CurrenWeather object
        */

        JSONObject forecast = new JSONObject(jsondata);

        String timezone = forecast.getString("timezone");

        JSONObject currently = forecast.getJSONObject("currently");

        Current current = new Current();

        current.setIcon(currently.getString("icon"));
        current.setHumidity(currently.getDouble("humidity"));
        current.setTime(currently.getLong("time"));
        current.setPrecipChance(currently.getDouble("precipProbability"));
        current.setSummary(currently.getString("summary"));
        current.setTemperature(Math.round(currently.getDouble("temperature")));
        current.setTimeZone(timezone);

        return current;
    }

    private Day[] getDailyForecast(String jsondata) throws JSONException{
        // We create forecast JSON object and populate it with String jsondata

        JSONObject forecast = new JSONObject(jsondata);
        // We get timezone from the forecast root
        String timezone = forecast.getString("timezone");

        // We get hourly data from forecast root
        JSONObject daily = forecast.getJSONObject("daily");
        // Hourly data comes in an Array, so we put it into an JSONArray called hourly
        JSONArray data = daily.getJSONArray("data");

        // We create a java Hour Array with the same lenght as the JSONArray data
        Day[] days = new Day[data.length()];

        // We loop through JSONArray data and go populating each JSON object
        for (int i=0; i<data.length(); i++) {
            JSONObject jsonDay = data.getJSONObject(i);

            /*
            We create the hour object inside the loop for using a new
            object each time. Otherwise, it the object is created outside the loop
            we end up populating the same object and filling the array with the same data
            */

            Day day = new Day();

            day.setSummary(jsonDay.getString("summary"));
            day.setTemperatureMax(jsonDay.getDouble("temperatureMax"));
            day.setIcon(jsonDay.getString("icon"));
            day.setTime(jsonDay.getLong("time"));
            day.setTimezone(timezone);

            // Once we have the object populated we save it into its position in the Array hours
            days[i] = day;
        }

        // We return hours array
        return days;

    }

    private Hour[] getHourlyForecast(String jsondata) throws JSONException {

        // We create forecast JSON object and populate it with String jsondata
        JSONObject forecast = new JSONObject(jsondata);
        // We get timezone from the forecast root
        String timezone = forecast.getString("timezone");

        // We get hourly data from forecast root
        JSONObject hourly = forecast.getJSONObject("hourly");
        // Hourly data comes in an Array, so we put it into an JSONArray called hourly
        JSONArray data = hourly.getJSONArray("data");

        // We create a java Hour Array with the same lenght as the JSONArray data
        Hour[] hours = new Hour[data.length()];

        // We loop through JSONArray data and go populating each JSON object
        for (int i=0; i<data.length(); i++) {
            JSONObject jsonHour = data.getJSONObject(i);

            /*
            We create the hour object inside the loop for using a new
            object each time. Otherwise, it the object is created outside the loop
            we end up populating the same object and filling the array with the same data
            */

            Hour hour = new Hour();

            hour.setSummary(jsonHour.getString("summary"));
            hour.setTemperature(jsonHour.getDouble("temperature"));
            hour.setIcon(jsonHour.getString("icon"));
            hour.setTime(jsonHour.getLong("time"));
            hour.setTimezone(timezone);

            // Once we have the object populated we save it into its position in the Array hours
            hours[i] = hour;
        }

        // We return hours array
        return hours;
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



    private void getLongLat(Location location) {
        // Method for setting long and lat using location object
        currentLongitude = location.getLongitude();
        currentLatitude = location.getLatitude();
    }

    private void getlonglat_deprecated() {
        // Method for getting longitude and latitude
        // Parameters:
        // Longitude (double)
        // Latitude (double)
        // Deprecated: obtained with google services
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
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

        Current current = mForecast.getCurrent();

        mTemperatureValue.setText(current.getTemperature() + "");
        applyAnimation(Techniques.ZoomIn, 500, R.id.temperatureLabel);
        mHumidityValue.setText(current.getHumidity() + "%");
        mPrecipValue.setText(current.getPrecipChance() + "%");
        mSummaryLabel.setText(current.getSummary() + "");
        Drawable drawable = getResources().getDrawable(current.getIconId());
        mIconImageView.setImageDrawable(drawable);
        applyAnimation(Techniques.FadeIn, 1500, R.id.iconImageView);

        // Optional: Change background only when opening app

        if (!getPicturesFlickr) {
            if (changeBgJustOnce) {
                if (changeBg_count < 1) {
                    changeBg_count++;
                    background = current.getBgId();
                    Drawable draw = getResources().getDrawable(background);
                    mBackgroundLayout.setBackground(draw);
                    applyAnimation(Techniques.FadeIn, 500, R.id.backgroundLayout);

                }
            }
        }


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

        aq.ajax(url.trim(), Bitmap.class, 0, new AjaxCallback<Bitmap>() {
            @Override
            public void callback(String url, Bitmap object, AjaxStatus status) {
                super.callback(url, object, status);
                mBackgroundLayout.setBackground(new BitmapDrawable(object));
            }
        });

        applyAnimation(Techniques.FadeIn, 500, R.id.backgroundLayout);

    }


    private void applyAnimation(Techniques technique, int duration, int view) {

        /*
        Method for using animations in a resource
        Parameters:
        technique (Technique)
        duration (int) animation duration (in ms)
        view (int) resource id
        */
        YoYo.with(technique)
                .duration(duration)
                .playOn(findViewById(view));

    }

    private void alertUserAboutError(String error) {
        // Method for showing messages on display
        // Parameters: error (String) Text to be shown in message body
        // Changed to use Toast instead

        /*AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.setText(error);
        dialog.show(getFragmentManager(), "error_dialog");
*/
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    // Methods for connection

    private boolean isNetworkAvailable() {
        // Method for check network availability
        // Returns boolean true(available) or false (unavailable)
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

    @Override
    public void onConnected(Bundle bundle) {
        /*
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            getLongLat(location);
            getDataAllData();
        }
    }


    @Override
    public void onConnectionSuspended(int i) {}

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
                connectionResult.startResolutionForResult(this, PLAY_SERVICES_RESOLUTION_REQUEST);
                     /*
                      * Thrown if Google Play services canceled the original
                      * PendingIntent
                      */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                Log.d(TAG,"Error Google Services");

            }
        } else {
         /*
          * If no resolution is available, display a dialog to the
          * user with the error.
          */
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @OnClick(R.id.dailyButton)
    public void startDailyActivity(View view) {
        Intent intent = new Intent(this, DailyForecastActivity.class);
        intent.putExtra("background",background);
        intent.putExtra(DAILY_FORECAST,mForecast.getDailyForecast());

        startActivity(intent);

    }

}