package _blog.demo.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtProperty {
   private String secret;  // read from application.properties 

   public byte[] getSecretBytes() {
       return secret.getBytes();
   }
}
