package team.bham.service.spotify;

import java.time.Instant;
import java.time.Period;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import team.bham.domain.Album;
import team.bham.domain.Artist;
import team.bham.domain.Genre;
import team.bham.domain.Stream;

public class MyInsightCalculator {

    private static String firstCharToUpper(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public static <K> void incrementIfPresent(Map<K, Integer> map, K key) {
        int currentValue = map.getOrDefault(key, 0);
        map.put(key, currentValue + 1);
    }

    /** Adds missing years to the map*/
    private static MyInsightsGraphData calculateGraphData(List<Stream> streams) {
        // Dictionary mappings for each counted element of data.
        // Converted to our data types later.

        ArrayList<ArrayList<HashMap>> allData = new ArrayList<>(5);

        Map<String, Integer> decadesAndCountsWeek = new HashMap<>();
        Map<String, Integer> decadesAndCountsMonth = new HashMap<>();
        Map<String, Integer> decadesAndCountsYear = new HashMap<>();
        Map<String, Integer> decadesAndCountsAllTime = new HashMap<>();
        allData.get(0).add((HashMap) decadesAndCountsWeek);
        allData.get(1).add((HashMap) decadesAndCountsMonth);
        allData.get(2).add((HashMap) decadesAndCountsYear);
        allData.get(3).add((HashMap) decadesAndCountsAllTime);

        Map<String, Integer> songsAndCountsWeek = new HashMap<>();
        allData.get(0).add((HashMap) songsAndCountsWeek);
        Map<String, Integer> songsAndCountsMonth = new HashMap<>();
        allData.get(1).add((HashMap) songsAndCountsMonth);
        Map<String, Integer> songsAndCountsYear = new HashMap<>();
        allData.get(2).add((HashMap) songsAndCountsYear);
        Map<String, Integer> songsAndCountsAllTime = new HashMap<>();
        allData.get(3).add((HashMap) songsAndCountsAllTime);

        Map<Artist, Integer> artistsAndCountsWeek = new HashMap<>();
        Map<Artist, Integer> artistsAndCountsMonth = new HashMap<>();
        Map<Artist, Integer> artistsAndCountsYear = new HashMap<>();
        Map<Artist, Integer> artistsAndCountsAllTime = new HashMap<>();
        allData.get(0).add((HashMap) artistsAndCountsWeek);
        allData.get(1).add((HashMap) artistsAndCountsMonth);
        allData.get(2).add((HashMap) artistsAndCountsYear);
        allData.get(3).add((HashMap) artistsAndCountsAllTime);

        Map<Genre, Integer> genresAndCountsWeek = new HashMap<>();
        Map<Genre, Integer> genresAndCountsMonth = new HashMap<>();
        Map<Genre, Integer> genresAndCountsYear = new HashMap<>();
        Map<Genre, Integer> genresAndCountsAllTime = new HashMap<>();
        allData.get(0).add((HashMap) genresAndCountsWeek);
        allData.get(1).add((HashMap) genresAndCountsMonth);
        allData.get(2).add((HashMap) genresAndCountsYear);
        allData.get(3).add((HashMap) genresAndCountsAllTime);

        Map<Album, Integer> albumsAndCountsWeek = new HashMap<>();
        Map<Album, Integer> albumsAndCountsMonth = new HashMap<>();
        Map<Album, Integer> albumsAndCountsYear = new HashMap<>();
        Map<Album, Integer> albumsAndCountsAllTime = new HashMap<>();
        allData.get(0).add((HashMap) albumsAndCountsWeek);
        allData.get(1).add((HashMap) albumsAndCountsMonth);
        allData.get(2).add((HashMap) albumsAndCountsYear);
        allData.get(3).add((HashMap) albumsAndCountsAllTime);

        Map<Instant, ZonedDateTime> timeAndCountsWeek = new HashMap<>();
        Map<Instant, ZonedDateTime> timeAndCountsMonth = new HashMap<>();
        Map<Instant, ZonedDateTime> timeAndCountsYear = new HashMap<>();
        Map<Instant, ZonedDateTime> timeAndCountsAllTime = new HashMap<>();
        allData.get(0).add((HashMap) timeAndCountsWeek);
        allData.get(1).add((HashMap) timeAndCountsMonth);
        allData.get(2).add((HashMap) timeAndCountsYear);
        allData.get(3).add((HashMap) timeAndCountsAllTime);

        String[] yearBounds = { "9999", "0000" };
        Instant current = Instant.now();
        Instant weekAgo = current.minus(Period.ofWeeks(1));
        Instant monthAgo = current.minus(Period.ofMonths(1));
        Instant yearAgo = current.minus(Period.ofYears(1));

        for (Stream stream : streams) {
            String year = stream.getSong().getReleaseDate().toString().substring(0, 4);
            //get the songs played this week
            if (stream.getPlayedAt().isBefore(weekAgo)) {
                // Each track has multiple artists
                for (int i = 0; i < 3; i++) {
                    //decade
                    incrementIfPresent(allData.get(i).get(0), year.substring(0, 3) + "0s"); // get decade from year

                    //song
                    incrementIfPresent(allData.get(i).get(1), stream.getSong());
                    //album
                    incrementIfPresent(allData.get(i).get(4), stream.getSong().getAlbum());
                    // Iterate through all artists and update their dictionary entries accordingly
                    for (Artist artist : stream.getSong().getArtists()) {
                        incrementIfPresent(allData.get(i).get(2), artist);
                        for (Genre genre : artist.getGenres()) {
                            incrementIfPresent(allData.get(i).get(3), genre);
                        }
                    }
                }
            } else if (stream.getPlayedAt().isBefore(monthAgo)) {
                for (int i = 1; i < 3; i++) {
                    incrementIfPresent(allData.get(i).get(1), year.substring(0, 3) + "0s"); // get decade from year

                    incrementIfPresent(allData.get(i).get(0), stream.getSong());
                    incrementIfPresent(allData.get(i).get(4), stream.getSong().getAlbum());
                    // Iterate through all artists and update their dictionary entries accordingly
                    for (Artist artist : stream.getSong().getArtists()) {
                        incrementIfPresent(allData.get(i).get(2), artist);
                        for (Genre genre : artist.getGenres()) {
                            incrementIfPresent(allData.get(i).get(3), genre);
                        }
                    }
                }
            } else if (stream.getPlayedAt().isBefore(yearAgo)) {
                for (int i = 2; i < 3; i++) {
                    incrementIfPresent(allData.get(i).get(1), year.substring(0, 3) + "0s"); // get decade from year

                    incrementIfPresent(allData.get(i).get(0), stream.getSong());
                    incrementIfPresent(allData.get(i).get(4), stream.getSong().getAlbum());
                    // Iterate through all artists and update their dictionary entries accordingly
                    for (Artist artist : stream.getSong().getArtists()) {
                        incrementIfPresent(allData.get(i).get(2), artist);
                        for (Genre genre : artist.getGenres()) {
                            incrementIfPresent(allData.get(i).get(3), genre);
                        }
                    }
                }
            } else {
                incrementIfPresent(allData.get(3).get(1), year.substring(0, 3) + "0s"); // get decade from year

                incrementIfPresent(allData.get(3).get(0), stream.getSong());
                incrementIfPresent(allData.get(3).get(4), stream.getSong().getAlbum());
                // Iterate through all artists and update their dictionary entries accordingly
                for (Artist artist : stream.getSong().getArtists()) {
                    incrementIfPresent(allData.get(3).get(2), artist);
                    for (Genre genre : artist.getGenres()) {
                        incrementIfPresent(allData.get(3).get(3), genre);
                    }
                }
            }
        }

        // Sorting the various maps

        List<Map.Entry<String, Integer>> songsAndCountsWeekList = (List<Map.Entry<String, Integer>>) allData
            .get(0)
            .get(1)
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue())
            .collect(Collectors.toList());

        List<Map.Entry<String, Integer>> decadesAndCountsWeekList = (List<Map.Entry<String, Integer>>) allData
            .get(0)
            .get(0)
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue())
            .collect(Collectors.toList());

