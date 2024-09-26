package com.example.mycalculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class MainActivity extends AppCompatActivity {
    TextView tvExpression, tvResult;
    StringBuilder expression = new StringBuilder();
    boolean isResultDisplayed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvExpression = findViewById(R.id.tvExpression);
        tvResult = findViewById(R.id.tvResult);

        setButtonListeners();
    }

    private void setButtonListeners() {
        int[] buttonIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
                R.id.btnDot, R.id.btnAdd, R.id.btnSubtract, R.id.btnMultiply, R.id.btnDivide,
                R.id.btnOpenBrackets, R.id.btnCloseBrackets, R.id.btnC, R.id.btnAC, R.id.btnEquals
        };

        for (int id : buttonIds) {
            Button button = findViewById(id);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleButtonClick((Button) v);
                }
            });
        }
    }

    private void handleButtonClick(Button button) {
        String buttonText = button.getText().toString();

        switch (buttonText) {
            case "C":
                clearLastCharacter();
                break;
            case "AC":
                clearAll();
                break;
            case "=":
                calculateResult();
                break;
            default:
                appendToExpression(buttonText);
                break;
        }
    }

    private void appendToExpression(String text) {
        if (isResultDisplayed) {
            expression.setLength(0);  // Очищаем выражение после отображения результата
            isResultDisplayed = false;
        }

        String operators = "+-*/";

        // Не добавляем ноль в начале выражения или сразу после оператора
        if (text.equals("0")) {
            if (expression.length() == 0 || operators.contains(String.valueOf(expression.charAt(expression.length() - 1)))) {
                return;
            }
        }

        // Проверка на добавление закрывающей скобки
        if (text.equals(")")) {
            // Проверка, что количество закрывающих скобок не превышает количество открывающих
            int openBrackets = 0;
            int closeBrackets = 0;
            for (int i = 0; i < expression.length(); i++) {
                if (expression.charAt(i) == '(') openBrackets++;
                if (expression.charAt(i) == ')') closeBrackets++;
            }
            // Запрет на добавление закрывающей скобки, если нет соответствующей открывающей
            if (closeBrackets >= openBrackets) {
                return;
            }
        }

        // Проверка на добавление оператора или числа
        if (operators.contains(text)) {
            // Оператор не может быть в начале или сразу после открывающей скобки
            if (expression.length() == 0 || expression.charAt(expression.length() - 1) == '(') {
                return;
            }

            // Запрет на повтор операторов
            if (expression.length() > 0 && operators.contains(String.valueOf(expression.charAt(expression.length() - 1)))) {
                return;
            }
        }

        // Проверка на добавление открывающей скобки
        if (text.equals("(")) {
            // Открывающая скобка может стоять в начале или после оператора
            if (expression.length() > 0 && !operators.contains(String.valueOf(expression.charAt(expression.length() - 1)))) {
                return;
            }
        }

        // Добавление текста в выражение
        expression.append(text);
        tvExpression.setText(expression.toString());
        tvResult.setText("");
    }

    // Проверка корректности баланса скобок
    private boolean isValidParentheses(String expr) {
        int balance = 0;
        for (char ch : expr.toCharArray()) {
            if (ch == '(') {
                balance++;
            } else if (ch == ')') {
                balance--;
            }
            if (balance < 0) {
                return false;
            }
        }
        return balance == 0;
    }

    private void clearLastCharacter() {
        if (expression.length() > 0) {
            expression.deleteCharAt(expression.length() - 1);
            tvExpression.setText(expression.toString());
        }
    }

    private void clearAll() {
        expression.setLength(0);
        tvExpression.setText("");
        tvResult.setText("");
        isResultDisplayed = false;
    }

    private void calculateResult() {
        try {
            Expression exp = new ExpressionBuilder(expression.toString()).build();
            double result = exp.evaluate();
            tvResult.setText(String.valueOf(result));
            isResultDisplayed = true;
        } catch (Exception e) {
            tvResult.setText("Error");
        }
    }
}