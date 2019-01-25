package gary.interview.wca.actor;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * an Actor for counting words of file
 */
@Component("aggregatorImpl")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Aggregator extends AbstractActor {

    private Logger log = LoggerFactory.getLogger(Aggregator.class);
    private String fileName;
    private int counter;

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(StartOfFile.class, this::handleStartOfFile)
                .match(Line.class, this::handleLine)
                .match(EndOfFile.class, this::handleEndOfFile)
                .build();
    }


    private void handleStartOfFile(StartOfFile sof) {
        fileName = sof.name;
        log.info("file: " + fileName + ", start scanning");
        getSender().tell(fileName, getSelf());
    }

    private void handleLine(Line line) {
        counter += line.content.split(" ").length;
        getSender().tell(counter, getSelf());
    }

    private void handleEndOfFile(EndOfFile eof) {
        log.info("file: " + fileName + ", number of words: " + counter);
        counter = 0;
        fileName = null;
        getSender().tell("reset", getSelf());
    }

    // ------ API -------

    public static class StartOfFile {
        public final String name;

        public StartOfFile(String name) {
            this.name = name;
        }
    }

    public static class Line {
        public final String content;

        public Line(String content) {
            this.content = content;
        }
    }

    public static class EndOfFile {
    }
}
