package acavailhez.optget

import org.apache.log4j.Appender
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Logger
import org.apache.log4j.PatternLayout
import org.junit.BeforeClass

abstract class AbstractTests {

    @BeforeClass
    public static void before() {
        BasicConfigurator.configure();
        Enumeration appenders = Logger.getRootLogger().getAllAppenders();
        while (appenders.hasMoreElements()) {
            ((Appender) appenders.nextElement()).setLayout(new PatternLayout("%d{HH:mm:ss.SSS} %-5p [%c{1}] %m%n"));
        }
    }

}
