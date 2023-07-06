package com.maru.inunavi.recommend.repository;

import java.util.List;
import java.util.Map;

public interface RecommendQueryRepository {

    List<String> findRecommendsByEmail(String email);

}
