package gary.interview.wca.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * an Actor for scanning files in specified folder.
 * It will delegates counting task of words to {@link Aggregator}
 */
@Component("fileParserImpl")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FileParser extends AbstractActor {

    private Logger log = LoggerFactory.getLogger(FileParser.class);

    @Autowired
    @Qualifier("aggregator")
    private ActorRef aggregator;

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(Scan.class, this::handleScan)
                .build();
    }

    private void handleScan(Scan scan) throws Exception {
        String folderPath = scan.path;

        File folder = new File(folderPath);
        if (!folder.exists()) {
            log.info("folder not found: " + folderPath);
            getSender().tell("path not found", getSelf());
            return;
        }

        if (!folder.isDirectory()) {
            log.info("specified path is not a folder: " + folderPath);
            getSender().tell("is not a folder", getSelf());
            return;
        }

        File[] files = folder.listFiles();
        if (files == null) {
            return;
        }

        log.info("start scanning: " + folderPath);
        scan(files);
    }

    void scan(File[] files) throws IOException {
        for (File file : files) {
            aggregator.tell(new Aggregator.StartOfFile(file.getName()), ActorRef.noSender());

            Files.lines(file.toPath()).forEach(line ->
                    aggregator.tell(new Aggregator.Line(line), ActorRef.noSender())
            );

            aggregator.tell(new Aggregator.EndOfFile(), ActorRef.noSender());
        }
    }

    // ------ API -------

    public static class Scan {
        public final String path;

        public Scan(String path) {
            this.path = path;
        }
    }
}
