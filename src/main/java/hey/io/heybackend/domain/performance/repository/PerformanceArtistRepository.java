package hey.io.heybackend.domain.performance.repository;

import hey.io.heybackend.domain.performance.domain.PerformanceArtist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerformanceArtistRepository extends JpaRepository<PerformanceArtist, Long>, PerformanceArtistQueryRepository {
}
