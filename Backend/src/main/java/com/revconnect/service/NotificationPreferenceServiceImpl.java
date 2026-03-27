package com.revconnect.service;

import com.revconnect.entity.NotificationPreference;
import com.revconnect.entity.NotificationType;
import com.revconnect.entity.User;
import com.revconnect.repository.NotificationPreferenceRepository;
import com.revconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationPreferenceServiceImpl implements NotificationPreferenceService {

    private final NotificationPreferenceRepository preferenceRepository;
    private final UserRepository userRepository;

    @Override
    public List<NotificationPreference> getPreferences(Long userId) {
        return preferenceRepository.findByUserId(userId);
    }

    @Override
    public NotificationPreference setPreference(Long userId, NotificationType type, boolean enabled) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        NotificationPreference pref = preferenceRepository.findByUserIdAndType(userId, type)
                .orElseGet(() -> NotificationPreference.builder()
                        .user(user)
                        .type(type)
                        .enabled(true)
                        .build());

        pref.setEnabled(enabled);
        return preferenceRepository.save(pref);
    }

    @Override
    public boolean isEnabled(Long userId, NotificationType type) {
        return preferenceRepository.findByUserIdAndType(userId, type)
                .map(NotificationPreference::isEnabled)
                .orElse(true);
    }
}

