package in.thegforest.chatting.Authentication;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import in.thegforest.chatting.Main.MainHome;
import in.thegforest.chatting.Pprogress.Progress;
import in.thegforest.chatting.Users;


public class UserLogin {
    FirebaseAuth  firebaseAuth=FirebaseAuth.getInstance();
    Context context;
    Users users;
    Progress progres;
    SessionManager sessionManager;

    public UserLogin(Context context, Users users){
        this.context=context;
        this.users=users;
    }
    public void logingInUser(Progress progress){
        sessionManager =new SessionManager(context);
        progres=progress;
        firebaseAuth.signInWithEmailAndPassword(users.getEmail(), users.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    if (firebaseAuth.getCurrentUser().isEmailVerified()){
                        HashMap<String,Object> map =new HashMap<>();
                        map.put("verified",true);
                        FirebaseDatabase.getInstance().getReference().child("allUsers")
                                .child(firebaseAuth.getCurrentUser().getUid()).updateChildren(map);
                        sessionManager.createSession(users.getEmail(),users.getPassword());
                        progres.ProgressEnd("LogedIn");
                        Intent intent=new Intent(context, MainHome.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //intent.putExtra("uid",uid);
                        context.startActivities(new Intent[]{intent});
                    }
                    else{
                        progress.ProgressEnd("Login");
                        AlertDialog.Builder myDialog= new AlertDialog.Builder(context);
                        myDialog.setTitle("you are not a verified user, please verify your email");
                        myDialog.setNegativeButton("OK",new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                        myDialog.show();
                    }


                }
                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                    progres.ProgressEnd("InvalidPassword");
                }
                else if (task.getException() instanceof FirebaseAuthInvalidUserException){
                    progres.ProgressEnd("UnregisterdUser");
                }
            }
        });
       //
    }
}
