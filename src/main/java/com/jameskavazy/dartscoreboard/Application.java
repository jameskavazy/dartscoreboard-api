package com.jameskavazy.dartscoreboard;

import com.jameskavazy.dartscoreboard.match.match.Match;
import com.jameskavazy.dartscoreboard.match.match.MatchRepository;
import com.jameskavazy.dartscoreboard.match.match.MatchType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.OffsetDateTime;

@SpringBootApplication
public class Application {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

//	@Bean
//	CommandLineRunner runner(MatchRepository matchRepository) {
//		return args -> {
//			Match match = new Match(
//					"2", MatchType.FiveO, 1, 1, OffsetDateTime.now(), 0
//			);
//			matchRepository.create(match);
//		};
//	}

}
