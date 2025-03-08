package View;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import Constant.Constant;
import Controller.ProductController;
import Model.BackupFile;
import Model.Product;
import Utils.Validation;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.CellStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;

public class ProductView {
  private Scanner scanner;
  private int currentStartIndex = 0;
  private int rowLimit;
  private int pageCount = 1;

  Validation v = new Validation();
  Constant c = new Constant();

  public ProductView() {
    scanner = new Scanner(System.in);
    this.rowLimit = 3;
  }

  //getScanner
  public Scanner getScanner() {
    return scanner;
  }

  //Initialize row limit
  public void initializeRowLimit(ProductController controller) {
    rowLimit = controller.getRowLimit();
    //System.out.println("Row limit initialized to: " + rowLimit);
  }

  // Display all products & Pagination & Menu
  public void displayAllProductsAndMenu(List<Product> products, ProductController controller) {

    if (products == null || products.isEmpty()) {
      System.out.println("No products available!");
      return;
    }
    outer:
    while (true) {
      try {
        CellStyle numberStyle = new CellStyle(CellStyle.HorizontalAlign.center);

        int totalRecord = products.size();

        Table t = new Table(5, BorderStyle.UNICODE_BOX_DOUBLE_BORDER_WIDE, ShownBorders.ALL);

        t.setColumnWidth(0, 20, 30);
        t.setColumnWidth(1, 20, 30);
        t.setColumnWidth(2, 20, 30);
        t.setColumnWidth(3, 20, 30);
        t.setColumnWidth(4, 20, 30);

        t.addCell("\033[32mID\033[0m", numberStyle);
        t.addCell("\033[32mNAME\033[0m", numberStyle);
        t.addCell("\033[32mUnit Price\033[0m", numberStyle);
        t.addCell("\033[32mQty\033[0m", numberStyle);
        t.addCell("\033[32mImportDate\033[0m", numberStyle);

        products.stream().skip(currentStartIndex).limit(rowLimit).forEach(product -> {
          t.addCell(String.valueOf(product.getId()), numberStyle);
          t.addCell(String.valueOf(product.getName()), numberStyle);
          t.addCell("$ " + product.getUnitPrice(), numberStyle);
          t.addCell(String.valueOf(product.getStockQty()), numberStyle);
          t.addCell(String.valueOf(product.getImportDate()), numberStyle);
        });
        int totalPages = (products.size() + rowLimit - 1) / rowLimit;

        t.addCell("Page : " + c.YELLOW + pageCount + c.RESET + " of " + c.RED + totalPages + c.RESET, numberStyle, 2);
        t.addCell("Total Record : " + c.GREEN + totalRecord + c.RESET, numberStyle, 3);

        System.out.println(t.render());


        System.out.print("\t\t\t\t\t\t\t------------------------ Menu ------------------------\n");

        System.out.print("\t\t" + c.GREEN + "N" + c.RESET + ". Next Page\t\t");
        System.out.print(c.GREEN + "P" + c.RESET + ". Previous Page\t\t");
        System.out.print(c.GREEN + "F" + c.RESET + ". First Page\t\t");
        System.out.print(c.GREEN + "L" + c.RESET + ". Last Page\t\t");
        System.out.print(c.GREEN + "G" + c.RESET + ". Goto\t\t\n\n");

        System.out.print(c.GREEN + "W)" + c.RESET + " Write Page\t\t");
        System.out.print(c.GREEN + "R)" + c.RESET + " Read (id)\t\t");
        System.out.print(c.GREEN + "D)" + c.RESET + " Delete\t\t");
        System.out.print(c.GREEN + "S)" + c.RESET + " Search (name)\t\t");
        System.out.print(c.GREEN + "Sr)" + c.RESET + " Set rows\t\t\n");
        System.out.print(c.GREEN + "Sa)" + c.RESET + " Save\t\t\t");
        System.out.print(c.GREEN + "Us)" + c.RESET + " Unsaved\t\t\t");
        System.out.print(c.GREEN + "Ba)" + c.RESET + " Backup\t\t");
        System.out.print(c.GREEN + "Rs)" + c.RESET + " Restore\t\t\t\t");
        System.out.print(c.GREEN + "E)" + c.RESET + " Exit\n");
        System.out.print("\t\t\t\t\t\t\t------------------------------------------------\n");


        String choosePaginationAndMenu = v.validateInput(c.CPMRegex, c.YELLOW + "=> Choose an Option() : " + c.RESET, string -> string).trim().toUpperCase();


        switch (choosePaginationAndMenu) {
          // TODO: First Page
          case "F":
            currentStartIndex = 0;
            pageCount = 1;
            break;
          // TODO: Next Page
          case "N":

            int lastIndex = -1;
            for (int i = products.size() - 1; i >= 0; i--) {
              if (products.getLast() != null) {
                lastIndex = i;
                break;
              }
            }
            if (lastIndex == -1) {
              System.out.println(c.RED + "No data available!" + c.RESET);
              continue;
            }
            int maxStartIndex = Math.max(0, lastIndex - (rowLimit - 1));

            if (currentStartIndex >= maxStartIndex) {

              System.out.println(c.RED + "You already on the last page." + c.RESET);

              continue;
            }

            if (currentStartIndex + rowLimit <= lastIndex) {
              currentStartIndex += rowLimit;
              pageCount++;
            } else {
              currentStartIndex = maxStartIndex;
            }
            continue;

            // TODO: Previous Page
          case "P":
            if (currentStartIndex - rowLimit >= 0) {
              currentStartIndex -= rowLimit;
              pageCount = pageCount - 1;
            }
            continue;
            // TODO: Last Page
          case "L":
            int lastIndexLastPage = -1;
            for (int i = products.size() - 1; i >= 0; i--) {
              if (products.getLast() != null) {
                lastIndexLastPage = i;
                break;
              }
            }

            if (lastIndexLastPage != -1) {
              currentStartIndex = lastIndexLastPage - (lastIndexLastPage % rowLimit);
              pageCount = totalPages;
            } else {
              currentStartIndex = 0;
            }
            continue;
            // TODO: Goto specific page
          case "G":
            while (true) {
              try {
                System.out.print("Page number : ");
                int gotoSpecificPage = Integer.parseInt(scanner.nextLine().trim());

                if (gotoSpecificPage < 1 || gotoSpecificPage > totalPages) {

                  System.out.println(c.RED + "Invalid page number. Please enter a number between 1 and " + totalPages + c.RESET);

                  continue;
                } else {
                  currentStartIndex = (gotoSpecificPage - 1) * rowLimit;
                  pageCount = gotoSpecificPage;
                }

              } catch (NumberFormatException nfe) {
                System.out.println(c.RED + "Invalid input. Please enter a valid number." + c.RESET);
                continue;
              }
              break;
            }
            break;
          case "W":
            controller.createNewProduct();
            break;
          case "R":
            handleGetProductById(controller);
            break;
          case "U":
            handleUpdateMenu(controller);
            break;
          case "D":
            handleDeleteProductById(controller);
            break;
          case "S":
            handleSearchByName(controller);
            break;
          case "SR":
            handleSetRowLimit(controller);
            break;
          case "SA":
            handleSaveDataToDatabase(controller);
            break;
          case "US":
            handleUnSaveMenu(controller);
            break;
          case "BA":
            controller.handleBackup();
            break;
          case "RS":
            controller.handleRestore();
            break;
          case "E":
            break outer;
        }
      } catch (Exception e) {
        System.out.println("An error occurred : " + e.getMessage());
        break;
      }
    }

  }

