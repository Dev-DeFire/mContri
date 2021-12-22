package com.executivestrokes.mcent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class Profile extends AppCompatActivity {
    ImageView back,userProfile;
    TextView editProfile,UserTitle,UserphNo,email_id,upi_id;
    Button saving,logout;
    DatabaseReference myDatabaseReference;
    FirebaseAuth firebaseAuth;
    String uImg;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        back=findViewById(R.id.backtosaving);
        userProfile=findViewById(R.id.profileImg);
        editProfile=findViewById(R.id.edit_profile);
        UserTitle=findViewById(R.id.UserTitle);
        UserphNo=findViewById(R.id.UserPhNo);
        saving=findViewById(R.id.savingplan);
        email_id=findViewById(R.id.email_id);
        upi_id=findViewById(R.id.upi_id);
        logout=findViewById(R.id.logout);

        //PROGRESS DIALOG
        progressDialog = new ProgressDialog (this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data Please wait !");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        firebaseAuth= FirebaseAuth.getInstance();
        FirebaseUser fUser=firebaseAuth.getCurrentUser();
        myDatabaseReference= FirebaseDatabase.getInstance().getReference("Users");
        UserphNo.setText(fUser.getPhoneNumber());
        Query query=myDatabaseReference.orderByChild("phone").equalTo(fUser.getPhoneNumber());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    //userPhNo.setText(String.valueOf(ds.child("phone").getValue()));
                    uImg=""+ds.child("image").getValue();
                    try {
                        UserTitle.setText(String.valueOf(ds.child("userName").getValue()));
                        email_id.setText(String.valueOf(ds.child("mail").getValue()));
                        upi_id.setText(String.valueOf(ds.child("upiId").getValue()));
                        Picasso.get().load(uImg).into(userProfile);
                    }catch(Exception e){
                        progressDialog.dismiss();
                        email_id.setText("Someone@gmail.com");
                        upi_id.setText("Someone@upi");
                        Picasso.get().load(R.drawable.ic_baseline_self_improvement_24).into(userProfile);
                    }
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Profile.this,Dashboard.class);
                startActivity(intent);
                finish();
            }
        });
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Profile.this,setProfile.class);
                intent.putExtra("check","editprofile");
                startActivity(intent);
                finish();
            }
        });
        saving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Profile.this,Dashboard.class);
                overridePendingTransition(R.anim.slide_in_left,R.anim.stay);
                startActivity(intent);
                finish();
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogout();
            }
        });
    }
    public void onLogout(){

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            firebaseAuth.signOut();
            startActivity(new Intent(this, MainActivity.class));
            // User is signed in
        } else {
            Toast.makeText(this, "User has been logged out...", Toast.LENGTH_SHORT).show();
            // User is signed out

        }
    }
}