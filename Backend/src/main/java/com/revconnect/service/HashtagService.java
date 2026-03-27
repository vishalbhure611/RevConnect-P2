package com.revconnect.service;

import com.revconnect.entity.Hashtag;

import java.util.List;

public interface HashtagService {

    List<Hashtag> getTrendingHashtags();

    Hashtag getByName(String name);
}