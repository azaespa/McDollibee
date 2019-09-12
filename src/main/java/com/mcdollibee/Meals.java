package com.mcdollibee;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;

class Meals extends Stages{
    private Image mealCompany;
    private String mealDescription;
    private ObservableList<String> mealObsView;

    Meals(AnchorPane homeSection,
          AnchorPane profileSection,
          AnchorPane myCartSection,
          AnchorPane mealsSection) {
        homeSection.setVisible(false);
        profileSection.setVisible(false);
        myCartSection.setVisible(false);
        mealsSection.setVisible(true);
    }

    Meals(){

    }

    void setMealsToListView(ListView<String> mealListView,
                            Label mealNameLbl,
                            Label mealPriceLbl,
                            Text mealDescTxt,
                            ImageView imgViewMeal,
                            ImageView mealCompanyImg) {
        Service<Void> backgroundThread = new Service<>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        McDatabase sql = new McDatabase();
                        mealObsView = FXCollections.observableArrayList(sql.getMealList());
                        return null;
                    }
                };
            }
        };
        backgroundThread.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                mealListView.setItems(mealObsView);
                mealListView.setDisable(false);
                mealListView.getSelectionModel().select(0);
                selectedMealInfo(mealListView,
                        mealNameLbl,
                        mealPriceLbl,
                        mealDescTxt,
                        imgViewMeal,
                        mealCompanyImg);
            }
        });
        backgroundThread.setOnRunning(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                mealListView.setDisable(true);
            }
        });
        backgroundThread.restart();
    }

    void selectedMealInfo(ListView<String> mealListView,
                                  Label mealNameLbl,
                                  Label mealPriceLbl,
                                  Text mealDescTxt,
                                  ImageView imgViewMeal,
                                  ImageView mealCompanyImg) {
        Service<Image> backgroundThreadImg = new Service<>() {
            @Override
            protected Task<Image> createTask() {
                return new Task<>() {
                    @Override
                    protected Image call() throws Exception {
                        McDatabase sql = new McDatabase();
                        sql.getMealInfo(mealListView.getSelectionModel().getSelectedItem());
                        updateMessage(sql.getMealName());
                        updateTitle("₱" + sql.getMealPrice());
                        mealDescription = sql.getMealDesc();
                        mealCompany = sql.getMealCompany();
                        return sql.getMealImage();
                    }
                };
            }
        };
        backgroundThreadImg.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                mealNameLbl.textProperty().unbind();
                mealPriceLbl.textProperty().unbind();
                imgViewMeal.imageProperty().unbind();
                mealDescTxt.setText(mealDescription);
                mealCompanyImg.setImage(mealCompany);
            }
        });
        mealNameLbl.setPrefWidth(274);
        mealNameLbl.setAlignment(Pos.CENTER_LEFT);
        mealPriceLbl.setPrefWidth(92);
        mealPriceLbl.setAlignment(Pos.CENTER_LEFT);
        imgViewMeal.imageProperty().bind(backgroundThreadImg.valueProperty());
        mealNameLbl.textProperty().bind(backgroundThreadImg.messageProperty());
        mealPriceLbl.textProperty().bind(backgroundThreadImg.titleProperty());
        backgroundThreadImg.restart();
    }

    void mealsBtnAppearance(Button homeBtn, Button profileBtn, Button myCartBtn, Button mealsBtn){
        homeBtn.setTextFill(Paint.valueOf("#FFFFFF"));
        homeBtn.setStyle("-fx-background-color: #DA153B");
        profileBtn.setTextFill(Paint.valueOf("#FFFFFF"));
        profileBtn.setStyle("-fx-background-color: #DA153B");
        myCartBtn.setTextFill(Paint.valueOf("#FFFFFF"));
        myCartBtn.setStyle("-fx-background-color: #DA153B");
        mealsBtn.setTextFill(Paint.valueOf("#DA153B"));
        mealsBtn.setStyle("-fx-background-color: #FFFFFF");
    }
}