package org.timetracker_server.services;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Set;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.jwt.Claims;
import org.mindrot.jbcrypt.BCrypt;
import org.timetracker_server.models.LoginDto;
import org.timetracker_server.models.TokenResponse;
import org.timetracker_server.models.User;

import config.AppConfig;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

@Transactional(Transactional.TxType.SUPPORTS)
@ApplicationScoped
public class SecurityService {

    @Inject
    UserService userService;

    @Inject
    AppConfig config;

    public Response userLogin(final LoginDto loginDto) throws Exception {
        Response userResponse = userService.findUser(loginDto.getUsername());
        Document userDoc = (Document) userResponse.getEntity();
        ObjectId userId = new ObjectId(userDoc.get("_id").toString());
        ObjectId roleId = new ObjectId(userDoc.get("roleId").toString());
        User user = new User(userId, userDoc.get("username").toString(), userDoc.get("name").toString(), userDoc.get("password").toString(), 
            userDoc.get("email").toString(), roleId);
    
        if (user != null && checkUserCredentials(user, loginDto.getPassword())) {
            String newToken = generateJwtToken(user);
            return Response.ok().entity(new TokenResponse(newToken, "86400")).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorised login attempt!").build();
        }
    }
    
    private boolean checkUserCredentials(User user, String password) {
        return user != null && BCrypt.checkpw(password, user.getPassword());
    }
    
    private static PrivateKey loadPrivateKey(String privateKeyPath) throws Exception {
        byte[] keyBytes = Files.readAllBytes(Paths.get(privateKeyPath));
        String keyContent = new String(keyBytes, StandardCharsets.UTF_8);
        keyContent = keyContent.replace("-----BEGIN PRIVATE KEY-----", "")
                               .replace("-----END PRIVATE KEY-----", "")
                               .replaceAll("\\s", "");
        byte[] decodedKeyBytes = Base64.getDecoder().decode(keyContent);
    
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decodedKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(spec);
    }
    
    private static PublicKey loadPublicKey(String publicKeyPath) throws Exception {

        byte[] keyBytes = Files.readAllBytes(Paths.get(publicKeyPath));
        String keyContent = new String(keyBytes, StandardCharsets.UTF_8);
        keyContent = keyContent.replace("-----BEGIN PUBLIC KEY-----", "")
                               .replace("-----END PUBLIC KEY-----", "")
                               .replaceAll("\\s", "");
        byte[] decodedKeyBytes = Base64.getDecoder().decode(keyContent);
    
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decodedKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }

    private String generateJwtToken(final User user) throws Exception {

        Set<String> userPermissions = userService.getUserPermissions(user);
        PrivateKey privateKey = loadPrivateKey("src/main/resources/privateKey.pem");

        return Jwt.issuer(config.getJwtIssuer())
            .upn(user.getUsername())
            .groups(userPermissions)
            .expiresIn(86400)
            .claim(Claims.email_verified.name(), user.getEmail())
            .sign(privateKey);
    }

    public Jws<io.jsonwebtoken.Claims> verifyJwt(String jwtToken) throws Exception {
        
        PublicKey publicKey = loadPublicKey("src/main/resources/publicKey.pem");

        try {
            return Jwts.parser().requireIssuer(config.getJwtIssuer()).verifyWith(publicKey).build().parseSignedClaims(jwtToken);
        } catch (SignatureException e) {
            throw new Exception("JWT Signature not valid");
        } catch (ExpiredJwtException e) {
            throw new Exception("JWT has expired");
        } catch (UnsupportedJwtException e) {
            throw new Exception("JWT not supported");
        } catch (MalformedJwtException e) {
            throw new Exception("Invalid JWT format");
        } catch (IllegalArgumentException e) {
            throw new Exception("Invalid JWT");
        }
    }
}
