package com.example.familymart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.familymart.eventbus.MyUpdateCartEvent;
import com.example.familymart.model.Cart;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.greenrobot.eventbus.EventBus;

public class Qrcode extends AppCompatActivity {


    Button done;
    ImageView imageView;
    ImageView btnBack;
    EditText detail;
    TextView time;

    String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);


        imageView = (ImageView) findViewById(R.id.imageview);
        done = findViewById(R.id.done);
        detail = findViewById(R.id.show);
        btnBack = findViewById(R.id.btnBack);
        time = findViewById(R.id.time);

        Intent intent = getIntent();
        String txt = intent.getStringExtra("qrcode");
        String datetime = intent.getStringExtra("time");
        String create = intent.getStringExtra("create");

        time.setText("Arrival Time: "+datetime);

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(txt, BarcodeFormat.QR_CODE, 500, 500);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            imageView.setImageBitmap(bitmap);

        } catch (Exception e) {
            e.printStackTrace();
        }

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                Query cartQuery = ref.child("Cart").child(currentuser);

                cartQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot cartSnapshot : dataSnapshot.getChildren()) {
                            Cart cart = cartSnapshot.getValue(Cart.class);
                            cart.setQuantity(0);
                            FirebaseDatabase.getInstance()
                                    .getReference("Cart")
                                    .child(currentuser)
                                    .child(cart.getKey())
                                    .setValue(cart)
                                    .addOnSuccessListener(aVoid -> EventBus.getDefault().postSticky(new MyUpdateCartEvent()));
                            cartSnapshot.getRef().removeValue();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });


                Intent intent = new Intent(Qrcode.this, Home.class);
                startActivity(intent);
                finish();
            }


        });

        detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Qrcode.this, History.class);
                intent.putExtra("time",create);
                startActivity(intent);
                finish();
            }
        });

        btnBack.setOnClickListener(view -> {
            finish();
        });
    }

}