  // Display backup file in a table
  public void displayBackupFiles(List<BackupFile> backups) {
    if (backups.isEmpty()) {
      System.out.println("No backups available!");
      return;
    }

    CellStyle numberStyle = new CellStyle(CellStyle.HorizontalAlign.center);
    Table t = new Table(3, BorderStyle.UNICODE_BOX_DOUBLE_BORDER_WIDE, ShownBorders.ALL);

    t.setColumnWidth(0, 10, 20); // Column for sequence number
    t.setColumnWidth(1, 60, 80); // Column for backup name
    t.setColumnWidth(2, 20, 30); // Column for version number

    t.addCell("Lists of backup data", numberStyle, 3);

    for (int i = 0; i < backups.size(); i++) {
      BackupFile backup = backups.get(i);
      t.addCell(String.valueOf(i + 1), numberStyle); // Sequence number
      t.addCell(backup.toString(), numberStyle, 2); // Raw filename
    }

    System.out.println(t.render());
  }

  // Get input for a new product
  public Product getInputForNewProduct(int generateNewId) {

    System.out.println("ID : " + c.GREEN + generateNewId + c.RESET);
    String name = v.validateInput(c.PRODUCT_NAME_REGEX, "Enter product name: ", s -> s).trim();
    double unitPrice = v.validateInput(c.PRODUCT_UNIT_PRICE, "Enter unit price: ", Double::parseDouble);
    int stockQty = v.validateInput(c.PRODUCT_STOCK_QTY, "Enter stock quantity: ", Integer::parseInt);

    return new Product(name, unitPrice, stockQty);
  }

