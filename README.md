# ![Logo](src/main/res/drawable-hdpi/ic_launcher.png) Shorty [![Build Status](https://travis-ci.org/billthefarmer/shorty.svg?branch=master)](https://travis-ci.org/billthefarmer/shorty) [![Available on F-Droid](https://f-droid.org/wiki/images/c/ca/F-Droid-button_available-on_smaller.png)](https://f-droid.org/packages/org.billthefarmer.shorty)

Create shortcuts for [Intent Radio](http://smblott.org/intent_radio).
The app is available on [F-Droid](https://f-droid.org/packages/org.billthefarmer.shorty)
and [here](https://github.com/billthefarmer/shorty/releases).

![](https://github.com/billthefarmer/billthefarmer.github.io/raw/master/images/Shorty.png) ![](https://github.com/billthefarmer/billthefarmer.github.io/raw/master/images/Shorty-dialog.png)

![](https://github.com/billthefarmer/billthefarmer.github.io/raw/master/images/Shorty-lookup.png) ![](https://github.com/billthefarmer/billthefarmer.github.io/raw/master/images/Shorty-help.png)

 * Create shortcuts from the app or using the android launcher from
 the desktop.
 * Defaults to BBC Radio 4 if fields are left empty.
 * Lookup stations from a list.
 * Field values and list remembered between uses.
 * Save and restore the list from the device file system.

## Using

Enter the station name and the url of the station in the fields and
touch the **Create** button. Touch the **Cancel** button to cancel. A
shortcut will be generated, either where you dragged the Shorty
shortcut, or in the next vacant slot on the desktop. It will be
labelled with name of the station.

Touch the shortcut to run Shorty in the background, which will send a
request to [Intent Radio](http://smblott.org/intent_radio) to play the
station you requested. Stop, Pause and Resume shortcuts may also be
created.

### Lookup

Touch the **Lookup** icon in the toolbar to show the lookup
display. Touch an item in the list to fill the fields, and the
**Select** button to return to the shortcut display. Use the **Add**
and **Remove** buttons to add and remove entries in the list.

### Search

Touch the **Search** icon in the toolbar to start an interactive
search of the station list. Touch the **Search** icon in the keyboard
to choose the first item in the list or touch an item in the list.

### Play

The **Play**, **Stop**, **Pause** and **Resume** items in the toolbar
will play a station in Intent Radio directly.

### Save and Restore

The **Save** and **Restore** items in the menu will save and restore
the current station list. The list location is
*Shorty/entries.json*. This folder is accessible from a file manager
or a connected PC. The list is in [JSON](https://json.org) format.

### Import

The **Import** item prompts for a file, default *extras.csv* in the
same location. You may change the name and/or location. This file
should be in
[CSV](https://en.wikipedia.org/wiki/Comma-separated_values) format,
which may be saved from a spreadsheet. If found the entries will be
added. The function will not add duplicate entries that are already in
the list, but will add duplicates in the file itself.

The shortcuts use Shorty in the background, so it will no longer work if
Shorty is uninstalled. Shorty will not create shortcuts if Intent
Radio is not installed.
