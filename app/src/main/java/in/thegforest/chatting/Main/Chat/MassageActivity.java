package in.thegforest.chatting.Main.Chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import in.thegforest.chatting.Main.Adapter.ChatModel;
import in.thegforest.chatting.Main.Adapter.SearchUserModel;
import in.thegforest.chatting.Main.profiles.FriendProfile;
import in.thegforest.chatting.Main.Notification.Client;
import in.thegforest.chatting.Main.Notification.Data;
import in.thegforest.chatting.Main.Notification.MyResponse;
import in.thegforest.chatting.Main.Notification.Sender;
import in.thegforest.chatting.Main.Notification.Token;
import in.thegforest.chatting.Pprogress.InternetConnection;
import in.thegforest.chatting.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MassageActivity extends AppCompatActivity {
    ImageView profile;
    LinearLayout profileS;
    ProgressBar progressBar;
    TextView name, msg;
    ImageButton send, back, imageSend;
    String uid,userName;
    String current_uid;
    FirebaseAuth mAuth;
    RecyclerChatList recyclerChatList;
    List<ChatModel> list;
    RecyclerView recyclerView;
    boolean IsFirstTime = true,notify;
    ChildEventListener seenListener;
    //ValueEventListener seenListener;
    DatabaseReference databaseReference;
    APIService apiService;
    String chatId,massagegId="";
    String time, date;
    private static final int img_req = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_massage);
        current_uid = mAuth.getInstance().getCurrentUser().getUid();
        profile = findViewById(R.id.profile);
        profileS = findViewById(R.id.profileS);
        progressBar=findViewById(R.id.progress);
        name = findViewById(R.id.name);
        msg = findViewById(R.id.msg);
        send = findViewById(R.id.send);// Send Button
        back = findViewById(R.id.back);
        imageSend = findViewById(R.id.imageSend);
        recyclerView = findViewById(R.id.listOfFriends); // For sowing massages
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        checkChatId();
        showTitle();
        imageSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, img_req);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (new InternetConnection(MassageActivity.this).checkInternet()) {
                    if (!msg.getText().toString().equals("")) {
                        sendMassage(current_uid, uid, msg.getText().toString(),"text");
                        msg.setText("");
                    } else {
                        Toast.makeText(getApplicationContext(), "Write in the massage box", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Dialog dialog = new Dialog(MassageActivity.this);
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
            }
        });
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        profileS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MassageActivity.this, FriendProfile.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });

        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("allUsers").child(uid);
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                SearchUserModel user = dataSnapshot.getValue(SearchUserModel.class);
                if (user.getStatus().equals("active-"+current_uid)) {
                    notify=false;
                    Log.e("error","false");
                }
                else{notify=true;Log.e("error","true");}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            progressBar.setVisibility(View.VISIBLE);
            String imageId = UUID.randomUUID().toString();
            Uri uriU = data.getData();
            if (chatId=="abc"||chatId==null){
                sendMassageForImage(current_uid, uid, uriU.toString(), "image", new MyCallback() {
                    @Override
                    public void onCallback(String value) {
                        progressBar.setVisibility(View.GONE);
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("chat:/" + value + "/" + imageId);
                        storageReference.putFile(uriU).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        FirebaseDatabase.getInstance().getReference().child("chats").child(chatId).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                    if (snapshot.child("msgId").getValue().equals(massagegId)) {
                                                        snapshot.child("msg").getRef().setValue(uri.toString());
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });

            }
            else{
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("chat:/" + chatId + "/" + imageId);
                storageReference.putFile(uriU).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                sendMassage(current_uid, uid,uri.toString(),"image");
                                progressBar.setVisibility(View.GONE);
                            }
                        });

                    }
                });
            }

        }
    }

    public void showTitle() {
        FirebaseDatabase.getInstance().getReference().child("allUsers").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                name.setText(data.get("name").toString());
                Picasso.with(getApplicationContext()).load(data.get("profile").toString()).into(profile);
                userName=data.get("name").toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    public void sendMassageForImage(String sender, String receiver, String msg,String type,MyCallback callback){//show messages
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd mm yyyy");
        Date date1 = new Date();
        date = simpleDateFormat.format(date1);
        //date= simpleDateFormat.format(calendar.getTime());
        Calendar calendar1 = Calendar.getInstance();
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm a");
        time = simpleTimeFormat.format(calendar1.getTime());
        String msgId=UUID.randomUUID().toString();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("msg", msg);
        hashMap.put("isseen", false);
        hashMap.put("time", time);
        hashMap.put("date", date);
        hashMap.put("deleteId", "nothing");
        hashMap.put("msgId",msgId);
        hashMap.put("type",type);
        massagegId=msgId;
        Log.e("error","msgidSet");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        if (IsFirstTime) { // if click send msg for first after opening message activity then it will run
            isNotAdded(new MyCallback() {
                @Override
                public void onCallback(String value) {
                    chatId = value;
                    databaseReference.child("chats").child(chatId).push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            showMsgForFirstTime(current_uid, uid);
                            callback.onCallback(value);
                            if(notify){
                                sendNotification(receiver, userName, msg);
                            }
                        }
                    });
                }
            }); // it will add user in chat list
            IsFirstTime = false;

        } else {
            databaseReference.child("chats").child(chatId).push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(notify){
                        sendNotification(receiver, userName, "image");
                    }
                }
            });
        }

            }
    public void sendMassage(String sender, String receiver, String msg,String type) {//show messages
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd mm yyyy");
        Date date1 = new Date();
        date = simpleDateFormat.format(date1);
        //date= simpleDateFormat.format(calendar.getTime());
        Calendar calendar1 = Calendar.getInstance();
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm a");
        time = simpleTimeFormat.format(calendar1.getTime());
        String msgId=UUID.randomUUID().toString();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("msg", msg);
        hashMap.put("isseen", false);
        hashMap.put("time", time);
        hashMap.put("date", date);
        hashMap.put("deleteId", "nothing");
        hashMap.put("msgId",msgId);
        hashMap.put("type",type);
        massagegId=msgId;
        Log.e("error","msgidSet");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        if (IsFirstTime) { // if click send msg for first after opening message activity then it will run
            isNotAdded(new MyCallback() {
                @Override
                public void onCallback(String value) {
                    chatId = value;
                    databaseReference.child("chats").child(chatId).push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(notify){
                                sendNotification(receiver, userName, msg);
                            }
                            showMsgForFirstTime(current_uid, uid);
                        }
                    });
                }
            }); // it will add user in chat list
            IsFirstTime = false;
        } else {
            databaseReference.child("chats").child(chatId).push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(notify){
                        sendNotification(receiver, userName, msg);
                    }
                }
            });
        }


    }

    private void sendNotification(String receiver, String name, String msg) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Token");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Token token = dataSnapshot1.getValue(Token.class);
                    Data data = new Data(current_uid, name + ": " + msg, R.mipmap.ic_launcher, "New Message", uid);
                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            /*if (response.code()==200){
                                if (response.body().success!=1){

                                }
                            }*/
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getMsg(final String myId, final String userId) {
        list = new ArrayList<>();
        list.clear();
        seenListener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot1, @Nullable String s) {
                //list.clear();
                //for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    ChatModel chatModel = dataSnapshot1.getValue(ChatModel.class);
                    if (chatModel.getReceiver().equals(myId) && chatModel.getSender().equals(userId)
                            || chatModel.getReceiver().equals(userId) && chatModel.getSender().equals(myId)) {
                        if (!chatModel.getDeleteId().equals(myId)) {
                            list.add(chatModel);
                            Log.e("errorList", chatModel.getMsg());
                        }
                        if (chatModel.getReceiver().equals(myId) && chatModel.getSender().equals(userId)) {
                            if (!chatModel.getIsIsseen()) {
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("isseen", true);
                                dataSnapshot1.getRef().updateChildren(hashMap);
                            }
                        }
                    }
                    recyclerChatList = new RecyclerChatList(MassageActivity.this, list, chatId,getSupportFragmentManager());
                    recyclerView.setAdapter(recyclerChatList);
                //}
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot1, @Nullable String s) {
                list = new ArrayList<>();
                databaseReference = FirebaseDatabase.getInstance().getReference().child("chats");
                databaseReference.child(chatId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            ChatModel chatModel = dataSnapshot1.getValue(ChatModel.class);
                            if (chatModel.getReceiver().equals(myId) && chatModel.getSender().equals(userId)
                                    || chatModel.getReceiver().equals(userId) && chatModel.getSender().equals(myId)) {
                                if (!chatModel.getDeleteId().equals(myId)) {
                                    list.add(chatModel);
                                    Log.e("errorList", chatModel.getMsg());
                                }
                            }
                            recyclerChatList = new RecyclerChatList(MassageActivity.this, list, chatId,getSupportFragmentManager());
                            recyclerView.setAdapter(recyclerChatList);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        databaseReference = FirebaseDatabase.getInstance().getReference().child("chats").child(chatId);
        databaseReference.addChildEventListener(seenListener);
       /* seenListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    ChatModel chatModel = dataSnapshot1.getValue(ChatModel.class);
                    if (chatModel.getReceiver().equals(myId) && chatModel.getSender().equals(userId)
                            || chatModel.getReceiver().equals(userId) && chatModel.getSender().equals(myId)) {
                        if (!chatModel.getDeleteId().equals(myId)) {
                            list.add(chatModel);
                            Log.e("errorList", chatModel.getMsg());
                        }
                        if (chatModel.getReceiver().equals(myId) && chatModel.getSender().equals(userId)) {
                            if (!chatModel.getIsIsseen()) {
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("isseen", true);
                                dataSnapshot1.getRef().updateChildren(hashMap);
                            }
                        }
                    }
                    recyclerChatList = new RecyclerChatList(MassageActivity.this, list, chatId);
                    recyclerView.setAdapter(recyclerChatList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };*/


    }

    public void showMsgForFirstTime(final String myId, final String userId) {
        list = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("chats");
        databaseReference.child(chatId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    ChatModel chatModel = dataSnapshot1.getValue(ChatModel.class);
                    if (chatModel.getReceiver().equals(myId) && chatModel.getSender().equals(userId)
                            || chatModel.getReceiver().equals(userId) && chatModel.getSender().equals(myId)) {
                        if (!chatModel.getDeleteId().equals(myId)) {
                            list.add(chatModel);
                            Log.e("errorList", chatModel.getMsg());
                        }
                    }
                    recyclerChatList = new RecyclerChatList(MassageActivity.this, list, chatId,getSupportFragmentManager());
                    recyclerView.setAdapter(recyclerChatList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

   /* public void seenMassages(final String userId) {
        databaseReference = FirebaseDatabase.getInstance().getReference("chats").child(chatId);
        seenListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatModel chatModel = snapshot.getValue(ChatModel.class);
                    if (chatModel.getReceiver().equals(current_uid) && chatModel.getSender().equals(userId)) {
                        if (!chatModel.getIsIsseen()) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("isseen", true);
                            snapshot.getRef().updateChildren(hashMap);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }*/

    private void currentUser(String u_id) {
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentuser", u_id);
        editor.apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //status("offline");
        HashMap<String, Object> map = new HashMap<>();
        map.put("status", "offline");
        FirebaseDatabase.getInstance().getReference().child("allUsers").child(current_uid).updateChildren(map);
        currentUser(uid);
        FirebaseDatabase.getInstance().getReference().child("chats").child(chatId).removeEventListener(seenListener);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //status("online");
        HashMap<String, Object> map = new HashMap<>();
        map.put("status", "active-"+uid);
        FirebaseDatabase.getInstance().getReference().child("allUsers").child(current_uid).updateChildren(map);
        currentUser("none");
    }

    private void checkChatId() {
        getChatId(new MyCallback() {
            @Override
            public void onCallback(String value) {
                chatId = value;

                //showTitle();
                getMsg(current_uid, uid);
            }
        });
    }

    public void isNotAdded(MyCallback myCallback) {

        DatabaseReference databaseReferences;
        databaseReferences = FirebaseDatabase.getInstance().getReference().child("chatedUser").child(current_uid);
        databaseReferences.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Toast.makeText(context, "returnde", Toast.LENGTH_SHORT).show();
                if (dataSnapshot.exists()) {
                    boolean ValueGot = true;
                    //OUTER_LOOP:
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        //Log.e("error1",snapshot.getKey().toString());
                        if (snapshot.getKey().equals(uid)) {
                            ValueGot = false;
                            String value = snapshot.child("chatId").getValue().toString();
                            myCallback.onCallback(value);
                        }
                    }
                    if (ValueGot) {
                        String chatID = UUID.randomUUID().toString();
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("uid", uid);
                        hashMap.put("chatId", chatID);
                        HashMap<String, Object> hashMap1 = new HashMap<>();
                        hashMap1.put("uid", current_uid);
                        hashMap1.put("chatId", chatID);
                        databaseReference = FirebaseDatabase.getInstance().getReference().child("chatedUser").child(current_uid).child(uid);
                        databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                databaseReference = FirebaseDatabase.getInstance().getReference().child("chatedUser").child(uid).child(current_uid);
                                databaseReference.setValue(hashMap1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        myCallback.onCallback(chatID);
                                    }
                                });
                            }
                        });
                    }

                } else {
                    String chatID = UUID.randomUUID().toString();
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("uid", uid);
                    hashMap.put("chatId", chatID);
                    HashMap<String, Object> hashMap1 = new HashMap<>();
                    hashMap1.put("uid", current_uid);
                    hashMap1.put("chatId", chatID);
                    databaseReference = FirebaseDatabase.getInstance().getReference().child("chatedUser").child(current_uid).child(uid);
                    databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            databaseReference = FirebaseDatabase.getInstance().getReference().child("chatedUser").child(uid).child(current_uid);
                            databaseReference.setValue(hashMap1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    myCallback.onCallback(chatID);
                                }
                            });
                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void getChatId(MyCallback callback) {
        FirebaseDatabase.getInstance().getReference().child("chatedUser").child(current_uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.getKey().equals(uid)) {
                            callback.onCallback(snapshot.child("chatId").getValue().toString());
                        } else {

                        }

                    }
                } else {
                    callback.onCallback("abc");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}