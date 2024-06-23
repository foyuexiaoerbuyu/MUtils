package cn.mvp.utils;

import java.util.Date;
import java.util.Map;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtUtils {

    private static String signKey = "xxx";

    /**
     * 12h过期
     */
    private static Long expire = 43200000L;

    /**
     * 生成JWT令牌
     *
     * @param claims JWT第二部分负载 payload 中存储的内容
     */
    public static String generateJwt(Map<String, Object> claims) {
        return Jwts.builder()
                .addClaims(claims)
                .signWith(SignatureAlgorithm.HS256, signKey.getBytes())
                .setExpiration(new Date(System.currentTimeMillis() + expire))
                .compact();
    }

    /**
     * 解析JWT令牌
     *
     * @param jwt JWT令牌
     * @return JWT第二部分负载 payload 中存储的内容
     */
    public static Claims parseJWT(String jwt) {
        return Jwts.parser()
                .setSigningKey(signKey.getBytes())
                .parseClaimsJws(jwt)
                .getBody();
    }
}