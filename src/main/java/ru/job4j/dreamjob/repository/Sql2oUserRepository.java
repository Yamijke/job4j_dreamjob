package ru.job4j.dreamjob.repository;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.sql2o.Sql2o;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.dreamjob.model.User;

import java.util.Collection;
import java.util.Optional;

@Repository
@Primary
public class Sql2oUserRepository implements UserRepository {
    private static final Logger logger = LoggerFactory.getLogger(Sql2oUserRepository.class);

    private final Sql2o sql2o;

    public Sql2oUserRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public Optional<User> save(User user) {
        try (var connection = sql2o.open()) {
            var sql = """
                    INSERT INTO users(email, name, password)
                    VALUES (:email, :name, :password)
                    """;
            var query = connection.createQuery(sql, true)
                    .addParameter("email", user.getEmail())
                    .addParameter("name", user.getName())
                    .addParameter("password", user.getPassword());
            int generatedId = query.executeUpdate().getKey(Integer.class);
            user.setId(generatedId);
            return Optional.of(user);
        } catch (Exception e) {
            logger.error("Пользователь с такими данными уже существует" + e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmailAndPassword(String email, String password) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery(
                    "SELECT * FROM users WHERE email = :email and password = :password;"
            );
            query.addParameter("email", email);
            query.addParameter("password", password);
            var user =
                    query.setColumnMappings(User.COLUMN_MAPPING)
                            .executeAndFetchFirst(User.class);
            return Optional.ofNullable(user);
        }
    }

    public Collection<User> findAll() {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("SELECT * FROM users");
            return query.setColumnMappings(User.COLUMN_MAPPING).executeAndFetch(User.class);
        }
    }

    public boolean deleteById(int id) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("DELETE FROM users WHERE id = :id");
            query.addParameter("id", id);
            var affectedRows = query.executeUpdate().getResult();
            return affectedRows > 0;
        }
    }
}
