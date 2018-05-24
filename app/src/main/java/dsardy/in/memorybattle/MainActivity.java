package dsardy.in.memorybattle;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import dsardy.in.memorybattle.managers.SharedPrefManager;
import dsardy.in.memorybattle.models.InputPojo;
import dsardy.in.memorybattle.viewholders.InputbuttonViewHolder;

import static android.R.attr.priority;

public class MainActivity extends AppCompatActivity {

    FirebaseRecyclerAdapter<InputPojo, InputbuttonViewHolder> mAdapter;
    DatabaseReference ref;
    long tapcount = 0;
    long mTapRank = 0;
    long rankCheck;
    long myIndex = 1;
    boolean isYourTurn = false;
    boolean isMatchOn = true;
    private String mYourName = "you";
    private String mOpponentName = "opponent";
    private String mGameId;
    private String mTitle;
    private ChildEventListener childEventListener;
    Query mRankQuery;
    private int pointsGain;
    private long totalPoints;
    private float[] hsvColor = {0, 1, 1};
    private Context mContext;
    LottieAnimationView animationView;


    private ImageView mReplayGame;
    private ImageView mPointsDot;
    private ImageView mCloseMatch;
    private RecyclerView gridRecyclarView;
    private TextView mTextGuide;
    private TextView mPoints;
    private TextView mWinnerText;
    private TextView mTitleText;
    // private FloatingActionButton mFabMute;
    // private FloatingActionButton mFabShare;
    // private FloatingActionButton mFabRate;
    private RelativeLayout mGameBg;
    private CountDownTimer mCountDown;
    private ProgressBar mProgress;
    private TextView mLoading;
    AlertDialog.Builder builder;


    private SoundPool mSoundPool;
    private final int MAX_STREAM = 10;
    private boolean soundLoaded = false;
    Bundle mBundleFromIntent;


    int gamestartSoundId;
    int nicetapSoundId;
    int rCorrectSoundId;
    int rWrongSoundId;
    int rLastSoundId;
    int theWinnerSoundId;
    int backgroundSoundId;
    int streamIdforBg;
    float vol;

    ValueEventListener whoseturnListner;
    ValueEventListener tapCountUpdate;
    ValueEventListener anyoneLostCheckListner;
    ValueEventListener pointsUpdateListner;
    ValueEventListener haveSomeOneCloase;


    private SharedPrefManager mSharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        mSharedPref = new SharedPrefManager(mContext);

        //from Intent
        mBundleFromIntent = getIntent().getExtras();
        mGameId = mBundleFromIntent.getString("gameId");
        mOpponentName = mBundleFromIntent.getString("opponentName");
        myIndex = mBundleFromIntent.getInt("mIndex", 2);
        mYourName = mSharedPref.getMyName();
        mTitle = mYourName + " Vs " + mOpponentName;
        Log.e("game id", mGameId);


