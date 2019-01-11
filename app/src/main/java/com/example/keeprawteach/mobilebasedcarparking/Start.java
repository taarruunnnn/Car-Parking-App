package com.example.keeprawteach.mobilebasedcarparking;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.keeprawteach.mobilebasedcarparking.Login.Login;

import de.hdodenhof.circleimageview.CircleImageView;

public class Start extends AppCompatActivity {

    CircleImageView circleImageView;

    Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        circleImageView = (CircleImageView) findViewById(R.id.aa);

        animation = AnimationUtils.loadAnimation(this, R.anim.rotate);


        openLoad();
    }

    private void openLoad() {

        new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {

                circleImageView.startAnimation(animation);

                recount();
            }
        }.start();
    }

    private void recount() {

        new CountDownTimer(8000, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {

                Intent intent=new Intent(Start.this, Login.class);

                startActivity(intent);

                finish();

                overridePendingTransition(R.anim.goup, R.anim.godown);
            }
        }.start();
    }

}
