package com.jpilay.bueesclient.network;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

public class Network {
	
	public static boolean checkInternetConnection(Context ctx) {

		boolean bConectado = false;

		ConnectivityManager connec = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		// No sólo wifi, también GPRS
		NetworkInfo[] redes = connec.getAllNetworkInfo();

		// Redes con acceso a internet
		for (int i = 0; i < redes.length; i++) {
			// ¿Tenemos conexión? ponemos a true
			if (redes[i].getState() == NetworkInfo.State.CONNECTED) {
				bConectado = true;
			}
		}
		return bConectado;
	}
	
	public static boolean isGpsEnabled(Context context) {
		try {
			LocationManager service = (LocationManager) context
					.getSystemService(context.LOCATION_SERVICE);
			return service.isProviderEnabled(LocationManager.GPS_PROVIDER)
					&& service
							.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch (Exception ex) {
			return false;
		}
	}

}
