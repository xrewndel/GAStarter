package gastarter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Andrew
 */
public class Task implements Runnable {
    private final String cmd;
    private final boolean test;
    public Task(String command, boolean tst) { cmd = command; test = tst; }
    
    @Override public void run() {
        System.out.println(cmd);
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
