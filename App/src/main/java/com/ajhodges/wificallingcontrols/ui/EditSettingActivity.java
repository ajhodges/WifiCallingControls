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
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.ajhodges.wificallingcontrols.R;
import com.ajhodges.wificallingcontrols.bundle.BundleScrubber;
import com.ajhodges.wificallingcontrols.bundle.PluginBundleManager;
import com.ajhodges.wificallingcontrols.ipphone.WifiCallingManager;

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

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        BundleScrubber.scrub(getIntent());

        final Bundle localeBundle = getIntent().getBundleExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE);
        BundleScrubber.scrub(localeBundle);

        setContentView(R.layout.edit_setting);

        if (null == savedInstanceState)
        {
            if (PluginBundleManager.isBundleValid(localeBundle))
            {

                final int mode = localeBundle.getInt(PluginBundleManager.BUNDLE_EXTRA_INT_MODE);

                switch (mode) {
                    case WifiCallingManager.MODE_TOGGLE: {
                        RadioButton button = (RadioButton) findViewById(R.id.toggle_mode);
                        button.setChecked(true);
                        break;
                    }
                    case WifiCallingManager.MODE_OFF: {
                        RadioButton button = (RadioButton) findViewById(R.id.off_mode);
                        button.setChecked(true);
                        break;
                    }
                    case WifiCallingManager.MODE_ON: {
                        RadioButton button = (RadioButton) findViewById(R.id.on_mode);
                        button.setChecked(true);
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
            RadioGroup rg = (RadioGroup)findViewById(R.id.mode_buttons);
            int mode = -1;
            switch (rg.getCheckedRadioButtonId()) {
                case R.id.toggle_mode: {
                    mode = WifiCallingManager.MODE_TOGGLE;
                    break;
                }
                case R.id.off_mode: {
                    mode = WifiCallingManager.MODE_OFF;
                    break;
                }
                case R.id.on_mode: {
                    mode = WifiCallingManager.MODE_ON;
                    break;
                }
            }


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
                    PluginBundleManager.generateBundle(getApplicationContext(), mode);
            resultIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE, resultBundle);

            /*
             * The blurb is concise status text to be displayed in the host's UI.
             */
            resultIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_STRING_BLURB, PluginBundleManager.generateBlurb(getApplicationContext(), mode));

            setResult(RESULT_OK, resultIntent);
        }

        super.finish();
    }
}