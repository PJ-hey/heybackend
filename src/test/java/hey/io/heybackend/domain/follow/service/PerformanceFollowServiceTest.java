package hey.io.heybackend.domain.follow.service;


import hey.io.heybackend.common.config.QuerydslConfig;
import hey.io.heybackend.common.exception.BusinessException;
import hey.io.heybackend.common.response.SliceResponse;
import hey.io.heybackend.domain.artist.domain.ArtistEntity;
import hey.io.heybackend.domain.artist.dto.ArtistListResponse;
import hey.io.heybackend.domain.artist.repository.ArtistRepository;
import hey.io.heybackend.domain.follow.domain.ArtistFollow;
import hey.io.heybackend.domain.follow.domain.PerformanceFollow;
import hey.io.heybackend.domain.follow.dto.FollowResponse;
import hey.io.heybackend.domain.follow.repository.FollowArtistRepository;
import hey.io.heybackend.domain.follow.repository.FollowPerformanceRepository;
import hey.io.heybackend.domain.performance.domain.Performance;
import hey.io.heybackend.domain.performance.domain.enums.PerformanceStatus;
import hey.io.heybackend.domain.performance.dto.PerformanceResponse;
import hey.io.heybackend.domain.performance.repository.PerformanceRepository;
import hey.io.heybackend.domain.user.domain.SocialCode;
import hey.io.heybackend.domain.user.domain.User;
import hey.io.heybackend.domain.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(value = QuerydslConfig.class)
class PerformanceFollowServiceTest {

    private FollowService followService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private FollowPerformanceRepository followPerformanceRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private FollowArtistRepository followArtistRepository;

    @BeforeEach
    void init() {
        followService = new FollowService(userRepository, performanceRepository, followPerformanceRepository, artistRepository, followArtistRepository);
    }

    @AfterEach
    void deleteAll() {
        userRepository.deleteAll();;
        performanceRepository.deleteAll();
        followPerformanceRepository.deleteAll();
        artistRepository.deleteAll();
        followArtistRepository.deleteAll();;
    }

    @Test
    @DisplayName("followPerformance - 성공")
    void followPerformance_success() {
        // given
        User user = User.create("email", SocialCode.GOOGLE);
        userRepository.save(user);
        Performance performance1 = createPerformance("1");
        performanceRepository.save(performance1);

        // when
        FollowResponse result = followService.followPerformance(user.getUserId(), performance1.getId());

        // then
        assertEquals(result.getId(), performance1.getId());
        assertEquals(result.getMessage(), "Follow Success");
    }

