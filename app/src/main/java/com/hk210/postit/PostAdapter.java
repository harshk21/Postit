package com.hk210.postit;

import android.content.Context;
import android.media.Image;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;
import java.util.Date;
import java.util.Spliterators;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {



    ArrayList<PostModel> dataList;
    Context mContext;
    FirebaseFirestore mFirestore;

    public PostAdapter(ArrayList<PostModel> dataList) {
        this.dataList = dataList;
    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext=parent.getContext();
        mFirestore = FirebaseFirestore.getInstance();
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.post_list_item, parent, false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        String title_text = dataList.get(position).getTitle();
        holder.setTitleText(title_text);
        String description_text = dataList.get(position).getDescription();
        holder.setDesText(description_text);
        String image_url = dataList.get(position).getPost();
        holder.setBlogImage(image_url);

        long millisecond = dataList.get(position).getTimestamp().getTime();
        String dateString = DateFormat.format("MM/dd/yyyy",new Date(millisecond)).toString();
        holder.setTimeText(dateString);

        String user_id_text = dataList.get(position).getUserid();
        mFirestore.collection("Users").document(user_id_text).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){

                        String user_name = task.getResult().getString("name");
                        String user_image = task.getResult().getString("image");

                        holder.setUserData(user_name,user_image);

                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView t1,t2,t3,t7;
        ImageView t4;
        CircleImageView t5;
        View mView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

        }
        public void setTitleText(String textTi){
            t1 = mView.findViewById(R.id.title_text);
            t1.setText(textTi);
        }

        public void setDesText(String textde){
            t2 = mView.findViewById(R.id.descrip_text);
            t2.setText(textde);
        }

        public void setTimeText(String dateTime){
            t7 = mView.findViewById(R.id.time_text);
            t7.setText(dateTime);
        }

        public void setBlogImage(String downloadUri){
            t4 = mView.findViewById(R.id.post_list_imageview);
            Glide.with(mContext).load(downloadUri).into(t4);
        }

        public void setUserData(String name, String image){

            t3 = mView.findViewById(R.id.user_id_text);
            t5 = mView.findViewById(R.id.post_user_image);

            t3.setText(name);
            Glide.with(mContext).load(image).into(t5);


        }
    }
}
