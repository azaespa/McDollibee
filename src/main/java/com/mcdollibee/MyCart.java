package com.mcdollibee;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;

import java.util.ArrayList;
import java.util.List;

class MyCart {
    private Image[] listOfImgs;
    private ObservableList<String> mealCheckOutObs;

    MyCart(AnchorPane homeSection,
           AnchorPane profileSection,
           AnchorPane myCartSection,
           AnchorPane mealsSection,
           Label emptyCartError,
           Label emptyCartError1,
           Label cartRedStarsError){
        homeSection.setVisible(false);
        profileSection.setVisible(false);
        myCartSection.setVisible(true);
        mealsSection.setVisible(false);
        emptyCartError.setVisible(false);
        emptyCartError1.setVisible(false);
        cartRedStarsError.setVisible(false);
    }
    void cartBtnAppearance(Button homeBtn,
                                   Button profileBtn,
                                   Button myCartBtn,
                                   Button mealsBtn){
        homeBtn.setTextFill(Paint.valueOf("#FFFFFF"));
        homeBtn.setStyle("-fx-background-color: #DA153B");
        profileBtn.setTextFill(Paint.valueOf("#FFFFFF"));
        profileBtn.setStyle("-fx-background-color: #DA153B");
        myCartBtn.setTextFill(Paint.valueOf("#DA153B"));
        myCartBtn.setStyle("-fx-background-color: #FFFFFF");
        mealsBtn.setTextFill(Paint.valueOf("#FFFFFF"));
        mealsBtn.setStyle("-fx-background-color: #DA153B");
    }
    void setOrdersToCart(ListView<String> addTCListView, ListView<String> mealCheckOutLV){
        Service <Void> backgroundThread = new Service<>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        McDatabase sql = new McDatabase();
                        mealCheckOutObs = FXCollections.observableArrayList(addTCListView.getItems());
                        listOfImgs = new Image[addTCListView.getItems().size()];
                        for(int i=0; i<listOfImgs.length; i++){
                            listOfImgs[i] = sql.imgCheckOut(addTCListView.getItems().get(i));
                        }
                        sql.disconnect();
                        return null;
                    }
                };
            }
        };
        backgroundThread.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                mealCheckOutLV.setItems(mealCheckOutObs);
                mealCheckOutLV.setCellFactory(param -> new ListCell<String>(){
                    private ImageView imgView = new ImageView();

                    @Override
                    protected void updateItem(String s, boolean b) {
                        super.updateItem(s, b);
                        if(b){
                            setText(null);
                            setGraphic(null);
                        } else{
                            for (int i=0; i<listOfImgs.length;i++){
                                if(mealCheckOutLV.getItems().get(i).equals(s)){
                                    imgView.setImage(listOfImgs[i]);
                                }
                                imgView.setFitHeight(50);
                                imgView.setFitWidth(50);
                                imgView.setPreserveRatio(true);
                                imgView.smoothProperty();
                                setText(s);
                                setGraphic(imgView);
                            }
                        }
                    }
                });
            }
        });
        backgroundThread.restart();
    }
}
