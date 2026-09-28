package by.bsuir.lr1.view;

import javax.swing.*;
import java.awt.*;

public class InputDialog extends JDialog {
    private static final Color WINDOW_BACKGROUND = new Color(24, 26, 31);
    private static final Color INPUT_BACKGROUND = new Color(27, 30, 36);
    private static final Color TEXT_PRIMARY = new Color(239, 243, 248);
    private static final Color BORDER = new Color(70, 76, 88);

    private final JTextArea inputArea = new JTextArea(12, 84);
    private boolean confirmed;

    public InputDialog(JFrame owner) {
        super(owner, "Ввод сигналов Азбуки Морзе", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        JPanel root = new JPanel(new BorderLayout(14, 14));
        root.setBorder(BorderFactory.createEmptyBorder(22, 24, 22, 24));
        root.setBackground(WINDOW_BACKGROUND);
        JLabel hint = new JLabel("Буквы разделяйте пробелами, слова — знаком /");
        hint.setFont(hint.getFont().deriveFont(20f));
        hint.setForeground(TEXT_PRIMARY);
        root.add(hint, BorderLayout.NORTH);
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setFont(new Font(Font.MONOSPACED, Font.BOLD, 30));
        inputArea.setForeground(TEXT_PRIMARY);
        inputArea.setBackground(INPUT_BACKGROUND);
        inputArea.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        JScrollPane inputScroll = new JScrollPane(inputArea);
        inputScroll.setPreferredSize(new Dimension(1100, 520));
        inputScroll.setBorder(BorderFactory.createLineBorder(BORDER));
        inputScroll.getViewport().setBackground(INPUT_BACKGROUND);
        root.add(inputScroll, BorderLayout.CENTER);

        JButton okButton = new JButton("ОК");
        JButton cancelButton = new JButton("Отмена");
        okButton.setFont(okButton.getFont().deriveFont(Font.BOLD, 20f));
        cancelButton.setFont(cancelButton.getFont().deriveFont(20f));
        okButton.setPreferredSize(new Dimension(160, 56));
        cancelButton.setPreferredSize(new Dimension(160, 56));
        okButton.setForeground(Color.WHITE);
        okButton.setBackground(new Color(37, 99, 235));
        cancelButton.setForeground(TEXT_PRIMARY);
        cancelButton.setBackground(new Color(55, 60, 70));
        okButton.setFocusPainted(false);
        cancelButton.setFocusPainted(false);
        okButton.addActionListener(e -> {
            confirmed = true;
            dispose();
        });
        cancelButton.addActionListener(e -> dispose());
        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        buttons.add(okButton);
        buttons.add(cancelButton);
        root.add(buttons, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(okButton);
        setContentPane(root);
        pack();
        setMinimumSize(new Dimension(1000, 650));
        setSize(1200, 720);
        setLocationRelativeTo(owner);
    }

    public void setInputText(String value) {
        inputArea.setText(value);
        inputArea.selectAll();
    }

    public String getInputText() { return inputArea.getText(); }

    public boolean isConfirmed() { return confirmed; }
}
