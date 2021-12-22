package com.executivestrokes.mcent;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends AppCompatActivity {
    private static int SPLASH_SCREEN=1000;
    Animation bottomAnim, topAnim;
    ImageView imageView;

    TextView name,desc;
    int k=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);
        //Animations
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            k=1;
        }



        //Hooks

        name = findViewById(R.id.textView);
//        desc = findViewById(R.id.textView2);
//        imageView=findViewById(R.id.buildings);


        name.setAnimation(topAnim);
//        desc.setAnimation(topAnim);
//        imageView.setAnimation(bottomAnim);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(k==1)
                {
                    Intent intent=new Intent(SplashScreen.this, Dashboard.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                else{
                    Intent intent=new Intent(SplashScreen.this,StartActivity.class);
                    startActivity(intent);
                }
            }
        },SPLASH_SCREEN);

    }
}