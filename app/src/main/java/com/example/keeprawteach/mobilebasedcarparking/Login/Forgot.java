package com.example.keeprawteach.mobilebasedcarparking.Login;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keeprawteach.mobilebasedcarparking.Database.Database;
import com.example.keeprawteach.mobilebasedcarparking.Ip.Ip;
import com.example.keeprawteach.mobilebasedcarparking.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class Forgot extends AppCompatActivity {


    private TextView Login, EmailFound;

    private AutoCompleteTextView Email, Pass, ConPass;

    private Button Reset, Change;

    Database sqLitedb;

    LinearLayout LL1, LL2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        sqLitedb=new Database(this);


        EmailFound = (TextView) findViewById(R.id.emfound);

        LL1 = (LinearLayout) findViewById(R.id.Header);

        LL2 = (LinearLayout) findViewById(R.id.Passwords);

        LL2.setVisibility(View.GONE);

        Login = (TextView) findViewById(R.id.login);

        Email = (AutoCompleteTextView) findViewById(R.id.aa);

        Pass = (AutoCompleteTextView) findViewById(R.id.bb);

        ConPass = (AutoCompleteTextView) findViewById(R.id.cc);

        Reset = (Button) findViewById(R.id.log);

        Change = (Button) findViewById(R.id.reset);

        //deactivate

        actionListener();
    }

    private void actionListener() {
        Change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get_Update();
            }
        });
        Reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_mail();
            }
        });
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void check_mail() {

        String user = Email.getText().toString();
        if (user.isEmpty()) {

            Toast.makeText(this, "Please enter your Email", Toast.LENGTH_SHORT).show();

        } else {

            search(user);
        }

    }

    private void search(String user) {

        Cursor cursor = sqLitedb.searchEmail();

        if (cursor != null && cursor.getCount() > 0) {

            while (cursor.moveToNext()) {
                String email = cursor.getString(4);
                if (user.equalsIgnoreCase(email)) {
                    activate_pass(email);
                } else {
                    Snackbar.make(Change, "No such User..Please create account", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }

        }
    }

    private void activate_pass(final String email) {
        LL1.setVisibility(View.GONE);

        LL2.setVisibility(View.VISIBLE);

        EmailFound.setText(email);

    }

    private void get_Update() {

        String email = EmailFound.getText().toString();

        String pass = Pass.getText().toString();

        String passcon = ConPass.getText().toString();
        if (pass.isEmpty() || passcon.isEmpty()) {
            Snackbar.make(Change, "Enter all fields", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        } else {

            if (pass.equalsIgnoreCase(passcon)) {
                update_db(email, pass);

            } else {
                Snackbar.make(Change, "Password does not match!!", Snackbar.LENGTH_LONG).setAction("Action", null).show();

            }
        }
    }

    private void update_db(String email, String pass) {

        boolean isInserted = sqLitedb.changePass(email, pass);

        if (isInserted == true) {

            Background background = new Background(this);

            background.execute(email, pass);

        } else {

            Snackbar.make(Change, "Failed", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }

    }

    private class Background extends AsyncTask<String, String, String> {
        Context context;

        ProgressDialog progressDialog;

        public Background(Context context) {

            this.context = context;

        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(context);

            progressDialog.setMessage("Updating...");

            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            String email = params[0];

            String password = params[1];

            String sign_url = new Ip(context).changepass();

            if (sign_url != null) {

                try {

                    URL url = new URL(sign_url);

                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                    httpURLConnection.setRequestMethod("POST");

                    httpURLConnection.setDoOutput(true);

                    OutputStream outputStream = httpURLConnection.getOutputStream();

                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");

                    BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

                    String post_data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") + "&"

                            + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");

                    bufferedWriter.write(post_data);

                    bufferedWriter.flush();

                    bufferedWriter.close();

                    InputStream inputStream = httpURLConnection.getInputStream();

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));

                    String result = "";

                    String line = "";

                    while ((line = bufferedReader.readLine()) != null) {

                        result += line;

                    }
                    bufferedReader.close();

                    inputStream.close();

                    httpURLConnection.disconnect();

                    return result;

                } catch (MalformedURLException e) {

                    e.printStackTrace();

                } catch (IOException e) {

                    e.printStackTrace();

                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);

            progressDialog.dismiss();

            if (result != null && result.equalsIgnoreCase("Password Successful Changed")) {

                Pass.setText("");

                ConPass.setText("");

                count();

            } else {

                Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void count() {

        new CountDownTimer(3000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {

                finish();

            }

        }.start();

    }
}
