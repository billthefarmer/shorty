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
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
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

import java.util.Locale;

// MainActivity
public class MainActivity extends Activity
    implements View.OnClickListener
{
    protected final static String PREF_URL = "pref_url";
    protected final static String PREF_NAME = "pref_name";
    protected final static String PREF_DARK = "pref_dark";

    protected final static String URL = "url";
    protected final static String NAME = "name";

    protected final static String PLAY = "org.smblott.intentradio.PLAY";
    protected final static String PAUSE = "org.smblott.intentradio.PAUSE";
    protected final static String RESTART = "org.smblott.intentradio.RESTART";
    protected final static String STOP = "org.smblott.intentradio.STOP";

    protected final static String BROADCAST =
        "org.billthefarmer.shorty.BROADCAST";
    protected final static String INSTALL_SHORTCUT =
        "com.android.launcher.action.INSTALL_SHORTCUT";

    protected final static int LOOKUP = 0;
    private final static int VERSION_M = 23;

    private RadioGroup group;
    private TextView nameView;
    private TextView urlView;

    private boolean dark = true;

    // On create
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Get preferences
        SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(this);

        dark = preferences.getBoolean(PREF_DARK, true);

        if (dark)
            setTheme(R.style.AppDarkTheme);

        setContentView(R.layout.main);

        // Get group and views
        group = findViewById(R.id.group);
        nameView = findViewById(R.id.name);
        urlView = findViewById(R.id.url);

        // Get buttons
        Button cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(this);

        Button create = findViewById(R.id.create);
        create.setOnClickListener(this);

        String name = preferences.getString(PREF_NAME, null);
        String url = preferences.getString(PREF_URL, null);

        // Set fields from preferences
        if (name != null)
            nameView.setText(name);
        if (url != null)
            urlView.setText(url);
    }

    // onPause
    @Override
    public void onPause()
    {
        super.onPause();

        SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean(PREF_DARK, dark);
        editor.apply();
    }

    // onCreateOptionsMenu
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it
        // is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    // onPrepareOptionsMenu
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menu.findItem(R.id.action_dark).setChecked(dark);

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

        // Dark
        case R.id.action_dark:
            return onDarkClick(item);

        default:
            return false;
        }
    }

    // onActivityResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data)
    {
        // Do nothing if cancelled
        if (resultCode != RESULT_OK)
            return;

        switch (requestCode)
        {
        case LOOKUP:

            String name = data.getStringExtra(NAME);
            String url = data.getStringExtra(URL);

            // Set fields from intent
            if (name != null)
                nameView.setText(name);
            if (url != null)
                urlView.setText(url);
            break;
        }
    }

    // On lookup click
    private boolean onLookupClick(MenuItem item)
    {
        Intent intent = new Intent(this, LookupActivity.class);
        startActivityForResult(intent, LOOKUP);

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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.about);

        String format = getString(R.string.version);
        String message =
            String.format(Locale.getDefault(),
                          format, BuildConfig.VERSION_NAME);
        builder.setMessage(message);

        // Add the button
        builder.setPositiveButton(R.string.ok, null);

        // Create the AlertDialog
        builder.show();

        return true;
    }

    // On dark click
    private boolean onDarkClick(MenuItem item)
    {
        dark = !dark;
        item.setChecked(dark);

        if (Build.VERSION.SDK_INT != VERSION_M)
            recreate();

        return true;
    }

    // On click
    @Override
    @SuppressWarnings("deprecation")
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
            catch (Exception e)
            {
            }

            if (icon == null)
            {
                showToast();
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
                    // Check the fields
                    if (name.length() == 0)
                        name = getString(R.string.default_name);
                    if (url.length() == 0)
                        url = getString(R.string.default_url);

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

                // Resume
                case R.id.resume:
                    name = "Resume";
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
    void showToast()
    {
        // Get text from resources
        String text = getString(R.string.not_installed);
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
