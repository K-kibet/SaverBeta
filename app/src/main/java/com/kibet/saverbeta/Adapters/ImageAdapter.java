package com.kibet.saverbeta.Adapters;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kibet.saverbeta.Model.ModelStatus;
import com.kibet.saverbeta.R;
import com.kibet.saverbeta.activities.ImageViewerActivity;
import com.kibet.saverbeta.fragments.ImageFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    public ArrayList<ModelStatus> imagesList;
    public List<ModelStatus> selected_imageList;
    Context acontext;
    ImageFragment imageFragment;
    public final static String POSITION = "Image Position";

    public ImageAdapter(Context context, ArrayList<ModelStatus> imageList, List<ModelStatus> selected_image, ImageFragment imageFragment){
        this.acontext = context;
        this.imagesList = imageList;
        this.selected_imageList = selected_image;
        this.imageFragment = imageFragment;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(acontext).inflate(R.layout.item_status, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        ModelStatus statusModel = imagesList.get(position);

        Glide.with(acontext).load(statusModel.getFull_path()).into(holder.ivThumbnailImageView);

        if (selected_imageList.contains(imagesList.get(position))){
            holder.opacity.setBackgroundColor(ContextCompat.getColor(acontext, R.color.image_selected));
            holder.opacity.setVisibility(View.VISIBLE);
        }else {
            holder.opacity.setBackgroundColor(ContextCompat.getColor(acontext, R.color.image_not_selected));
            holder.opacity.setVisibility(View.GONE);
        }

        holder.ivThumbnailImageView.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            String stringId = Integer.valueOf(pos).toString();

            Intent intent = new Intent(acontext, ImageViewerActivity.class);
            intent.putExtra(POSITION, stringId);
            intent.putExtra("image", statusModel.getFull_path());
            intent.putExtra("myPath", statusModel.getPath());
            intent.putExtra("myPackage", "1");
            intent.putExtra("type", "" + statusModel.getType());
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            acontext.startActivity(intent);
            ((Activity) acontext).overridePendingTransition(R.anim.enter, R.anim.pop_enter);
        });
    }

    @Override
    public int getItemCount() {
        return imagesList.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.ivThumbnail) ImageView ivThumbnailImageView;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.opacity) LinearLayout opacity;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }

}
