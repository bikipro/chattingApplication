package in.thegforest.chatting.Pprogress;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import in.thegforest.chatting.LoginActivity;
import in.thegforest.chatting.R;

import static java.lang.Thread.sleep;


public class FullscreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sleep(3000);
                    startActivity(new Intent(FullscreenActivity.this, LoginActivity.class));
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

    }

}
