package Model;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class Product {
  private int id;
  private String name;
  private double unitPrice;
  private int stockQty;
  private LocalDate importDate;

  public Product(String name, double unitPrice, int stockQty) {

    this.name = name;
    this.unitPrice = unitPrice;
    this.stockQty = stockQty;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public double getUnitPrice() {
    return unitPrice;
  }

  public void setUnitPrice(double unitPrice) {
    this.unitPrice = unitPrice;
  }

  public int getStockQty() {
    return stockQty;
  }

  public void setStockQty(int stockQty) {
    this.stockQty = stockQty;
  }

  public LocalDate getImportDate() {
    return importDate;
  }

  public void setImportDate(LocalDate importDate) {
    this.importDate = importDate;
  }

  @Override
  public String toString() {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String formattedDate = (importDate != null) ? dateFormat.format(importDate) : "N/A";
    return id + " │ " + name + " │ " + unitPrice + " │ " + stockQty + " │ " + formattedDate;
  }
}
