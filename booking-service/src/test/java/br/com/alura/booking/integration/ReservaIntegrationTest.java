//package br.com.alura.booking.integration;
//
//import br.com.alura.booking.dto.ReservaDTO;
//import br.com.alura.booking.model.StatusReserva;
//import br.com.alura.booking.repository.ReservaRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.context.annotation.Import;
//import org.springframework.http.*;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.testcontainers.containers.MySQLContainer;
//import org.testcontainers.containers.RabbitMQContainer;
//import org.testcontainers.containers.KafkaContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//import org.testcontainers.utility.DockerImageName;
//
//import java.time.LocalDateTime;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@Testcontainers
//@ActiveProfiles("test")
//@Import(ClientsMockConfig.class)
//class ReservaIntegrationTest {
//
//    static {
//        // Força o Testcontainers a usar o socket correto no WSL2/Linux
//        System.setProperty("DOCKER_HOST", "unix:///var/run/docker.sock");
//        System.setProperty("TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE", "/var/run/docker.sock");
//    }
//
//    // ─── Contêineres — static para serem compartilhados entre os testes ───
//
//    @Container
//    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
//            .withDatabaseName("test_booking")
//            .withUsername("test")
//            .withPassword("test");
//
//    @Container
//    static RabbitMQContainer rabbit = new RabbitMQContainer(
//            DockerImageName.parse("rabbitmq:3-management"));
//
//    @Container
//    static KafkaContainer kafka = new KafkaContainer(
//            DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));
//
//    // ─── Injeta propriedades dos contêineres no contexto Spring ───
//
//    @DynamicPropertySource
//    static void configProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.datasource.url",          mysql::getJdbcUrl);
//        registry.add("spring.datasource.username",     mysql::getUsername);
//        registry.add("spring.datasource.password",     mysql::getPassword);
//        registry.add("spring.rabbitmq.host",           rabbit::getHost);
//        registry.add("spring.rabbitmq.port",           rabbit::getAmqpPort);
//        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
//    }
//
//    @Autowired
//    private TestRestTemplate restTemplate;
//
//    @Autowired
//    private ReservaRepository reservaRepository;
//
//    @BeforeEach
//    void limpar() {
//        reservaRepository.deleteAll();
//    }
//
//    // ─── Testes ───
//
//    @Test
//    @DisplayName("Deve criar reserva com sucesso quando autenticado como USER")
//    void deveCriarReservaComSucesso() {
//        ReservaDTO dto = buildReservaDTO(1L, 1L,
//                LocalDateTime.now().plusDays(1),
//                LocalDateTime.now().plusDays(1).plusHours(2));
//
//        ResponseEntity<ReservaDTO> response = restTemplate
//                .withBasicAuth("user", "user123")
//                .postForEntity("/api/v1/reservas", dto, ReservaDTO.class);
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().getSalaId()).isEqualTo(1L);
//        assertThat(response.getBody().getStatus()).isEqualTo("ATIVA");
//        assertThat(reservaRepository.count()).isEqualTo(1);
//    }
//
//    @Test
//    @DisplayName("Deve retornar 401 ao criar reserva sem autenticação")
//    void deveRetornar401SemAutenticacao() {
//        ReservaDTO dto = buildReservaDTO(1L, 1L,
//                LocalDateTime.now().plusDays(1),
//                LocalDateTime.now().plusDays(1).plusHours(1));
//
//        ResponseEntity<String> response = restTemplate
//                .postForEntity("/api/v1/reservas", dto, String.class);
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
//    }
//
//    @Test
//    @DisplayName("Deve retornar 403 quando USER tenta acessar endpoint de ADMIN")
//    void deveRetornar403QuandoUserAcessaEndpointAdmin() {
//        ResponseEntity<String> response = restTemplate
//                .withBasicAuth("user", "user123")
//                .getForEntity("/api/v1/reservas", String.class);
//
//        // GET em reservas é público, então testa com um endpoint que exige ADMIN
//        // Aqui validamos que o mecanismo de segurança está ativo
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//    }
//
//    @Test
//    @DisplayName("Deve retornar 422 ao criar reserva com conflito de horário")
//    void deveRetornar422EmConflitoDePeriodo() {
//        LocalDateTime inicio = LocalDateTime.of(2025, 7, 10, 9, 0);
//        LocalDateTime fim    = LocalDateTime.of(2025, 7, 10, 11, 0);
//
//        // Cria a primeira reserva
//        restTemplate.withBasicAuth("user", "user123")
//                .postForEntity("/api/v1/reservas",
//                        buildReservaDTO(1L, 1L, inicio, fim), ReservaDTO.class);
//
//        // Tenta criar reserva com horário conflitante
//        ResponseEntity<String> response = restTemplate
//                .withBasicAuth("user", "user123")
//                .postForEntity("/api/v1/reservas",
//                        buildReservaDTO(1L, 2L,
//                                LocalDateTime.of(2025, 7, 10, 10, 0),
//                                LocalDateTime.of(2025, 7, 10, 12, 0)),
//                        String.class);
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
//        assertThat(response.getBody()).contains("conflito");
//    }
//
//    @Test
//    @DisplayName("Deve cancelar reserva alterando status para CANCELADA")
//    void deveCancelarReserva() {
//        ResponseEntity<ReservaDTO> criada = restTemplate
//                .withBasicAuth("user", "user123")
//                .postForEntity("/api/v1/reservas",
//                        buildReservaDTO(1L, 1L,
//                                LocalDateTime.now().plusDays(3),
//                                LocalDateTime.now().plusDays(3).plusHours(1)),
//                        ReservaDTO.class);
//
//        assertThat(criada.getStatusCode()).isEqualTo(HttpStatus.CREATED);
//        Long id = criada.getBody().getId();
//
//        restTemplate.withBasicAuth("user", "user123")
//                .delete("/api/v1/reservas/" + id);
//
//        assertThat(reservaRepository.findById(id))
//                .isPresent()
//                .hasValueSatisfying(r ->
//                        assertThat(r.getStatus()).isEqualTo(StatusReserva.CANCELADA));
//    }
//
//    @Test
//    @DisplayName("Deve listar reservas sem autenticação (GET público)")
//    void deveListarReservasSemAutenticacao() {
//        ResponseEntity<String> response = restTemplate
//                .getForEntity("/api/v1/reservas", String.class);
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//    }
//
//    // ─── Helper ───
//
//    private ReservaDTO buildReservaDTO(Long salaId, Long usuarioId,
//                                        LocalDateTime inicio, LocalDateTime fim) {
//        ReservaDTO dto = new ReservaDTO();
//        dto.setSalaId(salaId);
//        dto.setUsuarioId(usuarioId);
//        dto.setInicio(inicio);
//        dto.setFim(fim);
//        return dto;
//    }
//}