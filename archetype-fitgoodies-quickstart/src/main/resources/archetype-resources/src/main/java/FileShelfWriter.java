package ${package};

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Formatter;
import java.util.Locale;

public final class FileShelfWriter implements ShelfWriter {
    private String filename = "bookshelf.txt";

    public String getFilename() {
        return filename;
    }

    public void setFilename(final String file) {
        this.filename = file;
    }

    @Override
    public Bookshelf load() {
        return null;
    }

    private String fixedWidth(final String s, final int width) {
        StringBuilder sb = new StringBuilder(width);

        sb.append(s);
        while (sb.length() < width) {
            sb.append(" ");
        }

        return sb.substring(0, width);
    }

    @Override
    public void write(final Bookshelf shelf) {
        try {
            new File(filename).getAbsoluteFile().getParentFile().mkdirs();
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));

            for (int i = 0; i < shelf.bookCount(); ++i) {
                Formatter format = new Formatter(Locale.US);
                Book b = shelf.get(i);
                writer.write(fixedWidth(Integer.toString(i), 5));
                writer.write(fixedWidth(b.getIsbn().stripped(), 15));
                writer.write(fixedWidth(b.getTitle().toString(), 80));
                writer.write(fixedWidth(b.getAuthor(), 50));
                format.format("%6.02f", b.getPrice());
                writer.write(fixedWidth(format.toString(), 9));
            }
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
