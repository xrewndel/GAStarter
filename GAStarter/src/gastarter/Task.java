package gastarter;

import static gastarter.GAStarter.log;
import static gastarter.GAStarter.println;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Andrew
 */
public class Task implements Runnable {
    private final String cmd;
    private final boolean test;
    private final int id;
    public Task(String command, boolean tst, int _id) { cmd = command; test = tst; id = _id;}
    
    @Override public void run() {
        DecimalFormat fmt = new DecimalFormat("0000");
        println("Task " + fmt.format(id) + ". " + cmd);
        log.info("Task " + fmt.format(id) + "[" + GAStarter.totalExec + "]. " + cmd);
        
        if (!test)
        try {
            Process p1 = Runtime.getRuntime().exec(cmd);
            BufferedReader reader = new BufferedReader(new InputStreamReader(p1.getInputStream()));
            String line1 = reader.readLine();
            while (line1 != null) {
            System.out.print(line1 + "\n");
            line1 = reader.readLine();
            }
        } catch(IOException e1) {  System.out.println("IOException: " + e1.getMessage()); }
    }
} 
