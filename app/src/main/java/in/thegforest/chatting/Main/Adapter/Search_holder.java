package in.thegforest.chatting.Main.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;

import in.thegforest.chatting.R;


public class Search_holder extends RecyclerView.ViewHolder {
    View view;
    public Search_holder(@NonNull View itemView) {
        super(itemView);
        view= itemView;
    }

    public void setName(String name){

        TextView tv=view.findViewById(R.id.name);


        tv.setText(name);


    }
    public void setImg(String url, Context context)
    {

        ImageView img=view.findViewById(R.id.profile);
        img.setVisibility(View.VISIBLE);
        Picasso.with(context).load(url).into(img);//.placeholder(R.drawable.ic_person_black_24dp)
        ProgressBar progressBar=view.findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);

    }
}
