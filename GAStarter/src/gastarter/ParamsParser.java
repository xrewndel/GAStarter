package gastarter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Andrew
 GA:
 -cfg config (ga3.cfg)
 -c crossover (0 - no, 1 - yes)
 -m mutation (0 - no, 1 - yes)
 -p population size
 -g generations
 -f work files 

 GAStarter:
 -r repeat
 -cb crossover begin(start)
 -cs crossover step
 -ce crossover end
 -mb mutation begin(start)
 -ms mutation step
 -me mutation end
 -frb mutation free rate begin
 -frs mutation free rate step
 -fre mutation free rate end
 -wrb mutation waste rate begin
 -wrs mutation waste rate step
 -wre mutation waste rate end
 -t test (1 or 0). If test - just print task
 */
public class ParamsParser {
    // по умолчанию все отключено
    public int cpu = Runtime.getRuntime().availableProcessors() - 2;
    public static String begin = "";
    public static String jar = "GA.jar";
    public final String cfgDefault = "ga3.cfg";
    public String cfg = "ga3.cfg";
    public boolean replicaFixed = true;
    public int debug = 0;
    public int crossover = 0;
    public int mutation = 0; 
    public int population = 50;
    public int generation  = 1000;
    public int files = 300;
    public int repeat = 3;
    public int limit = 100;
    public float timeCoeff = 0.0f;
    
    public int crossBegin = 0;
    public int crossStep = 1;
    public int crossEnd = 0;
    
    public int mutateBegin = 0;
    public int mutateStep = 1;
    public int mutateEnd = 0;
    
    public int frb = 0;
    public int frs = 0;
    public int fre = 0;
    
    public int wrb = 0;
    public int wrs = 0;
    public int wre = 0;
    
    public double net = 0;
    public String experiment = "ExperimentName";
    public Triple<Integer, Integer, Integer> changeSpeed = new Triple(1,1,1);
    public int sleep = 1001;
     
    public boolean test = false;
    
    public ParamsParser (String[] args) {
        File cfgFile = new File(args[0]);
        Config config = null;
        try {
            config = new Config(cfgFile.getAbsolutePath());
            //config.printPropertyList();
        } catch (Exception ex) {
            System.out.print("Config file not found: " + ex.getLocalizedMessage() + "\n");
            throw new RuntimeException("Specify config file. ");
        }
        
        //System.out.println(config.asMap());
                
        Map<String, String> paramsMap = config.asMap();
        for (String param : paramsMap.keySet()) {
            switch(param) {
                case "cfg": cfg = paramsMap.get(param);
                case "jar":         jar = paramsMap.get(param);                          break;
                case "crossover":   crossover = Integer.valueOf(paramsMap.get(param));   break;
                case "mutation":    mutation = Integer.valueOf(paramsMap.get(param));    break;
                case "files":       files = Integer.valueOf(paramsMap.get(param));       break;
                case "population":  population = Integer.valueOf(paramsMap.get(param));  break;
                case "generation":  generation = Integer.valueOf(paramsMap.get(param));  break;
                case "limit":       limit = Integer.valueOf(paramsMap.get(param));       break;
                case "timeCoeff":   timeCoeff = Float.valueOf(paramsMap.get(param));     break;
                case "repeat":      repeat = Integer.valueOf(paramsMap.get(param));      break;
                case "crossBegin":  crossBegin = Integer.valueOf(paramsMap.get(param));  break;
                case "crossStep":   crossStep = Integer.valueOf(paramsMap.get(param));   break;
                case "crossEnd":    crossEnd = Integer.valueOf(paramsMap.get(param));    break;
                case "mutateBegin": mutateBegin = Integer.valueOf(paramsMap.get(param)); break;
                case "mutateStep":  mutateStep = Integer.valueOf(paramsMap.get(param));  break;
                case "mutateEnd":   mutateEnd = Integer.valueOf(paramsMap.get(param));   break;
                case "frb":         frb = Integer.valueOf(paramsMap.get(param));         break;
                case "frs":         frs = Integer.valueOf(paramsMap.get(param));         break;
                case "fre":         fre = Integer.valueOf(paramsMap.get(param));         break;
                case "wrb":         wrb = Integer.valueOf(paramsMap.get(param));         break;
                case "wrs":         wrs = Integer.valueOf(paramsMap.get(param));         break;
                case "wre":         wre = Integer.valueOf(paramsMap.get(param));         break;
                case "wr":          wrb = frb; wrs = frs; wre = fre;                     break;  // копируем интервал
                case "test":        test = Boolean.valueOf(paramsMap.get(param));        break;
                case "cpu":         cpu = Integer.valueOf(paramsMap.get(param));         break;
                case "net":         net = Double.valueOf(paramsMap.get(param));          break;
                case "changeSpeed": changeSpeed = parser(paramsMap.get(param));          break;
                case "debug":       debug = Integer.valueOf(paramsMap.get(param));       break;
                case "experiment":  experiment = paramsMap.get(param);                   break;
                case "replicaFix":  replicaFixed = Boolean.valueOf(paramsMap.get(param));break;
                case "sleep":       sleep = Integer.valueOf(paramsMap.get(param));       break;
                default: {
                    System.out.println("Paramter \"" + param + "\" is unknown");
                    System.exit(1);
                }
            }
        }
        
        validate();
        begin = "java -jar " + jar + " -o 0"; // -o 0 = no print to console extra data
    }
    
