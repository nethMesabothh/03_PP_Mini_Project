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

  Validation validation = new Validation();
  Constant constant = new Constant();

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
    System.out.println("Row limit initialized to: " + rowLimit);
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
          t.addCell(String.valueOf(product.getUnitPrice()), numberStyle);
          t.addCell(String.valueOf(product.getStockQty()), numberStyle);
          t.addCell(String.valueOf(product.getImportDate()), numberStyle);
        });
        int totalPages = (products.size() + rowLimit - 1) / rowLimit;

        t.addCell("Page : " + pageCount + " of " + totalPages, numberStyle, 2);
        t.addCell("Total Record : " + totalRecord, numberStyle, 3);

        System.out.println(t.render());


        System.out.print("\t\t\t\t\t\t\t------------------------ Menu ------------------------\n");

        System.out.print("\t\tN. Next Page\t\t");
        System.out.print("P. Previous Page\t\t");
        System.out.print("F. First Page\t\t");
        System.out.print("L. Last Page\t\t");
        System.out.print("G. Goto\t\t\n\n");

        System.out.print("W. Write Page\t\t");
        System.out.print("R. Read (id)\t\t");
        System.out.print("U. Update\t\t");
        System.out.print("D. Delete\t\t");
        System.out.print("S. Search (name)\t\t");
        System.out.print("Sr. Set rows\t\t\n");
        System.out.print("Sa. Save\t\t\t");
        System.out.print("Us. UnSave\t\t\t");
        System.out.print("Ba. Backup\t\t");
        System.out.print("Rs. Restore\t\t");
        System.out.print("E. Exit\n");
        System.out.print("\t\t\t\t\t\t\t------------------------------------------------\n");


        System.out.print("=> Choose an Option() : ");

        String choosePaginationAndMenu = scanner.nextLine().trim().toUpperCase();


