package orbit.ac.id;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import orbit.ac.id.data.AppDatabase;
import orbit.ac.id.data.BookingEntity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Confirmticket extends AppCompatActivity {
    Button confirmticket;
    ImageView backbtn;
    ImageView L11, infoicon;
    TextView selectedSeatLabel;

    String busroute;
    String busName;
    String busClass;
    String schedule;
    String price;
    String routeOrigin;
    String routeDestination;
    String routeDate;
    boolean isRoundTrip = false;
    String routeReturnDate;
    int paxCount = 1;

    int recentviewID = 0;
    String selectedseatID = "L11"; // Default automatic seat allocation
    AlertDialog dialog = null;
    String tomorrowDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmticket);

        L11 = findViewById(R.id.L11);
        infoicon = findViewById(R.id.infoicon);
        backbtn = findViewById(R.id.backarrow);
        confirmticket = findViewById(R.id.confirmticket);
        selectedSeatLabel = findViewById(R.id.selectedSeatLabel);

        // Read intent extras
        busroute = getIntent().getStringExtra("busnumber");
        if (busroute == null || busroute.isEmpty()) {
            busroute = "SB1";
        }

        busName = getIntent().getStringExtra("bus_name");
        busClass = getIntent().getStringExtra("bus_class");
        schedule = getIntent().getStringExtra("schedule");
        price = getIntent().getStringExtra("price");
        routeOrigin = getIntent().getStringExtra("route_origin");
        routeDestination = getIntent().getStringExtra("route_destination");
        routeDate = getIntent().getStringExtra("route_date");
        isRoundTrip = getIntent().getBooleanExtra("is_round_trip", false);
        routeReturnDate = getIntent().getStringExtra("route_return_date");
        paxCount = getIntent().getIntExtra("passenger_count", 1);

        // Fallbacks if any extra is null
        if (routeOrigin == null || routeOrigin.isEmpty()) routeOrigin = "Pulo Gebang, Jakarta";
        if (routeDestination == null || routeDestination.isEmpty()) routeDestination = "Bandung";
        if (routeDate == null || routeDate.isEmpty()) routeDate = "Sab, 25 Okt 2025";
        if (routeReturnDate == null || routeReturnDate.isEmpty()) routeReturnDate = "Sab, 31 Okt 2025";

        if (busName == null || busName.isEmpty()) {
            if ("AM1".equalsIgnoreCase(busroute)) {
                busName = "Agra Mas";
                busClass = "Sleeper Luxury 1+1";
                schedule = "10:00 - 16:30 WIB (6j 30m)";
                price = "Rp 210.000";
            } else if ("MJ1".equalsIgnoreCase(busroute)) {
                busName = "Murni Jaya";
                busClass = "Executive Class 2+2";
                schedule = "08:30 - 15:00 WIB (6j 30m)";
                price = "Rp 170.000";
            } else if ("RE1".equalsIgnoreCase(busroute)) {
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

        // Bind dynamic TextViews in UI
        TextView headerSubTitleText = findViewById(R.id.headerSubTitleText);
        TextView tripScheduleText = findViewById(R.id.tripScheduleText);
        TextView tripRouteText = findViewById(R.id.tripRouteText);
        TextView bannerPriceText = findViewById(R.id.bannerPriceText);
        TextView passengerNameText = findViewById(R.id.passengerNameText);
        TextView armadaBusText = findViewById(R.id.armadaBusText);
        TextView seatNumberDetailText = findViewById(R.id.seatNumberDetailText);
        TextView priceLabel = findViewById(R.id.priceLabel);

        if (headerSubTitleText != null) headerSubTitleText.setText(busName + "  •  " + busClass + (isRoundTrip ? " (PP)" : ""));
        if (tripScheduleText != null) {
            if (isRoundTrip) {
                tripScheduleText.setText("1. Pergi: " + routeDate + " (" + schedule + ")\n2. Pulang: " + routeReturnDate + " (" + schedule + ")");
            } else {
                tripScheduleText.setText(routeDate + "  •  " + schedule);
            }
        }
        if (tripRouteText != null) {
            if (isRoundTrip) {
                tripRouteText.setText("Pergi: " + routeOrigin + " → " + routeDestination + "\nPulang: " + routeDestination + " → " + routeOrigin);
            } else {
                tripRouteText.setText(routeOrigin + " → " + routeDestination);
            }
        }
        if (bannerPriceText != null) bannerPriceText.setText(price);
        if (armadaBusText != null) armadaBusText.setText(busName + (isRoundTrip ? " (PP)" : ""));
        if (seatNumberDetailText != null) {
            if (isRoundTrip) {
                seatNumberDetailText.setText(paxCount + " Kursi Pergi + " + paxCount + " Kursi Pulang\n(Otomatis Ditentukan)");
            } else {
                seatNumberDetailText.setText(paxCount + " Kursi (Otomatis Ditentukan)");
            }
        }
        if (selectedSeatLabel != null) {
            selectedSeatLabel.setText(isRoundTrip ? "Total Pembayaran (" + (paxCount * 2) + " Tiket Pulang-Pergi)" : "Total Pembayaran (" + paxCount + " Kursi)");
        }
        if (priceLabel != null) priceLabel.setText(price);

        // Load user name and phone from session
        sessionmanager session = new sessionmanager(this);
        String username = session.getuserdetail();
        if (username != null && !username.isEmpty() && !username.equals("Guest user")) {
            if (passengerNameText != null) passengerNameText.setText(username);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();
        tomorrowDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(tomorrow);

        // Load booked seats from Room DB
        loadBookedSeatsFromRoom();

        if (backbtn != null) {
            backbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }

        if (confirmticket != null) {
            confirmticket.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPaymentMethodDialog(selectedseatID, busroute);
                }
            });
        }
    }

    private void showPaymentMethodDialog(final String seatId, final String route) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_payment_method, null);

        final RadioButton radioQr = dialogView.findViewById(R.id.radioQr);
        final RadioButton radioCash = dialogView.findViewById(R.id.radioCash);
        View cardPaymentQr = dialogView.findViewById(R.id.cardPaymentQr);
        View cardPaymentCash = dialogView.findViewById(R.id.cardPaymentCash);
        TextView paymentTotalText = dialogView.findViewById(R.id.paymentTotalText);
        Button btnProcessPayment = dialogView.findViewById(R.id.btnProcessPayment);

        if (paymentTotalText != null) paymentTotalText.setText(price);

        if (radioQr != null) radioQr.setChecked(true);
        if (radioCash != null) radioCash.setChecked(false);

        View.OnClickListener selectQrListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (radioQr != null) radioQr.setChecked(true);
                if (radioCash != null) radioCash.setChecked(false);
            }
        };

        View.OnClickListener selectCashListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (radioCash != null) radioCash.setChecked(true);
                if (radioQr != null) radioQr.setChecked(false);
            }
        };

        if (radioQr != null) radioQr.setOnClickListener(selectQrListener);
        if (cardPaymentQr != null) cardPaymentQr.setOnClickListener(selectQrListener);

        if (radioCash != null) radioCash.setOnClickListener(selectCashListener);
        if (cardPaymentCash != null) cardPaymentCash.setOnClickListener(selectCashListener);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        final AlertDialog paymentDialog = builder.create();

        if (paymentDialog.getWindow() != null) {
            paymentDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        if (btnProcessPayment != null) {
            btnProcessPayment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    paymentDialog.dismiss();
                    if (radioQr != null && radioQr.isChecked()) {
                        Intent intent = new Intent(Confirmticket.this, QrPaymentActivity.class);
                        intent.putExtra("id", seatId);
                        intent.putExtra("busnumber", busroute);
                        intent.putExtra("bus_name", busName);
                        intent.putExtra("bus_class", busClass);
                        intent.putExtra("schedule", schedule);
                        intent.putExtra("price", price);
                        intent.putExtra("passenger_count", paxCount);
                        intent.putExtra("route_origin", routeOrigin);
                        intent.putExtra("route_destination", routeDestination);
                        intent.putExtra("route_date", routeDate);
                        intent.putExtra("is_round_trip", isRoundTrip);
                        intent.putExtra("route_return_date", routeReturnDate);
                        startActivity(intent);
                        finish();
                    } else {
                        confirmBookingInRoom(seatId, route, "CASH");
                    }
                }
            });
        }

        paymentDialog.show();
    }

    private void loadBookedSeatsFromRoom() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = AppDatabase.getInstance(Confirmticket.this);
                final List<BookingEntity> bookedSeats = db.bookingDao().getBookedSeats(tomorrowDate, busroute);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (bookedSeats != null) {
                            for (BookingEntity booking : bookedSeats) {
                                String seatId = booking.getSeatId();
                                int resId = getResources().getIdentifier(seatId, "id", getPackageName());
                                if (resId != 0) {
                                    ImageView seatView = findViewById(resId);
                                    if (seatView != null) {
                                        seatView.setTag("reserved");
                                        seatView.setImageResource(R.drawable.ic_seat_reserved);
                                    }
                                }
                            }
                        }
                    }
                });
            }
        }).start();
    }

    private void confirmBookingInRoom(final String seatId, final String route, final String paymentMethod) {
        final String username = new sessionmanager(Confirmticket.this).getuserdetail();
        final String effectiveUser = (username != null && !username.isEmpty()) ? username : "Guest user";

        new Thread(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = AppDatabase.getInstance(Confirmticket.this);

                String targetSeat = seatId != null ? seatId : "L11";
                String displayOrigin = routeOrigin != null ? routeOrigin : "Pulo Gebang, Jakarta";
                String displayDest = routeDestination != null ? routeDestination : "Bandung";
                String displayDate = routeDate != null ? routeDate : "Kam, 29 Okt";
                String displayPrice = price != null ? price : "Rp 180.000";
                String displaySchedule = schedule != null ? schedule : "07:00 - 13:30 WIB";
                String displayBusName = busName != null ? busName : "Sinar Jaya Shuttle";

                String methodStr = (paymentMethod != null && !paymentMethod.isEmpty()) ? paymentMethod : "CASH";
                String fullBookingInfo = displayBusName + "|" + displayOrigin + " → " + displayDest + "|" + displayPrice + "|" + displaySchedule + "|" + (isRoundTrip ? "PP" : "OneWay") + "|" + (routeReturnDate != null ? routeReturnDate : "") + "|" + methodStr;

                BookingEntity newBooking = new BookingEntity(effectiveUser, displayDate, fullBookingInfo, targetSeat, true);
                db.bookingDao().insertBooking(newBooking);

                if (!effectiveUser.equals("Guest user")) {
                    db.userDao().updateLastBookedDate(effectiveUser, displayDate);
                }

                final String finalSeat = targetSeat;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(Confirmticket.this, bookedsuccesfully.class);
                        intent.putExtra("id", finalSeat);
                        intent.putExtra("busnumber", busroute);
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
                        intent.putExtra("payment_method", paymentMethod);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        }).start();
    }

    public void Onselectedseat(View view) {
        if ("reserved".equals(view.getTag())) {
            Toast.makeText(this, "Kursi ini sudah dipesan", Toast.LENGTH_SHORT).show();
            return;
        }

        ImageView img = (ImageView) view;
        ImageView recentelectedImage = null;

        if (recentviewID != 0) {
            recentelectedImage = findViewById(recentviewID);
        }

        if ("true".equals(view.getTag())) {
            img.setImageResource(R.drawable.ic_seat_available);
            view.setTag("false");
            selectedseatID = "L11";
            if (selectedSeatLabel != null) {
                selectedSeatLabel.setText("Total Pembayaran (" + paxCount + " Kursi)");
            }
        } else {
            img.setImageResource(R.drawable.ic_seat_selected);
            view.setTag("true");
            selectedseatID = getResources().getResourceEntryName(view.getId()).trim();

            if (selectedSeatLabel != null) {
                selectedSeatLabel.setText("Kursi dipilih: " + getSeatDisplayName(selectedseatID));
            }

            if (recentviewID != 0 && recentviewID != view.getId() && recentelectedImage != null) {
                recentelectedImage.setImageResource(R.drawable.ic_seat_available);
                recentelectedImage.setTag("false");
            }
            recentviewID = view.getId();
        }
    }

    private String getSeatDisplayName(String id) {
        if (id == null) return "";
        switch (id) {
            case "L11": return "Kursi 1 (Depan)";
            case "L21": return "Kursi 2 (Tengah Kiri)";
            case "R21": return "Kursi 3 (Tengah Kanan)";
            case "L31": return "Kursi 4 (Kiri)";
            case "R31": return "Kursi 5 (Kanan)";
            case "L41": return "Kursi 6 (Belakang Kiri)";
            case "L42": return "Kursi 7 (Belakang Tengah)";
            case "R41": return "Kursi 8 (Belakang Kanan)";
            default: return id;
        }
    }
}
