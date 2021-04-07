package in.thegforest.chatting.Pprogress;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import in.thegforest.chatting.R;


public class Progress {
    CardView cardView;
    ConstraintLayout constraintLayout;
    ProgressBar progressBar;
    TextView textView;
    Animation fade_in;
    //View view=findViewByLayout(R.layout.progress_dialog);
    public Progress(Context context, View view){
        cardView=view.findViewById(R.id.cardView);
        constraintLayout=view.findViewById(R.id.constraint);
        progressBar=view.findViewById(R.id.progressBar);
        textView=view.findViewById(R.id.textView);
    }
    public void ProgressStart(){
        progressBar.setVisibility(View.VISIBLE);
        textView.setText("Please wait...");
    }
    public void ProgressEnd( String msg){
        //constraintLayout.setBackground(cardView.getResources().getColor(Color.parseColor("00ff00")));
                //setBackground(cardView.getResources().getColor(R.color.green));
        progressBar.setVisibility(View.GONE);
        textView.setText(msg);
    }
}
