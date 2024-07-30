package hey.io.heybackend.domain.performance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hey.io.heybackend.common.response.SliceResponse;
import hey.io.heybackend.common.security.jwt.JwtTokenProvider;
import hey.io.heybackend.domain.artist.domain.ArtistEntity;
import hey.io.heybackend.domain.artist.dto.ArtistListResponse;
import hey.io.heybackend.domain.performance.domain.enums.PerformanceStatus;
import hey.io.heybackend.domain.performance.domain.enums.TimePeriod;
import hey.io.heybackend.domain.performance.dto.*;
import hey.io.heybackend.domain.performance.service.PerformanceService;
import hey.io.heybackend.domain.user.domain.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PerformanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PerformanceService performanceService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new PerformanceController(performanceService)).build();
    }


    private PerformanceFilterRequest createPerformanceFilterRequest(List<PerformanceStatus> statuses, String visit) throws Exception {
        Constructor<PerformanceFilterRequest> constructor = PerformanceFilterRequest.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        PerformanceFilterRequest request = constructor.newInstance();

        setField(request, "statuses", statuses);
        setField(request, "visit", visit);

        return request;
    }

    private PerformanceSearchRequest createPerformanceSearchRequest(String keyword, List<PerformanceStatus> statuses) throws Exception {
        Constructor<PerformanceSearchRequest> constructor = PerformanceSearchRequest.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        PerformanceSearchRequest request = constructor.newInstance();

        setField(request, "keyword", keyword);
        setField(request, "statuses", statuses);

        return request;
    }

    private BoxOfficeRankRequest createBoxOfficeRankRequest(TimePeriod timePeriod) throws Exception {
        Constructor<BoxOfficeRankRequest> constructor = BoxOfficeRankRequest.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        BoxOfficeRankRequest request = constructor.newInstance();

        setField(request, "timePeriod", timePeriod);

        return request;
    }

    private PerformanceDetailResponse createPerformanceDetailResponse(String id) throws Exception {
        Constructor<PerformanceDetailResponse> constructor = PerformanceDetailResponse.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        PerformanceDetailResponse response = constructor.newInstance();

        setField(response, "id", id);
        setField(response, "placeId", "placeId");
        setField(response, "title", "title");
        setField(response, "startDate", LocalDate.now());
        setField(response, "endDate", LocalDate.now().plusDays(1));
        setField(response, "theater", "theater");
        setField(response, "cast", "cast");
        setField(response, "runtime", "runtime");
        setField(response, "age", "age");
        setField(response, "price", "price");
        setField(response, "poster", "poster");
        setField(response, "status", PerformanceStatus.ONGOING);
        setField(response, "storyUrls", Arrays.asList("url1", "url2"));
        setField(response, "schedule", "schedule");
        setField(response, "latitude", 37.5665);
        setField(response, "longitude", 126.9780);
        setField(response, "address", "address");

        return response;
    }

    private ArtistListResponse createArtistListResponse(String id, String artistName, String artistImage) throws Exception {
        Constructor<ArtistListResponse> constructor = ArtistListResponse.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        ArtistListResponse response = constructor.newInstance();

        setField(response, "id", id);
        setField(response, "artistName", artistName);
        setField(response, "artistImage", artistImage);

        return response;
    }


    private void setField(Object object, String fieldName, Object value) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

    @Test
    @WithAnonymousUser
    void getPerformancesByCondition() throws Exception {
        // given
        SliceResponse<PerformanceResponse> sliceResponse = new SliceResponse<>(new SliceImpl<>(Collections.emptyList(), PageRequest.of(0, 20), false));
        PerformanceFilterRequest filterRequest = createPerformanceFilterRequest(Arrays.asList(PerformanceStatus.ONGOING, PerformanceStatus.UPCOMING), "n");

        // when
        when(performanceService.getPerformancesByCondition(any(PerformanceFilterRequest.class), eq(20), eq(0), eq(Direction.DESC))).thenReturn(sliceResponse);
        ResultActions result = mockMvc.perform(get("/performances")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filterRequest)));

        // then
        result.andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void searchPerformance() throws Exception {
        // given
        SliceResponse<PerformanceResponse> sliceResponse = new SliceResponse<>(new SliceImpl<>(Collections.emptyList(), PageRequest.of(0, 20), false));
        PerformanceSearchRequest searchRequest = createPerformanceSearchRequest("keyword", Arrays.asList(PerformanceStatus.ONGOING, PerformanceStatus.UPCOMING));

        // when
        when(performanceService.searchPerformances(any(PerformanceSearchRequest.class), eq(20), eq(0), eq(Direction.DESC))).thenReturn(sliceResponse);
        ResultActions result = mockMvc.perform(get("/performances/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(searchRequest)));

        // then
        result.andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void getNewPerformances() throws Exception {
        // given
        List<PerformanceResponse> newPerformances = Collections.emptyList();

        // when
        when(performanceService.getNewPerformances()).thenReturn(newPerformances);
        ResultActions result = mockMvc.perform(get("/performances/new")
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void getBoxOffice() throws Exception {
        // given
        BoxOfficeRankRequest boxOfficeRequest = createBoxOfficeRankRequest(TimePeriod.WEEK);
        List<PerformanceResponse> boxOfficePerformances = Collections.emptyList();

        // when
        when(performanceService.getBoxOfficeRank(any(BoxOfficeRankRequest.class))).thenReturn(boxOfficePerformances);
        ResultActions result = mockMvc.perform(get("/performances/rank")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(boxOfficeRequest)));

        // then
        result.andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getPerformance() throws Exception {
        // given
        Long userId = 1L;
        String accessToken = "Bearer " + jwtTokenProvider.createAccessToken(userId, UserRole.USER);

        String performanceId = "performanceId";
        PerformanceDetailResponse performanceDetailResponse = createPerformanceDetailResponse(performanceId);

        // when
        when(performanceService.getPerformance(performanceId)).thenReturn(performanceDetailResponse);
        ResultActions result = mockMvc.perform(get("/performances/{id}", performanceId)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getPerformanceArtist() throws Exception {
        // given
        Long userId = 1L;
        String accessToken = "Bearer " + jwtTokenProvider.createAccessToken(userId, UserRole.USER);

        String performanceId = "performanceId";

        ArtistEntity artist = ArtistEntity.of("artistId", "name", "image", Arrays.asList("K-POP"));

        ArtistListResponse artistListResponse = createArtistListResponse(artist.getId(), artist.getArtistName(), artist.getArtistImage());
        List<ArtistListResponse> artistListResponses = Arrays.asList(artistListResponse);

        // when
        when(performanceService.getPerformanceArtists(performanceId)).thenReturn(artistListResponses);
        ResultActions result = mockMvc.perform(get("/performances/{id}/artists", performanceId)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk());
    }
}
