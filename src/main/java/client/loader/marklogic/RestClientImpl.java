package client.loader.marklogic;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.core.MultivaluedMap;

import org.json.simple.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPDigestAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * MarkLogic Rest Based HA test. 
 * 
 * Main class to will execute the test, and help log elasped time of a MarkLogic failover scenario.
 *
 * @author Eric Putnam
 */
public class RestClientImpl {

	public static void main( String[] args ) {
		String serverName = "vgsix-ml-ml";
		int totalClusterHosts = 9;		
		int socketReadTimeout = 4000;
		String port = "8045";
		int numberOfInsertCycles = 50000;
		Client client = Client.create();
		
		//Initial our time watch feature, to track time elapsed.
		TimeWatch watch = TimeWatch.start();
        // do something
    	SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
        Date now = new Date();
		
		try {
			client.addFilter(new HTTPDigestAuthFilter("admin","admin"));
			client.setReadTimeout(socketReadTimeout);
			
			//Setup our REST based MarkLogic resource.
			WebResource webResource2 = client.resource("http://" + serverName + totalClusterHosts + ":" + port+ "/v1/documents");
			
			//Show time elapsed before we fire off the program.
			long passedTimeInSeconds = watch.time(TimeUnit.SECONDS);
	        System.out.println(" Begin [time elapsed] " + watch.time(TimeUnit.SECONDS) + " [system time]" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			
	        //Iteration value
	        for(int x = 0; x <= numberOfInsertCycles; x++){
				MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
		        queryParams.add("uri", "/image/" + x +".json");
		        
		        JSONObject query = new JSONObject();
	        	query.put("testdata", x);

	        	boolean success = false;
	        	int failedNumber = 0;
	        	int iterationCount = 0;
	        	do {
	        		
					try{
						ClientResponse response2 = webResource2.queryParams(queryParams).entity(query.toJSONString()).put(ClientResponse.class);
						if (response2.getStatus() > 204) {
							throw new ClientHandlerException("Failed : HTTP error code : " + response2.getStatus());
						}else {
							System.out.println("wrote document" + "/image/" + x + ".json" + " [time elapsed] " + watch.time(TimeUnit.SECONDS) + " [system time]" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
							success = true;
							failedNumber = 0;
						}
		        	}catch(ClientHandlerException ste){
		        		//REST Service Error. Intelligently determine how to proceed.
		        		ste.printStackTrace();
		        		System.out.println(iterationCount);
		        		
		        		//If we fail, create and renew the REST resource, to ensure that we have a good one.
		        		try{
			        		client.destroy();
			        		client = Client.create();
			        		client.addFilter(new HTTPDigestAuthFilter("admin","admin"));
			    			client.setReadTimeout(socketReadTimeout);
		        		}catch(Exception e){
		        			e.printStackTrace();
		        		}
		    			
		        		if((failedNumber >= 0) && (failedNumber < totalClusterHosts)){
		        			String host = serverName + String.valueOf(failedNumber + 1);
		        			webResource2 = client.resource("http://" + host + ":" + port+ "/v1/documents");
		        			System.out.println("failed try again! [" + host + "]");
		        		} else {
		        			String host = serverName + String.valueOf(failedNumber);
		        			webResource2 = client.resource("http://" + host + ":" + port+ "/v1/documents");
		        			System.out.println("failed try again! [" + host + "]");
		        			failedNumber = -1;
		        		}
		        		
		        		failedNumber++;
		        		iterationCount++;
		        		
		        	}
	        	} while(!success);				
	        }
	
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//It's a good idea to close up the web resource if we are done using it.
			if(client != null){
				client.destroy();
			}
		}
	}
	
	
}
