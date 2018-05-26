package dsardy.in.memorybattle;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import dsardy.in.memorybattle.managers.SharedPrefManager;
import dsardy.in.memorybattle.models.GameInfoPojo;

public class ConnectWithQrActivity extends AppCompatActivity {

    ImageView imageView;
    Button button;
    EditText editText;
    String EditTextValue;
    Thread thread;
    public final static int QRcodeWidth = 500;
    Bitmap bitmap;
    private IntentIntegrator qrScan;
    FloatingActionButton scanQR;
    private Context mContext;
    private SharedPrefManager mSharedPrefManager;
    JSONObject qrData;
    DatabaseReference ref;
    String gameId;
    TextView mTextGeneratingQr;
    boolean isFirstUpdateOfUserNode = true;
    ValueEventListener valueEventListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("onCrt", "............");
        setContentView(R.layout.activity_connect_with_qr);
        imageView = findViewById(R.id.imageViewQr);
        scanQR = findViewById(R.id.floatingActionButtonScan);
        mTextGeneratingQr = findViewById(R.id.textViewGenerating);
        ref = FirebaseDatabase.getInstance().getReference();
        mContext = getApplicationContext();
        mSharedPrefManager = new SharedPrefManager(mContext);
        qrScan = new IntentIntegrator(this);


        prepareId();
        generateJsonObject();
        new GenerateQR().execute(qrData.toString());

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GameInfoPojo p = dataSnapshot.getValue(GameInfoPojo.class);
                //Log.e("activeuser",dataSnapshot.toString());
                if (p != null) {
                    startGameActivity(p.getmOpponentName(), p.getmGameId(), 2);
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        ref.child("activeUsers").child(mSharedPrefManager.getMyNumber()).addValueEventListener(valueEventListener);

        scanQR.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                qrScan.initiateScan();
            }
        });

    }

    private void startGameActivity(String s, String s1, int index) {

        Intent i = new Intent(mContext, MainActivity.class);
        i.putExtra("opponentName", s);
        i.putExtra("gameId", s1);
        i.putExtra("mIndex", index);
        ref.child("activeUsers").child(mSharedPrefManager.getMyNumber()).removeEventListener(valueEventListener);
        ref.child("activeUsers").child(mSharedPrefManager.getMyNumber()).removeValue();

        startActivity(i);
        finish();
    }

    private void generateJsonObject() {
        qrData = new JSONObject();
        try {
            qrData.put("mUserName", mSharedPrefManager.getMyName());
            qrData.put("mUserId", mSharedPrefManager.getMyNumber());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void prepareId() {
        if (mSharedPrefManager.getMyNumber().equals("")) {
            UUID uniqueKey = UUID.randomUUID();
            mSharedPrefManager.setMyNumber(uniqueKey.toString());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        Log.e("onAct", "" + requestCode + " " + requestCode + " " + result.getContents());

        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Try Again", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                try {
                    //converting the data to json
                    JSONObject obj = new JSONObject(result.getContents());
                    //setting values to textviews
                    if (obj.has("mUserName") && obj.has("mUserId")) {
                        String opponentName = obj.getString("mUserName");
                        String opponentId = obj.getString("mUserId");
                        String mGameId = mSharedPrefManager.getMyNumber() + opponentId;
                        //update opponents firebase node
                        ref.child("activeUsers").child(opponentId)
                                .setValue(new GameInfoPojo(opponentId,
                                        opponentName,
                                        mGameId,
                                        mSharedPrefManager.getMyName()));
                        startGameActivity(opponentName, mGameId, 1);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast
                    Log.e("exp", e.toString());
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark) : ContextCompat.getColor(getApplicationContext(), R.color.colorLightGrey);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    private class GenerateQR extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap b = null;
            try {
                b = TextToImageEncode(qrData.toString());
            } catch (WriterException e) {
                e.printStackTrace();
            }

            return b;
        }

        @Override
        protected void onPostExecute(Bitmap b) {

            if (b != null) {
                imageView.setImageBitmap(b);
                mTextGeneratingQr.setVisibility(View.INVISIBLE);
            } else {
                mTextGeneratingQr.setText("some error! try again");

            }
            // txt.setText(result);
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
        }

        @Override
        protected void onPreExecute() {
            mTextGeneratingQr.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    public void goBack(View view) {
        finish();
    }
}



