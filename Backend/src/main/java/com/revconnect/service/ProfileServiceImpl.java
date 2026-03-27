package com.revconnect.service;

import com.revconnect.dto.ProfileResponseDTO;
import com.revconnect.entity.Connection;
import com.revconnect.entity.ConnectionStatus;
import com.revconnect.entity.Profile;
import com.revconnect.entity.ProfilePrivacy;
import com.revconnect.entity.User;
import com.revconnect.repository.ConnectionRepository;
import com.revconnect.repository.FollowRepository;
import com.revconnect.repository.ProfileRepository;
import com.revconnect.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final ConnectionRepository connectionRepository;

    /**
     * CREATE PROFILE
     */
    @Override
    public Profile createProfile(Long userId, Profile profile) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        profile.setUser(user);

        if (profile.getPrivacy() == null) {
            profile.setPrivacy(ProfilePrivacy.PUBLIC);
        }

        return profileRepository.save(profile);
    }

    /**
     * UPDATE PROFILE
     */
    @Override
    public Profile updateProfile(Long userId, Profile updatedProfile) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Profile profile = profileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Profile created = new Profile();
                    created.setUser(user);
                    created.setPrivacy(ProfilePrivacy.PUBLIC);
                    return profileRepository.save(created);
                });

        profile.setFullName(updatedProfile.getFullName());
        profile.setBio(updatedProfile.getBio());
        profile.setLocation(updatedProfile.getLocation());
        profile.setWebsite(updatedProfile.getWebsite());
        profile.setProfilePictureUrl(updatedProfile.getProfilePictureUrl());
        profile.setPrivacy(updatedProfile.getPrivacy() != null ? updatedProfile.getPrivacy() : profile.getPrivacy());

        profile.setCategory(updatedProfile.getCategory());
        profile.setContactEmail(updatedProfile.getContactEmail());
        profile.setContactPhone(updatedProfile.getContactPhone());
        profile.setBusinessAddress(updatedProfile.getBusinessAddress());
        profile.setBusinessHours(updatedProfile.getBusinessHours());
        profile.setExternalLinks(updatedProfile.getExternalLinks());

        return profileRepository.save(profile);
    }

    /**
     * BASIC FETCH BY USER ID
     */
    @Override
    public Profile getProfileByUserId(Long userId) {

        return profileRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new RuntimeException("Profile not found"));
    }

    /**
     * ADVANCED PROFILE FETCH
     * Includes privacy + connection + follow checks
     */
    @Override
    public ProfileResponseDTO getProfile(Long viewerId, Long profileOwnerId) {

        User user = userRepository.findById(profileOwnerId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Profile profile = profileRepository.findByUserId(profileOwnerId)
                .orElseGet(() -> {
                    Profile created = new Profile();
                    created.setUser(user);
                    created.setPrivacy(ProfilePrivacy.PUBLIC);
                    return profileRepository.save(created);
                });

        // Privacy check
        if (profile.getPrivacy() == ProfilePrivacy.PRIVATE
                && viewerId != null
                && !viewerId.equals(profileOwnerId)) {

            Connection connection =
                    connectionRepository.findBySenderIdAndReceiverId(viewerId, profileOwnerId)
                            .or(() -> connectionRepository.findBySenderIdAndReceiverId(profileOwnerId, viewerId))
                            .orElse(null);

            boolean isConnected =
                    connection != null && connection.getStatus() == ConnectionStatus.ACCEPTED;

            boolean isFollowing =
                    followRepository.existsByFollowerIdAndFollowingId(
                            viewerId,
                            profileOwnerId
                    );

            if (!isConnected && !isFollowing) {
                throw new RuntimeException("This profile is private");
            }
        }

        Long followersCount =
                followRepository.countByFollowingId(profileOwnerId);

        Long followingCount =
                followRepository.countByFollowerId(profileOwnerId);

        Long connectionsCount =
                connectionRepository.countBySenderIdAndStatus(profileOwnerId, ConnectionStatus.ACCEPTED)
                        + connectionRepository.countByReceiverIdAndStatus(profileOwnerId, ConnectionStatus.ACCEPTED);

        boolean isFollowing = false;
        boolean isConnected = false;
        String connectionStatus = null;

        if (viewerId != null && !viewerId.equals(profileOwnerId)) {
            isFollowing = followRepository.existsByFollowerIdAndFollowingId(viewerId, profileOwnerId);

            Connection connection =
                    connectionRepository.findBySenderIdAndReceiverId(viewerId, profileOwnerId)
                            .or(() -> connectionRepository.findBySenderIdAndReceiverId(profileOwnerId, viewerId))
                            .orElse(null);

            if (connection != null) {
                connectionStatus = connection.getStatus().name();
                isConnected = connection.getStatus() == ConnectionStatus.ACCEPTED;
            }
        }

        return mapToResponse(
                profile,
                followersCount,
                followingCount,
                connectionsCount,
                isFollowing,
                isConnected,
                connectionStatus,
                viewerId
        );
    }

    /**
     * FETCH PROFILE USING USERNAME
     * (Recommended approach for frontend)
     */
    @Override
    public ProfileResponseDTO getProfileByUsername(Long viewerId, String username) {

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Profile profile = profileRepository
                .findByUserId(user.getId())
                .orElse(null);

        // If profile doesn't exist create default
        if (profile == null) {

            profile = new Profile();
            profile.setUser(user);
            profile.setPrivacy(ProfilePrivacy.PUBLIC);

            profile = profileRepository.save(profile);
        }

        Long followersCount =
                followRepository.countByFollowingId(user.getId());

        Long followingCount =
                followRepository.countByFollowerId(user.getId());

        Long connectionsCount =
                connectionRepository.countBySenderIdAndStatus(user.getId(), ConnectionStatus.ACCEPTED)
                        + connectionRepository.countByReceiverIdAndStatus(user.getId(), ConnectionStatus.ACCEPTED);

        boolean isFollowing = false;
        boolean isConnected = false;
        String connectionStatus = null;

        if (viewerId != null && !viewerId.equals(user.getId())) {
            isFollowing = followRepository.existsByFollowerIdAndFollowingId(viewerId, user.getId());

            Connection connection =
                    connectionRepository.findBySenderIdAndReceiverId(viewerId, user.getId())
                            .or(() -> connectionRepository.findBySenderIdAndReceiverId(user.getId(), viewerId))
                            .orElse(null);

            if (connection != null) {
                connectionStatus = connection.getStatus().name();
                isConnected = connection.getStatus() == ConnectionStatus.ACCEPTED;
            }
        }

        return mapToResponse(
                profile,
                followersCount,
                followingCount,
                connectionsCount,
                isFollowing,
                isConnected,
                connectionStatus,
                viewerId
        );
    }

    /**
     * MAP ENTITY → DTO
     */
    private ProfileResponseDTO mapToResponse(
            Profile profile,
            Long followers,
            Long following,
            Long connections,
            boolean isFollowing,
            boolean isConnected,
            String connectionStatus,
            Long viewerId
    ) {

        return ProfileResponseDTO.builder()
                .userId(profile.getUser().getId())
                .username(profile.getUser().getUsername())
                .role(profile.getUser().getRole().name())

                .fullName(profile.getFullName())
                .bio(profile.getBio())
                .location(profile.getLocation())
                .website(profile.getWebsite())
                .profilePictureUrl(profile.getProfilePictureUrl())

                .privacy(profile.getPrivacy())

                .category(profile.getCategory())
                .contactEmail(profile.getContactEmail())
                .contactPhone(profile.getContactPhone())
                .businessAddress(profile.getBusinessAddress())
                .businessHours(profile.getBusinessHours())

                .followersCount(followers)
                .followingCount(following)
                .connectionsCount(connections)

                .isOwner(
                        viewerId != null &&
                                profile.getUser().getId().equals(viewerId)
                )
                .isFollowing(isFollowing)
                .isConnected(isConnected)
                .connectionStatus(connectionStatus)
                .externalLinks(profile.getExternalLinks())

                .build();
    }
}
