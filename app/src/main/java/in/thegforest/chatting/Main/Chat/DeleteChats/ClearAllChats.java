package in.thegforest.chatting.Main.Chat.DeleteChats;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import in.thegforest.chatting.Main.Adapter.ChatModel;
import in.thegforest.chatting.Pprogress.LoadingAlertDialog;

public class ClearAllChats {
    String chatId,receiverId,myId;

    public ClearAllChats(String chatId,String receiverId,String myId){
        this.receiverId=receiverId;
        this.chatId=chatId;
        this.myId=myId;
    }

    public void clearAllChat(LoadingAlertDialog loadingAlertDialog) {
        FirebaseStorage fStorage=FirebaseStorage.getInstance();
        FirebaseDatabase.getInstance().getReference().child("chats").child(chatId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()) {
                        //snapshot.getRef().child("deleteId").setValue(myId);
                        //Log.e("error",snapshot.child("deleteId").toString());
                        if (snapshot.child("deleteId").getValue().toString().equals(receiverId)){
                            if (snapshot.child("type").getValue().toString().equals("image")) {
                                fStorage.getReferenceFromUrl(snapshot.child("msg").getValue().toString()).delete();
                                snapshot.getRef().removeValue();
                            }
                            else{ snapshot.getRef().removeValue();}
                        }
                        else{
                            Log.e("error",snapshot.child("deleteId").toString());
                            snapshot.getRef().child("deleteId").setValue(myId);
                        }
                    }
                    loadingAlertDialog.stopLoading();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
