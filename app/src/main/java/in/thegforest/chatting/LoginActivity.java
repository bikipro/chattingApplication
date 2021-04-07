package in.thegforest.chatting;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import in.thegforest.chatting.Authentication.SessionManager;
import in.thegforest.chatting.Authentication.UserLogin;
import in.thegforest.chatting.Main.MainHome;
import in.thegforest.chatting.Pprogress.InternetConnection;
import in.thegforest.chatting.Pprogress.Progress;


public class LoginActivity extends AppCompatActivity {
TextView jump;
View login;
EditText email,password;
View loginBtn;
Progress progress;
SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        jump=findViewById(R.id.jumpToSighUp);
        login=findViewById(R.id.LoginButton);
        email=findViewById(R.id.editTextEmail);
        password=findViewById(R.id.editTextPassword);
        loginBtn=login;
        progress= new Progress(LoginActivity.this,loginBtn);

        sessionManager=new SessionManager(this);
        if (sessionManager.checkForLogin()){
            //intent.putExtra("uid",uid);
            startActivity(new Intent(LoginActivity.this, MainHome.class));
            finish();
        }
        jump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(LoginActivity.this,RegistationUser.class);
                startActivity(intent);
                finish();
            }
        });
        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                    if (new InternetConnection(getApplicationContext()).checkInternet()) {

                        if (email.getText().toString().equals("") || password.getText().toString().equals("")) {
                            Toast.makeText(LoginActivity.this, "Please Fill Correctly", Toast.LENGTH_SHORT).show();
                        } else {
                            progress.ProgressStart();
                            Users users = new Users(email.getText().toString(), password.getText().toString());

                            LogingIn(users);
                        }

                } else {
                    Dialog dialog = new Dialog(getApplicationContext());
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
    }

    private void LogingIn(Users users) {
        new UserLogin(this,users).logingInUser(progress);
    }
}
