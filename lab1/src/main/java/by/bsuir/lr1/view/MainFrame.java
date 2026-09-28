package by.bsuir.lr1.view;

import by.bsuir.lr1.model.DecodeResult;
import by.bsuir.lr1.model.ModelListener;
import by.bsuir.lr1.model.MorseModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class MainFrame extends JFrame implements ModelListener {
    private static final Color WINDOW_BACKGROUND = new Color(24, 26, 31);
    private static final Color PANEL_BACKGROUND = new Color(36, 40, 48);
    private static final Color INPUT_BACKGROUND = new Color(27, 30, 36);
    private static final Color BORDER = new Color(77, 84, 98);
    private static final Color TEXT_PRIMARY = new Color(241, 245, 249);
    private static final Color TEXT_SECONDARY = new Color(178, 188, 202);
    private static final Color ACCENT = new Color(65, 132, 255);

    private final JTextArea sourceArea = readOnlyArea();
    private final JTextArea resultArea = readOnlyArea();
    private final JLabel signalCountLabel = countLabel();
    private final JLabel wordCountLabel = countLabel();
    private final JButton enterDataButton = new JButton("Ввести данные");
    private ViewActionHandler actionHandler;

    public MainFrame() {
        super("Декодирование Азбуки Морзе");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        buildLayout();
        pack();
        setMinimumSize(new Dimension(1050, 760));
        setSize(1400, 920);
        setLocationRelativeTo(null);
    }

    public void setActionHandler(ViewActionHandler handler) {
        this.actionHandler = handler;
    }

    public void showError(String message) {
        JDialog dialog = new JDialog(this, "Ошибка ввода", true);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(18, 18));
        root.setBackground(new Color(43, 30, 34));
        root.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JLabel title = new JLabel("Ошибка ввода", SwingConstants.CENTER);
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 27));
        title.setForeground(new Color(255, 185, 185));
        root.add(title, BorderLayout.NORTH);

        JTextPane messagePane = new JTextPane();
        messagePane.setText(message);
        messagePane.setEditable(false);
        messagePane.setFocusable(false);
        messagePane.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 24));
        messagePane.setForeground(new Color(255, 226, 226));
        messagePane.setBackground(new Color(54, 38, 43));
        messagePane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 70, 80)),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)));
        centerText(messagePane);
        root.add(messagePane, BorderLayout.CENTER);

        JButton closeButton = new JButton("ОК");
        closeButton.setFont(closeButton.getFont().deriveFont(Font.BOLD, 22f));
        closeButton.setForeground(Color.WHITE);
        closeButton.setBackground(new Color(190, 32, 42));
        closeButton.setFocusPainted(false);
        closeButton.setPreferredSize(new Dimension(170, 58));
        closeButton.addActionListener(e -> dialog.dispose());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttons.setOpaque(false);
        buttons.add(closeButton);
        root.add(buttons, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.setMinimumSize(new Dimension(720, 280));
        dialog.setSize(800, 340);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    @Override
    public void modelChanged(MorseModel model) {
        sourceArea.setText(model.getEncodedText());

        DecodeResult result = model.getResult();
        resultArea.setText(result.getDecodedText());
        signalCountLabel.setText(String.valueOf(result.getSignalCount()));
        wordCountLabel.setText(String.valueOf(result.getWordCount()));
    }

    private void buildLayout() {
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(WINDOW_BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(26, 32, 44, 32));

        JPanel header = new JPanel(new BorderLayout(0, 8));
        header.setOpaque(false);

        JLabel title = new JLabel("Декодирование Азбуки Морзе");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 31f));
        title.setForeground(TEXT_PRIMARY);

        JLabel subtitle = new JLabel(
                "Вводите точки и дефисы, а результат появится ниже");
        subtitle.setFont(subtitle.getFont().deriveFont(20f));
        subtitle.setForeground(TEXT_SECONDARY);

        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);

        addRow(root, header, 0, 0.0, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 20, 0));

        addRow(root, textSection("Введённые сигналы", sourceArea), 1, 0.45,
                GridBagConstraints.BOTH, new Insets(0, 0, 16, 0));

        addRow(root, textSection("Результат декодирования", resultArea), 2, 0.45,
                GridBagConstraints.BOTH, new Insets(0, 0, 16, 0));

        addRow(root, statisticsPanel(), 3, 0.0,
                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 18, 0));

        enterDataButton.setFont(
                enterDataButton.getFont().deriveFont(Font.BOLD, 22f));
        enterDataButton.setForeground(Color.WHITE);
        enterDataButton.setBackground(ACCENT);
        enterDataButton.setFocusPainted(false);
        enterDataButton.setBorderPainted(false);
        enterDataButton.setPreferredSize(new Dimension(360, 58));

        enterDataButton.addActionListener(e -> {
            if (actionHandler != null) {
                actionHandler.onEnterDataRequested();
            }
        });

        JPanel buttons = new JPanel(new GridBagLayout());
        buttons.setOpaque(false);

        GridBagConstraints buttonConstraints = new GridBagConstraints();
        buttonConstraints.gridx = 0;
        buttonConstraints.gridy = 0;
        buttonConstraints.weightx = 1.0;
        buttonConstraints.weighty = 1.0;
        buttonConstraints.anchor = GridBagConstraints.CENTER;
        buttonConstraints.fill = GridBagConstraints.NONE;

        buttons.add(enterDataButton, buttonConstraints);

        addRow(root, buttons, 4, 0.0, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0));

        setContentPane(root);
    }

    private static void addRow(JPanel root, JPanel component, int row,
                               double weightY, int fill, Insets insets) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.weightx = 1.0;
        constraints.weighty = weightY;
        constraints.fill = fill;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.insets = insets;
        root.add(component, constraints);
    }

    private static JTextArea readOnlyArea() {
        JTextArea area = new JTextArea(6, 65);
        area.setEditable(false);
        area.setFocusable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font(Font.MONOSPACED, Font.BOLD, 28));
        area.setForeground(TEXT_PRIMARY);
        area.setBackground(INPUT_BACKGROUND);
        area.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        return area;
    }

    private static JPanel textSection(String title, JTextArea area) {
        JPanel section = new JPanel(new BorderLayout(0, 10));
        section.setBackground(PANEL_BACKGROUND);
        section.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(18, 22, 22, 22)));

        JLabel label = new JLabel(title);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 22f));
        label.setForeground(TEXT_PRIMARY);
        section.add(label, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER));
        scroll.getViewport().setBackground(INPUT_BACKGROUND);
        scroll.setPreferredSize(new Dimension(0, 220));

        section.add(scroll, BorderLayout.CENTER);
        return section;
    }

    private JPanel statisticsPanel() {
        JPanel stats = new JPanel(new GridLayout(1, 2, 16, 0));
        stats.setBackground(PANEL_BACKGROUND);
        stats.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(16, 22, 16, 22)));

        stats.add(statistic("Сигналов", signalCountLabel));
        stats.add(statistic("Слов", wordCountLabel));

        return stats;
    }

    private static JPanel statistic(String title, JLabel value) {
        JPanel item = new JPanel(new BorderLayout(10, 0));
        item.setOpaque(false);

        JLabel label = new JLabel(title + ":");
        label.setFont(label.getFont().deriveFont(20f));
        label.setForeground(TEXT_SECONDARY);

        item.add(label, BorderLayout.WEST);
        item.add(value, BorderLayout.CENTER);

        return item;
    }

    private static JLabel countLabel() {
        JLabel label = new JLabel("—");
        label.setFont(label.getFont().deriveFont(Font.BOLD, 26f));
        label.setForeground(ACCENT);
        return label;
    }

    private static void centerText(JTextPane textPane) {
        StyledDocument document = textPane.getStyledDocument();
        SimpleAttributeSet attributes = new SimpleAttributeSet();

        StyleConstants.setAlignment(
                attributes,
                StyleConstants.ALIGN_CENTER);

        document.setParagraphAttributes(
                0,
                document.getLength(),
                attributes,
                false);
    }
}