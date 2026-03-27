package com.revconnect.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String fullName;
    private String bio;
    private String location;
    private String website;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String profilePictureUrl;

    @Enumerated(EnumType.STRING)
    private ProfilePrivacy privacy;

    // Creator/Business Fields
    private String category;
    private String contactEmail;
    private String contactPhone;
    private String businessAddress;
    private String businessHours;

    @Column(columnDefinition = "TEXT")
    private String externalLinks;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
