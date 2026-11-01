package orbit.ac.id;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class bookedsuccesfully extends AppCompatActivity {
    Button gohome;
    TextView bookingCodeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookedsuccesfully);

        gohome = findViewById(R.id.gohome_succesbook);
        TextView tvSuccessTitle = findViewById(R.id.tvSuccessTitle);
        TextView sucesstext = findViewById(R.id.seatsucessmsg);
        TextView statusText = findViewById(R.id.statusText);
        TextView operatorNameText = findViewById(R.id.operatorNameText);
        TextView busClassText = findViewById(R.id.busClassText);
        TextView routeTripText = findViewById(R.id.routeTripText);
        TextView tripDateScheduleText = findViewById(R.id.tripDateScheduleText);
        TextView cashNoticeDetailText = findViewById(R.id.cashNoticeDetailText);
        bookingCodeText = findViewById(R.id.bookingCodeText);
        View cardCashNotice = findViewById(R.id.cardCashNotice);

        // Register Context Menu for bookingCodeText (Long click to show Context Menu)
        if (bookingCodeText != null) {
            registerForContextMenu(bookingCodeText);
        }

        String seatId = getIntent().getStringExtra("id");
        String busCode = getIntent().getStringExtra("busnumber");
        String busName = getIntent().getStringExtra("bus_name");
        String busClass = getIntent().getStringExtra("bus_class");
        String schedule = getIntent().getStringExtra("schedule");
        String price = getIntent().getStringExtra("price");
        int paxCount = getIntent().getIntExtra("passenger_count", 1);
        String routeOrigin = getIntent().getStringExtra("route_origin");
        String routeDestination = getIntent().getStringExtra("route_destination");
        String routeDate = getIntent().getStringExtra("route_date");
        boolean isRoundTrip = getIntent().getBooleanExtra("is_round_trip", false);
        String routeReturnDate = getIntent().getStringExtra("route_return_date");
        String paymentMethod = getIntent().getStringExtra("payment_method");
        String bookingCode = getIntent().getStringExtra("booking_code");

        if (paymentMethod == null) paymentMethod = "QRIS";
        if (seatId == null || seatId.isEmpty()) seatId = "01";
        if (busCode == null || busCode.isEmpty()) busCode = "SB1";
        if (bookingCode == null || bookingCode.isEmpty()) {
            bookingCode = "SBT-" + busCode + "-" + seatId + "-" + (System.currentTimeMillis() % 100000);
        }

        if (bookingCodeText != null) {
            bookingCodeText.setText(bookingCode);
        }

        if (routeOrigin == null || routeOrigin.isEmpty()) routeOrigin = "Pulo Gebang, Jakarta";
        if (routeDestination == null || routeDestination.isEmpty()) routeDestination = "Bandung";
        if (routeDate == null || routeDate.isEmpty()) routeDate = "Sab, 25 Okt 2025";
        if (routeReturnDate == null || routeReturnDate.isEmpty()) routeReturnDate = "Sab, 31 Okt 2025";

        // Fallbacks for busName based on busCode
        if (busName == null || busName.isEmpty()) {
            if ("AM1".equalsIgnoreCase(busCode) || busCode.toLowerCase().contains("agra")) {
                busName = "Agra Mas";
                busClass = "Sleeper Luxury 1+1";
                schedule = "10:00 - 16:30 WIB (6j 30m)";
                price = "Rp 210.000";
            } else if ("MJ1".equalsIgnoreCase(busCode) || busCode.toLowerCase().contains("murni")) {
                busName = "Murni Jaya";
                busClass = "Executive Class 2+2";
                schedule = "08:30 - 15:00 WIB (6j 30m)";
                price = "Rp 170.000";
            } else if ("RE1".equalsIgnoreCase(busCode) || busCode.toLowerCase().contains("restu")) {
                busName = "Restu Express";
                busClass = "Economy AC 2+3";
                schedule = "11:30 - 18:00 WIB (6j 30m)";
                price = "Rp 140.000";
            } else {
                busName = "Sinar Jaya Shuttle";
                busClass = "Shuttle Executive 1+1";
                schedule = "07:00 - 13:30 WIB (6j 30m)";
                price = "Rp 180.000";
            }
        }

        if (busClass == null) busClass = "Executive Class";
        if (schedule == null) schedule = "07:00 - 13:30 WIB (6j 30m)";
        if (price == null) price = "Rp 180.000";

        // Dynamically set views in the UI
        if (operatorNameText != null) operatorNameText.setText(busName + (isRoundTrip ? " (Pulang-Pergi)" : ""));
        if (busClassText != null) busClassText.setText(busClass + "  •  " + paxCount + " Penumpang" + (isRoundTrip ? " (PP)" : ""));
        if (routeTripText != null) {
            if (isRoundTrip) {
                routeTripText.setText("1. Pergi: " + routeOrigin + " → " + routeDestination + "\n2. Pulang: " + routeDestination + " → " + routeOrigin);
            } else {
                routeTripText.setText(routeOrigin + " → " + routeDestination);
            }
        }
        if (tripDateScheduleText != null) {
            if (isRoundTrip) {
                tripDateScheduleText.setText("Berangkat: " + routeDate + " (" + schedule + ")\nPulang: " + routeReturnDate + " (" + schedule + ")");
            } else {
                tripDateScheduleText.setText(routeDate + "  •  " + schedule);
            }
        }

        if ("CASH".equalsIgnoreCase(paymentMethod)) {
            if (tvSuccessTitle != null) tvSuccessTitle.setText("Pesanan Berhasil!");
            if (sucesstext != null) {
                sucesstext.setText("Tiket Berhasil Dipesan!\nNomor Kursi: " + seatId + " (" + paxCount + " Kursi) | Armada: " + busName);
            }
            if (statusText != null) {
                statusText.setText("Menunggu Pembayaran");
                statusText.setTextColor(Color.parseColor("#D97706"));
            }
            if (cashNoticeDetailText != null) {
                cashNoticeDetailText.setText("Harap lakukan pembayaran tunai sebesar " + price + " (" + paxCount + " Penumpang) di loket resmi maksimal 30 menit sebelum jam keberangkatan bus.");
            }
            if (cardCashNotice != null) cardCashNotice.setVisibility(View.VISIBLE);
        } else {
            if (tvSuccessTitle != null) tvSuccessTitle.setText("Pembayaran Berhasil!");
            if (sucesstext != null) {
                sucesstext.setText("Tiket Berhasil Dipesan & Terkonfirmasi Lunas!\nNomor Kursi: " + seatId + " (" + paxCount + " Kursi) | Armada: " + busName);
            }
            if (statusText != null) {
                statusText.setText("Terkonfirmasi");
                statusText.setTextColor(Color.parseColor("#059669"));
            }
            if (cardCashNotice != null) cardCashNotice.setVisibility(View.GONE);
        }

        gohome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(bookedsuccesfully.this, welcomePage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.bookingCodeText) {
            getMenuInflater().inflate(R.menu.booking_context_menu, menu);
            menu.setHeaderTitle("Pilihan No. Booking");
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_copy_booking) {
            String bookingCode = bookingCodeText != null ? bookingCodeText.getText().toString() : "BJS782635";
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("No. Booking", bookingCode);
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "No. Booking disalin ke clipboard: " + bookingCode, Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (item.getItemId() == R.id.menu_share_booking) {
            String bookingCode = bookingCodeText != null ? bookingCodeText.getText().toString() : "BJS782635";
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Nomor Booking Tiket Bus");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Nomor Booking Tiket Bus saya: " + bookingCode);
            startActivity(Intent.createChooser(shareIntent, "Bagikan No. Booking"));
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(bookedsuccesfully.this, welcomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
