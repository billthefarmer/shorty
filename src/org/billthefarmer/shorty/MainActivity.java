////////////////////////////////////////////////////////////////////////////////
//
//  Shorty - An Android shortcut generator.
//
//  Copyright (C) 2015	Bill Farmer
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Bill Farmer	 william j farmer [at] yahoo [dot] co [dot] uk.
//
///////////////////////////////////////////////////////////////////////////////

package org.billthefarmer.shorty;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.content.pm.PackageManager;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity
    implements View.OnClickListener
{
    protected final static String PREF_URL = "pref_url";
    protected final static String PREF_NAME = "pref_name";

    protected final static String PLAY = "org.smblott.intentradio.PLAY";
    protected final static String PAUSE = "org.smblott.intentradio.PAUSE";
    protected final static String RESTART = "org.smblott.intentradio.RESTART";
    protected final static String STOP = "org.smblott.intentradio.STOP";

    protected final static String BROADCAST =
	"org.billthefarmer.shorty.BROADCAST";
    protected final static String INSTALL_SHORTCUT =
	"com.android.launcher.action.INSTALL_SHORTCUT";

    private RadioGroup group;
    private TextView nameView;
    private TextView urlView;

    // On create

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

	// Get group and views
	group = (RadioGroup)findViewById(R.id.group);
	nameView = (TextView)findViewById(R.id.name);
	urlView = (TextView)findViewById(R.id.url);

	// Get buttons
	Button cancel = (Button)findViewById(R.id.cancel);
	cancel.setOnClickListener(this);

	Button create = (Button)findViewById(R.id.create);
	create.setOnClickListener(this);

	// Get preferences
	SharedPreferences preferences =
 	    PreferenceManager.getDefaultSharedPreferences(this);

	String name = preferences.getString(PREF_NAME, null);
	String url = preferences.getString(PREF_URL, null);

	// Set fields from preferences
	if (name != null)
	    nameView.setText(name);
	if (url != null)
	    urlView.setText(url);
    }

    // Menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
	// Inflate the menu; this adds items to the action bar if it
	// is present.
	getMenuInflater().inflate(R.menu.main, menu);

	return true;
    }

    // On options item

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
	// Get id

	int id = item.getItemId();
	switch (id)
	{

	    // Lookup

	case R.id.action_lookup:
	    return onLookupClick(item);

	    // Help

	case R.id.action_help:
	    return onHelpClick(item);

	    // About

	case R.id.action_about:
	    return onAboutClick(item);

	default:
	    return false;
	}
    }

    // On lookup click

    private boolean onLookupClick(MenuItem item)
    {
	Intent intent = new Intent(this, LookupActivity.class);
	startActivity(intent);

	return true;
    }

    // On help click

    private boolean onHelpClick(MenuItem item)
    {
	Intent intent = new Intent(this, HelpActivity.class);
	startActivity(intent);

	return true;
    }

    // On about click

    private boolean onAboutClick(MenuItem item)
    {
	Intent intent = new Intent(this, AboutActivity.class);
	startActivity(intent);

	return true;
    }

    // On click

    @Override
    public void onClick(View v)
    {
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
		showToast(R.string.not_installed);
		setResult(RESULT_CANCELED);
		finish();
	    }

	    else
	    {
		// Get the name and url
		String name = nameView.getText().toString();
		String url = urlView.getText().toString();

		// Create the shortcut intent
		Intent shortcut = new Intent(BROADCAST);

		// Get the action
		int action = group.getCheckedRadioButtonId();
		switch (action)
		{
		    // Play

		case R.id.play:
		    // Get resources
		    Resources resources = getResources();

		    // Check the fields
		    if (name == null || name.length() == 0)
			name = resources.getString(R.string.default_name);
		    if (url == null || url.length() == 0)
			url = resources.getString(R.string.default_url);

		    // Set extra fields
		    shortcut.putExtra("url", url);
		    shortcut.putExtra("name", name);
		    shortcut.putExtra("action", PLAY);

		    // Get preferences
		    SharedPreferences preferences =
			PreferenceManager.getDefaultSharedPreferences(this);

		    // Get editor
		    SharedPreferences.Editor editor = preferences.edit();

		    // Update preferences
		    editor.putString(PREF_NAME, name);
		    editor.putString(PREF_URL, url);
		    editor.apply();
		    break;

		    // Stop

		case R.id.stop:
		    name = "Stop";
		    shortcut.putExtra("action", STOP);
		    break;

		    // Pause

		case R.id.pause:
		    name = "Pause";
		    shortcut.putExtra("action", PAUSE);
		    break;

		    // Restart

		case R.id.restart:
		    name = "Restart";
		    shortcut.putExtra("action", RESTART);
		    break;
		}

		// Create the shortcut
		Intent intent = new Intent(INSTALL_SHORTCUT);
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcut);
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, icon.getBitmap());

		// Send broadcast
		sendBroadcast(intent);
		showToast(name);

		// Make the activity go away
		setResult(RESULT_CANCELED);
		finish();
	    }
	    break;
	}
    }

    // Show toast.

    void showToast(int id)
    {
	// Get text from resources
	Resources resources = getResources();
	String text = resources.getString(id);
	showToast(text);
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
