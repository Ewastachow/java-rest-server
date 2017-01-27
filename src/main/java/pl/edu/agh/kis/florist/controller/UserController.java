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
 * Class controling using database for users
 * @author EwaStachow
 * @version v2.0
 * //@exception/@throws InvalidUserNameException, AuthorizationException
 */
public class UserController {

    private static final int CREATED = 201;

    private Connection connection;

    private UsersDao usersDao;
    private SessionDataDao sessionDataDao;
    private final Gson gson = new Gson();

    /**
     * Controller no parameter constructor
     * It's setting needed daos with configuration
     */
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

    /**
     * Method checking rights to access and seting it
     * @param request REST obtained request - needed headers "userName" and "hashedPassword"
     * @param response REST regived response status 200 and cookie "session
     * @return String - authorisation session id
     */
    public Object handleUserAccess(Request request, Response response) {
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

    /**
     * Method creating new user
     * @param request REST obtained request - needed Json body with user_name and user_pass
     * @param response REST regived response
     * @return Users object
     */
    public Object handleCreateNewUser(Request request, Response response) {
        Users users = gson.fromJson(request.body(), Users.class);
        Users user = new Users(null,users.getUserName(),users.getUserName(),createNewHashedPassword(users.getHashedPassword()));
        usersDao.insert(user);
        response.status(CREATED);
        return user;
    }

    /**
     * Method checking rights to access to /files
     * @param request REST obtained request - needed cookie "session"
     * @param response REST regived response
     */
    void accessAutorisation(Request request, Response response){
        try{
            String sessionId = request.cookie("session");
            SessionData session = sessionDataDao.fetchBySessionId(sessionId).get(0);
            Timestamp time = new Timestamp(System.currentTimeMillis());
            Timestamp latestTime = new Timestamp(session.getLastAccessed().getTime()+60*1000);
            if(time.before(latestTime)){
                sessionDataDao.delete(session);
                sessionDataDao.insert(new SessionData(session.getSessionId(), session.getUserId(), time));
            }else {
                sessionDataDao.delete(session);
                response.status(401);
                throw new AuthorizationException();
            }
        }catch(Exception e) {
            response.status(401);
            throw new AuthorizationException();
        }
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
