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

import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import org.json.JSONArray;

public class LookupActivity extends Activity
    implements AdapterView.OnItemClickListener, View.OnClickListener
{
    private final static String PREF_ENTRIES = "pref_entries";
    private final static String PREF_VALUES = "pref_values";

    private TextView nameView;
    private TextView urlView;
    private ListView listView;

    private ArrayAdapter<String> arrayAdapter;

    private ArrayList<String> entryList;
    private ArrayList<String> valueList;

    // On create

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lookup);

	// Get text views
	nameView = (TextView)findViewById(R.id.lu_name);
	urlView = (TextView)findViewById(R.id.lu_url);

	// Get buttons, set listener
	Button add = (Button)findViewById(R.id.add);
	if (add != null)
	    add.setOnClickListener(this);

	Button remove = (Button)findViewById(R.id.remove);
	if (remove != null)
	    remove.setOnClickListener(this);

	Button select = (Button)findViewById(R.id.select);
	if (select != null)
	    select.setOnClickListener(this);

	// get list view
	listView = (ListView)findViewById(R.id.list);

	if (listView != null)
	    listView.setOnItemClickListener(this);

	// Enable back navigation on action bar
	ActionBar actionBar = getActionBar();
	if (actionBar != null)
	    actionBar.setDisplayHomeAsUpEnabled(true);

	// Get preferences
	SharedPreferences preferences =
 	    PreferenceManager.getDefaultSharedPreferences(this);

	String name = preferences.getString(MainActivity.PREF_NAME, null);
	String url = preferences.getString(MainActivity.PREF_URL, null);

	// Set fields from preferences
	if (name != null)
	    nameView.setText(name);
	if (url != null)
	    urlView.setText(url);

	String entryJSON = preferences.getString(PREF_ENTRIES, null);
	String valueJSON = preferences.getString(PREF_VALUES, null);

	// Get resources
	Resources resources = getResources();

	// Add entries
	if (entryJSON != null)
	{
	    try
	    {
		JSONArray entryArray = new JSONArray(entryJSON);
		entryList = new ArrayList<String>();
		for (int i = 0; !entryArray.isNull(i); i++)
		    entryList.add(entryArray.getString(i));
	    }

	    catch (Exception e)
	    {
		String entries[] = resources.getStringArray(R.array.entries);
		entryList =
		    new ArrayList<String>(Arrays.asList(entries));
	    }
	}

	else
	{
	    String entries[] = resources.getStringArray(R.array.entries);
	    entryList =
		new ArrayList<String>(Arrays.asList(entries));
	}

	if (valueJSON != null)
	{
	    try
	    {
		JSONArray valueArray = new JSONArray(valueJSON);
		valueList = new ArrayList<String>();
		for (int i = 0; !valueArray.isNull(i); i++)
		    valueList.add(valueArray.getString(i));
	    }

	    catch (Exception e)
	    {
		String values[] = resources.getStringArray(R.array.values);
		valueList =
		    new ArrayList<String>(Arrays.asList(values));
	    }
	}

	else
	{
	    String values[] = resources.getStringArray(R.array.values);
	    valueList =
		new ArrayList<String>(Arrays.asList(values));
	}

	// Set array adapter
	arrayAdapter =
	    new ArrayAdapter<String>(this,
			     android.R.layout.simple_list_item_activated_1,
			     entryList);

	listView.setAdapter(arrayAdapter);
    }

    // Menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
	// Inflate the menu; this adds items to the action bar if it
	// is present.
	getMenuInflater().inflate(R.menu.lookup, menu);

	return true;
    }

    // On options item selected

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
	Intent broadcast;

	// Get id

	int id = item.getItemId();
	switch (id)
	{
	    // Home

	case android.R.id.home:
	    Intent intent = new Intent(this, MainActivity.class);
	    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    startActivity(intent);
	    break;

	    // Play

	case R.id.action_play:
	    // Get the name and url
	    String name = nameView.getText().toString();
	    String url = urlView.getText().toString();

	    // Get resources
	    Resources resources = getResources();

	    // Check the fields
	    if (name == null || name.length() == 0)
		name = resources.getString(R.string.default_name);
	    if (url == null || url.length() == 0)
		url = resources.getString(R.string.default_url);

	    // Create an intent to play using Intent Radio
	    broadcast = new Intent(MainActivity.PLAY);

	    // Put the name and url in the broadcast intent
	    broadcast.putExtra("name", name);
	    broadcast.putExtra("url", url);

	    // Send it
	    sendBroadcast(broadcast);
	    break;

	    // Pause

	case R.id.action_pause:
	    // Create an intent to pause using Intent Radio
	    broadcast = new Intent(MainActivity.PAUSE);

	    sendBroadcast(broadcast);
	    break;

	    // Restart

	case R.id.action_restart:
	    // Create an intent to restart using Intent Radio
	    broadcast = new Intent(MainActivity.RESTART);

	    sendBroadcast(broadcast);
	    break;

	    // Stop

	case R.id.action_stop:
	    // Create an intent to stop using Intent Radio
	    broadcast = new Intent(MainActivity.STOP);

	    sendBroadcast(broadcast);
	    break;

	default:
	    return false;
	}

	return true;
    }

    // On item click

    @Override
    public void onItemClick(AdapterView parent, View view,
			    int position, long id)
    {
	nameView.setText(entryList.get(position));
	urlView.setText(valueList.get(position));	
    }

    // On click

    @Override
    public void onClick(View v)
    {
	JSONArray entryArray;
	JSONArray valueArray;

	// Get preferences
	SharedPreferences preferences =
	    PreferenceManager.getDefaultSharedPreferences(this);

	// Get editor
	SharedPreferences.Editor editor = preferences.edit();

	// Get id

	int id = v.getId();
	switch (id)
	{
	    // Add

	case R.id.add:
	    // Add entry
	    entryList.add(nameView.getText().toString());
	    valueList.add(urlView.getText().toString());

	    // Get entries
	    entryArray = new JSONArray(entryList);
	    valueArray = new JSONArray(valueList);

	    // Update preferences
	    editor.putString(PREF_ENTRIES, entryArray.toString());
	    editor.putString(PREF_VALUES, valueArray.toString());
	    editor.apply();

	    // Update display
	    arrayAdapter.notifyDataSetChanged();
	    break;

	    // Remove

	case R.id.remove:
	    // Check entry is selected
	    if (listView.getCheckedItemCount() == 0)
	    {
		showToast("Nothing selected");
		break;
	    }

	    // Remove entry
	    int pos = listView.getCheckedItemPosition();
	    entryList.remove(pos);
	    valueList.remove(pos);

	    // Get entries
	    entryArray = new JSONArray(entryList);
	    valueArray = new JSONArray(valueList);

	    // Update preferences
	    editor.putString(PREF_ENTRIES, entryArray.toString());
	    editor.putString(PREF_VALUES, valueArray.toString());
	    editor.apply();

	    // Update display
	    arrayAdapter.notifyDataSetChanged();
	    break;

	    // Select

	case R.id.select:

	    // Update preferences
	    editor.putString(MainActivity.PREF_URL,
			     urlView.getText().toString());
	    editor.putString(MainActivity.PREF_NAME,
			     nameView.getText().toString());
	    editor.apply();

	    // Create intent
	    Intent intent = new Intent(this, MainActivity.class);
	    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

	    // Go back
	    startActivity(intent);
	    break;
	}
    }

    // Checks external storage is available for read and write

    public boolean isExternalStorageWritable()
    {
	String state = Environment.getExternalStorageState();
	if (Environment.MEDIA_MOUNTED.equals(state))
	    return true;

	return false;
    }
    /*
    void writeFile()
    {
	// get the path to sdcard
	File sdcard = Environment.getExternalStorageDirectory();
	// to this path add a new directory path
	File dir = new File(sdcard.getAbsolutePath() + "/your-dir-name/");
	// create this directory if not already created
	dir.mkdir();
	// create the file in which we will write the contents
	File file = new File(dir, "My-File-Name.txt");
	FileOutputStream os = outStream = new FileOutputStream(file);
	String data = “This is the content of my file”;
	os.write(data.getBytes());
	os.close();
    }
    */
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
