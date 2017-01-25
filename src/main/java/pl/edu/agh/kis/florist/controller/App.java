package pl.edu.agh.kis.florist.controller;

import static spark.Spark.*;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import pl.edu.agh.kis.florist.dao.AuthorDAO;
import pl.edu.agh.kis.florist.dao.BookDAO;
import pl.edu.agh.kis.florist.exceptions.InvalidPathException;
import pl.edu.agh.kis.florist.exceptions.ParameterFormatException;
import pl.edu.agh.kis.florist.model.ParameterFormatError;
import spark.Request;
import spark.ResponseTransformer;

public class App {

	final static private Logger LOGGER = LoggerFactory.getILoggerFactory().getLogger("requests");

	public static void main(String[] args) {

		final String AUTHORS_PATH = "/authors";
		final String BOOKS_PATH = "/books";
		final String BOOK_PATH = "/books/:bookid";

		final String FOLDER_CONTENT_PATH = "/files/:path/list_folder_content";
		final String FOLDER_PATH = "/files/:path/get_meta_data";
		final String FOLDER_DELETE_PATH = "/files/:path/delete";
		final String FOLDER_MOVE_PATH = "/files/:path/move";
		final String FOLDER_CREATE_PATH = "/files/:path/create_directory";
		final String FOLDER_RENAME_PATH = "/files/:path/rename";
		final String FILE_POST_PATH = "/files/:path/upload";
		final String FILE_DOWNLOAD_PATH = "/files/:path/download";

		final String USER_CREATE_PATH = "/users/create_user";
		final String USER_ACCESS_PATH = "/users/access";



		final FileController fileController = new FileController();
		final BookController bookController = new BookController(new AuthorDAO(),new BookDAO());
		final UserController userController = new UserController();

		final Gson gson = new Gson();
		final ResponseTransformer json = gson::toJson;

		port(4567);

		//registers filter before processing of any request with special metothod stated below
		//this method is run to log request with logger
		//but similar method can be used to check user authorisation
		before("/*/", (req, res) -> {
			info(req);
		});

		//__________BOOKS_&_AUTHORS__________

		get(AUTHORS_PATH, (request, response) -> {
			return bookController.handleAllAuthors(request, response);
		}, json);

		post(AUTHORS_PATH, (request, response) -> {
			return bookController.handleCreateNewAuthor(request,response);
		}, json);

		get(BOOKS_PATH, (request, response) -> {
			return bookController.hadleAllBooks(request,response);
		}, json);

		get(BOOK_PATH, (request, response) -> {
			return bookController.handleSingleBook(request,response);
		}, json);




//		__________FOLDERS_&_FILES__________

		get(FOLDER_CONTENT_PATH, (request, response) -> {
			return fileController.handleFolderContent(request,response);
		}, json);

		get(FOLDER_PATH, (request, response) -> {
			return fileController.handleFolderData(request,response);
		}, json);

		delete(FOLDER_DELETE_PATH, (request, response) -> {
			return fileController.handleDeleteFolder(request,response);
		}, json);

		put(FOLDER_MOVE_PATH, (request, response) -> {
			return fileController.handleMoveFolder(request,response);
		}, json);

		put(FOLDER_CREATE_PATH, (request, response) -> {
			return fileController.handleCreateFolder(request,response);
		}, json);

		post(FILE_POST_PATH, "multipart/form-data", (request, response) -> {
			return fileController.handlePostFile(request,response);
		}, json);

		put(FOLDER_RENAME_PATH, (request, response) -> {
			return fileController.handleRenameFolder(request,response);
		}, json);

		get(FILE_DOWNLOAD_PATH, (request, response) -> {
			return fileController.handleDownloadFile(request,response);
		}, json);

//		__________USER__________

		post(USER_CREATE_PATH, "multipart/form-data", (request, response) -> {
			return userController.handleCreateNewUser(request,response);
		}, json);

		get(USER_ACCESS_PATH, (request, response) -> {
			return userController.handleUserAccess(request,response);
		}, json);



//		__________EXCEPTIONS__________
		exception(ParameterFormatException.class,(ex,request,response) -> {
			response.status(403);
			response.body(gson.toJson(new ParameterFormatError(request.params())));
		});

		exception(InvalidPathException.class,(ex, request, response) -> {
			response.status(405);
//			response.body(gson.toJson(new ParameterFormatError(request.params()))); // TODO: 25.01.17 Zaimplementować tu ale nie wiem co
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
