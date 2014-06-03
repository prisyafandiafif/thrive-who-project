package com.alias.test.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.alias.test.QuestionAnswer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by aliakbars on 9/18/13.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "test.db";
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_QUESTION_TABLE = "CREATE TABLE IF NOT EXISTS `question` (\n" +
                "  `id` int(10) NOT NULL,\n" +
                "  `question` varchar(100) NOT NULL,\n" +
                "  `correct_answer` int(10) NOT NULL,\n" +
                "  `timestamp` datetime NOT NULL,\n" +
                "  PRIMARY KEY (`id`)\n" +
                ")";
        String CREATE_ANSWER_TABLE = "CREATE TABLE IF NOT EXISTS `answer` (\n" +
                "  `id` int(10) NOT NULL,\n" +
                "  `question_id` int(10) NOT NULL,\n" +
                "  `answer` varchar(100) NOT NULL,\n" +
                "  `timestamp` datetime NOT NULL,\n" +
                "  PRIMARY KEY (`id`,`question_id`)\n" +
                ")";

        String CREATE_COMPETENCY_TABLE = "CREATE TABLE IF NOT EXISTS `competency` (\n" +
                "  `id` varchar(50) NOT NULL,\n" +
                "  `answer1` int(11) NOT NULL,\n" +
                "  `answer2` int(11) NOT NULL,\n" +
                "  `answer3` int(11) NOT NULL,\n" +
                "  `answer4` int(11) NOT NULL,\n" +
                "  `answer5` int(11) NOT NULL,\n" +
                "  `answer6` int(11) NOT NULL,\n" +
                "  `answer7` int(11) NOT NULL,\n" +
                "  `answer8` int(11) NOT NULL,\n" +
                "  `answer9` int(11) NOT NULL,\n" +
                "  `answer10` int(11) NOT NULL,\n" +
                "  `answer11` int(11) NOT NULL,\n" +
                "  `answer12` int(11) NOT NULL,\n" +
                "  `answer13` int(11) NOT NULL,\n" +
                "  `answer14` int(11) NOT NULL,\n" +
                "  `answer15` int(11) NOT NULL,\n" +
                "  `answer16` int(11) NOT NULL,\n" +
                "  `answer17` int(11) NOT NULL,\n" +
                "  `answer18` int(11) NOT NULL,\n" +
                "  `answer19` int(11) NOT NULL,\n" +
                "  `answer20` int(11) NOT NULL,\n" +
                "  `score` int(11) NOT NULL,\n" +
                "  `timestamp` datetime NOT NULL,\n" +
                "  `uuid` varchar(50) NOT NULL\n" +
                ")";

        String CREATE_COMPETENCY_Q_TABLE = "CREATE TABLE IF NOT EXISTS `competency_q` (\n" +
                "  `id` varchar(50) NOT NULL,\n" +
                "  `question1` int(11) NOT NULL,\n" +
                "  `question2` int(11) NOT NULL,\n" +
                "  `question3` int(11) NOT NULL,\n" +
                "  `question4` int(11) NOT NULL,\n" +
                "  `question5` int(11) NOT NULL,\n" +
                "  `question6` int(11) NOT NULL,\n" +
                "  `question7` int(11) NOT NULL,\n" +
                "  `question8` int(11) NOT NULL,\n" +
                "  `question9` int(11) NOT NULL,\n" +
                "  `question10` int(11) NOT NULL,\n" +
                "  `question11` int(11) NOT NULL,\n" +
                "  `question12` int(11) NOT NULL,\n" +
                "  `question13` int(11) NOT NULL,\n" +
                "  `question14` int(11) NOT NULL,\n" +
                "  `question15` int(11) NOT NULL,\n" +
                "  `question16` int(11) NOT NULL,\n" +
                "  `question17` int(11) NOT NULL,\n" +
                "  `question18` int(11) NOT NULL,\n" +
                "  `question19` int(11) NOT NULL,\n" +
                "  `question20` int(11) NOT NULL,\n" +
                "  `timestamp` datetime NOT NULL,\n" +
                "  `uuid` varchar(50) NOT NULL\n" +
                ")";

        String CREATE_COMPETENCY_T_TABLE = "CREATE TABLE IF NOT EXISTS `competency_t` (\n" +
                "  `id` varchar(50) NOT NULL,\n" +
                "  `time1` double NOT NULL,\n" +
                "  `time2` double NOT NULL,\n" +
                "  `time3` double NOT NULL,\n" +
                "  `time4` double NOT NULL,\n" +
                "  `time5` double NOT NULL,\n" +
                "  `time6` double NOT NULL,\n" +
                "  `time7` double NOT NULL,\n" +
                "  `time8` double NOT NULL,\n" +
                "  `time9` double NOT NULL,\n" +
                "  `time10` double NOT NULL,\n" +
                "  `time11` double NOT NULL,\n" +
                "  `time12` double NOT NULL,\n" +
                "  `time13` double NOT NULL,\n" +
                "  `time14` double NOT NULL,\n" +
                "  `time15` double NOT NULL,\n" +
                "  `time16` double NOT NULL,\n" +
                "  `time17` double NOT NULL,\n" +
                "  `time18` double NOT NULL,\n" +
                "  `time19` double NOT NULL,\n" +
                "  `time20` double NOT NULL,\n" +
                "  `timestamp` datetime NOT NULL,\n" +
                "  `uuid` varchar(50) NOT NULL\n" +
                ")";

        sqLiteDatabase.execSQL(CREATE_QUESTION_TABLE);
        sqLiteDatabase.execSQL(CREATE_ANSWER_TABLE);
        sqLiteDatabase.execSQL(CREATE_COMPETENCY_TABLE);
        sqLiteDatabase.execSQL(CREATE_COMPETENCY_Q_TABLE);
        sqLiteDatabase.execSQL(CREATE_COMPETENCY_T_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        onCreate(sqLiteDatabase);
    }

    public void clearDb() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from question");
        db.execSQL("delete from answer");
    }

    public void insertQuestion(int id, String question, int correct_answer, Date timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("question", question);
        values.put("correct_answer", correct_answer);
        values.put("timestamp", dateFormat.format(timestamp));

        try {
            db.insertOrThrow("question", null, values);
        } catch (Exception e) {
            Log.e("alias14", e.getMessage());
        }
        db.close();
    }

    public void insertAnswer(int id, int question_id, String answer, Date timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("question_id", question_id);
        values.put("answer", answer);
        values.put("timestamp", dateFormat.format(timestamp));

        try {
            db.insertOrThrow("answer", null, values);
        } catch (Exception e) {
            Log.e("alias14", e.getMessage());
        }
        db.close();
    }

    public void insertCompetency(String id, List<Integer> answers, int score, Date timestamp, String uuid) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("id", id);
        for (int i = 0; i < answers.size(); i++) {
            values.put("answer" + (i + 1), answers.get(i));
        }
        values.put("score", score);
        values.put("timestamp", dateFormat.format(timestamp));
        values.put("uuid", uuid);

        try {
            db.insertOrThrow("competency", null, values);
        } catch (Exception e) {
            Log.e("alias14", e.getMessage());
        }
        db.close();
    }

    public void insertCompetencyQ(String id, List<Integer> questions, Date timestamp, String uuid) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("id", id);
        for (int i = 0; i < questions.size(); i++) {
            values.put("question" + (i + 1), questions.get(i));
        }
        values.put("timestamp", dateFormat.format(timestamp));
        values.put("uuid", uuid);

        try {
            db.insertOrThrow("competency_q", null, values);
        } catch (Exception e) {
            Log.e("alias14", e.getMessage());
        }
        db.close();
    }

    public void insertCompetencyT(String id, List<Double> times, Date timestamp, String uuid) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("id", id);
        for (int i = 0; i < times.size(); i++) {
            values.put("time" + (i + 1), times.get(i));
        }
        values.put("timestamp", dateFormat.format(timestamp));
        values.put("uuid", uuid);

        try {
            db.insertOrThrow("competency_t", null, values);
        } catch (Exception e) {
            Log.e("alias14", e.getMessage());
        }
        db.close();
    }

    public List<QuestionAnswer> selectAllQuestionAnswer() {
        SQLiteDatabase db = this.getWritableDatabase();

        List<QuestionAnswer> questionAnswers = new ArrayList<QuestionAnswer>();
        String[] columns = {"id, question, correct_answer"};
        Cursor cursor = db.query("question", columns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Cursor answer = db.query("answer", new String[]{"id","answer"}, "question_id=" + cursor.getInt(0), null, null, null, null);
            List<String> answerList = new ArrayList<String>();
            answer.moveToFirst();
            while (!answer.isAfterLast()) {
                answerList.add(answer.getString(1));
                answer.moveToNext();
            }
            Log.d("alias14",cursor.getInt(0) + " " + cursor.getString(1) + " " + cursor.getInt(2));
            questionAnswers.add(new QuestionAnswer(cursor.getInt(0), cursor.getString(1), answerList, cursor.getInt(2)));
            cursor.moveToNext();
        }
        cursor.close();
        return questionAnswers;
    }
}
