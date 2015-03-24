package com.afollestad.materialdialogssample.compat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.afollestad.materialdialogssample.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class PreferenceFragment extends Fragment implements
        PreferenceManagerCompat.OnPreferenceTreeClickListener {

    private static final String PREFERENCES_TAG = "android:preferences";

    private PreferenceManager mPreferenceManager;
    private ListView mList;
    private boolean mHavePrefs;
    private boolean mInitDone;

    /**
     * The starting request code given out to preference framework.
     */
    private static final int FIRST_REQUEST_CODE = 100;

    private static final int MSG_BIND_PREFERENCES = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MSG_BIND_PREFERENCES:
                    bindPreferences();
                    break;
            }
        }
    };

    final private Runnable mRequestFocus = new Runnable() {
        public void run() {
            mList.focusableViewAvailable(mList);
        }
    };

    /**
     * Interface that PreferenceFragment's containing activity should
     * implement to be able to process preference items that wish to
     * switch to a new fragment.
     */
    public interface OnPreferenceStartFragmentCallback {
        /**
         * Called when the user has clicked on a Preference that has
         * a fragment class name associated with it.  The implementation
         * to should instantiate and switch to an instance of the given
         * fragment.
         */
        boolean onPreferenceStartFragment(PreferenceFragment caller, Preference pref);
    }

    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        mPreferenceManager = PreferenceManagerCompat.newInstance(getActivity(), FIRST_REQUEST_CODE);
        PreferenceManagerCompat.setFragment(mPreferenceManager, this);
    }

    @Override
    public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup,
                             Bundle paramBundle) {
        return paramLayoutInflater.inflate(R.layout.preference_list_fragment, paramViewGroup,
                false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mHavePrefs) {
            bindPreferences();
        }

        mInitDone = true;

        if (savedInstanceState != null) {
            Bundle container = savedInstanceState.getBundle(PREFERENCES_TAG);
            if (container != null) {
                final PreferenceScreen preferenceScreen = getPreferenceScreen();
                if (preferenceScreen != null) {
                    preferenceScreen.restoreHierarchyState(container);
                }
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        PreferenceManagerCompat.setOnPreferenceTreeClickListener(mPreferenceManager, this);
    }

    @Override
    public void onStop() {
        super.onStop();
        PreferenceManagerCompat.dispatchActivityStop(mPreferenceManager);
        PreferenceManagerCompat.setOnPreferenceTreeClickListener(mPreferenceManager, null);
    }

    @Override
    public void onDestroyView() {
        mList = null;
        mHandler.removeCallbacks(mRequestFocus);
        mHandler.removeMessages(MSG_BIND_PREFERENCES);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PreferenceManagerCompat.dispatchActivityDestroy(mPreferenceManager);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        final PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (preferenceScreen != null) {
            Bundle container = new Bundle();
            preferenceScreen.saveHierarchyState(container);
            outState.putBundle(PREFERENCES_TAG, container);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        PreferenceManagerCompat.dispatchActivityResult(mPreferenceManager, requestCode, resultCode, data);
    }

    /**
     * Returns the {@link PreferenceManager} used by this fragment.
     * @return The {@link PreferenceManager}.
     */
    public PreferenceManager getPreferenceManager() {
        return mPreferenceManager;
    }

    /**
     * Sets the root of the preference hierarchy that this fragment is showing.
     *
     * @param preferenceScreen The root {@link PreferenceScreen} of the preference hierarchy.
     */
    public void setPreferenceScreen(PreferenceScreen preferenceScreen) {
        if (PreferenceManagerCompat.setPreferences(mPreferenceManager, preferenceScreen) && preferenceScreen != null) {
            mHavePrefs = true;
            if (mInitDone) {
                postBindPreferences();
            }
        }
    }

    /**
     * Gets the root of the preference hierarchy that this fragment is showing.
     *
     * @return The {@link PreferenceScreen} that is the root of the preference
     *         hierarchy.
     */
    public PreferenceScreen getPreferenceScreen() {
        return PreferenceManagerCompat.getPreferenceScreen(mPreferenceManager);
    }

    /**
     * Adds preferences from activities that match the given {@link Intent}.
     *
     * @param intent The {@link Intent} to query activities.
     */
    public void addPreferencesFromIntent(Intent intent) {
        requirePreferenceManager();

        setPreferenceScreen(PreferenceManagerCompat.inflateFromIntent(mPreferenceManager, intent, getPreferenceScreen()));
    }

    /**
     * Inflates the given XML resource and adds the preference hierarchy to the current
     * preference hierarchy.
     *
     * @param preferencesResId The XML resource ID to inflate.
     */
    public void addPreferencesFromResource(int preferencesResId) {
        requirePreferenceManager();

        setPreferenceScreen(PreferenceManagerCompat.inflateFromResource(mPreferenceManager, getActivity(),
                preferencesResId, getPreferenceScreen()));
    }

    /**
     * {@inheritDoc}
     */
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
                                         Preference preference) {
        //if (preference.getFragment() != null &&
        if (
                getActivity() instanceof OnPreferenceStartFragmentCallback) {
            return ((OnPreferenceStartFragmentCallback)getActivity()).onPreferenceStartFragment(
                    this, preference);
        }
        return false;
    }

    /**
     * Finds a {@link Preference} based on its key.
     *
     * @param key The key of the preference to retrieve.
     * @return The {@link Preference} with the key, or null.
     * @see PreferenceGroup#findPreference(CharSequence)
     */
    public Preference findPreference(CharSequence key) {
        if (mPreferenceManager == null) {
            return null;
        }
        return mPreferenceManager.findPreference(key);
    }

    private void requirePreferenceManager() {
        if (mPreferenceManager == null) {
            throw new RuntimeException("This should be called after super.onCreate.");
        }
    }

    private void postBindPreferences() {
        if (mHandler.hasMessages(MSG_BIND_PREFERENCES)) return;
        mHandler.obtainMessage(MSG_BIND_PREFERENCES).sendToTarget();
    }

    private void bindPreferences() {
        final PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (preferenceScreen != null) {
            preferenceScreen.bind(getListView());
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            // Workaround android bug for SDK 10 and below - see
            // https://github.com/android/platform_frameworks_base/commit/2d43d283fc0f22b08f43c6db4da71031168e7f59
            getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // If the list has headers, subtract them from the index.
                    if (parent instanceof ListView) {
                        position -= ((ListView)parent).getHeaderViewsCount();
                    }

                    Object item = preferenceScreen.getRootAdapter().getItem(position);
                    if (!(item instanceof Preference))
                        return;

                    final Preference preference = (Preference)item;
                    try {
                        Method performClick = Preference.class.getDeclaredMethod(
                                "performClick", PreferenceScreen.class);
                        performClick.setAccessible(true);
                        performClick.invoke(preference, preferenceScreen);
                    } catch (InvocationTargetException e) {
                    } catch (IllegalAccessException e) {
                    } catch (NoSuchMethodException e) {
                    }
                }
            });
        }
    }

    public ListView getListView() {
        ensureList();
        return mList;
    }

    private void ensureList() {
        if (mList != null) {
            return;
        }
        View root = getView();
        if (root == null) {
            throw new IllegalStateException("Content view not yet created");
        }
        View rawListView = root.findViewById(android.R.id.list);
        if (!(rawListView instanceof ListView)) {
            throw new RuntimeException(
                    "Content has view with id attribute 'android.R.id.list' "
                            + "that is not a ListView class");
        }
        mList = (ListView)rawListView;
        if (mList == null) {
            throw new RuntimeException(
                    "Your content must have a ListView whose id attribute is " +
                            "'android.R.id.list'");
        }
        mList.setOnKeyListener(mListOnKeyListener);
        mHandler.post(mRequestFocus);
    }

    private OnKeyListener mListOnKeyListener = new OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            Object selectedItem = mList.getSelectedItem();
            if (selectedItem instanceof Preference) {
                @SuppressWarnings("unused")
                View selectedView = mList.getSelectedView();
                //return ((Preference)selectedItem).onKey(
                //        selectedView, keyCode, event);
                return false;
            }
            return false;
        }

    };


}