package team.bham.service.spotify;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.*;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistsItemsRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetAudioFeaturesForSeveralTracksRequest;

/** WARNING: This is unfinished, uncommented, badly organised code that I still need to work on further.
 * I'm committing it so that other people can play around with it, but it does not integrate with the rest of the app
 * right now.
 */
public class PlaylistInsightCalculator {

    // Parameters for Spotify Web API access
    private static String clientId = ""; // Paste Client ID here
    private static String clientSecret = ""; // Paste Client Secret here
    private static final URI redirectUri = SpotifyHttpManager.makeUri("http://localhost:8080/");

    private static SpotifyApi spotifyApi = null;

    /** Given a playlist ID, builds an ArrayList of Track objects through multiple API requests and returns it*/
    private static ArrayList<Track> getPlaylistTracks(String playlistId) {
        ArrayList<Track> tracks = new ArrayList<Track>();

        final int limit = 50; // maximum playlist items in one request

        int offset = 0;
        int remaining = 0;
        boolean gotCount = false;
        boolean gotAllTracks = false;
        int pagingSize = limit;

        while (!gotAllTracks) {
            GetPlaylistsItemsRequest getRequest = spotifyApi.getPlaylistsItems(playlistId).limit(limit).offset(offset).build();
            try {
                Paging<PlaylistTrack> playlistTrackPaging = getRequest.execute();

                if (!gotCount) {
                    remaining = playlistTrackPaging.getTotal();
                    gotCount = true;
                }

                offset += limit; // pull the next batch of tracks

                if (remaining < limit) {
                    pagingSize = remaining;
                }

                for (int i = 0; i < pagingSize; i++) {
                    tracks.add((Track) playlistTrackPaging.getItems()[i].getTrack());
                }

                remaining -= limit;
                if (remaining < 0) {
                    gotAllTracks = true;
                }
            } catch (IOException | SpotifyWebApiException | ParseException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        return tracks;
    }

    /** Given an ArrayList of Tracks, returns an ArrayList of their corresponding AudioFeatures */
    public static ArrayList<AudioFeatures> getAudioFeatures(ArrayList<Track> tracks) {
        ArrayList<AudioFeatures> features = new ArrayList<AudioFeatures>();

        final int limit = 100; // maximum audio features in one request
        int offset = 0;
        boolean gotAllFeatures = false;

        while (!gotAllFeatures) {
            String[] requestIDs = trackListToIDArray(tracks, offset, limit);

            GetAudioFeaturesForSeveralTracksRequest getRequest = spotifyApi.getAudioFeaturesForSeveralTracks(requestIDs).build();

            try {
                AudioFeatures[] requestedFeatures = getRequest.execute();
                Collections.addAll(features, requestedFeatures);
                offset += limit;

                if (offset > tracks.size()) {
                    gotAllFeatures = true;
                }
            } catch (IOException | SpotifyWebApiException | ParseException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        return features;
    }

    /** Converts a portion of an ArrayList of Track objects to an array of IDs, to be passed into API requests. */
    public static String[] trackListToIDArray(ArrayList<Track> tracks, int offset, int count) {
        int remaining = tracks.size() - offset;
        if (remaining < count) {
            count = remaining;
        }

        String[] IDs = new String[count];

        for (int i = 0; i < count; i++) {
            IDs[i] = tracks.get(i + offset).getId();
        }

        return IDs;
    }

    /** Calculates and returns the indices of a playlist's happiest, most energetic, most and least anomalous songs (in this order)  */
    private static int[] selectHighlightedSongs(ArrayList<AudioFeatures> featuresList) {
        AudioFeaturesMean mean = new AudioFeaturesMean(featuresList);
        int[] songIndices = { -1, -1, -1, -1 };

        AudioFeatures current;
        double maxValence = -1; // Tracks highest recorded valence of a song.
        double maxEnergy = -1; // Tracks highest recorded energy of a song.
        double minDist = 999; // Tracks mininum recorded distance from the mean of all songs.
        double maxDist = -1; // Tracks maximum recorded distance from the mean of all songs.

        double currentDist = 0; // Tracks current song's distance from the mean of all songs.
        double currentValence = 0; // Tracks current song's valence.
        double currentEnergy = 0; // Tracks current song's energy.

        for (int i = 0; i < featuresList.size(); i++) {
            current = featuresList.get(i);

            currentValence = current.getValence();
            if (currentValence > maxValence) {
                maxValence = currentValence;
                songIndices[0] = i;
            }
            currentEnergy = current.getEnergy();
            if (currentEnergy > maxEnergy) {
                maxEnergy = currentEnergy;
                songIndices[1] = i;
            }

            currentDist = mean.distanceFromMean(current);
            if (currentDist < minDist) {
                minDist = currentDist;
                songIndices[2] = i;
            }
            if (currentDist > maxDist) {
                maxDist = currentDist;
                songIndices[3] = i;
            }
        }

        return songIndices;
    }

    /** Prints the tracks closest and furthest from the mean of a playlist, given an ID */
    private static void pullPlaylist(String playlistId) {
        ArrayList<Track> tracks = getPlaylistTracks(playlistId);
        ArrayList<AudioFeatures> audioFeaturesList = getAudioFeatures(tracks);

        int[] closestAndFurthestIndices = selectHighlightedSongs(audioFeaturesList);

        System.out.println("Highest Valence: " + tracks.get(closestAndFurthestIndices[0]).getName());
        System.out.println("Highest Energy: " + tracks.get(closestAndFurthestIndices[1]).getName());
        System.out.println("Sums Up Playlist: " + tracks.get(closestAndFurthestIndices[2]).getName());
        System.out.println("Most Anomalous: " + tracks.get(closestAndFurthestIndices[3]).getName());
    }

    // EVERYTHING BELOW THIS POINT IS TERRIBLE PLACEHOLDER AUTHORISATION CODE

    public static void authorizationCode_Sync(String code) {
        try {
            AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(code).build();
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();

            // Set access and refresh token for further "spotifyApi" object usage

            System.out.println("Tokens obtained. Paste these into their respective fields in main(), and set codeNeeded to True");
            spotifyApi.setAccessToken("ACCESS TOKEN: " + authorizationCodeCredentials.getAccessToken());
            System.out.println(authorizationCodeCredentials.getAccessToken());
            spotifyApi.setRefreshToken("REFRESH TOKEN: " + authorizationCodeCredentials.getRefreshToken());
            System.out.println(authorizationCodeCredentials.getRefreshToken());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void authorizationCodeUri_Sync() {
        AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi
            .authorizationCodeUri()
            .scope(
                "playlist-read-private,user-follow-read,user-read-playback-position,user-top-read," +
                "user-read-recently-played,user-library-read,user-read-email"
            )
            .show_dialog(true)
            .build();
        final URI uri = authorizationCodeUriRequest.execute();
        System.out.println(uri.toString());
    }

    private static void buildAPIObject() {
        spotifyApi = new SpotifyApi.Builder().setClientId(clientId).setClientSecret(clientSecret).setRedirectUri(redirectUri).build();
    }

    /** Terrible, terrible placeholder code for testing until Chris finishes his end, and the frontend is developed*/
    public static void main(String[] args) {
        // Set this to True if you don't have any tokens
        boolean codeNeeded = true;

        String[] credentials = CredentialsParser.parseCredentials();
        clientId = credentials[0];
        clientSecret = credentials[1];
        buildAPIObject();

        if (codeNeeded) {
            authorizationCodeUri_Sync();

            Scanner inp = new Scanner(System.in);
            System.out.println("Enter the URL redirected to:");
            String fullLink = inp.nextLine();

            // Extract the authorisation code from the URL, and use it to make a request for tokens
            String code = fullLink.substring(fullLink.lastIndexOf("code=") + 5);

            authorizationCode_Sync(code);
        }
        // After tokens have been acquired, paste their values in the appropriate places for future runs
        else {
            spotifyApi.setAccessToken("");
            spotifyApi.setRefreshToken("");
        }

        pullPlaylist("7bpGp7ceGVljvDzhB2YZNF"); // Paste your playlist ID here
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

    /** This is very unpleasant code, I'm deeply sorry */
    public static class AudioFeaturesMean {

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

        public AudioFeaturesMean(ArrayList<AudioFeatures> featuresList) {
            // We calculate the mean (average) as the sum of all songs' features, divided by the count
            for (AudioFeatures current : featuresList) {
                acousticness.average += current.getAcousticness();
                danceability.average += current.getDanceability();
                energy.average += current.getEnergy();
                instrumentalness.average += current.getInstrumentalness();
                key.average += current.getKey();
                liveness.average += current.getLiveness();
                loudness.average += current.getLoudness();
                mode.average += current.getMode().ordinal();
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

            int length = featuresList.size();

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
        public double distanceFromMean(AudioFeatures trackFeatures) {
            return (
                weightedFeatureDistance(acousticness, trackFeatures.getAcousticness(), 1) +
                weightedFeatureDistance(danceability, trackFeatures.getDanceability(), 0.1f) +
                weightedFeatureDistance(energy, trackFeatures.getEnergy(), 1) +
                weightedFeatureDistance(instrumentalness, trackFeatures.getInstrumentalness(), 0.5f) +
                weightedFeatureDistance(key, trackFeatures.getKey(), 0.1f) +
                weightedFeatureDistance(liveness, trackFeatures.getLiveness(), 0.3f) +
                weightedFeatureDistance(loudness, trackFeatures.getLoudness(), 0.3f) +
                weightedFeatureDistance(mode, trackFeatures.getMode().ordinal(), 0.3f) +
                weightedFeatureDistance(speechiness, trackFeatures.getSpeechiness(), 0.1f) +
                weightedFeatureDistance(tempo, trackFeatures.getTempo(), 0.1f) +
                weightedFeatureDistance(timeSignature, trackFeatures.getTimeSignature(), 0.1f) +
                weightedFeatureDistance(valence, trackFeatures.getValence(), 0.1f)
            );
        }

        /** Calculate a weighted distance between a track's audio feature and the mean, using derived stats */
        public double weightedFeatureDistance(AudioFeatureStats featureStats, float trackFeature, float weight) {
            return Math.abs((trackFeature - featureStats.average) / (featureStats.max) - (featureStats.min)) * weight;
        }
    }
}