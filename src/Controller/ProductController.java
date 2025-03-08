package Controller;

import Constant.Constant;
import Model.BackupFile;
import Model.Product;
import Model.ProductImplement;
import Utils.Validation;
import View.ProductView;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class ProductController {
  private ProductImplement pim;
  private ProductView pv;
  private final ArrayList<Product> tempInsertProducts;
  private final ArrayList<Product> tempUpdateProducts;
  private final String backupDirectory = "backups/";
  Validation v = new Validation();
  Constant c = new Constant();


  public ProductController(ProductImplement pim, ProductView pv) {
    this.pim = pim;
    this.pv = pv;
    this.tempInsertProducts = new ArrayList<>();
    this.tempUpdateProducts = new ArrayList<>();
    createBackupDirectoryIfNotExists();
  }

  //Create backup directory if it doesn't exist (Work with file)
  private void createBackupDirectoryIfNotExists() {
    File directory = new File(backupDirectory);
    if (!directory.exists()) {
      directory.mkdirs();
    }
  }

  //start an application
  public void startApplication() {
    try {
      List<Product> products = pim.getAllProducts();

      pv.displayAllProductsAndMenu(products, this);
    } catch (Exception e) {
      System.err.println("An error occurred: " + e.getMessage());
    }
  }

  public int generateNewProductId(List<Product> products, List<Product> tempoLists) {
    int maxDbId = products.stream()
            .mapToInt(Product::getId)
            .max()
            .orElse(0);

    int maxTempId = tempoLists.stream()
            .mapToInt(Product::getId)
            .max()
            .orElse(0);

    return Math.max(maxDbId, maxTempId) + 1;

  }

  //Create new Product
  public void createNewProduct() {
    try {

      ArrayList<Product> initialProductLists = pim.getAllProducts();
      int newId = generateNewProductId(initialProductLists, tempInsertProducts);

      Product product = pv.getInputForNewProduct(newId);

      product.setId(newId);

      product.setImportDate(LocalDate.now());

      tempInsertProducts.add(product);

      System.out.println("Product added to the temporary list. Use 'Save' to push to the database.");
    } catch (Exception e) {
      System.err.println("An error occurred while creating the product: " + e.getMessage());
    }
  }

  //Display temporarily product for insertion
  public void displayUnsavedProductsForInsert() {
    if (tempInsertProducts.isEmpty()) {
      System.out.println("No unsaved products available for insertion.");
      return;
    }

    System.out.println("Unsaved Products Available for Insertion:");
    pv.displayProducts(tempInsertProducts);
  }


  //Display temporarily product for update
  public void displayUnsavedProductsForUpdate() {
    if (tempUpdateProducts.isEmpty()) {
      System.out.println("No unsaved products available for update.");
      return;
    }

    System.out.println("Unsaved Products Available for Update:");
    pv.displayProducts(tempUpdateProducts); // Display the unsaved products
  }

  // Method to push (insertion) unsaved product to the database
  public void saveUnsavedInsertProducts() {
    if (tempInsertProducts.isEmpty()) {
      System.out.println("No unsaved insert products to save.");
      return;
    }

    try {
      for (Product product : tempInsertProducts) {
        pim.addProduct(product); // Insert each product into the database
      }

      System.out.println(tempInsertProducts.size() + " insert product(s) saved to the database.");
      tempInsertProducts.clear(); // Clear the temporary insert list after saving

      // Refetch the latest data from the database
      List<Product> updatedProducts = pim.getAllProducts();
      System.out.println("Data refetched from the database.");

      // Pass the updated products back to the view
      pv.displayAllProductsAndMenu(updatedProducts, this);
    } catch (Exception e) {
      System.err.println("An error occurred while saving insert products: " + e.getMessage());
    }
  }

  //Check if product exist in temporarily list
  private boolean productExistsInTempUpdateList(int productId) {
    return tempUpdateProducts.stream()
            .anyMatch(product -> product.getId() == productId);
  }

  //Display and Handle the Table of Product Update
  public Product displayUpdateProductTable(int productId) throws SQLException {

    boolean isProductExistsInTempUpdateList = productExistsInTempUpdateList(productId);

    Optional<Product> productToUpdate;
    if (isProductExistsInTempUpdateList) {
      productToUpdate = tempUpdateProducts.stream()
              .filter(product -> product.getId() == productId)
              .findFirst();
    } else {
      productToUpdate = pim.getAllProducts().stream()
              .filter(product -> product.getId() == productId)
              .findFirst();
    }

    if (productToUpdate.isPresent()) {
      Product product = productToUpdate.get();

      if (!tempUpdateProducts.contains(product)) {
        tempUpdateProducts.add(product);
      }

      return product;
    } else {
      System.out.println("Product with ID " + productId + " not found.");
      return null;
    }

  }

  //Update Product and push it to the temporarily list
  public void updateProductTable(int productId, String name, Double unitPrice, Integer stockQty) {

    Optional<Product> productToUpdate = tempUpdateProducts.stream()
            .filter(product -> product.getId() == productId)
            .findFirst();

    if (productToUpdate.isPresent()) {
      // Update the existing product
      Product product = productToUpdate.get();
      if (name != null) {
        product.setName(name);
      }
      if (unitPrice != null) {
        product.setUnitPrice(unitPrice);
      }
      if (stockQty != null) {
        product.setStockQty(stockQty);
      }

      System.out.println("Product updated in the temporary update list.");
    } else {
      // Fetch the product from the database and add it to the temporary update list
      try {
        Product product = displayUpdateProductTable(productId);
        if (product != null) {
          if (name != null) {
            product.setName(name);
          }
          if (unitPrice != null) {
            product.setUnitPrice(unitPrice);
          }
          if (stockQty != null) {
            product.setStockQty(stockQty);
          }

          tempUpdateProducts.add(product); // Add the updated product to the list
          System.out.println("Product added to the temporary update list.");
        }
      } catch (SQLException e) {
        System.err.println("An error occurred while fetching the product: " + e.getMessage());
      }
    }
  }

  // Save unsaved (update) products to the database
  public void saveUnsavedUpdateProducts() {
    if (tempUpdateProducts.isEmpty()) {
      System.out.println("No unsaved update products to save.");
      return;
    }

    try {
      for (Product product : tempUpdateProducts) {
        // Extract fields from the product
        int productId = product.getId();
        String name = product.getName();
        Double unitPrice = product.getUnitPrice();
        Integer stockQty = product.getStockQty();

        // Call the DAO method to update the product in the database
        pim.updateProduct(productId, name, unitPrice, stockQty);
      }

      System.out.println(tempUpdateProducts.size() + " update product(s) saved to the database.");
      tempUpdateProducts.clear(); // Clear the temporary update list after saving

      // Refetch the latest data from the database
      List<Product> updatedProducts = pim.getAllProducts();
      pv.displayAllProductsAndMenu(updatedProducts, this);
    } catch (Exception e) {
      System.err.println("An error occurred while saving update products: " + e.getMessage());
    }
  }

  //Fetch Product By ID
  public Product fetchProductById(int productId) throws SQLException {

    Optional<Product> product = pim.getAllProducts().stream().filter(productFilter -> productFilter.getId() == productId).findFirst();

    return product.orElse(null);
  }

  //Display Delete Product By ID Table
  public Product displayDeleteProductById(int productId) throws SQLException {
    Optional<Product> product = pim.getAllProducts().stream().filter(productFilter -> productFilter.getId() == productId).findFirst();

    return product.orElse(null);
  }

  //Delete product based on ID in database
  public void deleteProductById(int productId) throws SQLException {
    pim.deleteProductById(productId);

    // Refetch the latest data from the database
    List<Product> updatedProducts = pim.getAllProducts();
    System.out.println("Data refetched from the database.");

    pv.displayAllProductsAndMenu(updatedProducts, this);
  }

  //Search product by name
  public void searchProductsByName(String name) {
    try {
      List<Product> products = pim.searchProductByName(name);

      if (products.isEmpty()) {
        System.out.println("No products found matching the search term: " + name);
      } else {
        System.out.println("Search Results : ");
        pv.displayProductsByName(products);
      }
    } catch (SQLException e) {
      System.err.println("An error occurred while searching for products: " + e.getMessage());
    }
  }

  //Get row-limit
  public int getRowLimit() {
    try {
      return pim.getRowLimit();
    } catch (SQLException e) {
      System.err.println("An error occurred while fetching the row limit: " + e.getMessage());
      return 3; // Default value
    }
  }

  //Update row-limit
  public void updateRowLimit(int rowLimit) {
    try {
      pim.updateRowLimit(rowLimit);
      System.out.println("Row limit updated successfully.");
    } catch (SQLException e) {
      System.err.println("An error occurred while updating the row limit: " + e.getMessage());
    }
  }

  // Generate a backup filename with version and date
  private String generateBackupFileName() {
    File directory = new File(backupDirectory);
    File[] files = directory.listFiles((dir, name) -> name.startsWith("Version-"));

    int version = 1;
    if (files != null && files.length > 0) {
      version = Arrays.stream(files).map(file -> file.getName().split("-")[1]).mapToInt(Integer::parseInt).max().orElse(0) + 1;
    }

    return String.format(
            "Version-%d-product-backup-%s.sql",
            version,
            java.time.LocalDate.now()
    );
  }

  // Execute backup file using pg_dump
  private void executeBackup(String backupFileName) {
    String backupFilePath = backupDirectory + backupFileName; // Prepend the directory

    try {
      ProcessBuilder processBuilder = new ProcessBuilder(
              "C:\\Program Files\\PostgreSQL\\17\\bin\\pg_dump", // Full path to pg_dump
              "--dbname=postgresql://postgres:085760561Both@localhost:5432/stock",
              "--file=" + backupFilePath,
              "--format=custom"
      );
      Process process = processBuilder.start();
      int exitCode = process.waitFor();

      if (exitCode == 0) {
        System.out.println("Backup completed successfully : " + backupFilePath);
      } else {
        System.err.println("Backup failed. Exit code: " + exitCode);
      }

    } catch (Exception e) {
      System.err.println("Backup error: " + e.getMessage());
    }
  }

  // Handle backup operation
  public void handleBackup() {
    String choice = v.validateInput(c.ARE_YOU_SURE, "=> Are you sure you want to backup the data? (y/n) : ", s -> s).trim().toUpperCase();

    if (choice.equals("Y")) {
      String backupFileName = generateBackupFileName();
      executeBackup(backupFileName);
    }
  }

  // Handle restore operation
  public void handleRestore() {
    List<BackupFile> backupFiles = listBackupFiles();
    if (backupFiles.isEmpty()) {
      System.out.println("No backups available!");
    }
    pv.displayBackupFiles(backupFiles);

    int maxAttempts = 3;
    int attemptCount = 0;

    while (attemptCount < maxAttempts) {
      try {
        int restore_id = v.validateInput(c.INT, "=> Enter backup_id to restore : ", Integer::parseInt);
        if (restore_id > 0 && restore_id <= backupFiles.size()) {
          BackupFile selectedBackup = backupFiles.get(restore_id - 1);
          //Execute restore
          executeRestore(selectedBackup.getFilePath());
          try {
            List<Product> products = pim.getAllProducts();
            pv.displayAllProductsAndMenu(products, this);
          } catch (SQLException e) {
            System.err.println("An error occurred while refetching data: " + e.getMessage());
          }
          return;
        } else {
          System.out.println("Invalid choice! Please try again.");
          attemptCount++;
        }
      } catch (NumberFormatException e) {
        System.out.println("Invalid input! Please enter a valid number.");
        attemptCount++;
      }

      if(attemptCount >= maxAttempts){
        String choice = v.validateInput(c.ARE_YOU_SURE, "Do you want to continue (y/n) : ", s->s).toUpperCase();

        if(choice.equals("Y")){
          attemptCount = 0;
        }else {
          return;
        }
      }

    }
  }

  // List all backup files for Display
  private List<BackupFile> listBackupFiles() {
    File directory = new File(backupDirectory);
    File[] files = directory.listFiles((dir, name) -> name.startsWith("Version-"));

    List<BackupFile> backupFiles = new ArrayList<>();
    if (files != null) {
      for (File file : files) {
        backupFiles.add(new BackupFile(file.getName(), file.getAbsolutePath()));
      }
    }

    backupFiles.sort(Comparator.comparingInt(BackupFile::getVersion));

    return backupFiles;
  }

  // Execute restore using pg_restore
  public void executeRestore(String backupFilePath) {
    String choice = v.validateInput(c.ARE_YOU_SURE, "=> Are you sure you want to restore the data? (y/n) : ", s -> s).trim().toUpperCase();

    if (choice.equals("Y")) {
      try {
        ProcessBuilder processBuilder = new ProcessBuilder(
                "C:\\Program Files\\PostgreSQL\\17\\bin\\pg_restore",
                "--dbname=postgresql://postgres:085760561Both@localhost:5432/stock",
                "--clean",
                backupFilePath
        );
        Process process = processBuilder.start();
        int exitCode = process.waitFor();

        if (exitCode == 0) {
          System.out.println("Restore completed successfully!");
        } else {
          System.err.println("Restore failed. Exit code: " + exitCode);
        }
      } catch (Exception e) {
        System.err.println("Restore error: " + e.getMessage());
      }
    } else {
      System.out.println("Restore canceled.");
    }
  }

}
