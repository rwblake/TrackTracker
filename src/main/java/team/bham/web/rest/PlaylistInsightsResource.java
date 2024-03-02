package team.bham.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Transactional
public class PlaylistInsightsResource {

    private final Logger log = LoggerFactory.getLogger(PlaylistInsightsResource.class);

    @PostMapping("/playlist-insights")
    public ResponseEntity<String> createPlaylist(@Valid @RequestBody String url) throws URISyntaxException {
        // Do something
        return ResponseEntity.created(new URI("/api/playlist-insights")).body(url);
    }
}
