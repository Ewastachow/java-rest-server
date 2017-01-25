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
import pl.edu.agh.kis.florist.dao.UserDAO;
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

//		final String FOLDER_PATH = "/files/:path";
//		final String File_PATH = "/files/:path";
		final String FOLDER_CONTENT_PATH = "/files/:path/list_folder_content";
		final String FOLDER_PATH = "/files/:path/get_meta_data";
		final String FOLDER_DELETE_PATH = "/files/:path/delete";
		final String FOLDER_MOVE_PATH = "/files/:path/move";
		final String FOLDER_CREATE_PATH = "/files/:path/create_directory";
		final String FOLDER_RENAME_PATH = "/files/:path/rename";
		final String FILE_POST_PATH = "/files/:path/upload";
		final String FILE_DOWNLOAD_PATH = "/files/:path/download";

		final String USER_CREATE_PATH = "/users/create_user";



		final FileController fileController = new FileController();
		final BookController bookController = new BookController(new AuthorDAO(),new BookDAO());
		final UserController userController = new UserController(new UserDAO());

		final Gson gson = new Gson();
		final ResponseTransformer json = gson::toJson;

		//Changes port on which server listens
		port(4567);

		//registers filter before processing of any request with special metothod stated below
		//this method is run to log request with logger
		//but similar method can be used to check user authorisation
		before("/*/", (req, res) -> {
			info(req);
		});

		//__________BOOKS_&_AUTHORS__________

		//registers HTTP GET on resource /authors
		//and delegates processing into BookController
		get(AUTHORS_PATH, (request, response) -> {
			return bookController.handleAllAuthors(request, response);
		}, json);

		//registers HTTP POST on resource /authors
		//and delegates processing into BookController
		post(AUTHORS_PATH, (request, response) -> {
			return bookController.handleCreateNewAuthor(request,response);
		}, json);

		//registers HTTP GET on resource /books
		//and delegates processing into BookController
		get(BOOKS_PATH, (request, response) -> {
			return bookController.hadleAllBooks(request,response);
		}, json);

		//registers HTTP GET on resource /books/{bookId}
		//and delegates processing into BookController
		get(BOOK_PATH, (request, response) -> {
			return bookController.handleSingleBook(request,response);
		}, json);




		//__________FOLDERS_&_FILES__________

		//registers HTTP GET on resource /files/{path}/list_folder_content
		//and delegates processing into FileController
		get(FOLDER_CONTENT_PATH, (request, response) -> {
			return fileController.handleFolderContent(request,response);
		}, json);

		//registers HTTP GET on resource /files/{path}/get_meta_data
		//and delegates processing into FileController
		get(FOLDER_PATH, (request, response) -> {
			return fileController.handleFolderData(request,response);
		}, json);

		//registers HTTP GET on resource /files/{path}/delete
		//and delegates processing into FileController
		delete(FOLDER_DELETE_PATH, (request, response) -> {
			return fileController.handleDeleteFolder(request,response);
		}, json);

		//registers HTTP GET on resource /files/{path}/move
		//and delegates processing into FileController
		put(FOLDER_MOVE_PATH, (request, response) -> {
			return fileController.handleMoveFolder(request,response);
		}, json);

		//registers HTTP GET on resource /files/{path}/move
		//and delegates processing into FileController
		put(FOLDER_CREATE_PATH, (request, response) -> {
			return fileController.handleCreateFolder(request,response);
		}, json);

		post(FILE_POST_PATH, "multipart/form-data", (request, response) -> {
			return fileController.handlePostFile(request,response);
		}, json);

		//registers HTTP GET on resource /files/{path}/move
		//and delegates processing into FileController
		put(FOLDER_RENAME_PATH, (request, response) -> {
			return fileController.handleRenameFolder(request,response);
		}, json);

		get(FILE_DOWNLOAD_PATH, (request, response) -> {
			return fileController.handleDownloadFile(request,response);
		}, json);




		//__________USER__________

		post(USER_CREATE_PATH, (request, response) -> {
			return userController.handleCreateNewUser(request,response);
		}, json);

//		get("/user/access", (request, response) -> {
//			return userController.handleUserAccess(request, response);
//		}, json);


//		get(FOLDER_PATH+"/get_meta_data", (request, response) -> {
//			return folderController.handleSingleFolder(request,response);
//		}, json);




		//__________EXCEPTIONS__________
		//handleSingleBook can throw ParameterFromatException which will be processed
		//gracefully instead of 500 Internal Server Error
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
			//cos innego jeszcze?
			ex.printStackTrace();
		});

	}

//	public static void main(String[] args) {
//
//		final String AUTHORS_PATH = "/authors";
//		final String BOOKS_PATH = "/books";
//		final String BOOK_PATH = "/books/:bookid";
//
//		final Gson gson = new Gson();
//		final ResponseTransformer json = gson::toJson;
//
//		final BookController bookController = new BookController(new AuthorDAO(),new BookDAO());
//		//Changes port on which server listens
//		port(4567);
//
//		//registers filter before processing of any request with special metothod stated below
//		//this method is run to log request with logger
//		//but similar method can be used to check user authorisation
//		before("/*/", (req, res) -> {
//			info(req);
//		});
//
//		//registers HTTP GET on resource /atuthors
//		//and delegates processing into BookController
//		get(AUTHORS_PATH, (request, response) -> {
//			return bookController.handleAllAuthors(request, response);
//		}, json);
//
//		//registers HTTP POST on resource /atuthors
//		//and delegates processing into BookController
//		post(AUTHORS_PATH, (request, response) -> {
//			return bookController.handleCreateNewAuthor(request,response);
//		}, json);
//
//		//registers HTTP GET on resource /books
//		//and delegates processing into BookController
//		get(BOOKS_PATH, (request, response) -> {
//			return bookController.hadleAllBooks(request,response);
//		}, json);
//
//		//registers HTTP GET on resource /books/{bookId}
//		//and delegates processing into BookController
//		get(BOOK_PATH, (request, response) -> {
//			return bookController.handleSingleBook(request,response);
//		}, json);
//
//		//handleSingleBook can throw ParameterFromatException which will be processed
//		//gracefully instead of 500 Internal Server Error
//		exception(ParameterFormatException.class,(ex,request,response) -> {
//			response.status(403);
//			response.body(gson.toJson(new ParameterFormatError(request.params())));
//		});
//
//	}

	private static void info(Request req) {
		LOGGER.info("{}", req);
	}

}
