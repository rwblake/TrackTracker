package team.bham.repository;

import java.util.List;
import javax.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import team.bham.domain.AppUser;
import team.bham.domain.Card;
import team.bham.domain.enumeration.CardType;

/**
 * Spring Data JPA repository for the Card entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    public List<Card> getCardsByAppUserAndMetric(@NotNull AppUser appUser, @NotNull CardType metric);

    public void deleteByAppUserId(Long appUser_id);
}
