package orbit.ac.id;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import orbit.ac.id.data.AppDatabase;
import orbit.ac.id.data.UserEntity;

public class login extends AppCompatActivity {
    ImageView busimage;
    TextView welcometext, signintocontitxt;
    TextInputLayout username, password;
    Button Go_onLOGIN, new_userbtn;
    ProgressBar progressBar;
    TextView forgetPasswordtxt;
    TextView skipsignup;
    CheckBox checkboxRemember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Seed demo user jika DB kosong
        seedDemoUserIfNeeded();

        busimage   = findViewById(R.id.bus_image);
        welcometext = findViewById(R.id.welcomebacktext);
        signintocontitxt = findViewById(R.id.signintoconttext);
        username   = findViewById(R.id.Username);
        password   = findViewById(R.id.Password);
        Go_onLOGIN = findViewById(R.id.Go_button_on_login);
        new_userbtn = findViewById(R.id.signupbuttononlogin);
        progressBar = findViewById(R.id.loading);
        if (progressBar != null) progressBar.setVisibility(View.INVISIBLE);
        skipsignup = findViewById(R.id.skipsignup);
        checkboxRemember = findViewById(R.id.checkboxRemember);
        forgetPasswordtxt = findViewById(R.id.ForgetText);

        forgetPasswordtxt.setOnClickListener(v -> {
            startActivity(new Intent(login.this, Forgetpassword.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        new_userbtn.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Pair[] pairs = new Pair[]{
                    new Pair<View, String>(busimage, "bus_image"),
                    new Pair<View, String>(welcometext, "Text"),
                    new Pair<View, String>(signintocontitxt, "signin_tran"),
                    new Pair<View, String>(new_userbtn, "SIgnup_transi"),
                    new Pair<View, String>(username, "username_transi"),
                    new Pair<View, String>(password, "password_transi"),
                    new Pair<View, String>(Go_onLOGIN, "button_tran")
                };
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(login.this, pairs);
                startActivity(new Intent(login.this, signup.class), options.toBundle());
            } else {
                startActivity(new Intent(login.this, signup.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        Go_onLOGIN.setOnClickListener(v -> {
            hideKeyboard();
            if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
            loginuser();
        });

        skipsignup.setOnClickListener(v -> {
            sessionmanager sm = new sessionmanager(login.this);
            sm.createloginsession("Guest user", "");
            movetowelcomepage();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        sessionmanager sm = new sessionmanager(login.this);
        String saved = sm.getuserdetail();
        if (saved != null && !saved.isEmpty()) {
            movetowelcomepage();
        }
    }

    private void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (getCurrentFocus() != null)
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception ignored) {}
    }

    private void seedDemoUserIfNeeded() {
        new Thread(() -> {
            try {
                AppDatabase db = AppDatabase.getInstance(login.this);
                if (db.userDao().getUserByUsername("kelompok4") == null) {
                    UserEntity demo = new UserEntity(
                            "kelompok4", "Demo User",
                            PasswordUtils.hash("123456"),
                            "0812345678", "SB1", "IT", "2024",
                            "Terminal Pulo Gebang", "0");
                    db.userDao().insertUser(demo);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void loginuser() {
        if (!validateUsername() | !validpassword()) {
            if (progressBar != null) progressBar.setVisibility(View.INVISIBLE);
            return;
        }
        check_isUser();
    }

    private void check_isUser() {
        final String inputUsername = username.getEditText().getText().toString().trim();
        final String inputPassword  = password.getEditText().getText().toString().trim();

        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(login.this);
            final UserEntity user = db.userDao().getUserByUsername(inputUsername);

            runOnUiThread(() -> {
                if (progressBar != null) progressBar.setVisibility(View.INVISIBLE);

                if (user != null) {
                    username.setError(null);
                    username.setErrorEnabled(false);

                    // Bandingkan hash — support password lama (plaintext) untuk backward compat
                    boolean passwordMatch =
                        PasswordUtils.hash(inputPassword).equals(user.getPassword())
                        || inputPassword.equals(user.getPassword()); // fallback plaintext

                    if (passwordMatch) {
                        password.setError(null);
                        password.setErrorEnabled(false);

                        sessionmanager sm = new sessionmanager(login.this);
                        if (checkboxRemember != null && checkboxRemember.isChecked()) {
                            // Ingat Saya: simpan sesi persisten
                            sm.createloginsession(inputUsername, null);
                        } else {
                            // Tidak dicentang: tetap simpan sesi (diperlukan untuk navigasi),
                            // tapi bisa dikembangkan pakai in-memory session di masa depan
                            sm.createloginsession(inputUsername, null);
                        }

                        Toast.makeText(login.this,
                            "Selamat datang, " + user.getName() + "!",
                            Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(login.this, welcomePage.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        password.setError("Password salah");
                        password.requestFocus();
                    }
                } else {
                    username.setError("Pengguna tidak ditemukan");
                    username.requestFocus();
                }
            });
        }).start();
    }

    private boolean validpassword() {
        String val = password.getEditText().getText().toString();
        if (val.isEmpty()) {
            password.setError("Password tidak boleh kosong");
            return false;
        }
        password.setError(null);
        password.setErrorEnabled(false);
        return true;
    }

    private boolean validateUsername() {
        String val = username.getEditText().getText().toString();
        if (val.isEmpty()) {
            username.setError("Username tidak boleh kosong");
            return false;
        }
        username.setError(null);
        username.setErrorEnabled(false);
        return true;
    }

    private void movetowelcomepage() {
        Intent intent = new Intent(login.this, welcomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
            .setMessage("Yakin ingin keluar dari aplikasi?")
            .setCancelable(false)
            .setPositiveButton("Keluar", (d, i) -> finishAffinity())
            .setNegativeButton("Batal", (d, i) -> d.cancel())
            .show();
    }
}
