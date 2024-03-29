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
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// MainActivity
public class MainActivity extends Activity
    implements View.OnClickListener
{
    protected final static String PREF_URL = "pref_url";
    protected final static String PREF_NAME = "pref_name";
    protected final static String PREF_DARK = "pref_dark";
    protected final static String PREF_VLC = "pref_vlc";

    protected final static String URL = "url";
    protected final static String NAME = "name";
    protected final static String ACTION = "action";
    protected final static String PLAYER = "player";

    protected final static String PLAY = "org.smblott.intentradio.PLAY";
    protected final static String PAUSE = "org.smblott.intentradio.PAUSE";
    protected final static String RESTART = "org.smblott.intentradio.RESTART";
    protected final static String STOP = "org.smblott.intentradio.STOP";

    protected final static String INTENTRADIO = "org.smblott.intentradio";
    protected final static String VLC = "org.videolan.vlc";

    protected final static String BROADCAST =
        "org.billthefarmer.shorty.BROADCAST";
    protected final static String INSTALL_SHORTCUT =
        "com.android.launcher.action.INSTALL_SHORTCUT";

    protected final static int LOOKUP = 0;

    public static final int VERSION_CODE_S_V2 = 32;

    private RadioGroup group;
    private TextView nameView;
    private TextView urlView;

    private boolean dark = true;
    private boolean vlc = false;

    // On create
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Get preferences
        SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(this);

        dark = preferences.getBoolean(PREF_DARK, true);

        if (!dark)
            setTheme(R.style.AppTheme);

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

        vlc = preferences.getBoolean(PREF_VLC, false);
        if (vlc)
        {
            findViewById(R.id.stop).setEnabled(false);
            findViewById(R.id.pause).setEnabled(false);
            findViewById(R.id.resume).setEnabled(false);
        }

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
        editor.putBoolean(PREF_VLC, vlc);
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
        menu.findItem(R.id.action_vlc).setChecked(vlc);

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

        // Vlc
        case R.id.action_vlc:
            return onVlcClick(item);

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
        builder.setTitle(R.string.app_name);

        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        SpannableStringBuilder spannable =
            new SpannableStringBuilder(getText(R.string.version));
        Pattern pattern = Pattern.compile("%s");
        Matcher matcher = pattern.matcher(spannable);
        if (matcher.find())
            spannable.replace(matcher.start(), matcher.end(),
                              BuildConfig.VERSION_NAME);
        matcher.reset(spannable);
        if (matcher.find())
            spannable.replace(matcher.start(), matcher.end(),
                              dateFormat.format(BuildConfig.BUILT));
        builder.setMessage(spannable);

        // Add the button
        builder.setPositiveButton(R.string.ok, null);

        // Create the AlertDialog
        Dialog dialog = builder.show();

        // Set movement method
        TextView text = dialog.findViewById(android.R.id.message);
        if (text != null)
            text.setMovementMethod(LinkMovementMethod.getInstance());

        return true;
    }

    // On dark click
    private boolean onDarkClick(MenuItem item)
    {
        dark = !dark;
        item.setChecked(dark);

        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.M)
            recreate();

        return true;
    }

    // On vlc click
    private boolean onVlcClick(MenuItem item)
    {
        vlc = !vlc;
        item.setChecked(vlc);

        if (vlc)
        {
            findViewById(R.id.stop).setEnabled(false);
            findViewById(R.id.pause).setEnabled(false);
            findViewById(R.id.resume).setEnabled(false);
        }

        else
        {
            findViewById(R.id.stop).setEnabled(true);
            findViewById(R.id.pause).setEnabled(true);
            findViewById(R.id.resume).setEnabled(true);
        }

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

            // Get player icon
            BitmapDrawable icon = null;
            try
            {
                icon = (BitmapDrawable)
                       manager.getApplicationIcon(vlc ? VLC : INTENTRADIO);
            }

            catch (Exception e) {}

            if (icon == null)
            {
                showToast(vlc ? R.string.vlc_not_installed :
                          R.string.not_installed);
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
                    shortcut.putExtra(URL, url);
                    shortcut.putExtra(NAME, name);
                    shortcut.putExtra(ACTION, PLAY);
                    shortcut.putExtra(PLAYER, vlc ? VLC : INTENTRADIO);

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
                    shortcut.putExtra(ACTION, STOP);
                    break;

                // Pause
                case R.id.pause:
                    name = "Pause";
                    shortcut.putExtra(ACTION, PAUSE);
                    break;

                // Resume
                case R.id.resume:
                    name = "Resume";
                    shortcut.putExtra(ACTION, RESTART);
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
    private void showToast(int id)
    {
        // Get text from resources
        String text = getString(id);
        showToast(text);
    }

    // Show toast.
    private void showToast(String text)
    {
        // Make a new toast
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        // Fix for android 13
        View view = toast.getView();
        if (view != null && Build.VERSION.SDK_INT > VERSION_CODE_S_V2)
            view.setBackgroundResource(R.drawable.toast_frame);
        toast.show();
    }
}
