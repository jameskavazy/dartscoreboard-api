//package com.jameskavazy.dartscoreboard;
//
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.simple.JdbcClient;
//import org.springframework.stereotype.Component;
//
//import java.time.OffsetDateTime;
//import java.util.HashMap;
//import java.util.UUID;
//
//@Component
//public class TestDataLoader implements CommandLineRunner {
//
//    private final JdbcTemplate jdbcTemplate;
//
//    public TestDataLoader(JdbcTemplate jdbcTemplate){
//        this.jdbcTemplate = jdbcTemplate;
//    }
//
//    @Override
//    public void run(String... args) throws Exception {
////        loadData();
//    }
//
//    public HashMap<String, UUID> loadData(){
//        UUID matchId = UUID.randomUUID();
//        UUID setId = UUID.randomUUID();
//        UUID legId = UUID.randomUUID();
//        UUID visitId = UUID.randomUUID();
//
//        UUID jamesId = UUID.randomUUID();
//        UUID floId = UUID.randomUUID();
//        UUID dadId = UUID.randomUUID();
//
//
//        // Users
//        jdbcTemplate.update("INSERT INTO users (user_id, email, username) values (?, 'james.kavazy@gmail.com', 'KavarzE')", jamesId);
//        jdbcTemplate.update("INSERT INTO users (user_id, email, username) values (?, 'florence@gmail.com', 'Flo')", floId);
//        jdbcTemplate.update("INSERT INTO users (user_id, email, username) values (?, 'charles.kavazy@gmail.com', 'Charlie')", dadId);
//
//
//
//        // Matches
//        jdbcTemplate.update("INSERT INTO matches (match_id, created_at, match_type, race_to_leg, race_to_set, match_status) " +
//                "values (?, ?, 'FiveO', 1, 1, 'ONGOING')", matchId, OffsetDateTime.now());
//
//        jdbcTemplate.update("INSERT INTO sets (set_id, match_id, created_at) " +
//                "values (?, ?, ?)", setId, matchId, OffsetDateTime.now());
//
//        jdbcTemplate.update("INSERT INTO legs (leg_id, set_id, match_id, turn_index, created_at) " +
//                "values (?, ?, ?, ?, ?)", legId, setId, matchId, 0, OffsetDateTime.now());
//
//        jdbcTemplate.update("INSERT INTO visits (visit_id, leg_id, score, user_id, checkout) " +
//                "values (?, ?, ?, ?, ?)", visitId, legId, 180, jamesId, false);
//
//        jdbcTemplate.update("INSERT INTO matches_users (match_id, user_id, position) " +
//                "values (?, ?, ?)", matchId, jamesId, 0);
//        jdbcTemplate.update("INSERT INTO matches_users (match_id, user_id, position) " +
//                "values (?, ?, ?)", matchId, floId, 1);
//        jdbcTemplate.update("INSERT INTO matches_users (match_id, user_id, position) " +
//                "values (?, ?, ?)", matchId, dadId, 2);
//
//        HashMap<String, UUID> dataMap = new HashMap<>();
//        dataMap.put("matchId", matchId);
//        dataMap.put("setId", setId);
//        dataMap.put("legId", legId);
//        dataMap.put("visitId", visitId);
//        dataMap.put("testUser1", jamesId);
//        dataMap.put("testUser2", floId);
//        dataMap.put("testUser3", dadId);
//
//        return dataMap;
//
//
//    }
//
//    public void tearDown() {
//        jdbcTemplate.update("DELETE FROM visits");
//        jdbcTemplate.update("DELETE FROM legs");
//        jdbcTemplate.update("DELETE FROM sets");
//        jdbcTemplate.update("DELETE FROM matches_users");
//        jdbcTemplate.update("DELETE FROM matches");
//        jdbcTemplate.update("DELETE FROM users");
//    }
//}
