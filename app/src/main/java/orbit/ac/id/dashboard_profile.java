package orbit.ac.id;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import orbit.ac.id.data.AppDatabase;
import orbit.ac.id.data.UserEntity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class dashboard_profile extends Fragment {

    // UI refs
    private TextView usernameonprofiletxt, userEmailSubtitle, usernameedit;
    private TextView nametxt, contacttxt;
    private View btnEditProfile, btnEditInfoText, btnProfileMoreOptions, avatarCard;
    private ImageView profileImageView;
    private Button logoutbtn;

    // Data
    private String currentUsername;
    private UserEntity currentUserEntity;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    public dashboard_profile() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    saveImageToInternalStorage(selectedImageUri);
                }
            }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        sessionmanager sm = new sessionmanager(requireContext());
        currentUsername = sm.getuserdetail();

        // Jika guest → tampilkan layar invite login
        if (currentUsername == null || currentUsername.equals("Guest user")) {
            View v = inflater.inflate(R.layout.getlogin, container, false);
            Button btnSignup = v.findViewById(R.id.signupbuttoninvite);
            Button btnLogin  = v.findViewById(R.id.loginbtninvite);

            if (btnSignup != null) btnSignup.setOnClickListener(view -> {
                sm.logoutuserfromsession();
                Intent i = new Intent(getActivity(), signup.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            });

            if (btnLogin != null) btnLogin.setOnClickListener(view -> {
                sm.logoutuserfromsession();
                Intent i = new Intent(getActivity(), login.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            });
            return v;
        }

        View v = inflater.inflate(R.layout.fragment_dashboard_profile, container, false);

        // Bind views
        usernameonprofiletxt   = v.findViewById(R.id.usernameonprofiletab);
        userEmailSubtitle      = v.findViewById(R.id.userEmailSubtitle);
        nametxt                = v.findViewById(R.id.editablename);
        contacttxt             = v.findViewById(R.id.contactedit);
        usernameedit           = v.findViewById(R.id.usernameedit);

        avatarCard             = v.findViewById(R.id.avatarCard);
        profileImageView       = v.findViewById(R.id.profileImageView);

        btnEditProfile         = v.findViewById(R.id.btnEditProfile);
        btnEditInfoText        = v.findViewById(R.id.btnEditInfoText);
        btnProfileMoreOptions  = v.findViewById(R.id.btnProfileMoreOptions);
        logoutbtn              = v.findViewById(R.id.logoutonprofile);

        // 0. Klik Foto Profil / Avatar Card -> Pilihan Upload Foto
        if (avatarCard != null) {
            avatarCard.setOnClickListener(view -> showPhotoOptionsDialog());
        }

        // 1. Icon Edit Pensil -> Mengedit informasi pengguna (nama, no hp)
        if (btnEditProfile != null) {
            btnEditProfile.setOnClickListener(view -> showEditProfileDialog());
        }

        // 2. Menu Ganti Password -> Membuka form ganti password
        if (btnEditInfoText != null) {
            btnEditInfoText.setOnClickListener(view ->
                    startActivity(new Intent(getContext(), updatepassword.class)));
        }

        // 3. Tombol Titik Tiga Kanan Atas -> Pop Up Menu (Bantuan & Keluar)
        if (btnProfileMoreOptions != null) {
            btnProfileMoreOptions.setOnClickListener(view -> showProfileMoreOptionsMenu(btnProfileMoreOptions));
        }

        if (logoutbtn != null) {
            logoutbtn.setOnClickListener(view -> showLogoutDialog());
        }

        // Load data nyata dari Room DB & Foto Profil
        loadUserFromRoom();

        // Back handler
        requireActivity().getOnBackPressedDispatcher().addCallback(
            getViewLifecycleOwner(),
            new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    new AlertDialog.Builder(getContext())
                        .setMessage("Yakin ingin keluar dari aplikasi?")
                        .setPositiveButton("Keluar", (d, i) -> requireActivity().finish())
                        .setNegativeButton("Batal", (d, i) -> d.cancel())
                        .show();
                }
            }
        );

        return v;
    }

    private void showPhotoOptionsDialog() {
        Context ctx = getContext();
        if (ctx == null) return;

        final String[] options = {"Pilih Foto dari Galeri", "Hapus Foto Profil"};
        new AlertDialog.Builder(ctx)
                .setTitle("Foto Profil")
                .setItems(options, (dialogInterface, i) -> {
                    if (i == 0) {
                        openGallery();
                    } else if (i == 1) {
                        removeProfilePhoto();
                    }
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void removeProfilePhoto() {
        if (getContext() == null || currentUsername == null) return;
        File file = new File(requireContext().getFilesDir(), "profile_" + currentUsername + ".jpg");
        if (file.exists()) {
            file.delete();
        }
        loadProfileImage();
        Toast.makeText(getContext(), "Foto profil dihapus", Toast.LENGTH_SHORT).show();
    }

    private void saveImageToInternalStorage(Uri uri) {
        if (getContext() == null || currentUsername == null) return;
        try {
            InputStream is = requireContext().getContentResolver().openInputStream(uri);
            if (is == null) return;
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            is.close();

            File file = new File(requireContext().getFilesDir(), "profile_" + currentUsername + ".jpg");
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.flush();
            fos.close();

            loadProfileImage();
            Toast.makeText(getContext(), "Foto profil berhasil diperbarui", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Gagal menyimpan foto profil", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadProfileImage() {
        if (profileImageView == null || currentUsername == null || getContext() == null) return;
        File file = new File(requireContext().getFilesDir(), "profile_" + currentUsername + ".jpg");
        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            if (bitmap != null) {
                profileImageView.setImageBitmap(bitmap);
                profileImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                return;
            }
        }
        profileImageView.setImageResource(R.drawable.ic_default_whatsapp_profile);
        profileImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
    }

    private void showProfileMoreOptionsMenu(View anchor) {
        if (getContext() == null) return;
        PopupMenu popupMenu = new PopupMenu(getContext(), anchor);
        popupMenu.getMenuInflater().inflate(R.menu.profile_options_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_help) {
                startActivity(new Intent(getContext(), HelpActivity.class));
                return true;
            } else if (itemId == R.id.menu_logout) {
                showLogoutDialog();
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    /** Load data user dari Room DB dan tampilkan ke UI */
    private void loadUserFromRoom() {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(requireContext());
            UserEntity user = db.userDao().getUserByUsername(currentUsername);
            currentUserEntity = user;

            if (getActivity() == null) return;
            getActivity().runOnUiThread(() -> {
                loadProfileImage();
                if (user != null) {
                    // Header
                    String displayName = (user.getName() != null && !user.getName().isEmpty())
                        ? user.getName() : currentUsername;
                    if (usernameonprofiletxt != null) usernameonprofiletxt.setText("Akun Saya");
                    if (userEmailSubtitle   != null) userEmailSubtitle.setText("Kelola informasi data dan profil Anda");

                    // Info rows
                    if (nametxt       != null) nametxt.setText(displayName);
                    if (contacttxt    != null) contacttxt.setText(user.getPhoneNo() != null ? user.getPhoneNo()    : "—");
                    if (usernameedit  != null) usernameedit.setText(currentUsername);
                } else {
                    // Tidak ada data
                    if (usernameonprofiletxt != null) usernameonprofiletxt.setText("Akun Saya");
                    if (userEmailSubtitle   != null) userEmailSubtitle.setText("Kelola informasi data dan profil Anda");
                    Toast.makeText(getContext(), "Data profil tidak ditemukan", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    /** Dialog untuk mengedit informasi pengguna */
    private void showEditProfileDialog() {
        if (getContext() == null || currentUserEntity == null) {
            Toast.makeText(getContext(), "Memuat data pengguna...", Toast.LENGTH_SHORT).show();
            loadUserFromRoom();
            return;
        }

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_profile, null);

        final EditText etName = dialogView.findViewById(R.id.etEditProfileName);
        final EditText etPhone = dialogView.findViewById(R.id.etEditProfilePhone);

        Button btnCancel = dialogView.findViewById(R.id.btnCancelEditProfile);
        Button btnSave = dialogView.findViewById(R.id.btnSaveEditProfile);

        // Pre-fill fields dengan data saat ini
        if (etName != null && currentUserEntity.getName() != null) {
            etName.setText(currentUserEntity.getName());
        }
        if (etPhone != null && currentUserEntity.getPhoneNo() != null) {
            etPhone.setText(currentUserEntity.getPhoneNo());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        if (btnCancel != null) {
            btnCancel.setOnClickListener(v -> dialog.dismiss());
        }

        if (btnSave != null) {
            btnSave.setOnClickListener(v -> {
                final String newName = etName != null ? etName.getText().toString().trim() : "";
                final String newPhone = etPhone != null ? etPhone.getText().toString().trim() : "";

                if (newName.isEmpty()) {
                    if (etName != null) etName.setError("Nama tidak boleh kosong");
                    return;
                }
                if (newPhone.isEmpty()) {
                    if (etPhone != null) etPhone.setError("Nomor telepon tidak boleh kosong");
                    return;
                }

                // Update objek currentUserEntity
                currentUserEntity.setName(newName);
                currentUserEntity.setPhoneNo(newPhone);

                // Simpan ke Room DB
                new Thread(() -> {
                    AppDatabase db = AppDatabase.getInstance(requireContext());
                    db.userDao().updateUser(currentUserEntity);

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            dialog.dismiss();
                            Toast.makeText(getContext(), "Informasi profil berhasil diperbarui", Toast.LENGTH_SHORT).show();
                            loadUserFromRoom();
                        });
                    }
                }).start();
            });
        }

        dialog.show();
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(getContext())
            .setTitle("Keluar dari Akun")
            .setMessage("Yakin ingin keluar dari akun " + currentUsername + "?")
            .setCancelable(false)
            .setPositiveButton("Keluar", (d, i) -> {
                new sessionmanager(requireContext()).logoutuserfromsession();
                Intent intent = new Intent(getContext(), login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            })
            .setNegativeButton("Batal", (d, i) -> d.cancel())
            .show();
    }
}
