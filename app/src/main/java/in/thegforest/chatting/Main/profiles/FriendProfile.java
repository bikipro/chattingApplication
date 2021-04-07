package in.thegforest.chatting.Main.profiles;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import in.thegforest.chatting.Main.Chat.DeleteChats.ClearAllChats;
import in.thegforest.chatting.Main.Chat.MyCallback;
import in.thegforest.chatting.Pprogress.LoadingAlertDialog;
import in.thegforest.chatting.R;

public class FriendProfile extends AppCompatActivity {
    ImageView profile;
    String uid,chatId,current_uid;
    Button blockFriend,deleteAllChats;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        profile=findViewById(R.id.profile);
        blockFriend=findViewById(R.id.blockFriend);
        deleteAllChats=findViewById(R.id.deleteAllChat);
        uid=getIntent().getStringExtra("uid");
        current_uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        getChatId();
        FirebaseDatabase.getInstance().getReference("allUsers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    if (snapshot.getKey().equals(uid)){
                        toolbar.setTitle(snapshot.child("name").getValue().toString());
                        Picasso.with(FriendProfile.this).load(snapshot.child("profile").getValue().toString()).into(profile);
                    }
                }
                setSupportActionBar(toolbar);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        deleteAllChats.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                LoadingAlertDialog loadingAlertDialog=new LoadingAlertDialog(FriendProfile.this);
                loadingAlertDialog.startLoading();
                ClearAllChats clearAllChats=new ClearAllChats(chatId,uid,current_uid);
                clearAllChats.clearAllChat(loadingAlertDialog);

            }
        });
        blockFriend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //FirebaseDatabase.getInstance().getReference("allUsers")
                Toast.makeText(FriendProfile.this,"blocked",Toast.LENGTH_SHORT).show();
            }
        });

    }
    public void getChatId(){
        FirebaseDatabase.getInstance().getReference().child("chatedUser").child(current_uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.getKey().equals(uid)) {
                            chatId=snapshot.child("chatId").getValue().toString();
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
