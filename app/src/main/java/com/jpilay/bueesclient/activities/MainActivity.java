package com.jpilay.bueesclient.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.jpilay.bueesclient.R;
import com.jpilay.bueesclient.application.AppController;
import com.jpilay.bueesclient.models.ScaleImageView;
import com.jpilay.bueesclient.models.User;
import com.jpilay.bueesclient.network.Network;
import com.jpilay.bueesclient.util.Controller;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,OnMapReadyCallback { //, GoogleMap.OnMyLocationButtonClickListener {

    private static final LatLng GUAYAQUIL = new LatLng(-2.173200, -79.921335);
    private BusScheduleTask mAuthTask = null;
    public ImageLoader imageLoader = null;

    // GCM Notification Push
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public GoogleCloudMessaging gcm;
    public String regid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Map
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Option map
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        User user = getUser();
        View hView =  navigationView.getHeaderView(0);
        TextView nav_group = (TextView) hView.findViewById(R.id.nav_group);
        TextView nav_email = (TextView) hView.findViewById(R.id.nav_email);
        nav_group.setText(user.getmGroup());
        nav_email.setText(user.getmEmail());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, ChangePwdActivity.class));
            return true;
        }

        return false; //super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            showHome();

        } else if (id == R.id.nav_lost_objects) {
            showPublication();

        } else if (id == R.id.nav_signout) {
            showLogOut();

        } else if (id == R.id.nav_schedule) {
            mAuthTask = new BusScheduleTask();
            mAuthTask.execute((Void) null);

        } else if (id == R.id.nav_about) {
            showAbout();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we
     * just add a marker near Africa.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        Location myLocation;
        map.setMyLocationEnabled(true);

        // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(GUAYAQUIL)      // Sets the center of the map to my location
                .zoom(10)                   // Sets the zoom
                //.bearing(90)                // Sets the orientation of the camera to east
                //.tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        //map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    /*@Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(getApplicationContext(),"holi",Toast.LENGTH_SHORT).show();
        if(!Network.isGpsEnabled(getApplicationContext())){
            Snackbar.make(getCurrentFocus(), R.string.network_internet_disconnect, Snackbar.LENGTH_LONG)
                    .setAction(R.string.network_refresh, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(getApplicationContext(),"holi",Toast.LENGTH_SHORT).show();
                        }
                    }).show();
        }
        return false;
    }*/

    private void showAbout(){
        AlertDialog alert = new AlertDialog.Builder(MainActivity.this)
                .create();

        // Setting Dialog Title
        alert.setTitle("Acerca De");
        // Setting Dialog Message
        alert.setMessage("Esta aplicación es un demo de Joffre Pilay.\n\nTodos los derechos reservados");
        // Setting Icon to Dialog
        alert.setIcon(R.drawable.ic_uees);
        // Showing Alert Message
        alert.show();
    }

    private void showHome(){
        Intent i = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(i);
    }

    private void showLogOut(){
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle("Buees");
        alert.setMessage("Desea cerrar sesión?");
        alert.setIcon(R.drawable.ic_uees);

        alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int whichButton) {

                SharedPreferences sp = getSharedPreferences(getResources().getString(R.string.preferences)
                        , Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = sp.edit();
                editor.clear();
                editor.commit();

                Intent i = new Intent(MainActivity.this,LoginActivity.class);
                dialog.dismiss();
                startActivity(i);
                finish();

            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();

            }
        });

        AlertDialog ad = alert.create();
        ad.show();

    }

    private void showPublication(){
        Intent i = new Intent(MainActivity.this, DriverPublicationActivity.class);
        startActivity(i);
    }

    private User getUser(){
        SharedPreferences sp = getSharedPreferences(getResources().getString(R.string.preferences), Context.MODE_PRIVATE);
        String username = sp.getString(getResources().getString(R.string.username),"");
        String email = sp.getString(getResources().getString(R.string.email),"");
        String group = sp.getString(getResources().getString(R.string.group),"");

        User user = new User(username, email, group);
        return user;
    }

    private class BusScheduleTask extends AsyncTask<Void, Void, String> {
        private ProgressDialog mDialog;

        @Override
        protected String doInBackground(Void... params) {
            String image_url = null;
            try {

                Controller controller = new Controller(getApplicationContext());
                JSONObject jsonObj = controller.schedule();

                if (jsonObj != null)
                    image_url = jsonObj.getString("image");

            } catch (JSONException e) {
                Log.e("Buees", e.getMessage());
            }
            return image_url;

        }

        protected void onPreExecute() {
            mDialog = ProgressDialog.show(MainActivity.this,
                    "",
                    "Cargando...",
                    true, false);
        }

        @Override
        protected void onPostExecute(String url) {
            mAuthTask = null;
            mDialog.dismiss();

            if (url != null) {

                // Only for debbuger
                url = getResources().getString(R.string.url) + "media/HorarioBuses/Http403_dvigFHQ.png";
                if (imageLoader == null)
                    imageLoader = AppController.getInstance().getImageLoader();

                ScaleImageView zoom;
                zoom = new ScaleImageView(getApplicationContext());
                zoom.setImageUrl(url,imageLoader);

                AlertDialog alert = new AlertDialog.Builder(MainActivity.this)
                        .create();
                alert.setTitle("Horarios de Salida");
                alert.setView(zoom);
                alert.setIcon(R.drawable.ic_uees);
                alert.show();

            } else
                Toast.makeText(getApplicationContext(),
                        "No se encontro horario", Toast.LENGTH_SHORT).show();

        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            mDialog.dismiss();
        }

    }

    // GCM notification push
    /**
     * Check the device to make sure it has the Google Play Services APK. If it
     * doesn't, display a dialog that allows users to download the APK from the
     * Google Play Store or enable it in the device's system settings.
     */

    private void checkRegistrationGCM(){
        if(Network.checkInternetConnection(getApplicationContext())) {
            // Check device for Play Services APK. If check succeeds, proceed with
            // GCM registration.

            if (checkPlayServices()) {

                gcm = GoogleCloudMessaging.getInstance(this);
                regid = getRegistrationId(getApplicationContext());

                if (regid.isEmpty()) {
                    new registerInBackground(MainActivity.this).execute((Void) null);
                } else {
                    sendRegistrationIdToBackend();
                }
            } else {
                Log.i("Buees", "No valid Google Play Services APK found.");
                Toast.makeText(getApplicationContext(), "Por favor actualize Google Play Services para usar todos nuestros servicios.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i("GCM Buees", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private boolean checkPlayServices2() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();

                Toast.makeText(getApplicationContext(),
                        "Error revise Google Play Services.", Toast.LENGTH_LONG).show();
            } else {
                Log.i("GCM Buees", "This device is not supported.");

                Toast.makeText(getApplicationContext(),
                        "Este dispositivo no tiene soporte.", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

    /* Alternative of check google play services */

    private boolean servicesConnected() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            // Continue
            return true;
            // Google Play services was not available for some reason
        } else {
            Toast.makeText(getApplicationContext(),
                    "Le hace falta instalar Servicios de Google Play.",
                    Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=com.google.android.gms"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }

        return false;
    }

    /**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context
     *            application's context.
     * @param regId
     *            registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGcmPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i("GCM Buees", "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    /**
     * Gets the current registration ID for application on GCM service, if there
     * is one.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i("GCM Buees", "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
                Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i("GCM Buees", "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    public class registerInBackground extends AsyncTask<Void, Void, String> {

        private Context context;
        private Activity activity;

        public registerInBackground(Activity activity) {
            this.activity = activity;
            context = activity;
        }

        @Override
        protected String doInBackground(Void... params) {

            String msg = "";
            try {

                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }

                regid = gcm.register(getResources().getString(R.string.sender_id));
                msg = "Device registered, registration ID=" + regid;

                // You should send the registration ID to your server over.
                // Persist the regID - no need to register again.
                storeRegistrationId(context, regid);
            } catch (IOException ex) {
                regid = "";
                msg = "Error :" + ex.getMessage();
                // If there is an error, don't just keep trying to register.
                // Require the user to click a button again, or perform
                // exponential back-off.
            }
            return msg;
        }

        @Override
        protected void onPostExecute(String msg) {
            Log.i("GCM Buees", msg);
            sendRegistrationIdToBackend();
        }

    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences,
        // but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use
     * GCM/HTTP or CCS to send messages to your app.
     */
    public void sendRegistrationIdToBackend() {
        new SendRegistrationAPI(MainActivity.this).execute();
        //Toast.makeText(getApplicationContext(), regid, Toast.LENGTH_LONG).show();

    }

    /**
     * Send name, registration ID and device ID to your server over HTTP.
     */

    public class SendRegistrationAPI extends AsyncTask<String, Void, Boolean> {

        private Context context;
        private Activity activity;

        public SendRegistrationAPI(Activity activity) {
            this.activity = activity;
            context = activity;
        }

        @Override
        protected void onPostExecute(Boolean success) {

            if (success)
                Log.i("GCM Buees", "Save success regId on app server ");
            else
                Log.i("GCM Buees", "Save not success regId on app server ");
        }

        @Override
        protected Boolean doInBackground(String... args) {

            Boolean success = false;
            String device_id = getMDN_or_MEID(context);
            String registration_id = regid;
            String username = getUser(context);

            try {

                Controller controller = new Controller(getApplicationContext());
                JSONObject jsonObj = controller.registerDevice(username, registration_id, device_id);

                if (jsonObj != null) {
                    if(jsonObj.has("id"))
                        success = true;
                }

            } catch (Exception e) {
                Log.e("GCM Buees", e.getMessage());
            }

            return success;

        }
    }

    /** Obtain IdDevice can be GCM o CDMA **/
    private String getMDN_or_MEID(Context context) {

        // getSystemService is a method from the Activity class. getDeviceID()
        // will return the MDN or MEID of the device depending on which radio
        // the phone uses (GSM or CDMA).
        TelephonyManager tManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String uid = tManager.getDeviceId();
        return uid;

    }

    private String getUser(Context context) {
        SharedPreferences sp = context.getSharedPreferences(getResources().getString(R.string.preferences), Context.MODE_PRIVATE);
        String user = sp.getString(getResources().getString(R.string.username), "");
        return user;
    }

}
