package pl.edu.agh.kis.florist.controller;

import static spark.Spark.*;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import pl.edu.agh.kis.florist.exceptions.AuthorizationException;
import pl.edu.agh.kis.florist.exceptions.InvalidPathException;
import pl.edu.agh.kis.florist.exceptions.InvalidUserNameException;
import pl.edu.agh.kis.florist.exceptions.ParameterFormatException;
import pl.edu.agh.kis.florist.model.AuthorizationError;
import pl.edu.agh.kis.florist.model.InvalidPathError;
import pl.edu.agh.kis.florist.model.InvalidUserNameError;
import pl.edu.agh.kis.florist.model.ParameterFormatError;
import spark.Request;
import spark.ResponseTransformer;

/**
 * Main Server
 * @author EwaStachow
 * @version v2.0
 */
public class App {

	final static private Logger LOGGER = LoggerFactory.getILoggerFactory().getLogger("requests");

	public static void main(String[] args) {

		final String FOLDER_CONTENT_PATH = "/files/:path/list_folder_content";
		final String FOLDER_PATH = "/files/:path/get_meta_data";
		final String FOLDER_DELETE_PATH = "/files/:path/delete";
		final String FOLDER_MOVE_PATH = "/files/:path/move";
		final String FOLDER_CREATE_PATH = "/files/:path/create_directory";
		final String FOLDER_RENAME_PATH = "/files/:path/rename";
		final String FILE_UPLOAD_PATH = "/files/:path/upload";
		final String FILE_DOWNLOAD_PATH = "/files/:path/download";

		final String USER_CREATE_PATH = "/users/create_user";
		final String USER_ACCESS_PATH = "/users/access";

		final FileController fileController = new FileController();
		final UserController userController = new UserController();

		final Gson gson = new Gson();
		final ResponseTransformer json = gson::toJson;

		port(4567);

		before("/files/*", (req, res) -> {
			info(req);
			userController.accessAutorisation(req, res);
		});

        get(FOLDER_PATH, fileController::handleFolderData, json);

		get(FOLDER_CONTENT_PATH, fileController::handleFolderContent, json);

        put(FOLDER_CREATE_PATH, fileController::handleCreateFolder, json);

		put(FOLDER_MOVE_PATH, fileController::handleMoveFolder, json);

		put(FOLDER_RENAME_PATH, fileController::handleRenameFolder, json);

        delete(FOLDER_DELETE_PATH, fileController::handleDeleteFolder, json);

        post(FILE_UPLOAD_PATH, "multipart/form-data", fileController::handleUploadFile, json);

		get(FILE_DOWNLOAD_PATH, fileController::handleDownloadFile, json);

		post(USER_CREATE_PATH, "multipart/form-data", userController::handleCreateNewUser, json);

		get(USER_ACCESS_PATH, userController::handleUserAccess, json);

		exception(AuthorizationException.class, (ex, request, response) -> {
			response.status(403);
			response.body(gson.toJson(new AuthorizationError(request.params())));
		});

		exception(InvalidUserNameException.class, (ex, request, response) -> {
			response.status(400);
			response.body(gson.toJson(new InvalidUserNameError(request.params())));
		});

		exception(InvalidPathException.class, (ex, request, response) -> {
			response.status(405);
			response.body(gson.toJson(new InvalidPathError(request.params())));
 		});

		exception(Exception.class,(ex,req,res)-> {
			System.err.println(String.format("format: %s",req.uri()));
			System.err.println(String.format("params: %s",req.params()));
			System.err.println(String.format("headers: %s",req.headers()));
			System.err.println(String.format("cookies: %s",req.cookies()));
			System.err.println(String.format("body: %s",req.body()));
			ex.printStackTrace();
		});
	}

	private static void info(Request req) {
		LOGGER.info("{}", req);
	}

}
