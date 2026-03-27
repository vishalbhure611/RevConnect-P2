package com.revconnect.service;

import com.revconnect.entity.NotificationPreference;
import com.revconnect.entity.NotificationType;

import java.util.List;

public interface NotificationPreferenceService {

    List<NotificationPreference> getPreferences(Long userId);

    NotificationPreference setPreference(Long userId, NotificationType type, boolean enabled);

    boolean isEnabled(Long userId, NotificationType type);
}

