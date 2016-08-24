package com.jpilay.bueesclient.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
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
import com.jpilay.bueesclient.util.Controller;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,OnMapReadyCallback { //, GoogleMap.OnMyLocationButtonClickListener {

    private static final LatLng GUAYAQUIL = new LatLng(-2.173200, -79.921335);
    private BusScheduleTask mAuthTask = null;
    public ImageLoader imageLoader = null;

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

}
