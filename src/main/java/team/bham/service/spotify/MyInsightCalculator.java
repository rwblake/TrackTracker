package team.bham.service.spotify;

import java.util.*;
import java.util.stream.Collectors;
import team.bham.domain.Artist;
import team.bham.domain.Genre;
import team.bham.domain.Playlist;
import team.bham.domain.Song;

public class MyInsightCalculator {

    private static String firstCharToUpper(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public static <K> void incrementIfPresent(Map<K, Integer> map, K key) {
        int currentValue = map.getOrDefault(key, 0);
        map.put(key, currentValue + 1);
    }

    /** Adds missing years to the map*/
    private static MyInsightsGraphData calculateGraphData(Playlist playlist) {
        // Dictionary mappings for each counted element of data.
        // Converted to our data types later.
        Map<String, Integer> songsAndCounts = new HashMap<>();
        Map<String, Integer> decadesAndCounts = new HashMap<>();
        Map<Artist, Integer> artistsAndCounts = new HashMap<>();
        Map<Genre, Integer> genresAndCounts = new HashMap<>();

        String[] yearBounds = { "9999", "0000" };

        for (Song song : playlist.getSongs().toArray(new Song[0])) {
            // Each track has multiple artists
            String year = song.getReleaseDate().toString().substring(0, 4);

            incrementIfPresent(decadesAndCounts, year.substring(0, 3) + "0s"); // get decade from year
            incrementIfPresent(songsAndCounts, song.getName());

            // Iterate through all artists and update their dictionary entries accordingly
            for (Artist artist : song.getArtists()) {
                incrementIfPresent(artistsAndCounts, artist);
                for (Genre genre : artist.getGenres()) {
                    incrementIfPresent(genresAndCounts, genre);
                }
            }
        }

        // Sorting the various maps
        List<Map.Entry<String, Integer>> songsAndCountsList = songsAndCounts
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByKey())
            .collect(Collectors.toList());

        List<Map.Entry<String, Integer>> decadesAndCountsList = decadesAndCounts
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByKey())
            .collect(Collectors.toList());

        List<Map.Entry<Artist, Integer>> artistAndCountsList = artistsAndCounts
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue())
            .collect(Collectors.toList());

        List<Map.Entry<Genre, Integer>> genreAndCountsList = genresAndCounts
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue())
            .collect(Collectors.toList());

        // Convert Java hashmap to our data type, for JSON conversion
        // We also need to divide by the total number of artists here
        SongCountMapInsights[] songMaps = new SongCountMapInsights[songsAndCounts.size()];
        YearSongCountMapInsights[] decadeMaps = new YearSongCountMapInsights[decadesAndCounts.size()];
        ArtistSongCountMapInsights[] artistMaps = new ArtistSongCountMapInsights[artistsAndCounts.size()];
        GenreSongCountMapInsights[] genreMaps = new GenreSongCountMapInsights[genresAndCounts.size()];

        int index = 0;
        for (Map.Entry<String, Integer> entry : songsAndCountsList) {
            songMaps[index] = new SongCountMapInsights(entry.getKey(), entry.getValue());
            index++;
        }
        index = 0;
        for (Map.Entry<String, Integer> entry : decadesAndCountsList) {
            decadeMaps[index] = new YearSongCountMapInsights(entry.getKey(), entry.getValue());
            index++;
        }
        index = 0;
        for (Map.Entry<Artist, Integer> entry : artistAndCountsList) {
            artistMaps[index] = new ArtistSongCountMapInsights(entry.getKey().getName(), entry.getValue());
            index++;
        }
        index = 0;
        for (Map.Entry<Genre, Integer> entry : genreAndCountsList) {
            genreMaps[index] = new GenreSongCountMapInsights(firstCharToUpper(entry.getKey().getName()), entry.getValue());
            index++;
        }

        return new MyInsightsGraphData(decadeMaps, songMaps, artistMaps, genreMaps);
    }
}
