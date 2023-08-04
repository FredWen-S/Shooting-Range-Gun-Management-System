import java.sql.*;
import java.util.Scanner;

public class GunOrderSelector {
  private static String url = "jdbc:mysql://localhost:3306/armoury";
  private static String user = "root";
  private static String pass = "123456";
  private static Connection con;
  private int age;
  private double totalPrice = 0.0;

  public GunOrderSelector(int age) {
    this.age = age;
  }

  public void selectItems() {
    // add items by id into user's cart and find the total price
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
      con = DriverManager.getConnection(url, user, pass);

      int state = 0; // Initial state

      while (true) {
        if (state == 0) {
          totalPrice = 0;
          System.out.println("Input the id of weapons(enter 'quit' to quit):");
          String inputStr = getInput();

          if (inputStr.equalsIgnoreCase("quit")) {
            break;
          }

          int id = Integer.parseInt(inputStr);

          double price = getPriceById(id);
          if (price == -1) {
            continue; // Skip to the next iteration if the item is not found.
          }

          totalPrice += price;

          if (totalPrice != 0.0) {
            System.out.println("Total Price(with tax) is :" + (totalPrice * 1.0625));
            System.out.println("Checkout? (enter 'checkout' to checkout ,enter 're' to rechoose,"
                    + " enter 'sec' to choose another): ");
            String option = getInput();
            if (option.equalsIgnoreCase("checkout")) {
              System.out.println("Final Price(with tax) :" + (totalPrice * 1.0625));
              break;
            } else if (option.equalsIgnoreCase("re")) {
              continue;
            } else if (option.equalsIgnoreCase("sec")) {
              state = 1; // Move to the second selection state
            }
          }
        } else if (state == 1) {
          System.out.println("second id ('clear' back to last step, 'checkout' to checkout): ");
          String inputStr = getInput();

          if (inputStr.equalsIgnoreCase("clear")) {
            state = 0; // Move back to the first selection state
          } else if (inputStr.equalsIgnoreCase("checkout")) {
            System.out.println("Final Price(with tax) is:" + (totalPrice * 1.0625));
            break;
          } else {
            int id = Integer.parseInt(inputStr);

            double price = getPriceById(id);
            if (price == -1) {
              continue; // Skip to the next iteration if the item is not found.
            }

            totalPrice += price;

            if (totalPrice != 0.0) {
              System.out.println("Total Price(with tax) is :" + (totalPrice * 1.0625));
              System.out.println("Checkout? ('checkout' to checkout, 'clear' to clear the list,"
                      + " 'last' to last step): ");
              String option = getInput();
              if (option.equalsIgnoreCase("checkout")) {
                System.out.println("Final Price(with tax) is :" + (totalPrice * 1.0625));
                break;
              } else if (option.equalsIgnoreCase("clear")) {
                totalPrice -= price;
                state = 0; // Move back to the first selection state
              } else if (option.equalsIgnoreCase("last")) {
                state = 1; // Stay in the second selection state
              }
            }
          }
        }
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

  private String getInput() {
    Scanner scanner = new Scanner(System.in);
    return scanner.nextLine();
  }

  private double getPriceById(int id) throws SQLException {
    // to get message from database and judge age
    String sql = "SELECT priceGuns_order, ageGuns_order, "
            + "nameGuns_order FROM guns_order WHERE idGuns_order = ?";
    try (PreparedStatement ptmt = con.prepareStatement(sql)) {
      ptmt.setInt(1, id);
      try (ResultSet rs = ptmt.executeQuery()) {
        if (rs.next()) {
          double price = rs.getDouble("priceGuns_order");
          int itemAge = rs.getInt("ageGuns_order");
          String itemName = rs.getString("nameGuns_order");

          if (age >= itemAge) {
            System.out.println("Already have: " + itemName);
            return price;
          } else {
            System.out.println("You are not allowed to choose this");
          }
        } else {
          System.out.println("Found no such a weapon");
        }
      }
    }

    return -1;
  }

  private int getIntInput() {
    Scanner scanner = new Scanner(System.in);
    int input = -1;

    while (!scanner.hasNextInt()) {
      scanner.nextLine(); // Clear the invalid input.
      System.out.println("Please reinput: ");
    }

    input = scanner.nextInt();
    scanner.nextLine(); // Clear the newline character.

    return input;
  }
}
