package team.bham.service.spotify;

import java.io.IOException;
import java.util.*;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.model_objects.specification.AudioFeatures;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;

public class PlaylistRetriever {

    /** Given a playlist URL, return the respective Spotify ID for that playlist*/
    public static String parsePlaylistIDfromURL(String url) {
        int startIndex = url.indexOf("playlist/") + 9;
        return url.substring(startIndex, startIndex + 22);
    }

    public static String[] getIDs(List<Track> tracks) {
        String[] ids = new String[tracks.size()];
        for (int i = 0; i < tracks.size(); i++) {
            ids[i] = tracks.get(i).getId();
        }
        return ids;
    }

    // TEMPORARY AUTHENTICATION METHOD FOR TESTING PURPOSES
    /** Create SpotifyApi object using client credentials flow, and set access token*/
    public static SpotifyApi clientCredentials(String clientID, String clientSecret)
        throws IOException, ParseException, SpotifyWebApiException {
        SpotifyApi spotifyApi = SpotifyApi.builder().setClientId(clientID).setClientSecret(clientSecret).build();
        ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials().build();
        ClientCredentials clientCredentials = clientCredentialsRequest.execute();
        spotifyApi.setAccessToken(clientCredentials.getAccessToken());
        return spotifyApi;
    }

    public static List<Track> getTracks(SpotifyApi spotifyApi, String playlistID)
        throws IOException, ParseException, SpotifyWebApiException {
        ArrayList<Track> tracks = new ArrayList<Track>();

        final int limit = 50; // maximum playlist items in one request
        Paging<PlaylistTrack> tracksPager;
        int offset = 0;
        int tracksRecieved;
        int i;

        do {
            tracksPager = spotifyApi.getPlaylistsItems(playlistID).limit(limit).offset(offset).build().execute();
            tracksRecieved = tracksPager.getItems().length;
            for (i = 0; i < tracksRecieved; i++) {
                tracks.add((Track) tracksPager.getItems()[i].getTrack());
            }
            offset += limit;
        } while (tracksRecieved == limit);

        return tracks;
    }

    public static List<AudioFeatures> getAudioFeatures(SpotifyApi spotifyApi, List<Track> tracks)
        throws IOException, ParseException, SpotifyWebApiException {
        ArrayList<AudioFeatures> features = new ArrayList<AudioFeatures>();

        final int limit = 100; // maximum audio features in one request
        int offset = 0;
        String[] ids = getIDs(tracks);
        String[] requestIDs;
        int tracksRecieved = limit;
        int end;

        do {
            end = offset + limit;
            if (offset + limit >= ids.length) {
                end = ids.length;
            }
            requestIDs = Arrays.copyOfRange(ids, offset, end);
            AudioFeatures[] requestedFeatures = spotifyApi.getAudioFeaturesForSeveralTracks(requestIDs).build().execute();
            Collections.addAll(features, requestedFeatures);

            tracksRecieved = requestedFeatures.length;
            offset += limit;
        } while (tracksRecieved == limit);
        return features;
    }
}
