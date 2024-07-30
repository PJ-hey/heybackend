package hey.io.heybackend.domain.artist.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hey.io.heybackend.common.response.SliceResponse;
import hey.io.heybackend.common.security.jwt.JwtTokenProvider;
import hey.io.heybackend.domain.album.dto.AlbumResponse;
import hey.io.heybackend.domain.artist.domain.ArtistEntity;
import hey.io.heybackend.domain.artist.dto.ArtistListResponse;
import hey.io.heybackend.domain.artist.dto.ArtistResponse;
import hey.io.heybackend.domain.artist.service.ArtistService;
import hey.io.heybackend.domain.performance.dto.PerformanceResponse;
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
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
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
class ArtistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ArtistService artistService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ArtistController(artistService)).build();
    }

    private ArtistResponse createArtistResponse(ArtistEntity artist) throws Exception {
        Constructor<ArtistResponse> constructor = ArtistResponse.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        ArtistResponse response = constructor.newInstance();

        setField(response, "id", artist.getId());
        setField(response, "artistName", artist.getArtistName());
        setField(response, "artistImage", artist.getArtistImage());
        setField(response, "genre", artist.getGenre());

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
    void getArtist() throws Exception {
        // given
        ArtistEntity artist = ArtistEntity.of("artistId", "name", "image", Arrays.asList("K-POP"));
        ArtistResponse artistResponse = createArtistResponse(artist);
        // when
        when(artistService.getArtist(eq(artist.getId()))).thenReturn(artistResponse);
        ResultActions result = mockMvc.perform(get("/artists/{id}", "artistId")
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void searchArtist() throws Exception {
        // given
        SliceResponse<ArtistListResponse> sliceResponse = new SliceResponse<>(new SliceImpl<>(Collections.emptyList(), PageRequest.of(0, 20), false));

        // when
        when(artistService.searchArtists(any(), eq(20), eq(0), eq(Sort.Direction.DESC))).thenReturn(sliceResponse);
        ResultActions result = mockMvc.perform(get("/artists/search")
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk());

    }

    @Test
    @WithAnonymousUser
    void getAlbums() throws Exception {
        // given
        SliceResponse<AlbumResponse> sliceResponse = new SliceResponse<>(new SliceImpl<>(Collections.emptyList(), PageRequest.of(0, 20), false));

        // when
        when(artistService.getAlbums(any(), eq(20), eq(0), eq(Sort.Direction.DESC))).thenReturn(sliceResponse);
        ResultActions result = mockMvc.perform(get("/artists/{id}/albums", "artistId")
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void getArtistPerformances() throws Exception {
        // given
        SliceResponse<PerformanceResponse> sliceResponse = new SliceResponse<>(new SliceImpl<>(Collections.emptyList(), PageRequest.of(0, 20), false));

        // when
        when(artistService.getArtistPerformances(any(), eq(20), eq(0), eq(Sort.Direction.DESC))).thenReturn(sliceResponse);
        ResultActions result = mockMvc.perform(get("/artists/{id}/performances", "artistId")
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void getArtistRank() throws Exception {
        // given
        List<ArtistListResponse> artistListResponse = Collections.emptyList();

        // when
        when(artistService.getArtistRank()).thenReturn(artistListResponse);
        ResultActions result = mockMvc.perform(get("/artists/rank")
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk());
    }
}