package in.thegforest.chatting.Pprogress;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class InternetConnection {
    Context context;

    public InternetConnection(Context context){
        this.context=context;
    }
    public boolean checkInternet(){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo!=null&&networkInfo.isConnected()){
            return true;
        }
        else{
            return false;
        }
    }
}
