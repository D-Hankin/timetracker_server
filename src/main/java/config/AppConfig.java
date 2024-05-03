package config;

import org.eclipse.microprofile.config.inject.ConfigProperties;

@ConfigProperties(prefix = "app")
public class AppConfig {

    String jwtIssuer;

    public String getJwtIssuer() {
        return jwtIssuer;
    }

    public void setJwtIssuer(String jwtIssuer) {
        this.jwtIssuer = jwtIssuer;
    }
}
