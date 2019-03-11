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
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;

// BroadcastActivity
public class BroadcastActivity extends Activity
{
    protected final static String AUDIO_WILD = "audio/*";
    protected final static String TITLE = "title";

    // On create
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Get the intent
        Intent intent = getIntent();

        // Get the url and name
        String url = intent.getStringExtra(MainActivity.URL);
        String name = intent.getStringExtra(MainActivity.NAME);
        String action = intent.getStringExtra(MainActivity.ACTION);
        String player = intent.getStringExtra(MainActivity.PLAYER);

        if (MainActivity.VLC.equals(player))
        {
            // Create an intent to play using VLC
            Intent play = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.parse(url);
            play.setPackage(player);
            play.setDataAndType(uri, AUDIO_WILD);
            play.putExtra(TITLE, name);
            startActivity(play);
        }

        else
        {
            // Create an intent to play using Intent Radio
            Intent broadcast = new Intent(action);

            // Put the url and name in the broadcast intent
            broadcast.putExtra(MainActivity.URL, url);
            broadcast.putExtra(MainActivity.NAME, name);

            sendBroadcast(broadcast);
        }

        setResult(RESULT_OK);
        finish();
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
