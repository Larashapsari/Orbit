package orbit.ac.id;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import orbit.ac.id.data.AppDatabase;
import orbit.ac.id.data.BookingEntity;

import java.util.List;

public class rideshare_homepage extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private TextView tabFilterSemua, tabFilterAktif, tabFilterSelesai;
    private LinearLayout emptyHistoryView, historyListContainer;
    private Button btnCariTiketHistory;

    public rideshare_homepage() {
        // Required empty public constructor
    }

    public static rideshare_homepage newInstance(String param1, String param2) {
        rideshare_homepage fragment = new rideshare_homepage();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_rideshare_homepage, container, false);

        // Bind Views
        tabFilterSemua = v.findViewById(R.id.tabFilterSemua);
        tabFilterAktif = v.findViewById(R.id.tabFilterAktif);
        tabFilterSelesai = v.findViewById(R.id.tabFilterSelesai);

        emptyHistoryView = v.findViewById(R.id.emptyHistoryView);
        historyListContainer = v.findViewById(R.id.historyListContainer);
        btnCariTiketHistory = v.findViewById(R.id.btnCariTiketHistory);

        if (btnCariTiketHistory != null) {
            btnCariTiketHistory.setOnClickListener(view -> {
                if (getActivity() instanceof welcomePage) {
                    ((welcomePage) getActivity()).chipNavigationBar.setItemSelected(R.id.bottom_navigation_home, true);
                }
            });
        }

        // Setup Filter Tabs
        setupFilterTabs();

        // Load Room Database user history
        loadUserBookingsFromRoom();

        return v;
    }

    private void setupFilterTabs() {
        if (tabFilterSemua == null || tabFilterAktif == null || tabFilterSelesai == null) return;

        tabFilterSemua.setOnClickListener(view -> {
            setTabSelected(tabFilterSemua);
            setTabUnselected(tabFilterAktif);
            setTabUnselected(tabFilterSelesai);
            filterHistoryList("ALL");
        });

        tabFilterAktif.setOnClickListener(view -> {
            setTabUnselected(tabFilterSemua);
            setTabSelected(tabFilterAktif);
            setTabUnselected(tabFilterSelesai);
            filterHistoryList("AKTIF");
        });

        tabFilterSelesai.setOnClickListener(view -> {
            setTabUnselected(tabFilterSemua);
            setTabUnselected(tabFilterAktif);
            setTabSelected(tabFilterSelesai);
            filterHistoryList("SELESAI");
        });
    }

    private void setTabSelected(TextView tab) {
        if (tab == null) return;
        tab.setBackgroundResource(R.drawable.round_corner_bgblue);
        tab.setTextColor(Color.WHITE);
    }

    private void setTabUnselected(TextView tab) {
        if (tab == null) return;
        tab.setBackgroundResource(R.drawable.filter_chip_bg);
        tab.setTextColor(getResources().getColor(R.color.text_primary));
    }

    private void filterHistoryList(String filter) {
        if (historyListContainer == null) return;
        int childCount = historyListContainer.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = historyListContainer.getChildAt(i);
            if (child != null && child.getTag() != null) {
                String cardType = child.getTag().toString();
                if ("ALL".equalsIgnoreCase(filter)) {
                    child.setVisibility(View.VISIBLE);
                } else if ("AKTIF".equalsIgnoreCase(filter)) {
                    child.setVisibility("AKTIF".equalsIgnoreCase(cardType) ? View.VISIBLE : View.GONE);
                } else if ("SELESAI".equalsIgnoreCase(filter)) {
                    child.setVisibility("SELESAI".equalsIgnoreCase(cardType) ? View.VISIBLE : View.GONE);
                }
            }
        }
    }

    private void loadUserBookingsFromRoom() {
        sessionmanager session = new sessionmanager(requireContext());
        final String username = session.getuserdetail();

        if (username == null || username.equals("Guest user")) {
            if (emptyHistoryView != null) emptyHistoryView.setVisibility(View.VISIBLE);
            if (historyListContainer != null) historyListContainer.removeAllViews();
            return;
        }

        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(requireContext());
            final List<BookingEntity> userBookings = db.bookingDao().getUserBookings(username);

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> renderUserBookings(userBookings));
            }
        }).start();
    }

    private void renderUserBookings(List<BookingEntity> bookings) {
        if (historyListContainer == null) return;
        historyListContainer.removeAllViews();

        if (bookings == null || bookings.isEmpty()) {
            if (emptyHistoryView != null) emptyHistoryView.setVisibility(View.VISIBLE);
            return;
        }

        if (emptyHistoryView != null) emptyHistoryView.setVisibility(View.GONE);

        for (final BookingEntity booking : bookings) {
            if (!booking.isBooked()) continue;

            final String busRouteData = booking.getBusRoute();
            String operatorName = "Sinar Jaya Shuttle";
            String routeStr = "Terminal Pulo Gebang → Bandung";
            String priceStr = "Rp 180.000";
            String scheduleStr = "07:00 - 13:30 WIB";
            boolean isPP = false;
            String returnDateStr = "";
            String paymentMethod = "QRIS";

            if (busRouteData != null && busRouteData.contains("|")) {
                String[] parts = busRouteData.split("\\|", -1);
                if (parts.length > 0 && !parts[0].isEmpty()) operatorName = parts[0];
                if (parts.length > 1 && !parts[1].isEmpty()) routeStr = parts[1];
                if (parts.length > 2 && !parts[2].isEmpty()) priceStr = parts[2];
                if (parts.length > 3 && !parts[3].isEmpty()) scheduleStr = parts[3];
                if (parts.length > 4 && "PP".equalsIgnoreCase(parts[4])) isPP = true;
                if (parts.length > 5 && !parts[5].isEmpty()) returnDateStr = parts[5];
                if (parts.length > 6 && !parts[6].isEmpty()) paymentMethod = parts[6];
            } else if (busRouteData != null && !busRouteData.isEmpty()) {
                operatorName = busRouteData;
            }

            final boolean isCash = "CASH".equalsIgnoreCase(paymentMethod);

            // Inflate card view dynamically
            CardView cardView = new CardView(requireContext());
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            cardParams.setMargins(0, 0, 0, dpToPx(12));
            cardView.setLayoutParams(cardParams);
            cardView.setRadius(dpToPx(16));
            cardView.setCardElevation(dpToPx(3));
            cardView.setCardBackgroundColor(Color.WHITE);
            cardView.setTag("AKTIF");

            LinearLayout cardContent = new LinearLayout(requireContext());
            cardContent.setOrientation(LinearLayout.VERTICAL);
            cardContent.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));

            // Row 1: Operator Name & Status Badge
            RelativeLayout topRow = new RelativeLayout(requireContext());
            RelativeLayout.LayoutParams topRowParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            topRow.setLayoutParams(topRowParams);

            TextView tvStatus = new TextView(requireContext());
            int statusId = View.generateViewId();
            tvStatus.setId(statusId);
            if (isCash) {
                tvStatus.setText("Menunggu Pembayaran");
                tvStatus.setTextColor(Color.parseColor("#D97706"));
                tvStatus.setBackgroundResource(R.drawable.filter_chip_bg);
            } else {
                tvStatus.setText("Terkonfirmasi");
                tvStatus.setTextColor(Color.parseColor("#059669"));
                tvStatus.setBackgroundResource(R.drawable.badge_green_bg);
            }
            tvStatus.setTextSize(11);
            tvStatus.setPadding(dpToPx(10), dpToPx(3), dpToPx(10), dpToPx(3));
            tvStatus.setTypeface(null, Typeface.BOLD);

            RelativeLayout.LayoutParams statusParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            statusParams.addRule(RelativeLayout.ALIGN_PARENT_END);
            statusParams.addRule(RelativeLayout.CENTER_VERTICAL);
            tvStatus.setLayoutParams(statusParams);

            TextView tvOperator = new TextView(requireContext());
            tvOperator.setText(operatorName);
            tvOperator.setTextSize(15);
            tvOperator.setTextColor(getResources().getColor(R.color.text_primary));
            tvOperator.setTypeface(null, Typeface.BOLD);
            tvOperator.setMaxLines(1);
            tvOperator.setEllipsize(TextUtils.TruncateAt.END);

            RelativeLayout.LayoutParams opParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            opParams.addRule(RelativeLayout.ALIGN_PARENT_START);
            opParams.addRule(RelativeLayout.LEFT_OF, statusId);
            opParams.addRule(RelativeLayout.CENTER_VERTICAL);
            opParams.setMarginEnd(dpToPx(8));
            tvOperator.setLayoutParams(opParams);

            topRow.addView(tvOperator);
            topRow.addView(tvStatus);
            cardContent.addView(topRow);

            // Row 2: Route Text
            TextView tvRoute = new TextView(requireContext());
            tvRoute.setText(routeStr);
            tvRoute.setTextSize(13);
            tvRoute.setTextColor(getResources().getColor(R.color.text_primary));
            tvRoute.setTypeface(null, Typeface.BOLD);
            LinearLayout.LayoutParams routeParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            routeParams.setMargins(0, dpToPx(8), 0, 0);
            tvRoute.setLayoutParams(routeParams);
            cardContent.addView(tvRoute);

            // Row 3: Date, Schedule & Seat Info
            TextView tvDateSeat = new TextView(requireContext());
            String dateSeatText = "Tgl: " + booking.getDate() + "  •  Jam: " + scheduleStr + "  •  Kursi: " + booking.getSeatId();
            if (isPP && !returnDateStr.isEmpty()) {
                dateSeatText += "\nPulang: " + returnDateStr;
            }
            tvDateSeat.setText(dateSeatText);
            tvDateSeat.setTextSize(12);
            tvDateSeat.setTextColor(getResources().getColor(R.color.text_secondary));
            LinearLayout.LayoutParams dateSeatParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            dateSeatParams.setMargins(0, dpToPx(2), 0, 0);
            tvDateSeat.setLayoutParams(dateSeatParams);
            cardContent.addView(tvDateSeat);

            // Divider line
            View divider = new View(requireContext());
            divider.setBackgroundColor(getResources().getColor(R.color.divider));
            LinearLayout.LayoutParams divParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dpToPx(1)
            );
            divParams.setMargins(0, dpToPx(12), 0, dpToPx(12));
            divider.setLayoutParams(divParams);
            cardContent.addView(divider);

            // Row 4: Price & E-Ticket Button
            RelativeLayout bottomRow = new RelativeLayout(requireContext());
            RelativeLayout.LayoutParams bottomRowParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            bottomRow.setLayoutParams(bottomRowParams);

            TextView tvPrice = new TextView(requireContext());
            tvPrice.setText(priceStr);
            tvPrice.setTextSize(16);
            tvPrice.setTextColor(getResources().getColor(R.color.colorPrimary));
            tvPrice.setTypeface(null, Typeface.BOLD);
            RelativeLayout.LayoutParams priceParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            priceParams.addRule(RelativeLayout.ALIGN_PARENT_START);
            priceParams.addRule(RelativeLayout.CENTER_VERTICAL);
            tvPrice.setLayoutParams(priceParams);

            com.google.android.material.button.MaterialButton btnDetail = new com.google.android.material.button.MaterialButton(requireContext());
            btnDetail.setText("Lihat E-Tiket");
            btnDetail.setTextSize(12);
            btnDetail.setTextColor(Color.WHITE);
            btnDetail.setCornerRadius(dpToPx(19));
            btnDetail.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
            RelativeLayout.LayoutParams btnParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    dpToPx(38)
            );
            btnParams.addRule(RelativeLayout.ALIGN_PARENT_END);
            btnParams.addRule(RelativeLayout.CENTER_VERTICAL);
            btnDetail.setLayoutParams(btnParams);

            final String finalOp = operatorName;
            final String finalRoute = routeStr;
            final String finalPrice = priceStr;
            final String finalSch = scheduleStr;
            final boolean finalIsPP = isPP;
            final String finalReturnDate = returnDateStr;

            btnDetail.setOnClickListener(v -> showDynamicTicketDetailDialog(finalOp, finalRoute, booking.getDate(), finalSch, booking.getSeatId(), finalPrice, finalIsPP, finalReturnDate, isCash));

            bottomRow.addView(tvPrice);
            bottomRow.addView(btnDetail);
            cardContent.addView(bottomRow);

            cardView.addView(cardContent);
            historyListContainer.addView(cardView);
        }
    }

    private void showDynamicTicketDetailDialog(String operator, String route, String date, String schedule, String seat, String price, boolean isPP, String returnDate, boolean isCash) {
        StringBuilder msg = new StringBuilder();
        msg.append("Rincian E-Tiket Resmi Orbit:\n");
        if (isCash) {
            msg.append("\n• Status: Menunggu Pembayaran (Tunai di Loket)");
        } else {
            msg.append("\n• Status: Terkonfirmasi (Lunas via QRIS)");
        }
        msg.append("\n• Operator: ").append(operator);
        msg.append("\n• Rute: ").append(route);
        msg.append("\n• Tanggal & Jam: ").append(date).append(" (").append(schedule).append(")");
        if (isPP && returnDate != null && !returnDate.isEmpty()) {
            msg.append("\n• Tanggal Pulang: ").append(returnDate);
        }
        msg.append("\n• Kursi: ").append(seat);
        msg.append("\n• Total Bayar: ").append(price);
        if (isCash) {
            msg.append("\n\nHarap lakukan pembayaran tunai di loket resmi maksimal 30 menit sebelum jam keberangkatan bus.");
        } else {
            msg.append("\n\nTunjukkan E-Tiket ini kepada petugas saat naik ke bus.");
        }

        new AlertDialog.Builder(getContext())
                .setTitle("E-Tiket Perjalanan Bus")
                .setMessage(msg.toString())
                .setPositiveButton("Tutup", null)
                .show();
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
