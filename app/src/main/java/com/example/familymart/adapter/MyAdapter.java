package com.example.familymart.adapter;

import static android.content.Intent.getIntent;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.familymart.R;
import com.example.familymart.eventbus.MyUpdateCartEvent;
import com.example.familymart.listener.CartLoadListener;
import com.example.familymart.listener.ClickListener;
import com.example.familymart.model.Cart;
import com.example.familymart.model.Product;
import com.example.familymart.model.User;
import com.example.familymart.utils.SpaceItemDecoration;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{
    private Context context;
    private List<Product> productList;
    private CartLoadListener cartLoadListener;

    String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public MyAdapter(Context context, List<Product> productList, CartLoadListener cartLoadListener) {
        this.context = context;
        this.productList = productList;
        this.cartLoadListener = cartLoadListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context)
                .load(productList.get(position).getImage())
                .into(holder.imageView);

        holder.price.setText(new StringBuilder("NT$").append(productList.get(position).getPrice()));
        holder.name.setText(new StringBuilder().append(productList.get(position).getName()));

        holder.setListener((view, adapterPosition) -> {
            addToCart(productList.get(position));
        });


    }

    private void addToCart(Product product) {

        DatabaseReference userCart= FirebaseDatabase
                .getInstance()
                .getReference("Cart")
                .child(currentuser);
        userCart.child(product.getKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            Cart cart=snapshot.getValue(Cart.class);
                            cart.setQuantity(cart.getQuantity()+1);
                            cart.setRemain(cart.getRemain()-1);
                            Map<String,Object> updateData=new HashMap<>();
                            updateData.put("quantity",cart.getQuantity());
                            updateData.put("remain",cart.getRemain());
                            updateData.put("total",cart.getQuantity()*Float.parseFloat(cart.getPrice()));

                            userCart.child(product.getKey())
                                    .updateChildren(updateData)
                                    .addOnSuccessListener(aVoid ->{
                                        cartLoadListener.onCartLoadFailed("Add To Cart Success");

                                    })
                                    .addOnFailureListener(e -> cartLoadListener.onCartLoadFailed(e.getMessage()));
                        }
                        else{

                            Cart cart=new Cart();
                            cart.setName(product.getName());
                            cart.setImage(product.getImage());
                            cart.setQrcode(product.getQrcode());
                            cart.setKey(product.getKey());
                            cart.setPrice(product.getPrice());
                            cart.setRemain(product.getRemain());
                            cart.setQuantity(1);
                            cart.setTotal(Float.parseFloat(cart.getPrice()));

                            userCart.child(product.getKey())
                                    .setValue(cart)
                                    .addOnSuccessListener(aVoid ->{
                                        cartLoadListener.onCartLoadFailed("Add To Cart Success");

                                    })
                                    .addOnFailureListener(e -> cartLoadListener.onCartLoadFailed(e.getMessage()));
                        }
                        EventBus.getDefault().postSticky(new MyUpdateCartEvent());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        cartLoadListener.onCartLoadFailed(error.getMessage());
                    }
                });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.image)
        ImageView imageView;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.price)
        TextView price;

        ClickListener listener;

        public void setListener(ClickListener listener) {
            this.listener = listener;
        }

        private Unbinder unbinder;
        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            unbinder= ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            listener.onRecyclerClick(v,getAdapterPosition());
        }
    }


}
