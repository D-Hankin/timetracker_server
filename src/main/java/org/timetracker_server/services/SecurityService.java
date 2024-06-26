package org.timetracker_server.services;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Set;

import org.bson.Document;
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
     
        User user = new User(userDoc.get("_id").toString(), userDoc.get("username").toString(), userDoc.get("name").toString(), userDoc.get("password").toString(), 
            userDoc.get("email").toString(), userDoc.get("roleId").toString());

        String role;

        if ("66335005aad6d2c4821c092b".equals(userDoc.get("roleId").toString())) {
            role = "user";
        } else {
            role = "admin";
        }
    
        if (user != null && checkUserCredentials(user, loginDto.getPassword())) {
            String newToken = generateJwtToken(user);
            return Response.ok().entity(new TokenResponse(newToken, "86400", role)).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorised login attempt!").build();
        }
    }
    
    private boolean checkUserCredentials(User user, String password) {
        return user != null && BCrypt.checkpw(password, user.getPassword());
    }
    
    private PrivateKey loadPrivateKey() throws Exception {

        // if (!Files.exists(Paths.get("src/main/resources/privateKey.pem"))) {

            try {
                String privateKeyString = System.getenv("PRIVATE_KEY");

                privateKeyString = privateKeyString.replace("-----BEGIN PRIVATE KEY-----", "")
                                                    .replace("-----END PRIVATE KEY-----", "")
                                                    .replaceAll("\\s", "");
                byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyString);
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                
                return keyFactory.generatePrivate(keySpec);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return null;
            }
    }
    
    private static PublicKey loadPublicKey() throws Exception {

        String keyContent = System.getenv("PUBLIC_KEY");
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
        PrivateKey privateKey = loadPrivateKey();

        String issuer = config.jwtIssuer() != null ? config.jwtIssuer() : System.getenv("JWT_ISSUER");

        return Jwt.issuer(issuer)
            .upn(user.getUsername())
            .groups(userPermissions)
            .expiresIn(86400)
            .claim(Claims.email_verified.name(), user.getEmail())
            .sign(privateKey);
    }

    public Jws<io.jsonwebtoken.Claims> verifyJwt(String jwtToken) throws Exception {
        
        PublicKey publicKey = loadPublicKey();
        
        String issuer = config.jwtIssuer() != null ? config.jwtIssuer() : System.getenv("JWT_ISSUER");
        
        try {
            return Jwts.parser().requireIssuer(issuer).verifyWith(publicKey).build().parseSignedClaims(jwtToken);
        } catch (SignatureException e) {
            Exception exception = new Exception("JWT Signature not valid");
            exception.initCause(e);
            throw exception;
        } catch (ExpiredJwtException e) {
            Exception exception = new Exception("JWT has expired");
            exception.initCause(e);
            throw exception;
        } catch (UnsupportedJwtException e) {
            Exception exception = new Exception("JWT not supported");
            exception.initCause(e);
            throw exception;
        } catch (MalformedJwtException e) {
            Exception exception = new Exception("Invalid JWT format");
            exception.initCause(e);
            throw exception;
        } catch (IllegalArgumentException e) {
            Exception exception = new Exception("Invalid JWT");
            exception.initCause(e);
            throw exception;
        }
        
    }
}
