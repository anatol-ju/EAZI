package mvc;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.util.converter.NumberStringConverter;

import java.util.*;

public class ActionsController {

    private Model model;
    private Controller controller;

    private List<Control> actionControls;
    private List<Control> reactionControls;

    private final SimpleIntegerProperty selectedIndexProperty = new SimpleIntegerProperty();
    private FightersList fightersList;
    private Fighter selectedFighter;

    private int unarmedMod;
    private double fontSize;
    private ResourceBundle rb;
    private Properties config;
    private String style;

    private int freeActionDuration;
    private int simpleAction;
    private int simpleActionSurcharge;
    private int simpleReaction;
    private int simpleReactionSurcharge;

    @FXML
    private TitledPane actionTitledPane;
    @FXML
    private GridPane actionGridPane;
    @FXML
    private Button attack;
    @FXML
    private Button attack2;
    @FXML
    private Button parry;
    @FXML
    private Button parry2;
    @FXML
    private Button wait;
    @FXML
    private Button dodge;
    @FXML
    private Button move;
    @FXML
    private Button sprint;
    @FXML
    private Button position;
    @FXML
    private Button orientate;
    @FXML
    private Button drawWeapon;
    @FXML
    private Button loadBow;
    @FXML
    private Label actionPoints;
    @FXML
    private Label specialActionLabel;
    @FXML
    private Label modLabel;
    @FXML
    private ToggleButton useMagic;
    @FXML
    private ToggleButton aim;
    @FXML
    private ToggleButton longAction;
    @FXML
    private ToggleButton otherAction;
    @FXML
    private Button choice;
    @FXML
    private ToggleButton unarmed;
    @FXML
    private ComboBox<String> mod;
    @FXML
    private TextField value;
    @FXML
    private Button freeAction;

    @FXML
    private void initialize() {

        // set config file and frequently used data
        config = Configuration.get();
        loadDurationValues();

        // define locales
        rb = ResourceBundle.getBundle("locales.ActionController", Locale.getDefault());

        actionTitledPane.setText(rb.getString("title"));

        specialActionLabel.setText(rb.getString("specialActionLabel"));
        modLabel.setText(rb.getString("globalModLabel"));

        // make tooltips
        setTooltips();

        // create groups
        ToggleGroup toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().addAll(useMagic, aim, longAction, otherAction);

        actionControls = new ArrayList<>();
        reactionControls = new ArrayList<>();
        setActionsGroup();
        setReactionsGroup();

        setDisableGroup(actionControls, true);
        setDisableGroup(reactionControls, true);

        // create selections for combo elements
        ObservableList<String> modList = FXCollections.observableList(Arrays.asList(makeComboBoxList()));
        mod.setItems(modList);
        mod.getSelectionModel().select((int)Math.floor(modList.size()/2.0));
    }

    private void selectedIndexListener(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        if(newValue.intValue() == 0) {
            selectedFighter = fightersList.getSortedList().get(0);
            setDisableGroup(actionControls, false);
            setDisableGroup(reactionControls, false);
        } else if(newValue.intValue() == -1){
            selectedFighter = null;
            setDisableGroup(actionControls, true);
            setDisableGroup(reactionControls, true);
        } else {
            selectedFighter = fightersList.getSortedList().get(newValue.intValue());
            setDisableGroup(actionControls, true);
            setDisableGroup(reactionControls, false);
        }
    }

