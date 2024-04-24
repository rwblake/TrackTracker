package team.bham.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.bham.domain.AppUser;
import team.bham.repository.AppUserRepository;
import team.bham.repository.PlaylistRepository;
import team.bham.service.account.AppUserService;
import team.bham.service.account.NoAppUserException;
import tech.jhipster.web.util.HeaderUtil;

@RestController
@RequestMapping("/api")
public class CurrentUserResource {

    private final Logger log = LoggerFactory.getLogger(PlaylistResource.class);
    private final AppUserService appUserService;
    private final AppUserResource appUserResource;

    public CurrentUserResource(AppUserService appUserService, AppUserResource appUserResource) {
        this.appUserService = appUserService;
        this.appUserResource = appUserResource;
    }

    @DeleteMapping("/current-user")
    public ResponseEntity<Void> deleteCurrentUser() {
        log.debug("REST request to delete current user");
        AppUser current;

        try {
            current = appUserService.getCurrentAppUser();
        } catch (NoAppUserException e) {
            return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
        }

        return appUserResource.deleteAppUser(current.getId());
    }
}
