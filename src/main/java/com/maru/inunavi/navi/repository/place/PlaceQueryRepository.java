package com.maru.inunavi.navi.repository.place;

import com.maru.inunavi.navi.domain.entity.Place;

import java.util.List;
import java.util.Optional;

public interface PlaceQueryRepository {
    Optional<List<Place>> findByTitleOrSort(String searchKeyword);
}
