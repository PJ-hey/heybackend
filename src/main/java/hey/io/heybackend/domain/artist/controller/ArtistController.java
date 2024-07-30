package hey.io.heybackend.domain.artist.controller;

import hey.io.heybackend.common.response.SliceResponse;
import hey.io.heybackend.common.response.SuccessResponse;
import hey.io.heybackend.domain.album.dto.AlbumResponse;
import hey.io.heybackend.domain.artist.dto.ArtistListResponse;
import hey.io.heybackend.domain.artist.dto.ArtistResponse;
import hey.io.heybackend.domain.artist.service.ArtistService;
import hey.io.heybackend.domain.performance.dto.PerformanceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("artists")
@RequiredArgsConstructor
public class ArtistController {

    private final ArtistService artistService;

    @Operation(summary = "Get Artist", description = "Get Artist API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200"),
            @ApiResponse(
                    responseCode = "404",
                    description = "U001 : 회원을 찾을 수 없습니다",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(
                    responseCode = "404",
                    description = "AR001: 아티스트를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(hidden = true))),
    })
    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<ArtistResponse>> getArtist(@PathVariable("id") String artistId) {
        return SuccessResponse.of(artistService.getArtist(artistId)).asHttp(HttpStatus.OK);
    }

    @Operation(summary = "Search Artists", description = "Search Artists API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200")
    })
    @GetMapping("/search")
    public ResponseEntity<SuccessResponse<SliceResponse<ArtistListResponse>>> searchArtists(@RequestParam(value = "keyword", required = false) String keyword,
                                                                                            @RequestParam(value = "size", required = false, defaultValue = "20") int size,
                                                                                            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                                                            @RequestParam(name = "direction", required = false, defaultValue = "DESC") Sort.Direction direction) {
        return SuccessResponse.of(artistService.searchArtists(keyword, size, page, direction)).asHttp(HttpStatus.OK);
    }

    @Operation(summary = "Get Albums", description = "Get Albums API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200"),
            @ApiResponse(
                    responseCode = "404",
                    description = "U001 : 회원을 찾을 수 없습니다",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(
                    responseCode = "404",
                    description = "AR001: 아티스트를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(hidden = true))),
    })
    @GetMapping("/{id}/albums")
    public ResponseEntity<SuccessResponse<SliceResponse<AlbumResponse>>> getAlbums(@PathVariable("id") String artistId,
                                                                                   @RequestParam(value = "size", required = false, defaultValue = "20") int size,
                                                                                   @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                                                   @RequestParam(name = "direction", required = false, defaultValue = "DESC") Sort.Direction direction) {
        return SuccessResponse.of(artistService.getAlbums(artistId, size, page, direction)).asHttp(HttpStatus.OK);
    }

    @Operation(summary = "Get Artist`s Performances", description = "Get Artist`s Performances API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200"),
            @ApiResponse(
                    responseCode = "404",
                    description = "U001 : 회원을 찾을 수 없습니다",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(
                    responseCode = "404",
                    description = "AR001: 아티스트를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(hidden = true))),
    })
    @GetMapping("/{id}/performances")
    public ResponseEntity<SuccessResponse<SliceResponse<PerformanceResponse>>> getArtistPerformances(@PathVariable("id") String artistId,
                                                                                                     @RequestParam(value = "size", required = false, defaultValue = "20") int size,
                                                                                                     @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                                                                     @RequestParam(name = "direction", required = false, defaultValue = "DESC") Sort.Direction direction) {
        return SuccessResponse.of(artistService.getArtistPerformances(artistId, size, page, direction)).asHttp(HttpStatus.OK);
    }

    @Operation(summary = "Get Artist`s Performances", description = "Get Artist`s Performances API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200")
    })
    @GetMapping("/rank")
    public ResponseEntity<SuccessResponse<List<ArtistListResponse>>> getArtistRank() {
        return SuccessResponse.of(artistService.getArtistRank()).asHttp(HttpStatus.OK);
    }
}
