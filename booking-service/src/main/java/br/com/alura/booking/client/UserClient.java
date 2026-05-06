package br.com.alura.booking.client;

import br.com.alura.booking.exception.RegraDeNegocioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@Component
public class UserClient {

    private final RestTemplate restTemplate;

    @Autowired
    private Environment env;

    public UserClient() {
        this.restTemplate = new RestTemplate();
    }

    public void checkUser(Long userId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            String credentials = env.getProperty("APP_ADMIN_USER", "admin") + ":" +
                                 env.getProperty("APP_ADMIN_PASSWORD", "admin123");
            headers.set("Authorization", "Basic " +
                Base64.getEncoder().encodeToString(credentials.getBytes()));

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Object> response = restTemplate.exchange(
                "http://localhost:8081/api/v1/usuarios/" + userId,
                HttpMethod.GET,
                entity,
                Object.class
            );

            if (response.getBody() == null) {
                throw new RegraDeNegocioException("Usuário não encontrado.");
            }
        } catch (RegraDeNegocioException e) {
            throw e;
        } catch (Exception e) {
            throw new RegraDeNegocioException("Erro ao comunicar com o User Service ou usuário inexistente.");
        }
    }
}
