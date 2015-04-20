package org.billthefarmer.shorty;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity
    implements View.OnClickListener
{
    private RadioGroup group;
    private TextView nameView;
    private TextView urlView;

    // On create

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

	group = (RadioGroup)findViewById(R.id.group);
	nameView = (TextView)findViewById(R.id.name);
	urlView = (TextView)findViewById(R.id.url);

	Button cancel = (Button)findViewById(R.id.cancel);
	cancel.setOnClickListener(this);

	Button create = (Button)findViewById(R.id.create);
	create.setOnClickListener(this);
    }

    // On click

    @Override
    public void onClick(View v)
    {
        // Do something in response to button click

	// Get id

	int id = v.getId();
	switch (id)
	{
	    // Cancel

	case R.id.cancel:
	    setResult(RESULT_CANCELED);
	    finish();
	    break;


	    // Create

	case R.id.create:

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
		showToast("Intent Radio not installed\n" +
			  "Please install IntentRadio");
		setResult(RESULT_CANCELED);
		finish();
	    }

	    else
	    {
		// Get the url and name
		String url = urlView.getText().toString();
		String name = nameView.getText().toString();

		// Create the shortcut intent
		Intent shortcut = new
		    Intent("org.billthefarmer.shorty.BROADCAST");

		// Get the action
		int action = group.getCheckedRadioButtonId();
		switch (action)
		{
		    // Play

		case R.id.play:
		    // Check the fields
		    if (url == null || url.length() == 0)
			url = "http://www.listenlive.eu/bbcradio4.m3u";
		    if (name == null || name.length() == 0)
			name = "BBC Radio 4";
		    shortcut.putExtra("url", url);
		    shortcut.putExtra("name", name);
		    shortcut.putExtra("action",
				      "org.smblott.intentradio.PLAY");
		    break;

		    // Stop

		case R.id.stop:
		    name = "Stop";
		    shortcut.putExtra("action",
				      "org.smblott.intentradio.STOP");
		    break;

		    // Pause

		case R.id.pause:
		    name = "Pause";
		    shortcut.putExtra("action",
				      "org.smblott.intentradio.PAUSE");
		    break;

		    // Restart

		case R.id.restart:
		    name = "Restart";
		    shortcut.putExtra("action",
				      "org.smblott.intentradio.RESTART");
		    break;
		}

		// Create the shortcut
		Intent intent = new
		    Intent("com.android.launcher.action.INSTALL_SHORTCUT");
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcut);
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, icon.getBitmap());

		sendBroadcast(intent);
		showToast(name);

		setResult(RESULT_CANCELED);
		finish();
	    }
	    break;
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
