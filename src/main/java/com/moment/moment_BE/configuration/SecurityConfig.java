package com.moment.moment_BE.configuration;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.crypto.spec.SecretKeySpec;

// class nay se cau hinh spring security, la mot lop bao ve cua api
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // phan quyen theo method
public class SecurityConfig {

   private final String[] PUBLIC_ENDPOINTS = { "/auth/token", "/auth/introspect"};

    @Value("${jwt.signerKey}")
    private  String singerKey;

    @Value("${spring.web.cors.allowed-origin}")
    private String allowedOrigin;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
//       cac request trong nay la se duoc public vi dung dang nhap
        httpSecurity.authorizeHttpRequests(request ->
                request.requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS).permitAll()

                        .anyRequest().authenticated());

//        config neu co token va token do hop le thi cho phep call api
//        ham nay nhan token va xac thuc, nay cau hinh cho headerauthentication
//        ham nay de cho phep nhan token, khi o front end gui token qua header Authorization thi ham cai nay se nhan duoc
        httpSecurity.oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwtConfigurer -> jwtConfigurer.decoder(jwtDecoder()))
        );
//
//      mac dinh  tat cau hinh nay di
        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        return httpSecurity.build();
    }


    //    cau hinh giai ma token, xem co hop le hay khong hop le
    @Bean
    JwtDecoder jwtDecoder() {
        SecretKeySpec secretKeySpec = new SecretKeySpec(singerKey.getBytes(),"HS512");
        return NimbusJwtDecoder.withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Chỉ cho phép origin http://localhost:3000
        config.addAllowedOriginPattern(allowedOrigin);
        // Cho phép tất cả các HTTP method (GET, POST, etc.)
        config.addAllowedMethod("*");
        // Cho phép tất cả các header
        config.addAllowedHeader("*");
        // Cấu hình khác (nếu cần)
        config.setAllowCredentials(true); // Cho phép gửi thông tin xác thực (credentials) như cookies, authorization headers

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

}
