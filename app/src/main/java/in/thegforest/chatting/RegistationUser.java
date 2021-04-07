package in.thegforest.chatting;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import in.thegforest.chatting.Authentication.EmailAndPasswordAuth;
import in.thegforest.chatting.Main.Chat.MassageActivity;
import in.thegforest.chatting.Pprogress.InternetConnection;
import in.thegforest.chatting.Pprogress.Progress;


public class RegistationUser extends AppCompatActivity {
    TextView jump;
    EditText name, phone, email, password;
    View signUp;
    //FirebaseDatabase firebaseDatabase;
    //FirebaseAuthRegistrar firebaseAuthRegistrar;
    //DatabaseReference databaseReference;
    Users users;
    View view;
    Progress progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        jump = findViewById(R.id.jumpToLogin);
        name = findViewById(R.id.editTextName);
        phone = findViewById(R.id.editTextMobile);
        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);
        signUp = findViewById(R.id.SignupButton);
        view=findViewById(R.id.SignupButton);
        progress= new Progress(RegistationUser.this,view);
        jump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(RegistationUser.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (new InternetConnection(getApplicationContext()).checkInternet()) {
                    if (!isEmpty(email.getText().toString(), password.getText().toString(), name.getText().toString())) {
                        if (findLength(password.getText().toString(), "password")) {
                            if (findLength(phone.getText().toString(), "phone")) {

                                progress.ProgressStart();
                                users = new Users(name.getText().toString(), email.getText().toString(), password.getText().toString(), phone.getText().toString(),false);
                                Register(users);

                            } else {
                                Toast.makeText(RegistationUser.this, "phone no is incorrect", Toast.LENGTH_SHORT).show();

                            }
                        } else {

                            Toast.makeText(RegistationUser.this, "password should be more than 6", Toast.LENGTH_SHORT).show();
                        }
                    } else {

                        Toast.makeText(RegistationUser.this, "please Check Email And Password Fields", Toast.LENGTH_SHORT).show();
                    }
                }

                else {
                    Dialog dialog = new Dialog(RegistationUser.this);
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

    }

    public void Register(Users users) {
        EmailAndPasswordAuth emailAndPasswordAuth = new EmailAndPasswordAuth(users, this);//.registerUser();
        emailAndPasswordAuth.registerUser(progress);
    }


    public boolean findLength(String data, String type) {
        switch (type) {
            case "password":

                if (data.length() < 6) {
                    return false;
                } else {
                    return true;
                }
            case "phone":

                if (data.length() != 10) {
                    return false;
                } else {
                    return true;
                }
            default:
                return false;
        }
    }

    public boolean isEmpty(String email, String password,String name) {
        if (email.equals("") || password.equals("")||name.equals("")) {
            return true;
        } else {
            return false;
        }

    }

}
