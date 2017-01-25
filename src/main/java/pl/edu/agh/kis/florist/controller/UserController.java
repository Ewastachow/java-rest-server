package pl.edu.agh.kis.florist.controller;

import com.google.gson.Gson;
import org.jooq.Configuration;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultConfiguration;
import org.mindrot.jbcrypt.BCrypt;
import pl.edu.agh.kis.florist.db.tables.daos.UsersDao;
import pl.edu.agh.kis.florist.db.tables.pojos.Users;
import spark.Request;
import spark.Response;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by yevvye on 16.01.17.
 */
public class UserController {
    private final String DB_URL = "jdbc:sqlite:test.db";

    private static final int CREATED = 201;

    private Connection connection;
    private Configuration configuration;

    private UsersDao usersDao;
    private final Gson gson = new Gson();

    public UserController() {
        try {
            connection = DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        configuration = new DefaultConfiguration().set(connection).set(SQLDialect.SQLITE);

        usersDao = new UsersDao(configuration);
    }

    public Object handleUserAccess(Request request, Response response) {
        return null;
    }

    public Object handleCreateNewUser(Request request, Response response) {
        Users users = gson.fromJson(request.body(), Users.class);
        Users user = new Users(null,users.getUserName(),users.getUserName(),createNewHashedPassword(users.getHashedPassword()));
        usersDao.insert(user);
        response.status(CREATED);
        return user;
    }

    public static String createNewHashedPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean checkPassword(String candidatePassword,String storedHashedPassword) {
        return BCrypt.checkpw(candidatePassword, storedHashedPassword);
    }
}
