package org.billthefarmer.shorty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;

public class BroadcastActivity extends Activity
{
    /** Called when the activity is first created. */
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Get the intent
        Intent intent = getIntent();

	// Get the url and name
	String url = intent.getStringExtra("url");
	String name = intent.getStringExtra("name");

	// Create an intent to play using Intent Radio
	Intent broadcast = new Intent("org.smblott.intentradio.PLAY");

	// Put the url and name in the broadcast intent
        broadcast.putExtra("url", url);
        broadcast.putExtra("name", name);

	sendBroadcast(broadcast);

	setResult(RESULT_OK);
	finish();
    }

    // Show toast.

    void showToast(String text)
    {
	// Make a new toast

	Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
	toast.setGravity(Gravity.CENTER, 0, 0);
	toast.show();
    }
}
