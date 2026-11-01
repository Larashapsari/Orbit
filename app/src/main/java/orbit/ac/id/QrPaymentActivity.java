package orbit.ac.id;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import orbit.ac.id.data.AppDatabase;
import orbit.ac.id.data.BookingEntity;

public class QrPaymentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_payment);

        ImageView btnBackQrPage = findViewById(R.id.btnBackQrPage);
        Button btnDownloadQr = findViewById(R.id.btnDownloadQr);
        TextView qrPriceText = findViewById(R.id.qrPriceText);
        TextView qrTagText = findViewById(R.id.qrTagText);

        final String seatId = getIntent().getStringExtra("id");
        final String busCode = getIntent().getStringExtra("busnumber");
        final String busName = getIntent().getStringExtra("bus_name");
        final String busClass = getIntent().getStringExtra("bus_class");
        final String schedule = getIntent().getStringExtra("schedule");
        final String price = getIntent().getStringExtra("price");
        final int paxCount = getIntent().getIntExtra("passenger_count", 1);
        final String routeOrigin = getIntent().getStringExtra("route_origin");
        final String routeDestination = getIntent().getStringExtra("route_destination");
        final String routeDate = getIntent().getStringExtra("route_date");
        final boolean isRoundTrip = getIntent().getBooleanExtra("is_round_trip", false);
        final String routeReturnDate = getIntent().getStringExtra("route_return_date");

        if (qrPriceText != null && price != null) qrPriceText.setText(price);
        if (qrTagText != null && busName != null) qrTagText.setText("QRIS " + busName.toUpperCase());

        if (btnBackQrPage != null) {
            btnBackQrPage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }

        if (btnDownloadQr != null) {
            btnDownloadQr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    confirmBookingInRoom(seatId, busCode, busName, busClass, schedule, price, paxCount, routeOrigin, routeDestination, routeDate, isRoundTrip, routeReturnDate);
                }
            });
        }
    }

    private void confirmBookingInRoom(final String seatId, final String busCode, final String busName, final String busClass, final String schedule, final String price, final int paxCount, final String routeOrigin, final String routeDestination, final String routeDate, final boolean isRoundTrip, final String routeReturnDate) {
        final String username = new sessionmanager(QrPaymentActivity.this).getuserdetail();
        final String effectiveUser = (username != null && !username.isEmpty()) ? username : "Guest user";

        new Thread(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = AppDatabase.getInstance(QrPaymentActivity.this);

                String targetSeat = seatId != null ? seatId : "01";
                String displayOrigin = routeOrigin != null ? routeOrigin : "Pulo Gebang, Jakarta";
                String displayDest = routeDestination != null ? routeDestination : "Bandung";
                String displayDate = routeDate != null ? routeDate : "Kam, 29 Okt";
                String displayPrice = price != null ? price : "Rp 180.000";
                String displaySchedule = schedule != null ? schedule : "07:00 - 13:30 WIB";
                String displayBusName = busName != null ? busName : "Sinar Jaya Shuttle";

                String fullBookingInfo = displayBusName + "|" + displayOrigin + " → " + displayDest + "|" + displayPrice + "|" + displaySchedule + "|" + (isRoundTrip ? "PP" : "OneWay") + "|" + (routeReturnDate != null ? routeReturnDate : "") + "|QRIS";

                BookingEntity newBooking = new BookingEntity(effectiveUser, displayDate, fullBookingInfo, targetSeat, true);
                db.bookingDao().insertBooking(newBooking);

                if (!effectiveUser.equals("Guest user")) {
                    db.userDao().updateLastBookedDate(effectiveUser, displayDate);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(QrPaymentActivity.this, "Barcode QR berhasil diunduh!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(QrPaymentActivity.this, bookedsuccesfully.class);
                        intent.putExtra("id", targetSeat);
                        intent.putExtra("busnumber", busCode);
                        intent.putExtra("bus_name", displayBusName);
                        intent.putExtra("bus_class", busClass);
                        intent.putExtra("schedule", displaySchedule);
                        intent.putExtra("price", displayPrice);
                        intent.putExtra("passenger_count", paxCount);
                        intent.putExtra("route_origin", displayOrigin);
                        intent.putExtra("route_destination", displayDest);
                        intent.putExtra("route_date", displayDate);
                        intent.putExtra("is_round_trip", isRoundTrip);
                        intent.putExtra("route_return_date", routeReturnDate);
                        intent.putExtra("payment_method", "QRIS");
                        startActivity(intent);
                        finish();
                    }
                });
            }
        }).start();
    }
}
