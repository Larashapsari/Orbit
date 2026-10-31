package orbit.ac.id;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SessionManager — menyimpan state login pengguna.
 * PENTING: Hanya simpan username, JANGAN simpan password di SharedPreferences.
 */
public class sessionmanager {

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    private static final String PREF_NAME = "orbit_session";

    public final String key_username = "username";

    public sessionmanager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /**
     * Simpan sesi login — hanya username, bukan password.
     */
    public void createloginsession(String username, String passwordIgnored) {
        editor.putString(key_username, username);
        editor.apply();
    }

    public void setlastbookeddate(String date) {
        editor.putString("last_booked_date", date);
        editor.apply();
    }

    public void setseenwalkthrough(String isseen) {
        editor.putString("seen_walkthrough", isseen);
        editor.apply();
    }

    public String getseenwalkthrough() {
        return sharedPreferences.getString("seen_walkthrough", null);
    }

    public String getlastbookeddate() {
        return sharedPreferences.getString("last_booked_date", null);
    }

    public String getuserdetail() {
        return sharedPreferences.getString(key_username, null);
    }

    public void logoutuserfromsession() {
        editor.clear();
        editor.apply();
    }
}
