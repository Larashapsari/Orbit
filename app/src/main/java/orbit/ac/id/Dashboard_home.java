
package orbit.ac.id;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.switchmaterial.SwitchMaterial;

import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Dashboard_home extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private FragmentActivity myContext;

    private ImageView headerProfileImageView;

    private int adultCount = 1;
    private int infantCount = 0;

    // List of terminals/cities for address search
    private final String[] locations = {
            "Terminal Pulo Gebang, Jakarta",
            "Terminal Kampung Rambutan, Jakarta",
            "Terminal Kalideres, Jakarta",
            "Terminal Leuwipanjang, Bandung",
            "Surapati (Suci), Bandung",
            "Terminal Giwangan, Yogyakarta",
            "Terminal Tirtonadi, Surakarta",
            "Terminal Purabaya, Surabaya",
            "Terminal Terboyo, Semarang",
            "Terminal Bekasi",
            "Terminal Bogor",
            "Kebayoran Baru, Jakarta Selatan",
            "Tangerang Kota"
    };

    public Dashboard_home() {
        // Required empty public constructor
    }

    public static Dashboard_home newInstance(String param1, String param2) {
        Dashboard_home fragment = new Dashboard_home();
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
        final View v = inflater.inflate(R.layout.fragment_dashboard_home, container, false);

        // ===== CONTROLS =====
        final View profileIcon = v.findViewById(R.id.profileIcon);
        headerProfileImageView = v.findViewById(R.id.headerProfileImageView);
        loadHeaderProfileImage();
        final View fromRow = v.findViewById(R.id.fromRow);
        final View toRow = v.findViewById(R.id.toRow);
        final TextView fromText = v.findViewById(R.id.fromText);
        final TextView toText = v.findViewById(R.id.toText);
        final View dateRow = v.findViewById(R.id.dateRow);
        final TextView setdatedate = v.findViewById(R.id.setdatedate);
        final View swapBtn = v.findViewById(R.id.swapBtn);
        final Button serchbustn = v.findViewById(R.id.serchbustn);

        final SwitchMaterial switchPulangPergi = v.findViewById(R.id.switchPulangPergi);
        final View returnDateDivider = v.findViewById(R.id.returnDateDivider);
        final View returnDateContainer = v.findViewById(R.id.returnDateContainer);
        final TextView returnDateText = v.findViewById(R.id.returnDateText);

        final View passengerContainer = v.findViewById(R.id.passengerContainer);
        final TextView passengerText = v.findViewById(R.id.passengerText);

        // Setup ViewPager2 Carousel (Page 1 & Page 2 stacking swipe)
        androidx.viewpager2.widget.ViewPager2 viewPagerCarousel = v.findViewById(R.id.viewPagerCarousel);
        if (viewPagerCarousel != null) {
            viewPagerCarousel.setAdapter(new CarouselAdapter());
        }

        // Profile icon -> Opens Akun Saya (dashboard_profile)
        if (profileIcon != null) {
            profileIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getActivity() instanceof welcomePage) {
                        ((welcomePage) getActivity()).chipNavigationBar.setItemSelected(R.id.profiletab, true);
                    } else if (getActivity() != null) {
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit)
                                .replace(R.id.fragmant_container, new dashboard_profile())
                                .addToBackStack(null)
                                .commit();
                    }
                }
            });
        }

        // Pulang Pergi Switch Toggle
        if (switchPulangPergi != null) {
            switchPulangPergi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                        if (returnDateDivider != null) returnDateDivider.setVisibility(View.VISIBLE);
                        if (returnDateContainer != null) returnDateContainer.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), "Pilihan Pulang Pergi Diaktifkan", Toast.LENGTH_SHORT).show();
                    } else {
                        if (returnDateDivider != null) returnDateDivider.setVisibility(View.GONE);
                        if (returnDateContainer != null) returnDateContainer.setVisibility(View.GONE);
                    }
                }
            });
        }

        // Return Date Picker
        if (returnDateContainer != null && returnDateText != null) {
            returnDateContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showCustomDatePicker(returnDateText);
                }
            });
        }

        // Passenger Picker
        if (passengerContainer != null && passengerText != null) {
            passengerContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPassengerPickerDialog(passengerText);
                }
            });
        }

        // Interactive Address Search for "Dari" and "Ke"
        if (fromRow != null && fromText != null) {
            fromRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showLocationSearchDialog("Masukan Kota Asal", fromText, true);
                }
            });
        }

        if (toRow != null && toText != null) {
            toRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showLocationSearchDialog("Masukan Kota Tujuan", toText, false);
                }
            });
        }

        // Swap Origin & Destination
        if (swapBtn != null && fromText != null && toText != null) {
            swapBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CharSequence temp = fromText.getText();
                    fromText.setText(toText.getText());
                    toText.setText(temp);
                }
            });
        }

        // Custom Date Picker Dialog
        if (dateRow != null && setdatedate != null) {
            View.OnClickListener dateClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showCustomDatePicker(setdatedate);
                }
            };
            dateRow.setOnClickListener(dateClickListener);
            setdatedate.setOnClickListener(dateClickListener);
        }

        // Cari Bus Button - Alur Video
        if (serchbustn != null) {
            serchbustn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String from = fromText != null ? fromText.getText().toString() : "Bandung";
                    final String to = toText != null ? toText.getText().toString() : "Jakarta";
                    final String date = setdatedate != null ? setdatedate.getText().toString() : "Kam, 29 Okt";
                    final boolean isRoundTrip = switchPulangPergi != null && switchPulangPergi.isChecked();
                    final String returnDate = (returnDateText != null && !returnDateText.getText().toString().isEmpty()) ? returnDateText.getText().toString() : "Sab, 31 Okt";
                    showDepartureSlotBottomSheet(from, to, date, isRoundTrip, returnDate);
                }
            });
        }

        // Back pressed callback
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Apakah Anda yakin ingin keluar?")
                        .setCancelable(true)
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (getActivity() != null) {
                                    getActivity().finish();
                                }
                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                builder.create().show();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        return v;
    }

    // Dialog Select Passenger (Dewasa & Bayi counter)
    private void showPassengerPickerDialog(final TextView passengerText) {
        Context ctx = getContext();
        if (ctx == null) return;

        View dialogView = LayoutInflater.from(ctx).inflate(R.layout.dialog_select_passenger, null);

        TextView btnClosePassenger = dialogView.findViewById(R.id.btnClosePassenger);
        TextView btnMinusAdult = dialogView.findViewById(R.id.btnMinusAdult);
        final TextView tvCountAdult = dialogView.findViewById(R.id.tvCountAdult);
        TextView btnPlusAdult = dialogView.findViewById(R.id.btnPlusAdult);

        TextView btnMinusInfant = dialogView.findViewById(R.id.btnMinusInfant);
        final TextView tvCountInfant = dialogView.findViewById(R.id.tvCountInfant);
        TextView btnPlusInfant = dialogView.findViewById(R.id.btnPlusInfant);

        Button btnSavePassenger = dialogView.findViewById(R.id.btnSavePassenger);

        if (tvCountAdult != null) tvCountAdult.setText(String.format(Locale.US, "%02d", adultCount));
        if (tvCountInfant != null) tvCountInfant.setText(String.format(Locale.US, "%02d", infantCount));

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        if (btnClosePassenger != null) {
            btnClosePassenger.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        }

        // Adult counters
        if (btnMinusAdult != null && tvCountAdult != null) {
            btnMinusAdult.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (adultCount > 1) {
                        adultCount--;
                        tvCountAdult.setText(String.format(Locale.US, "%02d", adultCount));
                    }
                }
            });
        }

        if (btnPlusAdult != null && tvCountAdult != null) {
            btnPlusAdult.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (adultCount < 9) {
                        adultCount++;
                        tvCountAdult.setText(String.format(Locale.US, "%02d", adultCount));
                    }
                }
            });
        }

        // Infant counters
        if (btnMinusInfant != null && tvCountInfant != null) {
            btnMinusInfant.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (infantCount > 0) {
                        infantCount--;
                        tvCountInfant.setText(String.format(Locale.US, "%02d", infantCount));
                    }
                }
            });
        }

        if (btnPlusInfant != null && tvCountInfant != null) {
            btnPlusInfant.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (infantCount < adultCount) {
                        infantCount++;
                        tvCountInfant.setText(String.format(Locale.US, "%02d", infantCount));
                    } else {
                        Toast.makeText(getContext(), "Jumlah bayi tidak boleh melebihi penumpang dewasa", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        // Save
        if (btnSavePassenger != null) {
            btnSavePassenger.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String formattedText = String.format(Locale.US, "%02d Dewasa, %02d Bayi", adultCount, infantCount);
                    passengerText.setText(formattedText);
                    dialog.dismiss();
                }
            });
        }

        dialog.show();
    }

    // Interactive Full-Screen Search Screen
    private void showLocationSearchDialog(final String titleHint, final TextView targetTextView, final boolean isOrigin) {
        final Context ctx = getContext();
        if (ctx == null) return;

        View dialogView = LayoutInflater.from(ctx).inflate(R.layout.dialog_search_address, null);

        final EditText editSearchAddress = dialogView.findViewById(R.id.editSearchAddress);
        TextView btnCancelSearch = dialogView.findViewById(R.id.btnCancelSearch);

        final LinearLayout listSaatIni = dialogView.findViewById(R.id.listSaatIni);
        final LinearLayout listFavorit = dialogView.findViewById(R.id.listFavorit);
        final View sectionHeaderSaatIni = dialogView.findViewById(R.id.sectionHeaderSaatIni);
        final View sectionHeaderFavorit = dialogView.findViewById(R.id.sectionHeaderFavorit);

        if (editSearchAddress != null) {
            editSearchAddress.setHint(isOrigin ? "Masukan Kota Asal" : "Masukan Kota Tujuan");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        if (btnCancelSearch != null) {
            btnCancelSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        }

        // Section 1: Saat ini
        final String[][] saatIniData = {
                {"Terminal Pulo Gebang, Jakarta → Bandung", "Rute Terakhir", "Rute", "history"},
                {"Terminal Pulo Gebang, Jakarta", "Terminal Utama", isOrigin ? "Naik di" : "Turun di", "history"},
                {"Bandung", "Kota Tujuan", isOrigin ? "Naik di" : "Turun di", "history"}
        };

        // Section 2: Titik naik/turun favorit
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

        populateSearchList(ctx, listSaatIni, saatIniData, targetTextView, dialog);
        populateSearchList(ctx, listFavorit, favoritData, targetTextView, dialog);

        if (editSearchAddress != null) {
            editSearchAddress.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String query = s.toString().toLowerCase().trim();
                    filterSearchList(ctx, listSaatIni, saatIniData, query, targetTextView, dialog);
                    filterSearchList(ctx, listFavorit, favoritData, query, targetTextView, dialog);

                    if (query.isEmpty()) {
                        if (sectionHeaderSaatIni != null) sectionHeaderSaatIni.setVisibility(View.VISIBLE);
                        if (sectionHeaderFavorit != null) sectionHeaderFavorit.setVisibility(View.VISIBLE);
                    } else {
                        if (sectionHeaderSaatIni != null) sectionHeaderSaatIni.setVisibility(View.GONE);
                        if (sectionHeaderFavorit != null) sectionHeaderFavorit.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        dialog.show();
    }

    private void populateSearchList(Context ctx, LinearLayout container, String[][] items, final TextView targetTextView, final AlertDialog dialog) {
        filterSearchList(ctx, container, items, "", targetTextView, dialog);
    }

    private void filterSearchList(Context ctx, LinearLayout container, String[][] items, String query, final TextView targetTextView, final AlertDialog dialog) {
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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String selectedTitle = itemData[0];
                    if (selectedTitle.contains("→")) {
                        selectedTitle = selectedTitle.split("→")[0].trim();
                    }
                    targetTextView.setText(selectedTitle);
                    dialog.dismiss();
                }
            });

            container.addView(itemView);
        }
    }

    private void showCustomDatePicker(final TextView targetTextView) {
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
                        targetTextView.setText(sdf.format(selectedCalendar.getTime()));
                    }
                },
                year, month, day
        );
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentActivity) {
            myContext = (FragmentActivity) context;
        }
    }

    private void showDepartureSlotBottomSheet(final String fromCity, final String toCity, final String travelDate, final boolean isRoundTrip, final String returnDate) {
        if (getContext() == null) return;

        final BottomSheetDialog sheetDialog =
                new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme);
        View sheetView = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_departure_slots, null);
        sheetDialog.setContentView(sheetView);

        TextView tvSheetSubtitle = sheetView.findViewById(R.id.tvSheetSubtitle);
        if (tvSheetSubtitle != null) {
            tvSheetSubtitle.setText("1496 Shuttle ditemukan (" + fromCity + " -> " + toCity + ")");
        }

        View.OnClickListener slotClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sheetDialog.dismiss();
                String slotTime = "Semua Slot";
                int id = v.getId();
                if (id == R.id.slotMorning) {
                    slotTime = "06:00 - 12:00";
                } else if (id == R.id.slotAfternoon) {
                    slotTime = "15:00 - 18:00";
                } else if (id == R.id.slotEvening) {
                    slotTime = "18:00 - 21:00";
                } else if (id == R.id.slotNight) {
                    slotTime = "21:00 - 00:00";
                }
                proceedToBooking(fromCity, toCity, travelDate, isRoundTrip, returnDate, slotTime);
            }
        };

        View btnClose = sheetView.findViewById(R.id.btnCloseSheet);
        if (btnClose != null) {
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sheetDialog.dismiss();
                }
            });
        }

        View slot0 = sheetView.findViewById(R.id.slotMorning);
        View slot1 = sheetView.findViewById(R.id.slotAfternoon);
        View slot2 = sheetView.findViewById(R.id.slotEvening);
        View slot3 = sheetView.findViewById(R.id.slotNight);
        View btnAll = sheetView.findViewById(R.id.btnSeeAllShuttles);

        if (slot0 != null) slot0.setOnClickListener(slotClickListener);
        if (slot1 != null) slot1.setOnClickListener(slotClickListener);
        if (slot2 != null) slot2.setOnClickListener(slotClickListener);
        if (slot3 != null) slot3.setOnClickListener(slotClickListener);
        if (btnAll != null) btnAll.setOnClickListener(slotClickListener);

        sheetDialog.show();
    }

    private void proceedToBooking(String from, String to, String date, boolean isRoundTrip, String returnDate, String timeSlot) {
        Bundle bundle = new Bundle();
        bundle.putString("from", from);
        bundle.putString("to", to);
        bundle.putString("date", date);
        bundle.putBoolean("is_round_trip", isRoundTrip);
        bundle.putString("return_date", returnDate);
        bundle.putString("time_slot", timeSlot);
        bundle.putInt("passenger_count", adultCount);
        bundle.putInt("infant_count", infantCount);

        if (getActivity() instanceof welcomePage) {
            welcomePage wp = (welcomePage) getActivity();
            wp.openBookingSubScreen(bundle);
        } else if (getActivity() != null) {
            dashboard_booking bookingFragment = new dashboard_booking();
            bookingFragment.setArguments(bundle);

            getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit)
                    .replace(R.id.fragmant_container, bookingFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadHeaderProfileImage();
    }

    private void loadHeaderProfileImage() {
        if (headerProfileImageView == null || getContext() == null) return;
        sessionmanager sm = new sessionmanager(requireContext());
        String currentUsername = sm.getuserdetail();

        if (currentUsername != null && !currentUsername.equals("Guest user")) {
            File file = new File(requireContext().getFilesDir(), "profile_" + currentUsername + ".jpg");
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                if (bitmap != null) {
                    headerProfileImageView.setImageBitmap(bitmap);
                    headerProfileImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    return;
                }
            }
        }
        headerProfileImageView.setImageResource(R.drawable.ic_default_whatsapp_profile);
        headerProfileImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
    }
}
