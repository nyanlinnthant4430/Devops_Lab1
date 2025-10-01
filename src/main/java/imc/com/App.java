package imc.com;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class App {
    /**
     * Connection to MySQL database.
     */
    private Connection con = null;

    /**
     * Connect to the MySQL database.
     */
    public void connect()
    {
        try
        {
            // Load Database driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("Could not load SQL driver");
            System.exit(-1);
        }

        int retries = 10;
        for (int i = 0; i < retries; ++i)
        {
            System.out.println("Connecting to database...");
            try
            {
                // Wait a bit for db to start
                Thread.sleep(10000);
                // Connect to database
                con = DriverManager.getConnection("jdbc:mysql://db:3306/employees?useSSL=false&allowPublicKeyRetrieval=true", "root", "example");
                System.out.println("Successfully connected");
                break;
            }
            catch (SQLException sqle)
            {
                System.out.println("Failed to connect to database attempt " + Integer.toString(i));
                System.out.println(sqle.getMessage());
            }
            catch (InterruptedException ie)
            {
                System.out.println("Thread interrupted? Should not happen.");
            }
        }
    }

    /**
     * Disconnect from the MySQL database.
     */
    public void disconnect()
    {
        if (con != null)
        {
            try
            {
                // Close connection
                con.close();
            }
            catch (Exception e)
            {
                System.out.println("Error closing connection to database");
            }
        }
    }

    public Employee getEmployee(int ID)
    {
        try
        {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect =
                    "SELECT emp_no, first_name, last_name "
                            + "FROM employees "
                            + "WHERE emp_no = " + ID;
            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Return new employee if valid.
            // Check one is returned
            if (rset.next())
            {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("emp_no");
                emp.first_name = rset.getString("first_name");
                emp.last_name = rset.getString("last_name");
                return emp;
            }
            else
                return null;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get employee details");
            return null;
        }
    }


    /**
     * Get employees with current salary by role.
     */
    public List<Employee> implementSalariesByRoleFeature(String role)
    {
        try
        {
            Statement stmt = this.con.createStatement();
            String strSelect = "SELECT e.emp_no, e.first_name, e.last_name, t.title, s.salary " +
                    "FROM employees e, salaries s, titles t " +
                    "WHERE e.emp_no = s.emp_no " +
                    "AND e.emp_no = t.emp_no " +
                    "AND s.to_date = '9999-01-01' " +
                    "AND t.to_date = '9999-01-01' " +
                    "AND t.title = '" + role + "' " +
                    "ORDER BY e.emp_no ASC";

            ResultSet rset = stmt.executeQuery(strSelect);
            List<Employee> employees = new ArrayList<>();

            while (rset.next())
            {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("emp_no");
                emp.first_name = rset.getString("first_name");
                emp.last_name = rset.getString("last_name");
                emp.title = rset.getString("title");
                emp.salary = rset.getInt("salary");
                employees.add(emp);
            }

            return employees;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get salaries by role");
            return null;
        }
    }

    /**
     * Display employees in tabular format (for salaries by role).
     */
    public void displayEmployeesByRole(List<Employee> employees)
    {
        if (employees != null && !employees.isEmpty())
        {
            for (Employee emp : employees)
            {
                System.out.printf("%-10d %-15s %-20s %-10d%n",
                        emp.emp_no,
                        emp.first_name,
                        emp.last_name,
                        emp.salary);
            }
        }
        else
        {
            System.out.println("No employees found for this role.");
        }
    }



    public void displayEmployee(Employee emp)
    {
        if (emp != null)
        {
            System.out.println(
                    emp.emp_no + " "
                            + emp.first_name + " "
                            + emp.last_name + "\n"
                            + emp.title + "\n"
                            + "Salary:" + emp.salary + "\n"
                            + emp.dept_name + "\n"
                            + "Manager: " + emp.manager + "\n");
        }
    }



    /**
     * Main entry point.
     */
    public static void main(String[] args)
    {
        App a = new App();

        // Connect to database
        a.connect();

        // Example 1: Get single employee by ID
        Employee emp = a.getEmployee(255530);
        a.displayEmployee(emp);

        // Example 2: Get employees by role and display table
        List<Employee> engineers = a.implementSalariesByRoleFeature("Engineer");
        a.displayEmployeesByRole(engineers);

        // Disconnect
        a.disconnect();
    }
}