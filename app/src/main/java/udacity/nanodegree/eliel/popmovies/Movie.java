package udacity.nanodegree.eliel.popmovies;

/**
 * Created by eliel on 12/30/2016.
 */

public class Movie {

    public static String EXTRA_NAME = "movie_name";
    public static String EXTRA_THUMBNAIL_URL = "movie_thumbnail_url";
    public static String EXTRA_OVERVIEW = "movie_overview";
    public static String EXTRA_RATING = "movie_rating";
    public static String EXTRA_RELEASE_DATE = "movie_release_date";

    private String name;
    private String thumbnailUrl;
    private String overview;
    private double rating;
    private String releaseDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setPosterPath(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
}
