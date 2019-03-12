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

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
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

import com.opencsv.CSVReader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    private final static int REQUEST_SAVE   = 1;
    private final static int REQUEST_READ   = 2;
    private final static int REQUEST_IMPORT = 3;

    private MenuItem searchItem;
    private TextView nameView;
    private TextView urlView;
    private ListView listView;

    private ArrayAdapter<String> arrayAdapter;

    private List<String> entryList;
    private List<String> valueList;

    private boolean vlc;

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
        nameView = findViewById(R.id.lu_name);
        urlView = findViewById(R.id.lu_url);

        // Get buttons, set listener
        Button add = findViewById(R.id.add);
        if (add != null)
            add.setOnClickListener(this);

        Button remove = findViewById(R.id.remove);
        if (remove != null)
            remove.setOnClickListener(this);

        Button select = findViewById(R.id.select);
        if (select != null)
            select.setOnClickListener(this);

        // get list view
        listView = findViewById(R.id.list);

        if (listView != null)
            listView.setOnItemClickListener(this);

        // Enable back navigation on action bar
        ActionBar actionBar = getActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        vlc = preferences.getBoolean(MainActivity.PREF_VLC, false);

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
                entryList = new ArrayList<>();
                for (int i = 0; !entryArray.isNull(i); i++)
                    entryList.add(entryArray.getString(i));
            }

            catch (Exception e)
            {
                String entries[] = resources.getStringArray(R.array.entries);
                entryList =
                    new ArrayList<>(Arrays.asList(entries));
            }
        }

        else
        {
            String entries[] = resources.getStringArray(R.array.entries);
            entryList =
                new ArrayList<>(Arrays.asList(entries));
        }

        if (valueJSON != null)
        {
            try
            {
                JSONArray valueArray = new JSONArray(valueJSON);
                valueList = new ArrayList<>();
                for (int i = 0; !valueArray.isNull(i); i++)
                    valueList.add(valueArray.getString(i));
            }

            catch (Exception e)
            {
                String values[] = resources.getStringArray(R.array.values);
                valueList =
                    new ArrayList<>(Arrays.asList(values));
            }
        }

        else
        {
            String values[] = resources.getStringArray(R.array.values);
            valueList =
                new ArrayList<>(Arrays.asList(values));
        }

        // Set array adapter
        arrayAdapter =
            new ArrayAdapter<>(this, android.R.layout
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
        SearchView searchView = (SearchView) searchItem.getActionView();

        if (searchView != null)
        {
            searchView.setQueryHint(getText(R.string.hint));
            searchView.setOnQueryTextListener(this);
        }

        return true;
    }

    // onPrepareOptionsMenu
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menu.findItem(R.id.action_stop).setVisible(!vlc);
        menu.findItem(R.id.action_pause).setVisible(!vlc);
        menu.findItem(R.id.action_resume).setVisible(!vlc);

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
            setResult(RESULT_CANCELED);
            finish();
            break;

        // Play
        case R.id.action_play:
            // Get the name and url
            String name = nameView.getText().toString();
            String url = urlView.getText().toString();

            // Check the fields
            if (name.length() == 0)
                name = getString(R.string.default_name);
            if (url.length() == 0)
                url = getString(R.string.default_url);

            if (vlc)
            {
                // Create an intent to play using VLC
                Intent play = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.parse(url);
                play.setPackage(MainActivity.VLC);
                play.setDataAndType(uri, BroadcastActivity.AUDIO_WILD);
                play.putExtra(BroadcastActivity.TITLE, name);

                try
                {
                    startActivity(play);
                }

                catch (Exception e)
                {
                    showToast(R.string.vlc_not_installed);
                }
            }

            else
            {
                // Create an intent to play using Intent Radio
                broadcast = new Intent(MainActivity.PLAY);

                // Put the name and url in the broadcast intent
                broadcast.putExtra(MainActivity.NAME, name);
                broadcast.putExtra(MainActivity.URL, url);

                // Send it
                sendBroadcast(broadcast);
            }
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
    public boolean onQueryTextChange(String newText)
    {
        arrayAdapter.getFilter().filter(newText);
        return true;
    }

    // onQueryTextSubmit
    @Override
    public boolean onQueryTextSubmit(String query)
    {
        String item = listView.getItemAtPosition(0).toString();
        nameView.setText(item);
        int index = entryList.indexOf(item);
        urlView.setText(valueList.get(index));
        return true;
    }

    // On item click
    @Override
    public void onItemClick(AdapterView parent, View view,
                            int position, long id)
    {
        String item = parent.getItemAtPosition(position).toString();

        nameView.setText(item);
        int index = entryList.indexOf(item);
        urlView.setText(valueList.get(index));
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

            // Collapse search view
            if (searchItem.isActionViewExpanded())
                searchItem.collapseActionView();

            // Update display
            arrayAdapter = new
            ArrayAdapter<>(this, android.R.layout
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
            int index = listView.getCheckedItemPosition();
            String item = listView.getItemAtPosition(index).toString();
            if (entryList.contains(item))
            {
                index = entryList.indexOf(item);
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

            // Collapse search view
            if (searchItem.isActionViewExpanded())
                searchItem.collapseActionView();

            // Update display
            arrayAdapter = new
            ArrayAdapter<>(this, android.R.layout
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

    // onRequestPermissionsResult
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults)
    {
        switch (requestCode)
        {
        case REQUEST_SAVE:
            for (int i = 0; i < grantResults.length; i++)
                if (permissions[i].equals(Manifest.permission
                                          .WRITE_EXTERNAL_STORAGE) &&
                    grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    // Granted, save data
                    saveData();
            break;

        case REQUEST_READ:
            for (int i = 0; i < grantResults.length; i++)
                if (permissions[i].equals(Manifest.permission
                                          .READ_EXTERNAL_STORAGE) &&
                    grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    // Granted, restore data
                    restoreData();
            break;

        case REQUEST_IMPORT:
            for (int i = 0; i < grantResults.length; i++)
                if (permissions[i].equals(Manifest.permission
                                          .READ_EXTERNAL_STORAGE) &&
                    grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    // Granted, restore data
                    importData();
            break;
        }
    }

    // Save data
    private void saveData()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]
                    {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                     Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_SAVE);
                return;
            }
        }

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
                entry.put(MainActivity.URL, valueList.get(i));
                entry.put(MainActivity.NAME, name);
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
    private void restoreData()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]
                    {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                     Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ);
                return;
            }
        }

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

        // Collapse search view
        if (searchItem.isActionViewExpanded())
            searchItem.collapseActionView();

        // Update display
        arrayAdapter = new
        ArrayAdapter<>(this, android.R.layout
                       .simple_list_item_activated_1,
                       entryList);
        listView.setAdapter(arrayAdapter);

        showToast(R.string.data_restored, entryList.size());
    }

    // Import data
    private void importData()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]
                    {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                     Manifest.permission.READ_EXTERNAL_STORAGE},
                                   REQUEST_IMPORT);
                return;
            }
        }

        // Set the directory path
        File dir = new File(SHORTY_DIR);

        // Create the file
        File file = new File(dir, SHORTY_EXTRA);

        importDialog(file.getPath(), (dialog, id) ->
        {
            switch (id)
            {
            case DialogInterface.BUTTON_POSITIVE:
                EditText text =
                ((Dialog) dialog).findViewById(R.id.path_text);
                String path = text.getText().toString();
                importFile(path);
            }
        });
    }

    // importDialog
    private void importDialog(String path,
                              DialogInterface.OnClickListener listener)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.inport);
        builder.setMessage(R.string.path_info);

        // Add the buttons
        builder.setPositiveButton(R.string.ok, listener);
        builder.setNegativeButton(R.string.cancel, listener);

        // Create edit text
        Context context = builder.getContext();
        EditText text = new EditText(context);
        text.setId(R.id.path_text);
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
    private void importFile(String path)
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
            Set<String> nameSet = new HashSet<>(entryList);

            // Read the file
            CSVReader reader = new CSVReader(new FileReader(file));
            String nextLine[];
            while ((nextLine = reader.readNext()) != null)
            {
                String name = nextLine[0];
                String url = nextLine[1];

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

        // Collapse search view
        if (searchItem.isActionViewExpanded())
            searchItem.collapseActionView();

        // Update display
        arrayAdapter = new
        ArrayAdapter<>(this, android.R.layout
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
    private void showToast(int id, Object... args)
    {
        // Get text from resources
        String text = getString(id);
        showToast(text, args);
    }

    // Show toast.
    private void showToast(String format, Object... args)
    {
        String text = String.format(format, args);
        showToast(text);
    }

    // Show toast.
    private void showToast(String text)
    {
        // Make a new toast
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
