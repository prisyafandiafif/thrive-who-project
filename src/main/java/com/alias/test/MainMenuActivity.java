package com.alias.test;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.alias.test.util.App;
import com.alias.test.util.DatabaseHandler;
import com.alias.test.util.Request;
import com.alias.test.util.Result;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

public class MainMenuActivity extends Activity {

    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        App.preferences = this.getSharedPreferences("com.alias.test", Context.MODE_PRIVATE);
        App.host = App.preferences.getString(App.HOST_KEY, App.host);

        db = new DatabaseHandler(MainMenuActivity.this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            App.showServerSettings(MainMenuActivity.this);
            return true;
        } else if (id == R.id.action_update) {
            new AlertDialog.Builder(MainMenuActivity.this).setTitle(R.string.warning).setMessage(R.string.update_prompt)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            new UpdateTask().execute();
                        }
                    }).setNegativeButton(R.string.no, null).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void testButtonClicked(View v) {
        Intent i = new Intent(this, QuestionActivity.class);
//        i.putExtra("activity", "list");
        startActivity(i);
    }

    public void aboutButtonClicked(View v) {
        Toast.makeText(getApplicationContext(), "Created by Ali Akbar S.", Toast.LENGTH_LONG).show();
    }

    private class UpdateTask extends AsyncTask<List<NameValuePair>, String, Result> {

        private ProgressDialog loadingDialog = new ProgressDialog(MainMenuActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog.setMessage(getResources().getString(R.string.fetching_questions));
            loadingDialog.show();
        }

        @Override
        protected Result doInBackground(List<NameValuePair>... params) {
            try {
                publishProgress(getResources().getString(R.string.updating_database));
                return Request.get(App.host + App.GET);
            } catch (Exception e) {
                return new Result("Error: ", e.getMessage());
            }
        }

        @Override
        protected void onCancelled() {
            if (loadingDialog.isShowing())
                loadingDialog.dismiss();
            new AlertDialog.Builder(MainMenuActivity.this).setTitle(R.string.error).setMessage(R.string.error_updating).show();
        }

        @Override
        protected void onPostExecute(Result result) {
            if (loadingDialog.isShowing())
                loadingDialog.dismiss();

            if (result.getStatus().equals("success")) {
                try {
                    JSONArray jsonArray = new JSONArray(result.getMessage());
                    db.clearDb();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject v = (JSONObject) jsonArray.get(i);
                        db.insertQuestion(v.getInt("id"), v.getString("question"), v.getInt("correct_answer"), new Date());

                        JSONArray jsonAnswer = v.getJSONArray("answers");
                        for (int j = 0; j < jsonAnswer.length(); j++) {
                            String answer = (String) jsonAnswer.get(j);
                            db.insertAnswer(j, v.getInt("id"), answer, new Date());
                        }
                    }
                    new AlertDialog.Builder(MainMenuActivity.this).setTitle(R.string.info)
                            .setMessage(R.string.update_success).setNeutralButton("Oke", null).show();
                } catch (JSONException e) {
                    new AlertDialog.Builder(MainMenuActivity.this).setTitle(R.string.error)
                            .setMessage(e.getMessage()).setNeutralButton("Oke", null).show();
                }
            } else {
                // Server error
                new AlertDialog.Builder(MainMenuActivity.this).setTitle(R.string.error).setMessage(result.getMessage() + "\nUlangi?")
                        .setCancelable(false).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new UpdateTask().execute();
                    }
                }).setNegativeButton(R.string.no, null).show();
            }
        }
    }
}
