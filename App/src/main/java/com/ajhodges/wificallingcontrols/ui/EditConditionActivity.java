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
 * Created by Adam on 3/25/2014.
 */
public class EditConditionActivity extends AbstractPluginActivity {
    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        BundleScrubber.scrub(getIntent());

        final Bundle localeBundle = getIntent().getBundleExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE);
        BundleScrubber.scrub(localeBundle);

        setContentView(R.layout.edit_condition);

        if (null == savedInstanceState)
        {
            if (PluginBundleManager.isBundleValid(localeBundle))
            {

                final int mode = localeBundle.getInt(PluginBundleManager.BUNDLE_EXTRA_INT_MODE);

                switch (mode) {
                    case WifiCallingManager.MODE_OFF: {
                        RadioButton button = (RadioButton) findViewById(R.id.off_state);
                        button.setChecked(true);
                        break;
                    }
                    case WifiCallingManager.MODE_ON: {
                        RadioButton button = (RadioButton) findViewById(R.id.on_state);
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
            RadioGroup rg = (RadioGroup)findViewById(R.id.state_buttons);
            int mode = 0;
            switch (rg.getCheckedRadioButtonId()) {
                case R.id.off_state: {
                    mode = WifiCallingManager.MODE_OFF;
                    break;
                }
                case R.id.on_state: {
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
