package com.discoveryfx;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

import java.util.List;
import java.util.stream.Collectors;

public class KaiMenuBar extends MenuBar {
    public KaiMenuBar() {
        super();
        getMenus().addAll(
            createMenu("File", List.of("Import Data", "Import Package", "Save Report", "Export Report")),
            createMenu("Edit", List.of("Merge Intents", "Fold Intents", "Remove Intent", "Run report")),
            createMenu("Help", List.of("Contact"))
        );
    }

    private static  Menu createMenu(String name, List<String> items){
        Menu menu = new Menu(name);
        menu.getItems().addAll(items.stream().map(MenuItem::new).collect(Collectors.toList()));
        return menu;
    }
}
