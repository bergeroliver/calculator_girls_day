package epos.girlsday.calculator;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import epos.girlsday.calculator.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        binding.button0.setOnClickListener(handleClickDefault);
        binding.button1.setOnClickListener(handleClickDefault);
        binding.button2.setOnClickListener(handleClickDefault);
        binding.button3.setOnClickListener(handleClickDefault);
        binding.button4.setOnClickListener(handleClickDefault);
        binding.button5.setOnClickListener(handleClickDefault);
        binding.button6.setOnClickListener(handleClickDefault);
        binding.button7.setOnClickListener(handleClickDefault);
        binding.button8.setOnClickListener(handleClickDefault);
        binding.button9.setOnClickListener(handleClickDefault);
        binding.buttonAdd.setOnClickListener(handleClickDefault);
        binding.buttonSubtract.setOnClickListener(handleClickDefault);
        binding.buttonMultiply.setOnClickListener(handleClickDefault);
        binding.buttonDivide.setOnClickListener(handleClickDefault);

        binding.buttonEqual.setOnClickListener(handleClickResult);

        binding.buttonClear.setOnClickListener(handleClickClear);
    }

    private final View.OnClickListener handleClickDefault = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v instanceof Button) {
                Button button = (Button) v;
                char c = button.getText().charAt(0);
                if (binding.tvResult.getText().length() == 0 && (c == '+' || c == '-' || c == '\u00D7' || c == '\u00F7')) {
                    binding.tvResult.setText("0");
                }
                binding.tvResult.append(button.getText());
            }
        }
    };

    private final View.OnClickListener handleClickResult = v -> {
        if (v.getId() != binding.buttonEqual.getId()) {
            return;
        }
        String text = binding.tvResult.getText().toString();
        if (text.isEmpty()) {
            return;
        }
        char firstCharacter = text.charAt(0);
        // Erstes Zeichen muss eine Zahl sein!
        if (firstCharacter < '0' || firstCharacter > '9') {
            binding.tvResult.setText("ERROR");
            return;
        }

        List<Integer> orderedNumberList = Arrays.stream(text.split("[-+\u00D7\u00F7]")).map(Integer::parseInt).collect(Collectors.toList());
        List<Character> orderedOperatorList = new ArrayList<>();
        for (char c : text.toCharArray()) {
            if (c == '+' || c == '-' || c == '\u00D7' || c == '\u00F7') {
                orderedOperatorList.add(c);
            }
        }

        // Punkt-Run (Mal, Geteilt)
        ListIterator<Character> iterator = orderedOperatorList.listIterator();
        while(iterator.hasNext()) {
            Character operator = iterator.next();
            int firstOperand;
            int secondOperand;
            int result;
            switch (operator) {
                // Multiplication
                case '\u00D7':
                    firstOperand = orderedNumberList.get(iterator.previousIndex());
                    secondOperand = orderedNumberList.get(iterator.nextIndex());
                    result = firstOperand * secondOperand;
                    orderedNumberList.set(iterator.previousIndex(), result);
                    orderedNumberList.remove(iterator.nextIndex());
                    iterator.remove();
                    break;
                // Division
                case '\u00FD':
                    firstOperand = orderedNumberList.get(iterator.previousIndex());
                    secondOperand = orderedNumberList.get(iterator.nextIndex());
                    result = firstOperand / secondOperand;
                    orderedNumberList.set(iterator.previousIndex(), result);
                    orderedNumberList.remove(iterator.nextIndex());
                    iterator.remove();
                    break;
                // Addition and Subtraction (do nothing at first)
                default:
                    break;
            }
        }

        int endResult = orderedNumberList.get(0);
        // Strich-Run (Plus, Minus)
        for (int i = 0; i < orderedOperatorList.size(); i++) {
            if (orderedOperatorList.get(i) == '+') {
                endResult = endResult + orderedNumberList.get(i + 1);
            } else {
                endResult = endResult - orderedNumberList.get(i + 1);
            }
        }

        binding.tvResult.setText(String.valueOf(endResult));
    };

    private final View.OnClickListener handleClickClear = v -> {
        if (v.getId() == binding.buttonClear.getId()) {
            binding.tvResult.setText("");
        }
    };


}