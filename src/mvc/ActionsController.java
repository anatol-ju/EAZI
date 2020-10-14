package mvc;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.converter.NumberStringConverter;

import java.util.*;

public class ActionsController implements ChangeListener {

    private Model model;
    private Controller controller;

    private List<Control> actionControls;
    private List<Control> reactionControls;

    private ReadOnlyIntegerProperty selectedIndex;
    private SelectionModel selectionModel;
    private FightersList fightersList;

    private int actionMod;
    private ResourceBundle rb;

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
    private RadioButton useMagic;
    @FXML
    private RadioButton aim;
    @FXML
    private RadioButton longAction;
    @FXML
    private RadioButton otherAction;
    @FXML
    private Button choice;
    @FXML
    private ToggleButton unarmed;
    @FXML
    private ComboBox mod;
    @FXML
    private TextField value;
    @FXML
    private Button incr;
    @FXML
    private Button decr;
    @FXML
    private Button freeAction;
    @FXML
    private ToggleButton more;
    @FXML
    private ToggleButton less;

    @FXML
    private void initialize() {

        ToggleGroup toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().addAll(useMagic, aim, longAction, otherAction);

        rb = ResourceBundle.getBundle("locales.Main", Locale.getDefault());
        String ap = rb.getString("actionPoints");

        ToggleGroup extraRange = new ToggleGroup();
        extraRange.getToggles().addAll(more, less);

        ObservableList<String> modList = FXCollections.observableList(
                Arrays.asList("-3 "+ap, "-2 "+ap, "-1 "+ap, "0 "+ap, "+1 "+ap, "+2 "+ap, "+3 "+ap));
        mod.setItems(modList);
        mod.getSelectionModel().select(3);

        setTooltips();

        actionControls = new ArrayList<>();
        reactionControls = new ArrayList<>();
        setActionsGroup();
        setReactionsGroup();

        setDisableGroup(actionControls, true);
        setDisableGroup(reactionControls, true);

        //selectedIndex.addListener(this);
    }

    @Override
    public void changed(ObservableValue observable, Object oldValue, Object newValue) {

        if(observable.equals(selectedIndex)) {
            int index = (int) newValue;
            changedIndex(index);
        }
    }

    private void changedIndex(int index) {

        if(index == 0) {
            setDisableGroup(actionControls, false);
            setDisableGroup(reactionControls, false);
        } else if(index == -1){
            setDisableGroup(actionControls, true);
            setDisableGroup(reactionControls, true);
        } else {
            setDisableGroup(actionControls, true);
            setDisableGroup(reactionControls, false);
        }
    }

    /**
     * Ermöglicht Kommunikation zwischen diesem Controller und anderen
     * Komponenten, die nicht in <code>initialize()</code> verfügbar sind.
     */
    public void setRelations() {
        selectedIndex = this.controller.getListController().getSelectionModel().selectedIndexProperty();
        //this.selectionModel.selectedIndexProperty();
        selectedIndex.addListener(this);
        fightersList = controller.fightersListProperty();
    }

    /**
     * Set all tooltips for all controls.
     */
    private void setTooltips() {    // TODO set locales

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
        incr.setTooltip(new Tooltip(rbt.getString("incr")));
        decr.setTooltip(new Tooltip(rbt.getString("decr")));
        freeAction.setTooltip(new Tooltip(rbt.getString("freeAction")));
        more.setTooltip(new Tooltip(rbt.getString("more")));
        less.setTooltip(new Tooltip(rbt.getString("less")));

    }

    /**
     * Definiert Controls, die nur für den agierenden Teilnehmer
     * verfügbar sind.
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
        actionControls.add(more);
        actionControls.add(less);
        actionControls.add(choice);
        actionControls.add(value);
        actionControls.add(longAction);
        actionControls.add(incr);
        actionControls.add(decr);
        actionControls.add(mod);
        actionControls.add(freeAction);
    }

    /**
     * Definiert Controls, die auch für nicht-agierende
     * Teilnehmer verfügbar sind.
     */
    private void setReactionsGroup() {
        reactionControls.add(parry);
        reactionControls.add(parry2);
        reactionControls.add(dodge);
        reactionControls.add(otherAction);
        reactionControls.add(unarmed);
        reactionControls.add(more);
        reactionControls.add(less);
        reactionControls.add(choice);
        reactionControls.add(value);
        reactionControls.add(incr);
        reactionControls.add(decr);
        reactionControls.add(freeAction);
    }

    private void setDisableGroup(List<Control> group, boolean isDisabled) {
        for (Control control : group) {
            control.setDisable(isDisabled);
        }
    }

    // Action Events

    /**
     * Führt die eigentliche Aktion durch. Dazu wird die Funktion der Liste
     * mit den Parametern aufgerufen, die für die einzelnen Aktionen festgelegt
     * wurden. Zusätzlich wird der LogController über die Handlung informiert.
     * @param range
     * @param actionPerformed
     */
    private void action(int range, String actionPerformed) {

        Fighter fighter = (Fighter) selectionModel.getSelectedItem();

        if (fighter != null && range >= 0) {
            fightersList.action(fighter, range);
            controller.getLogController().action(fighter, actionPerformed);
            controller.getMenuController().action();
            controller.getListController().getListView().refresh();
        }
        if(range < 0) {
            ResourceBundle rbd = ResourceBundle.getBundle("locales.OtherDialog", Locale.getDefault());
            new InfoDialog(rbd.getString("infoValuesGreaterZero")).showAndWait();
        }
    }

