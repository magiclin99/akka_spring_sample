package gary.interview.wca.actor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * this subjects of this class: <br>
 * <ul>
 * <li>setup actor system</li>
 * <li>provides spring bean of ActorRef</li>
 * </ul>
 */
@Configuration
public class ActorConfiguration {

    @Bean
    public SpringExtension springExtension() {
        return new SpringExtension();
    }

    @Bean
    public ActorSystem getSystem(ApplicationContext context, SpringExtension springExtension) {
        ActorSystem actorSystem = ActorSystem.create("words-counting-application");
        springExtension.get(actorSystem).initialize(context);
        return actorSystem;
    }

    @Bean("fileParser")
    public ActorRef getFileParser(ActorSystem system, SpringExtension springExtension) {
        return system.actorOf(springExtension.get(system).props("fileParserImpl"));
    }

    @Bean("aggregator")
    public ActorRef getAggregator(ActorSystem system, SpringExtension springExtension) {
        return system.actorOf(springExtension.get(system).props("aggregatorImpl"));
    }

}
