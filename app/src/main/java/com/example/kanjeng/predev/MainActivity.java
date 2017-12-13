package com.example.kanjeng.predev;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    public TextView textView;
    Handler setDelay;
    AlertDialog dialog;
    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;

    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public DB_Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setDelay = new Handler();
        textView = (TextView)findViewById(R.id.status);

        if(isServicesOK()){
            init();
        }

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    toastMessage("Berhasil sign in dengan "+user.getEmail());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    toastMessage("Berhasil sign out ");
                }
                // ...
            }
        };

        editTextEmail = (EditText)findViewById(R.id.editTextEmail);
        editTextPassword = (EditText)findViewById(R.id.editTextPassword);

        controller = new DB_Controller(this,"",null,1);
        cekJmlUser();
    }

    public void cekJmlUser(){
        Cursor cursor = controller.getReadableDatabase().rawQuery("SELECT count(*) from USER",null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        textView.setText(String.valueOf(count));
    }

    public void btn_click(View view){
        switch (view.getId()){
            case R.id.buttonAdd:
                controller.insert_email(editTextEmail.getText().toString(),editTextPassword.getText().toString());
                toastMessage("selesai menambahkan di sqlite");
                break;
            case R.id.buttonList:
                controller.list_email(textView);
                toastMessage("selesai menampilkan data dari sqlite");
                break;
        }
    }

    public void signIn(View view){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        if (!email.equals("") && !password.equals("")){
            mAuth.signInWithEmailAndPassword(email,password);
        } else
        {
            Toast.makeText(this,"Lengkapi email dan password",Toast.LENGTH_SHORT).show();
        }
    }

    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    public void signOut(View view){
        mAuth.signOut();
        toastMessage("Sign out ...");
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void registerUser(View view){
        Log.d(TAG, "registerUser : begin");
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)){
            Toast.makeText(this,"Masukkan email",Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)){
            Toast.makeText(this,"Masukkan password",Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "createUserWithEmailAndPassword : hasilnya");
                if (task.isSuccessful()){
                    Toast.makeText(MainActivity.this,"Registered successfully",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this,"Registered failed",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void loginatmb(View view) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_wait, null);
        mBuilder.setView(mView);
        dialog = mBuilder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        new GetTask().execute();
    }

    class GetTask extends AsyncTask<String, String, String> {
        Context context;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "selesai loading";
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.hide();
            textView.setText(s);
        }
    }

    private void init(){
        Button btnMap = (Button)findViewById(R.id.btnMap);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,MapActivity.class);
                startActivity(intent);
            }
        });
    }
    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
        if (available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map request
            Log.d(TAG,"isServicesOK: Google Play Services is working");
            return true;
        }
        else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG,"isServicesOK : an error occur but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this,available,ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else {
            Toast.makeText(this,"You can't make map request",Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}