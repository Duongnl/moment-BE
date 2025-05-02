package com.moment.moment_BE.service;

import com.moment.moment_BE.dto.request.AuthenticationRequest;
import com.moment.moment_BE.dto.request.IntrospectRequest;
import com.moment.moment_BE.dto.response.AuthenticationResponse;
import com.moment.moment_BE.dto.response.IntrospectResponse;
import com.moment.moment_BE.dto.response.UserResponse;
import com.moment.moment_BE.entity.Account;
import com.moment.moment_BE.entity.Photo;
import com.moment.moment_BE.entity.Profile;
import com.moment.moment_BE.exception.AccountErrorCode;
import com.moment.moment_BE.exception.AppException;
import com.moment.moment_BE.exception.AuthErrorCode;
import com.moment.moment_BE.mapper.AccountMapper;
import com.moment.moment_BE.mapper.ProfileMapper;
import com.moment.moment_BE.repository.AccountRepository;
import com.moment.moment_BE.repository.PhotoRepository;
import com.moment.moment_BE.repository.ProfileRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

import static com.moment.moment_BE.utils.DateTimeUtils.getCurrentTimeInSystemLocalTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {

    AccountRepository accountRepository;
    ProfileRepository profileRepository;

    @NonFinal// de no kh inject vao contructe cua clombok
    @Value("${jwt.signerKey}")
    String SIGNER_KEY;

    @NonFinal
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    String clientId;

    @NonFinal
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
     String clientSecret;

    @NonFinal
    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
     String redirectUri;

    AccountMapper accountMapper;
    ProfileMapper profileMapper;
    PhotoRepository photoRepository;

    // kiem tra xem token co hop le khong
    public IntrospectResponse introspect(IntrospectRequest request)
            throws JOSEException, ParseException {
        var token = request.getToken();

        try {

            JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

//        token cua  ng dung
            SignedJWT signedJWT = SignedJWT.parse(token);

//        lay ra han cua token
            Date expityTime = signedJWT.getJWTClaimsSet().getExpirationTime();

//        tra ve true hoac flase
            var verified = signedJWT.verify(verifier);
            if (!verified) {
                throw new AppException(AuthErrorCode.UNAUTHENTICATED);
            } else {
                //         tra ve introspectresponse
                return IntrospectResponse.builder()
                        .valid(true)
                        .build();

            }
        } catch (Exception e) {
         throw new AppException(AuthErrorCode.UNAUTHENTICATED);
        }
    }

    public String getSubFromToken(String tokenReq)
            throws JOSEException, ParseException {
        var token = tokenReq;

        try {

            JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

//        token cua  ng dung
            SignedJWT signedJWT = SignedJWT.parse(token);

//        lay ra han cua token
            Date expityTime = signedJWT.getJWTClaimsSet().getExpirationTime();

//        tra ve true hoac flase
            var verified = signedJWT.verify(verifier);
            if (!verified) {
                throw new AppException(AuthErrorCode.UNAUTHENTICATED);
            } else {
                String userName = signedJWT.getJWTClaimsSet().getStringClaim("sub");
                //         tra ve introspectresponse
                // Kiểm tra username có tồn tại không
                if (userName == null) {
                    throw new AppException(AuthErrorCode.UNAUTHENTICATED);
                }

                return userName;

            }
        } catch (Exception e) {
            throw new AppException(AuthErrorCode.UNAUTHENTICATED);
        }
    }

    //    login ne
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = accountRepository.findByUserNameAndStatus(request.getUserName(), 1)
                .orElseThrow(() -> new AppException(AccountErrorCode.USER_NOT_FOUND));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
//        so sanh pass ng dung nhap vaao va pass o dataabase
        boolean authenticated = passwordEncoder.matches(request.getPassword(),
                user.getPassword());


//        neu dang nhap khong thanh cong
        if (!authenticated) {
            throw new AppException(AuthErrorCode.UNAUTHENTICATED);
        } else {
//            dang nhap thanh cong
            var token = generateToken(user);
            return
                    AuthenticationResponse.builder()
                            .token(token)
                            .authenticated(authenticated)
                            .build();
        }
    }


    //    tao token bang username
    public String generateToken(Account account) {

//        tao header
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
//      tao body
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(account.getUserName())
                .issuer("moment.com") // token nay dc issuer tu ai
                .issueTime(new Date()) // thoi diem hien tai
                .build();
//      tao pay load
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

//        built thong tin token
        JWSObject jwsObject = new JWSObject(header, payload);

//      ky token
        try {
//            hash content nay
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
//            tra ve kieu string
            return jwsObject.serialize();
        } catch (JOSEException e) {
//            log.error("Cannnot create token",e);
            throw new RuntimeException(e);
        }


    }

    // lay info
    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        Account account = accountRepository.findByUserNameAndStatus(name, 1).orElseThrow(
                () -> new AppException(AccountErrorCode.USER_NOT_FOUND)
        );

        UserResponse userResponse = new UserResponse();
        userResponse = accountMapper.toUserResponse(account);
        profileMapper.toUserResponse(userResponse, account.getProfile());

//        set avatar
        String avt = null;
        Photo photoOptional = photoRepository.findByAccount_IdAndStatus(userResponse.getId(), 2);
        if (photoOptional != null) {
            avt = photoOptional.getUrl();
        }
        userResponse.setUrlPhoto(avt);


        return userResponse;
    }

    public Account getMyAccount(int status) {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        return accountRepository.findByUserNameAndStatus(name, status).orElseThrow(
                () -> new AppException(AccountErrorCode.USER_NOT_FOUND)
        );
    }


    @Transactional
    public String getToken (String code){
        String tokenUrl = "https://oauth2.googleapis.com/token";
        Map<String, String> params = Map.of(
                "code", code,
                "client_id", clientId,
                "client_secret", clientSecret,
                "redirect_uri", redirectUri,
                "grant_type", "authorization_code"
        );
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, params, Map.class);
        Map<String, Object> tokenData = response.getBody();
        String accessToken = (String) tokenData.get("access_token");

        // 2️⃣ Lấy thông tin user từ Google
        String userInfoUrl = "https://www.googleapis.com/oauth2/v3/userinfo?access_token=" + accessToken;
        ResponseEntity<Map> userResponse = restTemplate.getForEntity(userInfoUrl, Map.class);
        Map<String, Object> userInfo = userResponse.getBody();

        String email = (String) userInfo.get("email");

        Account user = accountRepository.findByEmailAndStatus(email,1)
                .orElseGet(() -> {
                    LocalDateTime localDateTime = getCurrentTimeInSystemLocalTime();
                    String name = (String) userInfo.get("given_name");

                    Profile profile = new Profile();
                    profile.setName(name);

                    Account newAccount = new Account();
                    newAccount.setEmail(email);
                    newAccount.setUserName(email);
                    newAccount.setStatus(1);
                    newAccount.setCreatedAt(localDateTime);
                    newAccount.setProfile(profile);
                    newAccount.setPassword("");

                    profile.setAccount(newAccount);
                    accountRepository.save(newAccount);
                    profileRepository.save(profile);
                    return newAccount;
                });

        // 4️⃣ Tạo JWT token và gửi về FE
        return generateToken(user);
    }
}

