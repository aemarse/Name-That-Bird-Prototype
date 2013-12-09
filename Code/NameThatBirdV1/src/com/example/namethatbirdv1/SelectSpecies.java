package com.example.namethatbirdv1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class SelectSpecies extends Activity {

	// CLASS VARIABLES
	String[] xcSpecies = {"American+Bittern", "Downy+Woodpecker", "Ring-billed+Gull", "Blue+Jay", 
			"Mallard", "American+Black+Duck", "Black+Vulture", "Black-naped+Oriole", "Red-created+Cardinal", 
			"American+Robin", "Tufted+Titmouse"};
	String xcEndPt = "http://www.xeno-canto.org/api/recordings.php?query=";
	
	int maxNumRecs = 5;
	
	public final static String message = "JSON String";
	
	// CLASS METHODS
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_species);
		// Show the Up button in the action bar.
		setupActionBar();
		
		// Set up the species ListView
		setupListView();
	}
	
	public void setupListView() {
		final ListView listView = (ListView)findViewById(R.id.list_Species);
		final String[] species = new String[] {"American Bittern", "Downy Woodpecker", "Ring-billed Gull", 
        		"Blue Jay", "Mallard", "American Black Duck", "Black Vulture", "Black-naped Oriole", "Red-crested Cardinal",
        		"American Robin", "Tufted Titmouse"};
		
		final ArrayList<String> speciesList = new ArrayList<String>();
		for (int i = 0; i < species.length; i++) {
			speciesList.add(species[i]);
		}
		
		final StableArrayAdapter adapter = new StableArrayAdapter(this, android.R.layout.simple_list_item_1, speciesList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
				// Make some toast
				Toast.makeText(getApplicationContext(),
					      "Loading data for species: " + species[(int) id], Toast.LENGTH_LONG)
					      .show();
				
				// XC url to get JSON data				
				String xcUrl = xcEndPt + xcSpecies[(int) id];
				
				// Start AsyncTask to download JSON data
				GetJsonTask jsonTask = new GetJsonTask();
			    jsonTask.execute(xcUrl);
				
			}
		});
	}
	
	// GetJsonTask gets the JSON data from XC
	private class GetJsonTask extends AsyncTask<String, Void, String[]> {
		@Override
		protected String[] doInBackground(String... urls){

			StringBuilder builder = new StringBuilder();
			
			String[] theString = new String[maxNumRecs];
			
			for (String url : urls) {
				HttpClient client = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(url);
				try {
					HttpResponse httpResponse = client.execute(httpGet);
					StatusLine statusLine = httpResponse.getStatusLine();
					int statusCode = statusLine.getStatusCode();
					if (statusCode == 200) {
						HttpEntity entity = httpResponse.getEntity();
						InputStream content = entity.getContent();
						BufferedReader reader = new BufferedReader(new InputStreamReader(content));
						String line;
						while((line = reader.readLine()) != null) {
							builder.append(line);
						}
					} else {
						Log.e(SelectSpecies.class.toString(), "Failed to download JSON object");
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			//Get the JSON string
			String jsonString = builder.toString();
			
			//Get the main JSON Object from the string and parse data to pass to Activity:PlayAudio
			try {
				JSONObject mainObject = new JSONObject(jsonString);
				
				int numRecordings = Integer.parseInt((String) mainObject.get("numRecordings"));
				
				if (numRecordings < maxNumRecs) {
					maxNumRecs = numRecordings;
				}
				
				JSONArray recArr = mainObject.getJSONArray("recordings");
				JSONObject[] theObjects = new JSONObject[maxNumRecs];
				
				for(int i = 0; i < maxNumRecs; i++) {
					theObjects[i] = recArr.getJSONObject(i);
					theString[i]  = theObjects[i].toString();
//					Log.i("JSON Object: ", theString[i]);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return theString;
		}
		
		@Override
		protected void onPostExecute(String[] theString) {
			// Start Activity:PlayAudio
            Intent intent = new Intent(getApplicationContext(), AudioPlayer.class);
            intent.putExtra(message, theString);
            startActivity(intent);
		}
	}
	
	// StableArrayAdapter class
	private class StableArrayAdapter extends ArrayAdapter<String> {
			
		HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();
			
		public StableArrayAdapter(Context context, int textViewResourceId, List<String> objects) {
			super(context, textViewResourceId, objects);
			for(int i = 0; i < objects.size(); ++i) {
				mIdMap.put(objects.get(i), i);
			}
		}
			
		@Override
		public long getItemId(int position) {
			String item = getItem(position);
			return mIdMap.get(item);
		}
			
		@Override
		public boolean hasStableIds() {
			return true;
		}	
	}

	// BOILER PLATE CODE
		
	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.select_species, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