  // Display the unsaved menu of update or insert
  private void handleUnSaveMenu(ProductController controller) {
    BreakWhile:
    while (true) {
      System.out.println("'ui' for saving insert products and 'uu' for saving update products or 'b' for back to menu");


      String unsavedOption = v.validateInput(c.UI_UU, "Enter your option : ", s -> s).trim().toUpperCase();

      switch (unsavedOption) {
        case "UI":
          controller.displayUnsavedProductsForInsert();
          break;

        case "UU":
          controller.displayUnsavedProductsForUpdate();
          break;

        case "B":
          System.out.println(c.YELLOW + "Returning to main menu..." + c.RESET);
          break BreakWhile;

        default:
          System.out.println(c.RED + "Invalid option. Please try again." + c.RESET);


      }
    }

  }

  // Save data to the database (update or insert)
  private void handleSaveDataToDatabase(ProductController controller) {
    BreakWhile:
    while (true) {

      System.out.println("'si' for saving insert products and 'su' for saving update products or 'b' for back to menu");

      String saveDataOption = v.validateInput(c.SI_SU, "Enter your option : ", s -> s).trim().toUpperCase();

      switch (saveDataOption) {
        case "SI":
          controller.saveUnsavedInsertProducts();
          break;

        case "SU":
          controller.saveUnsavedUpdateProducts();
          break;

        case "B":
          System.out.println(c.YELLOW + "Returning to main menu..." + c.RESET);
          break BreakWhile;

        default:
          System.out.println(c.RED + "Invalid option. Please try again." + c.RESET);

      }
    }

  }

  // Display products (used for unsaved products)
  public void displayProducts(List<Product> products) {
    if (products == null || products.isEmpty()) {
      System.out.println(c.RED + "No products available!" + c.RESET);
      return;
    }

    CellStyle numberStyle = new CellStyle(CellStyle.HorizontalAlign.center);

    Table t = new Table(5, BorderStyle.UNICODE_BOX_DOUBLE_BORDER_WIDE, ShownBorders.ALL);

    t.setColumnWidth(0, 20, 30);
    t.setColumnWidth(1, 20, 30);
    t.setColumnWidth(2, 20, 30);
    t.setColumnWidth(3, 20, 30);
    t.setColumnWidth(4, 20, 30);

    t.addCell("\033[32mID\033[0m", numberStyle);
    t.addCell("\033[32mNAME\033[0m", numberStyle);
    t.addCell("\033[32mUnit Price\033[0m", numberStyle);
    t.addCell("\033[32mQty\033[0m", numberStyle);
    t.addCell("\033[32mImportDate\033[0m", numberStyle);

    products.forEach(product -> {
      t.addCell(String.valueOf(product.getId()), numberStyle);
      t.addCell(String.valueOf(product.getName()), numberStyle);
      t.addCell(String.valueOf(product.getUnitPrice()), numberStyle);
      t.addCell(String.valueOf(product.getStockQty()), numberStyle);
      t.addCell(String.valueOf(product.getImportDate()), numberStyle);
    });

    System.out.println(t.render());
    System.out.println("Press \"ENTER\" to continue...");
    scanner.nextLine();
  }

