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
public class JSONDetailParser {

    public MovieItem parseJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        return readMovieItem(reader);
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
            } else if (name.equals("trailers")) {
                movieItem.setTrailerList(readTrailerObject(reader));
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
            } else if (name.equals("status")) {
                movieItem.setStatus(reader.nextString());
            } else if (name.equals("tagline")) {
                movieItem.setTagline(reader.nextString());
            } else if (name.equals("runtime")) {
                movieItem.setRuntime(reader.nextInt());
            } else if (name.equals("revenue")) {
                movieItem.setRevenue(reader.nextInt());
            } else if (name.equals("homepage")) {
                movieItem.setHomepage(reader.nextString());
            } else if (name.equals("imdb_id")) {
                movieItem.setImdb_id(reader.nextString());
            } else if (name.equals("budget")) {
                movieItem.setBudget(reader.nextInt());
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

    public List<Trailer> readTrailerObject(JsonReader reader) throws IOException {

        List<Trailer> trailerList = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("youtube")) {
                trailerList = readTrailerArray(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return trailerList;
    }

    public List<Trailer> readTrailerArray(JsonReader reader) throws IOException {

        List<Trailer> messages = new ArrayList<Trailer>();

        reader.beginArray();
        while (reader.hasNext()) {
            messages.add(readTrailer(reader));
        }
        reader.endArray();
        return messages;
    }

    public Trailer readTrailer(JsonReader reader) throws IOException {

        Trailer trailer = new Trailer();

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("name")) {
                trailer.setName(reader.nextString());
            } else if (name.equals("size")) {
                trailer.setSize(reader.nextString());
            } else if (name.equals("source")) {
                trailer.setSource(reader.nextString());
            } else if (name.equals("type")) {
                trailer.setType(reader.nextString());
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return trailer;
    }
}
