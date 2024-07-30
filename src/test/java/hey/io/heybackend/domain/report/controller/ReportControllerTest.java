package hey.io.heybackend.domain.report.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import hey.io.heybackend.common.security.jwt.JwtTokenProvider;
import hey.io.heybackend.domain.report.dto.ReportRequest;
import hey.io.heybackend.domain.report.dto.ReportResponse;
import hey.io.heybackend.domain.report.service.ReportService;
import hey.io.heybackend.domain.user.domain.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReportService reportService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ReportController(reportService)).build();
    }

    private ReportRequest createReportRequest(List<String> type, String content) throws Exception {
        Constructor<ReportRequest> constructor = ReportRequest.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        ReportRequest request = constructor.newInstance();

        setField(request, "type", type);
        setField(request, "content", content);

        return request;
    }

    private void setField(Object object, String fieldName, Object value) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

    @Test
    @WithMockUser
    void reportPerformance() throws Exception {
        // given
        Long userId = 1L;
        String accessToken = "Bearer " + jwtTokenProvider.createAccessToken(userId, UserRole.USER);

        String performanceId = "performanceId";

        ReportRequest request = createReportRequest(Arrays.asList("공연명"), "공연명 오류");
        ReportResponse response = new ReportResponse(performanceId, userId);

        // when
        when(reportService.reportPerformance(eq(performanceId), eq(userId), any(ReportRequest.class))).thenReturn(response);
        ResultActions result = mockMvc.perform(post("/performances/{id}/report", performanceId)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().is2xxSuccessful());

    }

    @Test
    @WithMockUser
    void reportArtist() throws Exception {
        // given
        Long userId = 1L;
        String accessToken = "Bearer " + jwtTokenProvider.createAccessToken(userId, UserRole.USER);

        String artistId = "artistId";

        ReportRequest request = createReportRequest(Arrays.asList("아티스트명"), "아티스트명 오류");
        ReportResponse response = new ReportResponse(artistId, userId);

        // when
        when(reportService.reportArtist(eq(artistId), eq(userId), any(ReportRequest.class))).thenReturn(response);
        ResultActions result = mockMvc.perform(post("/artists/{id}/report", artistId)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().is2xxSuccessful());

    }
}