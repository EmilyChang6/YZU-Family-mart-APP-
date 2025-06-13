package com.example.familymart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.familymart.R;
import com.example.familymart.listener.CartLoadListener;
import com.example.familymart.model.Cart;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyHistoryAdapter extends RecyclerView.Adapter<MyHistoryAdapter.MyHistoryViewHolder>{

    private Context context;
    private List<Cart> cartList;

    String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public MyHistoryAdapter(Context context, List<Cart> cartList, CartLoadListener cartLoadListener) {
        this.context = context;
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public MyHistoryAdapter.MyHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyHistoryAdapter.MyHistoryViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.history,parent,false));


    }

    @Override
    public void onBindViewHolder(@NonNull MyHistoryAdapter.MyHistoryViewHolder holder, int position) {
        holder.item.setText(new StringBuilder().append(cartList.get(position).getName()));
        holder.quantity.setText(new StringBuilder("x").append(cartList.get(position).getQuantity()));
        Glide.with(context)
                .load(cartList.get(position).getImage())
                .into(holder.imageView);
    }


    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public class MyHistoryViewHolder extends RecyclerView.ViewHolder{


        @BindView(R.id.qrcode)
        ImageView imageView;
        @BindView(R.id.name)
        TextView item;
        @BindView(R.id.quantity)
        TextView quantity;

        Unbinder unbinder;
        public MyHistoryViewHolder(View inflate) {
            super(inflate);
            unbinder= ButterKnife.bind(this,itemView);
        }
    }



}

