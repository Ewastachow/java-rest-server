package pl.edu.agh.kis.florist.dao;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import pl.edu.agh.kis.db.tables.daos.UsersDao;
import pl.edu.agh.kis.db.tables.records.UsersRecord;
import pl.edu.agh.kis.florist.model.UserModel;

import static pl.edu.agh.kis.db.Tables.USERS;

/**
 * Created by yevvye on 16.01.17.
 */
public class UserDAO extends UsersDao{
    private final String DB_URL = "jdbc:sqlite:test.db";

    public UserModel store(UserModel userModel) {
        try (DSLContext create = DSL.using(DB_URL)) {
            UsersRecord record = create.newRecord(USERS,userModel);
            record.store();
            return record.into(UserModel.class);
        }
    }


}
