package pl.edu.agh.kis.florist.controller;

import com.google.gson.Gson;
import org.jooq.Configuration;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultConfiguration;
import org.mindrot.jbcrypt.BCrypt;
import pl.edu.agh.kis.florist.db.tables.daos.SessionDataDao;
import pl.edu.agh.kis.florist.db.tables.daos.UsersDao;
import pl.edu.agh.kis.florist.db.tables.pojos.SessionData;
import pl.edu.agh.kis.florist.db.tables.pojos.Users;
import pl.edu.agh.kis.florist.exceptions.AuthorizationException;
import spark.Request;
import spark.Response;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Created by yevvye on 16.01.17.
 */
class UserController {

    private static final int CREATED = 201;

    private Connection connection;

    private UsersDao usersDao;
    private SessionDataDao sessionDataDao;
    private final Gson gson = new Gson();

    UserController() {
        try {
            String DB_URL = "jdbc:sqlite:test.db";
            connection = DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Configuration configuration = new DefaultConfiguration().set(connection).set(SQLDialect.SQLITE);

        usersDao = new UsersDao(configuration);
        sessionDataDao = new SessionDataDao(configuration);
    }

    Object handleUserAccess(Request request, Response response) {
        Users user = new Users(null, request.headers("userName"), request.headers("userName"), request.headers("hashedPassword"));
        Users thisUser = usersDao.fetchByUserName(user.getUserName()).get(0);
        if(checkPassword(user.getHashedPassword(), thisUser.getHashedPassword())){
            //thisUser.getHashedPassword().equals(createNewHashedPassword(user.getHashedPassword()))){
            Timestamp time = new Timestamp(System.currentTimeMillis());
            SessionData sessionData = new SessionData(generateRandomString(10), thisUser.getId(), time);
            try{
                SessionData last = sessionDataDao.fetchByUserId(thisUser.getId()).get(0);
                sessionDataDao.delete(last);
            }catch (Exception e){
            }
            sessionDataDao.insert(sessionData);
            response.status(200);
            response.cookie("session", sessionData.getSessionId(), 60000);
            return sessionData.getSessionId();
        }else{
            throw new AuthorizationException();
        }
    }

    Object handleCreateNewUser(Request request, Response response) {
        Users users = gson.fromJson(request.body(), Users.class);
        Users user = new Users(null,users.getUserName(),users.getUserName(),createNewHashedPassword(users.getHashedPassword()));
        usersDao.insert(user);
        response.status(CREATED);
        return user;
    }

    private static String createNewHashedPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private static boolean checkPassword(String candidatePassword,String storedHashedPassword) {
        return BCrypt.checkpw(candidatePassword, storedHashedPassword);
    }

    private String generateRandomString(int len){
        char[] str = new char[100];
        for (int i = 0; i < len; i++){
            str[i] = (char) (((int)(Math.random() * 26)) + (int)'A');
        }
        return (new String(str, 0, len));
    }
}
