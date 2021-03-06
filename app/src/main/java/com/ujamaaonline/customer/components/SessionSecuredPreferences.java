package com.ujamaaonline.customer.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.utils.StringUtil;

import java.util.Map;
import java.util.Set;

public class SessionSecuredPreferences implements SharedPreferences {

    private SharedPreferences sharedPref;
    protected Context context;

    public SessionSecuredPreferences(Context context, SharedPreferences delegate) {
        this.sharedPref = delegate;
        this.context = context;
    }

    public SessionSecuredPreferences(Context context) {
        this.sharedPref = context.getSharedPreferences(StringUtil.getStringForID(R.string.app_name), Context.MODE_PRIVATE);
        this.context = context;
    }

    @Override
    public Map<String, ?> getAll() {
        return this.sharedPref.getAll();
    }

    public Editor edit() {
        return new Editor();
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return this.sharedPref.getBoolean(key, defValue);
    }

    @Override
    public float getFloat(String key, float defValue) {
        return this.sharedPref.getFloat(key, defValue);
    }

    @Override
    public int getInt(String key, int defValue) {
        return this.sharedPref.getInt(key, defValue);
    }

    @Override
    public long getLong(String key, long defValue) {
        return this.sharedPref.getLong(key, defValue);
    }

    @Override
    public String getString(String key, String defValue) {
        return this.sharedPref.getString(key, defValue);
    }

    @Override
    public Set<String> getStringSet(String key, Set<String> defValues) {
        return this.sharedPref.getStringSet(key, defValues);
    }

    @Override
    public boolean contains(String s) {
        return this.sharedPref.contains(s);
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        this.sharedPref.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        this.sharedPref.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    public class Editor implements SharedPreferences.Editor {

        protected SharedPreferences.Editor editor;

        @SuppressLint("CommitPrefEdits")
        public Editor() {
            this.editor = SessionSecuredPreferences.this.sharedPref.edit();
        }

        @Override
        public Editor putBoolean(String key, boolean value) {
            this.editor.putBoolean(key, value);
            return this;
        }

        @Override
        public Editor putFloat(String key, float value) {
            this.editor.putFloat(key, value);
            return this;
        }

        @Override
        public Editor putInt(String key, int value) {
            this.editor.putInt(key, value);
            return this;
        }

        @Override
        public Editor putLong(String key, long value) {
            this.editor.putLong(key, value);
            return this;
        }

        @Override
        public Editor putString(String key, String value) {
            this.editor.putString(key, value);
            return this;
        }

        @Override
        public Editor putStringSet(String key, Set<String> values) {
            this.editor.putStringSet(key, values);
            return this;
        }

        @Override
        public void apply() {
            this.editor.apply();
        }

        @Override
        public Editor clear() {
            this.editor.clear();
            return this;
        }

        @Override
        public boolean commit() {
            return this.editor.commit();
        }

        @Override
        public Editor remove(String s) {
            this.editor.remove(s);
            return this;
        }

    }

}
