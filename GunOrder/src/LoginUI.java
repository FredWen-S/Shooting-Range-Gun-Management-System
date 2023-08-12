import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class LoginUI extends JFrame {
    private static final String url = "jdbc:mysql://localhost:3306/armoury";
    private static final String user = "root";
    private static final String pass = "123456";
    private static Connection con;

    private JPanel loginPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signinButton;

    private JPanel signinPanel;
    private JTextField newUsernameField;
    private JTextField ageField;
    private JPasswordField newPasswordField;
    private JButton registerButton;
    private JPanel registerPanel; // Add registerPanel

    private JPanel mainPanel;
    private JLabel titleLabel;
    private JButton logoutButton;
    private JTabbedPane tabbedPane;
    private JPanel managementPanel; // Add managementPanel

    private String loggedInUser;

    public LoginUI() {
        initialize();
    }

    private void initialize() {
        setTitle("靶场枪支管理系统");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        loginPanel = createLoginPanel();
        signinPanel = createSigninPanel();
        mainPanel = createMainPanel();

        add(loginPanel, BorderLayout.CENTER);

        signinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showRegisterPanel();
            }
        });


        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
        

        setVisible(true);
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2));

        JLabel nameLabel = new JLabel("姓名：");
        usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("密码：");
        passwordField = new JPasswordField();
        loginButton = new JButton("登录");
        signinButton = new JButton("注册");

        panel.add(nameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(loginButton);
        panel.add(signinButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        signinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSigninPanel();
            }
        });

        return panel;
    }

    private JPanel createSigninPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2));

        JLabel newnameLabel = new JLabel("姓名：");
        newUsernameField = new JTextField();
        JLabel ageLabel = new JLabel("年龄：");
        ageField = new JTextField();
        JLabel newpasswordLabel = new JLabel("密码：");
        newPasswordField = new JPasswordField();
        registerButton = new JButton("注册");

        panel.add(newnameLabel);
        panel.add(newUsernameField);
        panel.add(ageLabel);
        panel.add(ageField);
        panel.add(newpasswordLabel);
        panel.add(newPasswordField);
        panel.add(registerButton);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                register();
            }
        });

        return panel;
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        titleLabel = new JLabel("靶场枪支管理系统");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        logoutButton = new JButton("退出登录");
        logoutButton.setEnabled(false);

        tabbedPane = new JTabbedPane();
        tabbedPane.add("库存管理", createInventoryPanel());
        tabbedPane.add("预约管理", createOrderPanel());

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(logoutButton, BorderLayout.SOUTH);
        panel.add(tabbedPane, BorderLayout.CENTER);

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });

        return panel;
    }

    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2));

        JLabel newnameLabel = new JLabel("姓名：");
        newUsernameField = new JTextField();
        JLabel ageLabel = new JLabel("年龄：");
        ageField = new JTextField();
        JLabel newpasswordLabel = new JLabel("密码：");
        newPasswordField = new JPasswordField();
        registerButton = new JButton("注册");

        panel.add(newnameLabel);
        panel.add(newUsernameField);
        panel.add(ageLabel);
        panel.add(ageField);
        panel.add(newpasswordLabel);
        panel.add(newPasswordField);
        panel.add(registerButton);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                register();
            }
        });

        return panel;
    }

    private JPanel createManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("管理面板");
        panel.add(titleLabel, BorderLayout.NORTH);

        JButton someButton = new JButton("Some Action");
        panel.add(someButton, BorderLayout.CENTER);

        // Add other components for management panel as needed

        return panel;
    }

    private JPanel createInventoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Add inventory management components here...
        JLabel titleLabel = new JLabel("Inventory Management");
        panel.add(titleLabel, BorderLayout.NORTH);

        // Add input fields and buttons for filtering
        JTextField nameFilterTextField = new JTextField(20);
        JTextField ageFilterTextField = new JTextField(5);
        JButton searchButton = new JButton("Search");

        JPanel filterPanel = new JPanel();
        filterPanel.add(new JLabel("Filter By Name: "));
        filterPanel.add(nameFilterTextField);
        filterPanel.add(new JLabel("Filter By Age: "));
        filterPanel.add(ageFilterTextField);
        filterPanel.add(searchButton);

        panel.add(filterPanel, BorderLayout.NORTH);

        // Add a table to display the results
        DefaultTableModel tableModel = new DefaultTableModel();
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Add action listener to the search button
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String nameFilterValue = nameFilterTextField.getText().trim();
                String ageFilterValue = ageFilterTextField.getText().trim();

                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    con = DriverManager.getConnection(url, user, pass);

                    String sql = "SELECT * FROM guns_order WHERE nameGuns_order LIKE ? AND ageGuns_order = ?";
                    try (PreparedStatement ptmt = con.prepareStatement(sql)) {
                        ptmt.setString(1, "%" + nameFilterValue + "%");
                        ptmt.setInt(2, Integer.parseInt(ageFilterValue));
                        try (ResultSet rs = ptmt.executeQuery()) {
                            tableModel.setRowCount(0);
                            while (rs.next()) {
                                Object[] row = new Object[]{
                                        rs.getInt("idGuns_order"),
                                        rs.getString("nameGuns_order"),
                                        rs.getString("ammoGuns_order"),
                                        rs.getInt("ageGuns_order"),
                                        rs.getDouble("priceGuns_order"),
                                        rs.getString("typeGuns_order")
                                };
                                tableModel.addRow(row);
                            }
                        }
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(panel, "Please enter a valid age");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        return panel;
    }

    private JPanel createOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Add order management components here...
        JLabel titleLabel = new JLabel("Order Management");
        panel.add(titleLabel, BorderLayout.NORTH);

        // Add input fields and buttons for ordering
        JTextField orderTextField = new JTextField(20);
        JButton orderButton = new JButton("Place Order");

        JPanel orderPanel = new JPanel();
        orderPanel.add(new JLabel("Order Gun: "));
        orderPanel.add(orderTextField);
        orderPanel.add(orderButton);

        panel.add(orderPanel, BorderLayout.CENTER);

        // Add a table to display the orders
        DefaultTableModel tableModel = new DefaultTableModel();
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.SOUTH);

        // Add action listener to the order button
        orderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String orderGun = orderTextField.getText().trim();
                // Implement order logic here...

                // Refresh the table with updated orders
                // Example code to add a new row to the table
                // tableModel.addRow(new Object[]{orderId, orderGun, orderDate, orderStatus});
            }
        });

        return panel;
    }



    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入姓名和密码");
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, user, pass);

            String sql = "SELECT * FROM user_info WHERE username=? AND password=?";
            try (PreparedStatement ptmt = con.prepareStatement(sql)) {
                ptmt.setString(1, username);
                ptmt.setString(2, password);
                try (ResultSet rs = ptmt.executeQuery()) {
                    if (rs.next()) {
                        loggedInUser = username;
                        JOptionPane.showMessageDialog(this, "登录成功");
                        showMainPanel(); // Show the main panel after successful login
                        logoutButton.setEnabled(true);
                    } else {
                        JOptionPane.showMessageDialog(this, "用户名或密码错误");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void register() {
        String username = newUsernameField.getText();
        String ageStr = ageField.getText();
        String password = new String(newPasswordField.getPassword());

        if (username.isEmpty() || ageStr.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入姓名、年龄和密码");
            return;
        }

        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(url, user, pass);

            int age = Integer.parseInt(ageStr);

            String sql = "INSERT INTO user_info (username, password, age) VALUES (?, ?, ?)";
            try (PreparedStatement ptmt = con.prepareStatement(sql)) {
                ptmt.setString(1, username);
                ptmt.setString(2, password);
                ptmt.setInt(3, age);
                ptmt.execute();
            }

            JOptionPane.showMessageDialog(this, "Registration successful, please log in");
            showLoginPanel();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid age");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Registration failed, username may already exist");
        } catch (Exception e) {
            e.printStackTrace();
        }
        JOptionPane.showMessageDialog(this, "Registration successful, please log in");
        showLoginPanel();
    }

    private void showLoginPanel() {
        loggedInUser = null;
        usernameField.setText("");
        passwordField.setText("");
        signinPanel.setVisible(false);
        loginPanel.setVisible(true);
        logoutButton.setEnabled(false);
        tabbedPane.setVisible(false);
        registerPanel.setVisible(false);
        managementPanel.setVisible(false);
    }

    private void showSigninPanel() {
        newUsernameField.setText("");
        ageField.setText("");
        newPasswordField.setText("");
        loginPanel.setVisible(false);
        signinPanel.setVisible(true);
    }
    private void showRegisterPanel() {
        if (registerPanel == null) {
            registerPanel = createRegisterPanel();
            add(registerPanel, BorderLayout.CENTER);
        }
        registerPanel.setVisible(true);
        loginPanel.setVisible(false);
        signinPanel.setVisible(false);
        tabbedPane.setVisible(false);
    }

    private void showManagementPanel() {
        if (managementPanel == null) {
            managementPanel = createManagementPanel();
            add(managementPanel, BorderLayout.CENTER);
        }
        managementPanel.setVisible(true);
        loginPanel.setVisible(false);
        signinPanel.setVisible(false);
        tabbedPane.setVisible(false);
        logoutButton.setEnabled(true);  // Enable the "Logout" button in the management panel
    }

    private void showMainPanel() {
        titleLabel.setText("靶场枪支管理系统 - 欢迎 " + loggedInUser);
        signinPanel.setVisible(false);
        loginPanel.setVisible(false);
        tabbedPane.setVisible(true);
        logoutButton.setEnabled(true);
    }


    private void logout() {
        showLoginPanel();
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LoginUI();
            }
        });
    }
}
