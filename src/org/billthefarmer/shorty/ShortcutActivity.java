package org.billthefarmer.shorty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class ShortcutActivity extends Activity
{
    /** Called when the activity is first created. */
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
 
        // The meat of our shortcut
        Intent shortcut = new Intent("org.billthefarmer.shorty.BROADCAST");
	shortcut.putExtra("url", "http://www.listenlive.eu/bbcradio4.m3u");
        shortcut.putExtra("name", "BBC Radio 4");

        Intent.ShortcutIconResource icon = Intent.ShortcutIconResource
	    .fromContext(this, android.R.drawable.ic_media_play);
         
        // The result we are passing back from this activity
        Intent intent = new Intent();
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcut);
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "BBC Radio 4");
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        setResult(RESULT_OK, intent);
         
        finish();
    }
}
