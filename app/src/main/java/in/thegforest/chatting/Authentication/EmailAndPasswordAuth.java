package in.thegforest.chatting.Authentication;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


import in.thegforest.chatting.Pprogress.Progress;
import in.thegforest.chatting.RealtimeStoring.StoreDataInFirebaseDb;
import in.thegforest.chatting.Users;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class EmailAndPasswordAuth {
    Users users;
    Context context;
    FirebaseAuth mAuth;
    String userId;
    Progress pr;

    public EmailAndPasswordAuth(Users user, Context context) {
        this.users = user;
        this.context = context;
    }

    public void registerUser(Progress p) {
        pr = p;
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(users.getEmail(), users.getPassword())
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            userId = mAuth.getCurrentUser().getUid();
                            new StoreDataInFirebaseDb(context, userId, users).storeData(pr);
                        } else {
                            pr.ProgressEnd("wrong..");
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(context, "createUserWithEmail:failure", Toast.LENGTH_SHORT).show();
                            userId = "false";

                        }
                        // ...
                    }
                });
    }
}
