package in.thegforest.chatting.Main.Search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import in.thegforest.chatting.Main.Adapter.SearchUserModel;
import in.thegforest.chatting.Main.Adapter.Search_holder;
import in.thegforest.chatting.R;


public class SearchNewFiends extends AppCompatActivity {
RecyclerView recyclerView;
FirebaseDatabase firebaseDatabase;
SearchView searchView;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_new_fiends);
        recyclerView=findViewById(R.id.listUser);
        searchView=findViewById(R.id.searchUser);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();//.child("allUsers");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //recyclerView.addItemDecoration(new DividerItemDecoration(this)),DividerItemDecoration.VERTICAL);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchFriend(s);
                return false;
            }
        });
        //
    }

    private void searchFriend( String data) {
        Query query =databaseReference.child("allUsers").orderByChild("name").startAt( data).endAt(data+"\uf8ff");
        FirebaseRecyclerAdapter<SearchUserModel, Search_holder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<SearchUserModel, Search_holder>(
                SearchUserModel.class,R.layout.searched_friend_record,Search_holder.class,query//databaseReference// query,
        ) {
            @Override
            protected void populateViewHolder(Search_holder search_holder, SearchUserModel searchUserModel, int i) {
                if(searchUserModel.getVerified()) {
                    final String uid = getRef(i).getKey();
                    search_holder.setName(searchUserModel.getName());
                    search_holder.setImg(searchUserModel.getProfile(), getApplicationContext());
                    search_holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(SearchNewFiends.this, UserProfile.class);
                            intent.putExtra("uid", uid);
                            startActivity(intent);

                        }
                    });
                }

            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
}
