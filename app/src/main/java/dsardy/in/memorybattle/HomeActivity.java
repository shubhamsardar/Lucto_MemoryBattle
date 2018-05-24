package dsardy.in.memorybattle;

import android.animation.ValueAnimator;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import dsardy.in.memorybattle.managers.SharedPrefManager;

public class HomeActivity extends AppCompatActivity {

    FloatingActionButton fabInfo, fabRate, fabShare, fabIntro, fabPlay;
    TextView txtPoints, txtMatches;
    SharedPrefManager mSharedPref;
    Context mContext;
    LottieAnimationView animationView;
    private float[] hsvColor = {0, 1, 1};
    ImageView pointsico, battlesiconn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mContext = getApplicationContext();
        mSharedPref = new SharedPrefManager(mContext);

        if (mSharedPref.getIsIntroWatched() == 0) {
            startActivity(new Intent(mContext, IntroActivity.class));
        } else {
            if (mSharedPref.getMyName().isEmpty()) {
                startActivity(new Intent(mContext, UserProfileActivity.class));
            }
        }

        initView();


        animationView.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                hsvColor[0] = 360f * valueAnimator.getAnimatedFraction();

                // pointsico.setColorFilter(Color.HSVToColor(hsvColor));
                battlesiconn.setColorFilter(Color.HSVToColor(hsvColor));
                //fabPlay.setColorFilter(Color.HSVToColor(hsvColor));

            }
        });

        fabPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOnline()) {
                    animationView.pauseAnimation();
                    startActivity(new Intent(mContext, ConnectWithQrActivity.class));
                } else {
                    Toast.makeText(mContext, "Not Connected! Try Again.", Toast.LENGTH_LONG).show();
                }
            }
        });
        fabIntro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animationView.pauseAnimation();
                startActivity(new Intent(mContext, IntroActivity.class));
            }
        });
        fabRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //rate the app
                animationView.pauseAnimation();
                launchMarket();
            }
        });
        fabShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //share the app
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Hey check out LUCTO game: https://play.google.com/store/apps/details?id=dsardy.in.memorybattle");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });
        fabInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //developer info
                animationView.pauseAnimation();
                startActivity(new Intent(mContext, InfoActivity.class));
            }
        });

        animationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (animationView.isAnimating()) {
                    animationView.pauseAnimation();
                } else {
                    animationView.resumeAnimation();
                }
            }
        });

        if (animationView.isAnimating()) {
            animationView.pauseAnimation();
        } else {
            animationView.playAnimation();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        txtMatches.setText(mSharedPref.getMyMatches() + " Luctos..");
        txtPoints.setText(mSharedPref.getMyScore() + " Points..");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        animationView.cancelAnimation();

    }

    private void initView() {
        fabInfo = findViewById(R.id.floatingActionButtonInfo);
        fabIntro = findViewById(R.id.floatingActionButtonIntro);
        fabRate = findViewById(R.id.floatingActionButtonRate);
        fabShare = findViewById(R.id.floatingActionButtonShare);
        fabPlay = findViewById(R.id.floatingActionButtonPlay);
        txtMatches = findViewById(R.id.titleMatches);
        txtPoints = findViewById(R.id.titlePoints);
        animationView = findViewById(R.id.animation_view);
        pointsico = findViewById(R.id.imageViewBack);
        battlesiconn = findViewById(R.id.imageViewPiont2);

    }

    private void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, " unable to find app", Toast.LENGTH_LONG).show();
        }
    }

    public void goToProfile(View view) {
        animationView.pauseAnimation();
        startActivity(new Intent(mContext, UserProfileActivity.class));
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
