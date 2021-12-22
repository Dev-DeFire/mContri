package com.executivestrokes.mcent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.wafflecopter.multicontactpicker.ContactResult;
import com.wafflecopter.multicontactpicker.LimitColumn;
import com.wafflecopter.multicontactpicker.MultiContactPicker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GroupActivity extends AppCompatActivity {

    private static final int CONTACT_PICKER_REQUEST = 929;
    TextView msavegrp;
    private ImageView back;
    private static int PICK_IMAGE=123;
    private Uri grpIgmPath;
    Button endDateButton;

    private EditText gTitle,gDesc,gAmount;
    Button gMember;
    Calendar calendar;
    private FirebaseAuth firebaseAuth;
    private String name;
    private String desc;
    private String amount;
    private String tMember;
    private String endDate,startDate;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private String ImageUriAcessToken="",grpId,totalMembers,dura,sDura;
    ProgressDialog progressDialog;
    List<ContactResult> results= new ArrayList<>();
    List<phoneBookModel> ItemList=new ArrayList<>();
    private static final int CONTACT_PERMISSION_CODE = 1;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();
        endDateButton=findViewById(R.id.endddate);
        back=findViewById(R.id.onback);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                startActivity(intent);
                finish();
            }
        });
        calendar=Calendar.getInstance();
        //startDate= SimpleDateFormat.getDateInstance().format(calendar.getTime());

        startDate=String.valueOf(android.text.format.DateFormat.format("dd-MM-yyyy", new java.util.Date()));
        gTitle=findViewById(R.id.plan_name);
        gDesc=findViewById(R.id.plan_UPI);
        gAmount=findViewById(R.id.plan_saving);
        gMember=findViewById(R.id.totalmembers);
        msavegrp=findViewById(R.id.creategrp);

        //PROGRESS DIALOG
        progressDialog = new ProgressDialog (this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Creating Group....");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);


        gMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkContactPermission()) {
                    new MultiContactPicker.Builder(GroupActivity.this) //Activity/fragment context
                            .hideScrollbar(false) //Optional - default: false
                            .showTrack(true) //Optional - default: true
                            .searchIconColor(Color.WHITE) //Option - default: White
                            .setChoiceMode(MultiContactPicker.CHOICE_MODE_MULTIPLE) //Optional - default: CHOICE_MODE_MULTIPLE
                            .handleColor(ContextCompat.getColor(GroupActivity.this, R.color.colorPrimary)) //Optional - default: Azure Blue
                            .bubbleColor(ContextCompat.getColor(GroupActivity.this, R.color.colorPrimary)) //Optional - default: Azure Blue
                            .bubbleTextColor(Color.WHITE) //Optional - default: White
                            .setTitleText("Select Contacts") //Optional - default: Select Contacts
                            .setLoadingType(MultiContactPicker.LOAD_ASYNC) //Optional - default LOAD_ASYNC (wait till all loaded vs stream results)
                            .limitToColumn(LimitColumn.NONE) //Optional - default NONE (Include phone + email, limiting to one can improve loading time)
                            .setActivityAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                                    android.R.anim.fade_in,
                                    android.R.anim.fade_out) //Optional - default: No animation overrides
                            .showPickerForResult(CONTACT_PICKER_REQUEST);
                }
                else
                {
                    requestContactPermission();
                }

            }
        });
        endDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year=calendar.get(Calendar.YEAR);
                int month= calendar.get(Calendar.MONTH);
                int day=calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog= new DatePickerDialog(GroupActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        endDate=dayOfMonth+"-"+(month+1)+"-"+year;
                        endDateButton.setText("End Date Selected : "+endDate);
                    }
                },day,month,year);
                datePickerDialog.show();


            }
        });


        msavegrp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                name=gTitle.getText().toString();
                desc=gDesc.getText().toString();
                amount=gAmount.getText().toString();
                if(name.isEmpty()||desc.isEmpty()||amount.isEmpty()||ItemList.isEmpty()||endDate.isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"Some Field is Empty",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
                else
                {

                    sendDataToRealTimeDatabase();

                }
            }
        });
    }

    private void sendDataToRealTimeDatabase()
    {
        long freq=0;
        name=gTitle.getText().toString().trim();
        desc=gDesc.getText().toString();
        amount=gAmount.getText().toString();
        SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");

        try {
            Date date1 = myFormat.parse(startDate);
            Date date2 = myFormat.parse(endDate);
            assert date2 != null;
            assert date1 != null;
            long diff = date2.getTime() - date1.getTime();
            freq=TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sDura=String.valueOf(freq)+ " days";
        totalMembers=Integer.toString(ItemList.size());
        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        FirebaseUser fUser=firebaseAuth.getCurrentUser();
        DatabaseReference databaseReference=firebaseDatabase.getReference("Groups").child(fUser.getUid());
        grpId= databaseReference.push().getKey();
        GroupModel user=new GroupModel(grpId,name, desc, totalMembers,amount,sDura,startDate,endDate);
        databaseReference.child(grpId).setValue(user);
        DatabaseReference dReference=firebaseDatabase.getReference("Group Members");
        dReference.child(grpId).setValue(ItemList);
        Toast.makeText(getApplicationContext(),"Group Profile Added Sucessfully",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(GroupActivity.this,GroupDisplay.class);
        intent.putExtra("gTitle",name);
        intent.putExtra("grpID",grpId);
        intent.putExtra("sDate",startDate);
        intent.putExtra("eDate",endDate);

        intent.putExtra("gDesc",desc);

        intent.putExtra("gMember",totalMembers);
        //intent.putExtra("gDuration",xyz);
        intent.putExtra("gMoney",amount);
        intent.putExtra("gDuration",sDura);

        progressDialog.dismiss();
        startActivity(intent);
        finish();



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == CONTACT_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {

                results = MultiContactPicker.obtainResult(data);
                ItemList.clear();
                for (int i = 0; i < results.size(); i++) {
                    String name = results.get(i).getDisplayName();
                    String numbers = results.get(i).getPhoneNumbers().get(0).getNumber();

                    phoneBookModel participantsInfo = new phoneBookModel(name, numbers);//For getting phone numbers;
                    //names.append(results.get(i).getDisplayName())//for getting names
                    ItemList.add(participantsInfo);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private boolean checkContactPermission(){
        //check if contact permission was granted or not
        boolean result = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS) == (PackageManager.PERMISSION_GRANTED
        );

        return result;  //true if permission granted, false if not
    }

    private void requestContactPermission(){
        //permissions to request
        String[] permission = {Manifest.permission.READ_CONTACTS};

        ActivityCompat.requestPermissions(this, permission, CONTACT_PERMISSION_CODE);
    }




}