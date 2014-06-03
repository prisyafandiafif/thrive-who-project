package com.alias.test;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alias.test.util.App;
import com.alias.test.util.DatabaseHandler;
import com.alias.test.util.Request;
import com.alias.test.util.Result;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class QuestionActivity extends Activity {

    private final int NUMBER_OF_OPTIONS = 4;
    private final int FINAL_QUESTION = 10;
    private int TOTAL_QUESTION;
    private TextView questionField;
    private TextView idView;
    private EditText idField;
    private String id;
    private RadioGroup answerButton;
    private RadioButton[] radioButtons = new RadioButton[NUMBER_OF_OPTIONS];
    private Button nextButton, submitButton;
    private List<UserAnswer> userAnswers;
    private List<Integer> questionNos;
    private List<QuestionAnswer> questionAnswerList;
    private int currentQuestion;
    private int currentAnswer;
    private int currentAnswerScore;
    private double currentTime;
    private long tStart, tEnd, deltaT;
    private int totalScore;
    private ArrayList<Integer> totalScores;
    private boolean savedFlag;
    private boolean hasSubmitted;
    private int test;
    private String uuid;
    private String url;
    private DatabaseHandler databaseHandler = new DatabaseHandler(this);

    public QuestionActivity() {
        currentQuestion = -1;
        currentAnswer = 99;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        App.preferences = this.getSharedPreferences("com.alias.test", Context.MODE_PRIVATE);
        App.host = App.preferences.getString(App.HOST_KEY, App.host);
        url = App.host + App.POST;

        idView = (TextView) findViewById(R.id.id);
        idField = (EditText) findViewById(R.id.idField);
        answerButton = (RadioGroup) findViewById(R.id.answerButton);
        questionField = (TextView) findViewById(R.id.questionField);
        radioButtons[0] = (RadioButton) findViewById(R.id.option0);
        radioButtons[1] = (RadioButton) findViewById(R.id.option1);
        radioButtons[2] = (RadioButton) findViewById(R.id.option2);
        radioButtons[3] = (RadioButton) findViewById(R.id.option3);
        nextButton = (Button) findViewById(R.id.nextButton);
        submitButton = (Button) findViewById(R.id.submitButton);
        totalScores = new ArrayList<Integer>();

        test = 0;

        reset();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.question, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (currentQuestion != FINAL_QUESTION - 1 || !savedFlag || test != 2) {
            new AlertDialog.Builder(QuestionActivity.this).setTitle(R.string.warning).setMessage(R.string.exit_prompt)
                    .setNegativeButton(R.string.no, null).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            }).show();
        } else {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                App.showServerSettings(QuestionActivity.this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void optionClicked(View v) {
        if (currentQuestion != -1) {
            QuestionAnswer currentQA = questionAnswerList.get(currentQuestion);
            for (int i = 0; i < NUMBER_OF_OPTIONS; i++) {
                if (radioButtons[i].isChecked()) {
                    if (currentQA.getCorrectAnswer() == i + 1) {
                        currentAnswerScore = 1;
                    } else {
                        currentAnswerScore = 0;
                    }
                    currentAnswer = i + 1;
                    Log.d("alias14", String.valueOf(currentAnswer));
                }
            }
        }
    }

    public boolean hasAnswered() {
        boolean flag = false;
        for (int i = 0; i < NUMBER_OF_OPTIONS; i++) {
            flag = flag || radioButtons[i].isChecked();
        }
        return flag;
    }

    public void nextButtonClicked(View v) {
        if (!hasAnswered()) {
            Toast.makeText(getApplicationContext(), R.string.error_no_answer, Toast.LENGTH_LONG).show();
            return;
        }
        tEnd = System.currentTimeMillis();
        if (currentQuestion != -1) {
            deltaT = tEnd - tStart;
            currentTime = deltaT / 1000.0;
            Log.d("alias14", String.valueOf(currentTime));
            userAnswers.add(new UserAnswer(questionAnswerList.get(currentQuestion).getQuestionId(), currentAnswer, currentAnswerScore, currentTime));
        } else {
            // Pengecekan pertanyaan bantuan
            boolean okayFlag = true;
            if (!radioButtons[2].isChecked()) {
                Toast.makeText(getApplicationContext(), R.string.wrong_answer, Toast.LENGTH_SHORT).show();
                okayFlag = false;
            }
            if (idField.getText().length() != 8) {
                Toast.makeText(getApplicationContext(), R.string.wrong_id, Toast.LENGTH_SHORT).show();
                okayFlag = false;
            }
            if (!okayFlag) {
                return;
            } else {
                new AlertDialog.Builder(QuestionActivity.this).setTitle(R.string.warning).setMessage(R.string.reminder)
                        .setNeutralButton("Oke", null).show();
                id = String.valueOf(idField.getText());
                idView.setVisibility(View.GONE);
                idField.setVisibility(View.GONE);
            }
        }
        currentQuestion++;
        renderView();
        tStart = System.currentTimeMillis();
    }

    public void submitButtonClicked(View v) {
        if (!hasAnswered()) {
            Toast.makeText(getApplicationContext(), R.string.error_no_answer, Toast.LENGTH_LONG).show();
            return;
        }
        tEnd = System.currentTimeMillis();
        deltaT = tEnd - tStart;
        currentTime = deltaT / 1000.0;
        if (!hasSubmitted) {
            userAnswers.add(new UserAnswer(questionAnswerList.get(currentQuestion).getQuestionId(), currentAnswer, currentAnswerScore, currentTime));
            hasSubmitted = true;
        }
        for (int i = 0; i < userAnswers.size(); i++) {
            totalScore += userAnswers.get(i).getScore();
        }
        totalScores.add(new Integer(totalScore));
        Log.d("alias14", "Panjang: " + userAnswers.size());
        saveData();
    }

    public void renderView() {
        QuestionAnswer currentQA;
        try {
            currentQA = questionAnswerList.get(currentQuestion);
        } catch (Exception e) {
            Toast.makeText(QuestionActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        nextButton.setVisibility(View.VISIBLE);
        submitButton.setVisibility(View.GONE);

        questionField.setText(currentQA.getQuestion());
        if (currentQA.getAnswers().size() == NUMBER_OF_OPTIONS) {
            for (int i = 0; i < currentQA.getAnswers().size(); i++) {
                radioButtons[i].setText(currentQA.getAnswers().get(i));
                radioButtons[i].setVisibility(View.VISIBLE);
            }
        } else {
            int i = 0;
            while (i < currentQA.getAnswers().size()) {
                radioButtons[i].setText(currentQA.getAnswers().get(i));
                i++;
            }
            do {
                radioButtons[i].setVisibility(View.GONE);
                i++;
            } while (i < NUMBER_OF_OPTIONS);
        }
        answerButton.clearCheck();
        if (currentQuestion == FINAL_QUESTION - 1) {
            nextButton.setVisibility(View.GONE);
            submitButton.setVisibility(View.VISIBLE);
        }
    }

    private class PostTask extends AsyncTask<List<NameValuePair>, String, Result> {

        private ProgressDialog loadingDialog = new ProgressDialog(QuestionActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            submitButton.setEnabled(false);
            loadingDialog.setMessage("Menyimpan jawaban...");
            loadingDialog.show();
        }

        @Override
        protected Result doInBackground(List<NameValuePair>... params) {
            try {
                publishProgress("Mengirim hasil...");
                return Request.post(url, params[0]);
            } catch (Exception e) {
                return new Result("Error: ", e.getMessage());
            }
        }

        @Override
        protected void onCancelled() {
            submitButton.setEnabled(true);
        }

        @Override
        protected void onPostExecute(Result result) {
            if (loadingDialog.isShowing())
                loadingDialog.dismiss();

            if (result != null) {
                if (result.getStatus().equals("success")) {
                    // Success
                    saveToDatabase();
                    endTest();
                } else {
                    // Server error
                    new AlertDialog.Builder(QuestionActivity.this).setTitle(R.string.error).setMessage(result.getMessage() + "\nUlangi?")
                            .setCancelable(false).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            saveData();
                        }
                    }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            saveToDatabase();
                            Toast.makeText(getApplicationContext(), "Tersimpan sementara", Toast.LENGTH_SHORT).show();
                            endTest();
                        }
                    }).setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            submitButton.setEnabled(true);
                        }
                    }).show();
                }
            } else {
                // Something went wrong
                new AlertDialog.Builder(QuestionActivity.this).setTitle(R.string.error).setMessage(R.string.error_saving)
                        .setCancelable(false).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        saveData();
                    }
                }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        saveToDatabase();
                        Toast.makeText(getApplicationContext(), "Tersimpan sementara", Toast.LENGTH_SHORT).show();
                        endTest();
                    }
                }).setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        submitButton.setEnabled(true);
                    }
                }).show();
            }
        }
    }

    public void endTest() {
        test++;
        if (test == 1) {
            new AlertDialog.Builder(QuestionActivity.this).setTitle("Selesai")
                    .setMessage(R.string.first_lap).setCancelable(false)
                    .setPositiveButton("OK", null).show();
            reset();
            currentQuestion = 0;
            renderView();
            tStart = System.currentTimeMillis();
        } else if (test == 2) {
            try {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                r.play();
            } catch (Exception e) {
                Log.e("alias14", "Failed to play sound");
            }
            new AlertDialog.Builder(QuestionActivity.this).setTitle("Selesai")
                    .setMessage("Tes telah selesai.\nSkor-1: " + totalScores.get(0) + "\nSkor-2: " + totalScores.get(1))
                    .setCancelable(false).setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                    if (i == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP)
                        finish();
                    return false;
                }
            }).show();
        }
        submitButton.setEnabled(true);
    }

    private void saveToDatabase() {
        List<Integer> answers = new ArrayList<Integer>();
        List<Double> times = new ArrayList<Double>();
        for (int i = 0; i < userAnswers.size(); i++) {
            answers.add(userAnswers.get(i).getScore());
            times.add(userAnswers.get(i).getTime());
        }
        while (answers.size() != TOTAL_QUESTION) {
            answers.add(0);
        }
        while (questionNos.size() != TOTAL_QUESTION) {
            questionNos.add(0);
        }
        while (times.size() != TOTAL_QUESTION) {
            times.add(0.0);
        }
        Log.d("alias14", "Skor: " + totalScore);
        Date date = new Date();
        databaseHandler.insertCompetency(id, answers, totalScore, date, uuid);
        databaseHandler.insertCompetencyQ(id, questionNos, date, uuid);
        databaseHandler.insertCompetencyT(id, times, date, uuid);
        savedFlag = true;
    }

    private void saveData() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("id", id));
        for (int i = 0; i < userAnswers.size(); i++) {
            params.add(new BasicNameValuePair("question" + (i + 1), String.valueOf(userAnswers.get(i).getQuestionId())));
            params.add(new BasicNameValuePair("answer" + (i + 1), String.valueOf(userAnswers.get(i).getAnswer())));
            params.add(new BasicNameValuePair("time" + (i + 1), String.valueOf(userAnswers.get(i).getTime())));
        }
        params.add(new BasicNameValuePair("score", String.valueOf(totalScore)));
        params.add(new BasicNameValuePair("uuid", uuid));
        new PostTask().execute(params);
    }

    private void reset() {
        uuid = UUID.randomUUID().toString();
        savedFlag = false;
        hasSubmitted = false;

        userAnswers = new ArrayList<UserAnswer>();
        questionNos = new ArrayList<Integer>();
        questionAnswerList = databaseHandler.selectAllQuestionAnswer();
        TOTAL_QUESTION = questionAnswerList.size();

        totalScore = 0;
        Collections.shuffle(questionAnswerList);
    }
}
