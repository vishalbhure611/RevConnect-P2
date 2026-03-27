package com.revconnect.repository;

import com.revconnect.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.time.LocalDateTime;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Post> findByScheduledTimeBefore(LocalDateTime time);

    List<Post> findByPinnedTrue();

    List<Post> findByUserIdAndPinnedTrueOrderByCreatedAtDesc(Long userId);

    List<Post> findByScheduledTimeBeforeAndPublishedFalse(LocalDateTime time);

    List<Post> findByUserIdOrderByPinnedDescCreatedAtDesc(Long userId);

    @Query("""
            select p
            from Post p
            join p.analytics a
            order by (a.totalLikes + a.totalComments + a.totalShares) desc, p.createdAt desc
            """)
    List<Post> findTrending(Pageable pageable);

    @Query("""
            select p
            from Post p
            join p.hashtags h
            where lower(h.name) = lower(:name)
            and p.published = true
            order by p.createdAt desc
            """)
    List<Post> findPublishedByHashtag(String name);

    @Query("""
            select p
            from Post p
            where p.published = true
            order by p.createdAt desc
            """)
    List<Post> findAllPublishedOrderByCreatedAtDesc();
}