package ${package};

import org.apache.log4j.Logger;

public final class ISBN {
    private final String isbn10;
    private final Logger logger = Logger.getLogger(ISBN.class);

    public ISBN(final String isbn) {
        isbn10 = isbn;
    }

    public String stripped() {
        return isbn10.replaceAll("-", "");
    }

    @Override
    public int hashCode() {
        return stripped().hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == null) {
            return false;
        } else if (!(other instanceof ISBN)) {
            return false;
        } else {
            return stripped().equals(((ISBN) other).stripped());
        }
    }

    public boolean isValid() {
        String s = isbn10.replaceAll("-", "");
        logger.debug("Validating: " + isbn10);

        char[] chars = new char[10];
        int sum = 0;

        if (s.length() != 10) {
            return false;
        }

        s.getChars(0, 10, chars, 0);

        for (int i = 0; i < 10; ++i) {
            if (chars[i] == 'X' && i == 9) {
                chars[i] = 10;
            } else {
                chars[i] -= '0';
            }

            sum = (sum + (i + 1) * chars[i]) % 11;
        }

        logger.debug("Result: " + sum);
        return sum == 0;
    }
}