    @Test
    @DisplayName("follow - 유저를 찾을 수 없습니다.")
    void follow_userNotFound() {
        // given
        User user = User.create("email", SocialCode.GOOGLE);
        userRepository.save(user);
        Performance performance1 = createPerformance("1");
        performanceRepository.save(performance1);

        // when
        Throwable throwable = catchThrowable(() -> followService.followPerformance(2L, performance1.getId()));

        // then
        Assertions.assertThat(throwable).isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("follow - 공연을 찾을 수 없습니다.")
    void follow_performanceNotFound() {
        // given
        User user = User.create("email", SocialCode.GOOGLE);
        userRepository.save(user);
        Performance performance1 = createPerformance("1");
        performanceRepository.save(performance1);

        // when
        Throwable throwable = catchThrowable(() -> followService.followPerformance(user.getUserId(), "2"));

        // then
        Assertions.assertThat(throwable).isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("follow - 이미 팔로우한 공연입니다.")
    void follow_alreadyFollowedPerformance() {
        // given
        User user = User.create("email", SocialCode.GOOGLE);
        userRepository.save(user);
        Performance performance1 = createPerformance("1");
        performanceRepository.save(performance1);
        PerformanceFollow performanceFollow = PerformanceFollow.of(user, performance1);
        followPerformanceRepository.save(performanceFollow);

        // when
        Throwable throwable = catchThrowable(() -> followService.followPerformance(user.getUserId(), performance1.getId()));

        // then
        Assertions.assertThat(throwable).isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("getFollow - 성공")
    void getFollowPerformances_success() {
        // given
        User user = User.create("email", SocialCode.GOOGLE);
        userRepository.save(user);
        Performance performance1 = createPerformance("1");
        performanceRepository.save(performance1);
        PerformanceFollow performanceFollow = PerformanceFollow.of(user, performance1);
        followPerformanceRepository.save(performanceFollow);

        // when
        SliceResponse<PerformanceResponse> result = followService.getFollowPerformances(user.getUserId(), 20, 0, Sort.Direction.DESC);

        // then
        List<PerformanceResponse> contents = result.getContent();
        Assertions.assertThat(contents).hasSize(1);
        Assertions.assertThat(contents).extracting("id").containsExactly(performance1.getId());
    }

    @Test
    @DisplayName("getFollow - 유저를 찾을 수 없습니다.")
    void getFollowPerformances_userNotFound() {
        // given
        User user = User.create("email", SocialCode.GOOGLE);
        userRepository.save(user);
        Performance performance1 = createPerformance("1");
        performanceRepository.save(performance1);
        PerformanceFollow performanceFollow = PerformanceFollow.of(user, performance1);
        followPerformanceRepository.save(performanceFollow);

        // when
        Throwable throwable = catchThrowable(() -> followService.getFollowPerformances(2L, 20, 0, Sort.Direction.DESC));

        // then
        Assertions.assertThat(throwable).isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("deletePerformances - 성공")
    void deletePerformances_success() {
        // given
        User user = User.create("email", SocialCode.GOOGLE);
        userRepository.save(user);
        Performance performance1 = createPerformance("1");
        performanceRepository.save(performance1);
        PerformanceFollow performanceFollow = PerformanceFollow.of(user, performance1);
        followPerformanceRepository.save(performanceFollow);

        // when
        assertDoesNotThrow(() -> followService.deleteFollowPerformances(user.getUserId(), List.of("1")));
    }

    @Test
    @DisplayName("deletePerformances - 유저를 찾을 수 없습니다.")
    void deletePerformances_userNotFound() {
        // given
        User user = User.create("email", SocialCode.GOOGLE);
        userRepository.save(user);
        Performance performance1 = createPerformance("1");
        performanceRepository.save(performance1);
        PerformanceFollow performanceFollow = PerformanceFollow.of(user, performance1);
        followPerformanceRepository.save(performanceFollow);

        /// when
        Throwable throwable = catchThrowable(() -> followService.deleteFollowPerformances(2L, List.of("1")));

        // then
        Assertions.assertThat(throwable).isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("followArtist - 성공")
    void followArtist_success() {
        // given
        User user = User.create("email", SocialCode.GOOGLE);
        userRepository.save(user);

        ArtistEntity artist = ArtistEntity.of("artistId", "name", "image", Arrays.asList("K-POP"));
        artistRepository.save(artist);


        // when
        FollowResponse result = followService.followArtist(user.getUserId(), artist.getId());

        // then
        assertEquals(result.getId(), artist.getId());
        assertEquals(result.getMessage(), "Follow Success");
    }

    @Test
    @DisplayName("follow - 유저를 찾을 수 없습니다.")
    void artist_userNotFound() {
        // given
        User user = User.create("email", SocialCode.GOOGLE);
        userRepository.save(user);
        ArtistEntity artist = ArtistEntity.of("artistId", "name", "image", Arrays.asList("K-POP"));
        artistRepository.save(artist);

        // when
        Throwable throwable = catchThrowable(() -> followService.followArtist(2L, artist.getId()));

        // then
        Assertions.assertThat(throwable).isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("follow - 아티스트를 찾을 수 없습니다.")
    void follow_artistNotFound() {
        // given
        User user = User.create("email", SocialCode.GOOGLE);
        userRepository.save(user);
        ArtistEntity artist = ArtistEntity.of("artistId", "name", "image", Arrays.asList("K-POP"));
        artistRepository.save(artist);


        // when
        Throwable throwable = catchThrowable(() -> followService.followArtist(user.getUserId(), "2"));

        // then
        Assertions.assertThat(throwable).isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("follow - 이미 팔로우한 아티스트입니다.")
    void follow_alreadyFollowedArtist() {
        // given
        User user = User.create("email", SocialCode.GOOGLE);
        userRepository.save(user);
        ArtistEntity artist = ArtistEntity.of("artistId", "name", "image", Arrays.asList("K-POP"));
        artistRepository.save(artist);
        ArtistFollow artistFollow = ArtistFollow.of(user, artist);
        followArtistRepository.save(artistFollow);

        // when
        Throwable throwable = catchThrowable(() -> followService.followPerformance(user.getUserId(), artist.getId()));

        // then
        Assertions.assertThat(throwable).isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("getFollowArtists - 성공")
    void getFollowArtists_success() {
        // given
        User user = User.create("email", SocialCode.GOOGLE);
        userRepository.save(user);
        ArtistEntity artist = ArtistEntity.of("artistId", "name", "image", Arrays.asList("K-POP"));
        artistRepository.save(artist);
        ArtistFollow artistFollow = ArtistFollow.of(user, artist);
        followArtistRepository.save(artistFollow);

        // when
        SliceResponse<ArtistListResponse> result = followService.getFollowArtists(user.getUserId(), 20, 0, Sort.Direction.DESC);

        // then
        List<ArtistListResponse> contents = result.getContent();
        Assertions.assertThat(contents).hasSize(1);
        Assertions.assertThat(contents).extracting("id").containsExactly(artist.getId());
    }

    @Test
    @DisplayName("getFollowArtists - 유저를 찾을 수 없습니다.")
    void getFollowArtists_userNotFound() {
        // given
        User user = User.create("email", SocialCode.GOOGLE);
        userRepository.save(user);
        ArtistEntity artist = ArtistEntity.of("artistId", "name", "image", Arrays.asList("K-POP"));
        artistRepository.save(artist);
        ArtistFollow artistFollow = ArtistFollow.of(user, artist);
        followArtistRepository.save(artistFollow);

        // when
        Throwable throwable = catchThrowable(() -> followService.getFollowPerformances(2L, 20, 0, Sort.Direction.DESC));

        // then
        Assertions.assertThat(throwable).isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("deleteArtists - 성공")
    void deleteArtists_success() {
        // given
        User user = User.create("email", SocialCode.GOOGLE);
        userRepository.save(user);
        ArtistEntity artist = ArtistEntity.of("artistId", "name", "image", Arrays.asList("K-POP"));
        artistRepository.save(artist);
        ArtistFollow artistFollow = ArtistFollow.of(user, artist);
        followArtistRepository.save(artistFollow);

        // when
        // then
        assertDoesNotThrow(() -> followService.deleteFollowPerformances(user.getUserId(), List.of("artistId")));
    }

    @Test
    @DisplayName("deleteArtists - 유저를 찾을 수 없습니다.")
    void deleteArtists_userNotFound() {
        // given
        User user = User.create("email", SocialCode.GOOGLE);
        userRepository.save(user);
        ArtistEntity artist = ArtistEntity.of("artistId", "name", "image", Arrays.asList("K-POP"));
        artistRepository.save(artist);
        ArtistFollow artistFollow = ArtistFollow.of(user, artist);
        followArtistRepository.save(artistFollow);

        /// when
        Throwable throwable = catchThrowable(() -> followService.deleteFollowPerformances(2L, List.of("artistId")));

        // then
        Assertions.assertThat(throwable).isInstanceOf(BusinessException.class);
    }

    private Performance createPerformance(String id) {
        return Performance.builder()
                .id(id)
                .title("title")
                .status(PerformanceStatus.ONGOING)
                .build();
    }

}