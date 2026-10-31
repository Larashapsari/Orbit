package orbit.ac.id;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import orbit.ac.id.data.AppDatabase;
import orbit.ac.id.data.BookingEntity;
import orbit.ac.id.data.UserEntity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class dashboard_booking extends Fragment {

    TextView date, day_month, busroutetext, recentbooktext, dateonticket, busrouteonticket, seatnumberonticket;
    TextView headerSubtitleText;
    ImageView backArrow, headerCalendarIcon;
    View btnBackContainer, btnCalendarContainer, headerSubtitleContainer;
    View cardSinarJaya, cardMurniJaya, cardAgraMas, cardRestu, cardRosalia;
    View btnDetailSinarJaya, btnDetailMurniJaya, btnDetailAgraMas, btnDetailRestu, btnDetailRosalia;
    RadioButton radioAllClass, radioExecutive, radioSleeper, radioEconomy;
    TextView filterFasilitasBtn;
    RadioGroup radioGroupBusClass;
    LinearLayout emptyStateView;
    Button btnResetFilters;

    Button serchbusbtn, trackbus;
    TextInputLayout outbox;
    ProgressBar progressBar;

    String routeOrigin = "Pulo Gebang, Jakarta";
    String routeDestination = "Bandung";
    String routeDate = "Sab, 25 Okt 2025";
    String selectedTimeSlot = "Semua Slot";
    String selectedBusClass = "ALL";
    boolean isRoundTrip = false;
    String routeReturnDate = "Sab, 31 Okt 2025";

    int initialPassengerCount = 1;
    int dialogAdultCount = 1;
    int dialogInfantCount = 0;

    public dashboard_booking() {
        // Required empty public constructor
    }

    public static dashboard_booking newInstance(String param1, String param2) {
        dashboard_booking fragment = new dashboard_booking();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String fromArg = getArguments().getString("from");
            String toArg = getArguments().getString("to");
            String dateArg = getArguments().getString("date");
            String slotArg = getArguments().getString("time_slot");
            boolean roundTripArg = getArguments().getBoolean("is_round_trip", false);
            String returnDateArg = getArguments().getString("return_date");
            int paxArg = getArguments().getInt("passenger_count", 1);
            int infantArg = getArguments().getInt("infant_count", 0);

            if (fromArg != null && !fromArg.isEmpty()) routeOrigin = fromArg;
            if (toArg != null && !toArg.isEmpty()) routeDestination = toArg;
            if (dateArg != null && !dateArg.isEmpty()) routeDate = dateArg;
            if (slotArg != null && !slotArg.isEmpty()) selectedTimeSlot = slotArg;
            isRoundTrip = roundTripArg;
            if (returnDateArg != null && !returnDateArg.isEmpty()) routeReturnDate = returnDateArg;
            if (paxArg > 0) initialPassengerCount = paxArg;
            dialogInfantCount = infantArg;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof welcomePage) {
            ((welcomePage) getActivity()).setBottomNavVisibility(false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() instanceof welcomePage) {
            ((welcomePage) getActivity()).setBottomNavVisibility(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dashboard_booking, container, false);

        if (getActivity() instanceof welcomePage) {
            ((welcomePage) getActivity()).setBottomNavVisibility(false);
        }

        final String username = new sessionmanager(requireContext()).getuserdetail();

        // View Bindings
        btnBackContainer = v.findViewById(R.id.btnBackContainer);
        backArrow = v.findViewById(R.id.backArrow);

        btnCalendarContainer = v.findViewById(R.id.btnCalendarContainer);
        headerCalendarIcon = v.findViewById(R.id.headerCalendarIcon);

        headerSubtitleContainer = v.findViewById(R.id.headerSubtitleContainer);
        headerSubtitleText = v.findViewById(R.id.headerSubtitleText);

        radioGroupBusClass = v.findViewById(R.id.radioGroupBusClass);
        radioAllClass = v.findViewById(R.id.radioAllClass);
        radioExecutive = v.findViewById(R.id.radioExecutive);
        radioSleeper = v.findViewById(R.id.radioSleeper);
        radioEconomy = v.findViewById(R.id.radioEconomy);
        filterFasilitasBtn = v.findViewById(R.id.filterFasilitasBtn);

        cardSinarJaya = v.findViewById(R.id.relativeticketview);
        cardMurniJaya = v.findViewById(R.id.cardMurniJaya);
        cardAgraMas = v.findViewById(R.id.cardAgraMas);
        cardRestu = v.findViewById(R.id.cardRestu);
        cardRosalia = v.findViewById(R.id.cardRosalia);

        btnDetailSinarJaya = v.findViewById(R.id.btnDetailSinarJaya);
        btnDetailMurniJaya = v.findViewById(R.id.btnDetailMurniJaya);
        btnDetailAgraMas = v.findViewById(R.id.btnDetailAgraMas);
        btnDetailRestu = v.findViewById(R.id.btnDetailRestu);
        btnDetailRosalia = v.findViewById(R.id.btnDetailRosalia);

        emptyStateView = v.findViewById(R.id.emptyStateView);
        btnResetFilters = v.findViewById(R.id.btnResetFilters);

        progressBar = v.findViewById(R.id.progressbarseatbooking);
        if (progressBar != null) progressBar.setVisibility(View.GONE);

        date = v.findViewById(R.id.setdatedate);
        day_month = v.findViewById(R.id.day_year);
        busroutetext = v.findViewById(R.id.editTextonseatbooking);
        serchbusbtn = v.findViewById(R.id.serchbustn);
        recentbooktext = v.findViewById(R.id.recentbookedtext);
        dateonticket = v.findViewById(R.id.dateonticket);
        busrouteonticket = v.findViewById(R.id.busroutetextonticketedit);
        seatnumberonticket = v.findViewById(R.id.seatonticketedit);
        trackbus = v.findViewById(R.id.trackbusbtn);
        outbox = v.findViewById(R.id.outboxofbusrouteonbooking);

        if (btnResetFilters != null) {
            btnResetFilters.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedTimeSlot = "Semua Slot";
                    selectedBusClass = "ALL";
                    if (radioAllClass != null) radioAllClass.setChecked(true);
                    applyCombinedFilters();
                    Toast.makeText(getContext(), "Menampilkan semua jadwal bus", Toast.LENGTH_SHORT).show();
                }
            });
        }

        updateHeaderSubtitle();

        // 1. Back Arrow -> Return to Dashboard Home
        View.OnClickListener backClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToHome();
            }
        };

        if (btnBackContainer != null) btnBackContainer.setOnClickListener(backClickListener);
        if (backArrow != null) backArrow.setOnClickListener(backClickListener);

        // 2. Subtitle / Calendar -> Change Route, Date, or Time Slot
        View.OnClickListener changeSearchListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRouteOrDatePickerDialog();
            }
        };

        if (headerSubtitleContainer != null) headerSubtitleContainer.setOnClickListener(changeSearchListener);
        if (headerSubtitleText != null) headerSubtitleText.setOnClickListener(changeSearchListener);
        if (btnCalendarContainer != null) btnCalendarContainer.setOnClickListener(changeSearchListener);
        if (headerCalendarIcon != null) headerCalendarIcon.setOnClickListener(changeSearchListener);

        // 3. Class Filter Logic
        setupFilterLogic();

        // 4. Click listeners for all bus cards and detail buttons
        setupBusCardClickListeners();

        // 5. Apply Initial Filters (Class + Selected Time Slot)
        applyCombinedFilters();

        // Load user and booking details from Room Database
        loadBookingDataFromRoom(username);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateToHome();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        return v;
    }

    private void updateHeaderSubtitle() {
        if (headerSubtitleText != null) {
            String subtitle = routeOrigin + " → " + routeDestination + "  •  " + routeDate;
            if (isRoundTrip) {
                subtitle += " (Pulang: " + routeReturnDate + ")";
            }
            if (selectedTimeSlot != null && !selectedTimeSlot.equals("Semua Slot")) {
                subtitle += "  •  Jam " + selectedTimeSlot;
            }
            headerSubtitleText.setText(subtitle);
        }
    }

    private void applyCombinedFilters() {
        boolean showSinarJaya = true;
        boolean showMurniJaya = true;
        boolean showAgraMas = true;
        boolean showRestu = true;
        boolean showRosalia = true;

        // 1. Filter based on selectedBusClass
        if ("Executive".equalsIgnoreCase(selectedBusClass)) {
            showMurniJaya = false; // Economy
            showAgraMas = false;   // Sleeper
            showRestu = false;     // Economy
        } else if ("Sleeper".equalsIgnoreCase(selectedBusClass)) {
            showSinarJaya = false; // Executive
            showMurniJaya = false; // Economy
            showRestu = false;     // Economy
            showRosalia = false;   // Executive
        } else if ("Economy".equalsIgnoreCase(selectedBusClass)) {
            showSinarJaya = false; // Executive
            showAgraMas = false;   // Sleeper
            showRosalia = false;   // Executive
        }

        // 2. Filter based on selectedTimeSlot
        if ("06:00 - 12:00".equalsIgnoreCase(selectedTimeSlot)) {
            // Pagi: Sinar Jaya (07:00), Murni Jaya (08:30)
            showAgraMas = false;
            showRestu = false;
            showRosalia = false;
        } else if ("15:00 - 18:00".equalsIgnoreCase(selectedTimeSlot)) {
            // Siang/Sore: Agra Mas (15:30)
            showSinarJaya = false;
            showMurniJaya = false;
            showRestu = false;
            showRosalia = false;
        } else if ("18:00 - 21:00".equalsIgnoreCase(selectedTimeSlot)) {
            // Malam: Restu Express (19:00)
            showSinarJaya = false;
            showMurniJaya = false;
            showAgraMas = false;
            showRosalia = false;
        } else if ("21:00 - 00:00".equalsIgnoreCase(selectedTimeSlot)) {
            // Dini Hari: Rosalia Indah (21:30)
            showSinarJaya = false;
            showMurniJaya = false;
            showAgraMas = false;
            showRestu = false;
        }

        if (cardSinarJaya != null) cardSinarJaya.setVisibility(showSinarJaya ? View.VISIBLE : View.GONE);
        if (cardMurniJaya != null) cardMurniJaya.setVisibility(showMurniJaya ? View.VISIBLE : View.GONE);
        if (cardAgraMas != null) cardAgraMas.setVisibility(showAgraMas ? View.VISIBLE : View.GONE);
        if (cardRestu != null) cardRestu.setVisibility(showRestu ? View.VISIBLE : View.GONE);
        if (cardRosalia != null) cardRosalia.setVisibility(showRosalia ? View.VISIBLE : View.GONE);

        boolean anyVisible = showSinarJaya || showMurniJaya || showAgraMas || showRestu || showRosalia;
        if (emptyStateView != null) {
            emptyStateView.setVisibility(anyVisible ? View.GONE : View.VISIBLE);
        }

        updateHeaderSubtitle();
    }

    private void navigateToHome() {
        if (getActivity() instanceof welcomePage) {
            ((welcomePage) getActivity()).setBottomNavVisibility(true);
            ((welcomePage) getActivity()).chipNavigationBar.setItemSelected(R.id.bottom_navigation_home, true);
        }
        if (getActivity() != null) {
            if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getActivity().getSupportFragmentManager().popBackStack();
            } else {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit)
                        .replace(R.id.fragmant_container, new Dashboard_home())
                        .commit();
            }
        }
    }

    // Dialog to choose modifying Route, Date, or Time Slot
    private void showRouteOrDatePickerDialog() {
        Context ctx = getContext();
        if (ctx == null) return;

        final String timeSlotOption = "Semua Slot".equals(selectedTimeSlot) ?
                "Pilih Slot Waktu Jam" : "Ubah Slot Waktu Jam (" + selectedTimeSlot + ")";

        final String[] options = {"Ganti Rute (Kota Asal & Tujuan)", "Ganti Tanggal Keberangkatan", timeSlotOption};
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("Ubah Pencarian Bus");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showRouteLocationSearchDialog();
                } else if (which == 1) {
                    showDatePicker();
                } else if (which == 2) {
                    showTimeSlotChoiceDialog();
                }
            }
        });
        builder.setNegativeButton("Batal", null);
        builder.show();
    }

    private void showTimeSlotChoiceDialog() {
        Context ctx = getContext();
        if (ctx == null) return;

        final String[] slots = {
                "06:00 - 12:00 (Pagi)",
                "15:00 - 18:00 (Siang - Sore)",
                "18:00 - 21:00 (Malam)",
                "21:00 - 00:00 (Dini Hari)",
                "Tampilkan Semua Slot Waktu"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("Pilih Slot Waktu Keberangkatan");
        builder.setItems(slots, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    selectedTimeSlot = "06:00 - 12:00";
                } else if (i == 1) {
                    selectedTimeSlot = "15:00 - 18:00";
                } else if (i == 2) {
                    selectedTimeSlot = "18:00 - 21:00";
                } else if (i == 3) {
                    selectedTimeSlot = "21:00 - 00:00";
                } else {
                    selectedTimeSlot = "Semua Slot";
                }
                applyCombinedFilters();
                Toast.makeText(getContext(), "Filter slot waktu diperbarui: " + selectedTimeSlot, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Batal", null);
        builder.show();
    }

    private void showRouteLocationSearchDialog() {
        showLocationSearchDialog(true);
    }

    private void showLocationSearchDialog(final boolean isOrigin) {
        final Context ctx = getContext();
        if (ctx == null) return;

        View dialogView = LayoutInflater.from(ctx).inflate(R.layout.dialog_search_address, null);

        final android.widget.EditText editSearchAddress = dialogView.findViewById(R.id.editSearchAddress);
        TextView btnCancelSearch = dialogView.findViewById(R.id.btnCancelSearch);

        final LinearLayout listSaatIni = dialogView.findViewById(R.id.listSaatIni);
        final LinearLayout listFavorit = dialogView.findViewById(R.id.listFavorit);
        final View sectionHeaderSaatIni = dialogView.findViewById(R.id.sectionHeaderSaatIni);
        final View sectionHeaderFavorit = dialogView.findViewById(R.id.sectionHeaderFavorit);

        if (editSearchAddress != null) {
            editSearchAddress.setHint(isOrigin ? "Masukan Kota Asal (Dari)" : "Masukan Kota Tujuan (Ke)");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        if (btnCancelSearch != null) {
            btnCancelSearch.setOnClickListener(v -> dialog.dismiss());
        }

        final String[][] saatIniData = {
                {"Terminal Pulo Gebang, Jakarta → Bandung", "Rute Terakhir", "Rute", "history"},
                {"Terminal Pulo Gebang, Jakarta", "Terminal Utama", isOrigin ? "Naik di" : "Turun di", "history"},
                {"Bandung", "Kota Tujuan", isOrigin ? "Naik di" : "Turun di", "history"}
        };

        final String[][] favoritData = {
                {"Pasteur, Bandung", "Pusat Kota Bandung", isOrigin ? "Naik di" : "Turun di", "bus"},
                {"Dipatiukur, Bandung", "Area Kampus & Kota", isOrigin ? "Naik di" : "Turun di", "bus"},
                {"Cibitung, Bekasi", "Terminal / Exit Tol", isOrigin ? "Naik di" : "Turun di", "bus"},
                {"Terminal Pulo Gebang, Jakarta", "Terminal Terpadu", isOrigin ? "Naik di" : "Turun di", "bus"},
                {"Buahbatu, Bandung", "Bandung Selatan", isOrigin ? "Naik di" : "Turun di", "bus"},
                {"Terminal Kampung Rambutan, Jakarta", "Terminal Jakarta Timur", isOrigin ? "Naik di" : "Turun di", "bus"},
                {"Terminal Bungurasih, Surabaya", "Terminal Utama Surabaya", isOrigin ? "Naik di" : "Turun di", "bus"},
                {"Balubur Town Square, Bandung", "Point Shuttle Baltos", isOrigin ? "Naik di" : "Turun di", "bus"},
                {"Terminal Giwangan, Yogyakarta", "Terminal Utama Jogja", isOrigin ? "Naik di" : "Turun di", "bus"},
                {"Terminal Tirtonadi, Surakarta", "Terminal Solo", isOrigin ? "Naik di" : "Turun di", "bus"}
        };

        populateSearchList(ctx, listSaatIni, saatIniData, isOrigin, dialog);
        populateSearchList(ctx, listFavorit, favoritData, isOrigin, dialog);

        if (editSearchAddress != null) {
            editSearchAddress.addTextChangedListener(new android.text.TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String query = s.toString().toLowerCase().trim();
                    filterSearchList(ctx, listSaatIni, saatIniData, query, isOrigin, dialog);
                    filterSearchList(ctx, listFavorit, favoritData, query, isOrigin, dialog);

                    if (query.isEmpty()) {
                        if (sectionHeaderSaatIni != null) sectionHeaderSaatIni.setVisibility(View.VISIBLE);
                        if (sectionHeaderFavorit != null) sectionHeaderFavorit.setVisibility(View.VISIBLE);
                    } else {
                        if (sectionHeaderSaatIni != null) sectionHeaderSaatIni.setVisibility(View.GONE);
                        if (sectionHeaderFavorit != null) sectionHeaderFavorit.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void afterTextChanged(android.text.Editable s) {}
            });
        }

        dialog.show();
    }

    private void populateSearchList(Context ctx, LinearLayout container, String[][] items, boolean isOrigin, AlertDialog dialog) {
        filterSearchList(ctx, container, items, "", isOrigin, dialog);
    }

    private void filterSearchList(Context ctx, LinearLayout container, String[][] items, String query, boolean isOrigin, AlertDialog dialog) {
        if (container == null) return;
        container.removeAllViews();

        for (final String[] itemData : items) {
            String title = itemData[0];
            String subtitle = itemData[1];
            String tag = itemData[2];
            String type = itemData[3];

            if (!query.isEmpty() && !title.toLowerCase().contains(query) && !subtitle.toLowerCase().contains(query)) {
                continue;
            }

            View itemView = LayoutInflater.from(ctx).inflate(R.layout.item_search_address, container, false);
            TextView tvTitle = itemView.findViewById(R.id.itemTitle);
            TextView tvSubtitle = itemView.findViewById(R.id.itemSubtitle);
            TextView tvTag = itemView.findViewById(R.id.itemTag);
            ImageView ivIcon = itemView.findViewById(R.id.itemIcon);

            if (tvTitle != null) tvTitle.setText(title);
            if (tvSubtitle != null && subtitle != null && !subtitle.isEmpty()) {
                tvSubtitle.setText(subtitle);
                tvSubtitle.setVisibility(View.VISIBLE);
            }
            if (tvTag != null) tvTag.setText(tag);

            if (ivIcon != null) {
                if ("history".equals(type)) {
                    ivIcon.setImageResource(R.drawable.calendar);
                } else {
                    ivIcon.setImageResource(R.drawable.ic_baseline_directions_bus_24);
                }
            }

            itemView.setOnClickListener(v -> {
                String selectedTitle = itemData[0];
                if (selectedTitle.contains("→")) {
                    selectedTitle = selectedTitle.split("→")[0].trim();
                }
                dialog.dismiss();

                if (isOrigin) {
                    routeOrigin = selectedTitle;
                    Toast.makeText(getContext(), "Kota Asal: " + routeOrigin, Toast.LENGTH_SHORT).show();
                    showLocationSearchDialog(false); // Next: Pick destination
                } else {
                    routeDestination = selectedTitle;
                    updateHeaderSubtitle();
                    Toast.makeText(getContext(), "Rute diperbarui: " + routeOrigin + " → " + routeDestination, Toast.LENGTH_SHORT).show();
                }
            });

            container.addView(itemView);
        }
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                        Calendar selectedCalendar = Calendar.getInstance();
                        selectedCalendar.set(selectedYear, selectedMonth, selectedDay);
                        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy", new Locale("id", "ID"));
                        routeDate = sdf.format(selectedCalendar.getTime());
                        updateHeaderSubtitle();
                        Toast.makeText(getContext(), "Tanggal diperbarui: " + routeDate, Toast.LENGTH_SHORT).show();
                    }
                },
                year, month, day
        );
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    // Filter Logic implementation
    private void setupFilterLogic() {
        if (radioGroupBusClass == null) return;

        radioGroupBusClass.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                resetFilterStyles();

                if (checkedId == R.id.radioAllClass) {
                    setFilterStyle(radioAllClass, true);
                    selectedBusClass = "ALL";
                } else if (checkedId == R.id.radioExecutive) {
                    setFilterStyle(radioExecutive, true);
                    selectedBusClass = "Executive";
                } else if (checkedId == R.id.radioSleeper) {
                    setFilterStyle(radioSleeper, true);
                    selectedBusClass = "Sleeper";
                } else if (checkedId == R.id.radioEconomy) {
                    setFilterStyle(radioEconomy, true);
                    selectedBusClass = "Economy";
                }
                applyCombinedFilters();
            }
        });

        if (filterFasilitasBtn != null) {
            filterFasilitasBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showFacilityFilterDialog();
                }
            });
        }
    }

    private void resetFilterStyles() {
        setFilterStyle(radioAllClass, false);
        setFilterStyle(radioExecutive, false);
        setFilterStyle(radioSleeper, false);
        setFilterStyle(radioEconomy, false);
    }

    private void setFilterStyle(RadioButton radioButton, boolean isSelected) {
        if (radioButton == null) return;
        if (isSelected) {
            radioButton.setBackgroundResource(R.drawable.round_corner_bgblue);
            radioButton.setTextColor(Color.WHITE);
        } else {
            radioButton.setBackgroundResource(R.drawable.filter_chip_bg);
            radioButton.setTextColor(getResources().getColor(R.color.text_primary));
        }
    }

    private void showFacilityFilterDialog() {
        final String[] facilities = {"AC & Rec. Seat", "WiFi Gratis", "USB Charger", "Bagasi Luas", "Makan / Snack"};
        final boolean[] checked = {true, true, true, true, false};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Filter Berdasarkan Fasilitas")
                .setMultiChoiceItems(facilities, checked, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checked[which] = isChecked;
                    }
                })
                .setPositiveButton("Terapkan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(), "Filter fasilitas diterapkan", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void setupBusCardClickListeners() {
        View.OnClickListener sinarJayaListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBusDetailDialog("Sinar Jaya Shuttle", "Shuttle Executive 1+1", "07:00 - 13:30 WIB (6j 30m)", "Rp 180.000", "SB1");
            }
        };
        if (cardSinarJaya != null) cardSinarJaya.setOnClickListener(sinarJayaListener);
        if (btnDetailSinarJaya != null) btnDetailSinarJaya.setOnClickListener(sinarJayaListener);

        View.OnClickListener murniJayaListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBusDetailDialog("Murni Jaya", "Economy AC 2+2", "08:30 - 15:00 WIB (6j 30m)", "Rp 135.000", "MJ1");
            }
        };
        if (cardMurniJaya != null) cardMurniJaya.setOnClickListener(murniJayaListener);
        if (btnDetailMurniJaya != null) btnDetailMurniJaya.setOnClickListener(murniJayaListener);

        View.OnClickListener agraMasListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBusDetailDialog("Agra Mas", "Sleeper Luxury 1+1", "15:30 - 22:00 WIB (6j 30m)", "Rp 240.000", "AM1");
            }
        };
        if (cardAgraMas != null) cardAgraMas.setOnClickListener(agraMasListener);
        if (btnDetailAgraMas != null) btnDetailAgraMas.setOnClickListener(agraMasListener);

        View.OnClickListener restuListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBusDetailDialog("Restu Express", "Economy AC 2+3", "19:00 - 01:30 WIB (6j 30m)", "Rp 110.000", "RE1");
            }
        };
        if (cardRestu != null) cardRestu.setOnClickListener(restuListener);
        if (btnDetailRestu != null) btnDetailRestu.setOnClickListener(restuListener);

        View.OnClickListener rosaliaListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBusDetailDialog("Rosalia Indah", "Executive Plus 2+2", "21:30 - 04:00 WIB (6j 30m)", "Rp 195.000", "RI1");
            }
        };
        if (cardRosalia != null) cardRosalia.setOnClickListener(rosaliaListener);
        if (btnDetailRosalia != null) btnDetailRosalia.setOnClickListener(rosaliaListener);
    }

    private void showBusDetailDialog(final String operatorName, final String busClass, final String schedule, final String priceStr, final String busCode) {
        Context ctx = getContext();
        if (ctx == null) return;

        View dialogView = LayoutInflater.from(ctx).inflate(R.layout.dialog_bus_detail, null);

        TextView detailOperatorName = dialogView.findViewById(R.id.detailOperatorName);
        TextView detailBusClass = dialogView.findViewById(R.id.detailBusClass);
        TextView detailRouteText = dialogView.findViewById(R.id.detailRouteText);
        TextView detailScheduleText = dialogView.findViewById(R.id.detailScheduleText);
        TextView detailPassengerSummaryText = dialogView.findViewById(R.id.detailPassengerSummaryText);
        final TextView detailPriceText = dialogView.findViewById(R.id.detailPriceText);
        final TextView detailPriceLabel = dialogView.findViewById(R.id.detailPriceLabel);

        ImageView detailBusImage = dialogView.findViewById(R.id.detailBusImage);
        Button btnBookBusDetail = dialogView.findViewById(R.id.btnBookBusDetail);

        dialogAdultCount = initialPassengerCount;

        // Parse price per single ticket
        final int unitPrice = parsePriceNumber(priceStr);
        final int tripMultiplier = isRoundTrip ? 2 : 1;
        final int total = unitPrice * dialogAdultCount * tripMultiplier;

        String paxSummary;
        if (dialogInfantCount > 0) {
            paxSummary = String.format(Locale.US, "%02d Dewasa, %02d Bayi", dialogAdultCount, dialogInfantCount);
        } else {
            paxSummary = String.format(Locale.US, "%02d Dewasa", dialogAdultCount);
        }

        if (isRoundTrip) {
            paxSummary += " (Pulang-Pergi)";
        }

        if (detailPassengerSummaryText != null) {
            detailPassengerSummaryText.setText(paxSummary);
        }
        if (detailPriceLabel != null) {
            detailPriceLabel.setText(isRoundTrip ? "Total Pembayaran (Pulang-Pergi 2x Tiket)" : "Total (" + paxSummary + ")");
        }
        if (detailPriceText != null) {
            detailPriceText.setText("Rp " + String.format(Locale.GERMAN, "%,d", total));
        }

        if (detailOperatorName != null) detailOperatorName.setText(operatorName);
        if (detailBusClass != null) detailBusClass.setText(busClass + (isRoundTrip ? " • Pulang Pergi" : ""));

        if (detailRouteText != null) {
            if (isRoundTrip) {
                detailRouteText.setText("1. Pergi: " + routeOrigin + " → " + routeDestination + "\n2. Pulang: " + routeDestination + " → " + routeOrigin);
            } else {
                detailRouteText.setText(routeOrigin + " → " + routeDestination);
            }
        }

        if (detailScheduleText != null) {
            if (isRoundTrip) {
                detailScheduleText.setText("Berangkat: " + routeDate + " (" + schedule + ")\nPulang: " + routeReturnDate + " (" + schedule + ")");
            } else {
                detailScheduleText.setText(routeDate + "  •  " + schedule);
            }
        }

        if (detailBusImage != null) {
            if ("SB1".equalsIgnoreCase(busCode) || "AM1".equalsIgnoreCase(busCode)) {
                detailBusImage.setImageResource(R.drawable.busfront);
            } else {
                detailBusImage.setImageResource(R.drawable.busfinal);
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        if (btnBookBusDetail != null) {
            btnBookBusDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    String formattedTotal = "Rp " + String.format(Locale.GERMAN, "%,d", total);

                    Intent intent = new Intent(getContext(), Confirmticket.class);
                    intent.putExtra("busnumber", busCode);
                    intent.putExtra("bus_name", operatorName);
                    intent.putExtra("bus_class", busClass);
                    intent.putExtra("schedule", schedule);
                    intent.putExtra("price", formattedTotal);
                    intent.putExtra("passenger_count", dialogAdultCount);
                    intent.putExtra("infant_count", dialogInfantCount);
                    intent.putExtra("route_origin", routeOrigin);
                    intent.putExtra("route_destination", routeDestination);
                    intent.putExtra("route_date", routeDate);
                    intent.putExtra("is_round_trip", isRoundTrip);
                    intent.putExtra("route_return_date", routeReturnDate);
                    startActivity(intent);
                }
            });
        }

        dialog.show();
    }

    private int parsePriceNumber(String priceStr) {
        if (priceStr == null) return 180000;
        try {
            String digits = priceStr.replaceAll("[^0-9]", "");
            if (!digits.isEmpty()) {
                return Integer.parseInt(digits);
            }
        } catch (Exception ignored) {}
        return 180000;
    }

    private void loadBookingDataFromRoom(final String username) {
        if (username == null || username.equals("Guest user")) return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = AppDatabase.getInstance(requireContext());
                final UserEntity user = db.userDao().getUserByUsername(username);

                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (user != null && user.getBus_no() != null && busroutetext != null) {
                                busroutetext.setText(user.getBus_no());
                            }
                        }
                    });
                }
            }
        }).start();
    }
}
