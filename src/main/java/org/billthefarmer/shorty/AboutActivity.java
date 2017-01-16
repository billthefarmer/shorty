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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AboutActivity extends Activity
    implements View.OnClickListener
{
    // On create

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        // Get text view
        TextView view = (TextView)findViewById(R.id.version);

        // Set version in text view
        if (view != null)
        {
            String v = (String) view.getText();
            String s = String.format(v, BuildConfig.VERSION_NAME);
            view.setText(s);
        }

        // Get button
        Button ok = (Button)findViewById(R.id.ok);
        ok.setOnClickListener(this);
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

        case R.id.ok:
            setResult(RESULT_CANCELED);
            finish();
            break;
        }
    }
}
