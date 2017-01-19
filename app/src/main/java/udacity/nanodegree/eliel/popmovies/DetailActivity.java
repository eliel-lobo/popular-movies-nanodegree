package udacity.nanodegree.eliel.popmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    public static final int THUMBNAIL_DETAIL_SIZE = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Intent theIntent = this.getIntent();
        String movieName = theIntent.getStringExtra(Movie.EXTRA_NAME);
        String movieOverview = theIntent.getStringExtra(Movie.EXTRA_OVERVIEW);
        String movieThumbnail = theIntent.getStringExtra(Movie.EXTRA_THUMBNAIL_URL);
        String movieReleaseDate = theIntent.getStringExtra(Movie.EXTRA_RELEASE_DATE);
        double movieRating = theIntent.getDoubleExtra(Movie.EXTRA_RATING, 0d);

        TextView titleTextView = (TextView) findViewById(R.id.detail_title_textview);
        titleTextView.setText(movieName);

        TextView overviewTextView = (TextView) findViewById(R.id.detail_overview_textview);
        overviewTextView.setText(movieOverview);

        TextView releasedTextView = (TextView) findViewById(R.id.detail_released_textview);
        releasedTextView.setText("Released: " + movieReleaseDate);

        TextView ratingTextView = (TextView) findViewById(R.id.detail_rating_fab);
        ratingTextView.setText(String.valueOf(movieRating));

        ImageView imageView = (ImageView) findViewById(R.id.detail_thumbnail_imageview);

        Uri posterUri = Uri.parse("http://image.tmdb.org/t/p")
                .buildUpon()
                .appendEncodedPath("w" + THUMBNAIL_DETAIL_SIZE)
                .appendEncodedPath(movieThumbnail)
                .build();

        Log.v("DETAIL", posterUri.toString());
        imageView.getWidth();

        Picasso
                .with(this)
                .load(posterUri)
//                .resize(THUMBNAIL_DETAIL_SIZE, THUMBNAIL_DETAIL_SIZE)
//                .centerCrop()
                .into(imageView);

    }
}
