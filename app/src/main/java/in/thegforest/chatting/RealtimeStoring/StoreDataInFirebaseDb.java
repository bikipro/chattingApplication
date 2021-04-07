package in.thegforest.chatting.RealtimeStoring;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import in.thegforest.chatting.Authentication.SessionManager;
import in.thegforest.chatting.Pprogress.Progress;
import in.thegforest.chatting.UploadProfilePhoto.UploadProfiePicture;
import in.thegforest.chatting.Users;


public class StoreDataInFirebaseDb {
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference().child("allUsers");
    Context context;
    Users users;
    String user;
    Progress progress;


    public StoreDataInFirebaseDb(Context context, String user, Users users) {
        this.context = context;
        this.users = users;
        this.user = user;
    }

    public void storeData(Progress p) {
        //sessionManager= new SessionManager(context);
        progress = p;
        String name=users.getName();
        //Log.e("uId2",user);
        DatabaseReference childUser = databaseReference.child(user);
        users.setName(name.substring(0,1).toUpperCase()+name.substring(1));

        childUser.setValue(users).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //sessionManager.createSession(users.getEmail(),users.getPassword());
                        progress.ProgressEnd("Done");
                        AlertDialog.Builder myDialog= new AlertDialog.Builder(context);
                        myDialog.setTitle("Email verification has sent to your email");
                        myDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(context, UploadProfiePicture.class);
                                intent.putExtra("uid", user);
                                intent.putExtra("password",users.getPassword());
                                context.startActivities(new Intent[]{intent});
                            }
                        });
                        myDialog.show();


                    }
                });
                //return store;
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progress.ProgressEnd("wrong..");
                    }
                });


    }
    public void UploadProfile(){
        //https://firebasestorage.googleapis.com/v0/b/fir-app-8334e.appspot.com/o/images%2Fimg_323984.png?alt=media&token=b7289541-3fac-481a-af19-1e3199b1424e

    }

}
