package br.com.alura.booking.client;

import br.com.alura.booking.exception.RegraDeNegocioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@Component
public class RoomClient {

    private final RestTemplate restTemplate;
    
    @Autowired
    private Environment env;

    public RoomClient() {
        this.restTemplate = new RestTemplate();
    }

    public void checkRoom(Long roomId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            String credentials = env.getProperty("APP_ADMIN_USER", "admin") + ":" +
                                 env.getProperty("APP_ADMIN_PASSWORD", "admin123");
            headers.set("Authorization", "Basic " +
                Base64.getEncoder().encodeToString(credentials.getBytes()));

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<SalaResponse> response = restTemplate.exchange(
                "http://localhost:8082/api/v1/salas/" + roomId,
                HttpMethod.GET,
                entity,
                SalaResponse.class
            );

            if (response.getBody() == null || !response.getBody().isAtiva()) {
                throw new RegraDeNegocioException("Sala inativa ou inexistente.");
            }
        } catch (RegraDeNegocioException e) {
            throw e;
        } catch (Exception e) {
            throw new RegraDeNegocioException("Erro ao comunicar com o Room Service ou sala inativa/inexistente.");
        }
    }

    public static class SalaResponse {
        private boolean ativa;

        public boolean isAtiva() { return ativa; }
        public void setAtiva(boolean ativa) { this.ativa = ativa; }
    }
}
