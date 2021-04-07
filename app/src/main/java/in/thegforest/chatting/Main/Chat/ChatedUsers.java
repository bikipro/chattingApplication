package in.thegforest.chatting.Main.Chat;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.UUID;


public class ChatedUsers {
    //FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String sender, receiver;
    Context context;
    // Map<String, Object> data;
    String returnData ;

    public ChatedUsers(Context context, String senderMyId, String receiver) {
        this.sender = senderMyId;
        this.receiver = receiver;
        this.context = context;

    }


    public void isNotAdded(MyCallback myCallback) {

        FirebaseDatabase.getInstance().getReference().child("chatedUser").child(sender).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Toast.makeText(context, "returnde", Toast.LENGTH_SHORT).show();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //Log.e("error1",snapshot.getKey().toString());
                    if (snapshot.getKey().equals(receiver)) {

                        returnData =snapshot.child("chatId").toString() ;
                        myCallback.onCallback(returnData);
                        Log.e("CU.error",returnData);
                        //Log.e("error2",returnData);
                        //Log.e("error3","returnde");
                       // Toast.makeText(context, "returnde", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("else.error","else run");
                        String chatId= UUID.randomUUID().toString();
                        returnData=chatId;
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("uid", receiver);
                        hashMap.put("chatId",chatId);
                        HashMap<String, Object> hashMap1 = new HashMap<>();
                        hashMap1.put("uid", sender);
                        hashMap1.put("chatId",chatId);
                        databaseReference = FirebaseDatabase.getInstance().getReference().child("chatedUser").child(sender).child(receiver);
                        databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.e("sender.error","sender creted");
                                databaseReference = FirebaseDatabase.getInstance().getReference().child("chatedUser").child(receiver).child(sender);
                                databaseReference.setValue(hashMap1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        myCallback.onCallback(returnData);
                                        Log.e("receiver.error","receiver creted");

                                    }
                                });
                            }
                        });
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void checkChatId(MyCallback callback){
        FirebaseDatabase.getInstance().getReference().child("chatedUser").child(sender).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Toast.makeText(context, "returnde", Toast.LENGTH_SHORT).show();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //Log.e("error1",snapshot.getKey().toString());
                    if (snapshot.getKey().equals(receiver)) {

                        returnData =snapshot.child("chatId").toString() ;
                        callback.onCallback(returnData);
                        Log.e("CU.error",returnData);
                        //Log.e("error2",returnData);
                        //Log.e("error3","returnde");
                        // Toast.makeText(context, "returnde", Toast.LENGTH_SHORT).show();
                    } else {

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}