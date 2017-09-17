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
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;

import com.opencsv.CSVReader;

// LookupActivity
public class LookupActivity extends Activity
    implements AdapterView.OnItemClickListener, View.OnClickListener,
               SearchView.OnQueryTextListener
{
    private final static String PREF_ENTRIES = "pref_entries";
    private final static String PREF_VALUES = "pref_values";
    private final static String SHORTY_DIR = "Shorty";
    private final static String SHORTY_FILE = "entries.json";

    protected final static String SHORTY_EXTRA = "extras.csv";
    protected final static String PATH = "path";

    protected final static int IMPORT = 1;
    private final static int TEXT = 1;

    private SearchView searchView;
    private MenuItem searchItem;
    private TextView nameView;
    private TextView urlView;
    private ListView listView;

    private ArrayAdapter<String> arrayAdapter;

    private List<String> entryList;
    private List<String> valueList;

    // On create
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Get preferences
        SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(this);

        boolean dark = preferences.getBoolean(MainActivity.PREF_DARK, true);

        if (dark)
            setTheme(R.style.AppDarkTheme);

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
            new ArrayAdapter<String>(this, android.R.layout
                                     .simple_list_item_activated_1,
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

        searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();

        if (searchView != null)
        {
            searchView.setQueryHint(getText(R.string.hint));
            searchView.setOnQueryTextListener(this);
        }

        return true;
    }

    // On options item selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Intent broadcast = null;

        // Get id
        int id = item.getItemId();
        switch (id)
        {
        // Home
        case android.R.id.home:
            setResult(RESULT_CANCELED);
            finish();
            break;

        // Play
        case R.id.action_play:
            // Get the name and url
            String name = nameView.getText().toString();
            String url = urlView.getText().toString();

            // Check the fields
            if (name == null || name.length() == 0)
                name = getString(R.string.default_name);
            if (url == null || url.length() == 0)
                url = getString(R.string.default_url);

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

        // Resume
        case R.id.action_resume:
            // Create an intent to resume using Intent Radio
            broadcast = new Intent(MainActivity.RESTART);
            sendBroadcast(broadcast);
            break;

        // Stop
        case R.id.action_stop:
            // Create an intent to stop using Intent Radio
            broadcast = new Intent(MainActivity.STOP);
            sendBroadcast(broadcast);
            break;

        // Save
        case R.id.action_save:
            saveData();
            break;

        // Restore
        case R.id.action_restore:
            restoreData();
            break;

        // Import
        case R.id.action_import:
            importData();
            break;

        default:
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    // onQueryTextChange
    @Override
    public boolean onQueryTextChange (String newText)
    {
        arrayAdapter.getFilter().filter(newText);
        return true;
    }

    // onQueryTextSubmit
    @Override
    public boolean onQueryTextSubmit (String query)
    {
        String item = (String) listView.getItemAtPosition(0);
        nameView.setText(item);
        int index = entryList.indexOf(item);
        urlView.setText(valueList.get(index));

        searchItem.collapseActionView();
        listView.setItemChecked(index, true);
        listView.smoothScrollToPosition(index);
        return true;
    }

    // On item click
    @Override
    public void onItemClick(AdapterView parent, View view,
                            int position, long id)
    {
        String item = (String) parent.getItemAtPosition(position);

        nameView.setText(item);
        int index = entryList.indexOf(item);
        urlView.setText(valueList.get(index));

        if (searchItem.isActionViewExpanded())
            searchItem.collapseActionView();

        listView.setItemChecked(index, true);
        listView.smoothScrollToPosition(index);
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
            arrayAdapter = new
                ArrayAdapter<String>(this, android.R.layout
                                     .simple_list_item_activated_1,
                                     entryList);
            listView.setAdapter(arrayAdapter);
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
            String item = (String) listView.getSelectedItem();
            if (entryList.contains(item))
            {
                int index = entryList.indexOf(item);
                entryList.remove(index);
                valueList.remove(index);
            }

            // Get entries
            entryArray = new JSONArray(entryList);
            valueArray = new JSONArray(valueList);

            // Update preferences
            editor.putString(PREF_ENTRIES, entryArray.toString());
            editor.putString(PREF_VALUES, valueArray.toString());
            editor.apply();

            // Update display
            arrayAdapter = new
                ArrayAdapter<String>(this, android.R.layout
                                     .simple_list_item_activated_1,
                                     entryList);
            listView.setAdapter(arrayAdapter);
            break;

        // Select
        case R.id.select:

            // Create intent
            Intent intent = new Intent();
            intent.putExtra(MainActivity.URL, urlView.getText().toString());
            intent.putExtra(MainActivity.NAME, nameView.getText().toString());
            setResult(RESULT_OK, intent);
            finish();
            break;
        }
    }

    // Save data
    void saveData()
    {
        // Create a JSON array
        JSONArray data = new JSONArray();

        // Loop through the data
        int i = 0;
        for (String name : entryList)
        {
            try
            {
                // Create a JSON object
                JSONObject entry = new JSONObject();

                // Add the entry
                entry.put("url", valueList.get(i));
                entry.put("name", name);
                data.put(entry);
            }

            catch (Exception e) {}

            i++;
        }

        // Open a file to write the JSON array
        try
        {
            // Get the path to sdcard
            File sdcard = Environment.getExternalStorageDirectory();

            // Add a new directory path
            File dir = new File(sdcard, SHORTY_DIR);

            // Create this directory if not already created
            dir.mkdir();

            // Create the file
            File file = new File(dir, SHORTY_FILE);

            // Create a file writer
            FileWriter writer = new FileWriter(file);

            // Write the data
            writer.write(data.toString(2));
            writer.close();

            showToast(R.string.data_saved, i);
        }

        catch (Exception e)
        {
            showToast(R.string.no_write);
        }
    }

    // Restore data
    void restoreData()
    {
        StringBuilder text = new StringBuilder();

        // Open a file to read the JSON
        try
        {
            // Get the path to sdcard
            File sdcard = Environment.getExternalStorageDirectory();

            // Add a new directory path
            File dir = new File(sdcard, SHORTY_DIR);

            // Create the file
            File file = new File(dir, SHORTY_FILE);

            // Create a file reader
            FileReader reader = new FileReader(file);

            // Create a buffered reader
            BufferedReader buffer = new BufferedReader(reader);

            String line;
            while ((line = buffer.readLine()) != null)
                text.append(line);

            buffer.close();
        }

        // No file or can't read it

        catch (Exception e)
        {
            showToast(R.string.no_read);
            return;
        }

        // Clear the entries
        entryList.clear();
        valueList.clear();

        // Add the entries from the file to the lists
        try
        {
            JSONArray data = new JSONArray(text.toString());
            for (int i = 0; !data.isNull(i); i++)
            {
                JSONObject entry = data.getJSONObject(i);

                String name = entry.getString("name");
                String url = entry.getString("url");

                if ((name != null) && (url != null))
                {
                    entryList.add(name);
                    valueList.add(url);
                }
            }
        }

        // Show read error
        catch (Exception e)
        {
            showToast(R.string.read_error);
        }

        // Get preferences
        SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(this);

        // Get editor
        SharedPreferences.Editor editor = preferences.edit();

        // Get entries
        JSONArray entryArray = new JSONArray(entryList);
        JSONArray valueArray = new JSONArray(valueList);

        // Update preferences
        editor.putString(PREF_ENTRIES, entryArray.toString());
        editor.putString(PREF_VALUES, valueArray.toString());
        editor.apply();

        // Update display
        arrayAdapter = new
            ArrayAdapter<String>(this, android.R.layout
                                 .simple_list_item_activated_1,
                                 entryList);
        listView.setAdapter(arrayAdapter);

        showToast(R.string.data_restored, entryList.size());
    }

    // Import data
    void importData()
    {
        // Set the directory path
        File dir = new File(SHORTY_DIR);

        // Create the file
        File file = new File(dir, SHORTY_EXTRA);

        importDialog(R.string.inport, R.string.path_info, file.getPath(),
                     new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int id)
                {
                    switch (id)
                    {
                    case DialogInterface.BUTTON_POSITIVE:
                        EditText text =
                            (EditText) ((Dialog) dialog).findViewById(TEXT);
                        String path = text.getText().toString();
                        importFile(path);
                    }
                }
            });
    }

    // importDialog
    private void importDialog(int title, int message, String path,
                              DialogInterface.OnClickListener listener)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);

        // Add the buttons
        builder.setPositiveButton(R.string.ok, listener);
        builder.setNegativeButton(R.string.cancel, listener);

        // Create edit text
        Context context = builder.getContext();
        EditText text = new EditText(context);
        text.setId(TEXT);
        text.setText(path);

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.setView(text, 30, 0, 30, 0);
        dialog.getWindow()
            .setSoftInputMode(WindowManager
                              .LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        dialog.show();
    }

    // importFile
    protected void importFile(String path)
    {
        // Get entry list size
        int old = entryList.size();

        try
        {
            // Get the path to sdcard
            File sdcard = Environment.getExternalStorageDirectory();

            // Add the path
            File file = new File(sdcard, path);

            // Create a set of the current names
            Set<String> nameSet = new HashSet<String>(entryList);

            // Read the file
            CSVReader reader = new CSVReader(new FileReader(file));
            String nextLine[];
            while ((nextLine = reader.readNext()) != null)
            {
                String name = nextLine[0];
                String url = nextLine[1];;

                if ((name != null) && (url != null) &&
                        !nameSet.contains(name))
                {
                    entryList.add(name);
                    valueList.add(url);
                }
            }

            reader.close();
        }

        // Show read error
        catch (Exception e)
        {
            showToast(R.string.read_error);
            return;
        }

        // Get preferences
        SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(this);

        // Get editor
        SharedPreferences.Editor editor = preferences.edit();

        // Get entries
        JSONArray entryArray = new JSONArray(entryList);
        JSONArray valueArray = new JSONArray(valueList);

        // Update preferences
        editor.putString(PREF_ENTRIES, entryArray.toString());
        editor.putString(PREF_VALUES, valueArray.toString());
        editor.apply();

        // Update display
        arrayAdapter = new
            ArrayAdapter<String>(this, android.R.layout
                                 .simple_list_item_activated_1,
                                 entryList);
        listView.setAdapter(arrayAdapter);

        // Get entries imported
        int imported = entryList.size() - old;

        if (imported == 0)
            showToast(R.string.none_imported);

        else
            showToast(R.string.data_imported, imported);
    }

    // Show toast.
    void showToast(int id, Object... args)
    {
        // Get text from resources
        String text = getString(id);
        showToast(text, args);
    }

    // Show toast.
    void showToast(String format, Object... args)
    {
        String text = String.format(format, args);
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