        List<Map.Entry<Artist, Integer>> artistsAndCountsWeekList = (List<Map.Entry<Artist, Integer>>) allData
            .get(0)
            .get(2)
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue())
            .collect(Collectors.toList());

        List<Map.Entry<Genre, Integer>> genresAndCountsWeekList = (List<Map.Entry<Genre, Integer>>) allData
            .get(0)
            .get(3)
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue())
            .collect(Collectors.toList());

        List<Map.Entry<Album, Integer>> albumsAndCountsWeekList = (List<Map.Entry<Album, Integer>>) allData
            .get(0)
            .get(4)
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue())
            .collect(Collectors.toList());

        List<Map.Entry<Instant, ZonedDateTime>> timesAndCountsWeekList = (List<Map.Entry<Instant, ZonedDateTime>>) allData
            .get(0)
            .get(5)
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue())
            .collect(Collectors.toList());

        List<Map.Entry<String, Integer>> songsAndCountsMonthList = (List<Map.Entry<String, Integer>>) allData
            .get(1)
            .get(1)
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue())
            .collect(Collectors.toList());

        List<Map.Entry<String, Integer>> decadesAndCountsMonthList = (List<Map.Entry<String, Integer>>) allData
            .get(1)
            .get(0)
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue())
            .collect(Collectors.toList());

        List<Map.Entry<Artist, Integer>> artistsAndCountsMonthList = (List<Map.Entry<Artist, Integer>>) allData
            .get(1)
            .get(2)
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue())
            .collect(Collectors.toList());

        List<Map.Entry<Genre, Integer>> genresAndCountsMonthList = (List<Map.Entry<Genre, Integer>>) allData
            .get(1)
            .get(3)
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue())
            .collect(Collectors.toList());

        List<Map.Entry<Album, Integer>> albumsAndCountsMonthList = (List<Map.Entry<Album, Integer>>) allData
            .get(1)
            .get(4)
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue())
            .collect(Collectors.toList());

        List<Map.Entry<Instant, ZonedDateTime>> timesAndCountsMonthList = (List<Map.Entry<Instant, ZonedDateTime>>) allData
            .get(1)
            .get(5)
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue())
            .collect(Collectors.toList());

        List<Map.Entry<String, Integer>> songsAndCountsYearList = (List<Map.Entry<String, Integer>>) allData
            .get(2)
            .get(1)
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue())
            .collect(Collectors.toList());

        List<Map.Entry<String, Integer>> decadesAndCountsYearList = (List<Map.Entry<String, Integer>>) allData
            .get(2)
            .get(0)
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue())
            .collect(Collectors.toList());

        List<Map.Entry<Artist, Integer>> artistsAndCountsYearList = (List<Map.Entry<Artist, Integer>>) allData
            .get(2)
            .get(2)
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue())
            .collect(Collectors.toList());

        List<Map.Entry<Genre, Integer>> genresAndCountsYearList = (List<Map.Entry<Genre, Integer>>) allData
            .get(2)
            .get(3)
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue())
            .collect(Collectors.toList());

        List<Map.Entry<Album, Integer>> albumsAndCountsYearList = (List<Map.Entry<Album, Integer>>) allData
            .get(2)
            .get(4)
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue())
            .collect(Collectors.toList());

        List<Map.Entry<Instant, ZonedDateTime>> timesAndCountsYearList = (List<Map.Entry<Instant, ZonedDateTime>>) allData
            .get(2)
            .get(5)
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue())
            .collect(Collectors.toList());

        List<Map.Entry<String, Integer>> songsAndCountsAllTimeList = (List<Map.Entry<String, Integer>>) allData
            .get(3)
            .get(1)
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue())
            .collect(Collectors.toList());

        List<Map.Entry<String, Integer>> decadesAndCountsAllTimeList = (List<Map.Entry<String, Integer>>) allData
            .get(3)
            .get(0)
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue())
            .collect(Collectors.toList());

        List<Map.Entry<Artist, Integer>> artistsAndCountsAllTimeList = (List<Map.Entry<Artist, Integer>>) allData
            .get(3)
            .get(2)
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue())
            .collect(Collectors.toList());

        List<Map.Entry<Genre, Integer>> genresAndCountsAllTimeList = (List<Map.Entry<Genre, Integer>>) allData
            .get(3)
            .get(3)
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue())
            .collect(Collectors.toList());

        List<Map.Entry<Album, Integer>> albumsAndCountsAllTimeList = (List<Map.Entry<Album, Integer>>) allData
            .get(3)
            .get(4)
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue())
            .collect(Collectors.toList());

        List<Map.Entry<Instant, ZonedDateTime>> timesAndCountsAllTimeList = (List<Map.Entry<Instant, ZonedDateTime>>) allData
            .get(3)
            .get(5)
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue())
            .collect(Collectors.toList());

        // Convert Java hashmap to our data type, for JSON conversion
        // We also need to divide by the total number of artists here
        SongCountMapInsights[] songMapsWeek = new SongCountMapInsights[songsAndCountsWeek.size()];
        SongCountMapInsights[] songMapsMonth = new SongCountMapInsights[songsAndCountsMonth.size()];
        SongCountMapInsights[] songMapsYear = new SongCountMapInsights[songsAndCountsYear.size()];
        SongCountMapInsights[] songMapsAllTime = new SongCountMapInsights[songsAndCountsAllTime.size()];

        YearSongCountMapInsights[] decadeMapsWeek = new YearSongCountMapInsights[decadesAndCountsWeek.size()];
        YearSongCountMapInsights[] decadeMapsMonth = new YearSongCountMapInsights[decadesAndCountsMonth.size()];
        YearSongCountMapInsights[] decadeMapsYear = new YearSongCountMapInsights[decadesAndCountsYear.size()];
        YearSongCountMapInsights[] decadeMapsAllTime = new YearSongCountMapInsights[decadesAndCountsAllTime.size()];

        ArtistSongCountMapInsights[] artistMapsWeek = new ArtistSongCountMapInsights[artistsAndCountsWeek.size()];
        ArtistSongCountMapInsights[] artistMapsMonth = new ArtistSongCountMapInsights[artistsAndCountsMonth.size()];
        ArtistSongCountMapInsights[] artistMapsYear = new ArtistSongCountMapInsights[artistsAndCountsYear.size()];
        ArtistSongCountMapInsights[] artistMapsAllTime = new ArtistSongCountMapInsights[artistsAndCountsAllTime.size()];

        GenreSongCountMapInsights[] genreMapsWeek = new GenreSongCountMapInsights[genresAndCountsWeek.size()];
        GenreSongCountMapInsights[] genreMapsMonth = new GenreSongCountMapInsights[genresAndCountsMonth.size()];
        GenreSongCountMapInsights[] genreMapsYear = new GenreSongCountMapInsights[genresAndCountsYear.size()];
        GenreSongCountMapInsights[] genreMapsAllTime = new GenreSongCountMapInsights[genresAndCountsAllTime.size()];

        TimeListenedCountMapInsights[] timeMapsWeek = new TimeListenedCountMapInsights[timeAndCountsWeek.size()];
        TimeListenedCountMapInsights[] timeMapsMonth = new TimeListenedCountMapInsights[timeAndCountsMonth.size()];
        TimeListenedCountMapInsights[] timeMapsYear = new TimeListenedCountMapInsights[timeAndCountsYear.size()];
        TimeListenedCountMapInsights[] timeMapsAllTime = new TimeListenedCountMapInsights[timeAndCountsAllTime.size()];

        AlbumSongCountMapInsights[] albumMapsWeek = new AlbumSongCountMapInsights[albumsAndCountsWeek.size()];
        AlbumSongCountMapInsights[] albumMapsMonth = new AlbumSongCountMapInsights[albumsAndCountsMonth.size()];
        AlbumSongCountMapInsights[] albumMapsYear = new AlbumSongCountMapInsights[albumsAndCountsYear.size()];
        AlbumSongCountMapInsights[] albumMapsAllTime = new AlbumSongCountMapInsights[albumsAndCountsAllTime.size()];

        int index = 0;
        for (Map.Entry<String, Integer> entry : songsAndCountsWeekList) {
            songMapsWeek[index] = new SongCountMapInsights(entry.getKey(), entry.getValue());
            index++;
        }
        index = 0;
        for (Map.Entry<String, Integer> entry : songsAndCountsMonthList) {
            songMapsMonth[index] = new SongCountMapInsights(entry.getKey(), entry.getValue());
            index++;
        }
        index = 0;
        for (Map.Entry<String, Integer> entry : songsAndCountsYearList) {
            songMapsYear[index] = new SongCountMapInsights(entry.getKey(), entry.getValue());
            index++;
        }
        index = 0;
        for (Map.Entry<String, Integer> entry : songsAndCountsAllTimeList) {
            songMapsAllTime[index] = new SongCountMapInsights(entry.getKey(), entry.getValue());
            index++;
        }

        index = 0;
        for (Map.Entry<String, Integer> entry : decadesAndCountsWeekList) {
            decadeMapsWeek[index] = new YearSongCountMapInsights(entry.getKey(), entry.getValue());
            index++;
        }
        index = 0;
        for (Map.Entry<String, Integer> entry : decadesAndCountsMonthList) {
            decadeMapsMonth[index] = new YearSongCountMapInsights(entry.getKey(), entry.getValue());
            index++;
        }
        index = 0;
        for (Map.Entry<String, Integer> entry : decadesAndCountsYearList) {
            decadeMapsYear[index] = new YearSongCountMapInsights(entry.getKey(), entry.getValue());
            index++;
        }
        index = 0;
        for (Map.Entry<String, Integer> entry : decadesAndCountsAllTimeList) {
            decadeMapsAllTime[index] = new YearSongCountMapInsights(entry.getKey(), entry.getValue());
            index++;
        }

        index = 0;
        for (Map.Entry<Artist, Integer> entry : artistsAndCountsWeekList) {
            artistMapsWeek[index] = new ArtistSongCountMapInsights(entry.getKey().getName(), entry.getValue());
            index++;
        }
        index = 0;
        for (Map.Entry<Artist, Integer> entry : artistsAndCountsMonthList) {
            artistMapsMonth[index] = new ArtistSongCountMapInsights(entry.getKey().getName(), entry.getValue());
            index++;
        }
        index = 0;
        for (Map.Entry<Artist, Integer> entry : artistsAndCountsYearList) {
            artistMapsYear[index] = new ArtistSongCountMapInsights(entry.getKey().getName(), entry.getValue());
            index++;
        }
        index = 0;
        for (Map.Entry<Artist, Integer> entry : artistsAndCountsAllTimeList) {
            artistMapsAllTime[index] = new ArtistSongCountMapInsights(entry.getKey().getName(), entry.getValue());
            index++;
        }

        index = 0;
        for (Map.Entry<Genre, Integer> entry : genresAndCountsWeekList) {
            genreMapsWeek[index] = new GenreSongCountMapInsights(firstCharToUpper(entry.getKey().getName()), entry.getValue());
            index++;
        }
        index = 0;
        for (Map.Entry<Genre, Integer> entry : genresAndCountsMonthList) {
            genreMapsMonth[index] = new GenreSongCountMapInsights(firstCharToUpper(entry.getKey().getName()), entry.getValue());
            index++;
        }
        index = 0;
        for (Map.Entry<Genre, Integer> entry : genresAndCountsYearList) {
            genreMapsYear[index] = new GenreSongCountMapInsights(firstCharToUpper(entry.getKey().getName()), entry.getValue());
            index++;
        }
        index = 0;
        for (Map.Entry<Genre, Integer> entry : genresAndCountsAllTimeList) {
            genreMapsAllTime[index] = new GenreSongCountMapInsights(firstCharToUpper(entry.getKey().getName()), entry.getValue());
            index++;
        }

        index = 0;
        for (Map.Entry<Album, Integer> entry : albumsAndCountsWeekList) {
            albumMapsWeek[index] = new AlbumSongCountMapInsights(firstCharToUpper(entry.getKey().getName()), entry.getValue());
            index++;
        }
        index = 0;
        for (Map.Entry<Album, Integer> entry : albumsAndCountsMonthList) {
            albumMapsMonth[index] = new AlbumSongCountMapInsights(firstCharToUpper(entry.getKey().getName()), entry.getValue());
            index++;
        }
        index = 0;
        for (Map.Entry<Album, Integer> entry : albumsAndCountsYearList) {
            albumMapsYear[index] = new AlbumSongCountMapInsights(firstCharToUpper(entry.getKey().getName()), entry.getValue());
            index++;
        }
        index = 0;
        for (Map.Entry<Album, Integer> entry : albumsAndCountsAllTimeList) {
            albumMapsAllTime[index] = new AlbumSongCountMapInsights(firstCharToUpper(entry.getKey().getName()), entry.getValue());
            index++;
        }

        index = 0;
        for (Map.Entry<Instant, ZonedDateTime> entry : timesAndCountsWeekList) {
            timeMapsWeek[index] = new TimeListenedCountMapInsights(entry.getKey(), entry.getValue());
            index++;
        }
        index = 0;
        for (Map.Entry<Instant, ZonedDateTime> entry : timesAndCountsMonthList) {
            timeMapsMonth[index] = new TimeListenedCountMapInsights(entry.getKey(), entry.getValue());
            index++;
        }
        index = 0;
        for (Map.Entry<Instant, ZonedDateTime> entry : timesAndCountsYearList) {
            timeMapsYear[index] = new TimeListenedCountMapInsights(entry.getKey(), entry.getValue());
            index++;
        }
        index = 0;
        for (Map.Entry<Instant, ZonedDateTime> entry : timesAndCountsAllTimeList) {
            timeMapsAllTime[index] = new TimeListenedCountMapInsights(entry.getKey(), entry.getValue());
            index++;
        }

        return new MyInsightsGraphData(
            decadeMapsWeek,
            decadeMapsMonth,
            decadeMapsYear,
            decadeMapsAllTime,
            songMapsWeek,
            songMapsMonth,
            songMapsYear,
            songMapsAllTime,
            artistMapsWeek,
            artistMapsMonth,
            artistMapsYear,
            artistMapsAllTime,
            genreMapsWeek,
            genreMapsMonth,
            genreMapsYear,
            genreMapsAllTime,
            albumMapsWeek,
            albumMapsMonth,
            albumMapsYear,
            albumMapsAllTime,
            timeMapsWeek,
            timeMapsMonth,
            timeMapsYear,
            timeMapsAllTime
        );
    }

    public static MyInsightsHTTPResponse getInsights(List<Stream> streams) {
        MyInsightsGraphData graphData = calculateGraphData(streams);
        return new MyInsightsHTTPResponse(graphData);
    }
}
