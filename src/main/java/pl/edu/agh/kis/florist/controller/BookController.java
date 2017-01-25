package pl.edu.agh.kis.florist.controller;

import java.util.List;

import com.google.gson.Gson;

import pl.edu.agh.kis.florist.dao.AuthorDAO;
import pl.edu.agh.kis.florist.dao.BookDAO;
import pl.edu.agh.kis.florist.exceptions.ParameterFormatException;
import pl.edu.agh.kis.florist.model.Author;
import pl.edu.agh.kis.florist.model.Book;
import spark.Request;
import spark.Response;

//this controller is 
public class BookController {

	private static final int CREATED = 201;
	
	private final AuthorDAO authorRepository;
	private final BookDAO bookRepository;
	private final Gson gson = new Gson();

	//book controller now can be easily tested
	//and thanks to injection of DataAccessObject objects with constructor 
	//can be tested even without database at all - one can stub both interfaces
	//with HashMap-like implementation
	public BookController(AuthorDAO authorRepository, BookDAO bookRepository) {
		this.authorRepository = authorRepository;
		this.bookRepository = bookRepository;
	}

	public Object hadleAllBooks(Request request, Response response) {
		List<Book> result = bookRepository.loadAllBooks();
		return result;
	}

	public Object handleSingleBook(Request request, Response response) {
		try {
			int bookId = Integer.parseInt(request.params("bookid"));
			Book result = bookRepository.loadBookOfId(bookId);
			return result;
		} catch (NumberFormatException ex) {
			throw new ParameterFormatException(ex);
		}
	}

	public Object handleCreateNewAuthor(Request request, Response response) {
		Author author = gson.fromJson(request.body(), Author.class);
		Author result = authorRepository.store(author);
		response.status(CREATED);
		return result;
	}

	public Object handleAllAuthors(Request request, Response response) {
		List<Author> result = authorRepository.loadAllAuthors();
		return result;
	}
	
	

}
