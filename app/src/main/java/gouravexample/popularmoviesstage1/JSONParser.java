package gouravexample.popularmoviesstage1;

import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by GOURAV on 18-08-2016.
 */
public class JSONParser {

    public List<MovieItem> parseJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        List<MovieItem> movieItems = null;
        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("results")) {
                    movieItems = readMoviesArray(reader);
                } else {
                    reader.skipValue();
                }
            }
        } finally {
            reader.endObject();
            reader.close();
        }

        return movieItems;
    }

    public List<MovieItem> readMoviesArray(JsonReader reader) throws IOException {
        List<MovieItem> messages = new ArrayList<MovieItem>();

        reader.beginArray();
        while (reader.hasNext()) {
            messages.add(readMovieItem(reader));
        }
        reader.endArray();
        return messages;
    }

    public MovieItem readMovieItem(JsonReader reader) throws IOException {

        MovieItem movieItem = new MovieItem();

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("poster_path")) {
                movieItem.setPosterPath(reader.nextString());
            } else if (name.equals("adult")) {
                movieItem.setAdult(reader.nextBoolean());
            } else if (name.equals("overview")) {
                movieItem.setOverview(reader.nextString());
            } else if (name.equals("release_date")) {
                movieItem.setReleaseDate(reader.nextString());
            } else if (name.equals("genre_ids")) {
                movieItem.setGenreIds(readGenreArray(reader));
            } else if (name.equals("id")) {
                movieItem.setId(reader.nextInt());
            } else if (name.equals("original_title")) {
                movieItem.setOriginalTitle(reader.nextString());
            } else if (name.equals("original_language")) {
                movieItem.setOriginalLanguage(reader.nextString());
            } else if (name.equals("title")) {
                movieItem.setTitle(reader.nextString());
            } else if (name.equals("backdrop_path")) {
                movieItem.setBackDropPath(reader.nextString());
            } else if (name.equals("popularity")) {
                movieItem.setPopularity(reader.nextDouble());
            } else if (name.equals("vote_count")) {
                movieItem.setVoteCount(reader.nextInt());
            } else if (name.equals("video")) {
                movieItem.setVideo(reader.nextBoolean());
            } else if (name.equals("vote_average")) {
                movieItem.setVoteAverage(reader.nextDouble());
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return movieItem;
    }

    public int[] readGenreArray(JsonReader reader) throws IOException {

        ArrayList<Integer> genreIds = new ArrayList<>();

        reader.beginArray();
        while(reader.hasNext()){
            genreIds.add(reader.nextInt());
        }
        reader.endArray();


        int[] ints = new int[genreIds.size()];
        int i = 0;
        for (Integer n : ints) {
            ints[i++] = n;
        }

        return ints;

    }
}
