package com.example.keeprawteach.mobilebasedcarparking.ViewSlots;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;

import java.text.ParseException;

import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.keeprawteach.mobilebasedcarparking.Database.Database;
import com.example.keeprawteach.mobilebasedcarparking.Ip.Ip;
import com.example.keeprawteach.mobilebasedcarparking.PaymentOption.PaymentOption;
import com.example.keeprawteach.mobilebasedcarparking.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ViewSlots extends AppCompatActivity {


    GridView gridView;
    Toolbar toolbar;
    Database sqLitedb;
    private ImageButton imageButtondate, imageButtontime;
    private EditText dateselected, timeselected;
    private Button see;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_slots);

        sqLitedb = new Database(this);

        String email = getIntent().getStringExtra("email_key");

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle(email);

        see = (Button) findViewById(R.id.seedata);


        imageButtondate = (ImageButton) findViewById(R.id.btndate);

        imageButtontime = (ImageButton) findViewById(R.id.btntime);

        dateselected = (EditText) findViewById(R.id.datenowselect);

        timeselected = (EditText) findViewById(R.id.timenowselect);

        imageButtondate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                getdatenow();

            }
        });

        imageButtontime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                gettimenow();
            }
        });

        see.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        });

        gridView = (GridView) findViewById(R.id.list);
    }

    private void gettimenow() {
        GregorianCalendar gc = new GregorianCalendar();
        final int hourOfDay, minute, sec;
        hourOfDay = gc.get(Calendar.HOUR_OF_DAY);
        minute = gc.get(Calendar.MINUTE);

        TimePickerDialog tpd = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {


            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                timeselected.setText(hourOfDay + ":" + minute);
            }
        }, hourOfDay, minute, true);
        tpd.show();


    }

    private void getdatenow() {

        GregorianCalendar gc = new GregorianCalendar();
        final int hourOfDay, minute, sec;
        hourOfDay = gc.get(Calendar.HOUR_OF_DAY);
        minute = gc.get(Calendar.MINUTE);

        TimePickerDialog tpd = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                dateselected.setText(hourOfDay + ":" + minute);
            }
        }, hourOfDay, minute, true);
        tpd.show();
    }

    private void send() {
        String daten, timen;
        daten = dateselected.getText().toString();
        timen = timeselected.getText().toString();
        if (daten.isEmpty() || timen.isEmpty()) {
            Snackbar.make(dateselected, "Check fields", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        } else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Time");
            alertDialog.setMessage("Starting:" + daten + "\n" +
                    "End :" + timen + "\n");
            alertDialog.setPositiveButton("Search", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String name = getSupportActionBar().getTitle().toString();
                    Download downloader = new Download(getApplicationContext(), gridView);
                    downloader.execute(name);


                }
            });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog al = alertDialog.create();
            al.show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
        return true;
    }

    private void confirm_if_no_other(String slot) {


        String id = "1";

        Cursor cursor = sqLitedb.check(id);

        if (cursor != null && cursor.getCount() > 0) {

            while (cursor.moveToNext()) {

                String email = cursor.getString(3);

                email_found(email, slot);

            }
        }

    }

    private void email_found(String email, String s) {
        Background update = new Background(this, s);
        update.execute(email);

    }

    private void showMpesa(final String slot, int hours) {

        String id = "1";

        Cursor cursor = sqLitedb.check(id);

        if (cursor != null && cursor.getCount() > 0) {

            while (cursor.moveToNext()) {

                String phone = cursor.getString(3);

                findUser(phone, slot, hours);

            }
        }


    }

    private void findUser(String phone, String slot, int hours) {

        String Hours = String.valueOf(hours);

        FindTotal findTotal = new FindTotal(this, slot);

        findTotal.execute(Hours, phone);
    }

    private void calculateDuration(String s) {


        String Start_Time = dateselected.getText().toString();

        String End_Time = timeselected.getText().toString();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");

        try {
            Date Start = simpleDateFormat.parse(Start_Time);

            Date End = simpleDateFormat.parse(End_Time);

            long difference = End.getTime() - Start.getTime();

            if (difference < 0) {

                Date Max = simpleDateFormat.parse("24:00");

                Date Min = simpleDateFormat.parse("00:00");

                difference = (Max.getTime() - Start.getTime()) + (End.getTime() - Min.getTime());
            }

            int Days = (int) (difference / (1000 * 60 * 60 * 24));

            int Hours = (int) ((difference - (1000 * 60 * 60 * 24 * Days)) / (1000 * 60 * 60));

            int Minutes = (int) (difference - (1000 * 60 * 60 * 24 * Days) - (1000 * 60 * 60 * Hours)) / (1000 * 60);

//            Toast.makeText(this, "Days"+Days+"\nHours"+Hours+"\nMinutes"+Minutes, Toast.LENGTH_SHORT).show();
            showMpesa(s, Hours);

        } catch (ParseException e) {

            e.printStackTrace();

        }


    }
    public void stk(final String s, String slot){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ViewSlots.this);

        alertDialog.setTitle("Payment");

        alertDialog.setMessage(""+s);

        alertDialog.setNegativeButton("Pay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                d(s);
            }
        });

        alertDialog.show();


    }
    private String getUsername() {

        String jeff = "";

        String user = "1";

        Cursor cursor = sqLitedb.searchEmail();

        if (cursor != null && cursor.getCount() > 0) {

            while (cursor.moveToNext()) {

                String d = cursor.getString(0);

                String e = cursor.getString(3);

                if (user.equalsIgnoreCase(d)) {
                    jeff = e;
                    return jeff;
                }

            }
        }
        return jeff;
    }

    private void d(String s) {

        String a=getUsername();

        Stk stk=new Stk(this);

        stk.execute(s,a);

    }
    public class Stk extends AsyncTask<String,String,String>{

        Context context;

        ProgressDialog progressDialog;

        public Stk(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog=new ProgressDialog(context);

            progressDialog.setMessage("Connecting to Safaricom Servers");

            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            String amount = params[0];

            String phone = params[1];

            String sign_url = new Ip(context).mpesa();

            if (sign_url != null) {

                try {

                    URL url = new URL(sign_url);

                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                    httpURLConnection.setRequestMethod("POST");

                    httpURLConnection.setDoOutput(true);

                    OutputStream outputStream = httpURLConnection.getOutputStream();

                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");

                    BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

                    String post_data = URLEncoder.encode("amount", "UTF-8") + "=" + URLEncoder.encode(amount, "UTF-8") + "&"

                            + URLEncoder.encode("phone", "UTF-8") + "=" + URLEncoder.encode(phone, "UTF-8");

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

            progressDialog.dismiss();

            Toast.makeText(context, ""+s, Toast.LENGTH_LONG).show();
        }
    }

    private void open_mpesa(String s, String slot) {

//        stk(s,slot);
//        gotoMpesa(s, slot);
//
//        Intent intent = getPackageManager().getLaunchIntentForPackage("com.freddygenicho.sample.mpesa");
//
//        intent.putExtra("amount", s);
//
//        intent.putExtra("slot", slot);
//
//        if (intent != null) {

//            startActivity(intent);

            Intent intentRegister = new Intent(ViewSlots.this, PaymentOption.class);

            Bundle bundle = new Bundle();

            bundle.putString("slot", slot);

            bundle.putString("from", dateselected.getText().toString());

            bundle.putString("to", timeselected.getText().toString());

            bundle.putString("amount", s);

            intentRegister.putExtras(bundle);

            startActivity(intentRegister);

            overridePendingTransition(R.anim.goup, R.anim.godown);

            finish();
//
//        } else {
//
//            gotoMpesa(s, slot);
//        }


//
//        try {
//            Intent jeff = new Intent(Intent.ACTION_VIEW);
//
//            jeff.setType("text/plain");
//
//            String url = s;
//
//            jeff.putExtra(Intent.EXTRA_TEXT, url);
//
//            jeff.setPackage("com.freddygenicho.sample.mpesa");
//
//            startActivity(jeff);
//
//        } catch (Exception e) {
//
//            Toast.makeText(ViewSlots.this, "please restart App", Toast.LENGTH_SHORT).show();
//        }
    }

    private void gotoMpesa(final String s, final String slot) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ViewSlots.this);

        alertDialog.setTitle("Payment");

        alertDialog.setMessage("Go to M-pesa\nSend Money\nPhone Number: 0724743788\nAmount:" + s);

        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                //open payment module:
                Intent intentRegister = new Intent(ViewSlots.this, PaymentOption.class);

                Bundle bundle = new Bundle();

                bundle.putString("slot", slot);

                bundle.putString("from", dateselected.getText().toString());

                bundle.putString("to", timeselected.getText().toString());

                bundle.putString("amount", s);

                intentRegister.putExtras(bundle);

                startActivity(intentRegister);

                overridePendingTransition(R.anim.goup, R.anim.godown);

                finish();

            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ViewSlots.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog al = alertDialog.create();
        al.show();

    }


    public class Download extends AsyncTask<String, Void, String> {
        Context context;
        GridView gridView;
        ProgressDialog progressDialog;
        HttpURLConnection httpURLConnection;
        BufferedReader bufferedReader;
        StringBuilder stringBuilder;

        public Download(Context context, GridView gridView) {
            this.context = context;
            this.gridView = gridView;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progressDialog = new ProgressDialog(context);
//            progressDialog.setMessage("downloading......");
//            progressDialog.show();
        }

        protected String doInBackground(String... params) {

            String message = params[0];

            String message_url = new Ip(context).searchslot();
            if (message_url != null) {
                try {
                    URL url = new URL(message_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
                    BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                    String post_data = URLEncoder.encode("message", "UTF-8") + "=" + URLEncoder.encode(message, "UTF-8");
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
//            progressDialog.dismiss();
            if (s != null) {
                ParserData parserData = new ParserData(context, gridView, s);
                parserData.execute(s);
            } else {
                Toast.makeText(context, "No slots found,Please search the next Location", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Toast.makeText(context, "Cancelled by user", Toast.LENGTH_LONG).show();

        }

        public String DownloadData() {
            stringBuilder = new StringBuilder();
            String line;
            httpURLConnection = new GetConnected().GetConnected();
            try {
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setConnectTimeout(120000);
                httpURLConnection.connect();

                bufferedReader = new BufferedReader(new InputStreamReader(new BufferedInputStream(httpURLConnection.getInputStream())));
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                httpURLConnection.disconnect();
            } catch (ProtocolException e) {
                return null;
            } catch (IOException e) {
                return null;
            }

            return stringBuilder.toString();
        }

        protected class GetConnected {
            URL url;
            HttpURLConnection httpU;
            String sign_url = new Ip(context).slots();

            public HttpURLConnection GetConnected() {
                if (sign_url != null) {
                    try {
                        url = new URL(sign_url);
                        httpU = (HttpURLConnection) url.openConnection();
                    } catch (MalformedURLException e) {
                        return null;
                    } catch (IOException e) {
                        return null;
                    }
                    return httpU;
                }
                return null;
            }
        }

        private class ParserData extends AsyncTask<String, Void, Boolean> {
            Context context;
            GridView gridView;
            ProgressDialog progressDialog;
            String jsondata;
            ArrayList<Spacecraft> arrayList = new ArrayList<>();

            public ParserData(Context context, GridView gridView, String jsondata) {
                this.context = context;
                this.gridView = gridView;
                this.jsondata = jsondata;
            }


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
//                progressDialog=new ProgressDialog(this);
//                progressDialog.setMessage("parsing......");
//                progressDialog.show();
            }

            @Override
            protected Boolean doInBackground(String... params) {
                jsondata = params[0];
                return ParseJsonDt();
            }


            @Override
            protected void onPostExecute(Boolean bool) {
                super.onPostExecute(bool);
//                progressDialog.dismiss();

                if (bool) {
                    AdapterClass adapterClass = new AdapterClass(arrayList, context, gridView);
                    gridView.setAdapter(adapterClass);
                } else {
                    Toast.makeText(context, "Encountered Error During processing", Toast.LENGTH_LONG).show();
                }
            }


            protected Boolean ParseJsonDt() {
                // jsondata=data;
                boolean vall = false;
                try {
                    JSONArray jsonArray = new JSONArray(jsondata);
                    JSONObject jsonObject = null;
                    int i = 0;

                    Spacecraft spacecraft;
                    while (i < jsonArray.length()) {

                        jsonObject = jsonArray.getJSONObject(i);
                        spacecraft = new Spacecraft();
                        String name, slot, namefound, status;
                        name = getSupportActionBar().getTitle().toString();
                        slot = jsonObject.getString("name");
                        namefound = jsonObject.getString("location");
                        status = jsonObject.getString("status");

                        spacecraft.setName(slot);
                        spacecraft.setId(namefound);
                        spacecraft.setLocation(status);
                        arrayList.add(spacecraft);
                        i++;
                    }
                    vall = true;
                } catch (JSONException e) {

                }
                return vall;
            }

        }

        private class Spacecraft {
            public String name, id, location;

            public Spacecraft() {
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getLocation() {
                return location;
            }

            public void setLocation(String location) {
                this.location = location;
            }
        }

        private class AdapterClass extends BaseAdapter {
            ArrayList<Spacecraft> spacecraftArrayList;
            Context context;
            GridView gridView;

            public AdapterClass(ArrayList<Spacecraft> spacecraftArrayList, Context context, GridView gridView) {
                this.spacecraftArrayList = spacecraftArrayList;
                this.context = context;
                this.gridView = gridView;
            }

            @Override
            public int getCount() {
                return spacecraftArrayList.size();
            }

            @Override
            public Object getItem(int position) {
                return spacecraftArrayList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder viewHolder = null;
                if (viewHolder == null) {
                    convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.model, null);
                    viewHolder = new ViewHolder(convertView);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }

                Spacecraft spacecraft = spacecraftArrayList.get(position);


                final String name, slot, namefound, status;
                name = getSupportActionBar().getTitle().toString();
                slot = spacecraft.getName();
                namefound = spacecraft.getId();
                status = spacecraft.getLocation();

                viewHolder.nameTxt.setText(slot);
                viewHolder.idTxt.setText(namefound);
                viewHolder.status.setText(status);
                if (status.equalsIgnoreCase("booked")) {
                    viewHolder.nameTxt.setBackgroundColor(Color.parseColor("#f70707"));
                }
                final String newstatus = viewHolder.status.getText().toString();
                viewHolder.nameTxt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        check_status(newstatus);
                    }

                    private void check_status(String newstatus) {
                        String booked = "booked";
                        if (newstatus.equalsIgnoreCase(booked)) {
                            Toast.makeText(ViewSlots.this, "Slot already engaged!!.. ", Toast.LENGTH_SHORT).show();
                        } else {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(ViewSlots.this);
                            alertDialog.setTitle("Confirm Selection");
                            alertDialog.setMessage("You're about to book slot\n" + slot + "\nFrom:" + dateselected.getText().toString() + "\nTo:" + timeselected.getText().toString());
                            alertDialog.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

//                                    showMpesa(slot);
                                    confirm_if_no_other(slot);

                                }
                            });
                            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(ViewSlots.this, "Cancelled", Toast.LENGTH_SHORT).show();
                                }
                            });
                            AlertDialog al = alertDialog.create();
                            al.show();


                        }
                    }
                });
                return convertView;
            }


        }

        public class ViewHolder {
            TextView idTxt, status;
            View view;
            ImageView imageView;
            Button nameTxt;

            public ViewHolder(View view) {
                this.view = view;
                nameTxt = (Button) view.findViewById(R.id.txtName);
                idTxt = (TextView) view.findViewById(R.id.txtId);
                status = (TextView) view.findViewById(R.id.textView5);
            }
        }
    }

    private class Background extends AsyncTask<String, String, String> {
        Context context;
        String s;

        public Background(Context context, String s) {
            this.context = context;
            this.s = s;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String phone = params[0];


            String message_url = new Ip(context).fetchstatus();
            if (message_url != null) {
                try {
                    URL url = new URL(message_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
                    BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                    String post_data = URLEncoder.encode("phone", "UTF-8") + "=" + URLEncoder.encode(phone, "UTF-8");
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
            if (result == null) {
                Toast.makeText(context, "Error Encountered\nCan't reach the Server", Toast.LENGTH_SHORT).show();
            } else {

                if (result != null && result.equalsIgnoreCase("Pending")) {

                    Toast.makeText(context, "You have a pending Transaction! wait for approval", Toast.LENGTH_SHORT).show();

                } else {
                    calculateDuration(s);
                }

            }

        }

    }

    private class FindTotal extends AsyncTask<String, Void, String> {

        Context context;
        String slot;

        public FindTotal(Context context, String slot) {
            this.context = context;
            this.slot = slot;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String hours = params[0];
            String phone = params[1];
            String message_url = new Ip(context).fetchcharges();
            if (message_url != null) {
                try {
                    URL url = new URL(message_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
                    BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                    String post_data = URLEncoder.encode("phone", "UTF-8") + "=" + URLEncoder.encode(phone, "UTF-8") + "&"
                            + URLEncoder.encode("hours", "UTF-8") + "=" + URLEncoder.encode(hours, "UTF-8");
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

//            gotoMpesa(s, slot);
            open_mpesa(s, slot);


        }

    }

}

