/*
 * Copyright 2013 two forty four a.m. LLC <http://www.twofortyfouram.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * <http://www.apache.org/licenses/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.ajhodges.wificallingcontrols.ui;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.ajhodges.wificallingcontrols.R;
import com.ajhodges.wificallingcontrols.bundle.BundleScrubber;
import com.ajhodges.wificallingcontrols.bundle.PluginBundleManager;
import com.ajhodges.wificallingcontrols.ipphone.WifiCallingManager;

import java.util.ArrayList;

/**
 * This is the "Edit" activity for a Locale Plug-in.
 * <p>
 * This Activity can be started in one of two states:
 * <ul>
 * <li>New plug-in instance: The Activity's Intent will not contain
 * {@link com.twofortyfouram.locale.Intent#EXTRA_BUNDLE}.</li>
 * <li>Old plug-in instance: The Activity's Intent will contain
 * {@link com.twofortyfouram.locale.Intent#EXTRA_BUNDLE} from a previously saved plug-in instance that the
 * user is editing.</li>
 * </ul>
 *
 * @see com.twofortyfouram.locale.Intent#ACTION_EDIT_SETTING
 * @see com.twofortyfouram.locale.Intent#EXTRA_BUNDLE
 */
public final class EditSettingActivity extends AbstractPluginActivity
{
    static TypedArray stateModeNames;
    static TypedArray stateModeVals;

    ArrayList<String> optionNames = new ArrayList<String>();
    ArrayAdapter<String> options;
    static int selectedMode = -1;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        BundleScrubber.scrub(getIntent());

        final Bundle localeBundle = getIntent().getBundleExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE);
        BundleScrubber.scrub(localeBundle);

        setContentView(R.layout.edit_view);

        stateModeNames = getResources().obtainTypedArray(R.array.select_modes);
        stateModeVals = getResources().obtainTypedArray(R.array.select_modes_vals);

        for(int i=0 ; i< stateModeNames.length() ; i++){
            optionNames.add(i, stateModeNames.getString(i));
        }



        options = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, optionNames);
        setListAdapter(options);

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selected = options.getItem(position);

                selectedMode = stateModeVals.getInt(optionNames.indexOf(selected),0);
            }
        });

        if (null == savedInstanceState)
        {
            if (PluginBundleManager.isBundleValid(localeBundle))
            {

                final int mode = localeBundle.getInt(PluginBundleManager.BUNDLE_EXTRA_INT_MODE);

                switch (mode) {
                    case WifiCallingManager.MODE_TOGGLE: {
                        getListView().setItemChecked(options.getPosition(getString(R.string.toggle_mode)), true);
                        break;
                    }
                    case WifiCallingManager.MODE_OFF: {
                        getListView().setItemChecked(options.getPosition(getString(R.string.off_mode)), true);
                        break;
                    }
                    case WifiCallingManager.MODE_ON: {
                        getListView().setItemChecked(options.getPosition(getString(R.string.on_mode)), true);
                        break;
                    }
                    case WifiCallingManager.PREFER_WIFI: {
                        getListView().setItemChecked(options.getPosition(getString(R.string.wifi_preferred_mode)), true);
                        break;
                    }
                    case WifiCallingManager.PREFER_CELL: {
                        getListView().setItemChecked(options.getPosition(getString(R.string.cell_preferred_mode)), true);
                        break;
                    }
                    case WifiCallingManager.PREFER_NEVER_CELL: {
                        getListView().setItemChecked(options.getPosition(getString(R.string.never_cell_mode)), true);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void finish()
    {
        if (!isCanceled())
        {
            final Intent resultIntent = new Intent();

            /*
             * This extra is the data to ourselves: either for the Activity or the BroadcastReceiver. Note
             * that anything placed in this Bundle must be available to Locale's class loader. So storing
             * String, int, and other standard objects will work just fine. Parcelable objects are not
             * acceptable, unless they also implement Serializable. Serializable objects must be standard
             * Android platform objects (A Serializable class private to this plug-in's APK cannot be
             * stored in the Bundle, as Locale's classloader will not recognize it).
             */
            final Bundle resultBundle =
                    PluginBundleManager.generateBundle(getApplicationContext(), selectedMode);
            resultIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE, resultBundle);

            /*
             * The blurb is concise status text to be displayed in the host's UI.
             */
            resultIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_STRING_BLURB, PluginBundleManager.generateBlurb(getApplicationContext(), selectedMode));

            setResult(RESULT_OK, resultIntent);
        }

        super.finish();
    }
}