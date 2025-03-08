package Utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
  private static final Properties properties = new Properties();

  static {
    try{
      FileInputStream inputStream = new FileInputStream("src/config.properties");
      properties.load(inputStream);
      inputStream.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static String get(String key){
    return properties.getProperty(key);
  }
}
