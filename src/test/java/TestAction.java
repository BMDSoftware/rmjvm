import com.bmdsoftware.monitor.resourcemonitor.actions.imp.ProcessRun;
import com.bmdsoftware.monitor.resourcemonitor.utils.OSDetector;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestAction  {


    private final ProcessRun processRun = new ProcessRun();

    @Test
    void run() {
        String cmd = "";
        if (OSDetector.isWindows()){
            cmd = "java";
        }
        else{
            cmd = "java";
        }
//        processRun.execute(1000);
    }
}
