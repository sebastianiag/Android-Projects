package com.sushimaan.shiragiku;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class RequestHandler {
	public String requestJSON(String url) throws ClientProtocolException, IOException{
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpEntity httpEntity = null;
		HttpResponse httpResponse = null;
		
		String[] url_parts = url.split("/");
		String board = url_parts[3];
		String threadnumber = url_parts[5];
		
		
		String json_url = "http://a.4cdn.org/" + board + "/thread/" + threadnumber +".json";
		
		HttpGet httpGet = new HttpGet(json_url);
		//httpGet.setHeader("User-Agent", "Mozilla/5.0 (X11; U; Linuxi686) Gecko/20071127 Firefox/2.0.0.11)");
		httpResponse = httpClient.execute(httpGet);
		
		
		httpEntity = httpResponse.getEntity();
		
		String response = EntityUtils.toString(httpEntity);
		
		return response;
				
	}
}
