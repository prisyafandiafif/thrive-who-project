package com.alias.test.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Created by aliakbars on 11/20/13.
 */
public class App {

    public static SharedPreferences preferences;

    public static String host = "http://test.aliakbars.com";
    public static final String POST = "/post";
    public static final String GET = "/get";
    public static final String HOST_KEY = "com.alias.test.host";
    public static String url = App.host + App.GET;

    public static String get(View v) {
        if (v instanceof EditText) {
            return ((EditText) v).getText().toString();
        } else if (v instanceof Spinner) {
            return ((Spinner) v).getSelectedItem().toString();
        } else if (v instanceof TextView) {
            return ((TextView) v).getText().toString();
        } else if (v instanceof CheckBox) {
        	return ((CheckBox) v).isChecked() ? "true" : "false";
        } else if (v instanceof RadioGroup) {
            int selectedId = ((RadioGroup) v).getCheckedRadioButtonId();
            RadioButton radioButton = (RadioButton) v.findViewById(selectedId);
            return radioButton.getText().toString();
        } else {
            throw new UnsupportedOperationException("Getting data from " + v.toString() + " is not implemented.");
        }
    }

    public static void showServerSettings(Context c) {
        final EditText ipAddressInput = new EditText(c);
        ipAddressInput.setHint("http://192.168.0.1");
        ipAddressInput.setText(App.host);
        new AlertDialog.Builder(c)
                .setTitle("Server")
                .setMessage("IP Address:")
                .setView(ipAddressInput)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        App.host = App.get(ipAddressInput);
                        App.preferences.edit().putString(App.HOST_KEY, App.host).commit();
                        url = App.host + App.POST;
                    }
                })
                .setNegativeButton("Cancel", null).show();
    }
}
