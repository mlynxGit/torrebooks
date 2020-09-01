package com.maghribpress.torrebook.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;

import com.codekidlabs.storagechooser.StorageChooser;
import com.maghribpress.torrebook.R;

public class SettingsFragement extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener{
    SharedPreferences sharedPreferences;
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.app_preferences);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        String key = preference.getKey();
        if(key.equals("save_directory_preference")){
            StorageChooser.Theme theme = new StorageChooser.Theme(getContext());
            theme.setScheme(getResources().getIntArray(R.array.paranoid_theme));
            StorageChooser chooser = new StorageChooser.Builder()
                    .withActivity(getActivity())
                    .allowCustomPath(true)
                    .setType(StorageChooser.DIRECTORY_CHOOSER)
                    .withFragmentManager(getActivity().getFragmentManager())
                    .withMemoryBar(true)
                    .setTheme(theme)
                    .build();

            // Show dialog whenever you want by
            chooser.show();

            // get path that the user has chosen
            chooser.setOnSelectListener(new StorageChooser.OnSelectListener() {
                @Override
                public void onSelect(String path) {
                    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    Preference directoryPicker = (Preference) findPreference("save_directory_preference");
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("save_directory_preference",path);
                    editor.apply();
                    directoryPicker.setSummary(path);
                }
            });
            return true;
        }
        return false;
    }


    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
        Preference directoryPicker = (Preference) findPreference("save_directory_preference");
        directoryPicker.setSummary(sharedPreferences.getString("save_directory_preference",Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if(key.equals("save_directory_preference")) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("save_directory_preference",sharedPreferences.getString(key,""));
            editor.apply();
            preference.setSummary(sharedPreferences.getString("save_directory_preference",""));
        }
    }
}
