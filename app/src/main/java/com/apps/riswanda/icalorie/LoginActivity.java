package com.apps.riswanda.icalorie;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail,inputKatasandi;
    private Button btn_logIn;

    private FirebaseAuth mAuth;

    private DatabaseReference mDatabase;

    private ProgressDialog mProgess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mProgess = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");


        inputEmail = (EditText) findViewById(R.id.inputEmail);
        inputEmail.addTextChangedListener(emailW);
        inputKatasandi = (EditText) findViewById(R.id.inputKatasandi);
        inputKatasandi.addTextChangedListener(pw);

        btn_logIn = (Button) findViewById(R.id.btn_logIn);

        btn_logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!inputEmail.getText().toString().isEmpty() && !inputKatasandi.getText().toString().isEmpty()) {
                    checkLogin();

                }
                if(inputEmail.getText().toString().isEmpty()){
                    inputEmail.setError("Email tidak boleh kosong!");
                }
                if(inputKatasandi.getText().toString().isEmpty()){
                    inputKatasandi.setError("Password tidak boleh kosong!");
                }

            }
        });


    }
    private TextWatcher emailW = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(inputEmail.getText().toString().isEmpty()) {
                inputEmail.setError("Email tidak boleh kosong!");
            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private TextWatcher pw =new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(inputKatasandi.getText().toString().isEmpty()){
                inputKatasandi.setError("Password tidak boleh kosong!");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    private void checkLogin() {

        String email = inputEmail.getText().toString().trim();
        String katasandi = inputKatasandi.getText().toString().trim();


        if (!TextUtils.isEmpty(email)&& Patterns.EMAIL_ADDRESS.matcher(email).matches() && !TextUtils.isEmpty(katasandi)){

            mProgess.setMessage("Signing In");
            mProgess.show();

            mAuth.signInWithEmailAndPassword(email, katasandi).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()){

                        checkUserExist();

                    }else {
                        Toast.makeText(LoginActivity.this, "Login Gagal. Email atau Password salah!", Toast.LENGTH_LONG).show();
                    }

                    mProgess.dismiss();



                }
            });
        }
        else {
            Toast.makeText(LoginActivity.this,"Email atau Password Salah!", Toast.LENGTH_LONG).show();
        }

    }

    private void checkUserExist() {

        final String user_id = mAuth.getCurrentUser().getUid();

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(user_id)){

                    Intent mainIntent = new Intent(LoginActivity.this, HomeActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
