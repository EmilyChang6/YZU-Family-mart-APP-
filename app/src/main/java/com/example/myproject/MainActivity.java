package com.example.myproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("DEBUG", )
    }

    @Override
    protected void onStart(){

        super.onStart();
        Log.d( "DEBUG","onStart");
        Log.i("info","Information");
        Log.e("error","Error");
        Log.w("warning","Warn");
    }

    public void gotoPage2(View view){
        Intent intent=new Intent(this,page2Activity.class);
        startActivity(intent);
        Log.d( "DEBUG","gotoPage2");

    }


}