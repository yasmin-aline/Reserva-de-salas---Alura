package br.com.alura.booking.integration;

import br.com.alura.booking.client.RoomClient;
import br.com.alura.booking.client.UserClient;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class ClientsMockConfig {

    @Bean
    @Primary
    public RoomClient roomClient() {
        RoomClient mock = Mockito.mock(RoomClient.class);
        Mockito.doNothing().when(mock).checkRoom(Mockito.anyLong());
        return mock;
    }

    @Bean
    @Primary
    public UserClient userClient() {
        UserClient mock = Mockito.mock(UserClient.class);
        Mockito.doNothing().when(mock).checkUser(Mockito.anyLong());
        return mock;
    }
}