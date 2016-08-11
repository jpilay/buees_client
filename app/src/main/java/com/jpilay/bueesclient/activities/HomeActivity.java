package com.jpilay.bueesclient.activities;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.jpilay.bueesclient.R;
import com.jpilay.bueesclient.network.NetworkFunction;

public class HomeActivity extends AppCompatActivity {

	private static String URL_HOME = "http://www.google.com";
	public View rootView;
	private ProgressDialog dialog;
	private WebView webView;
    private Toolbar toolbar;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_home);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		webView = (WebView) findViewById(R.id.web_home);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.setWebViewClient(new GeoWebViewClient());

		// Below required for geolocation
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setGeolocationEnabled(true);
		webView.setWebChromeClient(new GeoWebChromeClient());
	}

	@Override
	protected void onPostCreate(@Nullable Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		if (!NetworkFunction.checkInternetConnection(getApplicationContext())) {
			Snackbar.make(getCurrentFocus(), "No tiene Conexión a Internet", Snackbar.LENGTH_LONG)
					.setAction("Reintentar", new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							loadMap();
						}
					}).show();
		} else {
			loadMap();
		}
	}

	public void loadMap() {

		dialog = ProgressDialog.show(HomeActivity.this, "","Cargando...", true, false);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				dialog.dismiss();
				
			}
		});

		webView.loadUrl(URL_HOME);
	}

	/**
	 * WebViewClient subclass loads all hyperlinks in the existing WebView
	 */
	public class GeoWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// When user clicks a hyperlink, load in the existing WebView
			view.loadUrl(url);
			return true;
		}
	}

	/**
	 * WebChromeClient subclass handles UI-related calls Note: think chrome as
	 * in decoration, not the Chrome browser
	 */
	public class GeoWebChromeClient extends WebChromeClient {
		@Override
		public void onGeolocationPermissionsShowPrompt(String origin,
				GeolocationPermissions.Callback callback) {
			// Always grant permission since the app itself requires location
			// permission and the user has therefore already granted it
			callback.invoke(origin, true, false);
		}
	}
}
