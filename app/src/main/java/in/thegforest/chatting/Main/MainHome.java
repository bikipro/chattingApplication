package in.thegforest.chatting.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import in.thegforest.chatting.Authentication.SessionManager;
import in.thegforest.chatting.LoginActivity;
import in.thegforest.chatting.Main.Adapter.AdapterClass;
import in.thegforest.chatting.Main.Chat.ChatTab;
import in.thegforest.chatting.Main.Search.SearchNewFiends;
import in.thegforest.chatting.Main.profiles.OwnProfile;
import in.thegforest.chatting.Pprogress.InternetConnection;
import in.thegforest.chatting.R;


public class MainHome extends AppCompatActivity {
    AdapterClass adapterClass;
    Toolbar toolbar;
    //ImageView imageView;
    FirebaseAuth mAuth;
    String currentUid="";
    SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);
        toolbar = findViewById(R.id.tolbar1);
        setSupportActionBar(toolbar);
        final ViewPager viewPager = findViewById(R.id.viewPager1);
        final TabLayout tabLayout = findViewById(R.id.tab1);

        if (!new InternetConnection(getApplicationContext()).checkInternet()){
            Dialog dialog = new Dialog(MainHome.this);
            dialog.setContentView(R.layout.alert_box);
            dialog.setCancelable(false);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
            Button button = dialog.findViewById(R.id.btn);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (new InternetConnection(getApplicationContext()).checkInternet()) {
                        recreate();
                    }
                }
            });
            dialog.show();
        }
        currentUid= mAuth.getInstance().getCurrentUser().getUid();
        status("online");
        sessionManager= new SessionManager(this);
        adapterClass = new AdapterClass(getSupportFragmentManager());
        adapterClass.addFragment(new ChatTab(),"chats");
        adapterClass.addFragment(new FriendTab(),"Friends");
        viewPager.setAdapter(adapterClass);
        tabLayout.setupWithViewPager(viewPager);

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count=0;
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    for (DataSnapshot snapshot1:snapshot.getChildren()) {
                        if (snapshot1.child("receiver").getValue().toString().equals(currentUid) && snapshot1.child("isseen").getValue().equals(false)) {
                            count++;
                        }
                    }
                }
                if (count==0){
                    toolbar.setTitle("Chat");
                }
                else{
                    toolbar.setTitle("Chat("+count+")");
                }
                setSupportActionBar(toolbar);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.search:
                startActivity(new Intent(MainHome.this, SearchNewFiends.class));
                return true;
            case R.id.logout:
                sessionManager.logOut();
                mAuth.getInstance().signOut();
                startActivity(new Intent(MainHome.this, LoginActivity.class));
                finish();
                return true;
            case R.id.setting:
                startActivity(new Intent(MainHome.this, OwnProfile.class));
                return true;
            default:

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    public void status(String status){
        HashMap<String,Object> map =new HashMap<>();
        map.put("status",status);
        FirebaseDatabase.getInstance().getReference().child("allUsers").child(currentUid).updateChildren(map);
    }
}
