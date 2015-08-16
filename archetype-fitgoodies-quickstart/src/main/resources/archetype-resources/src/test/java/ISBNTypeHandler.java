package ${package};

import java.text.ParseException;

import de.cologneintelligence.fitgoodies.typehandler.TypeHandler;

public final class ISBNTypeHandler extends TypeHandler<ISBN> {
    public ISBNTypeHandler(String convertParameter) {
        super(convertParameter);
    }

    @Override
    public Class<ISBN> getType() {
        return ISBN.class;
    }

    @Override
    public ISBN unsafeParse(String input) throws ParseException {
        return new ISBN(input);
    }

    @Override
    public String toString(final ISBN isbn) {
        return isbn.stripped();
    }
}
