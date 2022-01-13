package com.maru.inunavi;

import com.maru.inunavi.repository.AllLectureRepository;
import com.maru.inunavi.repository.NaviRepository;
import com.maru.inunavi.repository.UserInfoRepository;
import com.maru.inunavi.repository.UserLectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication

public class InunaviApplication {

	public static void main(String[] args) {
		SpringApplication.run(InunaviApplication.class, args);
	}

}
