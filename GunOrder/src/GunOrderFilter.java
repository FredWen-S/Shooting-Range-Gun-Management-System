import java.sql.*;

public class GunOrderFilter {
  private static String url = "jdbc:mysql://localhost:3306/armoury";
  private static String user = "root";
  private static String pass = "123456";
  private static Connection con;
  private int age;

  public GunOrderFilter(int age) {
    this.age = age;
  }

  public void filterByColumn(int filterMethod, String filterValue) {
    // 根据不同的过滤方法选择不同的显示
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
      con = DriverManager.getConnection(url, user, pass);

      switch (filterMethod) {
        case 0:
          displayTable("idGuns_order", filterValue);
          break;
        case 1:
          displayTable("nameGuns_order", filterValue);
          break;
        case 2:
          displayTable("ammoGuns_order", filterValue);
          break;
        case 3:
          displayTableWithPriceFilter(Double.parseDouble(filterValue));
          break;
        case 4:
          displayTable("typeGuns_order", filterValue);
          break;
        default:
          System.out.println("Wrong method");
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        if (con != null) {
          con.close();
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  private void displayTable(String columnName, String filterValue) throws SQLException {
    // 显示表格
    String sql = "SELECT * FROM guns_order WHERE " + columnName + " LIKE ?";
    try (PreparedStatement ptmt = con.prepareStatement(sql)) {
      ptmt.setString(1, "%" + filterValue + "%");
      try (ResultSet rs = ptmt.executeQuery()) {
        displayResultSet(rs);
      }
    }
  }

  private void displayTableWithPriceFilter(double maxPrice) throws SQLException {
    // 找出价格低于输入值的记录并显示
    String sql = "SELECT * FROM guns_order WHERE priceGuns_order <= ? AND ageGuns_order <= ?";
    try (PreparedStatement ptmt = con.prepareStatement(sql)) {
      ptmt.setDouble(1, maxPrice);
      ptmt.setInt(2, age);
      try (ResultSet rs = ptmt.executeQuery()) {
        displayResultSet(rs);
      }
    }
  }

  private void displayResultSet(ResultSet rs) throws SQLException {
    // 显示整个表格
    System.out.println("idGuns_order\tnameGuns_order\tammoGuns_order"
            + "\tageGuns_order\tpriceGuns_order\ttypeGuns_order");
    while (rs.next()) {
      int id = rs.getInt("idGuns_order");
      String nameGuns = rs.getString("nameGuns_order");
      String ammoGuns = rs.getString("ammoGuns_order");
      int ageGuns = rs.getInt("ageGuns_order");
      double priceGuns = rs.getDouble("priceGuns_order");
      String typeGuns = rs.getString("typeGuns_order");

      System.out.println(
              id + "\t\t" + nameGuns + "\t\t" + ammoGuns + "\t\t" + ageGuns
                      + "\t\t" + priceGuns + "\t\t" + typeGuns);
    }
  }
  public ResultSet getFilteredResultSet() throws SQLException {
    // Returns the filtered ResultSet obtained from the filterByColumn method
    String sql = "SELECT * FROM guns_order";
    try (PreparedStatement ptmt = con.prepareStatement(sql)) {
      try (ResultSet rs = ptmt.executeQuery()) {
        return rs;
      }
    }
  }

}
