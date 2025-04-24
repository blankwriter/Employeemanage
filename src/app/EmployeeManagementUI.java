package app;

import database.EmployeeDatabase;
import model.Employee;
import utility.EmployeeSalaryComparator;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import ExceptionHandling.EmployeeNotFoundException;
import ExceptionHandling.InvalidDepartmentException;
import ExceptionHandling.InvalidSalaryException;
import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

public class EmployeeManagementUI extends Application {
    private final EmployeeDatabase<Integer> database = new EmployeeDatabase<>();
    private final ObservableList<Employee<Integer>> employeeList = FXCollections.observableArrayList();
    private static final AtomicInteger idCounter = new AtomicInteger(1000);
    private TableView<Employee<Integer>> table;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Employee Management System");

        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(15));
        layout.setStyle("-fx-background-color:rgb(195, 190, 190);");

        // Create and style the table
        table = createEmployeeTable();
        VBox tableContainer = new VBox(table);
        tableContainer.setPadding(new Insets(10));
        tableContainer.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 5;");
        layout.setCenter(tableContainer);

        // Create and style the add employee section
        VBox addBox = createAddEmployeeSection();
        addBox.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 5; -fx-padding: 15;");
        layout.setLeft(addBox);

        // Create and style the search section
        HBox searchBox = createSearchSection();
        searchBox.setStyle("-fx-background-color: #3f51b5; -fx-padding: 10; -fx-background-radius: 5;");
        layout.setTop(searchBox);

        // Create and style the bottom controls
        HBox bottomBox = createBottomControls();
        bottomBox.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 5; -fx-padding: 10;");
        layout.setBottom(bottomBox);

        // Create and style the right panel
        VBox sidePanel = createRightPanel();
        sidePanel.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 5; -fx-padding: 15;");
        layout.setRight(sidePanel);

        Scene scene = new Scene(layout, 1200, 700);
        stage.setScene(scene);
        stage.show();
    }

    private TableView<Employee<Integer>> createEmployeeTable() {
        TableView<Employee<Integer>> tableView = new TableView<>();
        tableView.setEditable(true);
        tableView.setStyle("-fx-font-size: 14px; -fx-font-family: 'Segoe UI';");
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Add alternating row colors
        tableView.setRowFactory(tv -> new TableRow<Employee<Integer>>() {
            @Override
            protected void updateItem(Employee<Integer> item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty) {
                    if (getIndex() % 2 == 0) {
                        setStyle("-fx-background-color:#e4e4e4;");
                    } else {
                        setStyle("-fx-background-color: white;");
                    }
                }
            }
        });

        // Helper method to create styled columns
        Function<String, TableColumn<Employee<Integer>, ?>> createColumn = (title) -> {
            TableColumn<Employee<Integer>, ?> col = new TableColumn<>(title);
            col.setStyle("-fx-alignment: CENTER;");
            return col;
        };

        // ID Column
        TableColumn<Employee<Integer>, Integer> idColumn = (TableColumn<Employee<Integer>, Integer>) createColumn.apply("ID");
        idColumn.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getEmployeeId()).asObject());
        idColumn.setStyle("-fx-font-weight: bold; -fx-text-fill: black;");

        // Name Column
        TableColumn<Employee<Integer>, String> nameColumn = (TableColumn<Employee<Integer>, String>) createColumn.apply("Name");
        nameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setStyle("-fx-text-fill: green;");
        nameColumn.setOnEditCommit(event -> {
            try {
                Employee<Integer> emp = event.getRowValue();
                emp.setName(event.getNewValue());
                database.updateEmployeeDetails(emp.getEmployeeId(), "name", event.getNewValue());
            } catch (Exception e) {
                showAlert("Error", e.getMessage());
                employeeList.setAll(database.getAllEmployees());
            }
        });

        // Department Column (with combo box for editing)
        TableColumn<Employee<Integer>, String> departmentColumn = (TableColumn<Employee<Integer>, String>) createColumn.apply("Department");
        departmentColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDepartment()));
        departmentColumn.setStyle("-fx-text-fill: black;");
        departmentColumn.setCellFactory(ComboBoxTableCell.forTableColumn(
            FXCollections.observableArrayList("Quality Assurance", "Frontend", "Finance", "Operations", "Backend", "DevOPs")
        ));
        
        departmentColumn.setOnEditCommit(event -> {
            try {
                Employee<Integer> emp = event.getRowValue();
                emp.setDepartment(event.getNewValue());
                database.updateEmployeeDetails(emp.getEmployeeId(), "department", event.getNewValue());
            } catch (InvalidDepartmentException ex) {
                showAlert("Invalid Department", ex.getMessage());
                employeeList.setAll(database.getAllEmployees());
            } catch (Exception ex) {
                showAlert("Error", ex.getMessage());
                employeeList.setAll(database.getAllEmployees());
            }
            
        });

        // Salary Column (with currency formatting)
        TableColumn<Employee<Integer>, Double> salaryColumn = (TableColumn<Employee<Integer>, Double>) createColumn.apply("Salary");
        salaryColumn.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getSalary()).asObject());
        salaryColumn.setCellFactory(column -> new TextFieldTableCell<Employee<Integer>, Double>(new DoubleStringConverter()) {
            @Override
            public void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setStyle("-fx-text-fill: black;");
                if (!empty && item != null) {
                    setText(String.format("$%,.2f", item));
                }
            }
        });
        salaryColumn.setOnEditCommit(event -> {
            try {
                Employee<Integer> emp = event.getRowValue();
                emp.setSalary(event.getNewValue());
                database.updateEmployeeDetails(emp.getEmployeeId(), "salary", event.getNewValue());
            } catch (InvalidSalaryException ex) {
                showAlert("Invalid Salary", ex.getMessage());
                employeeList.setAll(database.getAllEmployees());
            } catch (Exception ex) {
                showAlert("Error", ex.getMessage());
                employeeList.setAll(database.getAllEmployees());
            }
        });

        // Rating Column (with color coding)
        TableColumn<Employee<Integer>, Double> ratingColumn = (TableColumn<Employee<Integer>, Double>) createColumn.apply("Rating");
        ratingColumn.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getPerformanceRating()).asObject());
        ratingColumn.setCellFactory(column -> new TableCell<Employee<Integer>, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("%.1f", item));
                    if (item >= 4.5) setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    else if (item >= 3.5) setStyle("-fx-text-fill: darkgreen;");
                    else if (item >= 2.5) setStyle("-fx-text-fill: orange;");
                    else setStyle("-fx-text-fill: red;");
                }
            }
        });
        ratingColumn.setOnEditCommit(event -> {
            try {
                Employee<Integer> emp = event.getRowValue();
                emp.setPerformanceRating(event.getNewValue());
                database.updateEmployeeDetails(emp.getEmployeeId(), "rating", event.getNewValue());
            } catch (IllegalArgumentException ex) {
                showAlert("Invalid Rating", "Rating must be between 0 and 5");
                employeeList.setAll(database.getAllEmployees());
            } catch (Exception ex) {
                showAlert("Error", ex.getMessage());
                employeeList.setAll(database.getAllEmployees());
            }
        });

        // Experience Column
        TableColumn<Employee<Integer>, Integer> experienceColumn = (TableColumn<Employee<Integer>, Integer>) createColumn.apply("Exp (Yrs)");
        experienceColumn.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getYearsOfExperience()).asObject());
        experienceColumn.setStyle("-fx-text-fill: black;");
        experienceColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        experienceColumn.setOnEditCommit(event -> {
            try {
                Employee<Integer> emp = event.getRowValue();
                emp.setYearsOfExperience(event.getNewValue());
                database.updateEmployeeDetails(emp.getEmployeeId(), "experience", event.getNewValue());
            } catch (IllegalArgumentException ex) {
                showAlert("Invalid Experience", "Years of experience cannot be negative");
                employeeList.setAll(database.getAllEmployees());
            } catch (Exception ex) {
                showAlert("Error", ex.getMessage());
                employeeList.setAll(database.getAllEmployees());
            }
        });

        // Active Column (with better checkbox styling)
        TableColumn<Employee<Integer>, Boolean> activeColumn = (TableColumn<Employee<Integer>, Boolean>) createColumn.apply("Active");
        activeColumn.setCellValueFactory(cell -> new SimpleBooleanProperty(cell.getValue().isActive()));
        activeColumn.setCellFactory(tc -> {
            CheckBoxTableCell<Employee<Integer>, Boolean> cell = new CheckBoxTableCell<>();
            cell.setAlignment(Pos.CENTER);
            return cell;
        });
        activeColumn.setOnEditCommit(event -> {
            Employee<Integer> emp = event.getRowValue();
            emp.setActive(event.getNewValue());
            database.updateEmployeeDetails(emp.getEmployeeId(), "active", event.getNewValue());
        });

        tableView.getColumns().addAll(idColumn, nameColumn, departmentColumn, salaryColumn, 
                                   ratingColumn, experienceColumn, activeColumn);
        tableView.setItems(employeeList);
        
        // Style table headers
        tableView.lookupAll(".column-header").forEach(node -> 
            node.setStyle("-fx-background-color: #3f51b5; -fx-text-fill: white; -fx-font-weight: bold;")
        );
        
        return tableView;
    }

    private VBox createAddEmployeeSection() {
        VBox addBox = new VBox(15);
        addBox.setPadding(new Insets(15));
        
        Label title = new Label("Add New Employee");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #3f51b5;");
        
        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");
        nameField.setStyle("-fx-font-size: 14px; -fx-pref-height: 35px;");

        ComboBox<String> departmentField = new ComboBox<>();
        departmentField.setPromptText("Select Department");
        departmentField.getItems().addAll("Quality Assurance", "Frontend", "Finance", "Operations", "Backend", "DevOPs");
        departmentField.setStyle("-fx-font-size: 14px; -fx-pref-height: 35px;");

        TextField salaryField = new TextField();
        salaryField.setPromptText("Salary");
        salaryField.setStyle("-fx-font-size: 14px; -fx-pref-height: 35px;");

        TextField ratingField = new TextField();
        ratingField.setPromptText("Performance Rating (0-5)");
        ratingField.setStyle("-fx-font-size: 14px; -fx-pref-height: 35px;");

        TextField experienceField = new TextField();
        experienceField.setPromptText("Years of Experience");
        experienceField.setStyle("-fx-font-size: 14px; -fx-pref-height: 35px;");

        Button addButton = new Button("Add Employee");
        addButton.setStyle("-fx-background-color: #009688; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-pref-height: 40px;");
        addButton.setOnAction(e -> {
            try {
                String name = nameField.getText();
                String dept = departmentField.getValue();
                if (dept == null || dept.isBlank()) {
                    throw new InvalidDepartmentException("Please select a department");
                }
                double salary = Double.parseDouble(salaryField.getText());
                double rating = Double.parseDouble(ratingField.getText());
                int exp = Integer.parseInt(experienceField.getText());

                Employee<Integer> emp = new Employee<>(generateId(), name, dept, salary, rating, exp, true);
                database.addEmployee(emp);
                employeeList.setAll(database.getAllEmployees());
                
                // Clear fields
                nameField.clear();
                departmentField.setValue(null);
                salaryField.clear();
                ratingField.clear();
                experienceField.clear();
            } catch (NumberFormatException ex) {
                showAlert("Input Error", "Please enter valid numbers for salary, rating, and experience");
            } catch (InvalidDepartmentException | InvalidSalaryException ex) {
                showAlert("Input Error", ex.getMessage());
            } catch (Exception ex) {
                showAlert("Error", "An unexpected error occurred: " + ex.getMessage());
            }
        });

        addBox.getChildren().addAll(title, nameField, departmentField, salaryField, ratingField, experienceField, addButton);
        return addBox;
    }

    private HBox createSearchSection() {
        TextField searchField = new TextField();
        searchField.setPromptText("Search by Name, Dept, or ID");
        searchField.setStyle("-fx-font-size: 14px; -fx-pref-height: 35px; -fx-pref-width: 300px;");

        Button searchButton = new Button("Search");
        searchButton.setStyle("-fx-background-color:rgb(2, 123, 216); -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-pref-height: 35px;");
        searchButton.setOnAction(e -> {
            String query = searchField.getText().toLowerCase();
            try {
                if (query.isBlank()) {
                    throw new IllegalArgumentException("Search query cannot be empty");
                }
                
                ObservableList<Employee<Integer>> results = FXCollections.observableArrayList();
                for (Employee<Integer> emp : database.getAllEmployees()) {
                    if ((emp.getName() != null && emp.getName().toLowerCase().contains(query)) ||
                            (emp.getDepartment() != null && emp.getDepartment().toLowerCase().contains(query)) ||
                            String.valueOf(emp.getEmployeeId()).contains(query)) {
                        results.add(emp);
                    }
                }
                
                if (results.isEmpty()) {
                    showAlert("No Results", "No employees match your search criteria");
                }
                employeeList.setAll(results);
            } catch (IllegalArgumentException ex) {
                showAlert("Search Error", ex.getMessage());
            }
        });

        Button resetButton = new Button("Reset");
        resetButton.setStyle("-fx-background-color:rgb(4, 96, 87); -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-pref-height: 35px;");
        resetButton.setOnAction(e -> employeeList.setAll(database.getAllEmployees()));

        HBox searchBox = new HBox(10, searchField, searchButton, resetButton);
        searchBox.setAlignment(Pos.CENTER);
        return searchBox;
    }

    private HBox createBottomControls() {
        Button removeButton = new Button("Remove Selected");
        removeButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-pref-height: 35px;");
        removeButton.setOnAction(e -> {
            try {
                Employee<Integer> selected = table.getSelectionModel().getSelectedItem();
                if (selected == null) {
                    throw new EmployeeNotFoundException("No employee selected");
                }
                
                // Create confirmation dialog
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                confirmation.setTitle("Confirm Deletion");
                confirmation.setHeaderText("Delete Employee");
                confirmation.setContentText("Are you sure you want to delete " + selected.getName() + "?");
                
                // Show dialog and wait for response
                ButtonType result = confirmation.showAndWait().orElse(ButtonType.CANCEL);
                
                // Only proceed if user clicked OK
                if (result == ButtonType.OK) {
                    database.removeEmployee(selected.getEmployeeId());
                    employeeList.setAll(database.getAllEmployees());
                    showAlert("Success", "Employee deleted successfully");
                }
            } catch (EmployeeNotFoundException ex) {
                showAlert("Error", ex.getMessage());
            }
        });
    
        Button sortByExperienceButton = new Button("Sort by Experience");
        sortByExperienceButton.setStyle("-fx-background-color: #3F51B5; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-pref-height: 35px;");
        sortByExperienceButton.setOnAction(e -> {
            ObservableList<Employee<Integer>> sortedList = FXCollections.observableArrayList(database.getAllEmployees());
            sortedList.sort((a, b) -> {
                if (a == null && b == null) return 0;
                if (a == null) return 1;
                if (b == null) return -1;
                return Integer.compare(b.getYearsOfExperience(), a.getYearsOfExperience());
            });
            employeeList.setAll(sortedList);
        });
    
        Button sortBySalaryButton = new Button("Sort by Salary");
        sortBySalaryButton.setStyle("-fx-background-color: #3F51B5; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-pref-height: 35px;");
        sortBySalaryButton.setOnAction(e -> employeeList.setAll(database.sortBySalary()));
    
        HBox bottomBox = new HBox(15, sortByExperienceButton, sortBySalaryButton, removeButton);
        bottomBox.setAlignment(Pos.CENTER);
        return bottomBox;
    }

    private VBox createRightPanel() {
        VBox sidePanel = new VBox(15);
        sidePanel.setPadding(new Insets(15));
        
        Label title = new Label("Quick Actions");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #3f51b5;");

        // Department average salary controls
        ComboBox<String> departmentComboBox = new ComboBox<>();
        departmentComboBox.getItems().addAll("Quality Assurance", "Frontend", "Finance", "Operations", "Backend", "DevOPs");
        departmentComboBox.setPromptText("Select Department");
        departmentComboBox.setStyle("-fx-font-size: 14px; -fx-pref-height: 35px;");

        Button avgSalaryButton = new Button("Avg Salary");
        avgSalaryButton.setStyle("-fx-background-color: #3F51B5; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-pref-height: 35px;");
        avgSalaryButton.setOnAction(e -> {
            try {
                String selectedDept = departmentComboBox.getValue();
                if (selectedDept == null) {
                    throw new InvalidDepartmentException("Please select a department");
                }
                double avg = database.getAverageSalary(selectedDept);
                showAlert(selectedDept + " Department", "Average Salary: $" + String.format("%.2f", avg));
            } catch (InvalidDepartmentException ex) {
                showAlert("Error", ex.getMessage());
            } catch (Exception ex) {
                showAlert("Error", "Failed to calculate average salary: " + ex.getMessage());
            }
        });

        // Raise high performers button
        Button raiseButton = new Button("Raise High Performers");
        raiseButton.setStyle("-fx-background-color: #3F51B5; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-pref-height: 35px;");
        raiseButton.setOnAction(e -> {
            try {
                double threshold = 4.5;
                double increment = 1000;
                database.giveRaise(threshold, increment);
                employeeList.setAll(database.getAllEmployees());
                showAlert("Success", "Raise applied to high performers");
            } catch (Exception ex) {
                showAlert("Error", "Failed to apply raises: " + ex.getMessage());
            }
        });

        // Top paid employees button
        Button topPaidButton = new Button("Show Top 5 Paid");
        topPaidButton.setStyle("-fx-background-color: #3F51B5; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-pref-height: 35px;");
        topPaidButton.setOnAction(e -> {
            try {
                employeeList.setAll(database.getTopPaid(5));
            } catch (Exception ex) {
                showAlert("Error", "Failed to get top paid employees: " + ex.getMessage());
            }
        });

        // Print buttons
        Button printAllButton = new Button("Print All Employees");
        printAllButton.setStyle("-fx-background-color: #009688; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-pref-height: 35px;");
        printAllButton.setOnAction(e -> {
            try {
                StringBuilder builder = new StringBuilder();
                for (Employee<Integer> emp : database.getAllEmployees()) {
                    builder.append(emp.toString()).append("\n");
                }
                showPrintWindow("All Employees", builder.toString());
            } catch (Exception ex) {
                showAlert("Error", "Failed to print employees: " + ex.getMessage());
            }
        });

        sidePanel.getChildren().addAll(title, departmentComboBox, avgSalaryButton, raiseButton, 
                                     topPaidButton, printAllButton);
        return sidePanel;
    }

    private int generateId() {
        return idCounter.getAndIncrement();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showPrintWindow(String title, String content) {
        Stage printStage = new Stage();
        printStage.setTitle(title);
        TextArea area = new TextArea(content);
        area.setWrapText(true);
        area.setEditable(false);
        area.setStyle("-fx-font-family: monospace; -fx-font-size: 14px;");
        Scene scene = new Scene(new VBox(area), 500, 400);
        printStage.setScene(scene);
        printStage.show();
    }
}