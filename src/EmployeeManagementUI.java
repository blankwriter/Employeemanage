// Import necessary JavaFX classes and utilities
import javafx.application.Application;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;

import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

// Main class extending JavaFX Application
public class EmployeeManagementUI extends Application {
    // Database instance and list to hold employees
    private final EmployeeDatabase<Integer> database = new EmployeeDatabase<>();
    private final ObservableList<Employee<Integer>> employeeList = FXCollections.observableArrayList();
    private static final AtomicInteger idCounter = new AtomicInteger(1000); // ID generator

    public static void main(String[] args) {
        launch(args); // Launch JavaFX app
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Employee Management System"); // Set window title

        BorderPane layout = new BorderPane(); // Main layout pane
        layout.setPadding(new Insets(10));

        TableView<Employee<Integer>> table = new TableView<>(); // TableView for employees
        table.setEditable(true); // Make table editable

        // Define table columns
        TableColumn<Employee<Integer>, Integer> idColumn = new TableColumn<>("ID");
        TableColumn<Employee<Integer>, String> nameColumn = new TableColumn<>("Name");
        TableColumn<Employee<Integer>, String> departmentColumn = new TableColumn<>("Department");
        TableColumn<Employee<Integer>, Double> salaryColumn = new TableColumn<>("Salary");
        TableColumn<Employee<Integer>, Double> ratingColumn = new TableColumn<>("Performance Rating");
        TableColumn<Employee<Integer>, Integer> experienceColumn = new TableColumn<>("Experience");
        TableColumn<Employee<Integer>, Boolean> activeColumn = new TableColumn<>("Active");

        // Set up cell value factories and edit handlers
        idColumn.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getEmployeeId()).asObject());

        nameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setOnEditCommit(event -> {
            Employee<Integer> emp = event.getRowValue();
            emp.setName(event.getNewValue());
            database.updateEmployeeDetails(emp.getEmployeeId(), "name", event.getNewValue());
        });

        departmentColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDepartment()));
        departmentColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        departmentColumn.setOnEditCommit(event -> {
            Employee<Integer> emp = event.getRowValue();
            emp.setDepartment(event.getNewValue());
            database.updateEmployeeDetails(emp.getEmployeeId(), "department", event.getNewValue());
        });

        salaryColumn.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getSalary()).asObject());
        salaryColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        salaryColumn.setOnEditCommit(event -> {
            Employee<Integer> emp = event.getRowValue();
            emp.setSalary(event.getNewValue());
            database.updateEmployeeDetails(emp.getEmployeeId(), "salary", event.getNewValue());
        });

        ratingColumn.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getPerformanceRating()).asObject());
        ratingColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        ratingColumn.setOnEditCommit(event -> {
            Employee<Integer> emp = event.getRowValue();
            emp.setPerformanceRating(event.getNewValue());
            database.updateEmployeeDetails(emp.getEmployeeId(), "rating", event.getNewValue());
        });

        experienceColumn.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getYearsOfExperience()).asObject());
        experienceColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        experienceColumn.setOnEditCommit(event -> {
            Employee<Integer> emp = event.getRowValue();
            emp.setYearsOfExperience(event.getNewValue());
            database.updateEmployeeDetails(emp.getEmployeeId(), "experience", event.getNewValue());
        });

        activeColumn.setCellValueFactory(cell -> new SimpleBooleanProperty(cell.getValue().isActive()));
        activeColumn.setCellFactory(tc -> new CheckBoxTableCell<>());
        activeColumn.setEditable(true);
        activeColumn.setOnEditCommit(event -> {
            Employee<Integer> emp = event.getRowValue();
            emp.setActive(event.getNewValue());
            database.updateEmployeeDetails(emp.getEmployeeId(), "active", event.getNewValue());
        });

        // Add columns and data to the table
        table.getColumns().addAll(idColumn, nameColumn, departmentColumn, salaryColumn, ratingColumn, experienceColumn, activeColumn);
        table.setItems(employeeList);
        layout.setCenter(table); // Place table at center

        // Section to add employees
        VBox addBox = new VBox(10);
        addBox.setPadding(new Insets(10));

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        ComboBox<String> departmentField = new ComboBox<>();
        departmentField.setPromptText("Select Department");
        departmentField.getItems().addAll("Quality Assurance", "Frontend", "Finance", "Operations", "Backend", "DevOPs");

        TextField salaryField = new TextField();
        salaryField.setPromptText("Salary");

        TextField ratingField = new TextField();
        ratingField.setPromptText("Performance Rating");

        TextField experienceField = new TextField();
        experienceField.setPromptText("Years of Experience");

        Button addButton = new Button("Add");
        addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
        addButton.setFont(Font.font("Arial", 14));
        addButton.setOnAction(e -> {
            try {
                String name = nameField.getText();
                String dept = departmentField.getValue();
                if (dept == null || dept.equals("Select Department")) {
                    showAlert("Invalid Department", "Please select a department.");
                    return; // Stop execution if department is not selected
                }
                double salary = Double.parseDouble(salaryField.getText());
                double rating = Double.parseDouble(ratingField.getText());
                int exp = Integer.parseInt(experienceField.getText());
        
                // Validate performance rating
                if (rating < 0 || rating > 5) {
                    showAlert("Invalid Rating", "Performance rating must be between 0 and 5.");
                    return;
                }
        
                // Create new employee and add to database
                Employee<Integer> emp = new Employee<>(generateId(), name, dept, salary, rating, exp, true);
                database.addEmployee(emp);
                employeeList.setAll(database.getAllEmployees());
        
                // Clear fields after addition
                nameField.clear();
                departmentField.setValue(null);
                salaryField.clear();
                ratingField.clear();
                experienceField.clear();
            } catch (NumberFormatException ex) {
                showAlert("Input Error", "Please enter valid numbers.");
            }
        });
        

        addBox.getChildren().addAll(nameField, departmentField, salaryField, ratingField, experienceField, addButton);
        layout.setLeft(addBox); // Place add box on left side

        // Search section at the top
        TextField searchField = new TextField();
        searchField.setPromptText("Search by Name, Dept, or ID");

        Button searchButton = new Button("Search");
        searchButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px;");
        searchButton.setFont(Font.font("Arial", 14));
        searchButton.setOnAction(e -> {
            String query = searchField.getText().toLowerCase();
            ObservableList<Employee<Integer>> results = FXCollections.observableArrayList();
            for (Employee<Integer> emp : database.getAllEmployees()) {
                if (emp.getName().toLowerCase().contains(query) ||
                        emp.getDepartment().toLowerCase().contains(query) ||
                        String.valueOf(emp.getEmployeeId()).contains(query)) {
                    results.add(emp);
                }
            }
            employeeList.setAll(results);
        });

        Button resetButton = new Button("Reset");
        resetButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-size: 14px;");
        resetButton.setFont(Font.font("Arial", 14));
        resetButton.setOnAction(e -> employeeList.setAll(database.getAllEmployees())); // Reset to full list

        HBox searchBox = new HBox(10, searchField, searchButton, resetButton);
        searchBox.setPadding(new Insets(10));
        layout.setTop(searchBox);

        // Remove and sort controls at bottom
        Button removeButton = new Button("Remove Selected");
        removeButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-size: 14px;");
        removeButton.setFont(Font.font("Arial", 14));
        removeButton.setOnAction(e -> {
            Employee<Integer> selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                database.removeEmployee(selected.getEmployeeId());
                employeeList.setAll(database.getAllEmployees());
            }
        });

        Button sortByExperienceButton = new Button("Sort by Experience");
        sortByExperienceButton.setStyle("-fx-background-color: #9C27B0; -fx-text-fill: white; -fx-font-size: 14px;");
        sortByExperienceButton.setFont(Font.font("Arial", 14));
        sortByExperienceButton.setOnAction(e -> {
            ObservableList<Employee<Integer>> sortedList = FXCollections.observableArrayList(database.getAllEmployees());
            sortedList.sort((a, b) -> Integer.compare(b.getYearsOfExperience(), a.getYearsOfExperience()));
            employeeList.setAll(sortedList);
        });

        Button sortBySalaryButton = new Button("Sort by Salary");
        sortBySalaryButton.setStyle("-fx-background-color: #00BCD4; -fx-text-fill: white; -fx-font-size: 14px;");
        sortBySalaryButton.setFont(Font.font("Arial", 14));
        sortBySalaryButton.setOnAction(e -> {
            ObservableList<Employee<Integer>> sortedList = FXCollections.observableArrayList(database.getAllEmployees());
            sortedList.sort((a, b) -> Double.compare(b.getSalary(), a.getSalary()));
            employeeList.setAll(sortedList);
        });

        HBox bottomBox = new HBox(10, sortByExperienceButton, sortBySalaryButton, removeButton);
        bottomBox.setPadding(new Insets(10));
        layout.setBottom(bottomBox); // Set to bottom

        // Right panel: additional operations
        Button raiseButton = new Button("Raise High Performers");
        raiseButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
        raiseButton.setFont(Font.font("Arial", 14));
        raiseButton.setOnAction(e -> {
            double threshold = 4.5;
            double increment = 1000;
            ObservableList<Employee<Integer>> all = FXCollections.observableArrayList(database.getAllEmployees());

            StringBuilder raisedList = new StringBuilder();
            for (Employee<Integer> emp : all) {
                if (emp.getPerformanceRating() >= threshold) {
                    double oldSalary = emp.getSalary();
                    emp.setSalary(oldSalary + increment);
                    database.updateEmployeeDetails(emp.getEmployeeId(), "salary", emp.getSalary());
                    raisedList.append(String.format("ID: %s | %s | Old: $%.2f → New: $%.2f%n",
                            emp.getEmployeeId(), emp.getName(), oldSalary, emp.getSalary()));
                }
            }

            employeeList.setAll(database.getAllEmployees());

            if (raisedList.length() > 0) {
                showPrintWindow("High Performers Raised", raisedList.toString());
            } else {
                showAlert("No Raise Applied", "No employee met the performance threshold.");
            }
        });

        Button topPaidButton = new Button("Top 5 Paid");
        topPaidButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px;");
        topPaidButton.setFont(Font.font("Arial", 14));
        topPaidButton.setOnAction(e -> {
            ObservableList<Employee<Integer>> top5 = FXCollections.observableArrayList(database.getAllEmployees());
            top5.sort((a, b) -> Double.compare(b.getSalary(), a.getSalary()));
            employeeList.setAll(top5.stream().limit(5).collect(Collectors.toCollection(FXCollections::observableArrayList)));
        });

        // Average salary of selected department
        ComboBox<String> departmentComboBox = new ComboBox<>();
        departmentComboBox.getItems().addAll("Quality Assurance", "Frontend", "Finance", "Operations", "Backend", "DevOPs");
        departmentComboBox.setPromptText("Select Department");

        Button avgSalaryButton = new Button("Avg Salary of Selected Dept");
        avgSalaryButton.setStyle("-fx-background-color: #9C27B0; -fx-text-fill: white; -fx-font-size: 14px;");
        avgSalaryButton.setFont(Font.font("Arial", 14));
        avgSalaryButton.setOnAction(e -> {
            String selectedDept = departmentComboBox.getValue();
            if (selectedDept == null) {
                showAlert("No Department Selected", "Please select a department.");
                return;
            }

            ObservableList<Employee<Integer>> all = FXCollections.observableArrayList(database.getAllEmployees());
            double avg = all.stream().filter(e1 -> e1.getDepartment().equalsIgnoreCase(selectedDept))
                    .mapToDouble(Employee::getSalary).average().orElse(0.0);
            showAlert(selectedDept + " Department", "Average Salary: $" + String.format("%.2f", avg));
        });

        // Print all employees
        Button printAllButton = new Button("Print All Employee");
        printAllButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-size: 14px;");
        printAllButton.setFont(Font.font("Arial", 14));
        printAllButton.setOnAction(e -> {
            StringBuilder builder = new StringBuilder();
            for (Employee<Integer> emp : database.getAllEmployees()) {
                builder.append(emp.toString()).append("\n");
            }
            showPrintWindow("All Employees", builder.toString());
        });

        // Print formatted employee report
        Button reportButton = new Button("Print Report");
        reportButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-size: 14px;");
        reportButton.setFont(Font.font("Arial", 14));
        reportButton.setOnAction(e -> {
            String report = database.getAllEmployees().stream()
                    .map(e1 -> String.format("ID: %s, %s, Dept: %s, Rating: %.1f", e1.getEmployeeId(), e1.getName(), e1.getDepartment(), e1.getPerformanceRating()))
                    .collect(Collectors.joining("\n"));
            showPrintWindow("Employee Report", report);
        });

        // Add all right-side controls to a VBox
        VBox sidePanel = new VBox(10, departmentComboBox, avgSalaryButton, raiseButton, topPaidButton, printAllButton, reportButton);
        sidePanel.setPadding(new Insets(10));
        layout.setRight(sidePanel);

        // Finalize and show the scene
        Scene scene = new Scene(layout, 950, 600);
        stage.setScene(scene);
        stage.show();
    }

    // Generate unique ID
    private int generateId() {
        return idCounter.getAndIncrement();
    }

    // Show info alert
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Display a print window with text
    private void showPrintWindow(String title, String content) {
        Stage printStage = new Stage();
        printStage.setTitle(title);
        TextArea area = new TextArea(content);
        area.setWrapText(true);
        area.setEditable(false);
        Scene scene = new Scene(new VBox(area), 500, 400);
        printStage.setScene(scene);
        printStage.show();
    }
}
