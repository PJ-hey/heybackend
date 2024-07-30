package hey.io.heybackend.domain.report.service;


import hey.io.heybackend.common.config.QuerydslConfig;
import hey.io.heybackend.common.exception.BusinessException;
import hey.io.heybackend.domain.artist.domain.ArtistEntity;
import hey.io.heybackend.domain.artist.repository.ArtistRepository;
import hey.io.heybackend.domain.performance.domain.Performance;
import hey.io.heybackend.domain.performance.domain.enums.PerformanceStatus;
import hey.io.heybackend.domain.performance.repository.PerformanceRepository;
import hey.io.heybackend.domain.report.dto.ReportRequest;
import hey.io.heybackend.domain.report.dto.ReportResponse;
import hey.io.heybackend.domain.report.repository.ArtistReportRepository;
import hey.io.heybackend.domain.report.repository.PerformanceReportRepository;
import hey.io.heybackend.domain.user.domain.SocialCode;
import hey.io.heybackend.domain.user.domain.User;
import hey.io.heybackend.domain.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(value = QuerydslConfig.class)
class ReportServiceTest {

    private ReportService reportService;

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PerformanceReportRepository performanceReportRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private ArtistReportRepository artistReportRepository;

    @BeforeEach
    void init() {
        reportService = new ReportService(performanceRepository, userRepository, performanceReportRepository, artistRepository, artistReportRepository);
    }

    @AfterEach
    void deleteAll() {
        performanceRepository.deleteAll();
        userRepository.deleteAll();
        performanceReportRepository.deleteAll();
        artistRepository.deleteAll();
        artistReportRepository.deleteAll();
    }

    @Test
    @DisplayName("reportPerformance - 성공")
    void reportPerformance_success() {
        // given
        User user = User.create("email", SocialCode.GOOGLE);
        userRepository.save(user);

        Performance performance1 = createPerformance("1");
        performanceRepository.save(performance1);

        ReportRequest request = new ReportRequest(Arrays.asList("공연명"), "공연명 오류");

        // when
        ReportResponse result = reportService.reportPerformance(performance1.getId(), user.getUserId(), request);

        // then
        assertEquals(result.getId(), performance1.getId());
        assertEquals(result.getUserId(), user.getUserId());

    }

    @Test
    @DisplayName("reportPerformance - 유저를 찾을 수 없습니다.")
    void reportPerformance_userNotFound() {
        // given
        User user = User.create("email", SocialCode.GOOGLE);
        userRepository.save(user);

        Performance performance1 = createPerformance("1");
        performanceRepository.save(performance1);

        ReportRequest request = new ReportRequest(Arrays.asList("공연명"), "공연명 오류");

        // when
        Throwable throwable = catchThrowable(() -> reportService.reportPerformance(performance1.getId(), 2L, request));

        // then
        assertThat(throwable).isInstanceOf(BusinessException.class);

    }

    @Test
    @DisplayName("reportPerformance - 공연을 찾을 수 없습니다.")
    void reportPerformance_performanceNotFound() {
        // given
        User user = User.create("email", SocialCode.GOOGLE);
        userRepository.save(user);

        Performance performance1 = createPerformance("1");
        performanceRepository.save(performance1);

        ReportRequest request = new ReportRequest(Arrays.asList("공연명"), "공연명 오류");

        // when
        Throwable throwable = catchThrowable(() -> reportService.reportPerformance("2", user.getUserId(), request));

        // then
        assertThat(throwable).isInstanceOf(BusinessException.class);

    }

    @Test
    @DisplayName("reportArtist - 성공")
    void reportArtist_success() {
        // given
        User user = User.create("email", SocialCode.GOOGLE);
        userRepository.save(user);

        ArtistEntity artist = ArtistEntity.of("artistId", "name", "image", Arrays.asList("K-POP"));
        artistRepository.save(artist);

        ReportRequest request = new ReportRequest(Arrays.asList("아티스트명"), "아티스트명 오류");

        // when
        ReportResponse result = reportService.reportArtist(artist.getId(), user.getUserId(), request);

        // then
        assertEquals(result.getId(), artist.getId());
        assertEquals(result.getUserId(), user.getUserId());

    }

    @Test
    @DisplayName("reportArtist - 유저를 찾을 수 없습니다.")
    void reportArtist_userNotFound() {
        // given
        User user = User.create("email", SocialCode.GOOGLE);
        userRepository.save(user);

        ArtistEntity artist = ArtistEntity.of("artistId", "name", "image", Arrays.asList("K-POP"));
        artistRepository.save(artist);

        ReportRequest request = new ReportRequest(Arrays.asList("아티스트명"), "아티스트명 오류");

        // when
        Throwable throwable = catchThrowable(() -> reportService.reportArtist(artist.getId(), 5L, request));

        // then
        assertThat(throwable).isInstanceOf(BusinessException.class);

    }

    @Test
    @DisplayName("reportArtist - 아티스트를 찾을 수 없습니다.")
    void reportArtist_performanceNotFound() {
        // given
        User user = User.create("email", SocialCode.GOOGLE);
        userRepository.save(user);

        ArtistEntity artist = ArtistEntity.of("artistId", "name", "image", Arrays.asList("K-POP"));
        artistRepository.save(artist);

        ReportRequest request = new ReportRequest(Arrays.asList("아티스트명"), "아티스트명 오류");


        // when
        Throwable throwable = catchThrowable(() -> reportService.reportArtist("2", user.getUserId(), request));

        // then
        assertThat(throwable).isInstanceOf(BusinessException.class);

    }


    private Performance createPerformance(String id) {
        return Performance.builder()
                .id(id)
                .title("title")
                .status(PerformanceStatus.ONGOING)
                .build();
    }
}