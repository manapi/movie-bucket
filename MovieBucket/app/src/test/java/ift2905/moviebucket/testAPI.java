package ift2905.moviebucket;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.MovieDb;

import static org.junit.Assert.*;
import static org.slf4j.LoggerFactory.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class testAPI {

    final String API_KEY = "93928f442ab5ac81f8c03b874f78fb94";

    @Test
    public void testValidApiKey() throws Exception {

        TmdbMovies movies = new TmdbApi(API_KEY).getMovies();
        MovieDb movie = movies.getMovie(5353, "en");
        assertEquals(movie.getId(), 5353);
    }
}