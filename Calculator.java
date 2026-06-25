import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Calculator extends JFrame implements ActionListener {
    private JTextField display;
    private StringBuilder input;
    
    private List<String> history;
    private static final String HISTORY_FILE = "calculator_history.txt";
    
    private boolean isScientificMode;
    private JPanel scientificPanel;

    public Calculator() {
        super("Multi-Function Calculator");
        input = new StringBuilder();
        history = new ArrayList<>();
        isScientificMode = false;
        
        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        
        createMenuBar();
        
        display = new JTextField("0");
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setFont(new Font("Arial", Font.PLAIN, 28));
        display.setEditable(false);  // 禁用默认输入，完全由自定义逻辑控制
        display.setBackground(Color.WHITE);
        display.setFocusable(true);
        display.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent e) {
                handleKeyPress(e);
            }
        });
        display.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                display.setCaretPosition(display.getText().length());  // 点击时光标移到末尾
            }
        });
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(display, BorderLayout.NORTH);
        mainPanel.add(createStandardPanel(), BorderLayout.CENTER);
        
        scientificPanel = createScientificPanel();
        mainPanel.add(scientificPanel, BorderLayout.EAST);
        scientificPanel.setVisible(false);
        
        add(mainPanel);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("File(F)");
        fileMenu.setMnemonic('F');
        
        JMenuItem viewHistory = new JMenuItem("View History");
        viewHistory.setMnemonic('V');
        viewHistory.addActionListener(e -> viewHistory());
        
        JMenuItem clearHistory = new JMenuItem("Clear History");
        clearHistory.setMnemonic('C');
        clearHistory.addActionListener(e -> clearHistory());
        
        JMenuItem exit = new JMenuItem("Exit");
        exit.setMnemonic('X');
        exit.addActionListener(e -> System.exit(0));
        
        fileMenu.add(viewHistory);
        fileMenu.add(clearHistory);
        fileMenu.addSeparator();
        fileMenu.add(exit);
        
        JMenu editMenu = new JMenu("Edit(E)");
        editMenu.setMnemonic('E');
        
        JMenuItem copy = new JMenuItem("Copy");
        copy.setMnemonic('C');
        copy.addActionListener(e -> display.copy());
        
        JMenuItem paste = new JMenuItem("Paste");
        paste.setMnemonic('P');
        paste.addActionListener(e -> display.paste());
        
        editMenu.add(copy);
        editMenu.add(paste);
        
        JMenu viewMenu = new JMenu("View(V)");
        viewMenu.setMnemonic('V');
        
        JCheckBoxMenuItem scientificMode = new JCheckBoxMenuItem("Scientific Mode");
        scientificMode.setMnemonic('S');
        scientificMode.addActionListener(e -> toggleScientificMode());
        
        viewMenu.add(scientificMode);
        
        JMenu helpMenu = new JMenu("Help(H)");
        helpMenu.setMnemonic('H');
        
        JMenuItem about = new JMenuItem("About");
        about.setMnemonic('A');
        about.addActionListener(e -> showAbout());
        
        helpMenu.add(about);
        
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }

    private JPanel createStandardPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 4, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        String[] buttons = {
            "C", "del", "(", ")",
            "7", "8", "9", "/",
            "4", "5", "6", "*",
            "1", "2", "3", "-",
            "0", ".", "=", "+",
            "pi", "e", "^", "sqrt"
        };
        
        for (String text : buttons) {
            JButton btn = createButton(text);
            panel.add(btn);
        }
        
        return panel;
    }

    private JPanel createScientificPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 10));
        
        String[] buttons = {
            "sin", "cos",
            "tan", "lg",
            "log", "ln",
            "deg", "rad",
            "1/x", ",",
            "asin", "acos"
        };
        
        for (String text : buttons) {
            JButton btn = createButton(text);
            panel.add(btn);
        }
        
        return panel;
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.PLAIN, 14));
        
        if (text.matches("[0-9]") || text.equals(".")) {
            btn.setBackground(Color.WHITE);
            btn.setForeground(Color.BLACK);
        } else if (text.equals("=")) {
            btn.setBackground(new Color(70, 130, 180));
            btn.setForeground(Color.WHITE);
        } else if (text.equals("C")) {
            btn.setBackground(new Color(220, 50, 50));
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(new Color(240, 240, 240));
            btn.setForeground(Color.BLACK);
        }
        
        btn.addActionListener(this);
        btn.setFocusPainted(false);
        
        return btn;
    }

    private void handleKeyPress(java.awt.event.KeyEvent e) {
        char c = e.getKeyChar();
        int keyCode = e.getKeyCode();
        
        if (Character.isDigit(c) || c == '.' || c == '+' || c == '-' || c == '*' || c == '/' || c == '^' || c == '(' || c == ')') {
            input.append(c);
            display.setText(input.toString());
            e.consume();
        } else if (keyCode == java.awt.event.KeyEvent.VK_ENTER || keyCode == java.awt.event.KeyEvent.VK_EQUALS) {
            calculate();
            e.consume();
        } else if (keyCode == java.awt.event.KeyEvent.VK_ESCAPE || (keyCode == java.awt.event.KeyEvent.VK_C && e.isControlDown())) {
            clear();
            e.consume();
        } else if (keyCode == java.awt.event.KeyEvent.VK_BACK_SPACE) {
            if (input.length() > 0) {
                input.deleteCharAt(input.length() - 1);
                display.setText(input.length() > 0 ? input.toString() : "0");
            }
            e.consume();
        } else if (keyCode == java.awt.event.KeyEvent.VK_DELETE) {
            if (input.length() > 0) {
                int cursorPos = display.getCaretPosition();
                if (cursorPos > 0 && cursorPos <= input.length()) {
                    input.deleteCharAt(cursorPos - 1);
                    display.setText(input.toString());
                    display.setCaretPosition(cursorPos - 1);
                }
            }
            e.consume();
        } else if (keyCode == java.awt.event.KeyEvent.VK_LEFT) {
            e.consume();
        } else if (keyCode == java.awt.event.KeyEvent.VK_RIGHT) {
            e.consume();
        } else if (keyCode == java.awt.event.KeyEvent.VK_P && e.isControlDown()) {
            input.append("pi");
            display.setText(input.toString());
            e.consume();
        } else if (keyCode == java.awt.event.KeyEvent.VK_E && e.isControlDown()) {
            input.append("e");
            display.setText(input.toString());
            e.consume();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        
        try {
            if (command.matches("[0-9]")) {
                input.append(command);
                display.setText(input.toString());
            } else if (command.equals(".")) {
                if (!input.toString().contains(".")) {
                    input.append(".");
                    display.setText(input.toString());
                }
            } else if (command.matches("[+\\-*/^]")) {
                input.append(command);
                display.setText(input.toString());
            } else if (command.equals("=")) {
                calculate();
            } else if (command.equals("C")) {
                clear();
            } else if (command.equals("del")) {
                if (input.length() > 0) {
                    input.deleteCharAt(input.length() - 1);
                    display.setText(input.length() > 0 ? input.toString() : "0");
                }
            } else if (command.equals("(") || command.equals(")")) {
                input.append(command);
                display.setText(input.toString());
            } else if (command.equals("pi")) {
                input.append("pi");
                display.setText(input.toString());
            } else if (command.equals("e")) {
                input.append("e");
                display.setText(input.toString());
            } else if (command.equals("sqrt")) {
                input.append("sqrt(");
                display.setText(input.toString());
            } else if (command.equals("sin")) {
                input.append("sin(");
                display.setText(input.toString());
            } else if (command.equals("cos")) {
                input.append("cos(");
                display.setText(input.toString());
            } else if (command.equals("tan")) {
                input.append("tan(");
                display.setText(input.toString());
            } else if (command.equals("lg")) {
                input.append("lg(");
                display.setText(input.toString());
            } else if (command.equals("log")) {
                input.append("log(");
                display.setText(input.toString());
            } else if (command.equals("ln")) {
                input.append("ln(");
                display.setText(input.toString());
            } else if (command.equals(",")) {
                input.append(",");
                display.setText(input.toString());
            } else if (command.equals("deg")) {
                JOptionPane.showMessageDialog(this, "Switched to Degree Mode", "Mode", JOptionPane.INFORMATION_MESSAGE);
            } else if (command.equals("rad")) {
                JOptionPane.showMessageDialog(this, "Switched to Radian Mode", "Mode", JOptionPane.INFORMATION_MESSAGE);
            } else if (command.equals("1/x")) {
                input.append("1/");
                display.setText(input.toString());
            } else if (command.equals("asin")) {
                input.append("asin(");
                display.setText(input.toString());
            } else if (command.equals("acos")) {
                input.append("acos(");
                display.setText(input.toString());
            }
        } catch (Exception ex) {
            display.setText("Error");
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clear() {
        input = new StringBuilder();
        display.setText("0");
    }

    private void calculate() {
        String expression = display.getText();
        input = new StringBuilder(expression);
        try {
            double result = evaluateExpression(expression);
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String historyRecord = now.format(formatter) + " | " + expression + " = " + result;
            history.add(historyRecord);
            autoSaveHistory();
            display.setText(String.valueOf(result));
            input = new StringBuilder(String.valueOf(result));
        } catch (Exception ex) {
            display.setText("Error");
            JOptionPane.showMessageDialog(this, "Calculation error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void autoSaveHistory() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HISTORY_FILE))) {
            for (String record : history) {
                writer.write(record);
                writer.newLine();
            }
        } catch (IOException ex) {
            // 自动保存失败不提示，避免打扰用户
        }
    }

    private void syncInput() {
        input = new StringBuilder(display.getText());
    }

    private double evaluateExpression(String expression) throws Exception {
        if (expression == null || expression.trim().isEmpty()) {
            throw new Exception("Empty expression");
        }
        List<String> tokens = tokenize(expression);
        if (tokens.isEmpty()) {
            throw new Exception("Invalid expression");
        }
        return parseExpression(tokens);
    }

    private List<String> tokenize(String expression) {
        List<String> tokens = new ArrayList<>();
        StringBuilder num = new StringBuilder();
        
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            
            if (Character.isDigit(c) || c == '.') {
                num.append(c);
            } else if (c == 'p' && i + 1 < expression.length() && expression.charAt(i + 1) == 'i') {
                tokens.add("pi");
                i++;
            } else if (c == 'e') {
                tokens.add("e");
            } else if (c == 's' && i + 2 < expression.length() && expression.substring(i, i + 3).equals("sin")) {
                tokens.add("sin");
                i += 2;
            } else if (c == 'c' && i + 2 < expression.length() && expression.substring(i, i + 3).equals("cos")) {
                tokens.add("cos");
                i += 2;
            } else if (c == 't' && i + 2 < expression.length() && expression.substring(i, i + 3).equals("tan")) {
                tokens.add("tan");
                i += 2;
            } else if (c == 'l' && i + 1 < expression.length()) {
                if (expression.charAt(i + 1) == 'o') {
                    tokens.add("log");
                    i += 2;
                } else if (expression.charAt(i + 1) == 'n') {
                    tokens.add("ln");
                    i += 1;
                } else if (expression.charAt(i + 1) == 'g') {
                    tokens.add("lg");
                    i += 1;
                }
            } else if (c == 's' && i + 3 < expression.length() && expression.substring(i, i + 4).equals("sqrt")) {
                tokens.add("sqrt");
                i += 3;
            } else if (c == 'a' && i + 3 < expression.length()) {
                if (expression.substring(i, i + 4).equals("asin")) {
                    tokens.add("asin");
                    i += 3;
                } else if (expression.substring(i, i + 4).equals("acos")) {
                    tokens.add("acos");
                    i += 3;
                }
            } else if (c == '+' || c == '-' || c == '*' || c == '/' || c == '^' || c == '(' || c == ')' || c == ',') {
                if (num.length() > 0) {
                    tokens.add(num.toString());
                    num = new StringBuilder();
                }
                tokens.add(String.valueOf(c));
            }
        }
        
        if (num.length() > 0) {
            tokens.add(num.toString());
        }
        
        return tokens;
    }

    private double parseExpression(List<String> tokens) throws Exception {
        Stack<Double> values = new Stack<>();
        Stack<String> ops = new Stack<>();
        String prevToken = null;  // 用于判断一元正负号
        
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            
            if (token.equals("pi")) {
                values.push(Math.PI);
            } else if (token.equals("e")) {
                values.push(Math.E);
            } else if (token.matches("[0-9.]+")) {
                values.push(Double.parseDouble(token));
            } else if (token.equals("sin") || token.equals("cos") || token.equals("tan") ||
                       token.equals("log") || token.equals("ln") || token.equals("lg") ||
                       token.equals("sqrt") || token.equals("asin") || token.equals("acos")) {
                ops.push(token);
            } else if (token.equals("(")) {
                ops.push(token);
            } else if (token.equals(")")) {
                while (!ops.isEmpty() && !ops.peek().equals("(")) {
                    applyOp(values, ops);
                }
                if (ops.isEmpty()) {
                    throw new Exception("Mismatched parentheses");
                }
                ops.pop();
                
                if (!ops.isEmpty() && isFunction(ops.peek())) {
                    applyFunc(values, ops);
                }
            } else if (token.equals(",")) {
                while (!ops.isEmpty() && !ops.peek().equals("(")) {
                    applyOp(values, ops);
                }
                if (ops.isEmpty()) {
                    throw new Exception("Invalid comma usage");
                }
            } else if (token.matches("[+\\-*/^]")) {
                // 检测一元正负号：前面没有操作数时补0
                // 一元条件：前面是左括号、运算符、或表达式开头
                boolean isUnary = (prevToken == null || prevToken.equals("(") || prevToken.equals(",")) ||
                                  (prevToken.matches("[+\\-*/^]"));
                if (isUnary && (token.equals("+") || token.equals("-"))) {
                    values.push(0.0);
                }
                while (!ops.isEmpty() && !ops.peek().equals("(") && 
                       precedence(ops.peek()) >= precedence(token)) {
                    applyOp(values, ops);
                }
                ops.push(token);
            } else {
                throw new Exception("Invalid token: " + token);
            }
            
            prevToken = token;
        }
        
        while (!ops.isEmpty()) {
            if (ops.peek().equals("(")) {
                throw new Exception("Mismatched parentheses");
            }
            if (isFunction(ops.peek())) {
                applyFunc(values, ops);
            } else {
                applyOp(values, ops);
            }
        }
        
        if (values.size() != 1) {
            throw new Exception("Invalid expression");
        }
        
        return values.pop();
    }

    private boolean isFunction(String op) {
        return op.equals("sin") || op.equals("cos") || op.equals("tan") ||
               op.equals("log") || op.equals("ln") || op.equals("lg") ||
               op.equals("sqrt") || op.equals("asin") || op.equals("acos");
    }

    private int precedence(String op) {
        switch (op) {
            case "+":
            case "-":
                return 1;
            case "*":
            case "/":
                return 2;
            case "^":
                return 3;
            default:
                return 0;
        }
    }

    private void applyOp(Stack<Double> values, Stack<String> ops) throws Exception {
        String op = ops.pop();
        if (values.size() < 2) {
            throw new Exception("Not enough operands for " + op);
        }
        double b = values.pop();
        double a = values.pop();
        
        switch (op) {
            case "+": values.push(a + b); break;
            case "-": values.push(a - b); break;
            case "*": values.push(a * b); break;
            case "/":
                if (b == 0) throw new Exception("Division by zero");
                values.push(a / b); 
                break;
            case "^": values.push(Math.pow(a, b)); break;
        }
    }

    private void applyFunc(Stack<Double> values, Stack<String> ops) throws Exception {
        String func = ops.pop();
        
        switch (func) {
            case "sin":
            case "cos":
            case "tan":
            case "lg":
            case "ln":
            case "sqrt":
            case "asin":
            case "acos":
                if (values.isEmpty()) {
                    throw new Exception("Not enough operands for " + func);
                }
                break;
            case "log":
                if (values.size() < 2) {
                    throw new Exception("Not enough operands for log (need base and argument)");
                }
                break;
        }
        
        double x = values.pop();
        
        switch (func) {
            case "sin":
                values.push(Math.sin(x));
                break;
            case "cos":
                values.push(Math.cos(x));
                break;
            case "tan":
                values.push(Math.tan(x));
                break;
            case "lg":
                if (x <= 0) throw new Exception("Lg argument must be > 0 (got " + x + ")");
                values.push(Math.log10(x));
                break;
            case "ln":
                if (x <= 0) throw new Exception("Ln argument must be > 0 (got " + x + ")");
                values.push(Math.log(x));
                break;
            case "log":
                double base = values.pop();
                if (x <= 0) throw new Exception("Log argument must be > 0 (got " + x + ")");
                if (base <= 0 || base == 1) throw new Exception("Log base must be > 0 and != 1 (got " + base + ")");
                values.push(Math.log(x) / Math.log(base));
                break;
            case "sqrt":
                if (x < 0) throw new Exception("Cannot sqrt negative number (got " + x + ")");
                values.push(Math.sqrt(x));
                break;
            case "asin":
                if (x < -1 || x > 1) throw new Exception("Asin argument out of range [-1, 1] (got " + x + ")");
                values.push(Math.asin(x));
                break;
            case "acos":
                if (x < -1 || x > 1) throw new Exception("Acos argument out of range [-1, 1] (got " + x + ")");
                values.push(Math.acos(x));
                break;
        }
    }

    private void toggleScientificMode() {
        isScientificMode = !isScientificMode;
        scientificPanel.setVisible(isScientificMode);
        setSize(isScientificMode ? 520 : 400, 600);
        setLocationRelativeTo(null);
    }

    private void clearHistory() {
        int confirm = JOptionPane.showConfirmDialog(this, "Clear history?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            history.clear();
            new File(HISTORY_FILE).delete();
            JOptionPane.showMessageDialog(this, "History cleared", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void viewHistory() {
        // 自动从文件加载历史记录
        File file = new File(HISTORY_FILE);
        if (file.exists() && history.isEmpty()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    history.add(line);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "加载失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        if (history.isEmpty()) {
            JOptionPane.showMessageDialog(this, "历史记录为空", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 按日期分组显示
        StringBuilder sb = new StringBuilder();
        String currentDate = "";
        
        for (String record : history) {
            // 格式: yyyy-MM-dd HH:mm:ss | expression = result
            String date = "";
            String timeAndCalc = record;
            
            if (record.contains("|")) {
                String[] parts = record.split("\\|");
                if (parts.length >= 2) {
                    String dateTime = parts[0].trim();
                    if (dateTime.contains(" ")) {
                        date = dateTime.split(" ")[0];
                        timeAndCalc = dateTime.split(" ")[1] + " | " + parts[1].trim();
                    }
                }
            }
            
            // 如果日期变化，添加日期标题
            if (!date.isEmpty() && !date.equals(currentDate)) {
                if (!currentDate.isEmpty()) {
                    sb.append("\n");
                }
                sb.append("【").append(date).append("】\n");
                currentDate = date;
            }
            
            sb.append("  ").append(timeAndCalc).append("\n");
        }
        
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(350, 250));
        
        JOptionPane.showMessageDialog(this, scrollPane, "历史记录 (" + history.size() + " 条)", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAbout() {
        JOptionPane.showMessageDialog(this, 
            "多功能计算器 v1.2\n\n" +
            "功能：\n" +
            "- 基本四则运算\n" +
            "- 科学计算函数\n" +
            "- 表达式解析\n" +
            "- 历史记录管理\n" +
            "- 键盘输入支持\n\n" +
            "开发者： Kam1ng From GDUT\n" +
            "https://github.com/Kam1ng/Calculator", 
            "关于", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Calculator calculator = new Calculator();
            calculator.setVisible(true);
        });
    }
}