    public boolean crossover()      { return crossover == 1 && mutation == 0; }
    public boolean mutation()       { return crossover == 0 && mutation == 1; }
    public boolean crossAndMutate() { return crossover == 1 && mutation == 1; }
    public boolean fr() { return frb > 0 && fre > 0; }
    public boolean wr() { return wrb > 0 && wre > 0; }
    public boolean cross() { return crossBegin > 0 && crossEnd > 0; }
    public boolean mutate() { return mutateBegin > 0 && mutateEnd > 0; }
    public String fixed(int speed) {
        StringBuilder sb = new StringBuilder();
        sb.append(begin);
        //if (!cfg.equals(cfgDefault)) sb.append(CMD.cfg.cmd(cfg));             // no config anymore
        sb.append(CMD.c.cmd(crossover));
        sb.append(CMD.m.cmd(mutation));
        sb.append(CMD.l.cmd(limit));
        sb.append(CMD.exp.cmd(experiment));
        if (mutateBegin == mutateEnd) sb.append(CMD.mp.cmd(mutateBegin));
        if (!replicaFixed) sb.append(CMD.fix.cmd(0));
        //if (timeCoeff > 0) sb.append(CMD.tc.cmd(timeCoeff));
        if (net > 0) sb.append(CMD.net.cmd(net));
        if (speed > 0) sb.append(CMD.cspd.cmd(speed));
        if (debug > 0) sb.append(CMD.debug.cmd(debug));
        
         return sb.toString();
    }
    
    private Triple<Integer, Integer, Integer> parser(String s) {
        String[] str = s.split(",");
        if (str.length != 3) throw new Error("Invalid format for " + s);
        
        return new Triple(Integer.valueOf(str[0]), Integer.valueOf(str[1]), Integer.valueOf(str[2]));
    }
    
