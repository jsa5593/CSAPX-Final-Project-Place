package place.client;

import java.util.Arrays;
import java.util.List;

public class ConsoleApplication {
    private String[] cmdLineArgs;
    public List< String > getArguments() {
        return Arrays.asList( this.cmdLineArgs );
    }
}
