package dsardy.in.memorybattle;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import dsardy.in.memorybattle.managers.SharedPrefManager;

public class UserProfileActivity extends AppCompatActivity {

    ImageView back;
    EditText userName;
    FloatingActionButton set;
    SharedPrefManager mSharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        back = findViewById(R.id.imageViewBack);
        userName = findViewById(R.id.editTextUserName);
        set = findViewById(R.id.floatingActionButtonUsernameSet);
        mSharedPref = new SharedPrefManager(getApplicationContext());

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!userName.getText().toString().isEmpty()){
                    mSharedPref.setMyName(userName.getText().toString().trim());
                    finish();
                }else {
                    Toast.makeText(getApplicationContext(),"type something!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!mSharedPref.getMyName().equals("")){
            userName.setHint(mSharedPref.getMyName());
        }else {
            back.setVisibility(View.INVISIBLE);
        }
    }
}
