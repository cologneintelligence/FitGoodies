package ${package};

import de.cologneintelligence.fitgoodies.Fixture;

public final class SaveFixture extends Fixture {
    private boolean written;

    public void savedb() {
        written = false;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                ShelfWriter writer;
                writer = new DerbyShelfWriter();
                writer.write(FixtureObjects.SHELF);

                FileShelfWriter filewriter = new FileShelfWriter();
                filewriter.setFilename("target/demo-output/bookshelf.txt");
                filewriter.write(FixtureObjects.SHELF);

                XMLShelfWriter xmlwriter = new XMLShelfWriter();
                xmlwriter.setFilename("target/demo-output/bookshelf.xml");
                xmlwriter.write(FixtureObjects.SHELF);

                written = true;
            }
        });
        t.start();
    }

    public boolean isSaved() {
        return written;
    }
}
