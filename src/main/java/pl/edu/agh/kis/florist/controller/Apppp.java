package pl.edu.agh.kis.florist.controller;


import static spark.Spark.*;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import pl.edu.agh.kis.florist.dao.*;
import pl.edu.agh.kis.florist.exceptions.InvalidPathException;
import pl.edu.agh.kis.florist.exceptions.ParameterFormatException;
import pl.edu.agh.kis.florist.model.ParameterFormatError;
import spark.Request;
import spark.ResponseTransformer;

public class Apppp {

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

		final String USER_CREATE_PATH = "/users/create_user";



		final FileController fileController = new FileController(new FileDAO());
		final BookController bookController = new BookController(new AuthorDAO(),new BookDAO());
//		final FolderController folderController = new FolderController(new FolderDAO());
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
		});

	}

	private static void info(Request req) {
		LOGGER.info("{}", req);
	}

}
//
//		post("/upload", "multipart/form-data", (request, response) -> {
//
//		String location = "image";          // the directory location where files will be stored
//		long maxFileSize = 100000000;       // the maximum size allowed for uploaded files
//		long maxRequestSize = 100000000;    // the maximum size allowed for multipart/form-data requests
//		int fileSizeThreshold = 1024;       // the size threshold after which files will be written to disk
//
//		MultipartConfigElement multipartConfigElement = new MultipartConfigElement(
//			 location, maxFileSize, maxRequestSize, fileSizeThreshold);
//		 request.raw().setAttribute("org.eclipse.jetty.multipartConfig",
//			 multipartConfigElement);
//
//		Collection<Part> parts = request.raw().getParts();
//		for (Part part : parts) {
//		   System.out.println("Name: " + part.getName());
//		   System.out.println("Size: " + part.getSize());
//		   System.out.println("Filename: " + part.getSubmittedFileName());
//		}
//
//		String fName = request.raw().getPart("file").getSubmittedFileName();
//		System.out.println("Title: " + request.raw().getParameter("title"));
//		System.out.println("File: " + fName);
//
//		Part uploadedFile = request.raw().getPart("file");
//		Path out = Paths.get("image/" + fName);
//		try (final InputStream in = uploadedFile.getInputStream()) {
//		   Files.copy(in, out);
//		   uploadedFile.delete();
//		}
//		// cleanup
//		multipartConfigElement = null;
//		parts = null;
//		uploadedFile = null;
//
//		return "OK";
//		});

//http://stackoverflow.com/questions/34746900/sparkjava-upload-file-didt-work-in-spark-java-framework
//http://stackoverflow.com/questions/27244780/how-download-file-using-java-spark
