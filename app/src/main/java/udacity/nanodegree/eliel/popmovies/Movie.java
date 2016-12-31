package udacity.nanodegree.eliel.popmovies;

/**
 * Created by eliel on 12/30/2016.
 */

public class Movie {

    private String name;
    private String thumbnailUrl;

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
}
