package com.example.familymart;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.familymart.listener.CartLoadListener;
import com.example.familymart.model.Cart;
import com.example.familymart.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class Payment extends AppCompatActivity {


    TextView total;
    ImageView btnBack;
    Button next;
    RadioButton cash;
    RadioButton credit;
    EditText creditNum;
    EditText date;
    EditText cvn;
    String qrcode;
    LinearLayout show;

    private EditText applydate = null;
    private EditText applytime = null;

    DatabaseReference product,history;
    String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        total=findViewById(R.id.total);
        btnBack=findViewById(R.id.btnBack);
        next =(Button)findViewById(R.id.next);
        creditNum= (EditText)findViewById(R.id.CreditNum);
        date= (EditText)findViewById(R.id.date);
        cvn= (EditText)findViewById(R.id.cvn);
        cash = (RadioButton) findViewById(R.id.cash);
        credit = (RadioButton) findViewById(R.id.credit);
        show = findViewById(R.id.creditcard);

        TimeZone time = TimeZone.getTimeZone("Etc/GMT-8");
        TimeZone.setDefault(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date dt=new Date();
        String dts=sdf.format(dt);

        Intent intent = getIntent();
        int pay = intent.getIntExtra("total",0);
        total.setText("Total Cost: NT$"+pay);

        show.setVisibility(View.INVISIBLE);
        credit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked)
                    show.setVisibility(View.VISIBLE);
                else
                    show.setVisibility(View.INVISIBLE);

            }
        });




        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(credit.isChecked()){
                    addToHistory(pay,"credit card");
                }else if(cash.isChecked()){
                    addToHistory(pay,"cash");
                }else{
                    Toast.makeText(getApplicationContext(), "Select the radio button",Toast.LENGTH_LONG).show();
                }



            }
        });

        btnBack.setOnClickListener(view -> finish());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        applydate = findViewById(R.id.applydate);
        applytime = findViewById(R.id.applytime);




    }


    public void datePicker(View v) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                String datetime = String.valueOf(year) + "-" + String.valueOf(month+1) + "-" + String.valueOf(day);
                applydate.setText(datetime);
            }
        }, year, month, day).show();
    }

    public void timePicker(View v) {
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);

        new TimePickerDialog(v.getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String datetime = String.valueOf(hourOfDay) + ":" + String.valueOf(minute);
                applytime.setText(datetime);
            }
        }, hourOfDay, minute,true).show();

    }

    void addToHistory(int total,String payment){

        TimeZone time = TimeZone.getTimeZone("Etc/GMT-8");
        TimeZone.setDefault(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date dt=new Date();
        String dts=sdf.format(dt);

        history = FirebaseDatabase.getInstance().getReference("History").child(currentuser);
        product = FirebaseDatabase.getInstance().getReference("Product");

        history.child(dts).child("total cost").setValue(total);
        history.child(dts).child("payment").setValue(payment);
        history.child(dts).child("arrival time").setValue(applydate.getText().toString()+"  "+applytime.getText().toString());

        String datetime = applydate.getText().toString()+" "+applytime.getText().toString();

        qrcode=String.format("Arrival Time: %s \nTotal: NT$ %d \nPayment: %s",datetime,total,payment);
        Intent intent = new Intent(Payment.this, Qrcode.class);
        intent.putExtra("qrcode",qrcode);
        intent.putExtra("time",datetime);
        intent.putExtra("create",dts);
        startActivity(intent);

    }



}