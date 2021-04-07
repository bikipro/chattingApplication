package in.thegforest.chatting.Main.Chat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import in.thegforest.chatting.Main.Adapter.ChatModel;
import in.thegforest.chatting.Main.profiles.OwnProfile;
import in.thegforest.chatting.R;

public class RecyclerChatList extends RecyclerView.Adapter<RecyclerChatList.ProgrammingViewHolder> {
    private Context mContetxt;
    private List<ChatModel> mChat;
    private static final int chat_left = 0;
    private static final int chat_right = 1;
    String firstDate;
    FirebaseUser fUser;
    String chatId;
    int size;

    public RecyclerChatList(Context context, List<ChatModel> mChat, String chatId) {

        mContetxt = context;
        this.mChat = mChat;
        size = mChat.size();
        this.chatId = chatId;
    }

    @NonNull
    @Override
    public ProgrammingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == chat_right) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.chat_right, parent, false);
            return new ProgrammingViewHolder(view);
        } else {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.chat_left, parent, false);
            return new ProgrammingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ProgrammingViewHolder holder, int position) {
        ChatModel chatModel = mChat.get(position);
        if (chatModel.getType().equals("image")) {
            holder.showMsg.setVisibility(View.GONE);
            holder.image.setVisibility(View.VISIBLE);
            Picasso.with(mContetxt).load(Uri.parse(chatModel.getMsg())).into(holder.image);
        } else {
            holder.showMsg.setVisibility(View.VISIBLE);
            holder.image.setVisibility(View.GONE);
            holder.showMsg.setText(chatModel.getMsg());
        }

        holder.time.setText(chatModel.getTime());
        ChatModel chatModel1 = null;
        if (position > 0) {
            chatModel1 = mChat.get(position - 1);
            firstDate = chatModel1.getDate();
        } else {
            firstDate = chatModel.getDate();
        }
        if (position == size - 1 && !firstDate.equals(chatModel.getDate())) {
            holder.date.setVisibility(View.VISIBLE);
            firstDate = chatModel.getDate();
            holder.date.setText(chatModel.getDate());
        } else if (position == size - 1) {
            holder.date.setVisibility(View.GONE);
            firstDate = chatModel.getDate();
        } else if (!firstDate.equals(chatModel.getDate())) {
            holder.date.setVisibility(View.VISIBLE);
            firstDate = chatModel.getDate();
        } else if (position == 0) {
            holder.date.setText(chatModel.getDate());
            holder.date.setVisibility(View.VISIBLE);
        } else {
            holder.date.setVisibility(View.GONE);
        }
        if (position == size - 1) {
            if (chatModel.getIsIsseen()) {
                holder.isseenT.setVisibility(View.VISIBLE);
                holder.isseenF.setVisibility(View.GONE);
            } else {
                holder.isseenF.setVisibility(View.VISIBLE);
                holder.isseenT.setVisibility(View.GONE);

            }
        } else {
            holder.isseenT.setVisibility(View.GONE);
            holder.isseenF.setVisibility(View.GONE);
        }
        holder.contenerR.setAnimation(AnimationUtils.loadAnimation(mContetxt, R.anim.faee_animation_for_chat));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chatModel.getType().equals("image")){
                    //mContetxt.startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(chatModel.getMsg())));
                    Bundle bundle=new Bundle();
                    bundle.putString("uri",chatModel.getMsg());
                    ChatImageViewFragment chatImageViewFragment=new ChatImageViewFragment();

                    //chatImageViewFragment.show(getSupportFragmentManager());
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder myDialog = new AlertDialog.Builder(mContetxt);
                myDialog.setTitle("Do you want to delete this Massage!");
                final TextView tView = new TextView(mContetxt);
                if (chatModel.getType().equals("text")){tView.setText(chatModel.getMsg());}
                else{tView.setText("image");}
                tView.setGravity(Gravity.CENTER);
                myDialog.setView(tView);
                myDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase.getInstance().getReference().child("chats").child(chatId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    if (snapshot.child("msgId").getValue().toString().equals(chatModel.getMsgId())) {
                                        if (snapshot.child("deleteId").getValue().toString().equals(chatModel.getReceiver())
                                                || snapshot.child("deleteId").getValue().toString().equals(chatModel.getSender())) {
                                            if (snapshot.child("type").getValue().toString().equals("image")) {
                                                FirebaseStorage.getInstance().getReferenceFromUrl(snapshot.child("msg").getValue().toString()).delete();
                                                snapshot.getRef().removeValue();
                                            }
                                            else{snapshot.getRef().removeValue();}
                                        } else {
                                            Log.e("error", snapshot.child("deleteId").toString());
                                            snapshot.getRef().child("deleteId").setValue(fUser.getUid());
                                        }
                                        break;
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });
                myDialog.setNegativeButton("cancle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                myDialog.show();


                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return mChat.size();

    }

    public class ProgrammingViewHolder extends RecyclerView.ViewHolder {
        //ImageView imgIcon;
        TextView showMsg, date, time;
        ImageView isseenT, isseenF, image;
        LinearLayout contenerR;

        public ProgrammingViewHolder(View itemView) {

            super(itemView);
            //imgIcon=itemView.findViewById(R.id.img);
            showMsg = itemView.findViewById(R.id.msg);
            isseenT = itemView.findViewById(R.id.isseenT);
            isseenF = itemView.findViewById(R.id.isseenF);
            contenerR = itemView.findViewById(R.id.containerR);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            image = itemView.findViewById(R.id.image);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(fUser.getUid())) {
            return chat_right;
        } else {
            return chat_left;
        }
    }
}
