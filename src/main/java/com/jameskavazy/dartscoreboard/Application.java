package com.jameskavazy.dartscoreboard;

import com.jameskavazy.dartscoreboard.auth.config.AuthConfigProperties;
import com.jameskavazy.dartscoreboard.match.match.Match;
import com.jameskavazy.dartscoreboard.match.match.MatchRepository;
import com.jameskavazy.dartscoreboard.match.match.MatchType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.time.OffsetDateTime;

@SpringBootApplication
@EnableConfigurationProperties(AuthConfigProperties.class)
public class Application {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
