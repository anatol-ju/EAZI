package mvc;

import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.*;

public class CircleController implements ChangeListener, ListChangeListener {

    private Model model;
    private Controller controller;

    private GraphicsContext gcBackground;
    private GraphicsContext gcTokens;
    private GraphicsContext gcForeground;

    private ObservableList<Fighter> observableList;
    private HashMap<Fighter, Point2D> coordinateMap;

    private double arcRadius = 150;
    private double arcAngle;
    private double tokenSize = 10;
    private int tokenLimit = 4;
    private double numberRadius = 50;
    private int numberFontSize = 20;

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

    private Properties settings;
    private int fields;
    private static final Color BG_FILL_COLOR = Color.WHITE;
    private static final Color TOKEN_LABEL_COLOR = Color.GREY;
    private static final Color ARC_COLOR = Color.WHITE;
    private static final Color ARC_HIGHLIGHT_COLOR = Color.YELLOW;
    private static final Color BG_STROKE_COLOR = Color.GREY;
    private static final Color ARC_LABEL_COLOR = Color.LIGHTGREY;
    private static final Color BIN_FILL_COLOR = Color.GREY;

    @FXML
    private void initialize() {

        observableList = FightersList.sortedList;

        gcBackground = backgroundCanvas.getGraphicsContext2D();
        gcTokens = tokenCanvas.getGraphicsContext2D();
        gcForeground = foregroundCanvas.getGraphicsContext2D();

        windowSize = panel.widthProperty().add(panel.heightProperty());
        windowSize.addListener(this);

        coordinateMap = new HashMap<>();

        // use properties
        settings = Serializer.readConfigFile();
        fields = Integer.parseInt(settings.getProperty("actionCircleFieldCount"));
        arcAngle = 360.0/fields;
        tokenLimit = calcTokenLimit();

        updateCanvasSize();
    }

