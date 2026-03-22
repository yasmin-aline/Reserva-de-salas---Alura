CREATE DATABASE IF NOT EXISTS reserva_salas;
USE reserva_salas;

CREATE TABLE sala (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    capacidade INT NOT NULL,
    ativa BOOLEAN NOT NULL
);

CREATE TABLE usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE reserva (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sala_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    inicio DATETIME NOT NULL,
    fim DATETIME NOT NULL,
    status VARCHAR(50) NOT NULL,
    CONSTRAINT fk_reserva_sala FOREIGN KEY (sala_id) REFERENCES sala(id),
    CONSTRAINT fk_reserva_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);
