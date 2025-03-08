package Model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BackupFile {
  private final String fileName;
  private final String filePath;
  private final int version;
  private final String date;

  public BackupFile(String fileName, String filePath) {
    this.fileName = fileName;
    this.filePath = filePath;
    this.version = parseVersion(fileName);
    this.date = parseDate(fileName);
  }

  private int parseVersion(String fileName) {
    Pattern pattern = Pattern.compile("Version-(\\d+)-product-backup-\\d{4}-\\d{2}-\\d{2}\\.sql");
    Matcher matcher = pattern.matcher(fileName);
    if (matcher.matches()) {
      return Integer.parseInt(matcher.group(1));
    }
    return -1;
  }

  // Parse date using regex
  private String parseDate(String fileName) {
    Pattern pattern = Pattern.compile("Version-\\d+-product-backup-(\\d{4}-\\d{2}-\\d{2})\\.sql");
    Matcher matcher = pattern.matcher(fileName);
    if (matcher.matches()) {
      return matcher.group(1);
    }
    return "Unknown";
  }

  public int getVersion() {
    return version;
  }

  public String getDate() {
    return date;
  }

  public String getFilePath() {
    return filePath;
  }

  @Override
  public String toString() {
    return String.format("Version-%d-product-backup-%s.sql", version, date);
  }
}
