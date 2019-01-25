package gary.interview.wca.actor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.TestActorRef;
import akka.testkit.TestProbe;
import akka.testkit.javadsl.TestKit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

@RunWith(SpringRunner.class)
@Import({ActorConfiguration.class, FileParser.class, MockAggregator.class})
public class FileParserTest {

    @Autowired
    private ActorSystem system;

    @Autowired
    private SpringExtension springExtension;

    @Autowired
    @Qualifier("fileParser")
    private ActorRef fileParser;

    @Autowired
    private TestProbe mockAggregatorContext;

    @Test
    public void testScanInvalidPath() {
        new TestKit(system) {{
            fileParser.tell(new FileParser.Scan("./incorrect_path"), getRef());
            expectMsg("path not found");
        }};
    }

    @Test
    public void testScanNonFolderPath() {
        new TestKit(system) {{
            fileParser.tell(new FileParser.Scan("./build.gradle"), getRef());
            expectMsg("is not a folder");
        }};
    }

    @Test
    public void testScanAndInteractionWithAggregator() throws IOException {
        File file = new File("./testScan.txt");
        file.delete();
        Files.write(file.toPath(), "one line".getBytes(), StandardOpenOption.CREATE);

        TestActorRef<FileParser> ref = TestActorRef.create(
                system,
                springExtension.get(system).props("fileParserImpl")
        );
        FileParser fileParser = ref.underlyingActor();

        fileParser.scan(new File[]{file});

        mockAggregatorContext.expectMsgClass(Aggregator.StartOfFile.class);
        mockAggregatorContext.expectMsgClass(Aggregator.Line.class);
        mockAggregatorContext.expectMsgClass(Aggregator.EndOfFile.class);

        file.delete();
    }

}
