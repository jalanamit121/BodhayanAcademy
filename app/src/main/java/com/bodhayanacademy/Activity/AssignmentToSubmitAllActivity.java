package com.bodhayanacademy.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bodhayanacademy.Adapter.AssignmentToSubmitAllAdapter;
import com.bodhayanacademy.LoginActivity;
import com.bodhayanacademy.Model.AssignmentDatum;
import com.bodhayanacademy.Model.AssignmentToSubmit;
import com.bodhayanacademy.R;
import com.bodhayanacademy.RetrofitApiCall.ApiClient;
import com.bodhayanacademy.SharedPrefManager;
import com.bodhayanacademy.Utils.ProgressBarUtil;
import com.bodhayanacademy.WebApi.ClientApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssignmentToSubmitAllActivity extends AppCompatActivity {

    private List<AssignmentDatum> list;
    RelativeLayout home,histroy,logout;
    private ProgressBarUtil progressBarUtil;
    private AssignmentToSubmitAllAdapter assignmentToSubmitAdapter;
    private RecyclerView assignmentView;
    String Userid;
    private Button submit_assignment_btn;
    private AssignmentDatum assignmentDatum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_to_submit_all);
        home=findViewById(R.id.layout_home);
        histroy=findViewById(R.id.layout_history);
        logout=findViewById(R.id.layout_logout);
        Userid = SharedPrefManager.getInstance(this).refCode().getUserId();
        progressBarUtil   =  new ProgressBarUtil(this);
        assignmentView = findViewById(R.id.assignment_review);
        submit_assignment_btn=findViewById(R.id.submit_assignment_btn);
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null) {
            assignmentDatum = (AssignmentDatum) bundle.getSerializable("assignmentId");
        }
        submit_assignment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                Intent intent = new Intent(AssignmentToSubmitAllActivity.this, SubmitAssignmentActivity.class);
                bundle.putSerializable("content_name",assignmentDatum.getAssignment_id());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        histroy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://bodhayancoaching.com/"));
                startActivity(intent);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AssignmentToSubmitAllActivity.this,GecHomeActivity.class);
                startActivity(intent);
            }
        });

        callAllAssignment(Userid);
    }
    private void callAllAssignment(String Userid) {
        progressBarUtil.showProgress();
        ClientApi apiCAll = ApiClient.getClient().create(ClientApi.class);
        Call<AssignmentToSubmit> call = apiCAll.getAllAssignment("WB_004",Userid);
        call.enqueue(new Callback<AssignmentToSubmit>() {
            @Override
            public void onResponse(Call<AssignmentToSubmit> call, Response<AssignmentToSubmit> response) {
                AssignmentToSubmit assignmentToSubmit=response.body();
                int statusCode = response.code();
                list = new ArrayList();
                if(statusCode==200){
                    list=new ArrayList<>(Arrays.asList(assignmentToSubmit.getData()));
                    System.out.println("Suree body: "+response.body());
                    assignmentToSubmitAdapter = new AssignmentToSubmitAllAdapter(AssignmentToSubmitAllActivity.this,list);
                    assignmentView.setAdapter(assignmentToSubmitAdapter);
                    progressBarUtil.hideProgress();
                }
                else{
                    System.out.println("Suree: response code"+response.message());
                    Toast.makeText(getApplicationContext(),"NetWork Issue,Please Check Network Connection" ,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AssignmentToSubmit> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Failed" + t.getMessage(),Toast.LENGTH_SHORT).show();

                System.out.println("Suree: Error "+t.getMessage());
            }
        });
    }

    private void logout() {
        SharedPrefManager.getInstance(this).logout();
        startActivity(new Intent(this, LoginActivity.class));
        Objects.requireNonNull(this).finish();
    }

}
