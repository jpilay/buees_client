package com.jpilay.bueesclient.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.support.v7.widget.SearchView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.dgreenhalgh.android.simpleitemdecoration.linear.DividerItemDecoration;
import com.jpilay.bueesclient.R;
import com.jpilay.bueesclient.adapter.DriverPublicationAdapter;
import com.jpilay.bueesclient.application.AppController;
import com.jpilay.bueesclient.models.BusRoute;
import com.jpilay.bueesclient.models.DriverPublication;
import com.jpilay.bueesclient.models.User;
import com.jpilay.bueesclient.network.Network;
import com.jpilay.bueesclient.util.Controller;
import com.jpilay.bueesclient.util.Ui;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class DriverPublicationActivity extends AppCompatActivity {

    private SearchView mSearchView;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private DriverPublicationAdapter mAdapter;
    private FloatingActionButton mAddDriverPublication;
    private List<DriverPublication> driverPublicationList = new ArrayList<>();
    private List<DriverPublication> driverPublicationListSearch = new ArrayList<>();
    private ImageLoader imageLoader = null;
    private DriverPublicationTask mAuthTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set view activity
        setContentView(R.layout.activity_driver_publication);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new DriverPublicationAdapter(driverPublicationList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        Drawable dividerDrawable = ContextCompat.getDrawable(this, R.drawable.divider_item);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(dividerDrawable));
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                DriverPublication driverPublication = driverPublicationList.get(position);
                showInformation(driverPublication);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy > 0 ||dy<0 && mAddDriverPublication.isShown())
                    mAddDriverPublication.hide();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
                    mAddDriverPublication.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        mAddDriverPublication = (FloatingActionButton) findViewById(R.id.add_publication_button);
        mAddDriverPublication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), String.valueOf(driverPublicationList.size()), Toast.LENGTH_SHORT).show();
            }
        });

        fetchData();

    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search,menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchView.setQueryHint(getString(R.string.action_search));
        mSearchView.setIconified(true);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                Ui.hideSoftKeyboard(getApplicationContext(), mSearchView);
                searchPublication(s);
                mSearchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return mSearchView.hasFocus();
            }
        });
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_driver_publication, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.delivered:
                Toast.makeText(getApplicationContext(), "Menu Menu", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (mSearchView.isShown()){
            mSearchView.onActionViewCollapsed();  //collapse your ActionView
            mSearchView.setQuery("",false);       //clears your query without submit
        } else{
            super.onBackPressed();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_HOME && mSearchView.isShown()) {
            mSearchView.onActionViewCollapsed();  //collapse your ActionView
            mSearchView.setQuery("",false);       //clears your query without submit

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    private void showInformation(DriverPublication driverPublication){
        NetworkImageView image;
        TextView description, route, date, time, note;

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.content_info, null);

        image = (NetworkImageView) view.findViewById(R.id.info_image);
        description = (TextView) view.findViewById(R.id.info_description);
        route = (TextView) view.findViewById(R.id.info_route);
        time = (TextView) view.findViewById(R.id.info_hour);
        date = (TextView) view.findViewById(R.id.info_date);
        note = (TextView) view.findViewById(R.id.info_note);

        image.setImageUrl(driverPublication.getImage(),imageLoader);
        description.setText(driverPublication.getDescription());
        route.setText(driverPublication.getBusRoute().getName());
        time.setText(driverPublication.getHour());
        date.setText(driverPublication.getDate());

        AlertDialog alert = new AlertDialog.Builder(DriverPublicationActivity.this)
                .create();
        //alert.setTitle("Horarios de Salida");
        alert.setView(view);
        alert.show();
    }

    private void fetchData(){
        if (!Network.checkInternetConnection(getApplicationContext())) {
            Toast.makeText(getApplicationContext()
                    , R.string.network_internet_disconnect
                    , Toast.LENGTH_LONG).show();

        } else {
            mAuthTask = new DriverPublicationTask();
            mAuthTask.execute((Void) null);
        }

    }

    private void searchPublication(String query){

        if (driverPublicationList.size() != 0) {
            driverPublicationListSearch.clear();

            if (!query.equals("")) {

                for (DriverPublication dp : driverPublicationList) {

                    if (dp.getDescription().contains("query")) {
                        driverPublicationListSearch.add(dp);
                    }
                }

                if (driverPublicationListSearch.size() == 0)
                    Toast.makeText(getApplicationContext(), "No se encontro ninguna coincidencia", Toast.LENGTH_LONG).show();

                mAdapter.setDriverPublicationList(driverPublicationListSearch);
                mAdapter.notifyDataSetChanged();

            } else {
                mAdapter.setDriverPublicationList(driverPublicationList);
                mAdapter.notifyDataSetChanged();

            }
        }
    }

    public class DriverPublicationTask extends AsyncTask<Void, Void, Object> {
        private ProgressDialog mDialog;

        @Override
        protected Object doInBackground(Void... params) {

            JSONArray jsonArray = null;

            try {
                Controller controller = new Controller(getApplicationContext());
                jsonArray = controller.publications();

                if(jsonArray != null) {

                    if (jsonArray.length() != 0) {
                        JSONObject jsonObjectDP;

                        for (int i = 0; i < jsonArray.length(); i++) {

                            try {
                                jsonObjectDP = jsonArray.getJSONObject(i);
                                jsonObjectDP.put("bus_route",controller.routes(jsonObjectDP.getString("bus_route")));

                            } catch (JSONException e) {
                                Log.e("Buees", e.getMessage());
                            } catch (Exception e) {
                                Log.e("Buees", e.getMessage());
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                }

            } catch (Exception e){
                Log.e("Buees", e.getMessage());
            }

            return jsonArray;
        }

        protected void onPreExecute() {
            mDialog = ProgressDialog.show(DriverPublicationActivity.this,
                    "",
                    "Cargando...",
                    true, false);

        }

        @Override
        protected void onPostExecute(final Object object) {
            mAuthTask = null;
            mDialog.dismiss();

            if(object != null){
                JSONArray jsonArray = (JSONArray) object;

                if (jsonArray.length() != 0) {
                    JSONObject jsonObjectDP, jsonObjectBR;

                    for (int i = 0; i < jsonArray.length(); i++) {

                        try {
                            jsonObjectDP = jsonArray.getJSONObject(i);
                            jsonObjectBR = jsonObjectDP.getJSONObject("bus_route");

                            BusRoute br = new BusRoute(
                                    jsonObjectBR.getString("id"),
                                    jsonObjectBR.getString("name"));

                            DriverPublication dp = new DriverPublication(
                                    jsonObjectDP.getString("id"),
                                    jsonObjectDP.getString("date"),
                                    jsonObjectDP.getString("description"),
                                    jsonObjectDP.getString("hour"),
                                    jsonObjectDP.getString("image"),
                                    jsonObjectDP.getBoolean("status"),
                                    br
                            );

                            driverPublicationList.add(dp);

                        } catch (JSONException e) {
                            Log.e("Buees", e.getMessage());
                        } catch (Exception e) {
                            Log.e("Buees", e.getMessage());
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                }

            } else {
                Toast.makeText(DriverPublicationActivity.this,"No se encontro ninguna publicación registrada",Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            mDialog.dismiss();
        }
    }
}
