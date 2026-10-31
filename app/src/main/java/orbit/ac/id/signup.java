package orbit.ac.id;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
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

public class signup extends AppCompatActivity {

    Button Alreadyhaveacc, Gobuttononsignup;
    TextInputLayout iduser, idpass;
    ImageView busimage;
    TextView welcome, signuptocontitxt;
    TextInputLayout fullnameintextinput, phonenumber;
    ProgressBar progressBar;
    CheckBox checkboxTerms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        busimage          = findViewById(R.id.bus_image);
        welcome           = findViewById(R.id.welcomeonsignup);
        signuptocontitxt  = findViewById(R.id.signuptocontitxt);
        iduser            = findViewById(R.id.UsernameSignup);
        idpass            = findViewById(R.id.Passwordsignup);
        Gobuttononsignup  = findViewById(R.id.gobuttomonsignup);
        Alreadyhaveacc    = findViewById(R.id.ALreadyacc);
        progressBar       = findViewById(R.id.progressBar);
        if (progressBar != null) progressBar.setVisibility(View.INVISIBLE);
        fullnameintextinput = findViewById(R.id.name);
        phonenumber       = findViewById(R.id.phonenumber);
        checkboxTerms     = findViewById(R.id.checkboxTerms);

        Alreadyhaveacc.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Pair[] pairs = new Pair[]{
                    new Pair<View, String>(busimage, "bus_image"),
                    new Pair<View, String>(welcome, "Text"),
                    new Pair<View, String>(signuptocontitxt, "signin_tran"),
                    new Pair<View, String>(Alreadyhaveacc, "SIgnup_transi"),
                    new Pair<View, String>(iduser, "username_transi"),
                    new Pair<View, String>(idpass, "password_transi"),
                    new Pair<View, String>(Gobuttononsignup, "button_tran")
                };
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(signup.this, pairs);
                startActivity(new Intent(signup.this, login.class), options.toBundle());
            } else {
                startActivity(new Intent(signup.this, login.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        Gobuttononsignup.setOnClickListener(v -> {
            hideKeyboard();
            if (!validateName() | !validateUsername() | !validphonenumber() | !validpassword()) return;
            if (checkboxTerms != null && !checkboxTerms.isChecked()) {
                Toast.makeText(signup.this, "Harap setujui Syarat & Ketentuan terlebih dahulu", Toast.LENGTH_SHORT).show();
                return;
            }
            if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
            registerUserInRoom();
        });
    }

    private void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (getCurrentFocus() != null)
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception ignored) {}
    }

    private void registerUserInRoom() {
        final String new_username = iduser.getEditText().getText().toString().trim();
        final String phonenum     = phonenumber.getEditText().getText().toString().trim();
        final String name         = fullnameintextinput.getEditText().getText().toString().trim();
        final String rawPassword  = idpass.getEditText().getText().toString().trim();
        final String bus_no       = "Umum";

        // Hash password sebelum disimpan
        final String hashedPassword = PasswordUtils.hash(rawPassword);

        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(signup.this);

            if (db.userDao().getUserByUsername(new_username) != null) {
                runOnUiThread(() -> {
                    if (progressBar != null) progressBar.setVisibility(View.INVISIBLE);
                    iduser.setError("Username sudah terdaftar");
                    iduser.requestFocus();
                });
                return;
            }

            if (db.userDao().getUserByPhone(phonenum) != null) {
                runOnUiThread(() -> {
                    if (progressBar != null) progressBar.setVisibility(View.INVISIBLE);
                    phonenumber.setError("Nomor HP sudah terdaftar");
                    phonenumber.requestFocus();
                });
                return;
            }

            UserEntity newUser = new UserEntity(
                    new_username, name, hashedPassword,
                    phonenum, bus_no, "Umum", "2026",
                    "Terminal Utama", "0");
            db.userDao().insertUser(newUser);

            runOnUiThread(() -> {
                if (progressBar != null) progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(signup.this, "Pendaftaran berhasil! Silakan masuk", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(signup.this, login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            });
        }).start();
    }

    private Boolean validateName() {
        String val = fullnameintextinput.getEditText().getText().toString().trim();
        if (val.isEmpty()) { fullnameintextinput.setError("Nama tidak boleh kosong"); return false; }
        fullnameintextinput.setError(null); fullnameintextinput.setErrorEnabled(false); return true;
    }

    private Boolean validateUsername() {
        String val = iduser.getEditText().getText().toString().trim();
        if (val.isEmpty()) { iduser.setError("Username tidak boleh kosong"); return false; }
        if (val.contains(" ")) { iduser.setError("Spasi tidak diperbolehkan"); return false; }
        iduser.setError(null); iduser.setErrorEnabled(false); return true;
    }

    private Boolean validphonenumber() {
        String val = phonenumber.getEditText().getText().toString().trim();
        if (val.isEmpty() || val.length() < 8) {
            phonenumber.setError("Masukkan nomor HP yang valid (min. 8 angka)"); return false;
        }
        phonenumber.setError(null); phonenumber.setErrorEnabled(false); return true;
    }

    private Boolean validpassword() {
        String val = idpass.getEditText().getText().toString().trim();
        if (val.isEmpty()) { idpass.setError("Password tidak boleh kosong"); return false; }
        if (val.length() < 4) { idpass.setError("Password minimal 4 karakter"); return false; }
        idpass.setError(null); idpass.setErrorEnabled(false); return true;
    }
}
