package com.revconnect.repository;

import com.revconnect.entity.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {

    Optional<Hashtag> findByName(String name);

    List<Hashtag> findTop10ByOrderByUsageCountDesc();
}