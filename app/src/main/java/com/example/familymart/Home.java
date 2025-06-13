package com.example.familymart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.familymart.adapter.MyAdapter;
import com.example.familymart.eventbus.MyUpdateCartEvent;
import com.example.familymart.listener.CartLoadListener;
import com.example.familymart.listener.ProductLoadListener;
import com.example.familymart.model.Cart;
import com.example.familymart.model.Product;
import com.example.familymart.model.User;
import com.example.familymart.utils.SpaceItemDecoration;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nex3z.notificationbadge.NotificationBadge;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Home extends AppCompatActivity implements ProductLoadListener, CartLoadListener{

    @BindView(R.id.recycler_product)
    RecyclerView recyclerProduct;
    @BindView(R.id.mainLayout)
    RelativeLayout mainLayout;
    @BindView(R.id.badge)
    NotificationBadge badge;
    @BindView(R.id.btnCart)
    FrameLayout btnCart;
    @BindView(R.id.logout)
    ImageView logout;

    ProductLoadListener productLoadListener;
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
        countCartItem();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        init();
        loadProductFromFirebase();
        countCartItem();

    }

    private void loadProductFromFirebase() {
        List<Product> products=new ArrayList<>();
        FirebaseDatabase.getInstance()
                .getReference("Product")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for(DataSnapshot productSnapshot:snapshot.getChildren()){
                                Product product=productSnapshot.getValue(Product.class);
                                product.setKey(productSnapshot.getKey());
                                products.add(product);
                            }
                            productLoadListener.onProductLoadSuccess(products);

                        }
                        else
                            productLoadListener.onProductLoadFailed("Can't find product");


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        productLoadListener.onProductLoadFailed(error.getMessage());
                    }
                });
    }

    private void init(){
        ButterKnife.bind(this);

        productLoadListener=this;
        cartLoadListener=this;

        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,2);
        recyclerProduct.setLayoutManager(gridLayoutManager);
        recyclerProduct.addItemDecoration(new SpaceItemDecoration());


        btnCart.setOnClickListener(view -> {
            Intent intent = new Intent(Home.this,CartActivity.class);
            startActivity(intent);
        });

        logout.setOnClickListener(view -> {
            Intent intent = new Intent(Home.this,LoginActivity.class);
            startActivity(intent);
        });


    }


    @Override
    public void onProductLoadSuccess(List<Product> productList) {
        MyAdapter adapter=new MyAdapter(this,productList,cartLoadListener);
        recyclerProduct.setAdapter(adapter);
    }

    @Override
    public void onProductLoadFailed(String message) {
        Snackbar.make(mainLayout,message,Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onCartLoadSuccess(List<Cart> cartList) {
        int cartSum=0;
        for(Cart cart:cartList){
            cartSum+=cart.getQuantity();
        }
        badge.setNumber(cartSum);
    }

    @Override
    public void onCartLoadFailed(String message) {

        Snackbar.make(mainLayout,message,Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        countCartItem();
    }

    private void countCartItem() {


        List<Cart> carts=new ArrayList<>();
        FirebaseDatabase
                .getInstance().getReference("Cart")
                .child(currentuser)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot cartSnapshot:snapshot.getChildren()){
                            Cart cart=cartSnapshot.getValue(Cart.class);
                            cart.setKey(cartSnapshot.getKey());
                            carts.add(cart);
                        }
                        cartLoadListener.onCartLoadSuccess(carts);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        cartLoadListener.onCartLoadFailed(error.getMessage());
                    }
                });
    }
}