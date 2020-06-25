package com.bodhayanacademy;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bodhayanacademy.Activity.ForgetPasswordActivity;
import com.bodhayanacademy.Activity.GecHomeActivity;
import com.bodhayanacademy.Model.RefCode;
import com.bodhayanacademy.RetrofitApiCall.ApiClient;
import com.bodhayanacademy.Utils.ProgressBarUtil;
import com.bodhayanacademy.WebApi.ClientApi;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {


    EditText editTextUsername, editTextPassword;
    TextView referalCode;
    private long backPressedTime;
    private ProgressBarUtil progressBarUtil;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    LinearLayout forgetPassword;
    FirebaseAuth auth;
    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, GecHomeActivity.class));
            return;
        }
        initViews();
    }

    private void initViews() {
        progressBarUtil = new ProgressBarUtil(this);
        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        referalCode = (TextView) findViewById(R.id.link_referal_code);
        forgetPassword = (LinearLayout) findViewById(R.id.link_forget_password);
        auth=FirebaseAuth.getInstance();
        reference= FirebaseDatabase.getInstance().getReference().child("Users");


        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });


        findViewById(R.id.buttonLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // FireBaseValidation();
                userValidation();

            }
        });


        findViewById(R.id.textViewRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });
    }

    private void userValidation() {
        final String mobile = editTextUsername.getText().toString();
        final String password = editTextPassword.getText().toString();
        final String referaCode = referalCode.getText().toString();

        //validating inputs
        if (TextUtils.isEmpty(mobile)) {
            editTextUsername.setError("Please enter your mobile no");
            editTextUsername.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Please enter your password");
            editTextPassword.requestFocus();
            return;
        }
        RefCode refCode = new RefCode();
        refCode.setUsername(mobile);
        refCode.setPassword(password);
        refCode.setRef_code(referaCode);
        callRefCodeSignInApi(refCode);


    }

    private void callRefCodeSignInApi(final RefCode refCode) {

        progressBarUtil.showProgress();
        ClientApi mService = ApiClient.getClient().create(ClientApi.class);
        Call<RefCode> call = mService.refCodeSignIn(1, refCode.getUsername(), refCode.getPassword(), refCode.getRef_code());
        call.enqueue(new Callback<RefCode>() {
            @Override
            public void onResponse(Call<RefCode> call, Response<RefCode> response) {
                int statusCode = response.code();
                if (statusCode == 200 && response.body().getOrg_Code() != null) {
                    progressBarUtil.hideProgress();
                    Constants.CurrentUser = response.body();
                    SharedPrefManager.getInstance(getApplicationContext()).userLogin(Constants.CurrentUser);
                    Intent intent = new Intent(LoginActivity.this, GecHomeActivity.class);
                    intent.putExtra("userid", refCode.getUserId());
                    startActivity(intent);
                    finish();
                } else {
                    progressBarUtil.hideProgress();
                    Toast.makeText(LoginActivity.this, "Invalid UserName Password ", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<RefCode> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Failed" + t.getMessage(), Toast.LENGTH_LONG).show();
                System.out.println(t.getLocalizedMessage());
            }
        });
    }




    @Override
    public void onBackPressed() {

        if (backPressedTime+2000>System.currentTimeMillis()){
            super.onBackPressed();
            return;
        }else{
            Toast.makeText(getBaseContext(),"Press Exit again to exit",Toast.LENGTH_SHORT).show();
        }
        backPressedTime=System.currentTimeMillis();
    }


//    public void FireBaseValidation(){
//        final String Email="amitjalan62@gmail.com";
//        final String Name ="Amit";
//        final String Password="123456";
//        final String FireBaseMobile="8769114445";
//// final String Email=email.getText().toString();
////        final String Name =name.getText().toString();
////        final String Password=password.getText().toString();
////        final String FireBaseMobile=mobile.getText().toString();
//
//        if (!Email.equals("")&& !Password.equals("") ){
//            auth.createUserWithEmailAndPassword(Email,Password)
//                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            if (task.isSuccessful()) {
//                                FirebaseUser firebaseUser = auth.getCurrentUser();
//                                FireBaseUserId user = new FireBaseUserId();
//                                user.setEmail(Email);
//                                user.setName(Name);
//                                user.setMobile(FireBaseMobile);
//                                reference.child(firebaseUser.getUid()).setValue(user)
//                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<Void> task) {
//                                                if (task.isSuccessful()) {
////                                                    Toast.makeText(OtpVerficationActivity.this, "Register Successful", Toast.LENGTH_SHORT).show();
////                                                    finish();
////                                                    Intent intent = new Intent(OtpVerficationActivity.this, GroupChatActivity.class);
////                                                    startActivity(intent);
//
//                                                } else {
//                                                    // Toast.makeText(OtpVerficationActivity.this, "not successful", Toast.LENGTH_SHORT).show();
//
//                                                }
//                                            }
//                                        });
//                            }
//
//                            task.addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Log.e("error", e.getMessage());
//                                }
//                            });
//                        }
//
//                    });
//        }
//    }
}