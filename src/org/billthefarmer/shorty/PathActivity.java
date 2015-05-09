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
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PathActivity extends Activity
    implements View.OnClickListener
{
    private TextView textView;

    // On create

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.path);

	textView = (TextView)findViewById(R.id.path);

	Intent intent = getIntent();
	String path = intent.getStringExtra(LookupActivity.PATH);

	if (textView != null)
	    textView.setText(path);

	Button cancel = (Button)findViewById(R.id.path_cancel);
	if (cancel != null)
	    cancel.setOnClickListener(this);

	Button ok = (Button)findViewById(R.id.ok);
	if (ok != null)
	    ok.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
	// Get id

	int id = v.getId();
	switch (id)
	{
	    // Cancel

	case R.id.path_cancel:
	    setResult(RESULT_CANCELED);
	    finish();
	    break;

	    // OK

	case R.id.ok:
	    Intent intent = new Intent();
	    intent.putExtra(LookupActivity.PATH,
			    textView.getText().toString());
	    setResult(RESULT_OK, intent);
	    finish();
	    break;
	}
    }
}
