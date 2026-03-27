package com.revconnect.service;

import com.revconnect.entity.Hashtag;
import com.revconnect.repository.HashtagRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashtagServiceImpl implements HashtagService {

    private final HashtagRepository hashtagRepository;

    @Override
    public List<Hashtag> getTrendingHashtags() {
        return hashtagRepository.findTop10ByOrderByUsageCountDesc();
    }

    @Override
    public Hashtag getByName(String name) {
        return hashtagRepository.findByName(name.toLowerCase())
                .orElseThrow(() -> new RuntimeException("Hashtag not found"));
    }
}