package mvc;

import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class CircleController implements ChangeListener, ListChangeListener {

    private Model model;
    private Controller controller;

    private GraphicsContext gcBackground;
    private GraphicsContext gcTokens;
    private GraphicsContext gcForeground;

    private ObservableList observableList;
    private HashMap<Fighter, Point2D> coordinateMap;

    private double arcRadius = 150;
    private double arcAngle = 30;
    private double tokenSize = 10;
    private int tokenLimit = 4;
    private double numberRadius = 50;
    private int numberFontSize = 20;
    private int circleMinInsets = 20;

    private int actionIndex = -1;

    private DoubleBinding windowSize;

    @FXML
    private GridPane panel;
    @FXML
    private Canvas backgroundCanvas;
    @FXML
    private Canvas tokenCanvas;
    @FXML
    private Canvas foregroundCanvas;
    private FightersList fightersList;

    @FXML
    private void initialize() {

        gcBackground = backgroundCanvas.getGraphicsContext2D();
        gcTokens = tokenCanvas.getGraphicsContext2D();
        gcForeground = foregroundCanvas.getGraphicsContext2D();

        windowSize = panel.widthProperty().add(panel.heightProperty());
        windowSize.addListener(this);

        coordinateMap = new HashMap<>();
        tokenLimit = calcTokenLimit();

        updateCanvasSize();
    }

    @Override
    public void changed(ObservableValue observable, Object oldValue, Object newValue) {

        updateCanvasSize();

        arcRadius = Math.min(panel.getHeight(), panel.getWidth()) -
                2 * Math.min(getBorderOffsets().getWidth(), getBorderOffsets().getHeight());

        numberFontSize = (int) (panel.getHeight() / 20);
        numberRadius = arcRadius / 7;                           // Faktor passend gewählt.

        tokenSize = Math.toRadians(arcAngle / 14) * arcRadius;  // Faktor passend gewählt.

        Platform.runLater(this::draw);
    }

    @Override
    public void onChanged(Change c) {
        c.next();
        if (c.wasPermutated() || c.wasAdded() || c.wasRemoved() || c.wasUpdated()) {
            updateArcs();
            placeTokens();
        }
    }

    /**
     * Zeichnet die Zeichenebenen zusammen neu.
     */
    private void draw() {
        gcBackground.clearRect(0, 0, panel.getWidth(), panel.getHeight());
        gcForeground.clearRect(0, 0, panel.getWidth(), panel.getHeight());

        drawBackground();
        placeTokens();
    }

    /**
     * Zeichnet den Hintergrund auf <code>backgroundCanvas</code>, dazu gehören
     * die Kreissegmente und deren Beschriftung.
     */
    private void drawBackground() {

        for (int index = 0; index <= 11; index++) {
            drawArc(index);
        }

        // Aufteilen, damit alles nacheinander gezeichnet wird.
        for (int index = 0; index <= 11; index++) {
            drawArcNumber(index);
        }

        gcBackground.setFill(Color.WHITE);

        double radiusCenterCircle = arcRadius / 5;
        double x = panel.getWidth() / 2 - radiusCenterCircle / 2;
        double y = panel.getHeight() / 2 - radiusCenterCircle / 2;

        gcBackground.fillOval(x, y, radiusCenterCircle, radiusCenterCircle);
        gcBackground.strokeOval(x, y, radiusCenterCircle, radiusCenterCircle);
    }

    /**
     * Zeichnet die Spielsteine der Teilnehmer auf alle Kreissegmente.
     */
    public void placeTokens() {

        gcTokens.clearRect(0, 0, panel.getWidth(), panel.getHeight());
        coordinateMap.clear();

        for (int index = 0; index < 12; index++) {
            calcTokenLocations(index);
        }

        for (Map.Entry<Fighter, Point2D> entry : coordinateMap.entrySet()) {

            double x = entry.getValue().getX();
            double y = entry.getValue().getY();
            double size = tokenSize;
            double fontSize = tokenSize * 0.8;

            if(!observableList.isEmpty() && entry.getKey().equals(observableList.get(0))) {
                size = size * 1.4;
                fontSize = fontSize * 1.4;
            }

            // Unterscheidung nach Zugehörigkeit des Teilnehmers
            if (entry.getKey() instanceof AllyFighter) {
                gcTokens.setFill(entry.getKey().getColor());
                gcTokens.fillRoundRect(x, y, size, size, size/2, size/2);
                gcTokens.strokeRoundRect(x, y, size, size, size/2, size/2);
            } else if (entry.getKey() instanceof EnemyFighter) {
                gcTokens.setFill(entry.getKey().getColor());
                gcTokens.fillRect(x, y, size, size);
                gcTokens.strokeRect(x, y, size, size);
            } else {
                gcTokens.setFill(entry.getKey().getColor());
                gcTokens.fillOval(x, y, size, size);
                gcTokens.strokeOval(x, y, size, size);
            }

            // Beschriftung ist immer grau
            gcTokens.setFill(Color.GREY);
            gcTokens.setFont(new Font(fontSize));
            gcTokens.fillText(entry.getKey().getName(), x + size / 2, y - size / 2);
        }
    }

    private Point2D getCarthesianPoint(double radius, double angle) {
        Point2D point;
        double radians = Math.toRadians(angle);
        point = new Point2D(Math.cos(radians) * radius, - Math.sin(radians) * radius);
        return point;
    }

    private Point2D getPointInCircle(double radius, double angle) {
        double radians = Math.toRadians(angle);
        double x = Math.cos(radians) * radius + panel.getWidth() / 2;
        double y = - Math.sin(radians) * radius + panel.getHeight() / 2;
        return new Point2D(x, y);
    }

    private Point2D getCenter() {
        return new Point2D(panel.getWidth() / 2, panel.getHeight() / 2);
    }

    private Dimension2D getBorderOffsets() {
        Dimension2D dimension;
        double diff = panel.getWidth() - panel.getHeight();
        if (diff > 0) {
            dimension = new Dimension2D(circleMinInsets + Math.abs(diff) / 2, circleMinInsets);
        } else if (diff < 0){
            dimension = new Dimension2D(circleMinInsets, circleMinInsets + Math.abs(diff) / 2);
        } else {
            dimension = new Dimension2D(circleMinInsets, circleMinInsets);
        }
        return dimension;
    }

    /**
     * Aktuallisiert die Darstellung der Kreissegmente um bei einer Änderung
     * der Liste das aktuelle Feld zu betonen.
     */
    public void updateArcs() {
        if (!observableList.isEmpty()) {
            actionIndex = 12 - ((Fighter)observableList.get(0)).getIni();
            while (actionIndex < 0) {
                actionIndex = actionIndex + 12;
            }
        } else {
            actionIndex = -1;
        }
        drawBackground();
    }

    /**
     * Aktuallisiert die Größen aller Zeichenebenen und gleicht sie der Größe
     * des Panels an.
     */
    private void updateCanvasSize() {
        backgroundCanvas.setHeight(panel.getHeight());
        backgroundCanvas.setWidth(panel.getWidth());
        tokenCanvas.setHeight(panel.getHeight());
        tokenCanvas.setWidth(panel.getWidth());
        foregroundCanvas.setHeight(panel.getHeight());
        foregroundCanvas.setWidth(panel.getWidth());
    }


    /**
     * Zeichnet ein Kreissegment an einer Stelle, die dem Parameter entspricht.
     * @param index
     */
    private void drawArc(int index) {

        double angle = index * arcAngle + 90;
        Color arcColor = Color.WHITE;
        if(index == actionIndex) {
            arcColor = Color.YELLOW;
        }
        gcBackground.setFill(arcColor);
        gcBackground.fillArc(
                getBorderOffsets().getWidth(), getBorderOffsets().getHeight(),
                arcRadius, arcRadius, angle, arcAngle, ArcType.ROUND);
        gcBackground.setStroke(Color.GREY);
        gcBackground.strokeArc(
                getBorderOffsets().getWidth(), getBorderOffsets().getHeight(),
                arcRadius, arcRadius, angle, arcAngle, ArcType.ROUND);
    }

    /**
     * Zeichnet die Beschriftung der Felder, also die Zahlen 12 bis 1.
     * Die Koordinaten des Textes sind in der unteren linken Ecke.
     * @param index
     */
    private void drawArcNumber(int index) {
        double angle = index * arcAngle + 90;

        // Größe des Textes berechnen um ihn später zu zentrieren.
        double textWidth;
        double textHeight;
        Text text = new Text(Integer.toString(12 - index));
        text.setFont(new Font(numberFontSize));
        textWidth = text.getBoundsInLocal().getWidth();
        textHeight = text.getBoundsInLocal().getHeight();

        gcBackground.setFill(Color.LIGHTGREY);
        gcBackground.setFont(new Font(numberFontSize));
        gcBackground.fillText(
                Integer.toString(12 - index),
                getPointInCircle(numberRadius, angle + arcAngle / 2).getX() - textWidth / 2,
                getPointInCircle(numberRadius, angle + arcAngle / 2).getY() + textHeight / 4);
    }

    /**
     * Zeichnet die Spielsteine der Teilnehmer auf dem Feld, das dem
     * Index entspricht. Dabei wird von außen aufgefüllt und die Anzahl der
     * dargestellten Grafiken um 1 pro Reihe kleiner.
     * @param index
     */
    private void drawTokenSet(int index) {

        for (Map.Entry<Fighter, Point2D> entry : coordinateMap.entrySet()) {

            double x = entry.getValue().getX();
            double y = entry.getValue().getY();

            gcTokens.setFill(entry.getKey().getColor());
            gcTokens.fillOval(x, y, tokenSize, tokenSize);
            gcTokens.strokeOval(x, y, tokenSize, tokenSize);

            gcTokens.setFill(Color.GREY);
            gcTokens.fillText(entry.getKey().getName(), x + tokenSize, y - tokenSize);
        }
        List<Fighter> subList = fightersList.subListByIni(index + 1);


        int size = subList.size();

        if(size == 0) {
            return;
        }

        int count = size;                       // Anzahl der Tokens im Feld pro Reihe
        double angle = - index * arcAngle + 90; // Winkel ab dem gerechnet wird
        double tokenRadius = arcRadius * 0.46;
        int current = 0;

        while(size > 0) {
            if(size >= tokenLimit) {
                count = tokenLimit;
            }
            for(int ind = 0; ind < count; ind++) {

                double tokenAngle = angle - (30.0 / (count + 1)) * (ind + 1);
                double x = getPointInCircle(tokenRadius, tokenAngle).getX() - tokenSize / 2;
                double y = getPointInCircle(tokenRadius, tokenAngle).getY() - tokenSize / 2;

                gcTokens.setFill(subList.get(ind).getColor());
                gcTokens.fillOval(x, y, tokenSize, tokenSize);
                gcTokens.strokeOval(x, y, tokenSize, tokenSize);

                gcTokens.setFill(Color.GREY);
                gcTokens.fillText(subList.get(ind).getName(), x + tokenSize, y - tokenSize);

                current++;
            }
            size = size - tokenLimit;
            tokenRadius = tokenRadius - tokenSize * 2;
        }
    }

    /**
     * Zeichnet ein Rechteck, das anzeigt wieviele Teilnehmer nicht mehr auf
     * dem Feld angezeigt werden können. Wenn alle passen, wird kein Symbol
     * angezeigt.
     * @param count
     * @param angle
     */
    private void drawTokenBin(int count, double angle) {
        Text binText = new Text(Integer.toString(count));
        double width = binText.getBoundsInLocal().getWidth();
        double height = binText.getBoundsInLocal().getHeight();

        double x = getPointInCircle(arcRadius / 4, angle).getX();
        double y = getPointInCircle(arcRadius / 4, angle).getY();

        gcTokens.setFont(new Font(numberFontSize * 0.8));
        gcTokens.setFill(Color.GREY);
        gcTokens.strokeRect(x - width / 2, y - height / 2, width, height);
        gcTokens.strokeText(Integer.toString(count), x - width / 2, y + height / 2);
    }

    /**
     * Berechnet die Positionen der Tokens für das Kreissegment mit dem
     * angegebenen index und sichert diese als <code>Point2D</code> in
     * <code>coordinateMap</code>.
     * Die Koordinaten sind bereits um die Größe des Tokens verschoben.
     * @param index
     */
    private void calcTokenLocations(int index) {

        List<Fighter> subList = fightersList.subListByIni(index + 1);

        int size = subList.size();

        if(size == 0) {
            return;
        }

        int count;                              // Anzahl der Tokens im Feld pro Reihe
        double angle = - index * arcAngle + 90; // Winkel ab dem gerechnet wird
        double tokenRadius = arcRadius * 0.46;
        int current = 0;

        while(size > 0 && current < tokenLimit * 2 - 1) {   // Die äußeren zwei Reihen werden angezeigt
            if(size < tokenLimit) {
                count = size;
            } else {
                count = tokenLimit;
            }
            for(int ind = 0; ind < count; ind++) {

                double tokenAngle = angle - (arcAngle / (count + 1)) * (ind + 1);
                double x = getPointInCircle(tokenRadius, tokenAngle).getX() - tokenSize / 2;
                double y = getPointInCircle(tokenRadius, tokenAngle).getY() - tokenSize / 2;

                coordinateMap.put(subList.get(current), new Point2D(x, y));

                current++;
            }
            size = size - tokenLimit;
            tokenRadius = tokenRadius - tokenSize * 2;
        }

        if(size > 0) {
            drawTokenBin(size, angle - arcAngle / 2);
        }
    }

    /** Berechnet die maximale Anzahl an Tokens am Rand eines Kreissegmentes.
     * Je kleiner das Kreissegment, desto weniger Tokens finden dort Platz.
     * Diese Berechnung ist unabhängig von der Größe, weil diese in Relation
     * zur Größe des Kreises berechnet wird.
     * @return
     */
    private int calcTokenLimit() {

        int limit = 1;
        while (arcAngle / limit > 6) {
            limit++;
        }
        return limit;
    }

    public void setRelations() {
        observableList = controller.getObservableList();
        fightersList = controller.fightersListProperty();
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setFightersList(FightersList fightersList) {
        this.fightersList = fightersList;
    }

    public void setObservableList(ObservableList observableList) {
        this.observableList = observableList;
    }
}
