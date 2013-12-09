package com.example.namethatbirdv1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class NTBMain extends Activity {

	// CLASS METHODS	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ntbmain);
    }
    
    // Button callback
    public void onClick(View v) {
    	//Determine which button was pressed
    	switch(v.getId()) 
    	{
    	case R.id.b_species:
    		//Handle
    		Log.i("NTBMain:onClick", "Launching Activity:SelectSpecies");
    		Intent intent = new Intent(this, SelectSpecies.class);
        	startActivity(intent);
    		break;
    	case R.id.b_location:
    		//Handle
    		Log.i("NTBMain:onClick", "Launching Activity:SelectLocation");
//    		Intent intent = new Intent(this, SelectLocation.class);
//        	startActivity(intent);
    		break;
    	case R.id.b_callType:
    		//Handle
    		Log.i("NTBMain:onClick", "Launching Activity:SelectCall");
//    		Intent intent = new Intent(this, SelectCall.class);
//        	startActivity(intent);
    		break;
    	case R.id.b_random:
    		//Handle
    		Log.i("NTBMain:onClick", "Launching Activity:SelectRandom");
//    		Intent intent = new Intent(this, SelectRandom.class);
//        	startActivity(intent);
    		break;
    	default:
    		throw new RuntimeException("Unknown Button Id");
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.ntbmain, menu);
        return true;
    }
    
}
