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
 * Created by Adam on 3/25/2014.
 */
public class EditConditionActivity extends AbstractPluginActivity {
    static TypedArray modeNames;
    static TypedArray modeVals;
    ArrayList<String> optionNames = new ArrayList<String>();
    ArrayAdapter<String> options;
    static int selectedMode = 0;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        BundleScrubber.scrub(getIntent());

        final Bundle localeBundle = getIntent().getBundleExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE);
        BundleScrubber.scrub(localeBundle);

        setContentView(R.layout.edit_view);

        modeNames = getResources().obtainTypedArray(R.array.condition_modes);
        modeVals = getResources().obtainTypedArray(R.array.condition_modes_vals);

        for(int i=0 ; i<modeNames.length() ; i++){
            optionNames.add(i, modeNames.getString(i));
        }
        options = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, optionNames);
        setListAdapter(options);

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selected = options.getItem(position);
                selectedMode = modeVals.getInt(optionNames.indexOf(selected), 0);
            }
        });

        if (null == savedInstanceState)
        {
            if (PluginBundleManager.isBundleValid(localeBundle))
            {

                final int mode = localeBundle.getInt(PluginBundleManager.BUNDLE_EXTRA_INT_MODE);

                switch (mode) {
                    case WifiCallingManager.MODE_OFF: {
                        getListView().setItemChecked(options.getPosition(getString(R.string.off_mode)), true);
                        break;
                    }
                    case WifiCallingManager.MODE_ON: {
                        getListView().setItemChecked(options.getPosition(getString(R.string.on_mode)), true);
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
