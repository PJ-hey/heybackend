package hey.io.heybackend.domain.follow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hey.io.heybackend.common.response.SliceResponse;
import hey.io.heybackend.common.security.jwt.JwtTokenProvider;
import hey.io.heybackend.domain.follow.dto.DeleteFollowRequest;
import hey.io.heybackend.domain.follow.dto.FollowResponse;
import hey.io.heybackend.domain.follow.service.FollowService;
import hey.io.heybackend.domain.performance.dto.PerformanceResponse;
import hey.io.heybackend.domain.user.domain.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PerformanceFollowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FollowService followService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new FollowController(followService)).build();
    }

    private DeleteFollowRequest createDeleteFollowRequest() throws Exception {
        Constructor<DeleteFollowRequest> constructor = DeleteFollowRequest.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        DeleteFollowRequest request = constructor.newInstance();

        List<String> ids = Collections.singletonList("1");

        setField(request, "ids", ids);
        return request;
    }

    private void setField(Object object, String fieldName, Object value) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

    @Test
    @WithMockUser
    void followPerformance() throws Exception {
        // given
        Long userId = 1L;
        String accessToken = "Bearer " + jwtTokenProvider.createAccessToken(userId, UserRole.USER);

        String performanceId = "performanceId";

        FollowResponse followResponse = new FollowResponse(performanceId, "Follow Success");

        // when
        when(followService.followPerformance(userId, performanceId)).thenReturn(followResponse);
        ResultActions result = mockMvc.perform(post("/users/follow/performances/{id}", performanceId)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getFollowPerformances() throws Exception {
        // given
        Long userId = 1L;
        String accessToken = "Bearer " + jwtTokenProvider.createAccessToken(userId, UserRole.USER);

        String performanceId = "performanceId";

        SliceResponse<PerformanceResponse> sliceResponse = new SliceResponse<>(new SliceImpl<>(Collections.emptyList(), PageRequest.of(0, 20), false));

        // when
        when(followService.getFollowPerformances(any(), eq(20), eq(0), eq(Sort.Direction.DESC))).thenReturn(sliceResponse);
        ResultActions result = mockMvc.perform(get("/users/follow/performances")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void deleteFollowPerformances() throws Exception {
        // given
        Long userId = 1L;
        String accessToken = "Bearer " + jwtTokenProvider.createAccessToken(userId, UserRole.USER);

        DeleteFollowRequest deleteFollowRequest = createDeleteFollowRequest();

        // when
        doNothing().when(followService).deleteFollowPerformances(eq(userId), eq(deleteFollowRequest.getIds()));
        ResultActions result = mockMvc.perform(delete("/users/follow/performances")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deleteFollowRequest)));

        // then
        result.andExpect(status().is2xxSuccessful());
    }

    @Test
    @WithMockUser
    void followArtist() throws Exception {
        // given
        Long userId = 1L;
        String accessToken = "Bearer " + jwtTokenProvider.createAccessToken(userId, UserRole.USER);

        String artistId = "artistId";

        FollowResponse followResponse = new FollowResponse(artistId, "Follow Success");

        // when
        when(followService.followPerformance(userId, artistId)).thenReturn(followResponse);
        ResultActions result = mockMvc.perform(post("/users/follow/artists/{id}", artistId)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getFollowArtists() throws Exception {
        // given
        Long userId = 1L;
        String accessToken = "Bearer " + jwtTokenProvider.createAccessToken(userId, UserRole.USER);

        String artistId = "artistId";

        SliceResponse<PerformanceResponse> sliceResponse = new SliceResponse<>(new SliceImpl<>(Collections.emptyList(), PageRequest.of(0, 20), false));

        // when
        when(followService.getFollowPerformances(any(), eq(20), eq(0), eq(Sort.Direction.DESC))).thenReturn(sliceResponse);
        ResultActions result = mockMvc.perform(get("/users/follow/artists")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void deleteFollowArtists() throws Exception {
        // given
        Long userId = 1L;
        String accessToken = "Bearer " + jwtTokenProvider.createAccessToken(userId, UserRole.USER);

        DeleteFollowRequest deleteFollowRequest = createDeleteFollowRequest();

        // when
        doNothing().when(followService).deleteFollowPerformances(eq(userId), eq(deleteFollowRequest.getIds()));
        ResultActions result = mockMvc.perform(delete("/users/follow/artists")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deleteFollowRequest)));

        // then
        result.andExpect(status().is2xxSuccessful());
    }
}