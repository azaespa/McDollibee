package com.mcdollibee;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;

import java.util.List;

public class Profile {
    Profile(AnchorPane homeSection,
            AnchorPane profileSection,
            AnchorPane myCartSection,
            AnchorPane mealsSection,
            Label invalidAccountError){
        homeSection.setVisible(false);
        profileSection.setVisible(true);
        myCartSection.setVisible(false);
        mealsSection.setVisible(false);
        invalidAccountError.setVisible(false);
    }

    Profile (){
    }

    void profileBtnAppearance(Button homeBtn,
                              Button profileBtn,
                              Button myCartBtn,
                              Button mealsBtn){
        homeBtn.setTextFill(Paint.valueOf("#FFFFFF"));
        homeBtn.setStyle("-fx-background-color: #DA153B");
        profileBtn.setTextFill(Paint.valueOf("#DA153B"));
        profileBtn.setStyle("-fx-background-color: #FFFFFF");
        myCartBtn.setTextFill(Paint.valueOf("#FFFFFF"));
        myCartBtn.setStyle("-fx-background-color: #DA153B");
        mealsBtn.setTextFill(Paint.valueOf("#FFFFFF"));
        mealsBtn.setStyle("-fx-background-color: #DA153B");
    }

    void setOrderHistoryPane(int userId, ComboBox<String> dateOrderedCombo){
        dateOrderedCombo.getItems().clear();
        Service<List<String>> backgroundThread = new Service<>() {
            @Override
            protected Task<List<String>> createTask() {
                return new Task<>() {
                    @Override
                    protected List<String> call() throws Exception {
                        McDatabase sql = new McDatabase();
                        sql.setOrderHistoryDates(userId);
                        return sql.getDateOrderedList();
                    }
                };
            }
        };
        backgroundThread.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                dateOrderedCombo.getItems().addAll(backgroundThread.getValue());
            }
        });
        backgroundThread.restart();
    }
    void setUserInfoPane(int userId, Label fn, Label address, Label pn){
        McDatabase sql = new McDatabase();
        sql.setUserInfo(userId);
        fn.setText(sql.getFullName());
        address.setText(sql.getAddress());
        pn.setText(String.valueOf(sql.getPhoneNumber()));
    }
}
