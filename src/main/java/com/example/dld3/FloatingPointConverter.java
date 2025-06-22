package com.example.dld3;

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

public class FloatingPointConverter extends Application {

    private ComboBox<String> formatComboBox;
    private ComboBox<String> conversionTypeComboBox;
    private TextField inputField;
    private Label resultLabel;
    private Label binaryRepresentationLabel;
    private VBox mainContainer;
    private BorderPane rootPane;
    private Button convertButton;

    // Floating point format parameters
    private int exponentBits;
    private int mantissaBits;
    private int bias;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("8-Bit Floating Point Converter");

        createUI();
        Scene scene = new Scene(rootPane, 600, 700);

        // Add keyboard shortcuts
        scene.setOnKeyPressed(e -> {
            if (e.getCode().toString().equals("F11")) {
                primaryStage.setFullScreen(!primaryStage.isFullScreen());
            }
            if (e.getCode().toString().equals("ESCAPE") && primaryStage.isFullScreen()) {
                primaryStage.setFullScreen(false);
            }
        });

        // Try to load CSS, but don't fail if not found
        try {
            scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        } catch (Exception e) {
            // CSS file not found, continue without external styles
        }

        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(700);
        primaryStage.setMaximized(false);
        primaryStage.show();

        // Entrance animation
        animateEntrance();
    }

    private void createUI() {
        // Create root pane
        rootPane = new BorderPane();

        // Create menu bar
        MenuBar menuBar = createMenuBar();
        rootPane.setTop(menuBar);

        mainContainer = new VBox(25);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(40));
        mainContainer.setStyle("-fx-background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);");

        // Make the container responsive
        VBox.setVgrow(mainContainer, Priority.ALWAYS);
        rootPane.setCenter(mainContainer);

        // Title
        Label titleLabel = new Label("8-Bit Floating Point Converter");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 2, 0, 0, 1);");

        // Format selection card
        VBox formatCard = createCard("Format Configuration");
        Label formatLabel = new Label("Select Format (Exponent, Mantissa):");
        formatLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2c3e50; -fx-font-weight: bold;");

        formatComboBox = new ComboBox<>();
        formatComboBox.getItems().addAll("e=2, m=5", "e=3, m=4", "e=4, m=3");
        formatComboBox.setPromptText("Choose format...");
        formatComboBox.setPrefWidth(250);
        formatComboBox.setMaxWidth(Double.MAX_VALUE);
        formatComboBox.setStyle("-fx-font-size: 12px;");
        formatComboBox.setOnAction(e -> onFormatChanged());

        formatCard.getChildren().addAll(formatLabel, formatComboBox);

        // Conversion type card
        VBox conversionCard = createCard("Conversion Type");
        Label conversionLabel = new Label("Select Conversion Type:");
        conversionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2c3e50; -fx-font-weight: bold;");

        conversionTypeComboBox = new ComboBox<>();
        conversionTypeComboBox.getItems().addAll("Floating-point to Decimal", "Decimal to Floating-point");
        conversionTypeComboBox.setPromptText("Choose conversion...");
        conversionTypeComboBox.setPrefWidth(250);
        conversionTypeComboBox.setMaxWidth(Double.MAX_VALUE);
        conversionTypeComboBox.setStyle("-fx-font-size: 12px;");
        conversionTypeComboBox.setOnAction(e -> onConversionTypeChanged());

        conversionCard.getChildren().addAll(conversionLabel, conversionTypeComboBox);

        // Input card
        VBox inputCard = createCard("Input Value");
        Label inputLabel = new Label("Enter Value:");
        inputLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2c3e50; -fx-font-weight: bold;");

        inputField = new TextField();
        inputField.setPromptText("Enter your value here...");
        inputField.setPrefWidth(300);
        inputField.setMaxWidth(Double.MAX_VALUE);
        inputField.setStyle("-fx-font-size: 14px; -fx-padding: 10px; -fx-background-radius: 8px; -fx-border-radius: 8px;");

        convertButton = new Button("Convert");
        convertButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10px 20px; -fx-background-radius: 8px; -fx-cursor: hand;");
        convertButton.setOnAction(e -> performConversion());
        convertButton.setDisable(true);

        // Hover effect for convert button
        convertButton.setOnMouseEntered(e -> convertButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10px 20px; -fx-background-radius: 8px; -fx-cursor: hand;"));
        convertButton.setOnMouseExited(e -> convertButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10px 20px; -fx-background-radius: 8px; -fx-cursor: hand;"));

        inputCard.getChildren().addAll(inputLabel, inputField, convertButton);

        // Result card
        VBox resultCard = createCard("Result");
        resultLabel = new Label("Result will appear here...");
        resultLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-wrap-text: true;");

        binaryRepresentationLabel = new Label("");
        binaryRepresentationLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #8e44ad; -fx-font-family: 'Courier New'; -fx-wrap-text: true;");

        resultCard.getChildren().addAll(resultLabel, binaryRepresentationLabel);

        mainContainer.getChildren().addAll(titleLabel, formatCard, conversionCard, inputCard, resultCard);
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        // View Menu
        Menu viewMenu = new Menu("View");

        MenuItem fullScreenItem = new MenuItem("Toggle Full Screen");
        fullScreenItem.setAccelerator(new KeyCodeCombination(KeyCode.F11));
        fullScreenItem.setOnAction(e -> {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setFullScreen(!stage.isFullScreen());
        });

        MenuItem maximizeItem = new MenuItem("Maximize");
        maximizeItem.setOnAction(e -> {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setMaximized(!stage.isMaximized());
        });

        viewMenu.getItems().addAll(fullScreenItem, maximizeItem);

        // Help Menu
        Menu helpMenu = new Menu("Help");

        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(e -> showAboutDialog());

        MenuItem instructionsItem = new MenuItem("Instructions");
        instructionsItem.setOnAction(e -> showInstructionsDialog());

        helpMenu.getItems().addAll(instructionsItem, new SeparatorMenuItem(), aboutItem);

        menuBar.getMenus().addAll(viewMenu, helpMenu);
        return menuBar;
    }

    private void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("8-Bit Floating Point Converter");
        alert.setContentText("A modern JavaFX application for converting between 8-bit floating-point representation and decimal values.\n\nVersion 1.0\nBuilt with JavaFX");
        alert.showAndWait();
    }

    private void showInstructionsDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Instructions");
        alert.setHeaderText("How to Use");
        alert.setContentText("1. Select a format (e=exponent bits, m=mantissa bits)\n" +
                "2. Choose conversion type:\n" +
                "   • Floating-point to Decimal: Enter 8-bit binary\n" +
                "   • Decimal to Floating-point: Enter decimal number\n" +
                "3. Enter your value and click Convert\n\n" +
                "Keyboard Shortcuts:\n" +
                "• F11: Toggle Full Screen\n" +
                "• Esc: Exit Full Screen");
        alert.showAndWait();
    }

    private VBox createCard(String title) {
        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setMaxWidth(500);
        card.setStyle("-fx-background-color: rgba(255, 255, 255, 0.95); -fx-background-radius: 15px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        Label cardTitle = new Label(title);
        cardTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        card.getChildren().add(cardTitle);
        return card;
    }

    private void animateEntrance() {
        mainContainer.setOpacity(0);
        mainContainer.setTranslateY(50);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), mainContainer);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        TranslateTransition slideUp = new TranslateTransition(Duration.millis(800), mainContainer);
        slideUp.setFromY(50);
        slideUp.setToY(0);

        ParallelTransition entrance = new ParallelTransition(fadeIn, slideUp);
        entrance.play();
    }

    private void onFormatChanged() {
        String selected = formatComboBox.getValue();
        if (selected != null) {
            switch (selected) {
                case "e=2, m=5":
                    exponentBits = 2;
                    mantissaBits = 5;
                    bias = (1 << (exponentBits - 1)) - 1; // 2^(e-1) - 1
                    break;
                case "e=3, m=4":
                    exponentBits = 3;
                    mantissaBits = 4;
                    bias = (1 << (exponentBits - 1)) - 1;
                    break;
                case "e=4, m=3":
                    exponentBits = 4;
                    mantissaBits = 3;
                    bias = (1 << (exponentBits - 1)) - 1;
                    break;
            }
            updateConvertButtonState();
        }
    }

    private void onConversionTypeChanged() {
        updateInputPrompt();
        updateConvertButtonState();
    }

    private void updateInputPrompt() {
        if (conversionTypeComboBox.getValue() != null) {
            if (conversionTypeComboBox.getValue().equals("Floating-point to Decimal")) {
                inputField.setPromptText("Enter 8-bit binary (e.g., 01000010)");
            } else {
                inputField.setPromptText("Enter decimal number (e.g., 3.5)");
            }
        }
    }

    private void updateConvertButtonState() {
        convertButton.setDisable(formatComboBox.getValue() == null || conversionTypeComboBox.getValue() == null);
    }

    private void performConversion() {
        String input = inputField.getText().trim();
        if (input.isEmpty()) {
            showError("Please enter a value to convert.");
            return;
        }

        try {
            if (conversionTypeComboBox.getValue().equals("Floating-point to Decimal")) {
                convertBinaryToDecimal(input);
            } else {
                convertDecimalToBinary(input);
            }
            animateResult();
        } catch (Exception e) {
            showError("Invalid input. Please check your value and try again.");
        }
    }

    private void convertBinaryToDecimal(String binary) {
        // Validate binary input
        if (binary.length() != 8 || !binary.matches("[01]+")) {
            throw new IllegalArgumentException("Invalid binary format");
        }

        // Extract components
        int signBit = Character.getNumericValue(binary.charAt(0));
        String exponentStr = binary.substring(1, 1 + exponentBits);
        String mantissaStr = binary.substring(1 + exponentBits);

        int exponent = Integer.parseInt(exponentStr, 2);
        int mantissa = Integer.parseInt(mantissaStr, 2);

        double result;

        // Check for special cases
        if (exponent == 0) {
            if (mantissa == 0) {
                result = signBit == 1 ? -0.0 : 0.0;
            } else {
                // Subnormal number
                result = Math.pow(-1, signBit) * (mantissa / Math.pow(2, mantissaBits)) * Math.pow(2, 1 - bias);
            }
        } else if (exponent == (1 << exponentBits) - 1) {
            if (mantissa == 0) {
                result = signBit == 1 ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
            } else {
                result = Double.NaN;
            }
        } else {
            // Normal number
            double significand = 1.0 + (mantissa / Math.pow(2, mantissaBits));
            result = Math.pow(-1, signBit) * significand * Math.pow(2, exponent - bias);
        }

        resultLabel.setText("Decimal Value: " + (Double.isInfinite(result) ? (result > 0 ? "+∞" : "-∞") :
                Double.isNaN(result) ? "NaN" : String.format("%.6f", result)));

        binaryRepresentationLabel.setText(String.format("Binary Breakdown:\nSign: %d | Exponent: %s (%d) | Mantissa: %s (%d)\nBias: %d",
                signBit, exponentStr, exponent, mantissaStr, mantissa, bias));
    }

    private void convertDecimalToBinary(String decimalStr) {
        double decimal = Double.parseDouble(decimalStr);

        // Check if value can be represented
        double maxValue = calculateMaxValue();
        double minPositiveValue = calculateMinPositiveValue();

        if (Math.abs(decimal) > maxValue && !Double.isInfinite(decimal)) {
            throw new IllegalArgumentException("Value out of range");
        }

        // Handle special cases
        if (decimal == 0.0) {
            String result = "0" + "0".repeat(exponentBits) + "0".repeat(mantissaBits);
            displayBinaryResult(result, decimal);
            return;
        }

        if (Double.isInfinite(decimal)) {
            int signBit = decimal > 0 ? 0 : 1;
            String result = signBit + "1".repeat(exponentBits) + "0".repeat(mantissaBits);
            displayBinaryResult(result, decimal);
            return;
        }

        if (Double.isNaN(decimal)) {
            String result = "0" + "1".repeat(exponentBits) + "1".repeat(mantissaBits);
            displayBinaryResult(result, decimal);
            return;
        }

        // Convert normal numbers
        int signBit = decimal < 0 ? 1 : 0;
        decimal = Math.abs(decimal);

        // Find exponent
        int exponent = (int) Math.floor(Math.log(decimal) / Math.log(2)) + bias;

        // Check for subnormal numbers
        if (exponent <= 0) {
            // Subnormal
            exponent = 0;
            int mantissa = (int) Math.round(decimal * Math.pow(2, mantissaBits + bias - 1));
            String mantissaStr = String.format("%" + mantissaBits + "s", Integer.toBinaryString(mantissa)).replace(' ', '0');
            if (mantissaStr.length() > mantissaBits) {
                mantissaStr = mantissaStr.substring(mantissaStr.length() - mantissaBits);
            }
            String result = signBit + "0".repeat(exponentBits) + mantissaStr;
            displayBinaryResult(result, decimal * (signBit == 1 ? -1 : 1));
            return;
        }

        // Check for overflow
        if (exponent >= (1 << exponentBits) - 1) {
            String result = signBit + "1".repeat(exponentBits) + "0".repeat(mantissaBits);
            displayBinaryResult(result, signBit == 1 ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
            return;
        }

        // Normal number
        double significand = decimal / Math.pow(2, exponent - bias);
        int mantissa = (int) Math.round((significand - 1.0) * Math.pow(2, mantissaBits));

        String exponentStr = String.format("%" + exponentBits + "s", Integer.toBinaryString(exponent)).replace(' ', '0');
        String mantissaStr = String.format("%" + mantissaBits + "s", Integer.toBinaryString(mantissa)).replace(' ', '0');

        if (exponentStr.length() > exponentBits) {
            exponentStr = exponentStr.substring(exponentStr.length() - exponentBits);
        }
        if (mantissaStr.length() > mantissaBits) {
            mantissaStr = mantissaStr.substring(mantissaStr.length() - mantissaBits);
        }

        String result = signBit + exponentStr + mantissaStr;
        displayBinaryResult(result, decimal * (signBit == 1 ? -1 : 1));
    }

    private void displayBinaryResult(String binary, double originalValue) {
        resultLabel.setText("8-bit Binary: " + binary);

        int signBit = Character.getNumericValue(binary.charAt(0));
        String exponentStr = binary.substring(1, 1 + exponentBits);
        String mantissaStr = binary.substring(1 + exponentBits);

        binaryRepresentationLabel.setText(String.format("Binary Breakdown:\nSign: %d | Exponent: %s | Mantissa: %s\nOriginal Value: %.6f",
                signBit, exponentStr, mantissaStr, originalValue));
    }

    private double calculateMaxValue() {
        int maxExponent = (1 << exponentBits) - 2; // All 1s except infinity
        double maxMantissa = (Math.pow(2, mantissaBits) - 1) / Math.pow(2, mantissaBits);
        return (1 + maxMantissa) * Math.pow(2, maxExponent - bias);
    }

    private double calculateMinPositiveValue() {
        // Smallest subnormal number
        return Math.pow(2, 1 - bias - mantissaBits);
    }

    private void showError(String message) {
        resultLabel.setText("Error: " + message);
        resultLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-wrap-text: true;");
        binaryRepresentationLabel.setText("");

        // Shake animation for error
        TranslateTransition shake = new TranslateTransition(Duration.millis(100), inputField);
        shake.setFromX(0);
        shake.setToX(10);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.play();
    }

    private void animateResult() {
        resultLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-wrap-text: true;");

        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(300), resultLabel);
        scaleIn.setFromX(0.8);
        scaleIn.setFromY(0.8);
        scaleIn.setToX(1.0);
        scaleIn.setToY(1.0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), binaryRepresentationLabel);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        ParallelTransition resultAnimation = new ParallelTransition(scaleIn, fadeIn);
        resultAnimation.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}