package pl.edu.agh.kis.florist.controller;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pl.edu.agh.kis.florist.db.tables.pojos.Users;
import spark.Request;

import static org.junit.Assert.*;
import static pl.edu.agh.kis.florist.db.Tables.*;

/**
 * Created by yevvye on 27.01.17.
 */
public class UserControllerTest {

    private final String DB_URL = "jdbc:sqlite:test.db";
    private DSLContext create;

    @Before
    public void setUp() throws Exception {
        create = DSL.using(DB_URL);
        create.deleteFrom(SESSION_DATA).execute();
        create.deleteFrom(USERS).execute();
    }

    @After
    public void tearDown() throws Exception {
        create.close();
    }

    @Test
    public void handleUserAccess() throws Exception {

    }

    @Test
    public void handleCreateNewUser() throws Exception {
        // setup:
        UserController userController = new UserController();
        Request req = new RequestTest();
        String json = "{\n" +
                "\t\"userName\":\"lamalama\",\n" +
                "\t\"hashedPassword\":\"alpaka\"\n" +
                "}";

    }

    @Test
    public void accessAutorisation() throws Exception {

    }

}