package ${package};

import de.cologneintelligence.fitgoodies.ColumnFixture;

public final class ISBNFixture extends ColumnFixture {
    public ISBN isbn;

    public boolean valid() {
        return isbn.isValid();
    }

    public String stripped() {
        return isbn.stripped();
    }
}
