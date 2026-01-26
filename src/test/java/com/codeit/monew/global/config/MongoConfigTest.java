package com.codeit.monew.global.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MongoConfigTest {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    void mongoDBConnection_ShouldWork() {
        // MongoDB 연결 확인
        String dbName = mongoTemplate.getDb().getName();
        assertThat(dbName).isEqualTo("monew");
    }
}