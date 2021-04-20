package com.discoveryfx;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class AllTrueBinding extends BooleanBinding {
    private final ObservableList<BooleanProperty> boundList;
    private final ListChangeListener<BooleanProperty> BOUND_LIST_CHANGE_LISTENER =
            new ListChangeListener<BooleanProperty>() {
                @Override public void onChanged(
                        ListChangeListener.Change<? extends BooleanProperty> change
                ) {
                    refreshBinding();
                }
            };
    private BooleanProperty[] observedProperties = {};

    AllTrueBinding(ObservableList<BooleanProperty> booleanList) {
        booleanList.addListener(BOUND_LIST_CHANGE_LISTENER);
        boundList = booleanList;
        refreshBinding();
    }

    @Override protected boolean computeValue() {
        for (BooleanProperty bp: observedProperties) {
            if (!bp.get()) {
                return false;
            }
        }

        return true;
    }

    @Override public void dispose() {
        boundList.removeListener(BOUND_LIST_CHANGE_LISTENER);
        super.dispose();
    }

    private void refreshBinding() {
        super.unbind(observedProperties);
        observedProperties = boundList.toArray(new BooleanProperty[0]);
        super.bind(observedProperties);
        this.invalidate();
    }
}
