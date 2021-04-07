package in.thegforest.chatting.Main.AcceptRequest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Map;

import in.thegforest.chatting.R;

public class AcceptFriendRequestTab extends AppCompatActivity {
    TextView name;
    Button accept, cancel;
    ImageView profile;
    FirebaseDatabase myDatabase;
    DatabaseReference myDbRef, requestRef;
    FirebaseAuth mAuth;
    String uid;
    String current_uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_friend_request_tab);
        name = findViewById(R.id.name);
        accept = findViewById(R.id.accept);
        cancel = findViewById(R.id.reject);
        profile = findViewById(R.id.profile);
        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        current_uid = mAuth.getInstance().getCurrentUser().getUid();
        myDatabase = FirebaseDatabase.getInstance();
        myDbRef = myDatabase.getReference().child("allUsers");

        cancel.setVisibility(View.GONE);

        requestRef = FirebaseDatabase.getInstance().getReference().child("friends");//.child("friend_req");

        myDbRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                name.setText(data.get("name").toString());
                Picasso.with(getApplicationContext()).load(data.get("profile").toString()).into(profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        accept.setOnClickListener(new View.OnClickListener() {
         @Override
          public void onClick(View view) {

                 requestRef.child(current_uid).child(uid).child("type").setValue("friend").addOnCompleteListener(new OnCompleteListener<Void>() {
                     @Override
                     public void onComplete(@NonNull Task<Void> task) {
                         if (task.isSuccessful()){
                             requestRef.child(uid).child(current_uid).child("type").setValue("friend").addOnCompleteListener(new OnCompleteListener<Void>() {
                                 @Override
                                 public void onComplete(@NonNull Task<Void> task) {
                                     if (task.isSuccessful()){

                                         accept.setVisibility(View.GONE);
                                         cancel.setVisibility(View.VISIBLE);

                                         removeFriendRequrst();
                                     }
                                 }
                             });
                         }
                     }
                 });

                }
             }
        );
    }

    public void removeFriendRequrst(){
        myDatabase.getReference().child("friend_req").child(current_uid).child(uid).child("type").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    myDatabase.getReference().child("friend_req").child(uid).child(current_uid).child("type").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                //current_state="not_friend";
                                //send.setVisibility(View.VISIBLE);
                                //cancel.setVisibility(View.GONE);

                            }
                        }
                    });
                }
            }
        });
    }


}
