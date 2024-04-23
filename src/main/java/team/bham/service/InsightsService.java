package team.bham.service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.bham.domain.*;
import team.bham.service.spotify.Counter;
import team.bham.service.spotify.StreamInsightsResponse;

@Service
@Transactional
public class InsightsService {

    private static String firstCharToUpper(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    private static <K> void incrementIfPresent(Map<K, Integer> map, K key) {
        int currentValue = map.getOrDefault(key, 0);
        map.put(key, currentValue + 1);
    }

    /** Generic method to create a List of HashMaps of inferred types K and V */
    private static <K, V> List<HashMap<K, V>> initialiseCounter(int size) {
        // create empty ArrayList
        List<HashMap<K, V>> counterSplitByTimeFrame = new ArrayList<>(size);
        // populate with HashMaps of the inferred type
        for (int i = 0; i < size; i++) counterSplitByTimeFrame.add(new HashMap<>());
        // return populated list
        return counterSplitByTimeFrame;
    }

    /** Generic method to sort a List of HashMaps of inferred type K and Integer */
    private static <K> List<List<Map.Entry<K, Integer>>> sortCounter(List<HashMap<K, Integer>> counterSplitByTimeFrame) {
        return counterSplitByTimeFrame
            .stream()
            .map(eachRow -> eachRow.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toList()))
            .collect(Collectors.toList());
    }

    /** Extract listening insights from a list of streams
     * @param streams the list of streams
     * @return an object containing counter objects which can be used to extract useful data for the frontend graphs.*/
    public StreamInsightsResponse getInsights(List<Stream> streams) {
        // Initialise the counter structures

        // Counts the number of times that a song appears
        List<HashMap<Song, Integer>> songCounterSplitByTimeFrame = initialiseCounter(4);

        // Counts the number of times that songs from an artist appear
        List<HashMap<Artist, Integer>> artistCounterSplitByTimeFrame = initialiseCounter(4);

        // Counts the number of times that songs from a decade appear
        List<HashMap<String, Integer>> decadeCounterSplitByTimeFrame = initialiseCounter(4);

        // Counts the number of times that a genre appears
        List<HashMap<Genre, Integer>> genreCounterSplitByTimeFrame = initialiseCounter(4);

        // Counts the number of times that an album appears
        List<HashMap<Album, Integer>> albumCounterSplitByTimeFrame = initialiseCounter(4);

        // TODO: Maybe add this later
        // Stores the total listening duration, grouped by:
        // days (for week and month graphs),
        // or months (for year and all-time graphs)
        // List<HashMap<Instant, Duration>> timeCountSplitByTimeFrame = initialiseCounter(4);

        // Establish relative time stamps
        String[] yearBounds = { "9999", "0000" };
        Instant current = Instant.now();
        Instant weekAgo = current.minus(Duration.ofDays(7));
        Instant monthAgo = current.minus(Duration.ofDays(29));
        Instant yearAgo = current.minus(Duration.ofDays(365));

        // Analyse each stream, incrementing the suitable counters
        for (Stream stream : streams) {
            // Get the year the song was released
            String releaseYear = stream.getSong().getReleaseDate().toString().substring(0, 4);

            int smallest; // Defines which time groupings (week/month/year/all-time) to increment, given a stream

            if (stream.getPlayedAt().isBefore(yearAgo)) smallest = 3; else if (stream.getPlayedAt().isBefore(monthAgo)) smallest =
                2; else if (stream.getPlayedAt().isBefore(weekAgo)) smallest = 1; else smallest = 0;

            // Update the suitable counters
            for (int i = 3; i >= smallest; i--) {
                // Increment song counter
                incrementIfPresent(songCounterSplitByTimeFrame.get(i), stream.getSong());

                // Iterate through all artists and increment their counters accordingly
                for (Artist artist : stream.getSong().getArtists()) {
                    incrementIfPresent(artistCounterSplitByTimeFrame.get(i), artist);

                    // Increment the genres (as they are linked solely to artists)
                    for (Genre genre : artist.getGenres()) {
                        incrementIfPresent(genreCounterSplitByTimeFrame.get(i), genre);
                    }
                }

                // Increment decade counters
                incrementIfPresent(decadeCounterSplitByTimeFrame.get(i), releaseYear.substring(0, 3) + "0s"); // get decade from year

                // Increment album counters
                incrementIfPresent(albumCounterSplitByTimeFrame.get(i), stream.getSong().getAlbum());
                // TODO: Maybe add this in later
                // Duration duration = stream.getSong().getDuration();
                // timeCountSplitByTimeFrame.get(i).
            }
        }

        // Sort all the values by the integer value (within hash maps)
        List<List<Map.Entry<Song, Integer>>> sortedSongCounters = sortCounter(songCounterSplitByTimeFrame);
        List<List<Map.Entry<Artist, Integer>>> sortedArtistCounters = sortCounter(artistCounterSplitByTimeFrame);
        List<List<Map.Entry<String, Integer>>> sortedDecadeCounters = sortCounter(decadeCounterSplitByTimeFrame);
        List<List<Map.Entry<Genre, Integer>>> sortedGenreCounters = sortCounter(genreCounterSplitByTimeFrame);
        List<List<Map.Entry<Album, Integer>>> sortedAlbumCounters = sortCounter(albumCounterSplitByTimeFrame);

        // Create new Counter objects given the lists
        Counter<Song> songCounter = new Counter<>(sortedSongCounters);
        Counter<Artist> artistCounter = new Counter<>(sortedArtistCounters);
        Counter<String> decadeCounter = new Counter<>(sortedDecadeCounters);
        Counter<Genre> genreCounter = new Counter<>(sortedGenreCounters);
        Counter<Album> albumCounter = new Counter<>(sortedAlbumCounters);

        // Return a new StreamInsightsResponse object, passing in the Counter objects
        return new StreamInsightsResponse(songCounter, artistCounter, decadeCounter, genreCounter, albumCounter);
    }
}
