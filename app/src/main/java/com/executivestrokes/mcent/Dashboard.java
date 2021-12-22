package com.executivestrokes.mcent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class Dashboard extends AppCompatActivity {

    TabLayout tabLayout;
    TabItem mchat,mcall,mstatus;
    ViewPager viewPager;
    PagerAdapter pagerAdapter;
    androidx.appcompat.widget.Toolbar mtoolbar;

    FirebaseAuth firebaseAuth;
    Button cardViewBtn,profilesection;
    RecyclerView recyclerView;
    ImageView userImg;
    TextView userName,userPhNo;
    private FirebaseStorage firebaseStorage,firebaseDatabase;
    private StorageReference storageReference;
    String uImg , grpId;
    ProgressDialog progressDialog;
    //    private List<GroupModel> ItemList;
//    private RecyclerViewAdapter mAdapter;
    private List<GroupModel> userList;
    private UserAdapter userAdapter;

    private DatabaseReference myDatabaseReference,databaseReference;
    private static final int CONTACT_PERMISSION_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        cardViewBtn=findViewById(R.id.cardViewButton);
        profilesection=findViewById(R.id.gotoprofile);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        profilesection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Dashboard.this,Profile.class);
                overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
                startActivity(intent);
            }
        });

        //Recycler
        recyclerView = findViewById(R.id.grouplist);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        layoutManager.setReverseLayout(true);
