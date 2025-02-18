package hey.io.heybackend.domain.follow.service;


import hey.io.heybackend.common.exception.BusinessException;
import hey.io.heybackend.common.exception.ErrorCode;
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
import hey.io.heybackend.domain.performance.dto.PerformanceResponse;
import hey.io.heybackend.domain.performance.repository.PerformanceRepository;
import hey.io.heybackend.domain.user.domain.User;
import hey.io.heybackend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowService {

    private final UserRepository userRepository;
    private final PerformanceRepository performanceRepository;
    private final FollowPerformanceRepository followPerformanceRepository;
    private final ArtistRepository artistRepository;
    private final FollowArtistRepository followArtistRepository;

    @Transactional
    public FollowResponse followPerformance(Long userId, String performanceId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PERFORMANCE_NOT_FOUND));

        followPerformanceRepository.findByUserAndPerformance(user, performance)
                .ifPresent(it -> {
                    throw new BusinessException(ErrorCode.ALREADY_FOLLOWED_PERFORMANCE);
                });
        followPerformanceRepository.save(PerformanceFollow.of(user, performance));
        return new FollowResponse(performance.getId(), "Follow Success");
    }

    public SliceResponse<PerformanceResponse> getFollowPerformances(Long userId, int size, int page, Direction direction) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Slice<PerformanceResponse> followedPerformance = followPerformanceRepository.getFollowPerformances(user.getUserId(), Pageable.ofSize(size).withPage(page), direction);
        return new SliceResponse<>(followedPerformance);
    }

    @Transactional
    public void deleteFollowPerformances(Long userId, List<String> performanceIds) {
        userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        followPerformanceRepository.deleteAllByIds(performanceIds);
    }

    @Transactional
    public FollowResponse followArtist(Long userId, String artistId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        ArtistEntity artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ARTIST_NOT_FOUND));

        followArtistRepository.findByUserAndArtist(user, artist)
                .ifPresent(it -> {
                    throw new BusinessException(ErrorCode.ALREADY_FOLLOWED_ARTIST);
                });
        followArtistRepository.save(ArtistFollow.of(user, artist));
        return new FollowResponse(artist.getId(), "Follow Success");
    }

    public SliceResponse<ArtistListResponse> getFollowArtists(Long userId, int size, int page, Direction direction) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Slice<ArtistListResponse> followedArtist = followArtistRepository.getFollowArtists(user.getUserId(), Pageable.ofSize(size).withPage(page), direction);
        return new SliceResponse<>(followedArtist);
    }

    @Transactional
    public void deleteFollowArtists(Long userId, List<String> artistIds) {
        userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        followArtistRepository.deleteAllByIds(artistIds);
    }
}
