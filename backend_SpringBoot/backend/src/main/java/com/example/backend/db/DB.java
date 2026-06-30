package com.example.backend.db;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class DB {

    @Bean
    public DataSource source() {
        DriverManagerDataSource dmds = new DriverManagerDataSource();
        dmds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dmds.setUrl("jdbc:mysql://localhost:3306/sportsphere_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
        dmds.setUsername("root");
        dmds.setPassword("");
        return dmds;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}