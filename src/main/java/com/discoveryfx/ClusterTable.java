package com.discoveryfx;

import com.discoveryfx.com.kasisto.cluster.ClusterDatum;
import com.discoveryfx.com.kasisto.cluster.ClusterResult;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ClusterTable extends TableView<ClusterDatum> {

    private static final Logger LOG= LoggerFactory.getLogger(ClusterTable.class);

    private ClusterResult cluster;

    public ClusterTable() {
        super();
    }

    public ClusterTable(ClusterResult cluster) {
        this();
        this.cluster = cluster;

        TableColumn<ClusterDatum, String> sentenceColumn = new TableColumn<>("Request Text");
        sentenceColumn.setMinWidth(500);
        sentenceColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getDatum()));

        TableColumn<ClusterDatum, Integer> countColumn = new TableColumn<>("Count");
        countColumn.setMinWidth(50);
        countColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getCount()));

        TableColumn<ClusterDatum, Double> confidenceColumn = new TableColumn<>("Confidence");
        confidenceColumn.setMinWidth(50);
        confidenceColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(new BigDecimal(param.getValue().getConfidence()).doubleValue()));

        TableColumn<ClusterDatum, Double> dynamicColumn = new TableColumn<>("Dynamic");
        dynamicColumn.setMinWidth(50);


        dynamicColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(0d));

//        dynamicColumn.

        getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            getColumns().remove(getColumns().get(getColumns().size() - 1));
            TableColumn<ClusterDatum, Double> dC = new TableColumn<>("Sim To Selected");
            dC.setCellValueFactory(param -> {
                if (newValue != null && oldValue != newValue){
                    float[] baseVector = Client.getProcUttToEmb().getOrDefault(newValue.getDatum(), new float[768]);
                    float[] ourVector = Client.getProcUttToEmb().getOrDefault(param.getValue().getDatum(), new float[768]);

                    if (baseVector == null || ourVector == null){
                        LOG.info(param.getValue().getDatum());
                        param.getValue().setDynamicvalue(0d);
                        return new ReadOnlyObjectWrapper<>(0d);
                    }
                    double v = ModelIO.cosineSim(baseVector, ourVector);
                    param.getValue().setDynamicvalue(v);
                    return new ReadOnlyObjectWrapper<>(round(v, 4));
                } else {
                    param.getValue().setDynamicvalue(0d);
                    return new ReadOnlyObjectWrapper<>(0d);
                }
            });

            getColumns().add(dC);
        });


        setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(ClusterDatum item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null)
                    setBackground(null);
                else if (item.getDynamicvalue() >= 0) {
                    Color interpolate = expInterpolateColor(Color.WHITE, Color.GREEN, item.getDynamicvalue());
                    setBackground(new Background(new BackgroundFill(interpolate, CornerRadii.EMPTY, Insets.EMPTY)));
                }
                else
                    setBackground(null);
            }
        });

        getColumns().add(sentenceColumn);
        getColumns().add(countColumn);
        getColumns().add(confidenceColumn);
        getColumns().add(dynamicColumn);



        ObservableList<ClusterDatum> data = FXCollections.observableArrayList(cluster.getDatums());
        setItems(data);
    }




    public ClusterResult getCluster() {
        return cluster;
    }

    public void setCluster(ClusterResult cluster) {
        this.cluster = cluster;
    }


    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


    public static Color expInterpolateColor(Color startValue, Color endValue, double t){
        if (t <= 0.0) return startValue;
        if (t >= 1.0) return endValue;
        float ft = (float) (Math.pow(2, t) -1);
        //(2 ^ (.4) - 1)
        return new Color(
              startValue.getRed() +(endValue.getRed()  - startValue.getRed() )     * ft ,
                startValue.getGreen() + (endValue.getGreen()  - startValue.getGreen()  )   * ft,
                startValue.getBlue()    + (endValue.getBlue()    - startValue.getBlue())    * ft,
                startValue.getOpacity() + (endValue.getOpacity() - startValue.getOpacity()) * ft
        );
    }

}
