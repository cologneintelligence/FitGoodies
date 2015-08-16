package ${package};

public final class Book {
    private StringBuffer title;
    private String author;
    private ISBN isbn;
    private float price;

    public Book(final StringBuffer bookTitle, final String bookAuthor,
            final ISBN bookIsbn, final float bookPrice) {
        this.title = bookTitle;
        this.author = bookAuthor;
        this.isbn = bookIsbn;
        this.price = bookPrice;
    }

    public StringBuffer getTitle() {
        return title;
    }

    public void setTitle(final StringBuffer bookTitle) {
        this.title = bookTitle;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(final String bookAuthor) {
        this.author = bookAuthor;
    }

    public ISBN getIsbn() {
        return isbn;
    }

    public void setIsbn(final ISBN bookIsbn) {
        this.isbn = bookIsbn;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(final float bookPrice) {
        this.price = bookPrice;
    }

    public boolean isValid() {
        return (isbn.isValid() && !empty(author)
                && !empty(title.toString()) && price >= 0.0f);
    }

    private boolean empty(final String s) {
        return s == null || s.trim().length() == 0;
    }
}
