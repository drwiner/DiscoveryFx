package com.discoveryfx;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

public class ClusterHeader extends GridPane {
    private String name;
    private Integer size;
    private String repSentence;

    private Button closeButton = new Button("Remove");

    public Button getCloseButton() {
        return closeButton;
    }

    public ClusterHeader(String name, Integer size, String repSentence) {
        super();
        this.name = name;
        this.size = size;
        this.repSentence = repSentence;

        setHgap(10);
        setVgap(0);

        setPadding(new Insets(1));

        Label nameLabel = new Label(this.name);
        nameLabel.setMinWidth(40);
        nameLabel.setMaxWidth(40);
        Label sizeLabel = new Label("(n=" + size.toString()+ ")");
        sizeLabel.setMinWidth(70);
        sizeLabel.setMaxWidth(70);
        Label repLabel = new Label(repSentence);
        repLabel.setMinWidth(320);
        repLabel.setMaxWidth(320);

        GridPane.setHalignment(nameLabel, HPos.LEFT);
        GridPane.setValignment(nameLabel, VPos.CENTER);

        GridPane.setHalignment(sizeLabel, HPos.LEFT);
        GridPane.setValignment(sizeLabel, VPos.CENTER);

        GridPane.setHalignment(repLabel, HPos.LEFT);
        GridPane.setValignment(repLabel, VPos.CENTER);

        add(nameLabel, 0, 0, 1, 1);
        add(sizeLabel, 2, 0, 1, 1);
        add(repLabel, 4, 0, 4, 1);

        Region region1 = new Region();
        region1.setMinWidth(5);
        region1.setMaxWidth(5);
        Region region2 = new Region();
        region2.setMinWidth(5);
        region2.setMaxWidth(5);
        Region region3 =new Region();
        region3.setMinWidth(5);
        region3.setMaxWidth(5);
        add(region1, 1, 0, 1, 1);
        add(region2, 3, 0, 1, 1);
        add(region3, 8, 0, 1, 1);

        GridPane.setHalignment(closeButton, HPos.RIGHT);
        GridPane.setValignment(closeButton, VPos.TOP);

        add(closeButton, 9, 0, 1, 1);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getRepSentence() {
        return repSentence;
    }

    public void setRepSentence(String repSentence) {
        this.repSentence = repSentence;
    }
}