    private int actionMod(int modMore, int modLess) {
        int range = 0;
        if(more.isSelected()) {
            range = range + modMore;
            more.setSelected(false);
        } else if(less.isSelected()) {
            range = range - modLess;
            less.setSelected(false);
        }
        return range;
    }

    private int reactionMod() {
        if(more.isSelected()) {
            more.setSelected(false);
            return 3;
        } else if(less.isSelected()) {
            less.setSelected(false);
            return -3;
        }
        return 0;
    }

    public void attackAction() {
        int range = 6 + ((Fighter)selectionModel.getSelectedItem()).getModAT() +
                mod.getSelectionModel().getSelectedIndex() - 3 +
                actionMod;
        action(range, "attack");
    }

    public void attack2Action() {
        int range = 9 + ((Fighter)selectionModel.getSelectedItem()).getModAT() +
                mod.getSelectionModel().getSelectedIndex() - 3 +
                actionMod;
        action(range, "attack2");
    }

    public void parryAction() {
        action(0, "parry");
    }

    public void parry2Action() {
        action(3, "parry2");
    }

    public void waitAction() {
        action(3, "wait");
    }

    public void dodgeAction() {
        action(3, "dodge");
    }

    public void moveAction() {
        action(3, "move");
    }

    public void sprintAction() {
        action(6, "sprint");
    }

    public void positionAction() {
        int range = 6 + ((Fighter)selectionModel.getSelectedItem()).getModPosition() +
                mod.getSelectionModel().getSelectedIndex() - 3;
        action(range, "position");
    }

    public void orientateAction() {
        int range = 9 + ((Fighter)selectionModel.getSelectedItem()).getModPosition() +
                mod.getSelectionModel().getSelectedIndex() - 3;
        action(range, "orientate");
    }

    public void drawWeaponAction() {
        int range = 6 + ((Fighter)selectionModel.getSelectedItem()).getModDrawWeapon() +
                mod.getSelectionModel().getSelectedIndex() - 3;
        action(range, "drawWeapon");
    }
    public void loadBowAction() {
        int range = 6 + ((Fighter)selectionModel.getSelectedItem()).getModLoadBow() +
                mod.getSelectionModel().getSelectedIndex() - 3;
        action(range, "loadBow");
    }
    public void useMagicAction() {
        value.setText("0");
        choice.setText(rb.getString("actions"));
    }

    public void aimAction() {
        value.setText("8");
        choice.setText(rb.getString("actionPoints"));
    }

    public void longActionAction() {
        value.setText("2");
        choice.setText(rb.getString("actions"));
    }

    public void otherActionAction() {
        value.setText("2");
        choice.setText(rb.getString("actionPoints"));
    }

    private int validate() {

        int base = -1;
        Number input = null;
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
            base = base * 6;
            actionPerformed = "useMagic";
        } else if(aim.isSelected()) {
            actionPerformed = "aim";
        } else if(longAction.isSelected()) {
            base = base * 6;
            actionPerformed = "longAction";
        } else if(otherAction.isSelected()) {
            actionPerformed = "otherAction";
        }
        action(base, actionPerformed);
    }

    public void unarmedAction() {
        int mod;
        if(unarmed.isSelected()) {
            mod = -2;
        } else {
            mod = 0;
        }
        actionMod = mod;
    }

    public void freeActionAction() {
        action(0, "freeAction");
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

    private void setHandler(KeyEvent event) {
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

    // getter und setter

    public void setModel(Model model) {
        this.model = model;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void updateView(ListOfFighters list) {

    }

    public void setFightersList(FightersList fightersList) {
        this.fightersList = fightersList;
    }

    public void setSelectedIndex(ReadOnlyIntegerProperty selectedIndex) {
        this.selectedIndex = selectedIndex;
        selectedIndex.addListener(this);
    }

    public void setKeyEventHandlers(Scene scene) {
        scene.addEventHandler(KeyEvent.KEY_PRESSED, this::setHandler);
    }

    public void setSelectionModel(SelectionModel selectionModel) {
        this.selectionModel = selectionModel;
    }

    public SelectionModel getSelectionModel() {
        return selectionModel;
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

    public RadioButton getUseMagic() {
        return useMagic;
    }

    public RadioButton getAim() {
        return aim;
    }

    public RadioButton getLongAction() {
        return longAction;
    }

    public RadioButton getOtherAction() {
        return otherAction;
    }

    public Button getChoice() {
        return choice;
    }

    public ToggleButton getUnarmed() {
        return unarmed;
    }

    public ComboBox getMod() {
        return mod;
    }

    public TextField getWert() {
        return value;
    }

    public Button getIncr() {
        return incr;
    }

    public Button getDecr() {
        return decr;
    }

    public Button getFreeAction() {
        return freeAction;
    }
}
