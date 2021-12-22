package com.executivestrokes.mcent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class setProfile extends AppCompatActivity {

    private CardView mgetuserimage;
    private ImageView mgetuserimageinimageview,onbackPressed;
    private static int PICK_IMAGE=123;
    private Uri imagepath;

    private EditText mgetusername,emailId,userupiid;
    TextView mobileNumber;

    private android.widget.Button msaveprofile;

    private FirebaseAuth firebaseAuth;
    private String name, mail="",userUpiIds="";

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private String ImageUriAcessToken="";

    ProgressBar mprogressbarofsetprofile;
    FirebaseUser fUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();


        mgetusername=findViewById(R.id.getusername);
        mgetuserimageinimageview=findViewById(R.id.getuserimage);
        mobileNumber=findViewById(R.id.mobile_edt);
        emailId=findViewById(R.id.email_id);
        userupiid=findViewById(R.id.upi_id);
        msaveprofile=findViewById(R.id.createaccount);
        mprogressbarofsetprofile=findViewById(R.id.progressbarofsetProfile);
        onbackPressed=findViewById(R.id.onback);


        fUser=firebaseAuth.getCurrentUser();
        mobileNumber.setText(fUser.getPhoneNumber());
        mgetuserimageinimageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent,PICK_IMAGE);
            }
        });
        String checking=getIntent().getStringExtra("check");
        if (checking.equals("otpauth")){
            msaveprofile.setText("Save & Next");
        }
        onbackPressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checking.equals("otpauth"))
                {
                    Intent intent=new Intent(setProfile.this,OtpAuthentication.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Intent intent=new Intent(setProfile.this,Profile.class);
                    startActivity(intent);
                    finish();
                }

            }
        });


        msaveprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name=mgetusername.getText().toString();
                userUpiIds=userupiid.getText().toString();
                if(name.isEmpty()|| userUpiIds.isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"Name is Empty",Toast.LENGTH_SHORT).show();

                }
                else if(imagepath==null)
                {
                    sendDataToRealTimeDatabase();
                    mprogressbarofsetprofile.setVisibility(View.INVISIBLE);
                    Intent intent=new Intent(setProfile.this, Dashboard.class);
                    startActivity(intent);
                    finish();
                }
                else
                {

                    mprogressbarofsetprofile.setVisibility(View.VISIBLE);
                    sendImagetoStorage();
                    mprogressbarofsetprofile.setVisibility(View.INVISIBLE);
                    if (checking.equals("otpauth")){
                        Intent intent=new Intent(setProfile.this, Dashboard.class);
                        startActivity(intent);
                    }
                    else {
                        Intent intent=new Intent(setProfile.this, Profile.class);
                        startActivity(intent);
                    }
                    finish();


                }
            }
        });






    }




    private void sendImagetoStorage()
    {

        StorageReference imageref=storageReference.child("Images").child(firebaseAuth.getUid()).child("Profile Pic");

        //Image compresesion

        Bitmap bitmap=null;
        try {
            bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),imagepath);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,25,byteArrayOutputStream);
        byte[] data=byteArrayOutputStream.toByteArray();

        ///putting image to storage

        UploadTask uploadTask=imageref.putBytes(data);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imageref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        ImageUriAcessToken=uri.toString();
                        Toast.makeText(getApplicationContext(),"URI get sucess",Toast.LENGTH_SHORT).show();
                        sendDataToRealTimeDatabase();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"URI get Failed",Toast.LENGTH_SHORT).show();
                    }


                });
                Toast.makeText(getApplicationContext(),"Image is uploaded",Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Image Not UPdloaded",Toast.LENGTH_SHORT).show();
            }
        });





    }
    private void sendDataToRealTimeDatabase()
    {


        name=mgetusername.getText().toString().trim();
        mail=emailId.getText().toString().trim();
        userUpiIds=userupiid.getText().toString().trim();

        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference=firebaseDatabase.getReference("Users");
        FirebaseUser fUser=firebaseAuth.getCurrentUser();
        userprofile user=new userprofile(name, fUser.getPhoneNumber(), fUser.getUid(),ImageUriAcessToken.toString(),mail,userUpiIds);
        databaseReference.child(fUser.getUid()).setValue(user);
        Toast.makeText(getApplicationContext(),"User Profile Added Sucessfully",Toast.LENGTH_SHORT).show();





    }
//    private void sendDataTocloudFirestore() {
//
//
//        DocumentReference documentReference=firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
//        Map<String , Object> userdata=new HashMap<>();
//        userdata.put("name",name);
//        userdata.put("image",ImageUriAcessToken);
//        userdata.put("uid",firebaseAuth.getUid());
//        userdata.put("status","Online");
//
//        documentReference.set(userdata).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Toast.makeText(getApplicationContext(),"Data on Cloud Firestore send success",Toast.LENGTH_SHORT).show();
//
//            }
//        });
//
//
//
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode==PICK_IMAGE && resultCode==RESULT_OK)
        {
            imagepath=data.getData();
            mgetuserimageinimageview.setImageURI(imagepath);
        }




        super.onActivityResult(requestCode, resultCode, data);
    }

}