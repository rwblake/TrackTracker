package team.bham.service.spotify;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import team.bham.domain.*;

public class MyInsightCalculator {

    private static String firstCharToUpper(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public static <K> void incrementIfPresent(Map<K, Integer> map, K key) {
        int currentValue = map.getOrDefault(key, 0);
        map.put(key, currentValue + 1);
    }

    /** Generic method to create a List of HashMaps of inferred types K and V */
    public static <K, V> List<HashMap<K, V>> initialiseCounter(int size) {
        // create empty ArrayList
        List<HashMap<K, V>> counterSplitByTimeFrame = new ArrayList<>(size);
        // populate with HashMaps of the inferred type
        for (int i = 0; i < size; i++) counterSplitByTimeFrame.add(new HashMap<>());
        // return populated list
        return counterSplitByTimeFrame;
    }

    /** Generic method to sort a List of HashMaps of inferred type K and Integer */
    public static <K> List<List<Map.Entry<K, Integer>>> sortCounter(List<HashMap<K, Integer>> counterSplitByTimeFrame) {
        return counterSplitByTimeFrame
            .stream()
            .map(eachRow -> eachRow.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toList()))
            .collect(Collectors.toList());
    }

    /** Extract listening insights from a list of streams
     * @param streams the list of streams
     * @return an object containing counter objects which can be used to extract useful data for the frontend graphs.*/
    public static StreamInsightsResponse getInsights(List<Stream> streams) {
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
    //    /** Adds missing years to the map*/
    //    private static MyInsightsGraphData calculateGraphData(List<Stream> streams) {
    //        // Dictionary mappings for each counted element of data.
    //        // Converted to our data types later.
    //
    //        ArrayList<ArrayList<HashMap>> allData = new ArrayList<>();
    //
    //        HashMap<String, Integer> decadesAndCountsWeek = new HashMap<>();
    //        HashMap<String, Integer> decadesAndCountsMonth = new HashMap<>();
    //        HashMap<String, Integer> decadesAndCountsYear = new HashMap<>();
    //        HashMap<String, Integer> decadesAndCountsAllTime = new HashMap<>();
    //
    ////        // Initialize each row
    ////        for (int i = 0; i < 4; i++) {
    ////            ArrayList<HashMap> row = new ArrayList<>();
    ////
    ////            // Initialize each element in the row
    ////            for (int j = 0; j < 6; j++) {
    ////                row.add(new HashMap<String, Integer>()); // You can add any default value here
    ////            }
    ////
    ////            // Add the row to the 2D ArrayList
    ////            allData.add(row);
    ////        }
    //
    //        allData.set(0, new ArrayList<>(6)).add(decadesAndCountsWeek);
    //        allData.get(1).add(decadesAndCountsMonth);
    //        allData.get(2).add(decadesAndCountsYear);
    //        allData.get(3).add(decadesAndCountsAllTime);
    //
    //        Map<String, Integer> songsAndCountsWeek = new HashMap<>();
    //        allData.get(0).add((HashMap) songsAndCountsWeek);
    //        Map<String, Integer> songsAndCountsMonth = new HashMap<>();
    //        allData.get(1).add((HashMap) songsAndCountsMonth);
    //        Map<String, Integer> songsAndCountsYear = new HashMap<>();
    //        allData.get(2).add((HashMap) songsAndCountsYear);
    //        Map<String, Integer> songsAndCountsAllTime = new HashMap<>();
    //        allData.get(3).add((HashMap) songsAndCountsAllTime);
    //
    //        Map<Artist, Integer> artistsAndCountsWeek = new HashMap<>();
    //        Map<Artist, Integer> artistsAndCountsMonth = new HashMap<>();
    //        Map<Artist, Integer> artistsAndCountsYear = new HashMap<>();
    //        Map<Artist, Integer> artistsAndCountsAllTime = new HashMap<>();
    //        allData.get(0).add((HashMap) artistsAndCountsWeek);
    //        allData.get(1).add((HashMap) artistsAndCountsMonth);
    //        allData.get(2).add((HashMap) artistsAndCountsYear);
    //        allData.get(3).add((HashMap) artistsAndCountsAllTime);
    //
    //        Map<Genre, Integer> genresAndCountsWeek = new HashMap<>();
    //        Map<Genre, Integer> genresAndCountsMonth = new HashMap<>();
    //        Map<Genre, Integer> genresAndCountsYear = new HashMap<>();
    //        Map<Genre, Integer> genresAndCountsAllTime = new HashMap<>();
    //        allData.get(0).add((HashMap) genresAndCountsWeek);
    //        allData.get(1).add((HashMap) genresAndCountsMonth);
    //        allData.get(2).add((HashMap) genresAndCountsYear);
    //        allData.get(3).add((HashMap) genresAndCountsAllTime);
    //
    //        Map<Album, Integer> albumsAndCountsWeek = new HashMap<>();
    //        Map<Album, Integer> albumsAndCountsMonth = new HashMap<>();
    //        Map<Album, Integer> albumsAndCountsYear = new HashMap<>();
    //        Map<Album, Integer> albumsAndCountsAllTime = new HashMap<>();
    //        allData.get(0).add((HashMap) albumsAndCountsWeek);
    //        allData.get(1).add((HashMap) albumsAndCountsMonth);
    //        allData.get(2).add((HashMap) albumsAndCountsYear);
    //        allData.get(3).add((HashMap) albumsAndCountsAllTime);
    //
    //        Map<Instant, ZonedDateTime> timeAndCountsWeek = new HashMap<>();
    //        Map<Instant, ZonedDateTime> timeAndCountsMonth = new HashMap<>();
    //        Map<Instant, ZonedDateTime> timeAndCountsYear = new HashMap<>();
    //        Map<Instant, ZonedDateTime> timeAndCountsAllTime = new HashMap<>();
    //        allData.get(0).add((HashMap) timeAndCountsWeek);
    //        allData.get(1).add((HashMap) timeAndCountsMonth);
    //        allData.get(2).add((HashMap) timeAndCountsYear);
    //        allData.get(3).add((HashMap) timeAndCountsAllTime);
    //
    //        String[] yearBounds = { "9999", "0000" };
    //        Instant current = Instant.now();
    //        Instant weekAgo = current.minus(Period.ofWeeks(1));
    //        Instant monthAgo = current.minus(Period.ofMonths(1));
    //        Instant yearAgo = current.minus(Period.ofYears(1));
    //
    //        for (Stream stream : streams) {
    //            String year = stream.getSong().getReleaseDate().toString().substring(0, 4);
    //            //get the songs played this week
    //            if (stream.getPlayedAt().isBefore(weekAgo)) {
    //                // Each track has multiple artists
    //                for (int i = 0; i < 3; i++) {
    //                    //decade
    //                    incrementIfPresent(allData.get(i).get(0), year.substring(0, 3) + "0s"); // get decade from year
    //
    //                    //song
    //                    incrementIfPresent(allData.get(i).get(1), stream.getSong());
    //                    //album
    //                    incrementIfPresent(allData.get(i).get(4), stream.getSong().getAlbum());
    //                    // Iterate through all artists and update their dictionary entries accordingly
    //                    for (Artist artist : stream.getSong().getArtists()) {
    //                        incrementIfPresent(allData.get(i).get(2), artist);
    //                        for (Genre genre : artist.getGenres()) {
    //                            incrementIfPresent(allData.get(i).get(3), genre);
    //                        }
    //                    }
    //                }
    //            } else if (stream.getPlayedAt().isBefore(monthAgo)) {
    //                for (int i = 1; i < 3; i++) {
    //                    incrementIfPresent(allData.get(i).get(1), year.substring(0, 3) + "0s"); // get decade from year
    //
    //                    incrementIfPresent(allData.get(i).get(0), stream.getSong());
    //                    incrementIfPresent(allData.get(i).get(4), stream.getSong().getAlbum());
    //                    // Iterate through all artists and update their dictionary entries accordingly
    //                    for (Artist artist : stream.getSong().getArtists()) {
    //                        incrementIfPresent(allData.get(i).get(2), artist);
    //                        for (Genre genre : artist.getGenres()) {
    //                            incrementIfPresent(allData.get(i).get(3), genre);
    //                        }
    //                    }
    //                }
    //            } else if (stream.getPlayedAt().isBefore(yearAgo)) {
    //                for (int i = 2; i < 3; i++) {
    //                    incrementIfPresent(allData.get(i).get(1), year.substring(0, 3) + "0s"); // get decade from year
    //
    //                    incrementIfPresent(allData.get(i).get(0), stream.getSong());
    //                    incrementIfPresent(allData.get(i).get(4), stream.getSong().getAlbum());
    //                    // Iterate through all artists and update their dictionary entries accordingly
    //                    for (Artist artist : stream.getSong().getArtists()) {
    //                        incrementIfPresent(allData.get(i).get(2), artist);
    //                        for (Genre genre : artist.getGenres()) {
    //                            incrementIfPresent(allData.get(i).get(3), genre);
    //                        }
    //                    }
    //                }
    //            } else {
    //                incrementIfPresent(allData.get(3).get(1), year.substring(0, 3) + "0s"); // get decade from year
    //
    //                incrementIfPresent(allData.get(3).get(0), stream.getSong());
    //                incrementIfPresent(allData.get(3).get(4), stream.getSong().getAlbum());
    //                // Iterate through all artists and update their dictionary entries accordingly
    //                for (Artist artist : stream.getSong().getArtists()) {
    //                    incrementIfPresent(allData.get(3).get(2), artist);
    //                    for (Genre genre : artist.getGenres()) {
    //                        incrementIfPresent(allData.get(3).get(3), genre);
    //                    }
    //                }
    //            }
    //        }
    //
    //        // Sorting the various maps
    //
    //        List<Map.Entry<String, Integer>> songsAndCountsWeekList = (List<Map.Entry<String, Integer>>) allData
    //            .get(0)
    //            .get(1)
    //            .entrySet()
    //            .stream()
    //            .sorted(Map.Entry.comparingByValue())
    //            .collect(Collectors.toList());
    //
    //        List<Map.Entry<String, Integer>> decadesAndCountsWeekList = (List<Map.Entry<String, Integer>>) allData
    //            .get(0)
    //            .get(0)
    //            .entrySet()
    //            .stream()
    //            .sorted(Map.Entry.comparingByValue())
    //            .collect(Collectors.toList());
    //
    //        List<Map.Entry<Artist, Integer>> artistsAndCountsWeekList = (List<Map.Entry<Artist, Integer>>) allData
    //            .get(0)
    //            .get(2)
    //            .entrySet()
    //            .stream()
    //            .sorted(Map.Entry.comparingByValue())
    //            .collect(Collectors.toList());
    //
    //        List<Map.Entry<Genre, Integer>> genresAndCountsWeekList = (List<Map.Entry<Genre, Integer>>) allData
    //            .get(0)
    //            .get(3)
    //            .entrySet()
    //            .stream()
    //            .sorted(Map.Entry.comparingByValue())
    //            .collect(Collectors.toList());
    //
    //        List<Map.Entry<Album, Integer>> albumsAndCountsWeekList = (List<Map.Entry<Album, Integer>>) allData
    //            .get(0)
    //            .get(4)
    //            .entrySet()
    //            .stream()
    //            .sorted(Map.Entry.comparingByValue())
    //            .collect(Collectors.toList());
    //
    //        List<Map.Entry<Instant, ZonedDateTime>> timesAndCountsWeekList = (List<Map.Entry<Instant, ZonedDateTime>>) allData
    //            .get(0)
    //            .get(5)
    //            .entrySet()
    //            .stream()
    //            .sorted(Map.Entry.comparingByValue())
    //            .collect(Collectors.toList());
    //
    //        List<Map.Entry<String, Integer>> songsAndCountsMonthList = (List<Map.Entry<String, Integer>>) allData
    //            .get(1)
    //            .get(1)
    //            .entrySet()
    //            .stream()
    //            .sorted(Map.Entry.comparingByValue())
    //            .collect(Collectors.toList());
    //
    //        List<Map.Entry<String, Integer>> decadesAndCountsMonthList = (List<Map.Entry<String, Integer>>) allData
    //            .get(1)
    //            .get(0)
    //            .entrySet()
    //            .stream()
    //            .sorted(Map.Entry.comparingByValue())
    //            .collect(Collectors.toList());
    //
    //        List<Map.Entry<Artist, Integer>> artistsAndCountsMonthList = (List<Map.Entry<Artist, Integer>>) allData
    //            .get(1)
    //            .get(2)
    //            .entrySet()
    //            .stream()
    //            .sorted(Map.Entry.comparingByValue())
    //            .collect(Collectors.toList());
    //
    //        List<Map.Entry<Genre, Integer>> genresAndCountsMonthList = (List<Map.Entry<Genre, Integer>>) allData
    //            .get(1)
    //            .get(3)
    //            .entrySet()
    //            .stream()
    //            .sorted(Map.Entry.comparingByValue())
    //            .collect(Collectors.toList());
    //
    //        List<Map.Entry<Album, Integer>> albumsAndCountsMonthList = (List<Map.Entry<Album, Integer>>) allData
    //            .get(1)
    //            .get(4)
    //            .entrySet()
    //            .stream()
    //            .sorted(Map.Entry.comparingByValue())
    //            .collect(Collectors.toList());
    //
    //        List<Map.Entry<Instant, ZonedDateTime>> timesAndCountsMonthList = (List<Map.Entry<Instant, ZonedDateTime>>) allData
    //            .get(1)
    //            .get(5)
    //            .entrySet()
    //            .stream()
    //            .sorted(Map.Entry.comparingByValue())
    //            .collect(Collectors.toList());
    //
    //        List<Map.Entry<String, Integer>> songsAndCountsYearList = (List<Map.Entry<String, Integer>>) allData
    //            .get(2)
    //            .get(1)
    //            .entrySet()
    //            .stream()
    //            .sorted(Map.Entry.comparingByValue())
    //            .collect(Collectors.toList());
    //
    //        List<Map.Entry<String, Integer>> decadesAndCountsYearList = (List<Map.Entry<String, Integer>>) allData
    //            .get(2)
    //            .get(0)
    //            .entrySet()
    //            .stream()
    //            .sorted(Map.Entry.comparingByValue())
    //            .collect(Collectors.toList());
    //
    //        List<Map.Entry<Artist, Integer>> artistsAndCountsYearList = (List<Map.Entry<Artist, Integer>>) allData
    //            .get(2)
    //            .get(2)
    //            .entrySet()
    //            .stream()
    //            .sorted(Map.Entry.comparingByValue())
    //            .collect(Collectors.toList());
    //
    //        List<Map.Entry<Genre, Integer>> genresAndCountsYearList = (List<Map.Entry<Genre, Integer>>) allData
    //            .get(2)
    //            .get(3)
    //            .entrySet()
    //            .stream()
    //            .sorted(Map.Entry.comparingByValue())
    //            .collect(Collectors.toList());
    //
    //        List<Map.Entry<Album, Integer>> albumsAndCountsYearList = (List<Map.Entry<Album, Integer>>) allData
    //            .get(2)
    //            .get(4)
    //            .entrySet()
    //            .stream()
    //            .sorted(Map.Entry.comparingByValue())
    //            .collect(Collectors.toList());
    //
    //        List<Map.Entry<Instant, ZonedDateTime>> timesAndCountsYearList = (List<Map.Entry<Instant, ZonedDateTime>>) allData
    //            .get(2)
    //            .get(5)
    //            .entrySet()
    //            .stream()
    //            .sorted(Map.Entry.comparingByValue())
    //            .collect(Collectors.toList());
    //
    //        List<Map.Entry<String, Integer>> songsAndCountsAllTimeList = (List<Map.Entry<String, Integer>>) allData
    //            .get(3)
    //            .get(1)
    //            .entrySet()
    //            .stream()
    //            .sorted(Map.Entry.comparingByValue())
    //            .collect(Collectors.toList());
    //
    //        List<Map.Entry<String, Integer>> decadesAndCountsAllTimeList = (List<Map.Entry<String, Integer>>) allData
    //            .get(3)
    //            .get(0)
    //            .entrySet()
    //            .stream()
    //            .sorted(Map.Entry.comparingByValue())
    //            .collect(Collectors.toList());
    //
    //        List<Map.Entry<Artist, Integer>> artistsAndCountsAllTimeList = (List<Map.Entry<Artist, Integer>>) allData
    //            .get(3)
    //            .get(2)
    //            .entrySet()
    //            .stream()
    //            .sorted(Map.Entry.comparingByValue())
    //            .collect(Collectors.toList());
    //
    //        List<Map.Entry<Genre, Integer>> genresAndCountsAllTimeList = (List<Map.Entry<Genre, Integer>>) allData
    //            .get(3)
    //            .get(3)
    //            .entrySet()
    //            .stream()
    //            .sorted(Map.Entry.comparingByValue())
    //            .collect(Collectors.toList());
    //
    //        List<Map.Entry<Album, Integer>> albumsAndCountsAllTimeList = (List<Map.Entry<Album, Integer>>) allData
    //            .get(3)
    //            .get(4)
    //            .entrySet()
    //            .stream()
    //            .sorted(Map.Entry.comparingByValue())
    //            .collect(Collectors.toList());
    //
    //        List<Map.Entry<Instant, ZonedDateTime>> timesAndCountsAllTimeList = (List<Map.Entry<Instant, ZonedDateTime>>) allData
    //            .get(3)
    //            .get(5)
    //            .entrySet()
    //            .stream()
    //            .sorted(Map.Entry.comparingByValue())
    //            .collect(Collectors.toList());
    //
    //        // Convert Java hashmap to our data type, for JSON conversion
    //        // We also need to divide by the total number of artists here
    //        SongCountMapInsights[] songMapsWeek = new SongCountMapInsights[songsAndCountsWeek.size()];
    //        SongCountMapInsights[] songMapsMonth = new SongCountMapInsights[songsAndCountsMonth.size()];
    //        SongCountMapInsights[] songMapsYear = new SongCountMapInsights[songsAndCountsYear.size()];
    //        SongCountMapInsights[] songMapsAllTime = new SongCountMapInsights[songsAndCountsAllTime.size()];
    //
    //        YearSongCountMapInsights[] decadeMapsWeek = new YearSongCountMapInsights[decadesAndCountsWeek.size()];
    //        YearSongCountMapInsights[] decadeMapsMonth = new YearSongCountMapInsights[decadesAndCountsMonth.size()];
    //        YearSongCountMapInsights[] decadeMapsYear = new YearSongCountMapInsights[decadesAndCountsYear.size()];
    //        YearSongCountMapInsights[] decadeMapsAllTime = new YearSongCountMapInsights[decadesAndCountsAllTime.size()];
    //
    //        ArtistSongCountMapInsights[] artistMapsWeek = new ArtistSongCountMapInsights[artistsAndCountsWeek.size()];
    //        ArtistSongCountMapInsights[] artistMapsMonth = new ArtistSongCountMapInsights[artistsAndCountsMonth.size()];
    //        ArtistSongCountMapInsights[] artistMapsYear = new ArtistSongCountMapInsights[artistsAndCountsYear.size()];
    //        ArtistSongCountMapInsights[] artistMapsAllTime = new ArtistSongCountMapInsights[artistsAndCountsAllTime.size()];
    //
    //        GenreSongCountMapInsights[] genreMapsWeek = new GenreSongCountMapInsights[genresAndCountsWeek.size()];
    //        GenreSongCountMapInsights[] genreMapsMonth = new GenreSongCountMapInsights[genresAndCountsMonth.size()];
    //        GenreSongCountMapInsights[] genreMapsYear = new GenreSongCountMapInsights[genresAndCountsYear.size()];
    //        GenreSongCountMapInsights[] genreMapsAllTime = new GenreSongCountMapInsights[genresAndCountsAllTime.size()];
    //
    //        TimeListenedCountMapInsights[] timeMapsWeek = new TimeListenedCountMapInsights[timeAndCountsWeek.size()];
    //        TimeListenedCountMapInsights[] timeMapsMonth = new TimeListenedCountMapInsights[timeAndCountsMonth.size()];
    //        TimeListenedCountMapInsights[] timeMapsYear = new TimeListenedCountMapInsights[timeAndCountsYear.size()];
    //        TimeListenedCountMapInsights[] timeMapsAllTime = new TimeListenedCountMapInsights[timeAndCountsAllTime.size()];
    //
    //        AlbumSongCountMapInsights[] albumMapsWeek = new AlbumSongCountMapInsights[albumsAndCountsWeek.size()];
    //        AlbumSongCountMapInsights[] albumMapsMonth = new AlbumSongCountMapInsights[albumsAndCountsMonth.size()];
    //        AlbumSongCountMapInsights[] albumMapsYear = new AlbumSongCountMapInsights[albumsAndCountsYear.size()];
    //        AlbumSongCountMapInsights[] albumMapsAllTime = new AlbumSongCountMapInsights[albumsAndCountsAllTime.size()];
    //
    //        int index = 0;
    //        for (Map.Entry<String, Integer> entry : songsAndCountsWeekList) {
    //            songMapsWeek[index] = new SongCountMapInsights(entry.getKey(), entry.getValue());
    //            index++;
    //        }
    //        index = 0;
    //        for (Map.Entry<String, Integer> entry : songsAndCountsMonthList) {
    //            songMapsMonth[index] = new SongCountMapInsights(entry.getKey(), entry.getValue());
    //            index++;
    //        }
    //        index = 0;
    //        for (Map.Entry<String, Integer> entry : songsAndCountsYearList) {
    //            songMapsYear[index] = new SongCountMapInsights(entry.getKey(), entry.getValue());
    //            index++;
    //        }
    //        index = 0;
    //        for (Map.Entry<String, Integer> entry : songsAndCountsAllTimeList) {
    //            songMapsAllTime[index] = new SongCountMapInsights(entry.getKey(), entry.getValue());
    //            index++;
    //        }
    //
    //        index = 0;
    //        for (Map.Entry<String, Integer> entry : decadesAndCountsWeekList) {
    //            decadeMapsWeek[index] = new YearSongCountMapInsights(entry.getKey(), entry.getValue());
    //            index++;
    //        }
    //        index = 0;
    //        for (Map.Entry<String, Integer> entry : decadesAndCountsMonthList) {
    //            decadeMapsMonth[index] = new YearSongCountMapInsights(entry.getKey(), entry.getValue());
    //            index++;
    //        }
    //        index = 0;
    //        for (Map.Entry<String, Integer> entry : decadesAndCountsYearList) {
    //            decadeMapsYear[index] = new YearSongCountMapInsights(entry.getKey(), entry.getValue());
    //            index++;
    //        }
    //        index = 0;
    //        for (Map.Entry<String, Integer> entry : decadesAndCountsAllTimeList) {
    //            decadeMapsAllTime[index] = new YearSongCountMapInsights(entry.getKey(), entry.getValue());
    //            index++;
    //        }
    //
    //        index = 0;
    //        for (Map.Entry<Artist, Integer> entry : artistsAndCountsWeekList) {
    //            artistMapsWeek[index] = new ArtistSongCountMapInsights(entry.getKey().getName(), entry.getValue());
    //            index++;
    //        }
    //        index = 0;
    //        for (Map.Entry<Artist, Integer> entry : artistsAndCountsMonthList) {
    //            artistMapsMonth[index] = new ArtistSongCountMapInsights(entry.getKey().getName(), entry.getValue());
    //            index++;
    //        }
    //        index = 0;
    //        for (Map.Entry<Artist, Integer> entry : artistsAndCountsYearList) {
    //            artistMapsYear[index] = new ArtistSongCountMapInsights(entry.getKey().getName(), entry.getValue());
    //            index++;
    //        }
    //        index = 0;
    //        for (Map.Entry<Artist, Integer> entry : artistsAndCountsAllTimeList) {
    //            artistMapsAllTime[index] = new ArtistSongCountMapInsights(entry.getKey().getName(), entry.getValue());
    //            index++;
    //        }
    //
    //        index = 0;
    //        for (Map.Entry<Genre, Integer> entry : genresAndCountsWeekList) {
    //            genreMapsWeek[index] = new GenreSongCountMapInsights(firstCharToUpper(entry.getKey().getName()), entry.getValue());
    //            index++;
    //        }
    //        index = 0;
    //        for (Map.Entry<Genre, Integer> entry : genresAndCountsMonthList) {
    //            genreMapsMonth[index] = new GenreSongCountMapInsights(firstCharToUpper(entry.getKey().getName()), entry.getValue());
    //            index++;
    //        }
    //        index = 0;
    //        for (Map.Entry<Genre, Integer> entry : genresAndCountsYearList) {
    //            genreMapsYear[index] = new GenreSongCountMapInsights(firstCharToUpper(entry.getKey().getName()), entry.getValue());
    //            index++;
    //        }
    //        index = 0;
    //        for (Map.Entry<Genre, Integer> entry : genresAndCountsAllTimeList) {
    //            genreMapsAllTime[index] = new GenreSongCountMapInsights(firstCharToUpper(entry.getKey().getName()), entry.getValue());
    //            index++;
    //        }
    //
    //        index = 0;
    //        for (Map.Entry<Album, Integer> entry : albumsAndCountsWeekList) {
    //            albumMapsWeek[index] = new AlbumSongCountMapInsights(firstCharToUpper(entry.getKey().getName()), entry.getValue());
    //            index++;
    //        }
    //        index = 0;
    //        for (Map.Entry<Album, Integer> entry : albumsAndCountsMonthList) {
    //            albumMapsMonth[index] = new AlbumSongCountMapInsights(firstCharToUpper(entry.getKey().getName()), entry.getValue());
    //            index++;
    //        }
    //        index = 0;
    //        for (Map.Entry<Album, Integer> entry : albumsAndCountsYearList) {
    //            albumMapsYear[index] = new AlbumSongCountMapInsights(firstCharToUpper(entry.getKey().getName()), entry.getValue());
    //            index++;
    //        }
    //        index = 0;
    //        for (Map.Entry<Album, Integer> entry : albumsAndCountsAllTimeList) {
    //            albumMapsAllTime[index] = new AlbumSongCountMapInsights(firstCharToUpper(entry.getKey().getName()), entry.getValue());
    //            index++;
    //        }
    //
    //        index = 0;
    //        for (Map.Entry<Instant, ZonedDateTime> entry : timesAndCountsWeekList) {
    //            timeMapsWeek[index] = new TimeListenedCountMapInsights(entry.getKey(), entry.getValue());
    //            index++;
    //        }
    //        index = 0;
    //        for (Map.Entry<Instant, ZonedDateTime> entry : timesAndCountsMonthList) {
    //            timeMapsMonth[index] = new TimeListenedCountMapInsights(entry.getKey(), entry.getValue());
    //            index++;
    //        }
    //        index = 0;
    //        for (Map.Entry<Instant, ZonedDateTime> entry : timesAndCountsYearList) {
    //            timeMapsYear[index] = new TimeListenedCountMapInsights(entry.getKey(), entry.getValue());
    //            index++;
    //        }
    //        index = 0;
    //        for (Map.Entry<Instant, ZonedDateTime> entry : timesAndCountsAllTimeList) {
    //            timeMapsAllTime[index] = new TimeListenedCountMapInsights(entry.getKey(), entry.getValue());
    //            index++;
    //        }
    //
    //        return new MyInsightsGraphData(
    //            decadeMapsWeek,
    //            decadeMapsMonth,
    //            decadeMapsYear,
    //            decadeMapsAllTime,
    //            songMapsWeek,
    //            songMapsMonth,
    //            songMapsYear,
    //            songMapsAllTime,
    //            artistMapsWeek,
    //            artistMapsMonth,
    //            artistMapsYear,
    //            artistMapsAllTime,
    //            genreMapsWeek,
    //            genreMapsMonth,
    //            genreMapsYear,
    //            genreMapsAllTime,
    //            albumMapsWeek,
    //            albumMapsMonth,
    //            albumMapsYear,
    //            albumMapsAllTime,
    //            timeMapsWeek,
    //            timeMapsMonth,
    //            timeMapsYear,
    //            timeMapsAllTime
    //        );
    //    }

    //    public static MyInsightsHTTPResponse getInsights(List<Stream> streams) {
    //        return calculateGraphData_Chris(streams);
    //    }
}
