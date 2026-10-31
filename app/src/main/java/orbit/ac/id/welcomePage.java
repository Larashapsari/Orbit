package orbit.ac.id;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.View;

import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class welcomePage extends AppCompatActivity {

    public ChipNavigationBar chipNavigationBar;
    public Bundle pendingBookingBundle = null;
    protected Boolean tohome = false;
    String usernameonprofile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);

        chipNavigationBar = findViewById(R.id.bottom_navigation_menu);
        chipNavigationBar.setItemSelected(R.id.bottom_navigation_home, true);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmant_container, new Dashboard_home())
                .commit();

        bottommenu();

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragmant_container);
                if (current instanceof dashboard_booking) {
                    setBottomNavVisibility(false);
                } else {
                    setBottomNavVisibility(true);
                    if (current instanceof Dashboard_home && chipNavigationBar != null) {
                        chipNavigationBar.setItemSelected(R.id.bottom_navigation_home, true);
                    }
                }
            }
        });
    }

    public void setBottomNavVisibility(boolean visible) {
        if (chipNavigationBar != null) {
            chipNavigationBar.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    public void openBookingSubScreen(Bundle bundle) {
        setBottomNavVisibility(false);
        dashboard_booking bookingFragment = new dashboard_booking();
        if (bundle != null) {
            bookingFragment.setArguments(bundle);
        }

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit)
                .replace(R.id.fragmant_container, bookingFragment)
                .addToBackStack("booking_sub")
                .commit();
    }

    private void bottommenu() {
        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                Fragment fragment = null;

                if (i == R.id.bottom_navigation_home) {
                    setBottomNavVisibility(true);
                    fragment = new Dashboard_home();
                } else if (i == R.id.profiletab) {
                    setBottomNavVisibility(true);
                    fragment = new dashboard_profile();
                } else if (i == R.id.rideshare) {
                    setBottomNavVisibility(true);
                    fragment = new rideshare_homepage();
                }

                if (fragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit)
                            .replace(R.id.fragmant_container, fragment)
                            .commit();
                }
            }
        });
    }
}