//        String choosePaginationAndMenu = validation.validateInput(constant.choosePaginationAndMenuRegex, "=> Choose an Option() : ", string -> string ).toUpperCase();


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
              System.out.println("No data available!");
              continue;
            }
            int maxStartIndex = Math.max(0, lastIndex - (rowLimit - 1));

            if (currentStartIndex >= maxStartIndex) {

              System.out.println("You already on the last page.");
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
            try {
              System.out.print("Page number : ");
              int gotoSpecificPage = Integer.parseInt(scanner.nextLine().trim());

              if (gotoSpecificPage < 1 || gotoSpecificPage > totalPages) {
                System.out.println("Invalid page number. Please enter a number between 1 and " + totalPages);
              } else {
                currentStartIndex = (gotoSpecificPage - 1) * rowLimit;
                pageCount = gotoSpecificPage;
              }

            } catch (NumberFormatException nfe) {
              System.out.println("Invalid input. Please enter a valid number.");
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
  public void displayBackupFiles(List<BackupFile> backupFiles) {
    if (backupFiles.isEmpty()) {
      System.out.println("No backups available!");
    }

    CellStyle numberStyle = new CellStyle(CellStyle.HorizontalAlign.center);
    Table t = new Table(6, BorderStyle.UNICODE_BOX_DOUBLE_BORDER_WIDE, ShownBorders.SURROUND_HEADER_AND_COLUMNS);

    t.setColumnWidth(0, 10, 20);
    t.setColumnWidth(1, 20, 30);
    t.setColumnWidth(2, 20, 30);

    t.addCell("No. ", numberStyle,1);
    t.addCell("List of Backup Data", numberStyle,5);


    for (int i = 0; i < backupFiles.size(); i++) {
      BackupFile backup = backupFiles.get(i);
      t.addCell(String.valueOf(i + 1), numberStyle);
      t.addCell(backup.toString(), numberStyle ,5);
    }

    System.out.println(t.render());

  }

  // Get input for a new product
  public Product getInputForNewProduct(int generateNewId) {

    System.out.println("ID : " + generateNewId );

    System.out.print("Enter product name: ");
    String name = scanner.nextLine().trim();

    System.out.print("Enter unit price: ");
    double unitPrice = Double.parseDouble(scanner.nextLine().trim());

    System.out.print("Enter stock quantity: ");
    int stockQty = Integer.parseInt(scanner.nextLine().trim());

    return new Product(name, unitPrice, stockQty);
  }

  // Display the unsaved menu of update or insert
  private void handleUnSaveMenu(ProductController controller) {
    System.out.println("'ui' for saving insert products and 'uu' for saving update products or 'b' for back to menu");

    System.out.print("=> Choose an Option() : ");

    String unSaveOption = scanner.nextLine().trim().toUpperCase();

    switch (unSaveOption) {
      case "UI":
        controller.displayUnsavedProductsForInsert();
        break;

      case "UU":
        controller.displayUnsavedProductsForUpdate();
        break;

      case "B":
        System.out.println("Returning to main menu...");
        break;

      default:
        System.out.println("Invalid option. Please try again.");
    }
  }

  // Save data to the database (update or insert)
  private void handleSaveDataToDatabase(ProductController controller) {
    System.out.println("'si' for saving insert products and 'su' for saving update products or 'b' for back to menu");

    System.out.print("=> Choose an Option() : ");

    String saveDataOption = scanner.nextLine().trim().toUpperCase();

    switch (saveDataOption) {
      case "SI":
        controller.saveUnsavedInsertProducts();
        break;

      case "SU":
        controller.saveUnsavedUpdateProducts();
        break;

      case "B":
        System.out.println("Returning to main menu...");
        break;

      default:
        System.out.println("Invalid option. Please try again.");
    }
  }

  // Display products (used for unsaved products)
  public void displayProducts(List<Product> products) {
    if (products == null || products.isEmpty()) {
      System.out.println("No products available!");
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
    System.out.print("=> Enter ID to update: ");
    int productId = Integer.parseInt(scanner.nextLine().trim());

    Product product = controller.displayUpdateProductTable(productId);

    if (product == null) {
      System.out.println("Product not found!");
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
      System.out.print("=> Choose an Option to update : ");

      int updateOption = Integer.parseInt(scanner.nextLine().trim());


      switch (updateOption) {
        case 1:
          System.out.print("=> Enter new name : ");
          String newName = scanner.nextLine().trim();
          controller.updateProductTable(productId, newName, null, null);
          break;
        case 2:
          System.out.print("=> Enter new price : ");
          Double newPrice = scanner.nextDouble();
          scanner.nextLine();

          controller.updateProductTable(productId, null, newPrice, null);
          break;
        case 3:
          System.out.print("=> Enter new stock : ");
          Integer newStock = scanner.nextInt();
          scanner.nextLine();

          controller.updateProductTable(productId, null, null, newStock);
          break;
        case 4:
          System.out.print("=> Enter new name : ");
          String updateName = scanner.nextLine().trim();

          System.out.print("=> Enter new price : ");
          Double updatePrice = scanner.nextDouble();

          System.out.print("=> Enter new stock : ");
          Integer updateStock = scanner.nextInt();
          scanner.nextLine();

          controller.updateProductTable(productId, updateName, updatePrice, updateStock);

          break;
        case 5:
          break breakWhile;
      }
    }

  }

  // Handle get product by id
  public void handleGetProductById(ProductController controller) throws SQLException {
    System.out.print("=> Please input id to get record : ");
    int productId = scanner.nextInt();
    scanner.nextLine();

    Product product = controller.fetchProductById(productId);

    if (product == null) {
      System.out.println("Product not found!");
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
    System.out.print("=> Please input id to delete product : ");
    int productId = scanner.nextInt();
    scanner.nextLine();

    Product product = controller.displayDeleteProductById(productId);

    if (product == null) {
      System.out.println("Product not found!");
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
    System.out.print("=> Are you sure to delete product id : " + productId + "? (y/n) : ");
    String choice = scanner.nextLine().trim().toUpperCase();

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
    System.out.print("=> Enter a keyword to search for products: ");
    String keyword = scanner.nextLine().trim();

    if (keyword.isEmpty()) {
      System.out.println("Search term cannot be empty.");
      return;
    }

    controller.searchProductsByName(keyword);
  }

  // Display products by name
  public void displayProductsByName(List<Product> products) {
    if (products == null || products.isEmpty()) {
      System.out.println("No products available!");
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
    System.out.print("Enter the new row limit: ");
    String input = scanner.nextLine().trim();
    try {
      int newRowLimit = Integer.parseInt(input);
      if (newRowLimit <= 0) {
        System.out.println("Row limit must be greater than 0.");
        return;
      }
      controller.updateRowLimit(newRowLimit);
      this.rowLimit = newRowLimit;
      System.out.println("Row limit updated successfully.");
    } catch (NumberFormatException e) {
      System.out.println("Invalid input. Please enter a valid number.");
    }
  }
}
