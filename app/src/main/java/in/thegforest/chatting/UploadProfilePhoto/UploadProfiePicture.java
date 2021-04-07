package in.thegforest.chatting.UploadProfilePhoto;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import in.thegforest.chatting.Authentication.SessionManager;
import in.thegforest.chatting.LoginActivity;
import in.thegforest.chatting.Main.Chat.MassageActivity;
import in.thegforest.chatting.Main.MainHome;
import in.thegforest.chatting.Pprogress.InternetConnection;
import in.thegforest.chatting.R;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class UploadProfiePicture extends AppCompatActivity {
Toolbar toolbar;
String uid,password;
Button upload,save;
Uri imageUri;
ImageView imageView;
TextView cancle;
ProgressBar progressBar;
    private static final int img_req=1;
    StorageReference mStorageRef;
    FirebaseStorage firebaseStorage;
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_profie_picture);
        upload=findViewById(R.id.upload);
        save=findViewById(R.id.save);
        cancle=findViewById(R.id.cancel);
        toolbar=findViewById(R.id.Toolbar);
        imageView=findViewById(R.id.imageView);
        progressBar=findViewById(R.id.progress);
        mStorageRef=firebaseStorage.getInstance().getReference();
        Intent intent=getIntent();
       uid=intent.getStringExtra("uid");
        password=intent.getStringExtra("password");
       upload.setOnClickListener(new View.OnClickListener(){

           @Override
           public void onClick(View view) {
               Intent intent=new Intent();
                       intent.setAction(Intent.ACTION_GET_CONTENT);
                       intent.setType("image/*");
                       startActivityForResult(intent,img_req);
           }
       });
        save.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if (new InternetConnection(UploadProfiePicture.this).checkInternet()) {
                    imageView.setVisibility(GONE);
                    progressBar.setVisibility(VISIBLE);
                    uploadProfileImage(imageUri);
                } else {
                    Dialog dialog = new Dialog(UploadProfiePicture.this);
                    dialog.setContentView(R.layout.alert_box);
                    dialog.setCancelable(false);
                    dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
                    Button button = dialog.findViewById(R.id.btn);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            recreate();
                        }
                    });
                    dialog.show();
                }

            }
        });
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder myDialog= new AlertDialog.Builder(UploadProfiePicture.this);
                myDialog.setTitle("Do you want to skip");
                myDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent=new Intent(UploadProfiePicture.this, MainHome.class);
                        intent.putExtra("uid",uid);
                        startActivities(new Intent[]{intent});
                        finish();
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
        });

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
                imageView.setImageURI(imageUri);
                upload.setText("Change Photo");
                save.setVisibility(VISIBLE);
                //imageUri=result.getUri();
               /* if (resultCode==RESULT_OK)
                {
                    imageUri=result.getUri();
                    //uploadProfileImage(imageUri);
                    //Toast.makeText(MainActivity.this,"Uri has set",Toast.LENGTH_SHORT).show();
                   // Log.e("error",u.getPath());
                    imageView.setImageURI(imageUri);
                    upload.setVisibility(View.GONE);
                    save.setVisibility(VISIBLE);
                }
                else{if (resultCode==CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                    Exception error=result.getError();
                }}*/
            }
            else{ Toast.makeText(UploadProfiePicture.this,"Result code not ok!",Toast.LENGTH_SHORT).show();
            }

        }
        else{  Toast.makeText(UploadProfiePicture.this,"result code and request code not ok!",Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadProfileImage(Uri image){
        if(image!=null){
            sessionManager= new SessionManager(this);
            final StorageReference storageReference=mStorageRef.child("images/"+uid);
            storageReference.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = firebaseDatabase.getReference().child("allUsers");
                    final DatabaseReference childUser = databaseReference.child(uid).child("profile");
                    //childUser.setValue(storageReference.getDownloadUrl().toString());
                    storageReference.getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    childUser.setValue(uri.toString());
                                }
                            });
                    progressBar.setVisibility(GONE);
                    imageView.setVisibility(VISIBLE);
                    Toast.makeText(UploadProfiePicture.this,"Successfully uploaded...",Toast.LENGTH_SHORT).show();
                    if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()){
                        sessionManager.createSession(FirebaseAuth.getInstance().getCurrentUser().getEmail(),password);
                        Intent intent=new Intent(UploadProfiePicture.this, MainHome.class);
                        intent.putExtra("uid",uid);
                        startActivities(new Intent[]{intent});
                        finish();
                    }
                    else{
                        Intent intent=new Intent(UploadProfiePicture.this, LoginActivity.class);
                        //intent.putExtra("uid",uid);
                        startActivities(new Intent[]{intent});
                        finish();
                    }

                }
            });
        }
    }

    }

