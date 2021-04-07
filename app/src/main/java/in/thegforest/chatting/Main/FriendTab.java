package in.thegforest.chatting.Main;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import java.util.Map;

import in.thegforest.chatting.Main.AcceptRequest.AcceptFriendRequestTab;
import in.thegforest.chatting.Main.Adapter.SearchUserModel;
import in.thegforest.chatting.Main.Adapter.Search_holder;
import in.thegforest.chatting.Main.Adapter.friendRequestModel;
import in.thegforest.chatting.Main.Chat.MassageActivity;
import in.thegforest.chatting.Main.Search.UserProfile;
import in.thegforest.chatting.Pprogress.InternetConnection;
import in.thegforest.chatting.R;


public class FriendTab extends Fragment {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference, friendDatabase;
    DatabaseReference db;
    String current_uid;
    FirebaseAuth mAuth;
    int count = 0;
    RecyclerView recyclerView, friendRecyclerView;
    ImageView icon;
    String name = "wait", profile;
    TextView countRequest;
    LinearLayout countRequestLayout;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        current_uid = mAuth.getInstance().getCurrentUser().getUid();
        View view = inflater.inflate(R.layout.friend_tab, container, false);
        View bootmShit = view.findViewById(R.id.design_bottom_sheet);
        recyclerView = view.findViewById(R.id.listOfRequest);
        icon = view.findViewById(R.id.friend_icon);
        friendRecyclerView = view.findViewById(R.id.listOfFriends);
        countRequestLayout = view.findViewById(R.id.countRequestLayout);
        countRequest = view.findViewById(R.id.request_count);
        friendRecyclerView.setHasFixedSize(true);
        friendRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        friendRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("friend_req").child(current_uid);
        friendDatabase = firebaseDatabase.getReference().child("friends").child(current_uid);
        db = firebaseDatabase.getReference().child("allUsers");
        showFriends();
        searchFriend();
        final BottomSheetBehavior behavior = BottomSheetBehavior.from(bootmShit);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {


            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        return view;
    }


    private void searchFriend() {
        count=0;
        FirebaseRecyclerAdapter<friendRequestModel, Search_holder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<friendRequestModel, Search_holder>(
                friendRequestModel.class, R.layout.searched_friend_record, Search_holder.class, databaseReference
        ) {
            @Override
            protected void populateViewHolder(final Search_holder search_holder, friendRequestModel friendRequestModel, int i) {
                final String uid = getRef(i).getKey();
                if (friendRequestModel.getType().equals("received")) {
                    count++;
                    db.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                            name = data.get("name").toString();
                            profile = data.get("profile").toString();
                            search_holder.setName(name);
                            search_holder.setImg(profile, getContext());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    //search_holder.setName(name);
                    //search_holder.setImg(profile, getContext());
                    search_holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Intent intent= new Intent(FriendTab.this,AcceptFriendRequestTab.class);
                            Intent intent = new Intent(getActivity(), AcceptFriendRequestTab.class);
                            intent.putExtra("uid", uid);
                            startActivity(intent);
                            Toast.makeText(getContext(), uid, Toast.LENGTH_SHORT).show();

                        }
                    });
                }
                if (count!=0){
                    countRequestLayout.setVisibility(View.VISIBLE);
                    countRequest.setText(Integer.toString(count));
                }
                else{countRequestLayout.setVisibility(View.GONE);}

            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public void showFriends() {
        friendRecyclerView.setVisibility(View.VISIBLE);
        icon.setVisibility(View.GONE);
        FirebaseRecyclerAdapter<friendRequestModel, Search_holder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<friendRequestModel, Search_holder>(
                friendRequestModel.class, R.layout.friend_record, Search_holder.class, friendDatabase
        ) {
            @Override
            protected void populateViewHolder(final Search_holder search_holder, friendRequestModel friendRequestModel, int i) {
                final String uid = getRef(i).getKey();
                // if(friendRequestModel.getType().equals("received")) {

                db.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                        name = data.get("name").toString();
                        profile = data.get("profile").toString();
                        search_holder.setName(name);
                        search_holder.setImg(profile, getContext());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                search_holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Intent intent= new Intent(FriendTab.this,AcceptFriendRequestTab.class);
                        Intent intent = new Intent(getActivity(), MassageActivity.class);
                        intent.putExtra("uid", uid);
                        startActivity(intent);
                        //Toast.makeText(getContext(), uid, Toast.LENGTH_SHORT).show();

                    }
                });
                //}

            }
        };
        friendRecyclerView.setAdapter(firebaseRecyclerAdapter);

    }

}
