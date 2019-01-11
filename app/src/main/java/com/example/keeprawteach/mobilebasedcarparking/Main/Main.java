package com.example.keeprawteach.mobilebasedcarparking.Main;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keeprawteach.mobilebasedcarparking.Calling.Calling;
import com.example.keeprawteach.mobilebasedcarparking.Database.Database;
import com.example.keeprawteach.mobilebasedcarparking.Fragments.Home;
import com.example.keeprawteach.mobilebasedcarparking.Fragments.ReservationHolder;
import com.example.keeprawteach.mobilebasedcarparking.Fragments.Search;
import com.example.keeprawteach.mobilebasedcarparking.Fragments.Settings;
import com.example.keeprawteach.mobilebasedcarparking.Fragments.User;
import com.example.keeprawteach.mobilebasedcarparking.R;

import java.io.ByteArrayInputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class Main extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FloatingActionButton Searchfab;

    private ImageView MaleCalls;

    Database sqLitedb;

    private TextView Email, Name;

    private CircleImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sqLitedb = new Database(this);

        MaleCalls = (ImageView) findViewById(R.id.makecall);

        MaleCalls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Main.this, Calling.class);

                startActivity(intent);

            }
        });

        Searchfab = (FloatingActionButton) findViewById(R.id.fab);
        Searchfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setTitle("Find a Parking");

                Search log = new Search();

                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

                fragmentTransaction.replace(R.id.Drawer, log, "Home");

                fragmentTransaction.commit();

                Searchfab.setVisibility(View.VISIBLE);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View view = navigationView.getHeaderView(0);

        Email = (TextView) view.findViewById(R.id.email);

        Name = (TextView) view.findViewById(R.id.name);

        imageView = (CircleImageView) view.findViewById(R.id.bnm);


        setTitle("Eldo Park ");

        Home log = new Home();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.Drawer, log, "Home");

        fragmentTransaction.commit();

        Searchfab.setVisibility(View.VISIBLE);

        load();
    }


    private void load() {


        String user = "1";

        Cursor cursor = sqLitedb.searchEmail();

        if (cursor != null && cursor.getCount() > 0) {

            while (cursor.moveToNext()) {

                String d = cursor.getString(0);

                String fn = cursor.getString(1);

                String ln = cursor.getString(2);

                String ph = cursor.getString(3);

                String em = cursor.getString(4);

                byte[] blob = cursor.getBlob(8);

                if (user.equalsIgnoreCase(d)) {

                    Name.setText(fn + "  " + ln);

                    Email.setText(em);

                    if (blob==null){

                    }else {

                        ByteArrayInputStream inputStream = new ByteArrayInputStream(blob);

                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                        imageView.setImageBitmap(bitmap);
                    }
                }else {
                    Toast.makeText(this, "No Data found", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            leavetheapp();

//            super.onBackPressed();
        }
    }


    public void leavetheapp() {

        AlertDialog.Builder al = new AlertDialog.Builder(this);
        al.setMessage("Are you sure You want to Exit?");
        al.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                    finish();

                fullyexit();


            }
        });
        al.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alertDialog = al.create();
        alertDialog.show();
    }

    private void fullyexit() {

        AlertDialog.Builder al = new AlertDialog.Builder(this);
        al.setMessage("Are you Really sure You want to Exit?");

        al.setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        al.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog alertDialog = al.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            setTitle("Settings");

            Settings log = new Settings();

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

            fragmentTransaction.replace(R.id.Drawer, log, "Home");

            fragmentTransaction.commit();

            Searchfab.setVisibility(View.GONE);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            setTitle("Eldo Park ");

            Home log = new Home();

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

            fragmentTransaction.replace(R.id.Drawer, log, "Home");

            fragmentTransaction.commit();

            Searchfab.setVisibility(View.VISIBLE);

            load();

        } else if (id == R.id.nav_gallery) {



            setTitle("Find a Parking");

            Search log = new Search();

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

            fragmentTransaction.replace(R.id.Drawer, log, "Home");

            fragmentTransaction.commit();

            Searchfab.setVisibility(View.VISIBLE);

        } else if (id == R.id.nav_slideshow) {


            setTitle("Reservations");

            ReservationHolder log = new ReservationHolder();

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

            fragmentTransaction.replace(R.id.Drawer, log, "Home");

            fragmentTransaction.commit();

            Searchfab.setVisibility(View.GONE);

        } else if (id == R.id.nav_manage) {


            setTitle("Account");

            User log = new User();

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

            fragmentTransaction.replace(R.id.Drawer, log, "Home");

            fragmentTransaction.commit();

            Searchfab.setVisibility(View.GONE);

        } else if (id == R.id.nav_share) {
            openSharing();

        } else if (id == R.id.nav_send) {


            setTitle("EldoPark ");

            Home log = new Home();

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

            fragmentTransaction.replace(R.id.Drawer, log, "Home");

            fragmentTransaction.commit();

            Searchfab.setVisibility(View.VISIBLE);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void openSharing() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater lay = LayoutInflater.from(this);

        final View v = lay.inflate(R.layout.share, null);

        CircleImageView FACEBOOK, WHATSAPP, GOOGLEPLUS, TWITTER;

        FACEBOOK = (CircleImageView) v.findViewById(R.id.aa);

        WHATSAPP = (CircleImageView) v.findViewById(R.id.bb);

        GOOGLEPLUS = (CircleImageView) v.findViewById(R.id.cc);

        TWITTER = (CircleImageView) v.findViewById(R.id.dd);

        FACEBOOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                try {
                    Intent jeff = new Intent(Intent.ACTION_SEND);

                    jeff.setType("text/plain");

                    String url = "http//keeprawteachjaphetth.000webhostapp.com";

                    jeff.putExtra(Intent.EXTRA_TEXT, url);

                    jeff.setPackage("com.facebook.katana");

                    startActivity(jeff);

                } catch (Exception e) {

                    Toast.makeText(Main.this, "please Install Facebook App", Toast.LENGTH_SHORT).show();
                }
            }
        });
        WHATSAPP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                try {
                    Intent jeff = new Intent(Intent.ACTION_SEND);

                    jeff.setType("text/plain");

                    String url = "http//keeprawteachjaphetth.000webhostapp.com";

                    jeff.putExtra(Intent.EXTRA_TEXT, url);

                    jeff.setPackage("com.whatsapp");

                    startActivity(jeff);

                } catch (Exception e) {

                    Toast.makeText(Main.this, "please Install Whatsapp App", Toast.LENGTH_SHORT).show();
                }
            }
        });
        TWITTER.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                try {
                    Intent jeff = new Intent(Intent.ACTION_SEND);

                    jeff.setType("text/plain");

                    String url = "http//keeprawteachjaphetth.000webhostapp.com";

                    jeff.putExtra(Intent.EXTRA_TEXT, url);

                    jeff.setPackage("advanced.twitter.android");

                    startActivity(jeff);

                } catch (Exception e) {

                    Toast.makeText(Main.this, "please Install Twitter App", Toast.LENGTH_SHORT).show();
                }
            }
        });
        GOOGLEPLUS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                try {
                    Intent jeff = new Intent(Intent.ACTION_SEND);

                    jeff.setType("text/plain");

                    String url = "http//keeprawteachjaphetth.000webhostapp.com";

                    jeff.putExtra(Intent.EXTRA_TEXT, url);

                    jeff.setPackage("com.google.android.apps.plus");

                    startActivity(jeff);

                } catch (Exception e) {

                    Toast.makeText(Main.this, "please Install Google-Plus App", Toast.LENGTH_SHORT).show();
                }
            }
        });


        builder.setView(v);

        AlertDialog al = builder.create();

        al.show();
    }
}