  // Handle update menu
  private void handleUpdateMenu(ProductController controller) throws SQLException {
    int productId = v.validateInput(c.INT, "=> Enter ID to update: ", Integer::parseInt);
    Product product = controller.displayUpdateProductTable(productId);

    if (product == null) {
      System.out.println(c.RED + "Product not found!" + c.RESET);
      return;
    }

    CellStyle numberStyle = new CellStyle(CellStyle.HorizontalAlign.center);
    Table t = new Table(5, BorderStyle.UNICODE_BOX_DOUBLE_BORDER_WIDE, ShownBorders.ALL);

    t.setColumnWidth(0, 20, 30);
    t.setColumnWidth(1, 20, 30);
    t.setColumnWidth(2, 20, 30);
    t.setColumnWidth(3, 20, 30);
    t.setColumnWidth(4, 20, 30);

    t.addCell("\033[32mID\033[0m", numberStyle);
    t.addCell("\033[32mNAME\033[0m", numberStyle);
    t.addCell("\033[32mUnit Price\033[0m", numberStyle);
    t.addCell("\033[32mQty\033[0m", numberStyle);
    t.addCell("\033[32mImportDate\033[0m", numberStyle);

    t.addCell(String.valueOf(product.getId()), numberStyle);
    t.addCell(String.valueOf(product.getName()), numberStyle);
    t.addCell(String.valueOf(product.getUnitPrice()), numberStyle);
    t.addCell(String.valueOf(product.getStockQty()), numberStyle);
    t.addCell(String.valueOf(product.getImportDate()), numberStyle);

    System.out.println(t.render());

    breakWhile:
    while (true) {
      System.out.print("1. Name\t\t");
      System.out.print("2. Unit Price\t\t");
      System.out.print("3. Qty\t\t");
      System.out.print("4. All Fields\t\t");
      System.out.print("5. Exit\n");

      int updateOption = v.validateInput(c.INT, c.YELLOW + "=> Choose an Option to update : " + c.RESET, Integer::parseInt);


      switch (updateOption) {
        case 1:
          String newName = v.validateInput(c.PRODUCT_NAME_REGEX, "=> Enter new name : ", s -> s).trim();
          controller.updateProductTable(productId, newName, null, null);
          break;
        case 2:
          Double newPrice = v.validateInput(c.PRODUCT_UNIT_PRICE, "=> Enter new price : ", Double::parseDouble);
          controller.updateProductTable(productId, null, newPrice, null);
          break;
        case 3:
          Integer newStock = v.validateInput(c.PRODUCT_UNIT_PRICE, "=> Enter new stock : ", Integer::parseInt);
          controller.updateProductTable(productId, null, null, newStock);
          break;
        case 4:
          String updateName = v.validateInput(c.PRODUCT_NAME_REGEX, "=> Enter new name : ", s -> s).trim();
          Double updatePrice = v.validateInput(c.PRODUCT_UNIT_PRICE, "=> Enter new price : ", Double::parseDouble);
          Integer updateStock = v.validateInput(c.PRODUCT_UNIT_PRICE, "=> Enter new stock : ", Integer::parseInt);

          controller.updateProductTable(productId, updateName, updatePrice, updateStock);

          break;
        case 5:
          break breakWhile;
      }
    }

  }

  // Handle get product by id
  public void handleGetProductById(ProductController controller) throws SQLException {
    int productId = v.validateInput(c.INT, "=> Please input id to get record : ", Integer::parseInt);

    Product product = controller.fetchProductById(productId);

    if (product == null) {
      System.out.println(c.RED + "Product not found!" + c.RESET);
      return;
    }

    CellStyle numberStyle = new CellStyle(CellStyle.HorizontalAlign.center);
    Table t = new Table(5, BorderStyle.UNICODE_BOX_DOUBLE_BORDER_WIDE, ShownBorders.ALL);

    t.setColumnWidth(0, 20, 30);
    t.setColumnWidth(1, 20, 30);
    t.setColumnWidth(2, 20, 30);
    t.setColumnWidth(3, 20, 30);
    t.setColumnWidth(4, 20, 30);

    t.addCell("\033[32mID\033[0m", numberStyle);
    t.addCell("\033[32mNAME\033[0m", numberStyle);
    t.addCell("\033[32mUnit Price\033[0m", numberStyle);
    t.addCell("\033[32mQty\033[0m", numberStyle);
    t.addCell("\033[32mImportDate\033[0m", numberStyle);

    t.addCell(String.valueOf(product.getId()), numberStyle);
    t.addCell(String.valueOf(product.getName()), numberStyle);
    t.addCell(String.valueOf(product.getUnitPrice()), numberStyle);
    t.addCell(String.valueOf(product.getStockQty()), numberStyle);
    t.addCell(String.valueOf(product.getImportDate()), numberStyle);

    System.out.println(t.render());
    System.out.println("Press \"ENTER\" to continue...");
    scanner.nextLine();
  }

