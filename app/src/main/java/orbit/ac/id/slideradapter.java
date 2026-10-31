package orbit.ac.id;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class slideradapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;

    public slideradapter(Context context){
        this.context = context;
    }

    public int[] slideImages = {
            R.drawable.oneclickbooking,
            R.drawable.virtualidcard,
            R.drawable.onboardbookinh
    };

    public String[] slideHeadings ={
            "One Click Booking",
            "Virtual ID Card",
            "Pemesanan Cepat"
    };

    public String[] slideDescriptions ={
            "Pesan tiket bus pilihan Anda dengan mudah dalam satu kali klik tanpa perlu mengantre.",
            "Semua tiket dan data identitas tersimpan secara aman secara digital di smartphone Anda.",
            "Layanan pemesanan tiket resmi untuk perjalanan antar kota yang nyaman dan terpercaya."
    };

    @Override
    public int getCount() {
        return slideHeadings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);

        ImageView slideImageView = view.findViewById(R.id.iv_image_icon);
        TextView slideHeading = view.findViewById(R.id.tv_heading);
        TextView slideDescription = view.findViewById(R.id.tv_description);

        if (slideImageView != null) slideImageView.setImageResource(slideImages[position]);
        if (slideHeading != null) slideHeading.setText(slideHeadings[position]);
        if (slideDescription != null) slideDescription.setText(slideDescriptions[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
