package org.billthefarmer.shorty;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;

public class ShortcutActivity extends Activity
{
    /** Called when the activity is first created. */
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
 
	// Get package manager

	PackageManager manager = getPackageManager();

	// Get Intent Radio icon

	BitmapDrawable icon = null;
	try
	{
	    icon = (BitmapDrawable)
		manager.getApplicationIcon("org.smblott.intentradio");
	}

	catch (Exception e) {}

	if (icon == null)
	{
	    showToast("Intent Radio not installed\nPlease install IntentRadio");
	    setResult(RESULT_CANCELED);
	    finish();
	}

	else
	{
	    // Create the shortcut intent
	    Intent shortcut = new Intent("org.billthefarmer.shorty.BROADCAST");
	    shortcut.putExtra("url", "http://www.listenlive.eu/bbcradio4.m3u");
	    shortcut.putExtra("name", "BBC Radio 4");

	    // Create the shortcut
	    Intent intent = new Intent();
	    intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcut);
	    intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "BBC Radio 4");
	    intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, icon.getBitmap());
	    setResult(RESULT_OK, intent);
	    finish();
	}
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
