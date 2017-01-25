package pl.edu.agh.kis.florist.controller;

import com.google.gson.Gson;
import pl.edu.agh.kis.florist.dao.UserDAO;
import pl.edu.agh.kis.florist.model.UserModel;
import spark.Request;
import spark.Response;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Created by yevvye on 16.01.17.
 */
public class UserController {

    private static final int CREATED = 201;
    private final UserDAO userRepository;
    private final Gson gson = new Gson();

    public UserController(UserDAO userRepository) {
        this.userRepository = userRepository;
    }

    public Object handleUserAccess(Request request, Response response) {
        return null;
    }

    public Object handleCreateNewUser(Request request, Response response) {
        UserModel userModel = gson.fromJson(request.body(), UserModel.class);
        UserModel result = userRepository.store(userModel);
        response.status(CREATED);
        return result;
    }

    /*public static String createNewHashedPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
    public static boolean checkPassword(String candidatePassword,String storedHashedPassword) {
        return BCrypt.checkpw(candidatePassword, storedHashedPassword);
    }*/
}
