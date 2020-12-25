package ru.reybos.grabber.store;

import ru.reybos.grabber.parse.SqlRuParse;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PsqlStore implements Store, AutoCloseable {
    private Connection cnn;
    private static final Logger LOG = LoggerFactory.getLogger(PsqlStore.class.getName());

    public PsqlStore(Properties cfg) throws SQLException {
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        cnn = DriverManager.getConnection(
                cfg.getProperty("jdbc.url"),
                cfg.getProperty("jdbc.username"),
                cfg.getProperty("jdbc.password")
        );
    }

    @Override
    public void save(Post post) throws SQLException {
        String sql = "insert into post(link, title, date, description) values (?, ?, ?, ?)";
        try (PreparedStatement statement = cnn.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS
        )) {
            statement.setString(1, post.getLink());
            statement.setString(2, post.getTitle());
            statement.setTimestamp(3, new Timestamp(post.getDate().getTimeInMillis()));
            statement.setString(4, post.getDescription());
            statement.execute();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    post.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public List<Post> getAll() throws SQLException {
        List<Post> posts = new ArrayList<>();
        try (PreparedStatement statement = cnn.prepareStatement("select * from post")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Calendar date = new GregorianCalendar(new Locale("ru"));
                    date.setTime(resultSet.getDate("date"));
                    posts.add(new Post(
                            resultSet.getInt("id"),
                            resultSet.getString("link"),
                            resultSet.getString("title"),
                            date,
                            resultSet.getString("description")
                    ));
                }
            }
        }
        return posts;
    }

    @Override
    public Post findById(String id) throws SQLException {
        String sql = "select * from post where id = ?";
        try (PreparedStatement statement = cnn.prepareStatement(sql)) {
            statement.setInt(1, Integer.parseInt(id));
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Calendar date = new GregorianCalendar(new Locale("ru"));
                    date.setTime(resultSet.getDate("date"));
                    return new Post(
                            resultSet.getInt("id"),
                            resultSet.getString("link"),
                            resultSet.getString("title"),
                            date,
                            resultSet.getString("description")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }

    public static void main(String[] args) {
        Properties properties = new Properties();
        try {
            properties.load(new FileReader("./src/main/resources/grabber.properties"));
        } catch (FileNotFoundException e) {
            LOG.error("Не найден файл настроек", e);
            return;
        } catch (IOException e) {
            LOG.error("Ошибка чтения файла настроек", e);
            return;
        }
        SqlRuParse sqlRuParse = new SqlRuParse();
        Post post;
        try {
            String link = "https://www.sql.ru/forum/1331624/razrabotchik-ms-sql";
            post = sqlRuParse.detail(link);
        } catch (IOException e) {
            LOG.error("Ошибка чтения страницы", e);
            return;
        }
        try (PsqlStore store = new PsqlStore(properties)) {
            store.save(post);
            Post postFromDB = store.findById(String.valueOf(post.getId()));
            System.out.println(postFromDB.getLink());
        } catch (Exception e) {
            LOG.error("Ошибка при работе с базой данных", e);
        }
        System.out.println();
        try (PsqlStore store = new PsqlStore(properties)) {
            store.save(post);
        } catch (Exception e) {
            LOG.error("Повторное сохранение поста", e);
        }
        System.out.println();
        try (PsqlStore store = new PsqlStore(properties)) {
            List<Post> posts = store.getAll();
            for (int i = 0; i < posts.size(); i++) {
                System.out.println(posts.get(i).getLink());
            }
        } catch (Exception e) {
            LOG.error("Ошибка при работе с базой данных", e);
        }
    }
}