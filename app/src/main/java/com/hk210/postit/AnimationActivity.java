package com.hk210.postit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageHelper;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class AnimationActivity extends AppCompatActivity {

    private OnboardingAdapter onboardingAdapter;
    private LinearLayout onboardingIndicators;
    private Button button;
   // private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);
        getSupportActionBar().hide();


        setupOnboardingItems();
        onboardingIndicators = findViewById(R.id.onBoardingIndicator);
        button = findViewById(R.id.onBoardingButton);

        final ViewPager2 onboardingViewPager  = findViewById(R.id.onBoardingViewPager);
        onboardingViewPager.setAdapter(onboardingAdapter);




        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onboardingViewPager.getCurrentItem() + 1 < onboardingAdapter.getItemCount()){
                    onboardingViewPager.setCurrentItem(onboardingViewPager.getCurrentItem() + 1);
                }
                else{
                    startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                    finish();
                }

            }
        });

        setOnboardingIndicators();
        setCurrentOnboardingindicators(0);
        onboardingViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentOnboardingindicators(position);
            }
        });

    }

    private void setupOnboardingItems(){
        List<OnboardingItem> onboardingItems = new ArrayList<>();
        OnboardingItem camera = new OnboardingItem();
        camera.setTitle("Post your memories");
        camera.setDes("Anyone can share their memories of beautiful times, they will be shared to your loved ones instantly");
        camera.setImage(R.drawable.post);

        OnboardingItem plane = new OnboardingItem();
        plane.setTitle("World tour posts");
        plane.setDes("Share your world tour images to your friends. Global insiprations for others");
        plane.setImage(R.drawable.share);

        OnboardingItem friend = new OnboardingItem();
        friend.setTitle("News feed");
        friend.setDes("On PostIT you can follow your friends to see their updates. And they can see your too.");
        friend.setImage(R.drawable.news);

        onboardingItems.add(camera);
        onboardingItems.add(plane);
        onboardingItems.add(friend);

        onboardingAdapter = new OnboardingAdapter(onboardingItems);
    }
    private void setOnboardingIndicators(){
        ImageView[] indicators = new ImageView[onboardingAdapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(8,0,8,0);
        for(int i=0; i<indicators.length;i++){
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.onboarding_inactive));
            indicators[i].setLayoutParams(layoutParams);
            onboardingIndicators.addView(indicators[i]);
        }
    }
    private void setCurrentOnboardingindicators(int index){
        int childcount = onboardingIndicators.getChildCount();
        for(int i=0; i<childcount; i++){
            ImageView image = (ImageView) onboardingIndicators.getChildAt(i);
            if(i == index){
                image.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.onboarding_active));
            }
            else{
                image.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.onboarding_inactive));
            }
        }
        if(index  == onboardingAdapter.getItemCount() - 1){
            button.setText("Start");
        }
        else{
            button.setText("Next");
        }
    }

}