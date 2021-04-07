package in.thegforest.chatting.Main.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import in.thegforest.chatting.Main.Chat.ChatedUsers;
import in.thegforest.chatting.Main.Chat.MassageActivity;
import in.thegforest.chatting.Main.Chat.MyCallback;
import in.thegforest.chatting.R;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ProgrammingViewHolder> {
    ChatModel chatModel;
    private Context mContetxt;
    private List<SearchUserModel> muser;
    String lastMsg = "TapToChat",lastTime;
    String chatId;
    String MyUid ;
    int count=0;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance()
            .getReference()
            .child("allUsers");

    public ChatListAdapter(Context context, List<SearchUserModel> muser) {
        MyUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mContetxt = context;
        this.muser = muser;
    }

    @NonNull
    @Override
    public ProgrammingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.friend_record, parent, false);
        return new ProgrammingViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ProgrammingViewHolder holder, int position) {
        final SearchUserModel searchUserModel = muser.get(position);
        holder.name.setText(searchUserModel.getName());
        Picasso.with(mContetxt).load(searchUserModel.getProfile()).into(holder.profile);
        lastMsg(searchUserModel.getUid(), holder.lastMasg,holder.chatCount,holder.countChatLayout,holder.time);
        holder.progressBar.setVisibility(View.GONE);
        databaseReference.child(searchUserModel.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("error", searchUserModel.getUid());
                //holder.status.setText(dataSnapshot.child("status").getValue().toString());
                if (dataSnapshot.child("status").getValue().toString().equals("offline"))
                    holder.profile.setBorderColor(mContetxt.getResources().getColor(R.color.gray));
                else {
                    holder.profile.setBorderColor(mContetxt.getResources().getColor(R.color.green));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(mContetxt, searchUserModel.getUid(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContetxt, MassageActivity.class);
                intent.putExtra("uid", searchUserModel.getUid());
                mContetxt.startActivities(new Intent[]{intent});
            }
        });
    }

    @Override
    public int getItemCount() {
        return muser.size();

    }

    public class ProgrammingViewHolder extends RecyclerView.ViewHolder {
        //ImageView imgIcon;
        TextView name;
        CircleImageView profile;
        TextView lastMasg,chatCount,time;
        ProgressBar progressBar;
        LinearLayout countChatLayout;

        public ProgrammingViewHolder(View itemView) {

            super(itemView);
            //imgIcon=itemView.findViewById(R.id.img);
            name = itemView.findViewById(R.id.name);
            profile = itemView.findViewById(R.id.profile);
            lastMasg = itemView.findViewById(R.id.lastMsg);
            progressBar = itemView.findViewById(R.id.progress);
            chatCount=itemView.findViewById(R.id.chat_count);
            countChatLayout=itemView.findViewById(R.id.countChatLayout);
            time=itemView.findViewById(R.id.time);
            //progressBar.setVisibility(View.VISIBLE);

        }
    }

    public void lastMsg(String uid, final TextView lastMasg,final TextView chatCount,final LinearLayout countChatLayout,final TextView time) {
        String MyUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference().child("chatedUser").child(MyUid).child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    chatId = dataSnapshot.child("chatId").getValue().toString();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("chats").child(chatId);
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                chatModel = snapshot.getValue(ChatModel.class);
                                if (!chatModel.getDeleteId().equals(MyUid)) {
                                    lastMsg = chatModel.getMsg();
                                    lastTime=chatModel.getTime();
                                    if (chatModel.getReceiver().equals(MyUid)&&!chatModel.getIsIsseen()){
                                        count++;
                                    }
                                }
                            }
                            if(count==0){
                                countChatLayout.setVisibility(View.GONE);
                            }
                            else{
                                countChatLayout.setVisibility(View.VISIBLE);
                                chatCount.setText(Integer.toString(count));
                                count=0;
                            }
                            if (lastMsg.equals("TapToChat")) {
                                lastMasg.setText("Tap to Chat");
                            } else {
                                if (chatModel.getType().equals("image"))
                                {time.setText(lastTime);
                                    lastMasg.setText("Image");
                                    lastTime="";}
                                else{
                                    time.setText(lastTime);
                                    lastMasg.setText(lastMsg);
                                    lastTime="";
                                }

                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
                catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }








}
