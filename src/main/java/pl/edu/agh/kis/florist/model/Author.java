package pl.edu.agh.kis.florist.model;

import pl.edu.agh.kis.florist.db.tables.pojos.Authors;

public class Author extends Authors {

	public Author(Authors author) {
		super(author);
	}

	public Author(Integer id, String firstName, String lastName) {
		super(id, firstName, lastName);
	}
	
	public Author(String firstName, String lastName) {
		super(null, firstName, lastName);
	}

	private static final long serialVersionUID = -7821816371758851390L;
}
