package hey.io.heybackend.domain.artist.service;


import hey.io.heybackend.common.config.QuerydslConfig;
import hey.io.heybackend.common.exception.BusinessException;
import hey.io.heybackend.common.response.SliceResponse;
import hey.io.heybackend.domain.album.domain.AlbumEntity;
import hey.io.heybackend.domain.album.dto.AlbumResponse;
import hey.io.heybackend.domain.album.repository.AlbumRepository;
import hey.io.heybackend.domain.artist.domain.ArtistEntity;
import hey.io.heybackend.domain.artist.dto.ArtistListResponse;
import hey.io.heybackend.domain.artist.dto.ArtistResponse;
import hey.io.heybackend.domain.artist.repository.ArtistRepository;
import hey.io.heybackend.domain.performance.domain.BoxOfficeRank;
import hey.io.heybackend.domain.performance.domain.Performance;
import hey.io.heybackend.domain.performance.domain.PerformanceArtist;
import hey.io.heybackend.domain.performance.domain.enums.PerformanceStatus;
import hey.io.heybackend.domain.performance.domain.enums.TimePeriod;
import hey.io.heybackend.domain.performance.dto.PerformanceResponse;
import hey.io.heybackend.domain.performance.repository.BoxOfficeRankRepository;
import hey.io.heybackend.domain.performance.repository.PerformanceArtistRepository;
import hey.io.heybackend.domain.performance.repository.PerformanceRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(value = QuerydslConfig.class)
class ArtistServiceTest {

    private ArtistService artistService;

    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private PerformanceRepository performanceRepository;
    @Autowired
    private PerformanceArtistRepository performanceArtistRepository;
    @Autowired
    private AlbumRepository albumRepository;
    @Autowired
    private BoxOfficeRankRepository boxOfficeRankRepository;

    @BeforeEach
    void init() {
        artistService = new ArtistService(artistRepository, performanceRepository, performanceArtistRepository, albumRepository, boxOfficeRankRepository);
    }

    @AfterEach
    void deleteAll() {
        performanceArtistRepository.deleteAll();
        performanceRepository.deleteAll();
        albumRepository.deleteAll();
        boxOfficeRankRepository.deleteAll();
        artistRepository.deleteAll();
    }

    @Test
    @DisplayName("getArtist - 성공")
    void getArtist_success() {
        // given
        ArtistEntity artist = ArtistEntity.of("artistId", "name", "image", Arrays.asList("K-POP"));

        artistRepository.save(artist);

        // when
        ArtistResponse artistResponse = artistService.getArtist(artist.getId());

        // then
        assertThat(artistResponse.getId()).isEqualTo(artist.getId());
        assertThat(artistResponse.getArtistName()).isEqualTo(artist.getArtistName());
        assertThat(artistResponse.getArtistImage()).isEqualTo(artist.getArtistImage());
    }

    @Test
    @DisplayName("getArtist - 아티스트를 찾을 수 없습니다.")
    void getArtist_artistNotFound() {
        // given
        // when
        Throwable throwable = catchThrowable(() -> artistService.getArtist("artistId"));

        // then
        assertThat(throwable).isInstanceOf(BusinessException.class);

    }

    @Test
    @DisplayName("searchArtists - 성공")
    void searchArtists() {
        // given
        ArtistEntity artist = ArtistEntity.of("artistId", "name", "image", Arrays.asList("K-POP"));
        artistRepository.save(artist);

        SliceResponse<ArtistListResponse> result = artistService.searchArtists("name", 20, 0, Sort.Direction.DESC);
        List<ArtistListResponse> contents = result.getContent();
        assertThat(contents.get(0).getArtistName()).isEqualTo(artist.getArtistName());
    }

    @Test
    @DisplayName("getAlbums - 성공")
    void getAlbums() {
        ArtistEntity artist = ArtistEntity.of("artistId", "name", "image", Arrays.asList("K-POP"));
        artistRepository.save(artist);

        AlbumEntity album = AlbumEntity.of("albumId", "title", "albumImage", "releaseDate");
        album.setArtist(artist);
        albumRepository.save(album);

        SliceResponse<AlbumResponse> result = artistService.getAlbums("artistId", 20, 0, Sort.Direction.DESC);
        List<AlbumResponse> contents = result.getContent();
        assertThat(contents.get(0).getId()).isEqualTo(album.getId());
        assertThat(contents.get(0).getTitle()).isEqualTo(album.getTitle());
        assertThat(contents.get(0).getAlbumImage()).isEqualTo(album.getAlbumImage());
    }

    @Test
    @DisplayName("getAlbums - 아티스트를 찾을 수 없습니다.")
    void getAlbums_artistNotFound() {
        // given
        ArtistEntity artist = ArtistEntity.of("artistId", "name", "image", Arrays.asList("K-POP"));
        artistRepository.save(artist);

        AlbumEntity album = AlbumEntity.of("albumId", "title", "albumImage", "releaseDate");
        album.setArtist(artist);
        albumRepository.save(album);

        // when
        Throwable throwable = catchThrowable(() -> artistService.getAlbums("artistId2", 20, 0, Sort.Direction.DESC));

        // then
        assertThat(throwable).isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("getArtistPerformance - 성공")
    void getArtistPerformance() {
        // given
        Performance performance1 = createPerformance("1");
        performanceRepository.save(performance1);

        ArtistEntity artist = ArtistEntity.of("artistId", "name", "image", Arrays.asList("K-POP"));
        artistRepository.save(artist);

        PerformanceArtist performanceArtist = PerformanceArtist.of(performance1, artist);
        performanceArtistRepository.save(performanceArtist);

        SliceResponse<PerformanceResponse> result = artistService.getArtistPerformances("artistId", 20, 0, Sort.Direction.DESC);
        List<PerformanceResponse> contents = result.getContent();
        assertThat(contents.get(0).getId()).isEqualTo(performance1.getId());
        assertThat(contents.get(0).getTitle()).isEqualTo(performance1.getTitle());
        assertThat(contents.get(0).getStatus()).isEqualTo(performance1.getStatus());
    }

    @Test
    @DisplayName("getArtistRank")
    void getArtistRank() {
        // given
        BoxOfficeRank boxOfficeRank = BoxOfficeRank.builder()
                .timePeriod(TimePeriod.WEEK)
                .performanceIds("1")
                .build();
        boxOfficeRankRepository.save(boxOfficeRank);

        String performanceIds = boxOfficeRank.getPerformanceIds();
        List<String> performanceIdList = Arrays.asList(performanceIds.split("\\|"));

        Performance performance1 = createPerformance(performanceIdList.get(0));
        performanceRepository.save(performance1);

        ArtistEntity artist = ArtistEntity.of("artistId", "name", "image", Arrays.asList("K-POP"));
        artistRepository.save(artist);

        PerformanceArtist performanceArtist = PerformanceArtist.of(performance1, artist);
        performanceArtistRepository.save(performanceArtist);

        // when
        List<ArtistListResponse> result = artistService.getArtistRank();

        // then
        assertThat(result.get(0).getId()).isEqualTo(artist.getId());
        assertThat(result.get(0).getArtistName()).isEqualTo(artist.getArtistName());
        assertThat(result.get(0).getArtistImage()).isEqualTo(artist.getArtistImage());

    }

    private Performance createPerformance(String id) {
        return Performance.builder()
                .id(id)
                .title("title")
                .status(PerformanceStatus.ONGOING)
                .build();
    }
}