package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.dreamjob.DatasourceConfiguration;
import ru.job4j.dreamjob.model.User;

import java.util.Optional;
import java.util.Properties;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Sql2oUserRepositoryTest {
    private static Sql2oUserRepository sql2oUserRepository;

    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oUserRepository.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        sql2oUserRepository = new Sql2oUserRepository(sql2o);
    }

    @AfterEach
    public void clearVacancies() {
        var users = sql2oUserRepository.findAll();
        for (var user : users) {
            sql2oUserRepository.deleteById(user.getId());
        }
    }

    @Test
    public void whenSaveThenGetSame() {
        String name = "Vasya";
        String email = "i.Abramov@mail.ru";
        String password = "555";
        var optionalUser =
                sql2oUserRepository.save(new User(5, name, email, password));
        assertTrue(optionalUser.isPresent(), "Пользователь не был сохранен");
        User savedUser = optionalUser.orElseThrow();
        var expected = sql2oUserRepository.findByEmailAndPassword(savedUser.getEmail(), savedUser.getPassword());
        assertTrue(expected.isPresent(), "Пользователь не найден");
        assertThat(optionalUser).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    public void whenUserAlreadyExistsThenException() {
        String name = "Vasya";
        String email = "i.Abramov@mail.ru";
        String password = "555";
        User vasya = new User(25, name, email, password);
        sql2oUserRepository.save(vasya);
        assertThat(sql2oUserRepository.save(vasya)).isEqualTo(Optional.empty());
    }
}