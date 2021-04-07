package in.thegforest.chatting.Authentication;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String EMAIL = "Email";
    private static final String PASSWORD = "Password";
    public SessionManager(Context _context){
        context=_context;
        sharedPreferences = context.getSharedPreferences("userSession",Context.MODE_PRIVATE);
        editor= sharedPreferences.edit();
    }
    public void createSession(String email, String password){
        editor.putBoolean(IS_LOGIN,true);
        editor.putString(EMAIL,email);
        editor.putString(PASSWORD,password);
        editor.commit();
    }
    public HashMap<String,String> getData(){
        HashMap<String,String> data =new HashMap<>();
        data.put(EMAIL,sharedPreferences.getString(EMAIL,null));
        data.put(PASSWORD,sharedPreferences.getString(PASSWORD,null));
        return  data;
    }
    public boolean checkForLogin(){
        if (sharedPreferences.getBoolean(IS_LOGIN,false)){
            return true;
        }
        else{
            return false;
        }
    }
    public void logOut(){
        editor.clear();
        editor.commit();
    }
}
