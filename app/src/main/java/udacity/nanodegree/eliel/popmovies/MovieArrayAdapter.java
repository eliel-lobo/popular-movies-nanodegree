package udacity.nanodegree.eliel.popmovies;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import udacity.nanodegree.eliel.popmovies.util.Utils;

/**
 * Created by eliel on 12/30/2016.
 */

public class MovieArrayAdapter extends ArrayAdapter<Movie> {

    private static final String LOG_TAG = MovieArrayAdapter.class.getSimpleName();

    public MovieArrayAdapter(Context context, int resource, List<Movie> imagesUrls) {
        super(context, resource, imagesUrls);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_movie, parent, false);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.list_item_movie_imageview);

        Movie movieItem = getItem(position);
        Uri posterUri = Uri.parse("http://image.tmdb.org/t/p/w185")
                .buildUpon()
                .appendEncodedPath(movieItem.getThumbnailUrl())
                .build();

        Picasso.with(getContext())
                .load(posterUri)
                .placeholder(R.mipmap.movie_cover_placeholder)
                .error(R.mipmap.movie_cover_placeholder)
                .into(imageView);

        return convertView;
    }

}
