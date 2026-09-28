package by.bsuir.lr1;

import by.bsuir.lr1.controller.MorseController;
import by.bsuir.lr1.model.MorseModel;
import by.bsuir.lr1.view.MainFrame;
import javax.swing.SwingUtilities;

public final class Main {
    private Main() { }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MorseModel model = new MorseModel();
            MainFrame view = new MainFrame();
            MorseController controller = new MorseController(model, view);
            model.addListener(view);
            view.setActionHandler(controller);
            view.setVisible(true);
        });
    }
}
