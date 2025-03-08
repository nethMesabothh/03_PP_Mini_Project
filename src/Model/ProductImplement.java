package Model;

import Constant.Constant;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductImplement {
  private Connection connection;
  Constant c = new Constant();

  public ProductImplement(Connection connection) {
    this.connection = connection;
  }

  // Get all products
  public ArrayList<Product> getAllProducts() throws SQLException {
    ArrayList<Product> products = new ArrayList<>();
    String sql = "SELECT * FROM products ORDER BY id";
    try (Statement statement = connection.createStatement();
         ResultSet resultSet = statement.executeQuery(sql)) {
      while (resultSet.next()) {
        Product product = new Product(
                resultSet.getString("name"),
                resultSet.getDouble("unit_price"),
                resultSet.getInt("stock_qty")
        );
        product.setId(resultSet.getInt("id"));
        product.setImportDate(resultSet.getDate("import_date").toLocalDate());
        products.add(product);
      }
    }
    return products;
  }

  // Add a new product
  public void addProduct(Product product) throws SQLException {
    String sql = "INSERT INTO products (name, unit_price, stock_qty) VALUES (?, ?, ?)";
    try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      preparedStatement.setString(1, product.getName());
      preparedStatement.setDouble(2, product.getUnitPrice());
      preparedStatement.setInt(3, product.getStockQty());
      preparedStatement.executeUpdate();

      ResultSet resultSet = preparedStatement.getGeneratedKeys();
      if (resultSet.next()) {
        product.setId(resultSet.getInt(1));
        product.setImportDate(resultSet.getDate("import_date").toLocalDate());
      }
    }
  }

  // Update a product
  public void updateProduct(int productId, String name, Double unitPrice, Integer stockQty) throws SQLException {
    StringBuilder sqlBuilder = new StringBuilder("UPDATE products SET ");

    boolean hasUpdated = false;

    if (name != null) {
      sqlBuilder.append("name = ?, ");
      hasUpdated = true;
    }
    if (unitPrice != null) {
      sqlBuilder.append("unit_price = ?, ");
      hasUpdated = true;
    }
    if (stockQty != null) {
      sqlBuilder.append("stock_qty = ?, ");
      hasUpdated = true;
    }

    if (!hasUpdated) {
      throw new IllegalArgumentException(c.RED + "No fields to update" + c.RESET);
    }

    sqlBuilder.setLength(sqlBuilder.length() - 2);

    sqlBuilder.append(" WHERE id = ?");

    try (PreparedStatement preparedStatement = connection.prepareStatement(sqlBuilder.toString())) {
      int paramIndex = 1;

      if (name != null) {
        preparedStatement.setString(paramIndex++, name);
      }
      if (unitPrice != null) {
        preparedStatement.setDouble(paramIndex++, unitPrice);

      }
      if (stockQty != null) {
        preparedStatement.setInt(paramIndex++, stockQty);
      }

      preparedStatement.setInt(paramIndex, productId);
      preparedStatement.executeUpdate();
    }
  }

  // Delete a product
  public void deleteProductById(int productId) throws SQLException {
    String sql = "DELETE FROM products WHERE id = ?";

    try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setInt(1, productId);
      int rowsAffected = preparedStatement.executeUpdate();

      if (rowsAffected > 0) {
        System.out.println(c.GREEN + "Product with ID " + productId + " deleted successfully." + c.RESET);
      } else {
        System.out.println(c.RED + "No product found with ID " + productId + ". Nothing was deleted." + c.RESET);
      }
    } catch (SQLException e) {
      throw new SQLException(c.RED + "An error occurred while deleting the product: " + e.getMessage() + c.RESET);
    }
  }

  // Get products by name
  public List<Product> searchProductByName(String name) throws SQLException {
    List<Product> products = new ArrayList<>();
    String sql = "SELECT * FROM products WHERE LOWER(name) LIKE ?";

    try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
      preparedStatement.setString(1, "%" + name.toLowerCase() + "%");
      ResultSet resultSet = preparedStatement.executeQuery();

      while(resultSet.next()){
        Product product = new Product(
                resultSet.getString("name"),
                resultSet.getDouble("unit_price"),
                resultSet.getInt("stock_qty")
        );
        product.setId(resultSet.getInt("id"));
        product.setImportDate(resultSet.getDate("import_date").toLocalDate());
        products.add(product);
      }

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return products;
  }

  // Fetch row limit from database
  public int getRowLimit() throws SQLException {
    String sql = "SELECT value FROM settings WHERE key_name = 'row_limit'";
    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      ResultSet resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        return Integer.parseInt(resultSet.getString("value"));
      } else {
        // Default row limit if not set
        return 3; // Default value
      }
    }
  }

  // Update row limit to the database
  public void updateRowLimit(int rowLimit) throws SQLException {
    String sql = "INSERT INTO settings (key_name, value) VALUES (?, ?) " +
            "ON CONFLICT (key_name) DO UPDATE SET value = ?";
    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setString(1, "row_limit");
      preparedStatement.setString(2, String.valueOf(rowLimit));
      preparedStatement.setString(3, String.valueOf(rowLimit));
      preparedStatement.executeUpdate();
    }
  }
}
