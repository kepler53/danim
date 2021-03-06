package com.pd.danim.Util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Component;

@Component
public class GoogleReverseGeocodeUtil {
	
	public String getAddress(String lat, String lng) {

		String realAddress = null;

		try {
			String apiURL = "https://maps.googleapis.com/maps/api/geocode/json?latlng="+lat+","+lng+"&key=AIzaSyCP1LvTKFOdO5IqoFOZ9q2lV79gZtVeJXY"
					+ "&language=ko&result_type=street_address";
			URL url = new URL(apiURL);
			

			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			int responseCode = con.getResponseCode();
			BufferedReader br;
			if (responseCode == 200) {
				br = new BufferedReader(new InputStreamReader(con.getInputStream()));
			} else {
				br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
			}
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = br.readLine()) != null) {
				response.append(inputLine);
			}
			br.close();
			
			String result = response.toString();
			
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(result);
			JSONArray results = (JSONArray) jsonObject.get("results");

			for (int i = 0; i < results.size(); i++) {
				JSONObject object = (JSONObject) results.get(i);
				realAddress = (String)object.get("formatted_address");
			}
			return realAddress;
		} catch (Exception e) {

		}
		return realAddress;
	}

}
