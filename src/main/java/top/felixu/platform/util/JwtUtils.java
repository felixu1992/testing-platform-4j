package top.felixu.platform.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import top.felixu.platform.exception.ErrorCode;
import top.felixu.platform.exception.PlatformException;
import top.felixu.platform.properties.PermissionProperties;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author felixu
 * @since 2021.08.24
 */
public class JwtUtils {

    private static final PermissionProperties PROPERTIES = ApplicationUtils.getBean(PermissionProperties.class);

    private static final Algorithm ALGORITHM = Algorithm.HMAC512(PROPERTIES.getSecret());

    private static final JWTVerifier VERIFIER = JWT.require(ALGORITHM).build();

    public static String generate(Integer userId) {
        return JWT.create()
                .withClaim("userId", userId)
                .withIssuedAt(new Date())
                .withIssuer("felixu")
                .sign(ALGORITHM);
    }

    public static Integer getUserId(String token) {
        try {
            DecodedJWT jwt = VERIFIER.verify(token);
            Map<String, Claim> claims = jwt.getClaims();
            Integer userId = claims.get("userId").asInt();
            if (null == userId)
                throw new PlatformException(ErrorCode.REQUIRE_LOGIN);
            return userId;
        } catch (JWTVerificationException exception) {
            throw new PlatformException(ErrorCode.REQUIRE_LOGIN);
        }
    }
}
