package com.discoveryfx;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.NumberBinding;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.TreeView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class InteractiveControlPanel extends GridPane {

    private static final Logger LOG= LoggerFactory.getLogger(InteractiveControlPanel.class);

    private List<Control> controlItems;

    private static List<NumberBinding> localBindings = new ArrayList<>();

    public List<Control> getControlItems() {
        return controlItems;
    }

    public static InteractiveControlPanel createClusterDataPanel(TableReportInterface reportTree){
        Button split = new Button("Split");
        Button merge = new Button("Merge");
        Button remove = new Button("Remove");
        Button deselect = new Button("Deselect All");

        List<Control> controls = new ArrayList<>();
        controls.add(split);
        controls.add(merge);
        controls.add(remove);
        controls.add(deselect);

        split.setDisable(true);
        merge.setDisable(true);
        remove.setDisable(true);
        deselect.setDisable(true);


        IntegerBinding numHeadersSelected = Bindings.size(reportTree.getSelectionModel().getSelectedItems());
        merge.disableProperty().bind(numHeadersSelected.lessThan(2));
        remove.disableProperty().bind(numHeadersSelected.isEqualTo(0));
        deselect.disableProperty().bind(numHeadersSelected.isEqualTo(0));
        localBindings.add(numHeadersSelected);

        deselect.setOnAction(e -> {
            reportTree.getSelectionModel().clearSelection();
        });

        remove.setOnAction(e -> {
            ObservableList<Integer> selectedIndices = reportTree.getSelectionModel().getSelectedIndices();
            if (selectedIndices != null)
                TableActions.remove(selectedIndices, reportTree);
        });

        InteractiveControlPanel interactiveControlPanel = new InteractiveControlPanel(controls);

        return interactiveControlPanel;
    }

    public InteractiveControlPanel(List<Control> controls) {
        super();

        this.controlItems = controls;

        setHgap(10);
        setVgap(0);
        setPadding(new Insets(1));

        GridPane.setHalignment(this, HPos.LEFT);
        GridPane.setValignment(this, VPos.CENTER);

        int i = 0;
        for (Control control: controls) {
            Region r = new Region();
            r.setMinWidth(5);
            r.setMaxWidth(5);
            add(control, i, 0, 1, 1);
            add(r, i+1, 0, 1, 1);
            i+=2;
        }
    }

}
