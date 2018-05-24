package dsardy.in.memorybattle;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import dsardy.in.memorybattle.managers.SharedPrefManager;

public class IntroActivity extends AppCompatActivity {

    LottieAnimationView animationView;
    FloatingActionButton fabNext;
    TextView mTextTuto;
    int pauseCount = 0;
    private SharedPrefManager mSharedPref;
    ImageView mIngBack;
    TextView mTxtSkip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        animationView = findViewById(R.id.animation_view);
        fabNext = findViewById(R.id.fabNextIntro);
        mTextTuto = findViewById(R.id.textViewtut);
        mTxtSkip = findViewById(R.id.textViewSkip);
        mIngBack = findViewById(R.id.imageViewBack);

        mSharedPref = new SharedPrefManager(getApplicationContext());

        if (mSharedPref.getIsIntroWatched() == 0) {
            mIngBack.setVisibility(View.INVISIBLE);
        }

        animationView.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //mTextTuto.setText(valueAnimator.getCurrentPlayTime() + "");

                if (pauseCount == 0) {
                    if (valueAnimator.getCurrentPlayTime() / 100 == 63) {
                        animationView.pauseAnimation();
                        pauseCount = pauseCount + 1;
                        fabNext.setVisibility(View.VISIBLE);
                        fabNext.setVisibility(View.VISIBLE);

                    }
                }
                if (pauseCount == 1) {
                    if (valueAnimator.getCurrentPlayTime() / 100 == 69) {
                        animationView.pauseAnimation();
                        pauseCount = pauseCount + 1;
                        fabNext.setVisibility(View.VISIBLE);
                    }
                }
                if (pauseCount == 2) {
                    if (valueAnimator.getCurrentPlayTime() / 100 == 80) {
                        animationView.pauseAnimation();
                        pauseCount = pauseCount + 1;
                        fabNext.setVisibility(View.VISIBLE);
                    }
                }


            }
        });

        fabNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                animationView.resumeAnimation();
                fabNext.setVisibility(View.INVISIBLE);

                if (pauseCount == 1) {
                    mTextTuto.setText(R.string.tut2);
                }

                if (pauseCount == 2) {
                    mTextTuto.setText(R.string.tut3);
                }

                if(pauseCount == 3){
                    mSharedPref.setIsIntroWatched(1);
                    if(mSharedPref.getMyName().isEmpty()){
                        startActivity(new Intent(getApplicationContext(),UserProfileActivity.class));
                    }
                    finish();
                }
            }
        });

        mIngBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animationView.cancelAnimation();
                finish();
            }
        });

        mTxtSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animationView.cancelAnimation();
                mSharedPref.setIsIntroWatched(1);
                if(mSharedPref.getMyName().isEmpty()){
                    startActivity(new Intent(getApplicationContext(),UserProfileActivity.class));
                }
                finish();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        pauseCount = 0;
    }
}
