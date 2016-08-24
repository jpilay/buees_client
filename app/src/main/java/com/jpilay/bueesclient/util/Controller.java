package com.jpilay.bueesclient.util;



import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.jpilay.bueesclient.R;

/**
 * Class Controller send/receive data from server
 * */
public class Controller {
	
	private JSONParser jsonParser;
    private Context mContext;

	public Controller(Context context){
        this.mContext = context;
        this.jsonParser = new JSONParser();
	}

    public JSONObject login(String username, String password){

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username",username));
        params.add(new BasicNameValuePair("password",password));

        JSONObject json = jsonParser.postJSONFromUrl(mContext.getString(R.string.url) + "signin/", params);
        return json;
    }

	public JSONObject signup(String username, String password, String email, String group_name){

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("username",username));
			params.add(new BasicNameValuePair("password",password));
			params.add(new BasicNameValuePair("email", email));
			params.add(new BasicNameValuePair("group_name",group_name));

			JSONObject json = jsonParser.postJSONFromUrl(mContext.getString(R.string.url) + "signup/", params);
			return json;
	}

	public JSONObject recoveryPassword(String username){

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username",username));

		JSONObject json = jsonParser.postJSONFromUrl(mContext.getString(R.string.url) + "recovery_password/", params);
		return json;
	}

    public JSONObject recoveryPassword(String username, String password){

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username",username));
        params.add(new BasicNameValuePair("password",password));

        JSONObject json = jsonParser.postJSONFromUrl(mContext.getString(R.string.url) + "recovery_password/", params);
        return json;
    }

    public JSONObject schedule(){
        JSONObject jsonObject = null;
        JSONArray jsonArray = jsonParser.getJSONFromUrl(mContext.getString(R.string.url) + "api/BusSchedule/?format=json", "");

        if(jsonArray != null){
            try {

                if (jsonArray.length()!=0)
                    jsonObject = jsonArray.getJSONObject(0);

            } catch (JSONException e) {
                Log.e("Buees", e.getMessage());
            }
        }

        return jsonObject;
    }

    public JSONArray groups(){
        JSONArray jsonArray = jsonParser.getJSONFromUrl(mContext.getString(R.string.url) + "api/UserGroup/?format=json", "");

        return jsonArray;
    }

    public JSONArray publications(){
        JSONArray jsonArray = jsonParser.getJSONFromUrl(mContext.getString(R.string.url) + "api/DriverPublication/?format=json", "");

        return jsonArray;
    }

    public JSONObject routes(String id){

        JSONObject json = jsonParser.getJSONFromUrl(mContext.getString(R.string.url) + "api/BusRoute/" + id + "/?format=json");
        return json;
    }

    public JSONArray routes(){

        JSONArray jsonArray = jsonParser.getJSONFromUrl(mContext.getString(R.string.url) + "api/BusRoute/?format=json", "");
        return jsonArray;
    }

    public JSONObject registerDevice(String username, String registration_id, String device_id){

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username",username));
        params.add(new BasicNameValuePair("registration_id",registration_id));
        params.add(new BasicNameValuePair("device_id",device_id));

        JSONObject json = jsonParser.postJSONFromUrl(mContext.getString(R.string.url) + "register_device//", params);
        return json;
    }
	
}
