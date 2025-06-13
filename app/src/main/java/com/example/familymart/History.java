package com.example.familymart;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.familymart.adapter.MyAdapter;
import com.example.familymart.adapter.MyHistoryAdapter;
import com.example.familymart.eventbus.MyUpdateCartEvent;
import com.example.familymart.listener.CartLoadListener;
import com.example.familymart.model.Cart;
import com.example.familymart.model.User;
import com.example.familymart.utils.SpaceItemDecoration;
import com.google.android.material.snackbar.Snackbar;
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
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;





public class History extends AppCompatActivity implements CartLoadListener {

    @BindView(R.id.recycler_history)
    RecyclerView recyclerHistory;
    @BindView(R.id.mainLayout)
    RelativeLayout mainLayout;
    @BindView(R.id.btnBack)
    ImageView btnBack;

    Button done;
    DatabaseReference product,history,user;
    CartLoadListener cartLoadListener;
    String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();

    ImageView qrCode;

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        if(EventBus.getDefault().hasSubscriberForEvent(MyUpdateCartEvent.class))
            EventBus.getDefault().removeStickyEvent(MyUpdateCartEvent.class);
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void onUpdateCart(MyUpdateCartEvent event){
        loadCartFromFirebase();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        done=findViewById(R.id.done);
        qrCode = findViewById(R.id.qrcode);

        history = FirebaseDatabase.getInstance().getReference("History").child(currentuser);
        product = FirebaseDatabase.getInstance().getReference("Product");
        user = FirebaseDatabase.getInstance().getReference("User");


        Intent intent = getIntent();
        String datetime = intent.getStringExtra("time");

        init();

        List<Cart> carts=new ArrayList<>();
        FirebaseDatabase.getInstance()
                .getReference("Cart")
                .child(currentuser)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for(DataSnapshot cartSnapshot:snapshot.getChildren()){
                                Cart cart=cartSnapshot.getValue(Cart.class);
                                cart.setKey(cartSnapshot.getKey());
                                history.child(datetime).child(cart.getName()).setValue(cart.getQuantity());
                                product.child(cartSnapshot.getKey()).child("remain").setValue(cart.getRemain());
                                carts.add(cart);
                            }

                            cartLoadListener.onCartLoadSuccess(carts);

                        }
                        else
                            cartLoadListener.onCartLoadFailed("Cart empty");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        cartLoadListener.onCartLoadFailed(error.getMessage());
                    }
                });


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                Query cartQuery = ref.child("Cart").child(currentuser);

                cartQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot cartSnapshot: dataSnapshot.getChildren()) {
                            Cart cart=cartSnapshot.getValue(Cart.class);
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


                Intent intent = new Intent(History.this, Home.class);
                startActivity(intent);
                finish();
            }


        });

    }
    private void loadCartFromFirebase() {
        List<Cart> carts=new ArrayList<>();
        FirebaseDatabase.getInstance()
                .getReference("Cart")
                .child(currentuser)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for(DataSnapshot cartSnapshot:snapshot.getChildren()){
                                Cart cart=cartSnapshot.getValue(Cart.class);
                                cart.setKey(cartSnapshot.getKey());
                                carts.add(cart);
                            }
                            cartLoadListener.onCartLoadSuccess(carts);

                        }
                        else
                            cartLoadListener.onCartLoadFailed("Cart empty");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        cartLoadListener.onCartLoadFailed(error.getMessage());
                    }

                });
    }

    private void init(){
        ButterKnife.bind(this);

        cartLoadListener=this;

        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,2);
        recyclerHistory.setLayoutManager(gridLayoutManager);
        recyclerHistory.addItemDecoration(new SpaceItemDecoration());

        btnBack.setOnClickListener(view -> {
            finish();
        });

    }


    @Override
    public void onCartLoadSuccess(List<Cart> cartList) {
        MyHistoryAdapter adapter=new MyHistoryAdapter(this,cartList,cartLoadListener);
        recyclerHistory.setAdapter(adapter);

    }

    @Override
    public void onCartLoadFailed(String message) {
        Snackbar.make(mainLayout,message,Snackbar.LENGTH_LONG).show();

    }



}