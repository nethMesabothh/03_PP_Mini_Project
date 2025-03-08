package Utils;

import Constant.Constant;

import java.util.Scanner;
import java.util.function.Function;
import java.util.regex.Pattern;

public class Validation {
  Scanner scanner = new Scanner(System.in);
  Constant c = new Constant();

  public <T> T validateInput(String regex, String prompt, Function<String, T> converter){
    while(true){
      System.out.print(prompt);
      String input = scanner.nextLine().trim();

      if(Pattern.matches(regex, input)){
        try{
          return converter.apply(input);
        }catch (NumberFormatException nfe){
          System.out.println(c.RED + "Invalid input" + c.RESET);

        }
      }else{
        System.out.println(c.RED + "Invalid input, Please try again." + c.RESET);

      }
    }
  }
}
