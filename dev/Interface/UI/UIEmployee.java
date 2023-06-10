package Interface.UI;

import Service.HRManagerService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class UIEmployee extends JFrame {
    private JTextField idField;
    private JButton loginButton;
    private HRManagerService HRManagerService;

    private JButton viewWeeklyReqButton;
    private JButton updateButton;
    private JButton showWeeklyIncomeButton;

    private Map<String, Double> weeklyIncomeMap;
    private String loggedInId;

    public UIEmployee() throws SQLException {
        HRManagerService = new HRManagerService();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Employee Interface");
        setSize(400, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Create components
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1));

        idField = new JTextField();
        loginButton = new JButton("Log In");
        viewWeeklyReqButton = new JButton("View Weekly Requests");
        updateButton = new JButton("Update When Available");
        showWeeklyIncomeButton = new JButton("Show My Weekly Income");

        // Add components to the panel
        panel.add(idField);
        panel.add(loginButton);

        // Add action listeners
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = idField.getText().trim();
                if (!id.isEmpty()) {
                    loggedInId = id;
                    showEmployeeInterface();
                } else {
                    JOptionPane.showMessageDialog(UIEmployee.this, "Please enter your ID.");
                }
            }
        });

        // Set the panel as the content pane
        setContentPane(panel);
    }

    private void showEmployeeInterface() {
        // Update UI for employee interface
        getContentPane().removeAll();

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1));

        panel.add(viewWeeklyReqButton);
        panel.add(updateButton);
        panel.add(showWeeklyIncomeButton);

        viewWeeklyReqButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create input fields
                JTextField weekNumberField = new JTextField();
                JTextField yearNumberField = new JTextField();
                JTextField superNumberField = new JTextField();

                // Create the panel to hold the input fields
                JPanel inputPanel = new JPanel(new GridLayout(3, 2));
                inputPanel.add(new JLabel("Week Number:"));
                inputPanel.add(weekNumberField);
                inputPanel.add(new JLabel("Year Number:"));
                inputPanel.add(yearNumberField);
                inputPanel.add(new JLabel("Super Number:"));
                inputPanel.add(superNumberField);

                // Show the input dialog and get the user's input
                int result = JOptionPane.showConfirmDialog(panel, inputPanel, "Enter Weekly Shift Details",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                // If the user clicked "OK"
                if (result == JOptionPane.OK_OPTION) {
                    // Get the values from the input fields
                    int weekNum = Integer.parseInt(weekNumberField.getText());
                    int yearNum = Integer.parseInt(yearNumberField.getText());
                    int superNum = Integer.parseInt(superNumberField.getText());

                    // Create an instance of UIWeeklyShiftReq with the provided input values
                    try {
                        UIWeeklyShiftReq weeklyShiftReq = new UIWeeklyShiftReq(weekNum, yearNum, superNum,true);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }

        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddavilableforemployeeDialog();
            }
        });

        showWeeklyIncomeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create a dialog to get the week number, year number, and super number
                JTextField weekField = new JTextField();
                JTextField yearField = new JTextField();
                JTextField superNumberField = new JTextField();

                JPanel panel = new JPanel(new GridLayout(3, 2));
                panel.add(new JLabel("Week Number:"));
                panel.add(weekField);
                panel.add(new JLabel("Year Number:"));
                panel.add(yearField);

                int result = JOptionPane.showConfirmDialog(UIEmployee.this, panel, "Enter Details", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    // Get the input values
                    int weekNumber = Integer.parseInt(weekField.getText().trim());
                    int yearNumber = Integer.parseInt(yearField.getText().trim());

                    // Call the function with the input values
                    int income = getWeeklyIncome(weekNumber, yearNumber);
                    JOptionPane.showMessageDialog(UIEmployee.this, "Your weekly income is: $" + income);
                }
            }
        });

        setContentPane(panel);
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                UIEmployee employeeUI = null;
                try {
                    employeeUI = new UIEmployee();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                employeeUI.setVisible(true);
            }
        });
    }
    private void showAddavilableforemployeeDialog() {
        JTextField idField = new JTextField();
        JTextField daynumField = new JTextField();
        JTextField nordField = new JTextField();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("day num Info:"));
        panel.add(daynumField);
        panel.add(new JLabel("night or day:"));
        panel.add(nordField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Add available for employee", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(null, "Added available for employee!");
            // Get the values from the text fields
            int id = Integer.parseInt(this.loggedInId);
            int daynum = Integer.parseInt(daynumField.getText());
            String nordInfo = nordField.getText();

            // Call the addEmployee function with the gathered input
            HRManagerService.addavilableforemployee(id,daynum,nordInfo);
        }
    }
    private int getWeeklyIncome(int weekNum,int yearNum){
        return HRManagerService.getwageforemployee(Integer.parseInt(this.loggedInId),weekNum,yearNum);
    }

}

