package com.discoveryfx;

import com.discoveryfx.com.kasisto.cluster.ClusterDatum;
import javafx.scene.control.TableRow;

public class DatumRow extends TableRow<ClusterDatum> {

    private String tableName;
    private DatumInteractionManager.TableViewEnum tableType;

    public DatumRow() {
        super();
    }

    public DatumRow(String tableName, DatumInteractionManager.TableViewEnum tableType) {
        super();
        this.tableName = tableName;
        this.tableType = tableType;
    }

    @Override
    protected void updateItem(ClusterDatum item, boolean empty) {
        super.updateItem(item, empty);

//        if (item == null || empty)
//            setBackground(null);
//        else if (item.getMagicValue() > 0.001) {
////            setBackground(null);
////            Color interpolate = expInterpolateColor(Color.WHITE, Color.GREEN, item.getMagicValue());
////            setBackground(new Background(new BackgroundFill(interpolate, CornerRadii.EMPTY, Insets.EMPTY)));
//        }
//        else if (item.getMagicValue() <= 0.001) {
//        }
//        else
//            setBackground(null);
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public DatumInteractionManager.TableViewEnum getTableType() {
        return tableType;
    }

    public void setTableType(DatumInteractionManager.TableViewEnum tableType) {
        this.tableType = tableType;
    }

}
