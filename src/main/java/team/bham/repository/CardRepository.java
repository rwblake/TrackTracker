package team.bham.repository;

import java.util.List;
import java.util.Optional;
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

    public void deleteAllByAppUserIdAndMetricAndMetricValue(Long appUser_id, @NotNull CardType metric, @NotNull Integer metricValue);

    public Optional<Card> findOneByAppUserAndMetricAndMetricValue(AppUser appUser, CardType cardType, Integer value);
}
