package orbit.ac.id;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;


public class splash_screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Cek session langsung di Splash — tidak paksa lewat walkthrough
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            sessionmanager session = new sessionmanager(splash_screen.this);
            String savedUsername = session.getuserdetail();

            Intent intent;
            if (savedUsername != null && !savedUsername.isEmpty()) {
                // Sudah login sebelumnya → langsung ke Dashboard
                intent = new Intent(splash_screen.this, welcomePage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            } else {
                // Belum login → ke Login
                intent = new Intent(splash_screen.this, login.class);
            }

            startActivity(intent);
            finish();
        }, 1800);
    }
}
