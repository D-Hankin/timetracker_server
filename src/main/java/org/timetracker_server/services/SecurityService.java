package org.timetracker_server.services;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Set;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.jwt.Claims;
import org.mindrot.jbcrypt.BCrypt;
import org.timetracker_server.models.LoginDto;
import org.timetracker_server.models.TokenResponse;
import org.timetracker_server.models.User;
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

    public Response userLogin(final LoginDto loginDto) throws Exception {
        Response userResponse = userService.findUser(loginDto.getUsername());
        Document userDoc = (Document) userResponse.getEntity();
        ObjectId userId = new ObjectId(userDoc.get("_id").toString());
        ObjectId roleId = new ObjectId(userDoc.get("roleId").toString());
        User user = new User(userId, userDoc.get("username").toString(), userDoc.get("name").toString(), userDoc.get("password").toString(), 
            userDoc.get("email").toString(), roleId);
    
        if (user != null && checkUserCredentials(user, loginDto.getPassword())) {
            String token = generateJwtToken(user);
            return Response.ok().entity(new TokenResponse("Bearer " + token, "86400")).build();
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
    


    private String generateJwtToken(final User user) throws Exception {

        Set<String> userPermissions = userService.getUserPermissions(user);
        PrivateKey privateKey = loadPrivateKey("src/main/resources/privateKey.pem");
        System.out.println(privateKey);
        return Jwt.issuer("the-dark-lord")
            .upn(user.getUsername())
            .groups(userPermissions)
            .expiresIn(86400)
            .claim(Claims.email_verified.name(), user.getEmail())
            .sign(privateKey);
    } 
}