  // Handle delete product by id
  public void handleDeleteProductById(ProductController controller) throws SQLException {
    int productId = v.validateInput(c.INT, "=> Please input id to delete product : ", Integer::parseInt);
    Product product = controller.displayDeleteProductById(productId);

    if (product == null) {
      System.out.println(c.RED + "Product not found!" + c.RESET);

      return;
    }

    CellStyle numberStyle = new CellStyle(CellStyle.HorizontalAlign.center);
    Table t = new Table(5, BorderStyle.UNICODE_BOX_DOUBLE_BORDER_WIDE, ShownBorders.ALL);

    t.setColumnWidth(0, 20, 30);
    t.setColumnWidth(1, 20, 30);
    t.setColumnWidth(2, 20, 30);
    t.setColumnWidth(3, 20, 30);
    t.setColumnWidth(4, 20, 30);

    t.addCell("\033[32mID\033[0m", numberStyle);
    t.addCell("\033[32mNAME\033[0m", numberStyle);
    t.addCell("\033[32mUnit Price\033[0m", numberStyle);
    t.addCell("\033[32mQty\033[0m", numberStyle);
    t.addCell("\033[32mImportDate\033[0m", numberStyle);

    t.addCell(String.valueOf(product.getId()), numberStyle);
    t.addCell(String.valueOf(product.getName()), numberStyle);
    t.addCell(String.valueOf(product.getUnitPrice()), numberStyle);
    t.addCell(String.valueOf(product.getStockQty()), numberStyle);
    t.addCell(String.valueOf(product.getImportDate()), numberStyle);

    System.out.println(t.render());
    String choice = v.validateInput(c.ARE_YOU_SURE, c.YELLOW + "=> Are you sure to delete product id : " + productId + " ? (y/n) : " + c.RESET, s -> s).trim().toUpperCase();
    switch (choice) {
      case "Y":
        controller.deleteProductById(productId);
        break;
      case "N":
        break;
    }

  }

  // Handle get product by name
  public void handleSearchByName(ProductController controller) {
    String keyword = v.validateInput(c.ONLY_STRING_NUMBER, "=> Enter a keyword to search for products: ", s -> s).trim();

    if (keyword.isEmpty()) {
      System.out.println(c.RED + "Search term cannot be empty." + c.RESET);

      return;
    }

    controller.searchProductsByName(keyword);
  }

  // Display products by name
  public void displayProductsByName(List<Product> products) {
    if (products == null || products.isEmpty()) {
      System.out.println(c.RED + "No products available!" + c.RESET);

      return;
    }

    CellStyle numberStyle = new CellStyle(CellStyle.HorizontalAlign.center);

    Table t = new Table(5, BorderStyle.UNICODE_BOX_DOUBLE_BORDER_WIDE, ShownBorders.SURROUND_HEADER_AND_COLUMNS);

    t.setColumnWidth(0, 20, 30);
    t.setColumnWidth(1, 20, 30);
    t.setColumnWidth(2, 20, 30);
    t.setColumnWidth(3, 20, 30);
    t.setColumnWidth(4, 20, 30);

    t.addCell("\033[32mID\033[0m", numberStyle);
    t.addCell("\033[32mNAME\033[0m", numberStyle);
    t.addCell("\033[32mUnit Price\033[0m", numberStyle);
    t.addCell("\033[32mQty\033[0m", numberStyle);
    t.addCell("\033[32mImportDate\033[0m", numberStyle);

    products.forEach(product -> {
      t.addCell(String.valueOf(product.getId()), numberStyle);
      t.addCell(String.valueOf(product.getName()), numberStyle);
      t.addCell(String.valueOf(product.getUnitPrice()), numberStyle);
      t.addCell(String.valueOf(product.getStockQty()), numberStyle);
      t.addCell(String.valueOf(product.getImportDate()), numberStyle);
    });

    System.out.println(t.render());
    System.out.println("Press \"ENTER\" to continue...");
    scanner.nextLine();
  }

  // Update row limit and save it to the database
  private void handleSetRowLimit(ProductController controller) {
    String input = v.validateInput(c.INT, "Enter the new row limit : ", s -> s);
    try {
      int newRowLimit = Integer.parseInt(input);
      if (newRowLimit <= 0) {
        System.out.println(c.RED + "Row limit must be greater than 0." + c.RESET);

        return;
      }
      controller.updateRowLimit(newRowLimit);
      this.rowLimit = newRowLimit;
      System.out.println(c.GREEN + "Row limit updated successfully." + c.RESET);
    } catch (NumberFormatException e) {
      System.out.println(c.RED + "Invalid input. Please enter a valid number." + c.RESET);

    }
  }
}
