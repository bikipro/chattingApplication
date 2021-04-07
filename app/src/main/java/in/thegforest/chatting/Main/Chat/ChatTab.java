package in.thegforest.chatting.Main.Chat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;

import in.thegforest.chatting.Main.Adapter.ChatListAdapter;
import in.thegforest.chatting.Main.Adapter.ChatModel;
import in.thegforest.chatting.Main.Adapter.ChatedUserModel;
import in.thegforest.chatting.Main.Adapter.SearchUserModel;
import in.thegforest.chatting.Main.Notification.Token;
import in.thegforest.chatting.R;


public class ChatTab extends Fragment {
    RecyclerView recyclerView;
    ChatListAdapter chatListAdapter;
    List<SearchUserModel> list;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference, databaseReference1;
    List<String> userList;
    ImageView chatIcon;


    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_tab, container, false);
        recyclerView = view.findViewById(R.id.listOfChats);
        chatIcon = view.findViewById(R.id.chat_icon);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("chatedUser").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    userList.add(snapshot.getKey());
                    Log.e("log1", "second");
                }
                if (!userList.isEmpty()) {
                    recyclerView.setVisibility(View.VISIBLE);
                    chatIcon.setVisibility(View.GONE);
                }
                readData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //chatListAdapter = new ChatListAdapter(getContext(),userList);
        //recyclerView.setAdapter(chatListAdapter);
        updateToken(FirebaseInstanceId.getInstance().getToken());

        return view;
    }

    public void readData() {
        list = new ArrayList<>();
        list.clear();
        databaseReference1 = FirebaseDatabase.getInstance().getReference().child("allUsers");
        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                try {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        SearchUserModel searchUserModel = dataSnapshot1.getValue(SearchUserModel.class);
                        searchUserModel.setUid(dataSnapshot1.getKey());
                        for (String id : userList) {
                            if (searchUserModel.getUid().equals(id)) {
                                list.add(searchUserModel);
                            }
                        }
                    }
                } catch (ConcurrentModificationException c) {
                }


                chatListAdapter = new ChatListAdapter(getContext(), list);

                recyclerView.setAdapter(chatListAdapter);
                userList.clear();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateToken(String token) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Token");
        Token token1 = new Token(token);
        databaseReference.child(firebaseUser.getUid()).setValue(token1);
    }


}
