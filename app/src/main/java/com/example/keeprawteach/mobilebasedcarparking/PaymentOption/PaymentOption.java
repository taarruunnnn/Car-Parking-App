package com.example.keeprawteach.mobilebasedcarparking.PaymentOption;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keeprawteach.mobilebasedcarparking.Database.Database;
import com.example.keeprawteach.mobilebasedcarparking.Ip.Ip;
import com.example.keeprawteach.mobilebasedcarparking.R;
import com.example.keeprawteach.mobilebasedcarparking.Success.SucessfullyBooked;

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
import java.util.ArrayList;

public class PaymentOption extends AppCompatActivity {


    Toolbar toolbar;
    TextView textView;
    ProgressBar progressBar;
    Database sqLitedb;
    String slot;
    String from;
    String to;
    String amount;
    ListView listView;
    AutoCompleteTextView autoCompleteTextView;
    ArrayList<String> arrayList;
    ArrayAdapter<String> arrayAdapter;
    private Button ok;

    ImageView floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_option);

        sqLitedb = new Database(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        slot = getIntent().getStringExtra("slot");

        from = getIntent().getStringExtra("from");

        to = getIntent().getStringExtra("to");

        amount = getIntent().getStringExtra("amount");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Payment");

        floatingActionButton=(ImageView)findViewById(R.id.cashhere);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                open_mpesa(amount);

            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        textView = (TextView) findViewById(R.id.found);

        textView.setText(amount);

        listView = (ListView) findViewById(R.id.list);

        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.Code);

        read();

//        open_mpesa(amount);

//        listView.setVisibility(View.GONE);

        progressBar = (ProgressBar) findViewById(R.id.pr);

        progressBar.setVisibility(View.INVISIBLE);

        ok = (Button) findViewById(R.id.ok);


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
            }
        });


    }

    private void open_mpesa(String s) {

        String phone = getPhone();

        Intent intent = getPackageManager().getLaunchIntentForPackage("com.freddygenicho.sample.mpesa");

        intent.putExtra("amount", s);

        intent.putExtra("phone", phone);

        if (intent != null) {

            startActivity(intent);
        }
    }

    private String getPhone() {

        String id = "1";

        String email = "";

        Cursor cursor = sqLitedb.check(id);

        if (cursor != null && cursor.getCount() > 0) {

            while (cursor.moveToNext()) {

                email = cursor.getString(3);
            }
            return email;
        }
        return null;
    }


    private void read() {


        Uri uri = Uri.parse("content://sms/inbox");

        arrayList = new ArrayList<String>();

        ContentResolver contentResolver = getContentResolver();

        Cursor cursor = contentResolver.query(uri, null, null, null, null);

        while (cursor.moveToNext()) {

            String Sender = cursor.getString(cursor.getColumnIndexOrThrow("address")).toString();

            String body = cursor.getString(cursor.getColumnIndexOrThrow("body")).toString();

            if (Sender.equalsIgnoreCase("MPESA")) {

                arrayList.add(body);
            }

        }

        cursor.close();

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);

        listView.setAdapter(arrayAdapter);
    }

    private void check() {

        String code = autoCompleteTextView.getText().toString();

        String amount = textView.getText().toString();

        if (code.isEmpty()) {

            autoCompleteTextView.setError("Enter M-pesa Transaction code");

            autoCompleteTextView.requestFocus();

        } else {

            fetch_email(code, amount);

        }
    }


    private void fetch_email(String code, String amount) {

        String id = "1";

        Cursor cursor = sqLitedb.check(id);

        if (cursor != null && cursor.getCount() > 0) {

            while (cursor.moveToNext()) {

                String email = cursor.getString(3);

                email_found(email, code, amount);

            }
        }
    }

    private void email_found(String email, String code, String amount) {

        //update payment
        Background background = new Background(this);

        background.execute(slot, from, to, email, code, amount);

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle("Exit");

        alertDialog.setMessage("Are you sure to cancel the Booking Process");

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });

        AlertDialog al = alertDialog.create();

        al.show();

    }

    private void updateStatus(String slot) {

        String book = "Booked";

        Update update = new Update();

        update.execute(slot, book);
    }

    private class Background extends AsyncTask<String, Void, String> {
        Context context;

        public Background(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {

            String slot = params[0];

            String from = params[1];

            String to = params[2];

            String email = params[3];

            String code = params[4];

            String amount = params[5];

            String sign_url = new Ip(context).transaction();

            if (sign_url != null) {

                try {

                    URL url = new URL(sign_url);

                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                    httpURLConnection.setRequestMethod("POST");

                    httpURLConnection.setDoOutput(true);

                    OutputStream outputStream = httpURLConnection.getOutputStream();

                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");

                    BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

                    String post_data = URLEncoder.encode("slot", "UTF-8") + "=" + URLEncoder.encode(slot, "UTF-8") + "&"
                            + URLEncoder.encode("from", "UTF-8") + "=" + URLEncoder.encode(from, "UTF-8") + "&"
                            + URLEncoder.encode("to", "UTF-8") + "=" + URLEncoder.encode(to, "UTF-8") + "&"
                            + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") + "&"
                            + URLEncoder.encode("code", "UTF-8") + "=" + URLEncoder.encode(code, "UTF-8") + "&"
                            + URLEncoder.encode("amount", "UTF-8") + "=" + URLEncoder.encode(amount, "UTF-8");

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
        protected void onPostExecute(String s) {

            super.onPostExecute(s);

            progressBar.setVisibility(View.INVISIBLE);

            if (s != null && s.equalsIgnoreCase("Transaction Code Saved")) {

                updateStatus(slot);

            } else {

                Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class Update extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {

            String slot = params[0];

            String status = params[1];

            String message_url = new Ip(getApplicationContext()).bookSlot();

            if (message_url != null) {

                try {

                    URL url = new URL(message_url);

                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                    httpURLConnection.setRequestMethod("POST");

                    httpURLConnection.setDoOutput(true);

                    OutputStream outputStream = httpURLConnection.getOutputStream();

                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");

                    BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

                    String post_data = URLEncoder.encode("slot", "UTF-8") + "=" + URLEncoder.encode(slot, "UTF-8") + "&"
                            + URLEncoder.encode("status", "UTF-8") + "=" + URLEncoder.encode(status, "UTF-8");
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
        protected void onPostExecute(String s) {

            super.onPostExecute(s);

            if (s != null && s.equalsIgnoreCase("Success")) {

                Intent intentRegister = new Intent(PaymentOption.this, SucessfullyBooked.class);

                startActivity(intentRegister);

                overridePendingTransition(R.anim.goup, R.anim.godown);

                finish();

            } else {

                Toast.makeText(PaymentOption.this, s, Toast.LENGTH_SHORT).show();
            }
        }

    }
}
