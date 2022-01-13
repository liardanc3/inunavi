package com.maru.inunavi.service;

import com.maru.inunavi.repository.AllLectureRepository;
import com.maru.inunavi.repository.NaviRepository;
import com.maru.inunavi.repository.UserInfoRepository;
import com.maru.inunavi.repository.UserLectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NaviService {

    private final NaviRepository _NaviRepository;
    private final UserInfoRepository _UserInfoRepository;
    private final UserLectureRepository _UserLectureRepository;
    private final AllLectureRepository _AllLectureRepository;

}
