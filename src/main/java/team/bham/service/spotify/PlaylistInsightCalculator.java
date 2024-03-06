package team.bham.service.spotify;

import java.util.*;
import java.util.stream.Collectors;
import se.michaelthelin.spotify.model_objects.specification.*;
import team.bham.domain.Artist;
import team.bham.domain.Playlist;
import team.bham.domain.Song;

public class PlaylistInsightCalculator {

    /** Calculates and returns an array containing playlist's happiest, most energetic, most and least anomalous tracks (in this order)  */
    public static SimpleSong[] selectHighlightedSongs(Playlist playlist, AllFeaturesStats mean) {
        Song[] selectedSongs = { null, null, null, null };

        double maxValence = -1; // Highest recorded valence of a song.
        double maxEnergy = -1; // Highest recorded energy of a song.
        double minDist = 999; // Minimum recorded distance from the mean of all songs.
        double maxDist = -1; // Maximum recorded distance from the mean of all songs.

        double currentDist = 0; // Tracks current song's distance from the mean of all songs.
        double currentValence = 0; // Tracks current song's valence.
        double currentEnergy = 0; // Tracks current song's energy.

        for (Song song : playlist.getSongs().toArray(new Song[0])) {
            currentValence = song.getValence();
            if (currentValence > maxValence) {
                maxValence = currentValence;
                selectedSongs[0] = song;
            }
            currentEnergy = song.getEnergy();
            if (currentEnergy > maxEnergy) {
                maxEnergy = currentEnergy;
                selectedSongs[1] = song;
            }

            currentDist = mean.distanceFromMean(song);
            if (currentDist < minDist) {
                minDist = currentDist;
                selectedSongs[2] = song;
            }
            if (currentDist > maxDist) {
                maxDist = currentDist;
                selectedSongs[3] = song;
            }
        }

        // Convert to HTTP-sendable simple song
        SimpleSong[] simpleSongs = { null, null, null, null };
        Song current;
        for (int i = 0; i < 4; i++) {
            current = selectedSongs[i];
            simpleSongs[i] = new SimpleSong(current.getSpotifyID(), current.getName(), current.getImageURL());
        }

        return simpleSongs;
    }

    /** Returns an array of mappings of artist IDs mapped to the proportion of tracks they're on*/
    private static YearSongCountMap[] calculateYears(Playlist playlist) {
        // Dictionary map to store years with the corresponding number of song occurrences
        Map<String, Integer> yearsAndCounts = new HashMap<String, Integer>();

        for (Song song : playlist.getSongs().toArray(new Song[0])) {
            String year = song.getReleaseDate().toString().substring(0, 3) + "0s";
            if (yearsAndCounts.containsKey(year)) {
                yearsAndCounts.replace(year, yearsAndCounts.get(year) + 1);
            } else {
                yearsAndCounts.put(year, 1);
            }
        }

        // Sorts the year-count map by year value
        List<Map.Entry<String, Integer>> yearsAndCountsList = yearsAndCounts
            .entrySet()
            .stream()
            .sorted(Comparator.comparing(Map.Entry::getKey))
            .collect(Collectors.toList());

        // Convert Java hashmap to our data type, for JSON conversion
        YearSongCountMap[] map = new YearSongCountMap[yearsAndCounts.size()];
        int index = 0;
        for (Map.Entry<String, Integer> entry : yearsAndCountsList) {
            map[index] = new YearSongCountMap(entry.getKey(), entry.getValue());
            index++;
        }

        return map;
    }

    /** Returns an array of mappings of artist IDs mapped to the proportion of tracks they're on*/
    private static ArtistProportionMap[] calculateArtistProportions(Playlist playlist) {
        // Dictionary map to store years with the corresponding number of song occurrences
        Map<Artist, Integer> artistsAndCounts = new HashMap<>();

        for (Song song : playlist.getSongs().toArray(new Song[0])) {
            // Each track has multiple artists
            Artist[] artists = song.getArtists().toArray(new Artist[0]);

            // Iterate through all artists and update their dictionary entries accordingly
            for (Artist artist : artists) {
                if (artistsAndCounts.containsKey(artist)) {
                    artistsAndCounts.replace(artist, artistsAndCounts.get(artist) + 1);
                } else {
                    artistsAndCounts.put(artist, 1);
                }
            }
        }

        // sort by count for the pie chart
        List<Map.Entry<Artist, Integer>> artistAndCountsList = artistsAndCounts
            .entrySet()
            .stream()
            .sorted(Comparator.comparing(Map.Entry::getValue))
            .collect(Collectors.toList());

        // Convert Java hashmap to our data type, for JSON conversion
        // We also need to divide by the total number of artists here
        ArtistProportionMap[] map = new ArtistProportionMap[artistsAndCounts.size()];
        int index = 0;
        for (Map.Entry<Artist, Integer> entry : artistAndCountsList) {
            map[index] =
                new ArtistProportionMap(
                    entry.getKey().getSpotifyID(),
                    entry.getKey().getName(),
                    entry.getKey().getImageURL(),
                    entry.getValue()
                );
            index++;
        }

        return map;
    }

    /** Selects the relevant averages from a playlist to be shown on the frontend*/
    private static float[] getRelevantAverages(AllFeaturesStats mean) {
        return new float[] { mean.valence.average, mean.energy.average, mean.acousticness.average, mean.danceability.average };
    }

