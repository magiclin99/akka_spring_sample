package gary.interview.wca.actor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@Import({ActorConfiguration.class, FileParser.class, Aggregator.class})
public class AggregatorTest {

    @Autowired
    private ActorSystem system;

    @Autowired
    @Qualifier("aggregator")
    private ActorRef aggregator;

    @Test
    public void test() {
        new TestKit(system) {{

            String fileName = "test.txt";

            aggregator.tell(new Aggregator.StartOfFile(fileName), getRef());
            expectMsg(fileName);

            aggregator.tell(new Aggregator.Line("a b c"), getRef());
            expectMsg(3);

            aggregator.tell(new Aggregator.Line("d"), getRef());
            expectMsg(4);

            aggregator.tell(new Aggregator.EndOfFile(), getRef());
            expectMsg("reset");
        }};
    }
}
