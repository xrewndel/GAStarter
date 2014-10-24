package gastarter;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Andrew
 */
public class GAStarter {
    private final static String version = "1.0\n";
    private static ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);
    private static String s = "java -jar GA.jar ga3.cfg ";
    private static int repeat = 3; // кол-во раз поторять тест с одинаковыми параметрами
    /* Конфиг:
     * ga.cfg [1] [2] [3] [4] [5] [6]
     * где:
     * [1] - кроссовер. 1 - да, 0 - нет
     * [2] - мутация. 1 - да, 0 - нет
     * [3] - кол-во файлов
     * [4] - процент перекрестного использования файлов в задачах
     * [5] - процент кроссоверов целое, 10-40
     * [6] - процент мутаций (целое) 
     * Пример: ga.cfg 1 1 600 20 40 4
    */
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println(version);
        //DecimalFormat df = format();
        if (args.length < 8) {
            StringBuilder sb = new StringBuilder();
            sb.append("java -jar GAStarter.jar [0] [1] [2] [3] [4] [5] [6] [7] [8]\n");
            sb.append("where:\n").append("[0] - repeat\n");
            sb.append("[1] - work files\n");
            sb.append("[2] - crossover start\n");
            sb.append("[3] - crossover step\n");
            sb.append("[4] - crossover end\n");
            sb.append("[5] - mutation start\n");
            sb.append("[6] - mutation step\n");
            sb.append("[7] - mutation end\n");
            sb.append("[8] - test (1 or 0). If test - just print task\n");
            sb.append("Example: java -jar StarterGA.jar 3 300 10 10 50 1 3 10\n");
            sb.append("will exec on 300 files, cross from 10 to 50 by 10, mutate from 1 to 10 by 3, repeate each task 3 times");
            System.out.println(sb);
            System.exit(1);
        }
        
        System.out.print("Core: " + Runtime.getRuntime().availableProcessors() + "\n");
        //String cmd = "java -jar GA.jar ga3.cfg 1 1 600 20 40 4";
        repeat = Integer.valueOf(args[0]);
        int files       = Integer.valueOf(args[1]);
        int crossStart  = Integer.valueOf(args[2]);
        int crossStep   = Integer.valueOf(args[3]);
        int crossEnd    = Integer.valueOf(args[4]);
        int mutateStart = Integer.valueOf(args[5]);
        int mutateStep  = Integer.valueOf(args[6]);
        int mutateEnd   = Integer.valueOf(args[7]);
        boolean test = false;
        if (args.length == 9) test = args[8].equals("1");
        System.out.println("test: " + test);
        
        //for (int file = fileStart; file < fileEnd; file = file * fileFactor) {    }
            for (int cross = crossStart; cross < crossEnd; cross += crossStep) {
                for (int mutate = mutateStart; mutate <= mutateEnd; mutate += mutateStep) {
                    for (int i = 0; i< repeat; i++) {
                        //String cmd = s + " " + 1 + " " + 1 + " " + fileStart + " 20 " + df.format(cross) + " " + mutate; 
                        String cmd = s + " " + 1 + " " + 1 + " " + files + " 20 " + cross + " " + mutate; 
                        Runnable task = new Task(cmd, test);
                        pool.execute(task);
                        Thread.sleep(1000);
                    }
                }
            }
        
        pool.shutdown();
    }
    
    private static DecimalFormat format() {
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator('.');
        //decimalFormatSymbols.setGroupingSeparator(',');
        return new DecimalFormat("#,#0.0", decimalFormatSymbols);
    }
}
