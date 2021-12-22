package com.executivestrokes.mcent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class StartActivity extends AppCompatActivity {
    private ViewPager sPager;
    private LinearLayout mLayout;
    private SliderAdapter sliderAdapter;
    private TextView[] mDots;
    private int mCurrentpage;
    Button next,prev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        sPager=findViewById(R.id.slidingPager);
        mLayout=findViewById(R.id.mlaYout);
        next=findViewById(R.id.next);
        prev=findViewById(R.id.back);
        sliderAdapter=new SliderAdapter(this);
        sPager.setAdapter(sliderAdapter);
        addDotsIndicator(0);
        sPager.addOnPageChangeListener(viewListner);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentpage==2){
                    Intent intent=new Intent(StartActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    try {
                        sPager.setCurrentItem(mCurrentpage+1);
                    }catch (Exception e){
                        Toast.makeText(StartActivity.this, "SKIP "+" <---"+ e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(StartActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    public void addDotsIndicator(int position){
        mDots=new TextView[3];
        mLayout.removeAllViews();
        for (int i=0;i<mDots.length;i++){
            mDots[i]=new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.colorPrimary));
            mLayout.addView(mDots[i]);
        }
        if(mDots.length>0){
            mDots[position].setTextColor(getResources().getColor(R.color.themeColour));
        }
    }
    ViewPager.OnPageChangeListener viewListner= new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
            mCurrentpage=position;
            if (position == 0){
                next.setEnabled(true);
                next.setText("Next");
                prev.setVisibility(View.VISIBLE);


            }
            else if (position == 2){
                next.setEnabled(true);
                next.setText("Finish");
                prev.setVisibility(View.INVISIBLE);


            }
            else{
                next.setEnabled(true);
                next.setText("Next");
                prev.setVisibility(View.VISIBLE);


            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}