//        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,true));
        //Contact Permission
        firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser fUser=firebaseAuth.getCurrentUser();
        if (fUser == null) {
            firebaseAuth.signOut();
            startActivity(new Intent(this, MainActivity.class));
            // User is signed in
        }


        userList = new ArrayList<>();
        userAdapter = new UserAdapter(Dashboard.this, userList, new UserAdapter.UserRecyclerViewOnClickListner() {
            @Override
            public void onIttemClick(String name,String UID,String desc,String amount,String totalMembers,String sDura, String sDate,String eDate) {
                try {
                    Intent intent=new Intent(Dashboard.this,GroupDisplay.class);
                    //intent.putExtra("grpID",UID);
                    intent.putExtra("gTitle",name);
                    intent.putExtra("gDesc",desc);
                    intent.putExtra("eDate",eDate);
                    intent.putExtra("grpID",UID);
                    intent.putExtra("gMember",totalMembers);
                    intent.putExtra("sDate",sDate);
                    intent.putExtra("gMoney",amount);
                    intent.putExtra("gDuration",sDura);


                    startActivity(intent);
                    finish();
                }catch (Exception e){
                    Toast.makeText(Dashboard.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }


            }
        });

        recyclerView.setAdapter(userAdapter);


        //PROGRESS DIALOG
        progressDialog = new ProgressDialog (this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data Please wait !");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        progressDialog.show();
        myDatabaseReference=FirebaseDatabase.getInstance().getReference("Users");
        /*databaseReference.child(fUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        DataSnapshot snapshot=task.getResult();
                        userName.setText(String.valueOf(snapshot.child("userName").getValue()));
//                        String img=String.valueOf(snapshot.child("userName").getValue());
//                        Picasso.get().load(img).into(userImg);

                    }
                    else{
                        userName.setText(" Welcome ! ");
                    }
                }
                else{
                    userName.setText(" Welcome ! ");
                }
                Picasso.get().load(R.drawable.ic_baseline_self_improvement_24).into(userImg);
            }
        });

         */
        /*StorageReference imageref=storageReference.child("Images").child(firebaseAuth.getUid()).child("Profile Pic");
        imageref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Userimage=uri.toString();
                Picasso.get().load(uri).into(userImg);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Picasso.get().load(R.drawable.ic_baseline_self_improvement_24).into(userImg);
            }
        });

         */


        readData();
        progressDialog.dismiss();
        cardViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Dashboard.this,GroupActivity.class);
                startActivity(intent);
            }
        });
        firebaseAuth=FirebaseAuth.getInstance();



    }

    /*private void loadGroupList() {
        /*groupModels=new ArrayList<>();
        firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser fUser=firebaseAuth.getCurrentUser();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Groups").child(fUser.getUid());
        reference.child(fUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        DataSnapshot snapshot=task.getResult();
                        for (DataSnapshot ds : snapshot.getChildren()){
                            GroupModel model=ds.getValue(GroupModel.class);
                            groupModels.add(model);
                        }

                    }
                }
                gAdapter= new GroupAdapter(Dashboard.this,groupModels);
                recyclerView.setAdapter(gAdapter);

            }

        });

         /*
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Iterable<DataSnapshot> snapshotIterator = snapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                groupModels.clear();
                while((iterator.hasNext())){
                    GroupModel donor = iterator.next().getValue(GroupModel.class);
                    groupModels.add(donor);
                    gAdapter.notifyDataSetChanged();
                }
                gAdapter= new GroupAdapter(getApplicationContext(),groupModels);
                recyclerView.setAdapter(gAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

         */
    //readData();

    /*private void SearchGroupList(final String query) {
        groupModels=new ArrayList<>();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Groups");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds : snapshot.getChildren()){
                    if(!ds.child("Participants").child(firebaseAuth.getUid()).exists()){
                        if(ds.child("groupTitle").toString().toLowerCase().contains(query.toLowerCase())){
                            User model=ds.getValue(User.class);
                            groupModels.add(model);
                        }

                    }
                }
                //gAdapter= new GroupAdapter(Dashboard.this,groupModels);
                recyclerView.setAdapter(gAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }*/
   /* private void readData(){
        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        FirebaseUser fUser=firebaseAuth.getCurrentUser();
        databaseReference=firebaseDatabase.getReference("Groups").child(fUser.getUid());
        grpId= databaseReference.push().getKey();
        ItemList = new ArrayList<>();
        databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    ItemList.clear();
                    DataSnapshot snapshot=task.getResult();
                    for (DataSnapshot ds: snapshot.getChildren()){
                        GroupModel data=ds.getValue(GroupModel.class);
                        ItemList.add(data);
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Dashboard.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ItemList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    GroupModel data=ds.getValue(GroupModel.class);
                    ItemList.add(data);
                }
                mAdapter.notifyDataSetChanged();
                /*Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                ItemList.clear();
                while((iterator.hasNext())){
                    GroupModel donor = iterator.next().getValue(GroupModel.class);
                    ItemList.add(donor);
                    mAdapter.notifyDataSetChanged();
                }


            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Dashboard.this,"Database Error ! ",Toast.LENGTH_SHORT).show();

            }

        });

        mAdapter=new RecyclerViewAdapter(
                Dashboard.this,ItemList, new RecyclerViewAdapter.RecyclerViewOnClickListner() {
            @Override
            public void onIttemClick(String linkName) {
                progressDialog.show();
                try {
                    gotoUrl(linkName);
                }
                catch (Exception e)
                {
                    progressDialog.dismiss();
                    Toast.makeText(Dashboard.this, "Error ! Invalid Link , Link Not Found... ", Toast.LENGTH_SHORT).show();
                }

            }

            private void gotoUrl(String h) {
                Uri uri= Uri.parse(h);
                startActivity(new Intent(Intent.ACTION_VIEW,uri));
            }

        });
        mRecyclerview.setAdapter(mAdapter);

    }
    */
    private void readData() {
        FirebaseUser fUser=firebaseAuth.getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Groups").child(fUser.getUid());

        //DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference();

        //.orderByChild("participantsName").equalTo("201")
        /*Query query = reference2.child("Group Members").orderByChild("Members").equalTo("Members");

        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                System.out.println(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

         */

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                if(snapshot.exists()){
                    try {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            GroupModel user = dataSnapshot.getValue(GroupModel.class);
                            userList.add(user);
                        }
                        userAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();

                        if (userList.isEmpty()){
                            Toast.makeText(Dashboard.this, "No recipients", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }catch(Exception e){
                        progressDialog.dismiss();
                        Toast.makeText(Dashboard.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
            }
        });

    }
//    private boolean checkContactPermission(){
//        //check if contact permission was granted or not
//        boolean result = ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.READ_CONTACTS) == (PackageManager.PERMISSION_GRANTED
//        );
//
//        return result;  //true if permission granted, false if not
//    }
//
//    private void requestContactPermission(){
//        //permissions to request
//        String[] permission = {Manifest.permission.READ_CONTACTS};
//
//        ActivityCompat.requestPermissions(this, permission, CONTACT_PERMISSION_CODE);
//    }

}