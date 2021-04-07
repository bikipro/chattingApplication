package in.thegforest.chatting.Pprogress;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;

import in.thegforest.chatting.R;

public class LoadingAlertDialog {
    private AlertDialog alertDialog;
    private Activity context;
    public LoadingAlertDialog(Activity context){
        this.context=context;
    }
    public void startLoading(){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        LayoutInflater inflater=context.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.loding_alert_dialoag,null));
        builder.setCancelable(false);

        alertDialog=builder.create();
        alertDialog.show();
    }
    public void stopLoading(){
        alertDialog.dismiss();
    }
}
