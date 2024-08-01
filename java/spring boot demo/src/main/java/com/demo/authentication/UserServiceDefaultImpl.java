package com.demo.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;
import java.util.stream.IntStream;

/**
 * 默认创建一个用户，一般没啥用
 */
public class UserServiceDefaultImpl implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceDefaultImpl.class);
    private final AuthenticationUser user;

    public UserServiceDefaultImpl(PasswordEncoder passwordEncoder) {
        SecureRandom random = new SecureRandom();
        String charSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        String password = IntStream.range(0, 10)
                .mapToObj(i -> charSet.charAt(random.nextInt(charSet.length())))
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        user = new AuthenticationUser("admin", passwordEncoder.encode(password));
        log.info("Default user service create user at-username:{},password:{}", user.getUsername(), password);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (user.getUsername().equals(username))
            return new AuthenticationUser(username, user.getPassword());
        return null;
    }
}
