package ${package};

import de.cologneintelligence.fitgoodies.ColumnFixture;

public final class BookFixture extends ColumnFixture {
    public String author;
    public ISBN isbn;
    public StringBuffer title;
    public float price;
    public Integer lookup;

    public Integer save() {
        return FixtureObjects.SHELF.addBook(
                new Book(title, author, isbn, price));
    }

    public boolean valid() {
        return new Book(title, author, isbn, price).isValid();
    }

    public boolean lookup() {
        try {
            Book book = FixtureObjects.SHELF.get(lookup);
            author = book.getAuthor();
            isbn = book.getIsbn();
            title = book.getTitle();
            price = book.getPrice();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String author() { return author; }
    public StringBuffer title() { return title; }
    public float price() { return price; }
    public ISBN isbn() { return isbn; }
}
