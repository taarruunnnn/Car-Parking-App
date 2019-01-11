package com.example.keeprawteach.mobilebasedcarparking.Login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keeprawteach.mobilebasedcarparking.Calling.Calling;
import com.example.keeprawteach.mobilebasedcarparking.Conditions.Conditions;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JoinUs extends AppCompatActivity {

    Database database;
    String[] cartype = {"Private", "Public", "Truck"};
    String[] a = {"254"};
    private Toolbar toolbar;
    private ImageView MaleCalls;
    private AutoCompleteTextView AA, BB, CC, DD, EE, FF, GG, TYPE;
    private CheckBox checkBox;
    private Button button;
    private TextView TermsandConditions;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_us);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        database = new Database(this);

        spinner = (Spinner) findViewById(R.id.spin);

//        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Join Eldopark");

        TermsandConditions = (TextView) findViewById(R.id.Global);

        AA = (AutoCompleteTextView) findViewById(R.id.aa);

        BB = (AutoCompleteTextView) findViewById(R.id.bb);

        CC = (AutoCompleteTextView) findViewById(R.id.cc);

        DD = (AutoCompleteTextView) findViewById(R.id.dd);

        EE = (AutoCompleteTextView) findViewById(R.id.pass);

        FF = (AutoCompleteTextView) findViewById(R.id.pass2);

        GG = (AutoCompleteTextView) findViewById(R.id.car);

        TYPE = (AutoCompleteTextView) findViewById(R.id.type);

        ArrayAdapter<String> array = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, cartype);

        TYPE.setAdapter(array);

        ArrayAdapter<String> phone=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,a);

        spinner.setAdapter(phone);

        checkBox = (CheckBox) findViewById(R.id.terms);

        button = (Button) findViewById(R.id.save);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
            }
        });

        TermsandConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(JoinUs.this, Conditions.class);

                startActivity(intent);
            }
        });


        MaleCalls = (ImageView) findViewById(R.id.makecall);
        MaleCalls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(JoinUs.this, Calling.class);

                startActivity(intent);

            }
        });
    }

    private void check() {
        String A, B, C, D, E, F, G, TG;

        A = AA.getText().toString();

        B = BB.getText().toString();

        C = CC.getText().toString();

        D = DD.getText().toString();

        E = EE.getText().toString();

        F = FF.getText().toString();

        G = GG.getText().toString();

        TG = TYPE.getText().toString();

        if (A.isEmpty() && B.isEmpty() && C.isEmpty() && D.isEmpty() && E.isEmpty() && F.isEmpty() && G.isEmpty() && TG.isEmpty()) {

            AA.setError("Enter Firstname");

            AA.requestFocus();

            BB.setError("Enter Lastname");

            CC.setError("Enter Email");

            DD.setError("Enter Phone");

            EE.setError("Enter Password");

            FF.setError("Confirm Password");

            GG.setError("Enter Car Number");

            TYPE.setError("Enter Car Type");

        } else if (A.isEmpty()) {

            AA.setError("Enter Firstname");

            AA.requestFocus();
        } else if (B.isEmpty()) {

            BB.setError("Enter Lastname");

            BB.requestFocus();
        } else if (C.isEmpty()) {

            CC.setError("Enter Email");

            CC.requestFocus();
        } else if (D.isEmpty()) {

            DD.setError("Enter Phone");

            DD.requestFocus();
        } else if (E.isEmpty()) {

            EE.setError("Enter Password");

            EE.requestFocus();
        } else if (F.isEmpty()) {

            FF.setError("Enter Confirm Password");

            FF.requestFocus();
        } else if (G.isEmpty()) {

            GG.setError("Enter Car Number");

            GG.requestFocus();
        } else if (TG.isEmpty()) {

            TYPE.setError("Enter Car Type");

            TYPE.requestFocus();
        } else {
            if (!validEmail(C)) {

                CC.setError("Invalid Email Address");

                CC.requestFocus();

            } else if (D.length() > 9 || D.length() < 9) {

                DD.setError("Check Phone Number");

                DD.requestFocus();
            } else {
                if (E.equalsIgnoreCase(F)) {

                    if (checkBox.isChecked()) {

                        insertSQlite(A, B, C, D, E, G, TG);

                    } else {

                        Toast.makeText(this, "You must agree to the terms and condition", Toast.LENGTH_SHORT).show();
                    }

                } else {

                    Toast.makeText(this, "Password does not match", Toast.LENGTH_SHORT).show();
                }
            }

        }

    }

    private void insertSQlite(String a, String b, String c, String d, String e, String f, String g) {

        boolean isInserted = database.addUser(a, b, d, c, e, f, g);

        if (isInserted == true) {

            Background background = new Background(this);

            background.execute(a, b, c, d, e, f, g);


        } else {

            Snackbar.make(AA, "Failed", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }

    }

    private boolean validEmail(String c) {

        Pattern pattern;

        Matcher matcher;

        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        pattern = Pattern.compile(EMAIL_PATTERN);

        matcher = pattern.matcher(c);

        return matcher.matches();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();

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

    public class Background extends AsyncTask<String, String, String> {

        Context context;

        ProgressDialog progressDialog;

        public Background(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(context);

            progressDialog.setMessage("Creating Account......");

            progressDialog.setIndeterminate(true);

            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            String firstname = params[0];

            String lastname = params[1];

            String phonenumber = params[2];

            String emailadress = params[3];

            String password = params[4];

            String car = params[5];

            String type = params[6];

            String sign_url = new Ip(context).signup();

            if (sign_url != null) {

                try {

                    URL url = new URL(sign_url);

                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                    httpURLConnection.setRequestMethod("POST");

                    httpURLConnection.setDoOutput(true);

                    OutputStream outputStream = httpURLConnection.getOutputStream();

                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");

                    BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

                    String post_data = URLEncoder.encode("firstname", "UTF-8") + "=" + URLEncoder.encode(firstname, "UTF-8") + "&"
                            + URLEncoder.encode("lastname", "UTF-8") + "=" + URLEncoder.encode(lastname, "UTF-8") + "&"
                            + URLEncoder.encode("phonenumber", "UTF-8") + "=" + URLEncoder.encode(phonenumber, "UTF-8") + "&"
                            + URLEncoder.encode("emailadress", "UTF-8") + "=" + URLEncoder.encode(emailadress, "UTF-8") + "&"
                            + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8") + "&"
                            + URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode(type, "UTF-8") + "&"
                            + URLEncoder.encode("car", "UTF-8") + "=" + URLEncoder.encode(car, "UTF-8");

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

            if (result == null) {
//                ask_for_ip();

            } else if (result != null && result.equalsIgnoreCase("Email already taken.....Try Again..!!")) {

                Toast.makeText(context, result, Toast.LENGTH_SHORT).show();

            } else if (result != null && result.equalsIgnoreCase("Account Creation Successful")) {

                Toast.makeText(context, result, Toast.LENGTH_SHORT).show();

                count();

            } else {

                Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
            }

        }
    }
}
