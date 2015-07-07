package gastarter;

import gastarter.ParamsParser.CMD;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * @author Andrew
 */
public class GAStarter {
    private final static String version = "2.7";
    private static ExecutorService pool;
    private static ParamsParser prm;
    public static int totalExec = 0;
    public final static Logger log = Logger.getLogger(LOG.main.$());
    private volatile static AtomicInteger id = new AtomicInteger(0);
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Version: " + version + "\n");
        if (args.length == 0) {
            ParamsParser.help();
            System.exit(1);
        }
        
        LOG.init();
        
        prm = new ParamsParser(args);
        pool = Executors.newFixedThreadPool(prm.cpu);
        println("Cores: " + Runtime.getRuntime().availableProcessors());
        log.info("Cores: " + Runtime.getRuntime().availableProcessors());
        
        for (int i = prm.changeSpeed.begin; i <= prm.changeSpeed.end; i += prm.changeSpeed.step) 
            for (int net = prm.net.begin; net <= prm.net.end; net += prm.net.step)
                starter(true, i, net);
        
        log.info("Total exec count: " + totalExec);
        
        for (int i = prm.changeSpeed.begin; i <= prm.changeSpeed.end; i += prm.changeSpeed.step) 
            for (int net = prm.net.begin; net <= prm.net.end; net += prm.net.step)
                starter(false, i, net);
        
        pool.shutdown();
    }

    private static void starter(boolean idle, int changeSpeed, int net) {
        // формируем неизменную часть строки запуска
        String begin = prm.fixed(changeSpeed, net);
        if (prm.crossAndMutate()) {
            for (int cross = prm.cross.begin; cross < prm.cross.end; cross += prm.cross.step) {
                for (int mutate = prm.mutate.begin; mutate <= prm.mutate.end; mutate += prm.mutate.step) {
                    String cmd = begin + CMD.f.cmd(prm.files) + CMD.cp.cmd(cross) + CMD.mp.cmd(mutate);
                    mutation(cmd, idle);
                    //execute(cmd);
                }
            }
        }
        else if (prm.crossover()) {
            for (int cross = prm.cross.begin; cross < prm.cross.end; cross += prm.cross.step) {
                String cmd = begin + CMD.f.cmd(prm.files) + CMD.cp.cmd(cross) + CMD.m.cmd(prm.mutation); 
                execute(cmd, idle);
            }
        }
        else if (prm.mutation()) {
            mutation(begin, idle);
        }
        
        //pool.shutdown();
    }
    
    private static void mutation(String begin, boolean idle) {
        if(prm.fr() && prm.wr()) {
            for (int freeRate = prm.fr.begin; freeRate < prm.fr.end; freeRate += prm.fr.step) {
                for (int wasteRate = prm.wr.begin; wasteRate <= prm.wr.end; wasteRate += prm.wr.step) {
                    String cmd = begin + CMD.fr.cmd(freeRate) + CMD.wr.cmd(wasteRate);
                    execute(cmd, idle);
                }
            }
        }
        else if(prm.fr() && !prm.wr()) {
            for (int freeRate = prm.fr.begin; freeRate <= prm.fr.end; freeRate += prm.fr.step) {
                //String cmd = begin + CMD.fr.cmd(freeRate);
                String cmd = begin + CMD.fr.cmd(freeRate) + CMD.wr.cmd(prm.wr.begin);
                execute(cmd, idle);
            }
        }
        else if(!prm.fr() && prm.wr()) {
            for (int wasteRate = prm.wr.begin; wasteRate <= prm.wr.end; wasteRate += prm.wr.step) {
                //String cmd = begin + CMD.wr.cmd(wasteRate);
                String cmd = begin + CMD.fr.cmd(prm.fr.begin) + CMD.wr.cmd(wasteRate);
                execute(cmd, idle);
            }
        }
    }
    
    private static void execute(String exec, boolean idle) {
        for (int i = 0; i < prm.repeat; i++) {
            if (idle) totalExec++;
            else {
                Runnable task = new Task(exec, prm.test, id.incrementAndGet());
                pool.execute(task);
                if (!prm.test) try { Thread.sleep(prm.sleep); } catch (InterruptedException ex) {
                    System.out.println(ex);
                    ex.printStackTrace();
                }
            }
        }
    }
    
    private static DecimalFormat format() {
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator('.');
        //decimalFormatSymbols.setGroupingSeparator(',');
        return new DecimalFormat("#,#0.0", decimalFormatSymbols);
    }
    
    private static String round(double value) {
        return "" + Math.round(value * 100.0 ) / 100.0;
    }
    
    public static void println(String s) { System.out.println("[GAStarter " + version + "] " + s); }
    
    
    private enum LOG{
        main("main");
        private final String log;  LOG(String s) { log = s; }
        public String $() { return log; }
        private String logFile() { return "./log/starter-" + new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date()) + ".log"; }
        private static final List<LOG> undefLog = new ArrayList<>(Arrays.asList(LOG.values()));
        
        public static void init() {
            String PATTERN = "%m%n";
            Level dbg = Level.DEBUG;
            Logger rootLogger = Logger.getRootLogger();
            if (rootLogger.getAllAppenders().hasMoreElements()) {
                System.out.println("Init log config");
                Logger.getRootLogger().getLoggerRepository().resetConfiguration();
                rootLogger.setLevel(dbg);
            }

            for (LOG log : undefLog) {
                FileAppender fa = new FileAppender();
                fa.setName(log.$());
                fa.setFile(log.logFile());
                fa.setLayout(new PatternLayout(PATTERN));
                fa.setThreshold(dbg);
                fa.setAppend(true);
                fa.activateOptions();
                
                Logger pkgLogger = rootLogger.getLoggerRepository().getLogger(log.$());
                pkgLogger.setLevel(dbg);
                pkgLogger.addAppender(fa);
            }
        }
    }
}
