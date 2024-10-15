package com.example.calculator;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private TextView display;
    private TextView operationView;
    private String currentNumber = "";
    private String currentOperation = "";
    private Double firstOperand = null;
    private Double memory = null;
    private boolean hasFirstOperand = false;
    private final DecimalFormat decimalFormat = new DecimalFormat("#.########");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = findViewById(R.id.textView);
        operationView = findViewById(R.id.operationTextView);

        int[] numberButtons = {
                R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4,
                R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9
        };

        View.OnClickListener numberClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button = (Button) v;
                onNumberClick(button.getText().toString());
            }
        };

        for (int id : numberButtons) {
            findViewById(id).setOnClickListener(numberClickListener);
        }

        findViewById(R.id.buttonPlus).setOnClickListener(v -> onOperationClick("+"));
        findViewById(R.id.buttonMinus).setOnClickListener(v -> onOperationClick("-"));
        findViewById(R.id.buttonMultiply).setOnClickListener(v -> onOperationClick("*"));
        findViewById(R.id.buttonDivide).setOnClickListener(v -> onOperationClick("/"));
        findViewById(R.id.buttonEqual).setOnClickListener(v -> onEqualClick());
        findViewById(R.id.buttonClear).setOnClickListener(v -> onClearClick());
        findViewById(R.id.buttonSqrt).setOnClickListener(v -> onSqrtClick());
        findViewById(R.id.buttonInverse).setOnClickListener(v -> onInverseClick());
        findViewById(R.id.buttonPower).setOnClickListener(v -> onOperationClick("^"));

        findViewById(R.id.buttonDot).setOnClickListener(v -> onDotClick());

        findViewById(R.id.buttonMPlus).setOnClickListener(v -> onMemoryPlusClick());
        findViewById(R.id.buttonMR).setOnClickListener(v -> onMemoryRecallClick());
        findViewById(R.id.buttonMC).setOnClickListener(v -> onMemoryClearClick());
    }

    private void showAlertDialog(String message, String url) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Ошибка")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setNegativeButton("Подробнее", (dialog, which) -> {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                })
                .show();
    }

    private void showAlertDialogNoUrl(String message) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Ошибка")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void onNumberClick(String number) {
        if (currentNumber.equals("-")) {
            currentNumber += number;
        } else {
            currentNumber += number;
        }
        display.setText(currentNumber);
        trimOperationView();
        operationView.setText(operationView.getText().toString() + number);
    }

    private void onDotClick() {
        if (!currentNumber.contains(".")) {
            currentNumber += ".";
            display.setText(currentNumber);
            trimOperationView();
            operationView.setText(operationView.getText().toString() + ".");
        }
    }

    private void onOperationClick(String op) {
        if (op.equals("-") && currentNumber.isEmpty()) {
            currentNumber = "-";
            display.setText(currentNumber);
        }
        else if (op.equals("-") && currentNumber.equals("-")) {
            return;
        }
        else if (!currentNumber.isEmpty()) {
            if (firstOperand == null) {
                firstOperand = Double.parseDouble(currentNumber);
            } else if (currentOperation != null && !currentNumber.equals("-")) {
                onEqualClick();
                firstOperand = Double.parseDouble(currentNumber);
            }
            currentOperation = op;
            currentNumber = "";

            String operationText = operationView.getText().toString();
            trimOperationView();
            if (operationText.matches(".*[+\\-*/^]\\s$")) {
                operationView.setText(operationText.substring(0, operationText.length() - 3) + " " + op + " ");
            } else {
                operationView.setText(operationText + " " + op + " ");
            }
        }
        else if (firstOperand != null && !op.equals("-")) {
            currentOperation = op;
            String operationText = operationView.getText().toString();
            if (operationText.matches(".*[+\\-*/^]\\s$")) {
                trimOperationView();
                operationView.setText(operationText.substring(0, operationText.length() - 3) + " " + op + " ");
            }

        }
    }

    private void trimOperationView() {
        String operationText = operationView.getText().toString();
        if (operationText.length() > 50) { // допустимая длина 50 символов
            operationView.setText(operationText.substring(operationText.length() - 50));
        }
    }


    private void onEqualClick() {
        if (firstOperand != null && !currentNumber.isEmpty() && currentOperation != null) {
            Double secondOperand = Double.parseDouble(currentNumber);
            Double result = null;
            String strResult;
            switch (currentOperation) {
                case "+":
                    result = firstOperand + secondOperand;
                    break;
                case "-":
                    result = firstOperand - secondOperand;
                    break;
                case "*":
                    result = firstOperand * secondOperand;
                    break;
                case "/":
                    if (secondOperand != 0) {
                        result = firstOperand / secondOperand;
                    } else {
                        showAlertDialog("Нельзя делить на ноль", "https://ru.wikipedia.org/wiki/Деление_на_ноль");
                        return;
                    }
                    break;
                case "^":
                    result = Math.pow(firstOperand, secondOperand);
                    break;
            }

            if (result != null) {
                strResult = decimalFormat.format(result);
                display.setText(strResult);
                operationView.setText(operationView.getText().toString() + " = " + strResult);
                currentNumber = result.toString();
                firstOperand = null;
                currentOperation = null;
            }
        }
    }

    private void onClearClick() {
        currentNumber = "";
        firstOperand = null;
        currentOperation = null;
        display.setText("0");
        operationView.setText("");
    }

    private void onSqrtClick() {
        if (currentOperation != null && currentNumber.isEmpty()) {
            showAlertDialogNoUrl("Сначала введите число после операции");
            return;
        }
        if (!currentNumber.isEmpty()) {
            Double number = Double.parseDouble(currentNumber);
            if (number >= 0) {
                Double result = Math.sqrt(number);
                display.setText(decimalFormat.format(result));
                currentNumber = result.toString();
            } else {
                showAlertDialog("Нельзя взять корень из отрицательного числа", "https://ru.wikipedia.org/wiki/Корень_(математика)");
            }
        }
    }

    private void onInverseClick() {
        if (!currentNumber.isEmpty()) {
            Double number = Double.parseDouble(currentNumber);
            if (number != 0) {
                Double result = 1 / number;
                display.setText(decimalFormat.format(result));
                currentNumber = result.toString();
            } else {
                showAlertDialog("Нельзя делить на ноль", "https://ru.wikipedia.org/wiki/Деление_на_ноль");
            }
        }
    }

    private void onMemoryPlusClick() {
        if (!currentNumber.isEmpty()) {
            memory = Double.parseDouble(currentNumber);
            Toast.makeText(this, "Число сохранено в память (M+)", Toast.LENGTH_SHORT).show();
        }
    }

    private void onMemoryRecallClick() {
        if (memory != null) {
            String strMemory = memory.toString();
            if (strMemory.matches("^\\d\\.0+$")) {
                currentNumber = strMemory.substring(0, strMemory.indexOf("."));
            }
            else currentNumber = strMemory;
            display.setText(currentNumber);
        }
    }

    private void onMemoryClearClick() {
        memory = null;
        Toast.makeText(this, "Память очищена (MC)", Toast.LENGTH_SHORT).show();
    }
}
