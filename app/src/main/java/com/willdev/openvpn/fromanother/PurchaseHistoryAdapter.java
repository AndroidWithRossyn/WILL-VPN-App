package com.willdev.openvpn.fromanother;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.willdev.openvpn.R;
import com.willdev.openvpn.model.PurchaseHistory;
import java.util.List;


public class PurchaseHistoryAdapter extends RecyclerView.Adapter<PurchaseHistoryAdapter.MyViewHolder> {

    Context context;
    List<PurchaseHistory> phList;

    public PurchaseHistoryAdapter(Context context, List<PurchaseHistory> phList) {
        this.context = context;
        this.phList = phList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_purchase_history, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.tvPlanName.setText(phList.get(position).type);
        holder.tvPrice.setText(phList.get(position).amount);
        holder.tvDate.setText(phList.get(position).date_created);

        if (phList.get(position).payment_method.equals("Stripe"))
            holder.ivPaymentMethod.setImageDrawable(context.getResources().getDrawable(R.drawable.stripe_logo));
        else
            holder.ivPaymentMethod.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_google_play));
    }

    @Override
    public int getItemCount() {
        if (phList != null && phList.size() > 0) {
            return phList.size();
        }
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvPlanName;
        TextView tvDate;
        TextView tvPrice;
        ImageView ivPaymentMethod;

        public MyViewHolder(View view) {
            super(view);
            tvPlanName = view.findViewById(R.id.tvPlanName);
            tvDate = view.findViewById(R.id.tvDate);
            tvPrice = view.findViewById(R.id.tvPrice);
            ivPaymentMethod = view.findViewById(R.id.ivPaymentMethod);
        }
    }

}