    private void validate() {
        if (files < 0) throw new RuntimeException(CMD.f.param() + " < 0");
        if (population < 0) throw new RuntimeException(CMD.p.param() + " < 0");
        if (generation < 0) throw new RuntimeException(CMD.g.param() + " < 0");
        if (repeat < 0) throw new RuntimeException(CMD.r.param() + " < 0");
        
        if (crossBegin < 0) throw new RuntimeException(CMD.cb.param() + " < 0");
        if (crossStep < 0) throw new RuntimeException(CMD.cs.param() + " < 0");
        if (crossEnd < 0) throw new RuntimeException(CMD.ce.param() + " < 0");
        if (crossEnd < crossBegin) throw new RuntimeException(CMD.ce.param() + " < " + CMD.cb.param());
        //if (crossBegin > 0 && crossEnd > 0) crossover = 1;
        
        if (mutateBegin < 0) throw new RuntimeException(CMD.mb.param() + " < 0");
        if (mutateStep < 0) throw new RuntimeException(CMD.ms.param() + " < 0");
        if (mutateEnd < 0) throw new RuntimeException(CMD.me.param() + " < 0");
        if (mutateEnd < mutateBegin) throw new RuntimeException(CMD.me.param() + " < " + CMD.mb.param());
        //if (mutateBegin > 0 && mutateEnd > 0) mutation = 1;
        
        if (frb < 0) throw new RuntimeException(CMD.frb.param() + " < 0");
        if (frs < 0) throw new RuntimeException(CMD.frs.param() + " < 0");
        if (fre < 0) throw new RuntimeException(CMD.fre.param() + " < 0");
        if (fre < frb) throw new RuntimeException(CMD.fre.param() + " < " + CMD.frb.param());
        if (fre > 0 && fre > 0) mutation = 1;
        
        if (wrb < 0) throw new RuntimeException(CMD.wrb.param() + " < 0");
        if (wrs < 0) throw new RuntimeException(CMD.wrs.param() + " < 0");
        if (wre < 0) throw new RuntimeException(CMD.wre.param() + " < 0");
        if (wre < wrb) throw new RuntimeException(CMD.wre.param() + " < " + CMD.wrb.param());
        if (wre > 0 && wre > 0) mutation = 1;
    }
    
    public final static void help() {
        StringBuilder sb = new StringBuilder();
        sb.append(begin).append(CMD.help);
        sb.append("\n\tfrb, frs, fre, wrb, wrs, wre are rate but specified in integer like 10, 25");
        System.out.println(sb);
    }
    
    public enum CMD { 
        //GA
        cfg("-cfg",     "\n\t-cfg file.cfg"),
        c("-c",         "\n\t-c crossover"),
        m("-m",         "\n\t-m mutation"),
        f("-f",         "\n\t-f files"),
        p("-p",         "\n\t-p populatio"),
        g("-g",         "\n\t-g generations"),
        cp("-cp",       ""), // crossRate
        mp("-mp",       ""), // mutateRate
        fr("-fr",       ""), // freeRate
        wr("-wr",       ""), // wasteRate
        l("-l",         "\n\t limit. Track fitness change over n generetion"),
        fix("-fix",     "\n\t is replica fixed"),
        tc("-tc",       ""), // timeCoeff
        net("-nc",      "\n\t network coefficient"),
        cspd("-cspd",   "\n\t network coefficient"),
        debug("-dbg",   "\n\t debug etra info"),
        exp("-exp",     "\n\t experiment name"),
        
        // GA Starter
        r("-r",     "\n\t-r repeat"),
        cb("-cb",   "\n\n\t-cb crossover begin(start)"),
        cs("-cs",   "\n\t-cs crossover step"),
        ce("-ce",   "\n\t-ce crossover end"),
        mb("-mb",   "\n\n\t-mb mutation begin(start)"),
        ms("-ms",   "\n\t-ms mutation step"),
        me("-me",   "\n\t-me mutation end"),
        frb("-frb", "\n\n\t-frb mutation free rate begin"),
        frs("-frs", "\n\t-frs mutation free rate step"),
        fre("-fre", "\n\t-fre mutation free rate end"),
        wrb("-wrb", "\n\n\t-wrb mutation waste rate begin"),
        wrs("-wrs", "\n\t-wrs mutation waste rate step"),
        wre("-wre", "\n\t-wre mutation waste rate end"),
        t("-t",     "\n\n\t-t test"),
        cpu("-cpu", "\n\t number of cpu"),
        h("-h",     ""),
        undef ("",  "");
        
        private final String name;
        private final String hlp;
        CMD(String n, String h) { name = n; hlp = h; }
        private static final Map<String, CMD> params = new HashMap<>();
        static { for (CMD parameter : values()) params.put(parameter.name, parameter); }
        
        private static final StringBuilder help = new StringBuilder();
        static { for (CMD parameter : values()) help.append(parameter.hlp); }
        
        private static CMD fromString(String name) {
            if (params.containsKey(name)) return params.get(name);
            else return CMD.undef;
        }
        
        //public String hlp() { return hlp; }
        public String cmd(String value) { return " " + name + " " + value; }
        public String cmd(int value)    { return cmd("" + value); }
        public String cmd(double value) { return cmd("" + value); }
        public String param() { return name; }
    }
}
