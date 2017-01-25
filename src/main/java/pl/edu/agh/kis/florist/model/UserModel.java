package pl.edu.agh.kis.florist.model;
import pl.edu.agh.kis.db.tables.pojos.Users;

/**
 * Created by yevvye on 16.01.17.
 */
public class UserModel extends Users {
    private static final long serialVersionUID = -7983312207667354842L;

    public UserModel(Users value) {
        super(value);
    }

    public UserModel(Integer id, String userName, String displayName, String hashedPassword) {
        super(id, userName, displayName, hashedPassword);
    }
}
