package mvc;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class LogController {

    private Model model;
    private Controller controller;

    private LogMessageFactory logMessageFactory;
    private List<String> undoList;
    private int undoPointer = -1;

    @FXML
    private TextArea textPanel;

    @FXML
    private void initialize() {

        logMessageFactory = new LogMessageFactory();

        undoList = new LinkedList<>();

        textPanel.setText(logMessageFactory.getRandomMessage("init") + "\n");

        // adjust the font size according to panel size
        textPanel.heightProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                textPanel.setFont(Font.font(newValue.doubleValue() / 12));
            });
        });

    }

    public void action(Fighter fighter, String actionPerformed) {

        synchronized (this) {
            // Die Liste auf 10 Einträge begrenzen.
            if (undoList.size() > 10) {
                undoList.remove(0);
            }
            // Den Zeiger auf die Liste auf die Länge der Liste beschränken.
            if (undoPointer >= 10) {
                undoPointer = undoList.size() - 1;
            }

            String name = fighter.getName();
            String message = " " + logMessageFactory.getRandomMessage(actionPerformed);
            String change = " [ INI " + fighter.getPreviousIni() + " -> " + fighter.getIni() + " ]\n";

            String actionText = name + message;

            if (actionPerformed.equals("join")) {
                actionText = actionText + " [ INI: " + fighter.getIni() + " ]\n";
            } else if (!actionPerformed.equals("leave")) {
                actionText = actionText + change;
            }

            textPanel.appendText(actionText);

            undoPointer = undoPointer + 1;
            undoList.subList(undoPointer, undoList.size()).clear();
            undoList.add(undoPointer, actionText);
        }
    }

    public void undoAction() {

        if (undoPointer >= 0) {
            int entryLength = undoList.get(undoPointer).length();
            int textLength = textPanel.getText().length();
            textPanel.setText(textPanel.getText(0, textLength - entryLength));
            textPanel.layout();
            textPanel.setScrollTop(textPanel.getHeight());
            undoPointer = undoPointer - 1;
        }

    }

    public void redoAction() {

        if (undoPointer < undoList.size() - 1) {
            undoPointer = undoPointer + 1;
            textPanel.setText(textPanel.getText() + undoList.get(undoPointer));
            textPanel.layout();
            textPanel.setScrollTop(textPanel.getHeight());
        }

    }

    /**
     * Deletes text from the log.
     * @param linesToDelete Number of lines to be deleted, starting from last one.
     *              Set to 0 to delete all text.
     */
    public void clearLog(int linesToDelete) {
        if (linesToDelete < 0) return;
        if (linesToDelete == 0) {
            textPanel.clear();
        } else {
            // count lines by splitting at '\n'
            String text = textPanel.getText();
            String[] content = text.split("\n");
            int lineCount = content.length;
            // make sure there are enough lines
            if (lineCount < linesToDelete) return;

            int charCount = 0;
            for (int line = lineCount - 1; line > linesToDelete; line--) {
                charCount += content[line].length() + 2;    // includes '\n'
            }

            textPanel.setText(text.substring(0, text.length() - charCount - 1));
        }
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }
}
