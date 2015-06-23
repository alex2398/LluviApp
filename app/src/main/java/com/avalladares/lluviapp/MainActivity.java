package com.avalladares.lluviapp;



import android.content.Context;
        import android.content.IntentSender;
        import android.graphics.Color;
        import android.graphics.drawable.Drawable;
        import android.location.Location;
        import android.net.ConnectivityManager;
        import android.net.NetworkInfo;
        import android.os.Bundle;
        import android.support.v7.app.AppCompatActivity;
        import android.util.Log;
        import android.view.View;
        import android.widget.ImageView;
        import android.widget.ProgressBar;
        import android.widget.RelativeLayout;
        import android.widget.TextView;
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

        import butterknife.ButterKnife;
        import butterknife.InjectView;


public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener        {

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    public static final String TAG = MainActivity.class.getSimpleName();

    private double currentLongitude;
    private double currentLatitude;

    private CurrentWeather mCurrentWeather;
    private CurrentLocation mCurrentLocation;

    private Location location;

    // Nuevo metodo con ButterKnife

    @InjectView(R.id.temperatureLabel) TextView mTemperatureValue;
    @InjectView(R.id.humidityValue) TextView mHumidityValue;
    @InjectView(R.id.precipValue) TextView mPrecipValue;
    @InjectView(R.id.summaryLabel) TextView mSummaryLabel;
    @InjectView(R.id.iconImageView) ImageView mIconImageView;
    @InjectView(R.id.refreshImageView) ImageView mRefreshImageView;
    @InjectView(R.id.progressBar) ProgressBar mProgressBar;
    @InjectView(R.id.locationLabel) TextView mLocationLabel;
    @InjectView(R.id.lastUpdateLabel) TextView mLastUpdateLabel;
    @InjectView(R.id.backgroundLayout) RelativeLayout mBackgroundLayout;

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
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(2 * 1000); // 2 second, in milliseconds

        // By default, set progressbar invisible
        mProgressBar.setVisibility(View.INVISIBLE);

        // Method for updating data on image click
        mRefreshImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getDataAllData();
            }
        });

    }

    // Method for getting forecast and location data
    private void getDataAllData() {
        getForecast();
        getLocationCity(currentLongitude, currentLatitude);
    }

    // Method for getting longitude and latitude
    // Parameters:
    // Longitude (double)
    // Latitude (double)
    private void getLocationCity(double lon, double lat) {
        String requestOpenStreetMapUrl = "http://nominatim.openstreetmap.org/reverse?format=json&lat=" + lat + "&lon=" + lon;
        getJSONData(requestOpenStreetMapUrl, "city");


    }

    // Method for getting forecast data from forecast.io
    private void getForecast() {
        String apiKey = "27974c4bc33201748eaf542a6769c3b7";
        String forecastUrl = "https://api.forecast.io/forecast/" + apiKey +
                "/" + currentLatitude + "," + currentLongitude + "?units=auto&lang=es";
        getJSONData(forecastUrl, "weather");
    }

    // Method for getting JSON data
    // Parameters:
    // Url (String) : url for retrieving JSON data
    // dataType (String) :
    //          "weather" for forecast
    //          "city" for location

    private void getJSONData(String Url,String dataType) {

        final String mType=dataType;

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

                            if (mType.equals("weather")) {
                                mCurrentWeather = getCurrentWeather(jsondata);
                            }else if (mType.equals("city")) {
                                mCurrentLocation = getCurrentCity(jsondata);
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (mType.equals("weather")) {
                                        updateDisplay();
                                    }else if (mType.equals("city")) {
                                        updateCity();
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

            alertUserAboutError(getString(R.string.noNetworkMessage));
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


    // Method for setting long and lat using location objetc
    private void getLongLat(Location location) {

        currentLongitude = location.getLongitude();
        currentLatitude = location.getLatitude();
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

    // Method for updating display data
    // Animations added
    private void updateDisplay() {

        mTemperatureValue.setText(mCurrentWeather.getTemperature() + "");
        applyAnimation(Techniques.ZoomIn, 500, R.id.temperatureLabel);

        mLastUpdateLabel.setText(getString(R.string.last_update) + mCurrentWeather.getFormattedTime());
        mHumidityValue.setText(mCurrentWeather.getHumidity() + "%");
        mPrecipValue.setText(mCurrentWeather.getPrecipChance() + "%");
        mSummaryLabel.setText(mCurrentWeather.getSummary() + "");
        Drawable drawable = getResources().getDrawable(mCurrentWeather.getIconId());

        mIconImageView.setImageDrawable(drawable);
        applyAnimation(Techniques.FadeIn, 1500, R.id.iconImageView);

        mBackgroundLayout.setBackgroundResource(mCurrentWeather.getBgId());
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

    // Method for update city data in display
    private void updateCity() {
        mLocationLabel.setText(mCurrentLocation.getCity() + "");
        applyAnimation(Techniques.FadeIn,400,R.id.locationLabel);

    }

    // Method getCurrentCity: Obtains city name using JSON returned string
    // Returns CurrentLocation object
    private CurrentLocation getCurrentCity(String jsondata) throws JSONException {
        JSONObject data = new JSONObject(jsondata);

        JSONObject address = data.getJSONObject("address");
        String location_city=address.getString("city");

        CurrentLocation currentCity = new CurrentLocation();

        currentCity.setCity(location_city);

        return currentCity;

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

        return currentWeather;
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
    private void alertUserAboutError(String error) {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.setText(error);
        dialog.show(getFragmentManager(), "error_dialog");
    }

    // Method for getting last location data on connected google services
    // If no lastlocation is found, new request is done
    // Otherwise we update lon, lat and forecast data

    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        else {
            getLongLat(location);
            getDataAllData();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
    // Updates long and lat on location change
    @Override
    public void onLocationChanged(Location location) {
        getLongLat(location);
    }

    // Show alert if google services are not connected
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