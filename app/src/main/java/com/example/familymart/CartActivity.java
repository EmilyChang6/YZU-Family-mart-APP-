package com.example.familymart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.familymart.adapter.MyCartAdapter;
import com.example.familymart.eventbus.MyUpdateCartEvent;
import com.example.familymart.listener.CartLoadListener;
import com.example.familymart.model.Cart;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CartActivity extends AppCompatActivity implements CartLoadListener {

    @BindView(R.id.recycler_cart)
    RecyclerView recyclerCart;
    @BindView(R.id.mainLayout)
    RelativeLayout mainLayout;
    @BindView(R.id.btnBack)
    ImageView btnBack;
    @BindView(R.id.total)
    TextView total;
    @BindView(R.id.button)
    Button button;

    CartLoadListener cartLoadListener;

    String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();

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
        setContentView(R.layout.activity_cart);

        init();
        loadCartFromFirebase();


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
                        else {

                            cartLoadListener.onCartLoadFailed("Cart empty");
                            finish();

                        }
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
        LinearLayoutManager layoutManager =new LinearLayoutManager(this);
        recyclerCart.setLayoutManager(layoutManager);
        recyclerCart.addItemDecoration(new DividerItemDecoration(this,layoutManager.getOrientation()));
        btnBack.setOnClickListener(view -> finish());

    }

    @Override
    public void onCartLoadSuccess(List<Cart> cartList) {
        int sum=0;
        for(Cart cart:cartList){
            sum+=cart.getTotal();
        }

        int finalSum = sum;
        button.setOnClickListener(view -> {
            Intent intent = new Intent(this, Payment.class);
            intent.putExtra("total", finalSum);
            startActivity(intent);
        });

        total.setText(new StringBuilder("NT$").append(sum));
        MyCartAdapter adapter=new MyCartAdapter(this,cartList);
        recyclerCart.setAdapter(adapter);



    }

    @Override
    public void onCartLoadFailed(String message) {
        Snackbar.make(mainLayout,message,Snackbar.LENGTH_LONG).show();

    }
}