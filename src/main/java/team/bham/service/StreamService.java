package team.bham.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.michaelthelin.spotify.model_objects.specification.AudioFeatures;
import se.michaelthelin.spotify.model_objects.specification.PlayHistory;
import team.bham.domain.AppUser;
import team.bham.domain.Song;
import team.bham.domain.Stream;
import team.bham.repository.SongRepository;
import team.bham.repository.StreamRepository;

@Service
@Transactional
public class StreamService {

    public final SongService songService;
    public final SongRepository songRepository;
    public final StreamRepository streamRepository;

    public StreamService(SongService songService, SongRepository songRepository, StreamRepository streamRepository) {
        this.songService = songService;
        this.songRepository = songRepository;
        this.streamRepository = streamRepository;
    }

    public Stream createStream(PlayHistory stream, AudioFeatures audioFeatures, AppUser appUser) {
        // Create the song (will check for duplicates)
        Song mySong = this.songService.createSong(stream.getTrack(), audioFeatures);
        this.songRepository.save(mySong);

        // Create the stream
        Stream myStream = new Stream();
        myStream.setPlayedAt(stream.getPlayedAt().toInstant());
        myStream.setAppUser(appUser); // This relationship is set to cascade, so will be set for the AppUser too
        myStream.setSong(mySong); // This relationship is set to cascade, so will be set for song too
        this.streamRepository.save(myStream);
        return myStream;
    }
}
