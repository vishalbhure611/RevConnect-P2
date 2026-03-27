package com.revconnect.controller;

import com.revconnect.dto.ApiResponse;
import com.revconnect.dto.FollowerDemographicsDTO;
import com.revconnect.dto.PostAnalyticsResponseDTO;
import com.revconnect.dto.UserInsightsDTO;
import com.revconnect.entity.Post;
import com.revconnect.entity.PostAnalytics;
import com.revconnect.entity.Role;
import com.revconnect.entity.Follow;
import com.revconnect.repository.FollowRepository;
import com.revconnect.repository.PostAnalyticsRepository;
import com.revconnect.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final PostAnalyticsRepository postAnalyticsRepository;
    private final PostRepository postRepository;
    private final FollowRepository followRepository;

    @GetMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<PostAnalyticsResponseDTO>> getPostAnalytics(
            @PathVariable Long postId
    ) {
        PostAnalytics analytics = postAnalyticsRepository.findByPostId(postId)
                .orElseThrow(() -> new RuntimeException("Analytics not found"));

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Post analytics fetched successfully",
                        PostAnalyticsResponseDTO.builder()
                                .postId(postId)
                                .totalLikes(analytics.getTotalLikes())
                                .totalComments(analytics.getTotalComments())
                                .totalShares(analytics.getTotalShares())
                                .reach(analytics.getReach())
                                .build()
                )
        );
    }

    @GetMapping("/users/{userId}/insights")
    public ResponseEntity<ApiResponse<UserInsightsDTO>> getUserInsights(
            @PathVariable Long userId
    ) {
        List<Post> posts = postRepository.findByUserIdOrderByCreatedAtDesc(userId);

        long totalLikes = 0;
        long totalComments = 0;
        long totalShares = 0;

        for (Post p : posts) {
            if (p.getAnalytics() != null) {
                totalLikes += p.getAnalytics().getTotalLikes();
                totalComments += p.getAnalytics().getTotalComments();
                totalShares += p.getAnalytics().getTotalShares();
            }
        }

        long followerCount = followRepository.findByFollowingId(userId).size();
        long followingCount = followRepository.findByFollowerId(userId).size();

        return ResponseEntity.ok(
            new ApiResponse<>(
                    true,
                    "User insights fetched successfully",
                    UserInsightsDTO.builder()
                            .userId(userId)
                            .totalPosts(posts.size())
                            .totalLikes(totalLikes)
                            .totalComments(totalComments)
                            .totalShares(totalShares)
                            .followerCount(followerCount)
                            .followingCount(followingCount)
                            .build()
            )
        );
    }

    @GetMapping("/users/{userId}/followers/demographics")
    public ResponseEntity<ApiResponse<FollowerDemographicsDTO>> getFollowerDemographics(
            @PathVariable Long userId
    ) {
        List<Follow> followers = followRepository.findByFollowingId(userId);
        LocalDateTime last30Days = LocalDateTime.now().minusDays(30);

        long personalFollowers = followers.stream()
                .filter(follow -> follow.getFollower().getRole() == Role.PERSONAL)
                .count();

        long creatorFollowers = followers.stream()
                .filter(follow -> follow.getFollower().getRole() == Role.CREATOR)
                .count();

        long businessFollowers = followers.stream()
                .filter(follow -> follow.getFollower().getRole() == Role.BUSINESS)
                .count();

        long newFollowersLast30Days = followers.stream()
                .filter(follow -> follow.getCreatedAt() != null && follow.getCreatedAt().isAfter(last30Days))
                .count();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Follower demographics fetched successfully",
                        FollowerDemographicsDTO.builder()
                                .userId(userId)
                                .totalFollowers((long) followers.size())
                                .personalFollowers(personalFollowers)
                                .creatorFollowers(creatorFollowers)
                                .businessFollowers(businessFollowers)
                                .newFollowersLast30Days(newFollowersLast30Days)
                                .build()
                )
        );
    }
}
