package imc.com;

import java.sql.*;
import java.util.LinkedList;

public class App {
    /**
     * Connection to MySQL database.
     */
    private Connection con = null;

    /**
     * Connect to the MySQL database.
     */
    public void connect() {
        try {
            // Load Database driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Could not load SQL driver");
            System.exit(-1);
        }

        int retries = 10;
        for (int i = 0; i < retries; ++i) {
            System.out.println("Connecting to database...");
            try {
                // Wait a bit for db to start
                Thread.sleep(10000);
                // Connect to database
                con = DriverManager.getConnection(
                        "jdbc:mysql://db:3306/employees?useSSL=false&allowPublicKeyRetrieval=true",
                        "root", "example");
                System.out.println("Successfully connected");
                break;
            } catch (SQLException sqle) {
                System.out.println("Failed to connect to database attempt " + i);
                System.out.println(sqle.getMessage());
            } catch (InterruptedException ie) {
                System.out.println("Thread interrupted? Should not happen.");
            }
        }
    }

    /**
     * Disconnect from the MySQL database.
     */
    public void disconnect() {
        if (con != null) {
            try {
                con.close();
                System.out.println("Disconnected from database.");
            } catch (Exception e) {
                System.out.println("Error closing connection to database");
            }
        }
    }

    /**
     * Get employee details including their department and manager.
     */
    public Employee getEmployee(int ID) {
        try {
            String sql = "SELECT e.emp_no, e.first_name, e.last_name, d.dept_name, " +
                    "m.first_name AS manager_first, m.last_name AS manager_last " +
                    "FROM employees e " +
                    "JOIN dept_emp de ON e.emp_no = de.emp_no " +
                    "JOIN departments d ON de.dept_no = d.dept_no " +
                    "JOIN dept_manager dm ON d.dept_no = dm.dept_no " +
                    "JOIN employees m ON dm.emp_no = m.emp_no " +
                    "WHERE e.emp_no = ? " +
                    "AND de.to_date = '9999-01-01' " +
                    "AND dm.to_date = '9999-01-01'";

            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, ID);
            ResultSet rset = stmt.executeQuery();

            if (rset.next()) {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("emp_no");
                emp.first_name = rset.getString("first_name");
                emp.last_name = rset.getString("last_name");
                emp.dept_name = rset.getString("dept_name");
                emp.manager = rset.getString("manager_first") + " " + rset.getString("manager_last");
                return emp;
            }
        } catch (Exception e) {
            System.out.println("Failed to get employee details: " + e.getMessage());
        }
        return null;
    }

    /**
     * Gets all the current employees and salaries.
     */
    public LinkedList<Employee> getAllSalaries() {
        try {
            Statement stmt = con.createStatement();
            String sql = "SELECT e.emp_no, e.first_name, e.last_name, s.salary " +
                    "FROM employees e, salaries s " +
                    "WHERE e.emp_no = s.emp_no AND s.to_date = '9999-01-01' " +
                    "ORDER BY e.emp_no ASC";

            ResultSet rset = stmt.executeQuery(sql);

            LinkedList<Employee> employees = new LinkedList<>();
            while (rset.next()) {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("emp_no");
                emp.first_name = rset.getString("first_name");
                emp.last_name = rset.getString("last_name");
                emp.salary = rset.getInt("salary");
                employees.add(emp);
            }
            return employees;
        } catch (Exception e) {
            System.out.println("Failed to get salary details: " + e.getMessage());
            return null;
        }
    }

    /**
     * Get department details including its current manager.
     */
    public Department getDepartment(String dept_name) {
        Department dept = null;
        String sql = "SELECT d.dept_no, d.dept_name, e.first_name, e.last_name " +
                "FROM departments d " +
                "JOIN dept_manager dm ON d.dept_no = dm.dept_no " +
                "JOIN employees e ON dm.emp_no = e.emp_no " +
                "WHERE d.dept_name = ? " +
                "AND dm.to_date = '9999-01-01'";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, dept_name);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                dept = new Department();
                dept.dept_no = rs.getString("dept_no");
                dept.dept_name = rs.getString("dept_name");
                dept.manager = rs.getString("first_name") + " " + rs.getString("last_name");
            }
        } catch (SQLException e) {
            System.out.println("Failed to get department details: " + e.getMessage());
        }
        return dept;
    }

    /**
     * Gets all employees and their salaries for a specific department.
     */
    public LinkedList<Employee> getSalariesByDepartment(String deptNo) {
        LinkedList<Employee> employees = new LinkedList<>();

        String sql = "SELECT e.emp_no, e.first_name, e.last_name, s.salary " +
                "FROM employees e, salaries s, dept_emp de, departments d " +
                "WHERE e.emp_no = s.emp_no " +
                "AND e.emp_no = de.emp_no " +
                "AND de.dept_no = d.dept_no " +
                "AND s.to_date = '9999-01-01' " +
                "AND d.dept_no = ? " +
                "ORDER BY e.emp_no ASC";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, deptNo);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Employee emp = new Employee();
                emp.emp_no = rs.getInt("emp_no");
                emp.first_name = rs.getString("first_name");
                emp.last_name = rs.getString("last_name");
                emp.salary = rs.getInt("salary");
                employees.add(emp);
            }

        } catch (SQLException e) {
            System.out.println("Failed to get salary details by department: " + e.getMessage());
        }

        return employees;
    }

    /**
     * Get an employee by first and last name.
     */
    public Employee getEmployeeByName(String firstName, String lastName) {
        try {
            String sql = "SELECT emp_no, first_name, last_name " +
                    "FROM employees WHERE first_name = ? AND last_name = ?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Employee emp = new Employee();
                emp.emp_no = rs.getInt("emp_no");
                emp.first_name = rs.getString("first_name");
                emp.last_name = rs.getString("last_name");
                return emp;
            } else {
                System.out.println("No employee found with name: " + firstName + " " + lastName);
            }
        } catch (SQLException e) {
            System.out.println("Failed to get employee by name: " + e.getMessage());
        }
        return null;
    }

    public void displayEmployee(Employee emp) {
        if (emp != null) {
            System.out.println("---------------------------------");
            System.out.println("Employee No: " + emp.emp_no);
            System.out.println("Name       : " + emp.first_name + " " + emp.last_name);
            if (emp.dept_name != null)
                System.out.println("Department : " + emp.dept_name);
            if (emp.manager != null)
                System.out.println("Manager    : " + emp.manager);
            if (emp.salary != 0)
                System.out.println("Salary     : " + emp.salary);
            System.out.println("---------------------------------");
        }
    }

    public void printSalaries(LinkedList<Employee> employees) {
        System.out.println(String.format("%-10s %-15s %-20s %-8s", "Emp No", "First Name", "Last Name", "Salary"));
        for (Employee emp : employees) {
            System.out.println(String.format("%-10s %-15s %-20s %-8s",
                    emp.emp_no, emp.first_name, emp.last_name, emp.salary));
        }
    }

    /**
     * Main entry point.
     */
    public static void main(String[] args) {
        App a = new App();
        a.connect();

        // 1️⃣ Get all current salaries
        LinkedList<Employee> employees = a.getAllSalaries();
        if (employees != null) {
            System.out.println("\nAll current salaries: " + employees.size());
            a.printSalaries(employees);
        }

        // 2️⃣ Get salaries by department
        LinkedList<Employee> list = a.getSalariesByDepartment("d005");
        if (list != null) {
            System.out.println("\nSalaries in department d005:");
            a.printSalaries(list);
        }

        // 3️⃣ Get department and its manager
        Department dept = a.getDepartment("Development");
        if (dept != null) {
            System.out.println("\nDepartment: " + dept.dept_name + " (" + dept.dept_no + ")");
            System.out.println("Manager: " + dept.manager);
        }

        // 4️⃣ Get employee by ID (with department & manager)
        Employee emp = a.getEmployee(10001);
        a.displayEmployee(emp);

        // 5️⃣ Get employee by name
        Employee byName = a.getEmployeeByName("Georgi", "Facello");
        a.displayEmployee(byName);

        a.disconnect();
    }
}
