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
        
        Intent intent = getIntent();

	String url = intent.getStringExtra("url");
	String name = intent.getStringExtra("name");

	Intent broadcast = new Intent("org.smblott.intentradio.PLAY");
	// BBC Radio 4
        // broadcast.putExtra("url", "http://www.listenlive.eu/bbcradio4.m3u");
        // broadcast.putExtra("name", "BBC Radio 4");
        broadcast.putExtra("url", url);
        broadcast.putExtra("name", name);

	sendBroadcast(broadcast);

	showToast(name);

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
