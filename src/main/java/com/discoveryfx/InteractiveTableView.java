package com.discoveryfx;

import com.discoveryfx.com.kasisto.cluster.ClusterDatum;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.discoveryfx.DatumInteractionManager.SERIALIZED_MIME_TYPE;

public class InteractiveTableView extends TableView<ClusterDatum> {
    private static final Logger LOG= LoggerFactory.getLogger(InteractiveTableView.class);

    protected String tableName;
    protected DatumInteractionManager datumInteractionManager;
    protected DatumInteractionManager.TableViewEnum tableType;
    protected List<ClusterDatum> data;

    public final BooleanProperty updatedValuesProperty = new SimpleBooleanProperty(false);

    public List<ClusterDatum> getData() {
        return data;
    }

    public InteractiveTableView(String tableName, DatumInteractionManager.TableViewEnum tableType,
                                List<ClusterDatum> data, List<TableColumn<ClusterDatum, ?>> columns) {
        this(tableName, tableType, data, columns, true, true);
    }

    public InteractiveTableView(String tableName, DatumInteractionManager.TableViewEnum tableType,
                                List<ClusterDatum> data, List<TableColumn<ClusterDatum, ?>> columns,
                                boolean listenToMagic,
                                boolean dragAndDroppable) {
        super();

        this.tableName = tableName;
        this.tableType = tableType;
        this.data = data;

        datumInteractionManager = DatumInteractionManager.getINSTANCE();

        if (dragAndDroppable) {
            createAndSetDragOverAndDrop();
            createAndSetRowFactory();
        }

        if (listenToMagic)
            createMagicListener();

        getColumns().addAll(columns);

        data.forEach(datum -> datum.setSource(tableName));

        setItems(FXCollections.observableArrayList(data));
    }

    public void add(ClusterDatum datum){
        getItems().add(datum);
        data.add(datum);
    }

    private void createMagicListener(){
        Client.getMagicSelectionTable().avgUpdated.addListener((observableValue, aBoolean, t1) -> {
            if (t1 == null || !t1) // Do nothing when false.
                return;

            // Remove the column and keep it that way if there is nothing in the selection.
//            if (Client.getMagicSelectionTable().isClear.get())
//                return;

            LOG.info("Processing that Averages were updated for table " + tableName);

//            getColumns().remove(getColumns().get(getColumns().size() - 1));
//
//            TableColumn<ClusterDatum, Double> dC = new TableColumn<>("Magic Sim");

            getItems().forEach(item -> {
                float[] currentAvg = Client.getMagicSelectionTable().getCurrentAvg();
                float[] ourVector = Client.getProcUttToEmb().getOrDefault(item.getDatum(), new float[768]);
                if (currentAvg == null || ourVector == null){
                    item.setMagicValue(0d);
                } else {
                    double v = ModelIO.cosineSim(currentAvg, ourVector);
                    item.setMagicValue(v);
                }

            });

            refresh();
            updatedValuesProperty.setValue(true);


//            dC.setCellValueFactory(param -> {
//                LOG.info("Updating Average for table " + tableName);
//                float[] currentAvg = Client.getMagicSelectionTable().getCurrentAvg();
//                float[] ourVector = Client.getProcUttToEmb().getOrDefault(param.getValue().getDatum(), new float[768]);
//
//                if (currentAvg == null || ourVector == null){
//                    LOG.info(param.getValue().getDatum());
//                    param.getValue().setMagicValue(0d);
//                    updatedValuesProperty.setValue(true);
//                    return new ReadOnlyObjectWrapper<>(0d);
//                }
//
//                double v = ModelIO.cosineSim(currentAvg, ourVector);
//                param.getValue().setMagicValue(v);
//                updatedValuesProperty.setValue(true);
//                return new ReadOnlyObjectWrapper<>(DataTableHelper.round(v, 4));
//
//            });

//            getColumns().add(dC);

//            Client.getAudienceSelectionTable().updateData(getItems());
        });
    }

    private void createAndSetDragOverAndDrop(){
        addEventHandler(DragEvent.DRAG_OVER, e -> {
            if (datumInteractionManager.isClear())
                return;

            Dragboard db = e.getDragboard();
            if (db.hasContent(SERIALIZED_MIME_TYPE) && ! datumInteractionManager.getSelected().get(0).getSource().equalsIgnoreCase(this.tableName)) {
                e.acceptTransferModes(TransferMode.ANY);
                e.consume();
            }
        });

        addEventHandler(DragEvent.DRAG_DROPPED, e -> {
            // TODO only register and allow drag over/into if from a cluster and not from this table.=

            if (datumInteractionManager.isClear())
                return;


            Dragboard db = e.getDragboard();

            if (db.hasContent(SERIALIZED_MIME_TYPE)){
                List<ClusterDatum> selected = datumInteractionManager.getSelected();

                for (ClusterDatum datum: selected){
                    if (datum.getSource().equalsIgnoreCase(this.tableName)){
                        // then do nothing
                    } else {
                        // drop into

                        add(datum);

                        // If previous is cluster, then set opacity to low.
//                            drow.setOpacity(0.2);
                    }
                }
                datumInteractionManager.clear();
                e.setDropCompleted(true);
                e.consume();
            }
        });
    }

    private void createAndSetRowFactory(){
        setRowFactory(tv -> {

            DatumRow row = new DatumRow(tableName, tableType);

            addEventHandler(MouseEvent.DRAG_DETECTED, e -> {
                if (row.isEmpty())
                    return;

                if (row.getScene() == null)
                    return;

                Integer index = row.getIndex();

                // TODO: if CNTRL is pressed down, do not clear.
                // TODO: if SHIFT is pressed down, select all between this row and last selected if applicable.
                datumInteractionManager.clear();

                ObservableList<ClusterDatum> items = getSelectionModel().getSelectedItems();
                datumInteractionManager.select(items);

                // If it's the same table type, then it's move. Otherwise it's Copy.
                Dragboard db = row.startDragAndDrop(TransferMode.COPY);
                db.setDragView(row.snapshot(null, null));
                ClipboardContent cc = new ClipboardContent();
                cc.put(SERIALIZED_MIME_TYPE, index);

                db.setContent(cc);
                e.consume();
            });

            return row;
        });
    }
}
