package com.executivestrokes.mcent;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GroupDisplay extends AppCompatActivity{
    ProgressDialog progressDialog;
    //    private List<GroupModel> ItemList;
//    private RecyclerViewAdapter mAdapter;
    private List<phoneBookModel> userList;
    private MembersAdapter userAdapter;
    RecyclerView recyclerView;
    ImageView back;
    TextView gtitle, tMoney, tMemb, tDuration,startDate,endDtae;
    FirebaseAuth firebaseAuth;
    String grpId, uImg;
    Button sendUpi;
    final int UPI_PAYMENT = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_display);
        recyclerView = findViewById(R.id.members);
        gtitle = findViewById(R.id.grpDispTitle);
        sendUpi = findViewById(R.id.sendMoney);
        tMoney = findViewById(R.id.totalDispMoney);
        tMemb = findViewById(R.id.totalDispMembers);
        tDuration = findViewById(R.id.totalDispDuration);
        back=findViewById(R.id.onback);
        startDate=findViewById(R.id.start_date);
        endDtae=findViewById(R.id.end_date);
        startDate.setText(getIntent().getStringExtra("sDate"));
        endDtae.setText(getIntent().getStringExtra("eDate"));
        back=findViewById(R.id.onback);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                startActivity(intent);
                finish();
            }
        });

        //Recycler


        //String fUid=getIntent().getStringExtra("UIID");
        //DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
        //      .child("Groups").child(fUid);


        //PROGRESS DIALOG
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading Data....");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        progressDialog.show();
        grpId = getIntent().getStringExtra("grpID");
        /*

        try {
            Picasso.get().load(getIntent().getStringExtra("grpImg")).into(grpImg);
        }catch (Exception e){
            Picasso.get().load(R.drawable.ic_baseline_self_improvement_24).into(grpImg);
        }

        int mem=Integer.parseInt(getIntent().getStringExtra("gMember"));
        tMemb.setText(mem);
        tDuration.setText("Duration");
        int money=Integer.parseInt(getIntent().getStringExtra("gMoney"));
        tMoney.setText((money/mem)+" /Member");

         */
        /*Query query=reference.orderByChild("groupID").equalTo(grpId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists()){
                        try {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                gtitle.setText(String.valueOf(ds.child("groupTitle").getValue()));
                                gDesc.setText(String.valueOf(ds.child("groupDescription").getValue()));
                                tMemb.setText(String.valueOf(ds.child("totalMember").getValue()));
                                tMoney.setText(String.valueOf(ds.child("tAmount").getValue()));
                                uImg = "" + ds.child("groupIcon").getValue();
                                try {
                                    Picasso.get().load(uImg).into(grpImg);
                                } catch (Exception e) {
                                    Picasso.get().load(R.drawable.ic_baseline_self_improvement_24).into(grpImg);
                                }
                            }
                        }catch(Exception e){
                                Toast.makeText(GroupDisplay.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();


                        }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

         */
        gtitle.setText(String.valueOf(getIntent().getStringExtra("gTitle")));

        int mem = Integer.parseInt(getIntent().getStringExtra("gMember"));
        tMemb.setText(String.valueOf(mem));
        tDuration.setText(getIntent().getStringExtra("gDuration"));
        int money = Integer.parseInt(getIntent().getStringExtra("gMoney"));
        tMoney.setText(getIntent().getStringExtra("gMoney"));
        //Context context= ;
        readData();
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("ddMMyyyyHHmmss", Locale.getDefault());
        String transcId = df.format(c);
        progressDialog.dismiss();

        sendUpi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePayment(String.valueOf((double)(money / mem)), getIntent().getStringExtra("gDesc"), getIntent().getStringExtra("gTitle"), "Contributing Rs. " + String.valueOf(money / mem) + " to " + getIntent().getStringExtra("gTitle") + " in mContri App.",transcId);

            }
        });


    }

    private void readData() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference dReference = firebaseDatabase.getReference("Group Members").child(grpId);
        userList = new ArrayList<>();
        //Query query = reference.orderByChild(firebaseAuth.getUid()).equalTo("donor");
        dReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //userList.clear();
                if (snapshot.exists()) {
                    try {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            System.out.println(dataSnapshot);
                            phoneBookModel user = dataSnapshot.getValue(phoneBookModel.class);
                            userList.add(user);
                        }
                        LinearLayoutManager layoutManager = new LinearLayoutManager(GroupDisplay.this);
                        layoutManager.setReverseLayout(true);
                        layoutManager.setStackFromEnd(true);
                        recyclerView.setLayoutManager(layoutManager);


                        userAdapter = new MembersAdapter(GroupDisplay.this, userList);
                        recyclerView.setAdapter(userAdapter);
                        progressDialog.dismiss();

                        if (userList.isEmpty()) {
                            Toast.makeText(GroupDisplay.this, "No recipients", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    } catch (Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(GroupDisplay.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    void makePayment(String amount, String upiId, String name, String note, String transactionId) {

        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", name)
                .appendQueryParameter("tn", note)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                .build();


        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);

        // will always show a dialog to user to choose an app
        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");

        // check if intent resolves
        if (null != chooser.resolveActivity(getPackageManager())) {
            startActivityForResult(chooser, UPI_PAYMENT);
        } else {
            Toast.makeText(GroupDisplay.this, "No UPI app found, please install one to continue", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case UPI_PAYMENT:
                if ((resultCode == Activity.RESULT_OK) || (resultCode == 1)) {
                    if (data != null) {
                        String trxt = data.getStringExtra("response");
                        Log.d("UPI", "onActivityResult: " + trxt);
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add(trxt);
                        upiPaymentDataOperation(dataList);
                    } else {
                        Log.d("UPI", "onActivityResult: " + "Return data is null");
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                    }
                } else {
                    Log.d("UPI", "onActivityResult: " + "Return data is null"); //when user simply back without payment
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
                break;
        }
    }

    private void upiPaymentDataOperation(ArrayList<String> data) {
        if (isConnectionAvailable(GroupDisplay.this)) {
            String str = data.get(0);
            Log.d("UPIPAY", "upiPaymentDataOperation: " + str);
            String paymentCancel = "";
            if (str == null) str = "discard";
            String status = "";
            String approvalRefNo = "";
            String response[] = str.split("&");
            for (String s : response) {
                String equalStr[] = s.split("=");
                if (equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    } else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                        approvalRefNo = equalStr[1];
                    }
                } else {
                    paymentCancel = "Payment cancelled by user.";
                }
            }

            if (status.equals("success")) {
                //Code to handle successful transaction here.
                Toast.makeText(GroupDisplay.this, "Transaction successful.", Toast.LENGTH_SHORT).show();
                Log.d("UPI", "responseStr: " + approvalRefNo);
            } else if ("Payment cancelled by user.".equals(paymentCancel)) {
                Toast.makeText(GroupDisplay.this, "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(GroupDisplay.this, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(GroupDisplay.this, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            @SuppressLint("MissingPermission") NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }
//        public void makePayment(double amount, String gTitle, String desc){
//            Checkout checkout=new Checkout();
//            checkout.setKeyID("rzp_test_RiubhWbsLyWJAM");
//            try {
//                JSONObject jsonObject=new JSONObject();
//                jsonObject.put("name",gTitle);
//                jsonObject.put("description",desc);
//                jsonObject.put("theme.color","#FF735C");
//                jsonObject.put("currency","INR");
//                jsonObject.put("amount",amount*100);
//                JSONObject retryObj=new JSONObject();
//                retryObj.put("enabled","true");
//                retryObj.put("max_count",4);
//                retryObj.put("retry",retryObj);
//                checkout.open(GroupDisplay.this,jsonObject);
//            } catch (JSONException e) {
//                Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        }
//        @Override
//        public void onBackPressed() {
//            Intent intent = new Intent(getApplicationContext(), Dashboard.class);
//            startActivity(intent);
//            finish();
//        }
//
//        @Override
//        public void onPaymentSuccess(String s) {
//            Toast.makeText(this, ""+s, Toast.LENGTH_SHORT).show();
//        }

        // all parameters to it such as upi id,name, description and others.
        /*final EasyUpiPayment easyUpiPayment = new EasyUpiPayment.Builder()
                .with(this)
                // on below line we are adding upi id.
                .setPayeeVpa(upiId)
                // on below line we are setting name to which we are making oayment.
                .setPayeeName(name)
                // on below line we are passing transaction id.
                .setTransactionId(transactionId)
                // on below line we are passing transaction ref id.
                .setTransactionRefId(transactionId)
                // on below line we are adding description to payment.
                .setDescription(note)
                // on below line we are passing amount which is being paid.
                .setAmount(amount)
                // on below line we are calling a build method to build this ui.
                .build();
        // on below line we are calling a start
        // payment method to start a payment.
        try {
            easyUpiPayment.setDefaultPaymentApp(PaymentApp.GOOGLE_PAY);
            easyUpiPayment.startPayment();
            // on below line we are calling a set payment
            // status listener method to call other payment methods.
            easyUpiPayment.setPaymentStatusListener(this);
        } catch (Exception exception) {
            exception.printStackTrace();
            Toast.makeText(this, "" + ("Error: " + exception.getMessage()), Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onTransactionCompleted(TransactionDetails transactionDetails) {
        //Toast.makeText(this, "Transaction successfully completed..", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTransactionSuccess() {
        //Toast.makeText(this, "Transaction successfully completed..", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTransactionSubmitted() {
        Log.e("TAG", "TRANSACTION SUBMIT");
    }

    @Override
    public void onTransactionFailed() {
       // Toast.makeText(this, "Transaction Failed !..", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTransactionCancelled() {
        //Toast.makeText(this, "Transaction Cancelled !..", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAppNotFound() {
        //Toast.makeText(this, "Transaction App Not Found !..", Toast.LENGTH_SHORT).show();
    }

         */



    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Dashboard.class);
        startActivity(intent);
        finish();
    }
}