    /**
     * Use settings file to define duration values for actions.
     */
    private void loadDurationValues() {
        try {
            freeActionDuration = Integer.parseInt(config.getProperty("freeActions"));
            simpleAction = Integer.parseInt(config.getProperty("simpleActions"));
            simpleActionSurcharge = Integer.parseInt(config.getProperty("simpleActionsSurcharge"));
            simpleReaction = Integer.parseInt(config.getProperty("simpleReactions"));
            simpleReactionSurcharge = Integer.parseInt(config.getProperty("simpleReactionsSurcharge"));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set all tooltips for all controls.
     * The actual text is found in <code>locales.ActionTooltips</code>.
     */
    private void setTooltips() {

        ResourceBundle rbt = ResourceBundle.getBundle("locales.ActionTooltips", Locale.getDefault());
        attack.setTooltip(new Tooltip(rbt.getString("attack")));
        attack2.setTooltip(new Tooltip(rbt.getString("attack2")));
        parry.setTooltip(new Tooltip(rbt.getString("parry")));
        parry2.setTooltip(new Tooltip(rbt.getString("parry2")));
        wait.setTooltip(new Tooltip(rbt.getString("wait")));
        dodge.setTooltip(new Tooltip(rbt.getString("dodge")));
        move.setTooltip(new Tooltip(rbt.getString("move")));
        sprint.setTooltip(new Tooltip(rbt.getString("sprint")));
        position.setTooltip(new Tooltip(rbt.getString("position")));
        orientate.setTooltip(new Tooltip(rbt.getString("orientate")));
        drawWeapon.setTooltip(new Tooltip(rbt.getString("drawWeapon")));
        loadBow.setTooltip(new Tooltip(rbt.getString("loadBow")));
        useMagic.setTooltip(new Tooltip(rbt.getString("useMagic")));
        aim.setTooltip(new Tooltip(rbt.getString("aim")));
        longAction.setTooltip(new Tooltip(rbt.getString("longAction")));
        otherAction.setTooltip(new Tooltip(rbt.getString("otherAction")));
        choice.setTooltip(new Tooltip(rbt.getString("choice")));
        unarmed.setTooltip(new Tooltip(rbt.getString("unarmed")));
        mod.setTooltip(new Tooltip(rbt.getString("mod")));
        value.setTooltip(new Tooltip(rbt.getString("value")));
        freeAction.setTooltip(new Tooltip(rbt.getString("freeAction")));

    }

    /**
     * Defines controls, that are only available for the currently
     * acting participant. The members of this group will be set to
     * inactive for any selected participant, that is not the first
     * in the list of fighters.
     */
    private void setActionsGroup() {
        actionControls.add(attack);
        actionControls.add(attack2);
        actionControls.add(wait);
        actionControls.add(move);
        actionControls.add(sprint);
        actionControls.add(position);
        actionControls.add(orientate);
        actionControls.add(drawWeapon);
        actionControls.add(loadBow);
        actionControls.add(useMagic);
        actionControls.add(aim);
        actionControls.add(unarmed);
        actionControls.add(choice);
        actionControls.add(longAction);
        actionControls.add(mod);
        actionControls.add(freeAction);
    }

    /**
     * Defines a group of controls that are available, if the selected
     * participant is not the first in the list. Members of this group
     * can only use reactive actions.
     */
    private void setReactionsGroup() {
        reactionControls.add(parry);
        reactionControls.add(parry2);
        reactionControls.add(dodge);
        reactionControls.add(otherAction);
        reactionControls.add(unarmed);
        reactionControls.add(choice);
        reactionControls.add(value);
        reactionControls.add(freeAction);
        reactionControls.add(actionPoints);
        reactionControls.add(specialActionLabel);
        reactionControls.add(modLabel);
        reactionControls.add(value);
        reactionControls.add(mod);
    }

    /**
     * Sets the disabled or enabled state for a group of controls.
     * @param group A {@link List} of controls with the state to be changed.
     * @param isDisabled Set to <code>true</code> if the controls in the
     *                   group needs to be disabled ot <code>false</code> if
     *                   they need to be enabled.
     */
    private void setDisableGroup(List<Control> group, boolean isDisabled) {
        for (Control control : group) {
            control.setDisable(isDisabled);
        }
    }

    // Action Events

    /**
     * This method is responsible for the action itself.
     * It calls the responsible controllers and forwards information if needed.
     * @param range The value to substract from the INI, aka Action Points.
     * @param actionPerformed A string representation of the action.
     */
    private void action(int range, String actionPerformed) {

        Fighter fighter = selectedFighter;

        if (fighter != null && range >= 0) {
            if (range < Integer.parseInt(config.getProperty("actionCircleFieldCount"))) {
                fightersList.action(fighter, range);
                controller.getLogController().action(fighter, actionPerformed);
                controller.getMenuController().action();
                controller.getListController().getListView().refresh();
            } else {
                new InfoDialog(rb.getString("errorRange")).showAndWait();
            }
        }
        if(range < 0) {
            ResourceBundle rbd = ResourceBundle.getBundle("locales.OtherDialog", Locale.getDefault());
            new InfoDialog(rbd.getString("infoValuesGreaterZero")).showAndWait();
        }
    }

    /**
     * Create entries for the ComboBox control based on the number of fields
     * in the action circle. The entries are in range of <code>[-MAX/4, MAX/4]</code>
     * with steps of <code>1</code>.
     * @return A {@link String} array containing elements of the {@link ComboBox}.
     */
    private String[] makeComboBoxList() {
        // one quarter of the max INI for +mod and -mod, also count 0
        // gives MAX / 4 * 2 + 1 = list size
        int maxIniQuarter = Integer.parseInt(config.getProperty("actionCircleFieldCount")) / 4;

        // fill string array with values [- max ... 0 ... max]
        String[] list = new String[maxIniQuarter * 2 + 1];
        for (int index = 0; index < list.length; index++) {
            list[index] = String.valueOf(index - maxIniQuarter);
        }
        return list;
    }

    public void attackAction() {
        int range = simpleAction + (selectedFighter).getModAT() +
                mod.getSelectionModel().getSelectedIndex() - 3 +
                unarmedMod;
        action(range, "attack");
    }

    public void attack2Action() {
        int range = simpleAction + simpleActionSurcharge + (selectedFighter).getModAT() +
                mod.getSelectionModel().getSelectedIndex() - 3 +
                unarmedMod;
        action(range, "attack2");
    }

    public void parryAction() {
        action(simpleReaction, "parry");
    }

    public void parry2Action() {
        action(simpleReaction + simpleReactionSurcharge, "parry2");
    }

    public void waitAction() {
        action(simpleActionSurcharge, "wait");
    }

    public void dodgeAction() {
        action(simpleReaction + simpleReactionSurcharge, "dodge");
    }

    public void moveAction() {
        action(simpleActionSurcharge, "move");
    }

    public void sprintAction() {
        action(simpleAction, "sprint");
    }

    public void positionAction() {
        int range = simpleAction + (selectedFighter).getModPosition() +
                mod.getSelectionModel().getSelectedIndex() - 3;
        action(range, "position");
    }

    public void orientateAction() {
        int range = simpleAction + simpleActionSurcharge + (selectedFighter).getModPosition() +
                mod.getSelectionModel().getSelectedIndex() - 3;
        action(range, "orientate");
    }

    public void drawWeaponAction() {
        int range = simpleAction + (selectedFighter).getModDrawWeapon() +
                mod.getSelectionModel().getSelectedIndex() - 3;
        action(range, "drawWeapon");
    }
    public void loadBowAction() {
        int range = simpleAction + (selectedFighter).getModLoadBow() +
                mod.getSelectionModel().getSelectedIndex() - 3;
        action(range, "loadBow");
    }
    public void useMagicAction() {
        value.setText("0");
        actionPoints.setText(rb.getString("actions"));
    }

    public void aimAction() {
        value.setText(String.valueOf(Integer.parseInt(config.getProperty("actionCircleFieldCount"))/3*2));
        actionPoints.setText(rb.getString("actionPoints"));
    }

    public void longActionAction() {
        value.setText("2");
        actionPoints.setText(rb.getString("actions"));
    }

    public void otherActionAction() {
        value.setText("2");
        actionPoints.setText(rb.getString("actionPoints"));
    }

    private int validate() {

        int base;
        Number input;
        boolean wrongInput = false;
        float converted = -1;

        try {
            input = new NumberStringConverter().fromString(value.getText());
            converted = input.floatValue();
        } catch (Exception e) {
            wrongInput = true;
        }
        base = Math.round(converted);
        System.out.println(base);

        if(wrongInput || base < 0) {
            ResourceBundle rbd = ResourceBundle.getBundle("locales.OtherDialog", Locale.getDefault());
            new InfoDialog(rbd.getString("infoInputFormat")).showAndWait();
        }

        return base;
    }

    public void choiceAction() {

        int base = validate();
        if(base == -1) {
            return;
        }
        String actionPerformed = "";

        if(useMagic.isSelected()) {
            base = base * simpleAction;
            actionPerformed = "useMagic";
        } else if(aim.isSelected()) {
            actionPerformed = "aim";
        } else if(longAction.isSelected()) {
            base = base * simpleAction;
            actionPerformed = "longAction";
        } else if(otherAction.isSelected()) {
            actionPerformed = "otherAction";
        }
        action(base, actionPerformed);

        // unselect togglebuttons
        aim.setSelected(false);
        useMagic.setSelected(false);
        longAction.setSelected(false);
        otherAction.setSelected(false);
    }

    public void unarmedAction() {
        int mod;
        if(unarmed.isSelected()) {
            mod = -simpleAction/3;
        } else {
            mod = 0;
        }
        unarmedMod = mod;
    }

    public void freeActionAction() {
        action(freeActionDuration, "freeAction");
    }

    public void incrAction() {
        int current = validate();
        value.setText(Integer.toString(current + 1));
    }

    public void decrAction() {
        int current = validate();
        if(current > 0) {
            value.setText(Integer.toString(current - 1));
        }
    }

    public void incrModAction() {
        int current = mod.getSelectionModel().getSelectedIndex();
        if (current < mod.getItems().size() - 1) {
            mod.getSelectionModel().select(current + 1);
        }
    }

    public void decrModAction() {
        int current = mod.getSelectionModel().getSelectedIndex();
        if (current > 0) {
            mod.getSelectionModel().select(current - 1);
        }
    }

    /**
     * Defines the actions performed when using key combinations.
     * @param event The {@link KeyEvent} to be processed.
     */
    private void processKeyEvents(KeyEvent event) {
        if (event.isControlDown() && !event.isShiftDown()) {
            if (event.getCode().equals(KeyCode.A)) {
                attack.requestFocus();
            } else if (event.getCode().equals(KeyCode.P)) {
                parry.requestFocus();
            } else if (event.getCode().equals(KeyCode.W)) {
                wait.requestFocus();
            } else if (event.getCode().equals(KeyCode.U)) {
                dodge.requestFocus();
            } else if (event.getCode().equals(KeyCode.B)) {
                move.requestFocus();
            } else if (event.getCode().equals(KeyCode.S)) {
                position.requestFocus();
            } else if (event.getCode().equals(KeyCode.O)) {
                orientate.requestFocus();
            } else if (event.getCode().equals(KeyCode.I)) {
                drawWeapon.requestFocus();
            } else if (event.getCode().equals(KeyCode.L)) {
                loadBow.requestFocus();
            } else if (event.getCode().equals(KeyCode.Z)) {
                useMagic.requestFocus();
            } else if (event.getCode().equals(KeyCode.M)) {
                mod.requestFocus();
            } else if (event.getCode().equals(KeyCode.T)) {
                value.requestFocus();
            } else if (event.getCode().equals(KeyCode.F)) {
                freeAction.requestFocus();
            } else if (event.getCode().equals(KeyCode.UP)) {
                incrModAction();
            } else if (event.getCode().equals(KeyCode.DOWN)) {
                decrModAction();
            }
        } else if (event.isControlDown()) {
            if (event.getCode().equals(KeyCode.A)) {
                attack2.requestFocus();
            } else if (event.getCode().equals(KeyCode.P)) {
                parry2.requestFocus();
            } else if (event.getCode().equals(KeyCode.U)) {
                unarmed.requestFocus();
            } else if (event.getCode().equals(KeyCode.B)) {
                sprint.requestFocus();
            } else if (event.getCode().equals(KeyCode.S)) {
                otherAction.requestFocus();
            } else if (event.getCode().equals(KeyCode.L)) {
                longAction.requestFocus();
            } else if (event.getCode().equals(KeyCode.UP)) {
                incrAction();
            } else if (event.getCode().equals(KeyCode.DOWN)) {
                decrAction();
            }
        }
    }

    /**
     * Used to resize font of controls.
     */
    private void resizeControlsText() {

        for (Control control : actionControls) {
            if (control instanceof Button) {
                Button control1 = (Button) control;
                control1.setFont(Font.font(fontSize));
            } else if (control instanceof ToggleButton) {
                ToggleButton control1 = (ToggleButton) control;
                control1.setFont(Font.font(fontSize));
            } else if (control instanceof TextField) {
                TextField control1 = (TextField) control;
                control1.setFont(Font.font(fontSize));
            } else if (control instanceof Label) {
                Label control1 = (Label) control;
                control1.setFont(Font.font(fontSize));
            }
        }
        for (Control control : reactionControls) {
            if (control instanceof Button) {
                Button control1 = (Button) control;
                control1.setFont(Font.font(fontSize));
            } else if (control instanceof ToggleButton) {
                ToggleButton control1 = (ToggleButton) control;
                control1.setFont(Font.font(fontSize));
            } else if (control instanceof TextField) {
                TextField control1 = (TextField) control;
                control1.setFont(Font.font(fontSize));
            } else if (control instanceof Label) {
                Label control1 = (Label) control;
                control1.setFont(Font.font(fontSize));
            }
        }
    }

    // getter und setter

    public void setModel(Model model) {
        this.model = model;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setFightersList(FightersList fightersList) {
        this.fightersList = fightersList;
    }

    public void setKeyEventHandlers(Scene scene) {
        scene.addEventHandler(KeyEvent.KEY_PRESSED, this::processKeyEvents);
    }

    public Button getAttack() {
        return attack;
    }

    public Button getAttack2() {
        return attack2;
    }

    public Button getParry() {
        return parry;
    }

    public Button getParry2() {
        return parry2;
    }

    public Button getWait() {
        return wait;
    }

    public Button getDodge() {
        return dodge;
    }

    public Button getMove() {
        return move;
    }

    public Button getSprint() {
        return sprint;
    }

    public Button getPosition() {
        return position;
    }

    public Button getOrientate() {
        return orientate;
    }

    public Button getDrawWeapon() {
        return drawWeapon;
    }

    public Button getLoadBow() {
        return loadBow;
    }

    public ToggleButton getUseMagic() {
        return useMagic;
    }

    public ToggleButton getAim() {
        return aim;
    }

    public ToggleButton getLongAction() {
        return longAction;
    }

    public ToggleButton getOtherAction() {
        return otherAction;
    }

    public Button getFreeAction() {
        return freeAction;
    }

    public void bindSelectedIndexProperty(ObservableValue<? extends Number> property) {
        this.selectedIndexProperty.bind(property);
        this.selectedIndexProperty.addListener(this::selectedIndexListener);
    }
}
