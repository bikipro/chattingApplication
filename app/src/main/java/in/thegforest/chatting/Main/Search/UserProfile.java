package in.thegforest.chatting.Main.Search;

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
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.Map;

import in.thegforest.chatting.R;

public class UserProfile extends AppCompatActivity {
    TextView name,isFriend;
    Button send, cancel;
    ImageView profile;
    String uid, current_uid,current_state="not_friends";
    FirebaseDatabase myDatabase;
    DatabaseReference myDbRef,requestRef;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        name = findViewById(R.id.name);
        send = findViewById(R.id.send);
        cancel = findViewById(R.id.cancel);
        profile = findViewById(R.id.profile);
        isFriend=findViewById(R.id.status);
        myDatabase = FirebaseDatabase.getInstance();
        myDbRef = myDatabase.getReference().child("allUsers");
        requestRef=FirebaseDatabase.getInstance().getReference().child("friend_req");
        current_uid = mAuth.getInstance().getCurrentUser().getUid();
        cancel.setVisibility(View.GONE);
        checkForOwnAccount();
        checkForAlreadyFriend();

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

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(current_state.equals("not_friends")){}
                sendFriendRequestToPerson();

            }
        });

        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                cancelFriendRequest();
            }
        });


    }

    private void checkForAlreadyFriend() {
        FirebaseDatabase.getInstance().getReference().child("friends").child(current_uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if( dataSnapshot.hasChild(uid)){
                   send.setVisibility(View.GONE);
                   cancel.setVisibility(View.GONE);
                   isFriend.setVisibility(View.VISIBLE);
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void cancelFriendRequest() {
        requestRef.child(current_uid).child(uid).child("type").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    requestRef.child(uid).child(current_uid).child("type").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                current_state="not_friend";
                                send.setVisibility(View.VISIBLE);
                                cancel.setVisibility(View.GONE);

                            }
                        }
                    });
                }
            }
        });
    }

    private void sendFriendRequestToPerson() {
        requestRef.child(current_uid).child(uid).child("type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    requestRef.child(uid).child(current_uid).child("type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                current_state="request_sent";
                                send.setVisibility(View.GONE);
                                cancel.setVisibility(View.VISIBLE);

                            }
                        }
                    });
                }
            }
        });


    }

    private void checkForOwnAccount() {

        if (uid.equals(current_uid)) {
            send.setVisibility(View.GONE);
            cancel.setVisibility(View.GONE);
        } else {
            requestRef.child(current_uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(uid)){
                        String status= dataSnapshot.child(uid).child("type").getValue().toString();
                        if(status.equals("sent")){
                            send.setVisibility(View.GONE);
                            cancel.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
           //....
        }
    }
}