        //sound pool
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            mSoundPool = new SoundPool.Builder()
                    .setAudioAttributes(attributes)
                    .setMaxStreams(MAX_STREAM)
                    .build();
        } else {

            mSoundPool = new SoundPool(MAX_STREAM, AudioManager.STREAM_MUSIC, 0);
        }
        gamestartSoundId = mSoundPool.load(this, R.raw.gamestart, priority);
        nicetapSoundId = mSoundPool.load(this, R.raw.gamestart, priority);
        rCorrectSoundId = mSoundPool.load(this, R.raw.responcecorrect, priority);
        rWrongSoundId = mSoundPool.load(this, R.raw.responcewrong, priority);
        rLastSoundId = mSoundPool.load(this, R.raw.responcelast, priority);
        theWinnerSoundId = mSoundPool.load(this, R.raw.thewinner, priority);
        backgroundSoundId = mSoundPool.load(this, R.raw.gamebg, priority);

        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int soundId, int status) {
                if (status == 0) {
                    soundLoaded = true;
                }
            }
        });


        setContentView(R.layout.activity_main);

        initView();
        ref = FirebaseDatabase.getInstance().getReference().child("activeGames");
        initFirebaseNode();

        builder = new AlertDialog.Builder(this);

        mAdapter = new FirebaseRecyclerAdapter<InputPojo, InputbuttonViewHolder>(
                InputPojo.class,
                R.layout.gridunit,
                InputbuttonViewHolder.class,
                ref.child(mGameId).child("values")) {
            @Override
            protected void populateViewHolder(final InputbuttonViewHolder viewHolder, InputPojo model, final int position) {

                if (model.getmTapCount() == tapcount) {

                    if (model.getmStatus() == 1) {
                        //correct responce
                        if (!isYourTurn) {
                            mSoundPool.play(rCorrectSoundId, 1, 1, 1, 0, 1);
                        }
                        viewHolder.floatingActionButton
                                .setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(),
                                        R.color.colorAccent)));
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Do something after 5s = 5000ms
                                viewHolder.floatingActionButton
                                        .setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(),
                                                R.color.colorPrimaryDark)));

                            }
                        }, 500);
                    }
                    if (model.getmStatus() == 2) {
                        //incorrect recponce
                        if (!isYourTurn) {
                            mSoundPool.play(rWrongSoundId, 1, 1, 1, 0, 1);
                        }
                        viewHolder.floatingActionButton
                                .setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(),
                                        R.color.colorWrongInput)));
                    }
                    if (model.getmStatus() == 3) {
                        //last responce
                        if (!isYourTurn) {
                            mSoundPool.play(rLastSoundId, 1, 1, 1, 0, 1);
                        }
                        viewHolder.floatingActionButton
                                .setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(),
                                        R.color.colorLastInput)));
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Do something after 5s = 5000ms
                                viewHolder.floatingActionButton
                                        .setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(),
                                                R.color.colorPrimaryDark)));

                            }
                        }, 1000);
                    }
                    if (model.getmStatus() == 4) {
                        //expected correct responce
                        viewHolder.floatingActionButton
                                .setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(),
                                        R.color.colorAccent)));
                    }
                    if (model.getmStatus() == 5) {
                        //MatchDraw
                        viewHolder.floatingActionButton
                                .setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(),
                                        R.color.colorLastInput)));
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Do something after 5s = 5000ms
                                viewHolder.floatingActionButton
                                        .setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(),
                                                R.color.colorPrimaryDark)));

                            }
                        }, 1000);
                        mSoundPool.stop(streamIdforBg);
                        mSoundPool.play(theWinnerSoundId, 1, 1, 1, 0, 1);
                        mTextGuide.setText("BRAVO!! its A tie, Hit replay!");
                        mWinnerText.setText("points goes to RAJNIKANTH!");
                        mReplayGame.setVisibility(View.VISIBLE);
                        mGameBg.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorDotsbgWon));
                        isMatchOn = false;
                    }


                }
                if (model.getmTapCount() == -1) {
                    viewHolder.floatingActionButton
                            .setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(),
                                    R.color.colorLightGrey)));
                }


            }

            @Override
            public void onBindViewHolder(final InputbuttonViewHolder viewHolder, final int position) {
                super.onBindViewHolder(viewHolder, position);

                viewHolder.floatingActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isMatchOn) {
                            if (isYourTurnToPlay()) {
                                tapcount = tapcount + 1;
                                mTapRank = mTapRank + 1;
                                viewHolder.floatingActionButton
                                        .setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(),
                                                R.color.colorPrimary)));
                                if (!isLastResponce()) {

                                    ref.child(mGameId).child("values").child("" + viewHolder.getAdapterPosition()).child("mRank").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            rankCheck = (long) dataSnapshot.getValue();
                                            if (rankCheck == mTapRank) {
                                                //responce right
                                                mCountDown.cancel();
                                                totalPoints = totalPoints + pointsGain;
                                                ref.child(mGameId).child("totalPoints").setValue(totalPoints);
                                                mProgress.setProgress(0);

                                                ref.child(mGameId).child("values").child("" + viewHolder.getAdapterPosition())
                                                        .child("mStatus").setValue(1);
                                                ref.child(mGameId).child("tapcount").setValue(tapcount);
                                                ref.child(mGameId).child("values").child("" + viewHolder.getAdapterPosition())
                                                        .child("mTapCount").setValue(tapcount);

                                                pointsGain = 100;
                                                mCountDown.start();

                                            } else {
                                                //responce incorrect
                                                mSoundPool.play(rWrongSoundId, 1, 1, 1, 0, 1);
                                                mCountDown.cancel();
                                                mProgress.setProgress(0);
                                                ref.child(mGameId).child("values").child("" + viewHolder.getAdapterPosition())
                                                        .child("mStatus").setValue(2);
                                                ref.child(mGameId).child("tapcount").setValue(tapcount);
                                                ref.child(mGameId).child("values").child("" + viewHolder.getAdapterPosition())
                                                        .child("mTapCount").setValue(tapcount);

                                                mRankQuery = ref.child(mGameId).child("values").orderByChild("mRank").equalTo(mTapRank);

                                                childEventListener = new ChildEventListener() {
                                                    @Override
                                                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                                        Log.e("on incorrect tap", dataSnapshot.toString());
                                                        InputPojo p = dataSnapshot.getValue(InputPojo.class);
                                                        ref.child(mGameId).child("values").child("" + p.getmKey())
                                                                .child("mStatus").setValue(4);
                                                        ref.child(mGameId).child("values").child("" + p.getmKey())
                                                                .child("mTapCount").setValue(tapcount);
                                                        mRankQuery.removeEventListener(childEventListener);
                                                    }

                                                    @Override
                                                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                                    }

                                                    @Override
                                                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                                                    }

                                                    @Override
                                                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                };

                                                mRankQuery.addChildEventListener(childEventListener);

                                                ref.child(mGameId).child("lostIndex").setValue(myIndex);

                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                } else {
                                    //if last responce
                                    Log.e("respnce...", "last");

                                    ref.child(mGameId).child("values").child("" + viewHolder.getAdapterPosition())
                                            .child("mRank").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if ((long) dataSnapshot.getValue() == 0) {

                                                mCountDown.cancel();
                                                totalPoints = totalPoints + pointsGain;
                                                ref.child(mGameId).child("totalPoints").setValue(totalPoints);
                                                pointsGain = 100;
                                                mProgress.setProgress(0);

                                                long iValue;
                                                if (myIndex == 1) {
                                                    iValue = 2;
                                                } else {
                                                    iValue = 1;
                                                }
                                                ref.child(mGameId).child("mIndex").setValue(iValue);

                                                if (tapcount == 325) {
                                                    ref.child(mGameId).child("values").child("" + viewHolder.getAdapterPosition())
                                                            .child("mStatus").setValue(5);
                                                } else {
                                                    ref.child(mGameId).child("values").child("" + viewHolder.getAdapterPosition())
                                                            .child("mStatus").setValue(3);
                                                }
                                                ref.child(mGameId).child("tapcount").setValue(tapcount);
                                                ref.child(mGameId).child("values").child("" + viewHolder.getAdapterPosition())
                                                        .child("mRank").setValue(mTapRank);
                                                ref.child(mGameId).child("values").child("" + viewHolder.getAdapterPosition())
                                                        .child("mTapCount").setValue(tapcount);


                                            } else {
                                                //selected from already tapped point
                                                viewHolder.floatingActionButton
                                                        .setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(),
                                                                R.color.colorPrimaryDark)));
                                                mTextGuide.setText("select new one from white points!");
                                                tapcount = tapcount - 1;
                                                mTapRank = mTapRank - 1;
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });


                                }

                            } else {
                                //if not your turn
                                Log.e("respnce...", "not your turn");
                                mTextGuide.setText("its not your turn yet! let " + mOpponentName + " finish");

                            }

                        } else {
                            //start a new game
                            mTextGuide.setText("this game is done, start a new one!");
                        }


                    }
                });
            }

        };

        gridRecyclarView.setAdapter(mAdapter);


        whoseturnListner = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mTapRank = 0;
                isYourTurn = myIndex == (long) dataSnapshot.getValue();
                SpannableStringBuilder sb = new SpannableStringBuilder(mTitle);
                final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
                if (isYourTurn) {
                    streamIdforBg = mSoundPool.play(backgroundSoundId, 1, 1, 1, -1, 1);
                    animationView.resumeAnimation();

                    mTextGuide.setText("Its Your Turn to play " + mYourName + "!");
                    sb.setSpan(bss, mTitle.indexOf(mYourName), mTitle.indexOf(mYourName) + mYourName.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    mTitleText.setText(sb);
                    mGameBg.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorDotsbg));
                    pointsGain = 100;
                    mCountDown.start();


                } else {

                    animationView.pauseAnimation();
                    mSoundPool.stop(streamIdforBg);
                    mTextGuide.setText(mOpponentName + " is playing...");
                    sb.setSpan(bss, mTitle.indexOf(mOpponentName), mTitle.indexOf(mOpponentName) + mOpponentName.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    mTitleText.setText(sb);
                    mGameBg.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorDotsbg2));


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        ref.child(mGameId).child("mIndex").addValueEventListener(whoseturnListner);


        tapCountUpdate = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                tapcount = (long) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        ref.child(mGameId).child("tapcount").addValueEventListener(tapCountUpdate);

        anyoneLostCheckListner = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if ((long) dataSnapshot.getValue() != 0) {
                    if ((long) dataSnapshot.getValue() == myIndex) {
                        // you lost
                        animationView.pauseAnimation();
                        mSoundPool.stop(streamIdforBg);
                        mTextGuide.setText("you lost it, ask " + mOpponentName + " for a new match!");
                        mWinnerText.setText("points goes to " + mOpponentName);
                        mGameBg.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorDotsbgLost));
                        mSharedPref.setMyMatches(Integer.parseInt(mSharedPref.getMyMatches()) + 1 + "");

                        isMatchOn = false;
                    } else {
                        // you win
                        mSoundPool.stop(streamIdforBg);
                        mSoundPool.play(theWinnerSoundId, 1, 1, 1, 0, 1);

                        mTextGuide.setText("you won the match, Hit replay!");
                        mWinnerText.setText("points goes to " + mYourName);
                        mReplayGame.setVisibility(View.VISIBLE);
                        mGameBg.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorDotsbgWon));
                        mSharedPref.setMyMatches(Integer.parseInt(mSharedPref.getMyMatches()) + 1 + "");

                        isMatchOn = false;

                        //points addition in shared pref
                        addPoint(totalPoints);

                    }
                } else {
                    isMatchOn = true;
                    tapcount = 0;
                    mTapRank = 0;
                    mWinnerText.setText("points will go to the winner!");
                    if (isYourTurn) {
                        mGameBg.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorDotsbg));
                        mTextGuide.setText("Its Your Turn to play " + mYourName + "!");

                        pointsGain = 100;
                        mCountDown.start();

                    } else {
                        mGameBg.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorDotsbg2));
                        mTextGuide.setText("" + mOpponentName + " is playing...");

                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        ref.child(mGameId).child("lostIndex").addValueEventListener(anyoneLostCheckListner);

        pointsUpdateListner = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                totalPoints = (long) dataSnapshot.getValue();
                mPoints.setText("" + totalPoints);

                hsvColor[0] = 360f * totalPoints / 36500;

                mPointsDot.setColorFilter(Color.HSVToColor(hsvColor));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        ref.child(mGameId).child("totalPoints").addValueEventListener(pointsUpdateListner);

        haveSomeOneCloase = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long isClosed;
                isClosed = (long) dataSnapshot.getValue();
                if (isClosed == 1) {
                    mReplayGame.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        ref.child(mGameId).child("someoneclosed").addValueEventListener(haveSomeOneCloase);


        mReplayGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initFirebaseNode();
                mReplayGame.setVisibility(View.INVISIBLE);
                mTextGuide.setText("you won the match! "+mOpponentName+" left.");

            }
        });

