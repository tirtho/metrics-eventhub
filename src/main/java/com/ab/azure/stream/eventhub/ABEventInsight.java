package com.ab.azure.stream.eventhub;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;


public class ABEventInsight {
	
	private String apiAddress;
	private String endpoint;
	private String key;
	
	public ABEventInsight(String apiAddress, String endpoint, String key) {
		super();
		this.apiAddress = apiAddress;
		this.endpoint = endpoint;
		this.key = key;
	}

	public String getPrediction(String inputData) {
		// TODO: Add call to AML API
		return null;
	}
	
	public String getAnomaly(String requestData) {
		CloseableHttpClient client = HttpClients.createDefault();
	    try {
	        HttpPost request = new HttpPost(endpoint + apiAddress);
	        // Request headers.
	        request.setHeader("Content-Type", "application/json");
	        request.setHeader("Ocp-Apim-Subscription-Key", key);
	        request.setEntity(new StringEntity(requestData));
	        try (CloseableHttpResponse response = client.execute(request)) {
	            HttpEntity respEntity = response.getEntity();
	            if (respEntity != null) {
	                return EntityUtils.toString(respEntity, "utf-8");
	            }
	        } catch (Exception respEx) {
	            respEx.printStackTrace();
	        }
	    } catch (IOException ex) {
	        System.err.println("Exception on Anomaly Detector: " + ex.getMessage());
	        ex.printStackTrace();
	    } finally {
	    	if (client != null) {
	    		try {
					client.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
	    }
	    return null;
	}
}
