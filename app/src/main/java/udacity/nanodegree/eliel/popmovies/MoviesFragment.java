package udacity.nanodegree.eliel.popmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import udacity.nanodegree.eliel.popmovies.util.Utils;

/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesFragment extends Fragment {

    private MovieArrayAdapter movieAdapter;
    private String prefSortBy;

    public MoviesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        movieAdapter = new MovieArrayAdapter(getActivity(), R.layout.list_item_movie,
                new ArrayList<Movie>());

        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);
        gridView.setAdapter(movieAdapter);
        gridView.setOnScrollListener(new PaginatedScrollListener(10, 0));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie theMovie = movieAdapter.getItem(position);
                Intent detailIntent = new Intent(getActivity(), DetailActivity.class);

                detailIntent.putExtra(Movie.EXTRA_NAME, theMovie.getName());
                detailIntent.putExtra(Movie.EXTRA_OVERVIEW, theMovie.getOverview());
                detailIntent.putExtra(Movie.EXTRA_RATING, theMovie.getRating());
                detailIntent.putExtra(Movie.EXTRA_RELEASE_DATE, theMovie.getReleaseDate());
                detailIntent.putExtra(Movie.EXTRA_THUMBNAIL_URL, theMovie.getThumbnailUrl());

                startActivity(detailIntent);
            }
        });


        return  rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        reloadMovies();
    }

    private void reloadMovies() {

        if (!Utils.isOnline(getActivity())) {
            Toast.makeText(getActivity(), R.string.toast_no_internet, Toast.LENGTH_LONG).show();
        }

        String prefSortByNewValue = getPreferenceString(R.string.pref_sort_by_key,
                R.string.pref_sort_by_default);

        // only reload if is the first time (prefSortBy is null) or if pref value changed
        if (!prefSortByNewValue.equals(prefSortBy)) {
            prefSortBy = prefSortByNewValue;
            movieAdapter.clear();
            new FetchMoviesTask().execute("1");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private String getPreferenceString(int preferenceKey, int preferenceDefault) {

        SharedPreferences sharedPreferences = PreferenceManager.
                getDefaultSharedPreferences(getActivity());

        return sharedPreferences.getString(getString(preferenceKey), getString(preferenceDefault));
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, Movie[]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected Movie[] doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String jsonMovies;
            String currentPage = params.length > 0 ? params[0] : "1";

            try {

                Uri movies = Uri.parse("http://api.themoviedb.org/3/movie/")
                        .buildUpon()
                        .appendPath(prefSortBy)
                        .appendQueryParameter("api_key", BuildConfig.TMDB_API_KEY)
                        .appendQueryParameter("page", String.valueOf(currentPage))
                        .build();

                URL url = new URL(movies.toString());
                Log.v(LOG_TAG, "calling " + url);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    Log.v(LOG_TAG, "Didn't get any movies");
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                jsonMovies = buffer.toString();

                try {
                    return getMoviesFromJson(jsonMovies);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "Error parsing TMDb response", e);
                }

            } catch (Exception e) {
                Log.e(LOG_TAG, "Error querying movies", e);
            }

            return new Movie[0];
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            for (int i = 0; i < movies.length; i++) {
                movieAdapter.add(movies[i]);
            }
        }

        private Movie[] getMoviesFromJson(String jsonMoviesStr) throws JSONException {

            final String RESULTS = "results";
            final String POSTER_PATH = "poster_path";
            final String NAME = "original_title";
            final String OVERVIEW = "overview";
            final String RELEASE_DATE = "release_date";
            final String VOTE_AVERAGE = "vote_average";

            JSONObject jsonMovies = new JSONObject(jsonMoviesStr);
            JSONArray moviesArray = jsonMovies.getJSONArray(RESULTS);

            Movie[] toReturn = new Movie[moviesArray.length()];
            for (int i = 0; i < moviesArray.length(); i++) {
                JSONObject jsonMovieObj = moviesArray.getJSONObject(i);
                Movie movie = new Movie();

                movie.setName(jsonMovieObj.getString(NAME));
                movie.setOverview(jsonMovieObj.getString(OVERVIEW));
                movie.setPosterPath(jsonMovieObj.getString(POSTER_PATH));
                movie.setRating(jsonMovieObj.getDouble(VOTE_AVERAGE));
                movie.setReleaseDate(jsonMovieObj.getString(RELEASE_DATE));

                toReturn[i] = movie;
            }

            return toReturn;
        }
    }

    public class PaginatedScrollListener implements AbsListView.OnScrollListener {

            // The minimum amount of items to have below your current scroll position
            // before loading more.
            private int visibleThreshold = 5;
            // The current offset index of data you have loaded
            private int currentPage = 0;
            // The total number of items in the dataset after the last load
            private int previousTotalItemCount = 0;
            // True if we are still waiting for the last set of data to load.
            private boolean loading = true;
            // Sets the starting page index
            private int startingPageIndex = 1;

            public PaginatedScrollListener() {
            }

            public PaginatedScrollListener(int visibleThreshold) {
                this.visibleThreshold = visibleThreshold;
            }

            public PaginatedScrollListener(int visibleThreshold, int startPage) {
                this.visibleThreshold = visibleThreshold;
                this.startingPageIndex = startPage;
                this.currentPage = startPage;
            }

            // This happens many times a second during a scroll, so be wary of the code you place here.
            // We are given a few useful parameters to help us work out if we need to load some more data,
            // but first we check if we are waiting for the previous load to finish.
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // If the total item count is zero and the previous isn't, assume the
                // list is invalidated and should be reset back to initial state
                if (totalItemCount < previousTotalItemCount) {
                    this.currentPage = this.startingPageIndex;
                    this.previousTotalItemCount = totalItemCount;
                    if (totalItemCount == 0) {
                        this.loading = true;
                    }
                }

                // If it’s still loading, we check to see if the dataset count has
                // changed, if so we conclude it has finished loading and update the current page
                // number and total item count.
                if (loading && (totalItemCount > previousTotalItemCount)) {
                    loading = false;
                    previousTotalItemCount = totalItemCount;
                    currentPage++;
                }

                // If it isn’t currently loading, we check to see if we have breached
                // the visibleThreshold and need to reload more data.
                // If we do need to reload some more data, we execute onLoadMore to fetch the data.
                if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                    onLoadMore(currentPage + 1, totalItemCount);
                    loading = true;
                }
            }

            // Defines the process for actually loading more data based on page
            public void onLoadMore(int page, int totalItemsCount) {
                new FetchMoviesTask().execute(String.valueOf(page), prefSortBy);
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // Don't take any action on changed
            }
        }
}
