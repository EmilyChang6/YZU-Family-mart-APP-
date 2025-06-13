package com.example.familymart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.familymart.R;
import com.example.familymart.model.Cart;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyQrcodeAdapter extends RecyclerView.Adapter<MyQrcodeAdapter.MyQrcodeViewHolder> {

    private Context context;
    private List<Cart> cartList;

    String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public MyQrcodeAdapter(Context context, List<Cart> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public MyQrcodeAdapter.MyQrcodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyQrcodeAdapter.MyQrcodeViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.history,parent,false));


    }

    @Override
    public void onBindViewHolder(@NonNull MyQrcodeViewHolder holder, int position) {
        Glide.with(context)
                .load(cartList.get(position).getQrcode())
                .into(holder.imageView);
    }


    @Override
    public int getItemCount() {
        return cartList.size();
    }


    public class MyQrcodeViewHolder extends RecyclerView.ViewHolder{


        @BindView(R.id.qrcode)
        ImageView imageView;

        Unbinder unbinder;
        public MyQrcodeViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder= ButterKnife.bind(this,itemView);
        }
    }

}
