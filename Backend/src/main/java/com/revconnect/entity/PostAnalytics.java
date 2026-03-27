package com.revconnect.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "post_analytics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===== Owning side =====
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false, unique = true)
    private Post post;

    // ===== Metrics =====
    @Column(nullable = false)
    private long totalLikes = 0;

    @Column(nullable = false)
    private long totalComments = 0;

    @Column(nullable = false)
    private long totalShares = 0;

    @Column(nullable = false)
    private long reach = 0;
}