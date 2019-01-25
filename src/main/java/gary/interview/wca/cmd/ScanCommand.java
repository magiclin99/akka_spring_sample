package gary.interview.wca.cmd;

import akka.actor.ActorRef;
import gary.interview.wca.actor.FileParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.util.StringUtils;

@ShellComponent
public class ScanCommand {

    @Autowired
    @Qualifier("fileParser")
    private ActorRef fileParser;

    @Value("${app.default_scan_path}")
    private String defaultScanPath;

    @ShellMethod("scan and count words in files under specified path")
    public void scan(@ShellOption(defaultValue = "",
            help = "default value = spring.yaml:app.default_scan_path") String pathToScan) {

        if (StringUtils.isEmpty(pathToScan)) {
            pathToScan = defaultScanPath;
        }
        fileParser.tell(new FileParser.Scan(pathToScan), ActorRef.noSender());
    }
}
