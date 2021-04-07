package in.thegforest.chatting.Main.profiles;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.media.audiofx.AudioEffect;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import in.thegforest.chatting.Main.Chat.MassageActivity;
import in.thegforest.chatting.Main.MainHome;
import in.thegforest.chatting.Pprogress.InternetConnection;
import in.thegforest.chatting.R;
import in.thegforest.chatting.UploadProfilePhoto.UploadProfiePicture;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class OwnProfile extends AppCompatActivity {
CircleImageView profile;
ImageView uploadProfile,editName;
TextView name,phone,email;
String myId;
String changedName;
Uri imageUri;
ProgressBar progressBar;
    private static final int img_req=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_own_profile);
        if (!new InternetConnection(OwnProfile.this).checkInternet()) {
            Dialog dialog = new Dialog(OwnProfile.this);
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
        profile=findViewById(R.id.profile);
        uploadProfile=findViewById(R.id.uploadProfile);
        editName=findViewById(R.id.editName);
        name=findViewById(R.id.name);
        phone=findViewById(R.id.phone);
        email=findViewById(R.id.email);
        progressBar=findViewById(R.id.progress);
        myId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        ShowInformation();
        uploadProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,img_req);
                uploadProfile.setVisibility(GONE);
            }
        });
        editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editNameFunction();
            }
        });
    }



    private void editNameFunction() {

        AlertDialog.Builder myDialog= new AlertDialog.Builder(OwnProfile.this);
        myDialog.setTitle("Update name");
        final EditText input =new EditText(OwnProfile.this);
        input.setText(name.getText());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        myDialog.setView(input);
        myDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                changedName=input.getText().toString();
                HashMap<String,Object> map =new HashMap<>();
                map.put("name",changedName);
                FirebaseDatabase.getInstance().getReference().child("allUsers").child(myId).updateChildren(map);
            }
        });
        myDialog.setNegativeButton("cancle",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        myDialog.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            Uri uri=data.getData();
            CropImage.activity(uri).setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1).start(this);
            if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
                CropImage.ActivityResult result=CropImage.getActivityResult(data);
                imageUri=result.getUri();
                progressBar.setVisibility(VISIBLE);
                if(imageUri!=null){
                    StorageReference mStorageRef= FirebaseStorage.getInstance().getReference();
                    final StorageReference storageReference=mStorageRef.child("images/"+myId);
                    storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                            DatabaseReference databaseReference = firebaseDatabase.getReference().child("allUsers");
                            final DatabaseReference childUser = databaseReference.child(myId).child("profile");
                            //childUser.setValue(storageReference.getDownloadUrl().toString());
                            storageReference.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            childUser.setValue(uri.toString());
                                            profile.setImageURI(imageUri);
                                            progressBar.setVisibility(GONE);
                                            uploadProfile.setVisibility(VISIBLE);
                                        }
                                    });
                        }
                    });
                }

            }
            else{ Toast.makeText(OwnProfile.this,"Result code not ok!",Toast.LENGTH_SHORT).show();
            }

        }
        else{  Toast.makeText(OwnProfile.this,"result code and request code not ok!",Toast.LENGTH_SHORT).show();
        }
    }
    public void ShowInformation(){
        FirebaseDatabase.getInstance().getReference("allUsers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){

                    if (snapshot.getKey().equals(myId)){
                        name.setText(snapshot.child("name").getValue().toString());
                        Picasso.with(OwnProfile.this).load(snapshot.child("profile").getValue().toString()).into(profile);
                        phone.setText(snapshot.child("phone").getValue().toString());
                        email.setText(snapshot.child("email").getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
