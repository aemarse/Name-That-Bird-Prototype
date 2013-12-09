package com.example.namethatbirdv1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class Listen extends Activity {

	public final static String xcSpeciesName = "XenoCanto";
	String[] xcSpecies = {"American+Bittern", "Downy+Woodpecker", "Ring-billed+Gull", "Blue+Jay", 
			"Mallard", "American+Black+Duck", "Black+Vulture"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listen);
		// Show the Up button in the action bar.
		setupActionBar();
		
		// Populate the List View
		setupListView();
	}

	// Sets up the ListView
	private void setupListView() {
		final ListView listview = (ListView) findViewById(R.id.listView1);
        
        String[] species = new String[] {"American Bittern", "Downy Woodpecker", "Ring-billed Gull", 
        		"Blue Jay", "Mallard", "American Black Duck", "Black Vulture"};
        
        final ArrayList<String> list = new ArrayList<String>();
        
        for (int i = 0; i < species.length; i++) {
        	list.add(species[i]);
        }
        
        final StableArrayAdapter adapter = new StableArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        
        	listview.setAdapter(adapter);
        
        	listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                int position, long id) {
              final String item  = (String) parent.getItemAtPosition(position);
              final String theId = Long.toString(id);
              
              final String print = "Item #" + theId + item;
              Log.i("list view test", print);
              
              // Launch Activity:AudioPlayer with Intent
              Intent intent = new Intent(getApplicationContext(), AudioPlayer.class);
              intent.putExtra(xcSpeciesName, xcSpecies[(int) id]);
              startActivity(intent);
              
            }
          });
    }
    
    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
            List<String> objects) {
          super(context, textViewResourceId, objects);
          for (int i = 0; i < objects.size(); ++i) {
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
		getMenuInflater().inflate(R.menu.listen, menu);
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
