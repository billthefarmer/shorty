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
import android.content.SharedPreferences;
import android.os.Bundle;
import java.util.Set;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class LookupActivity extends Activity
    implements AdapterView.OnItemClickListener, View.OnClickListener
{
    private final static String PREF_ENTRIES = "pref_entries";
    private final static String PREF_VALUES = "pref_values";

    private TextView nameView;
    private TextView urlView;
    private ListView listView;

    private ArrayAdapter arrayAdapt;

    private String entries[];
    private String values[];

    // On create

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lookup);

	// Get text views
	nameView = (TextView)findViewById(R.id.lu_name);
	urlView = (TextView)findViewById(R.id.lu_url);

	// Get buttons
	Button cancel = (Button)findViewById(R.id.lu_cancel);
	cancel.setOnClickListener(this);

	Button add = (Button)findViewById(R.id.lu_add);
	add.setOnClickListener(this);

	Button delete = (Button)findViewById(R.id.lu_delete);
	delete.setOnClickListener(this);

	Button select = (Button)findViewById(R.id.lu_select);
	select.setOnClickListener(this);

	// get list view and adapter
	listView = (ListView)findViewById(R.id.list);
	arrayAdapt = (ArrayAdapter)listView.getAdapter();

	listView.setOnItemClickListener(this);

	// Get preferences
	SharedPreferences preferences = getPreferences(MODE_PRIVATE);

	Set<String> entriesSet = preferences.getStringSet(PREF_ENTRIES, null);
	Set<String> valuesSet = preferences.getStringSet(PREF_VALUES, null);

	if (entriesSet != null)
	    entries = (String[])entriesSet.toArray();

	if (valuesSet != null)
	    entries = (String[])valuesSet.toArray();
    }

    // On item click

    @Override
    public void onItemClick(AdapterView parent, View view,
			    int position, long id)
    {
	nameView.setText(((TextView)view).getText());
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

	case R.id.lu_cancel:
	    setResult(RESULT_CANCELED);
	    finish();
	    break;

	    // Add

	case R.id.lu_add:
	    break;

	    // Delete

	case R.id.lu_delete:
	    break;

	    // Select

	case R.id.lu_select:
	    break;
	}
    }
}
