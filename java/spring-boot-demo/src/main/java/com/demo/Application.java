package com.demo;


import com.demo.core.authentication.GoogleCacheTokenManager;
import com.demo.core.authentication.TokenManager;
import com.demo.core.config.jpa.CustomerBaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableJpaRepositories(
        //设置自定义JPA Repository
        repositoryBaseClass = CustomerBaseRepositoryImpl.class
)
public class Application {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public TokenManager tokenManager(@Value("${authentication.tokenTimeout}") int timeout) {
        return new GoogleCacheTokenManager(timeout);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
