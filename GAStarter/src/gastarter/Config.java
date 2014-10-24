package gastarter;

import java.util.Properties;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
 
public class Config {
   private Properties configFile;
   
    public Config(String fileName) throws FileNotFoundException, IOException  {
	configFile = new Properties();
	//try {
            InputStream is = new FileInputStream(fileName);
            configFile.load(is);
	//} catch(IOException ex){
        //    System.out.print("Error while reading cfg file: " + ex.getLocalizedMessage());
	//}
    }
    
    public Map<String, String> asMap() {
        Map<String, String> map = new TreeMap <>();
        Iterator it = configFile.entrySet().iterator();
        while (it.hasNext()) {
            String str = it.next().toString();
            int delimiter = str.indexOf("=");
            if (delimiter>0) {
                map.put(str.substring(0, delimiter), str.substring(delimiter + 1));
            }
        }
        return map;
    }
 
    public String getProperty(String key) { return configFile.getProperty(key); }  // получение значение параметра
    public void printPropertyList() { configFile.list(System.out); }
}