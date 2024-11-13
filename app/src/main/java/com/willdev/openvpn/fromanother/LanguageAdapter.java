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

import com.bumptech.glide.Glide;
import com.willdev.openvpn.R;
import com.willdev.openvpn.api.WebAPI;
import com.willdev.openvpn.interfaces.ClickListener;
import com.willdev.openvpn.model.Language;

import java.util.List;

import ph.gemeaux.materialloadingindicator.MaterialCircularIndicator;

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.MyViewHolder> {

    Context context;
    List<Language> languageList;
    ClickListener<Language> clickListener;

    public LanguageAdapter(Context context, List<Language> languageList, ClickListener<Language> clickListener) {
        this.context = context;
        this.languageList = languageList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_language, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.tvLanguageName.setText(languageList.get(position).name);

        if (!languageList.get(position).flag.equals("")) {
            Glide.with(context.getApplicationContext()).load(WebAPI.ADMIN_PANEL_API + languageList.get(position).flag)
                    .placeholder(R.drawable.rounded_rect).into(holder.ivFlag);
        } else {
            holder.ivFlag.setImageResource(R.drawable.us);
        }

        holder.itemView.setOnClickListener(v -> {
            MaterialCircularIndicator progressDialog = new MaterialCircularIndicator(context);
            progressDialog.show();
            clickListener.onClick(languageList.get(position));
        });
    }

    @Override
    public int getItemCount() {
        if (languageList != null && languageList.size() > 0) {
            return languageList.size();
        }
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvLanguageName;
        ImageView ivFlag;

        public MyViewHolder(View view) {
            super(view);
            tvLanguageName = view.findViewById(R.id.tvLanguage);
            ivFlag = view.findViewById(R.id.ivFlag);
        }
    }

}