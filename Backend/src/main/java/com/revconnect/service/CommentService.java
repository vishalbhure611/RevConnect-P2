package com.revconnect.service;

import com.revconnect.entity.Comment;

import java.util.List;

public interface CommentService {
    Comment addComment(Long userId, Long postId, String content);

    void deleteComment(Long commentId, Long requestUserId);

    List<Comment> getCommentsByPost(Long postId);
}
