import Controller.ProductController;
import Model.ProductImplement;
import Utils.DatabaseConnection;
import View.ProductView;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {
  public static void main(String[] args) {
    try(Connection connection = DatabaseConnection.getConnection()) {
      ProductImplement productImplement = new ProductImplement(connection);
      ProductView productView = new ProductView();
      ProductController productController = new ProductController(productImplement, productView);

      productView.initializeRowLimit(productController);
      productController.startApplication();

    }catch (SQLException e){
      throw new RuntimeException(e);
    }
  }
}
