package com.example.familymart.adapter;

import android.app.AlertDialog;
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
import com.example.familymart.model.Cart;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.MyCartViewHolder>{
    private Context context;
    private List<Cart> cartList;

    String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public MyCartAdapter(Context context, List<Cart> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public MyCartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyCartViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.cart,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyCartViewHolder holder, int position) {
        Glide.with(context)
                .load(cartList.get(position).getImage())
                .into(holder.imageView);

        holder.price.setText(new StringBuilder("NT$").append(cartList.get(position).getPrice()));
        holder.name.setText(new StringBuilder().append(cartList.get(position).getName()));
        holder.quantity.setText(new StringBuilder().append(cartList.get(position).getQuantity()));
        holder.remain.setText(new StringBuilder().append(cartList.get(position).getRemain()));


        holder.minus.setOnClickListener(view -> {
            plusremainCart(holder,cartList.get(position));
            minusCartItem(holder,cartList.get(position));
        });

        holder.plus.setOnClickListener(view -> {
            plusCartItem(holder,cartList.get(position));
            minusremainCart(holder,cartList.get(position));
        });

        holder.delete.setOnClickListener(view -> {
            AlertDialog dialog=new AlertDialog.Builder(context)
                    .setTitle("Delete item")
                    .setMessage("Do you really want to delete item")
                    .setNegativeButton("CANCEL", (dialog1,which) -> dialog1.dismiss())
                    .setPositiveButton("OK",(dialog2, which) -> {

                        notifyItemRemoved(position);

                        deleteFromFirebase(cartList.get(position));
                        dialog2.dismiss();
                    }).create();
            dialog.show();

        });


    }

    private void plusremainCart(MyCartViewHolder holder, Cart cart) {
        if(cart.getQuantity()>1) {
            cart.setRemain(cart.getRemain() + 1);

            holder.remain.setText(new StringBuilder().append(cart.getRemain()));
            updateFirebase(cart);
        }
    }

    private void minusremainCart(MyCartViewHolder holder, Cart cart) {
        if(cart.getRemain()>0) {
            cart.setRemain(cart.getRemain() - 1);

            holder.remain.setText(new StringBuilder().append(cart.getRemain()));
            updateFirebase(cart);
        }
    }

    private void deleteFromFirebase(Cart cart) {
        FirebaseDatabase.getInstance()
                .getReference("Cart")
                .child(currentuser)
                .child(cart.getKey())
                .removeValue()
                .addOnSuccessListener(aVoid -> EventBus.getDefault().postSticky(new MyUpdateCartEvent()));

    }

    private void plusCartItem(MyCartViewHolder holder, Cart cart) {
        if(cart.getRemain()>0) {
            cart.setQuantity(cart.getQuantity() + 1);
            cart.setTotal(cart.getQuantity() * Float.parseFloat(cart.getPrice()));


            holder.quantity.setText(new StringBuilder().append(cart.getQuantity()));
            updateFirebase(cart);
        }
    }

    private void minusCartItem(MyCartViewHolder holder, Cart cart) {
        if(cart.getQuantity()>1){
            cart.setQuantity(cart.getQuantity()-1);
            cart.setTotal(cart.getQuantity()*Float.parseFloat(cart.getPrice()));

            holder.quantity.setText(new StringBuilder().append(cart.getQuantity()));
            updateFirebase(cart);
        }
    }

    private void updateFirebase(Cart cart) {
        FirebaseDatabase.getInstance()
                .getReference("Cart")
                .child(currentuser)
                .child(cart.getKey())
                .setValue(cart)
                .addOnSuccessListener(aVoid -> EventBus.getDefault().postSticky(new MyUpdateCartEvent()));
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }


    public class MyCartViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.minus)
        ImageView minus;
        @BindView(R.id.plus)
        ImageView plus;
        @BindView(R.id.delete)
        ImageView delete;
        @BindView(R.id.image)
        ImageView imageView;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.price)
        TextView price;
        @BindView(R.id.quantity)
        TextView quantity;
        @BindView(R.id.remain)
        TextView remain;


        Unbinder unbinder;
        public MyCartViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder= ButterKnife.bind(this,itemView);
        }
    }
}
