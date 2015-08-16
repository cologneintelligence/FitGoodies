package ${package};

import java.util.ArrayList;

public final class Bookshelf {
    private final ArrayList<Book> books = new ArrayList<Book>();

    public Integer addBook(final Book b) {
        if (b.isValid()) {
            for (Book book : books) {
                if (book.getIsbn().equals(b.getIsbn())) {
                    return null;
                }
            }

            books.add(b);
            return books.size() - 1;
        } else {
            return null;
        }
    }

    public Book get(final Integer lookup) {
        return books.get(lookup);
    }

    public int bookCount() {
        return books.size();
    }
}