    /** Calculates playlist Insights, given a list of tracks and their respective audio features*/
    public static PlaylistInsightsHTTPResponse getInsights(Playlist playlist) {
        AllFeaturesStats stats = new AllFeaturesStats(playlist);

        // Calculate stats
        SimpleSong[] highlightedIDs = selectHighlightedSongs(playlist, stats);
        float[] averages = getRelevantAverages(stats);
        YearSongCountMap[] yearSongs = calculateYears(playlist);
        ArtistProportionMap[] artistProportions = calculateArtistProportions(playlist);

        // Package it
        return new PlaylistInsightsHTTPResponse(
            playlist.getName(),
            playlist.getImageURL(),
            highlightedIDs,
            averages,
            yearSongs,
            artistProportions
        );
    }

    /** Stores statistical features of each audio feature, for normalising the distances between them*/
    public static class AudioFeatureStats {

        public float average;
        public float min;
        public float max;

        public AudioFeatureStats(float average, float min, float max) {
            this.average = average;
            this.min = min;
            this.max = max;
        }
    }

    /** Used to store statistical analysis of all audio features for a playlist
     * This is deeply unpleasant code, but it does get the job done
     */
    public static class AllFeaturesStats {

        // AudioFeatureStats for each feature.
        // Averages are initialised to 0 and then calculated in the constructor method.
        public AudioFeatureStats acousticness = new AudioFeatureStats(0, 0, 1);
        public AudioFeatureStats danceability = new AudioFeatureStats(0, 0, 1);
        public AudioFeatureStats energy = new AudioFeatureStats(0, 0, 1);
        public AudioFeatureStats instrumentalness = new AudioFeatureStats(0, 0, 1);
        public AudioFeatureStats key = new AudioFeatureStats(0, 0, 11);
        public AudioFeatureStats liveness = new AudioFeatureStats(0, 0, 1);
        public AudioFeatureStats loudness = new AudioFeatureStats(0, 0, 1);
        public AudioFeatureStats mode = new AudioFeatureStats(0, 0, 1);
        public AudioFeatureStats speechiness = new AudioFeatureStats(0, 0, 1);
        public AudioFeatureStats tempo = new AudioFeatureStats(0, 999, -1);
        public AudioFeatureStats timeSignature = new AudioFeatureStats(0, 3, 7);
        public AudioFeatureStats valence = new AudioFeatureStats(0, 0, 1);

        public AllFeaturesStats(Playlist playlist) {
            // We calculate the mean (average) as the sum of all songs' features, divided by the count
            for (Song current : playlist.getSongs().toArray(new Song[0])) {
                acousticness.average += current.getAcousticness();
                danceability.average += current.getDanceability();
                energy.average += current.getEnergy();
                instrumentalness.average += current.getInstrumentalness();
                key.average += current.getMusicalKey();
                liveness.average += current.getLiveness();
                loudness.average += current.getLoudness();
                mode.average += current.getMode() ? 1 : 0;
                speechiness.average += current.getSpeechiness();
                timeSignature.average += current.getTimeSignature();
                valence.average += current.getValence();

                // Tempo is the only feature with variable bounds, so this needs to be handled
                float currentTempo = current.getTempo();
                tempo.average += currentTempo;
                if (currentTempo > tempo.max) {
                    tempo.max = currentTempo;
                }
                if (currentTempo < tempo.min) {
                    tempo.min = currentTempo;
                }
            }

            int length = playlist.getSongs().size();

            // Divide all by the number of items

            acousticness.average /= length;
            danceability.average /= length;
            energy.average /= length;
            instrumentalness.average /= length;
            key.average /= length;
            liveness.average /= length;
            loudness.average /= length;
            mode.average /= length;
            tempo.average /= length;
            timeSignature.average /= length;
            valence.average /= length;
        }

        /** Calculate a weighted distance between a track's audio features and the mean*/
        public double distanceFromMean(Song song) {
            return (
                weightedFeatureDistance(acousticness, song.getAcousticness(), 1) +
                weightedFeatureDistance(danceability, song.getDanceability(), 0.1f) +
                weightedFeatureDistance(energy, song.getEnergy(), 1) +
                weightedFeatureDistance(instrumentalness, song.getInstrumentalness(), 0.5f) +
                weightedFeatureDistance(key, song.getMusicalKey(), 0.1f) +
                weightedFeatureDistance(liveness, song.getLiveness(), 0.3f) +
                weightedFeatureDistance(loudness, song.getLoudness(), 0.3f) +
                weightedFeatureDistance(mode, song.getMode() ? 1 : 0, 0.3f) +
                weightedFeatureDistance(speechiness, song.getSpeechiness(), 0.1f) +
                weightedFeatureDistance(tempo, song.getTempo(), 0.1f) +
                weightedFeatureDistance(timeSignature, song.getTimeSignature(), 0.1f) +
                weightedFeatureDistance(valence, song.getValence(), 0.1f)
            );
        }

        /** Calculate a weighted distance between a track's audio feature and the mean, using derived stats */
        public double weightedFeatureDistance(AudioFeatureStats featureStats, float trackFeature, float weight) {
            return Math.abs((trackFeature - featureStats.average) / (featureStats.max) - (featureStats.min)) * weight;
        }
    }
}
