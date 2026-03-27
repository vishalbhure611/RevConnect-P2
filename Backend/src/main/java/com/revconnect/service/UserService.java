package com.revconnect.service;

import com.revconnect.entity.User;
import com.revconnect.dto.UpdateAccountRequestDTO;
import com.revconnect.entity.Privacy;
import java.util.List;

public interface UserService {

    User createUser(User user);

    User getUserById(Long userId);

    List<User> searchUsers(String keyword);

    User updateUser(Long userId, User updatedUser);

    User updateAccount(Long userId, UpdateAccountRequestDTO request);

    User findByEmail(String email);

    User findByEmailOrUsername(String identifier);

    void updatePrivacy(Long userId, Privacy privacy);
}
