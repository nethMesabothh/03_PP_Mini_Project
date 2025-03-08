package Constant;

public class Constant {
  // First Regex is ChoosePaginationAndMenuRegex
  public final String CPMRegex = "^(N|P|F|L|G|W|R|U|D|S|Sr|Sa|Us|Ba|Rs|E|n|p|f|l|g|w|r|u|d|s|sr|sa|us|ba|rs|e)$";
  public final String  PRODUCT_NAME_REGEX = "^[A-Za-z0-9][A-Za-z0-9 _'-]{0,49}$";
  public final String  PRODUCT_UNIT_PRICE = "^\\d+(\\.\\d{1,2})?$";
  public final String  PRODUCT_STOCK_QTY = "^\\d+$";
  public final String  INT = "^\\d+$";
  public final String  UI_UU = "^(ui|uu|b)$";
  public final String  SI_SU = "^(si|su|b)$";
  public final String  ARE_YOU_SURE = "^(y|Y|n|N)$";
  public final String  ONLY_STRING_NUMBER = "^[A-Za-z0-9 ]+$";

}
