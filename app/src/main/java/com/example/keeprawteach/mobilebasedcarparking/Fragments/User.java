package com.example.keeprawteach.mobilebasedcarparking.Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keeprawteach.mobilebasedcarparking.Database.Database;
import com.example.keeprawteach.mobilebasedcarparking.Ip.Ip;
import com.example.keeprawteach.mobilebasedcarparking.R;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class User extends Fragment {

    private static final int CAMERA_REQUEST = 1888;

    Database database;

    AlertDialog al;

    Bitmap bitmap;

    boolean check = true;
    AutoCompleteTextView AAAA;
    AutoCompleteTextView BBBB;
    private CircleImageView circleImageView;
    private TextView AA, BB, CC, DD;
    private FloatingActionButton floatingActionButton;

    public User() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        database = new Database(getContext());

        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab);

        circleImageView = (CircleImageView) view.findViewById(R.id.aaa);

        AA = (TextView) view.findViewById(R.id.Email);

        BB = (TextView) view.findViewById(R.id.Phone);

        CC = (TextView) view.findViewById(R.id.car);

        DD = (TextView) view.findViewById(R.id.Name);

        loadUser();

        actionListener();

        return view;


    }

    private void actionListener() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBoom();
            }
        });
    }

    private void openBoom() {

        PopupMenu popupMenu = new PopupMenu(getContext(), floatingActionButton);

        popupMenu.getMenuInflater().inflate(R.menu.user, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                int id = menuItem.getItemId();

                if (id == R.id.profile) {
                    selection();

                } else if (id == R.id.account) {

                } else if (id == R.id.car) {
                    carDetails();

                }
                return false;
            }
        });

        popupMenu.show();
    }

    private void selection() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Select");

        builder.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                open_gallery();

            }
        });
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();


            }
        });
        builder.setNegativeButton("Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                open_camera();

            }
        });
        builder.show();
    }


    private void open_gallery() {

        Intent intent = new Intent();

        intent.setType("image/*");

        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Select Image From Gallery"), 1);

    }

    private void open_camera() {

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            bitmap = (Bitmap) data.getExtras().get("data");
            circleImageView.setImageBitmap(bitmap);
            upload();
//            save_to_sqlite();
        } else {
            if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
                Uri uri = data.getData();

                try {

                    bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);

                    circleImageView.setImageBitmap(bitmap);

                    upload();
//                    save_to_sqlite();
                } catch (IOException e) {

                    e.printStackTrace();
                }

            }
        }

    }

    private void save_to_sqlite() {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

        byte[] byteArray = stream.toByteArray();

        String id = "1";

        boolean isInserted = database.updateProfele(byteArray, id);

        if (isInserted == true) {

            Snackbar.make(AA, "Profile updated Successfully", Snackbar.LENGTH_LONG).setAction("Action", null).show();

            loadUser();

        } else {

            Snackbar.make(AA, "Failed", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }

    }

    private String getUsername() {

        String jeff = "";

        String user = "1";

        Cursor cursor = database.searchEmail();

        if (cursor != null && cursor.getCount() > 0) {

            while (cursor.moveToNext()) {

                String d = cursor.getString(0);

                String e = cursor.getString(4);

                if (user.equalsIgnoreCase(d)) {
                    jeff = e;
                    return jeff;
                }

            }
        }
        return jeff;
    }

    private void upload() {

        ByteArrayOutputStream byteArrayOutputStreamObject;

        byteArrayOutputStreamObject = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStreamObject);

        byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();

        final String ConvertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);

        class AsyncTaskUploadClass extends AsyncTask<Void, Void, String> {

            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {

                super.onPreExecute();

                progressDialog = ProgressDialog.show(getContext(), "Uploading Image", "Please Wait", false, false);
            }

            @Override
            protected void onPostExecute(String string1) {

                super.onPostExecute(string1);

                // Dismiss the progress dialog after done uploading.
                progressDialog.dismiss();

                // Printing uploading success message coming from server on android app.
                if (string1 != null && string1.equalsIgnoreCase("Profile Uploaded Successfully")) {

                    savetosqlite();
                    save_to_sqlite();

                } else {
                    Toast.makeText(getContext(), string1, Toast.LENGTH_LONG).show();
                }
                Toast.makeText(getContext(), string1, Toast.LENGTH_LONG).show();


            }

            private void savetosqlite() {

                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                byte[] byteArray = stream.toByteArray();

                String id = "1";

                boolean isInserted = database.updateProfile(byteArray, id);

                if (isInserted == true) {

                    Snackbar.make(circleImageView, "Profile Updated Successfully", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                } else {

                    Snackbar.make(circleImageView, "Failed", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                }
            }

            @Override
            protected String doInBackground(Void... params) {

                String ImagePath = "image_path";

                String ImageName = "image_name";

                String user = getUsername();


                String ServerUploadPath = new Ip(getContext()).uploadProfile();

                if (ServerUploadPath != null) {

                    ImageProcessClass imageProcessClass = new ImageProcessClass();

                    HashMap<String, String> HashMapParams = new HashMap<String, String>();


                    HashMapParams.put(ImageName, user);

                    HashMapParams.put(ImagePath, ConvertImage);

                    String FinalData = imageProcessClass.ImageHttpRequest(ServerUploadPath, HashMapParams);

                    return FinalData;
                }
                return null;
            }
        }

        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();

        AsyncTaskUploadClassOBJ.execute();

    }

    private void carDetails() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater lay = LayoutInflater.from(getContext());

        final View viewdata = lay.inflate(R.layout.usercar, null);

        String[] cartype = {"Private", "Public", "Truck"};

        AAAA = (AutoCompleteTextView) viewdata.findViewById(R.id.aa);

        BBBB = (AutoCompleteTextView) viewdata.findViewById(R.id.bb);

        final Button CC = (Button) viewdata.findViewById(R.id.Cancel);

        final Button DD = (Button) viewdata.findViewById(R.id.Update);


        ArrayAdapter<String> array = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, cartype);

        BBBB.setAdapter(array);


        CC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                al.dismiss();
            }
        });
        DD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkInput();
            }
        });

        builder.setView(viewdata);

        al = builder.create();

        al.show();
    }

    private void checkInput() {

        final String A, B;

        A = AAAA.getText().toString();

        B = BBBB.getText().toString();

        if (A.isEmpty()) {

            AAAA.setError("Enter car Number");

            AAAA.requestFocus();

        } else if (B.isEmpty()) {

            BBBB.setError("Enter car Type");

            BBBB.requestFocus();
        } else {
            UpdateCar(A, B);
        }
    }

    private void UpdateCar(String a, String b) {

        al.dismiss();

        String usermail = getUsername();

        Update update = new Update(getContext(),a, b, usermail);

        update.execute(a, b, usermail);
    }

    private void loadUser() {


        String user = "1";

        Cursor cursor = database.searchEmail();

        if (cursor != null && cursor.getCount() > 0) {

            while (cursor.moveToNext()) {

                String d = cursor.getString(0);

                String fn = cursor.getString(1);

                String ln = cursor.getString(2);

                String ph = cursor.getString(3);

                String em = cursor.getString(4);

                String car = cursor.getString(6);

                byte[] blob = cursor.getBlob(8);


                if (user.equalsIgnoreCase(d)) {

                    AA.setText(em);

                    BB.setText(ph);

                    CC.setText(car);

                    DD.setText(fn + "  " + ln);

                    if (blob == null) {

                    } else {

                        ByteArrayInputStream inputStream = new ByteArrayInputStream(blob);

                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                        circleImageView.setImageBitmap(bitmap);
                    }

                } else {
                    Toast.makeText(getContext(), "No Data found", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private class ImageProcessClass {
        public String ImageHttpRequest(String requestURL, HashMap<String, String> PData) {

            StringBuilder stringBuilder = new StringBuilder();

            try {

                URL url;
                HttpURLConnection httpURLConnectionObject;
                OutputStream OutPutStream;
                BufferedWriter bufferedWriterObject;
                BufferedReader bufferedReaderObject;
                int RC;

                url = new URL(requestURL);

                httpURLConnectionObject = (HttpURLConnection) url.openConnection();

                httpURLConnectionObject.setReadTimeout(19000);

                httpURLConnectionObject.setConnectTimeout(19000);

                httpURLConnectionObject.setRequestMethod("POST");

                httpURLConnectionObject.setDoInput(true);

                httpURLConnectionObject.setDoOutput(true);

                OutPutStream = httpURLConnectionObject.getOutputStream();

                bufferedWriterObject = new BufferedWriter(

                        new OutputStreamWriter(OutPutStream, "UTF-8"));

                bufferedWriterObject.write(bufferedWriterDataFN(PData));

                bufferedWriterObject.flush();

                bufferedWriterObject.close();

                OutPutStream.close();

                RC = httpURLConnectionObject.getResponseCode();

                if (RC == HttpsURLConnection.HTTP_OK) {

                    bufferedReaderObject = new BufferedReader(new InputStreamReader(httpURLConnectionObject.getInputStream()));

                    stringBuilder = new StringBuilder();

                    String RC2;

                    while ((RC2 = bufferedReaderObject.readLine()) != null) {

                        stringBuilder.append(RC2);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();

        }

        private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {

            StringBuilder stringBuilderObject;

            stringBuilderObject = new StringBuilder();

            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {

                if (check)

                    check = false;
                else
                    stringBuilderObject.append("&");

                stringBuilderObject.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));

                stringBuilderObject.append("=");

                stringBuilderObject.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
            }

            return stringBuilderObject.toString();
        }
    }

    private class Update extends AsyncTask<String, String, String> {

        Context context;

        ProgressDialog progressDialog;

        String a,b,c;

        public Update(Context context, String a, String b, String c) {
            this.context = context;
            this.a = a;
            this.b = b;
            this.c = c;
        }

        public Update(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(context);

            progressDialog.setMessage("Updating....");

            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            String carname = params[0];

            String cartype = params[1];

            String user = params[2];


            String sign_url = new Ip(context).updatecar();

            if (sign_url != null) {

                try {

                    URL url = new URL(sign_url);

                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                    httpURLConnection.setRequestMethod("POST");

                    httpURLConnection.setDoOutput(true);

                    OutputStream outputStream = httpURLConnection.getOutputStream();

                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");

                    BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

                    String post_data = URLEncoder.encode("carname", "UTF-8") + "=" + URLEncoder.encode(carname, "UTF-8") + "&"
                            + URLEncoder.encode("cartype", "UTF-8") + "=" + URLEncoder.encode(cartype, "UTF-8") + "&"
                            + URLEncoder.encode("user", "UTF-8") + "=" + URLEncoder.encode(user, "UTF-8");

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

            if (s == null) {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
            } else {
                if (s != null && s.equalsIgnoreCase("Updated Successfully")) {

//                    Toast.makeText(context, "" + s, Toast.LENGTH_SHORT).show();
                    save_car_sqlite(a,b);
                } else {
                    Toast.makeText(context, "" + s, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void save_car_sqlite(String a, String b) {

        String id = "1";

        boolean isInserted = database.updateCar(a,b, id);

        if (isInserted == true) {

            Snackbar.make(AA, "Car updated Successfully", Snackbar.LENGTH_LONG).setAction("Action", null).show();

            loadUser();

        } else {

            Snackbar.make(AA, "Failed", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }

    }
}
