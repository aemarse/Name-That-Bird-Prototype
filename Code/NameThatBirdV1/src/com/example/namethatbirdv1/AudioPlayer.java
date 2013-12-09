package com.example.namethatbirdv1;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class AudioPlayer extends Activity {

	// CLASS VARIABLES
	public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
//	private ProgressDialog mProgressDialog;
	
	TextView t_fileTxt;
	TextView t_recTxt;
	
	// Init data holders
	String[] id = null;
    String[] en = null;
    String[] rec = null;
    String[] cnt = null;
    String[] loc = null;
    String[] file = null;
    
    int numRecs;
    int currFile;
    MediaPlayer player = new MediaPlayer();
    
	// CLASS METHODS
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_audio_player);
		// Show the Up button in the action bar.
		setupActionBar();
		
		// Get TextView from view
		t_fileTxt = (TextView) findViewById(R.id.t_fileTxt);
		t_recTxt = (TextView) findViewById(R.id.t_recTxt);
		
		// Set the default file number when the activity starts
		currFile = 0;
		
		// Get the JSONString from the intent
		Intent intent = getIntent();
	    String[] jsonString = intent.getStringArrayExtra(SelectSpecies.message);
	    
	    // Convert back from JSONString to JSONObject
	    numRecs = jsonString.length;
	    
	    // Now that we know how many recordings, allocate memory for data holders
	    id   = new String[numRecs];
	    en   = new String[numRecs];
	    rec  = new String[numRecs];
	    cnt  = new String[numRecs];
	    loc  = new String[numRecs];
	    file = new String[numRecs];
	    
	    for (int i = 0; i < numRecs; i++) {
	    	try {
	    		//Get the current object
	    		JSONObject theObj = new JSONObject(jsonString[i]);
	    		
	    		//Get the data from the object
	    		en[i]   = theObj.getString("id");
	    		en[i]   = theObj.getString("en");
	    		rec[i]  = theObj.getString("rec");
	    		cnt[i]  = theObj.getString("cnt");
	    		loc[i]  = theObj.getString("loc");
	    		file[i] = theObj.getString("file");
	    		
//	            new DownloadFileAsync().execute(file[i]);
	    		
	    	} catch (JSONException e) {
	    		// TODO Auto-generated catch block
	    		e.printStackTrace();
	    	}
	    }
	    
	    new DownloadFileAsync(file).execute(file);
	}
	
	// Create the download progress dialog
//	@Override
//	protected Dialog onCreateDialog(int id) {
//		switch(id) {
//		case DIALOG_DOWNLOAD_PROGRESS:
//			mProgressDialog = new ProgressDialog(this);
//			mProgressDialog.setMessage("Downloading file ");
//			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//			mProgressDialog.setCancelable(false);
//			mProgressDialog.show();
//			return mProgressDialog;
//		default:
//			return null;
//		}
//	}
	
	// Download File Class
	class DownloadFileAsync extends AsyncTask<String, String, String> {
		
		int current = 0;
		String[] paths;
        String fpath;
        
        public DownloadFileAsync(String[] paths) {
            super();
            this.paths = paths;
        }
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
//			onCreateDialog(DIALOG_DOWNLOAD_PROGRESS);
		}
		
		@Override
		protected String doInBackground(String... aurl) {
			
			int rows = aurl.length;
			
			while(current < rows) {
			
			int count;
			
			try {
				fpath = this.paths[current];
				
				Log.e("File URL: ", fpath);
                URL url = new URL(fpath);
				URLConnection conn = url.openConnection();
				conn.connect();
				
				int lengthOfFile = conn.getContentLength();
				Log.d("DownloadFileAsync", "Length of file: " + lengthOfFile);
				
				InputStream input = new BufferedInputStream(url.openStream());
				
				String urlString = url.toString();
				String[] parts = urlString.split("=");
				String fileId = parts[1];
				
//				String outFilename = "/sdcard/test/" + fileId + ".mp3";
//				Log.e("filename: ", outFilename);
//				
//				OutputStream output = new FileOutputStream(outFilename);
				
				String outFilename = getFilesDir() + "/" + fileId + ".mp3";
				Log.e("filename: ", outFilename);
				
				OutputStream output = new FileOutputStream(outFilename);
				
				byte data[] = new byte[1024];
				
				long total = 0;
				
				while ((count = input.read(data)) != -1) {
					total += count;
//					publishProgress(""+(int)((total*100)/lengthOfFile));
					output.write(data, 0, count);
				}
				
				output.flush();
				output.close();
				input.close();
				
			} catch (Exception e) {
			}
			return null;
		}
			return null;
	}
		
//		protected void onProgressUpdate(String... progress) {
////			 Log.d("ANDRO_ASYNC",progress[0]);
//			 mProgressDialog.setProgress(Integer.parseInt(progress[0]));
//		}

		@Override
		protected void onPostExecute(String unused) {
		}
		
	}
	
	// BUTTON HANDLERS
	
	//Previous
	public void onClickPrev(View v) {
		
		//Only update the player if  
		if (currFile > 0) {
			currFile --;
			
			updateText();
			player.pause();
			playAudio();
		}	
	}
	
	//Pause/Stop
	public void onClickPause(View v) {
		player.stop();
	}

	//Play
	public void onClickPlay(View v) {
		updateText();
		playAudio();
	}

	//Next
	public void onClickNext(View v) {
		
		if (currFile < numRecs) {
			currFile ++;
		}
		
		updateText();
		player.pause();
		playAudio();
	}
	
	// HELPER FUNCTIONS
	
	//Sets up mediaplayer and starts audio playback
	public void playAudio() {
    		
    		player.reset();
    		
    		// Set audio player stream type
        	player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        	// Set source url
        	try {
        		player.setDataSource(file[currFile]);
        		player.prepare();
        	} catch (IllegalArgumentException e) {
        		// TODO Auto-generated catch block
        		e.printStackTrace();
        	} catch (SecurityException e) {
        		// TODO Auto-generated catch block
        		e.printStackTrace();
        	} catch (IllegalStateException e) {
        		// TODO Auto-generated catch block
        		e.printStackTrace();
        	} catch (IOException e) {
        		// TODO Auto-generated catch block
        		e.printStackTrace();
        	}
        	
			player.start();

	}
	
	// Releases mediaplayer
    private void releasePlayer() {
		 if (player != null) {
			 player.release();
	         player = null;
	     }
	 }
	 
    // Called when mediaplayer is done
	 @Override
	 protected void onDestroy() {
		 super.onDestroy();
		 releasePlayer();
	 }
	
	// Updates the TextView to display the current info
	public void updateText() {
		String s_fileTxt = "Playing file: " + currFile + "/" + numRecs;
		String s_recTxt     = "Copyright: " + rec[currFile];
		t_fileTxt.setText(s_fileTxt);
		t_recTxt.setText(s_recTxt);
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
		getMenuInflater().inflate(R.menu.audio_player, menu);
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