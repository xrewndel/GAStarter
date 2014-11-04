package gastarter;

import gastarter.ParamsParser.CMD;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Andrew
 */
public class GAStarter {
    private final static String version = "2.2";
    private static ExecutorService pool;
    private static ParamsParser params;
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Version: " + version + "\n");
        if (args.length == 0) {
            ParamsParser.help();
            System.exit(1);
        }
        
        params = new ParamsParser(args);
        pool = Executors.newFixedThreadPool(params.cpu);
        println("Cores: " + Runtime.getRuntime().availableProcessors());
        
        // формируем неизменную часть строки запуска
        String begin = params.fixed();
        if (params.crossAndMutate()) {
            for (int cross = params.crossBegin; cross < params.crossEnd; cross += params.crossStep) {
                for (int mutate = params.mutateBegin; mutate <= params.mutateEnd; mutate += params.mutateStep) {
                    String cmd = begin + CMD.f.cmd(params.files) + CMD.cp.cmd(cross) + CMD.mp.cmd(mutate);
                    mutation(cmd);
                    //execute(cmd);
                }
            }
        }
        else if (params.crossover()) {
            for (int cross = params.crossBegin; cross < params.crossEnd; cross += params.crossStep) {
                String cmd = begin + CMD.f.cmd(params.files) + CMD.cp.cmd(cross) + CMD.m.cmd(params.mutation); 
                execute(cmd);
            }
        }
        else if (params.mutation()) {
            mutation(begin);
        }
        
        pool.shutdown();
    }
    
    private static void mutation(String begin) {
        if(params.fr() && params.wr()) {
                for (int freeRate = params.frb; freeRate < params.fre; freeRate += params.frs) {
                    for (int wasteRate = params.wrb; wasteRate <= params.wre; wasteRate += params.wrs) {
                        String cmd = begin + CMD.fr.cmd(freeRate) + CMD.wr.cmd(wasteRate);
                        execute(cmd);
                    }
                }
            }
            else if(params.fr() && !params.wr()) {
                for (int freeRate = params.frb; freeRate <= params.fre; freeRate += params.frs) {
                    //String cmd = begin + CMD.fr.cmd(freeRate);
                    String cmd = begin + CMD.fr.cmd(freeRate) + CMD.wr.cmd(params.wrb);
                    execute(cmd);
                }
            }
            else if(!params.fr() && params.wr()) {
                for (int wasteRate = params.wrb; wasteRate <= params.wre; wasteRate += params.wrs) {
                    //String cmd = begin + CMD.wr.cmd(wasteRate);
                    String cmd = begin + CMD.fr.cmd(params.frb) + CMD.wr.cmd(wasteRate);
                    execute(cmd);
                }
            }
    }
    
    private static void execute(String exec) {
        for (int i = 0; i < params.repeat; i++) {
            Runnable task = new Task(exec, params.test);
            pool.execute(task);
            if (!params.test) try { Thread.sleep(1001); } catch (InterruptedException ex) {
                System.out.println(ex);
                ex.printStackTrace();
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
}
