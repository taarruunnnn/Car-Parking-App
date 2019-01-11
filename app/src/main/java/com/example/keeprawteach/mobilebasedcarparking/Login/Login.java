package com.example.keeprawteach.mobilebasedcarparking.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.example.keeprawteach.mobilebasedcarparking.Database.Database;
import com.example.keeprawteach.mobilebasedcarparking.Main.Main;
import com.example.keeprawteach.mobilebasedcarparking.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity {


    private Toolbar toolbar;

    private TextView ForgotPass;

    private Button CreateAccount, LoginNow;

    private AutoCompleteTextView AA, BB;

    Database sqLitedb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sqLitedb=new Database(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

//        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Sign In");

        ForgotPass = (TextView) findViewById(R.id.Forgot);

        CreateAccount = (Button) findViewById(R.id.join);

        LoginNow = (Button) findViewById(R.id.sign);

        AA = (AutoCompleteTextView) findViewById(R.id.aa);

        BB = (AutoCompleteTextView) findViewById(R.id.bb);

        actionListener();


    }

    private void actionListener() {
        LoginNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
            }
        });
        CreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Login.this, JoinUs.class);

                startActivity(intent);
            }
        });
        ForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Forgot.class);

                startActivity(intent);
            }
        });
    }

    private void check() {

        String A = AA.getText().toString();

        String B = BB.getText().toString();

        if (A.isEmpty() && B.isEmpty()) {

            AA.setError("Enter Email");

            BB.setError("Enter Password");

            AA.requestFocus();

        } else if (A.isEmpty()) {

            AA.setError("Enter Email");

            AA.requestFocus();

        } else if (B.isEmpty()) {

            BB.setError("Enter Password");

            BB.requestFocus();

        } else {
            if (!validEmail(A)) {

                AA.setError("Invalid Email Address");

                AA.requestFocus();

            } else {
                progress(A,B);

            }
        }

    }

    private void progress(final String a, final String b) {

        final ProgressDialog progressDialog = new ProgressDialog(Login.this);

        progressDialog.setIndeterminate(true);

        progressDialog.setMessage("Authenticating...");

        progressDialog.show();

        new android.os.Handler().postDelayed(

                new Runnable() {

                    public void run() {

                        check_db(a,b);

                        progressDialog.dismiss();

                    }
                }, 3000);
    }

    private void check_db(String a, String b) {

        String isInserted = sqLitedb.CheckUser(a,b);

        if (isInserted.equalsIgnoreCase("Success")) {


            Intent intent=new Intent(Login.this, Main.class);

            startActivity(intent);

            finish();

            overridePendingTransition(R.anim.goup, R.anim.godown);

        } else {
            Snackbar.make(AA, isInserted, Snackbar.LENGTH_LONG).setAction("Action", null).show();

        }

    }

    private boolean validEmail(String a) {

        Pattern pattern;

        Matcher matcher;

        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        pattern = Pattern.compile(EMAIL_PATTERN);

        matcher = pattern.matcher(a);

        return matcher.matches();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();

    }
}
