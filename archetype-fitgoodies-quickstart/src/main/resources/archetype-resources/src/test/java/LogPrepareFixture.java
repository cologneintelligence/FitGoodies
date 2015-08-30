package ${package};

import java.io.PrintWriter;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import de.cologneintelligence.fitgoodies.ActionFixture;

public final class LogPrepareFixture extends ActionFixture {
    public void initializeLogging() {
        ConsoleAppender appender = new ConsoleAppender();
        appender.setWriter(new PrintWriter(System.out));
        appender.setLayout(new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN));
        appender.setName("stdout");
        Logger.getRootLogger().addAppender(appender);
    }
}
