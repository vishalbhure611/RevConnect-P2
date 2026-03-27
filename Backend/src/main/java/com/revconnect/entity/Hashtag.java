package com.revconnect.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "hashtags",
        uniqueConstraints = @UniqueConstraint(columnNames = "name")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;   // example: springboot (store without #)

    @Column(nullable = false)
    private long usageCount = 0;

    // Inverse side
    @ManyToMany(mappedBy = "hashtags")
    private List<Post> posts = new ArrayList<>();
}