    @Override
    public void changed(ObservableValue observable, Object oldValue, Object newValue) {

        updateCanvasSize();

        arcRadius = Math.min(panel.getHeight(), panel.getWidth()) -
                2 * Math.min(getBorderOffsets().getWidth(), getBorderOffsets().getHeight());

        numberFontSize = (int) (panel.getHeight() / 20);
        numberRadius = arcRadius / 7;                           // ratio based on visuals

        tokenSize = Math.toRadians(arcAngle / 14) * arcRadius;  // ratio based on visuals

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
     * Draws the complete canvas or redraw it.
     */
    private void draw() {
        gcBackground.clearRect(0, 0, panel.getWidth(), panel.getHeight());
        gcForeground.clearRect(0, 0, panel.getWidth(), panel.getHeight());

        drawBackground();
        placeTokens();
    }

    /**
     * Draws the background on <code>backgroundCanvas</code>,
     * including arcs and their respective descriptions.
     */
    private void drawBackground() {

        for (int index = 0; index < fields; index++) {
            drawArc(index);
        }

        // split drawing arcs and numbers
        for (int index = 0; index < fields; index++) {
            drawArcNumber(index);
        }

        gcBackground.setFill(BG_FILL_COLOR);

        double radiusCenterCircle = arcRadius / 5;
        double x = panel.getWidth() / 2 - radiusCenterCircle / 2;
        double y = panel.getHeight() / 2 - radiusCenterCircle / 2;

        gcBackground.fillOval(x, y, radiusCenterCircle, radiusCenterCircle);
        gcBackground.strokeOval(x, y, radiusCenterCircle, radiusCenterCircle);
    }

    /**
     * Draws tokens for the participants based on the stored coordinates.
     */
    public void placeTokens() {

        gcTokens.clearRect(0, 0, panel.getWidth(), panel.getHeight());
        coordinateMap.clear();

        for (int index = 0; index < fields; index++) {
            calcTokenLocations(index);
        }

        Color color;

        for (Map.Entry<Fighter, Point2D> entry : coordinateMap.entrySet()) {

            double x = entry.getValue().getX();
            double y = entry.getValue().getY();
            double size = tokenSize;
            double fontSize = tokenSize * 0.8;

            if(!observableList.isEmpty() && entry.getKey().equals(observableList.get(0))) {
                size = size * 1.4;
                fontSize = fontSize * 1.4;
            }

            // differentiation according to participants affiliation
            if (entry.getKey() instanceof AllyFighter) {
                color = Serializer.string2color(settings.getProperty("colorAlly"));
                gcTokens.setFill(color);
                gcTokens.fillRoundRect(x, y, size, size, size/2, size/2);
                gcTokens.strokeRoundRect(x, y, size, size, size/2, size/2);
            } else if (entry.getKey() instanceof EnemyFighter) {
                color = Serializer.string2color(settings.getProperty("colorEnemy"));
                gcTokens.setFill(color);
                gcTokens.fillRect(x, y, size, size);
                gcTokens.strokeRect(x, y, size, size);
            } else {
                color = Serializer.string2color(settings.getProperty("colorFighter"));
                gcTokens.setFill(color);
                gcTokens.fillOval(x, y, size, size);
                gcTokens.strokeOval(x, y, size, size);
            }

            gcTokens.setFill(TOKEN_LABEL_COLOR);
            gcTokens.setFont(new Font(fontSize));
            gcTokens.fillText(entry.getKey().getName(), x + size / 2, y - size / 2);
        }
    }

    /**
     * Converts polar coordinates into cartesian.
     * @param radius The given radial coordinate.
     * @param angle The given angle coordinate.
     * @return A Point2D object containing the cartesian coordinates.
     */
    private Point2D polar2cartesian(double radius, double angle) {
        double radians = Math.toRadians(angle);
        double x = Math.cos(radians) * radius + panel.getWidth() / 2;
        double y = - Math.sin(radians) * radius + panel.getHeight() / 2;
        return new Point2D(x, y);
    }

    /**
     * Calculates offsets for the circle, so it is not drawn starting from the edge of the canvas.
     * @return Dimension2D object, where width and height represent the offsets of x and y axes.
     */
    private Dimension2D getBorderOffsets() {
        Dimension2D dimension;
        double diff = panel.getWidth() - panel.getHeight();
        int circleMinInsets = 20;
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
     * Updates the drawn arcs on the circle with respect to the first fighter in the list.
     * This fighter's location (or INI) is then updated and is highlighted on redraw.
     */
    public void updateArcs() {
        int fields = Integer.parseInt(settings.getProperty("actionCircleFieldCount"));
        if (!observableList.isEmpty()) {
            actionIndex = fields - (observableList.get(0)).getIni();
            while (actionIndex < 0) {
                actionIndex = actionIndex + fields;
            }
        } else {
            actionIndex = -1;
        }
        drawBackground();
    }

    /**
     * Updates the canvas size on change of size or redraw.
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
     * Draws an arc of the circle according to its location, given by the index.
     * @param index The index of the arc.
     */
    private void drawArc(int index) {

        double angle = index * arcAngle + 90;
        Color arcColor = ARC_COLOR;
        if(index == actionIndex) {
            arcColor = ARC_HIGHLIGHT_COLOR;
        }
        gcBackground.setFill(arcColor);
        gcBackground.fillArc(
                getBorderOffsets().getWidth(), getBorderOffsets().getHeight(),
                arcRadius, arcRadius, angle, arcAngle, ArcType.ROUND);
        gcBackground.setStroke(BG_STROKE_COLOR);
        gcBackground.strokeArc(
                getBorderOffsets().getWidth(), getBorderOffsets().getHeight(),
                arcRadius, arcRadius, angle, arcAngle, ArcType.ROUND);
    }

    /**
     * Draws the labels of the arcs.
     * The coordinates are in the bottom left corner.
     * @param index The index of the arc.
     */
    private void drawArcNumber(int index) {
        double angle = index * arcAngle + 90;
        int fields = Integer.parseInt(settings.getProperty("actionCircleFieldCount"));

        // Calculated text size to be able to center it.
        double textWidth;
        double textHeight;
        Text text = new Text(Integer.toString(fields - index));
        text.setFont(new Font(numberFontSize));
        textWidth = text.getBoundsInLocal().getWidth();
        textHeight = text.getBoundsInLocal().getHeight();

        gcBackground.setFill(ARC_LABEL_COLOR);
        gcBackground.setFont(new Font(numberFontSize));
        gcBackground.fillText(
                Integer.toString(fields - index),
                polar2cartesian(numberRadius, angle + arcAngle / 2).getX() - textWidth / 2,
                polar2cartesian(numberRadius, angle + arcAngle / 2).getY() + textHeight / 4);
    }

    /**
     * Draws a square containing a number representing how many tokens can not
     * be drawn on the arc due to size limitations. This 'bin' will not be
     * shown if empty, that means all tokens can be placed.
     * @param count The number of tokens to hide in the bin, used as label.
     * @param angle The location of the bin given by the angle.
     */
    private void drawTokenBin(int count, double angle) {
        // do not show if bin is empty
        if (count == 0) {
            return;
        }
        Text binText = new Text(Integer.toString(count));
        double width = binText.getBoundsInLocal().getWidth();
        double height = binText.getBoundsInLocal().getHeight();
        double size = Math.max(width, height);

        double x = polar2cartesian(arcRadius / 4, angle).getX();
        double y = polar2cartesian(arcRadius / 4, angle).getY();

        gcTokens.setFont(new Font(numberFontSize * 0.8));
        gcTokens.setFill(BIN_FILL_COLOR);
        gcTokens.strokeRect(x - size / 2, y - size / 2, size, size);
        gcTokens.strokeText(Integer.toString(count), x - width / 2, y + height / 3);
    }

    /**
     * Calculates the coordinates for the tokens and saves them as a
     * <code>Point2D</code> object in <code>coordinateMap</code>.
     * The saved coordinates are translated by their respective sizes.
     * If more tokens need to be drawn than space available, remaining tokens
     * are placed in a 'bin' using the <code>drawTokenBin()</code> method.
     * @param arcIndex The index of the arc.
     */
    private void calcTokenLocations(int arcIndex) {

        List<Fighter> subList = fightersList.subListByIni(arcIndex + 1);

        int subListSize = subList.size();

        if(subListSize == 0) {
            return;
        }

        int count;  // number of tokens in an arc per row
        double angle = - arcIndex * arcAngle + 90; // starting angle
        double tokenRadius = arcRadius * 0.46;
        int current = 0;

        while(subListSize > 0 && current < tokenLimit * 2 - 1) {   // draw only first 2 outer rows
            count = Math.min(subListSize, tokenLimit);
            for(int ind = 0; ind < count; ind++) {

                double tokenAngle = angle - (arcAngle / (count + 1)) * (ind + 1);
                double x = polar2cartesian(tokenRadius, tokenAngle).getX() - tokenSize / 2;
                double y = polar2cartesian(tokenRadius, tokenAngle).getY() - tokenSize / 2;

                coordinateMap.put(subList.get(current), new Point2D(x, y));

                current++;
            }
            subListSize = subListSize - tokenLimit;
            tokenRadius = tokenRadius - tokenSize * 2;
        }

        if(subListSize > 0) {
            drawTokenBin(subListSize, angle - arcAngle / 2);
        }
    }

    /**
     * Calculates the maximum amount of tokens that can be drawn on an arc.
     * Smaller arcs can hold less tokens.
     * The calculation is based on the arc's size and therefore independent
     * of its location
     * @return An int representing the maximum number of tokens in the outer
     * row of the arc.
     */
    private int calcTokenLimit() {

        int limit = 1;
        while (arcAngle / limit > 6) {
            limit++;
        }
        return limit;
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

    public void setObservableList(ObservableList<Fighter> observableList) {
        this.observableList = observableList;
    }

    public void setSettings(Properties settings) {
        this.settings = settings;
    }

    public WritableImage getImage() {
        WritableImage img = new WritableImage((int)panel.getWidth(), (int)panel.getHeight());
        panel.snapshot(null, img);
        return img;
    }
}
