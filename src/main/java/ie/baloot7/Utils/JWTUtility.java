package ie.baloot7.Utils;

import java.io.Serializable;
import java.util.Base64;
import java.util.Date;

//import io.jsonwebtoken.impl.crypto.DefaultJwtSignatureValidator;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;

@Component
public class JWTUtility implements Serializable {

    public static final long JWT_TOKEN_VALIDITY_DURATION = 24*60*60;

    private static final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    private static final SecretKeySpec secret = new SecretKeySpec("'===========================baloot2023==========================='".getBytes(), signatureAlgorithm.getJcaName());

//    private static final DefaultJwtSignatureValidator validator = new DefaultJwtSignatureValidator(signatureAlgorithm, Secret);



    public static String getUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody().getSubject();
    }

    public static Date getIssuedAtDateFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .getIssuedAt();
    }

    public static Date getExpirationDateFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();

        return claims.getExpiration();
    }

    public static boolean isTokenExpired(String token) {
        return getExpirationDateFromToken(token).before(new Date());
    }

    public static String generateToken(@NotNull String username) {

        return Jwts.builder().setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY_DURATION *1000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }


    public static boolean validateToken(@NotNull String token) {

        try {
            Jwts.parserBuilder().setSigningKey(secret).build().parse(token);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
}
