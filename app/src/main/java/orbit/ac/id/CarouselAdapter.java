package orbit.ac.id;

import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class CarouselAdapter extends RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder> {

    @NonNull
    @Override
    public CarouselViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_carousel_banner, parent, false);
        return new CarouselViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarouselViewHolder holder, int position) {
        CardView cardView = (CardView) holder.itemView;
        if (position == 0) {
            cardView.setCardBackgroundColor(Color.WHITE);
            holder.imageView.setVisibility(View.VISIBLE);
            holder.videoView.setVisibility(View.GONE);
            holder.imageView.setImageResource(R.drawable.corousel1);
        } else {
            cardView.setCardBackgroundColor(Color.BLACK);
            holder.imageView.setVisibility(View.GONE);
            holder.videoView.setVisibility(View.VISIBLE);
            try {
                String videoPath = "android.resource://" + holder.itemView.getContext().getPackageName() + "/" + R.raw.corousel2;
                holder.videoView.setVideoURI(Uri.parse(videoPath));
                holder.videoView.setOnPreparedListener(mp -> {
                    mp.setLooping(true);
                    holder.videoView.start();
                });
            } catch (Exception e) {
                android.util.Log.e("CarouselAdapter", "Error playing video", e);
            }
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    static class CarouselViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        VideoView videoView;

        CarouselViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.carouselImageView);
            videoView = itemView.findViewById(R.id.carouselVideoView);
        }
    }
}