//        mFabMute.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (vol == 1) {
//                    vol = 0;
//                    mSoundPool.stop(streamIdforBg);
//                    mFabMute.setImageResource(R.drawable.ic_volume_up_black_24dp);
//                } else {
//                    vol = 1;
//                    mFabMute.setImageResource(R.drawable.ic_volume_off_black_24dp);
//
//                }
//            }
//        });
//
//        mFabShare.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //share the app
//            }
//        });
//
//        mFabRate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //rate the app
//            }
//        });

        mCloseMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isMatchOn) {

                    //Uncomment the below code to Set the message and title from the strings.xml file
                    //builder.setMessage(R.string.dialog_message) .setTitle(R.string.dialog_title);

                    //Setting message manually and performing action on button click
                    builder.setMessage(totalPoints
                            + " points goes to "
                            + mOpponentName
                            + " if you Quit! you really want to run away like this?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //go to main screen
                                    ref.child(mGameId).child("lostIndex").setValue(myIndex);
                                    ref.child(mGameId).child("someoneclosed").setValue(1);
                                    removeAllListners();
                                    finish();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //  Action for 'NO' Button
                                    dialog.cancel();
                                }
                            });

                    //Creating dialog box
                    AlertDialog alert = builder.create();
                    //Setting the title manually
                    alert.show();
                } else {
                    ref.child(mGameId).child("someoneclosed").setValue(1);
                    finish();
                }
            }

        });


    }

    private void addPoint(long totalPoints) {
        String lastscore = mSharedPref.getMyScore();
        Long l = Long.parseLong(lastscore) + totalPoints;
        mSharedPref.setMyScore("" + l);
    }


    @Override
    protected void onResume() {
        super.onResume();

        vol = 1;

    }

    @Override
    protected void onPause() {
        super.onPause();
        vol = 0;
        mSoundPool.stop(streamIdforBg);
    }

    @Override
    public void onBackPressed() {
    }

    private void initFirebaseNode() {

        if (myIndex == 1 || !isMatchOn) {
            ref.child(mGameId).child("tapcount").setValue(0);
            ref.child(mGameId).child("mIndex").setValue(1);
            ref.child(mGameId).child("lostIndex").setValue(0);
            ref.child(mGameId).child("totalPoints").setValue(0);
            ref.child(mGameId).child("someoneclosed").setValue(0);


            for (int i = 0; i < 25; i++) {
                ref.child(mGameId).child("values").child("" + i).setValue(new InputPojo(0, -1, 0, i));
            }
        }
        isMatchOn = true;
        //init CountDown Timer
        mCountDown = new CountDownTimer(10000, 100) {

            public void onTick(long millisUntilFinished) {
                pointsGain = (int) millisUntilFinished / 100;
                mProgress.setProgress(pointsGain);
            }

            public void onFinish() {
                pointsGain = 0;
            }
        };


    }

    private void initView() {
        gridRecyclarView = findViewById(R.id.gridRecyclarView);
        gridRecyclarView.setLayoutManager(new GridLayoutManager(this, 5));
        mReplayGame = findViewById(R.id.imageViewReplay);
        mTextGuide = findViewById(R.id.textViewGuide);
        //mFabMute = findViewById(R.id.floatingActionButtonMute);
        //mFabRate = findViewById(R.id.floatingActionButtonRate);
        //mFabShare = findViewById(R.id.floatingActionButtonShare);
        mWinnerText = findViewById(R.id.textViewWinner);
        mPoints = findViewById(R.id.textViewPoints);
        mTitleText = findViewById(R.id.whoesTrun);
        mGameBg = findViewById(R.id.rlGameField);
        mProgress = findViewById(R.id.progressBar);
        mPointsDot = findViewById(R.id.imageViewPointTemp);
        mLoading = findViewById(R.id.textViewLoading);
        mCloseMatch = findViewById(R.id.imageViewBack);
        animationView = findViewById(R.id.animation_view);


    }

    private boolean isLastResponce() {
        long checkValue = 1;
        for (int i = 0; i < 25; i++) {
            if (checkValue == tapcount) {
                return true;
            }
            checkValue = checkValue + 2 + i;
        }
        return false;
    }

    private boolean isYourTurnToPlay() {
        return isYourTurn;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
        removeAllListners();
    }

    void removeAllListners() {
        ref.child(mGameId).child("mIndex").removeEventListener(whoseturnListner);
        ref.child(mGameId).child("tapcount").removeEventListener(tapCountUpdate);
        ref.child(mGameId).child("lostIndex").removeEventListener(anyoneLostCheckListner);
        ref.child(mGameId).child("totalPoints").removeEventListener(pointsUpdateListner);
        ref.child(mGameId).child("someoneclosed").removeEventListener(haveSomeOneCloase);
    }
}
