package gary.interview.wca.actor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.TestProbe;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MockAggregator {

    @Bean
    public TestProbe testProbe(ActorSystem system) {
        return TestProbe.apply(system);
    }

    @Bean("aggregator")
    public ActorRef getMockAggregatorImpl(TestProbe probe) {
        return probe.ref();
    }
}
