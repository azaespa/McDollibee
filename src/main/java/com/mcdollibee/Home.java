package com.mcdollibee;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;

class Home {

    private int featuredMealPrice;
    private String featuredMealName;
    private String featuredMealDesc;
    private Image featuredCompany;

    Home(AnchorPane homeSection, AnchorPane profileSection, AnchorPane myCartSection, AnchorPane mealsSection){
        homeSection.setVisible(true);
        profileSection.setVisible(false);
        myCartSection.setVisible(false);
        mealsSection.setVisible(false);
    }
    void homeBtnAppearance(Button homeBtn, Button profileBtn, Button myCartBtn, Button mealsBtn){
        homeBtn.setTextFill(Paint.valueOf("#DA153B"));
        homeBtn.setStyle("-fx-background-color: #FFFFFF");
        profileBtn.setTextFill(Paint.valueOf("#FFFFFF"));
        profileBtn.setStyle("-fx-background-color: #DA153B");
        myCartBtn.setTextFill(Paint.valueOf("#FFFFFF"));
        myCartBtn.setStyle("-fx-background-color: #DA153B");
        mealsBtn.setTextFill(Paint.valueOf("#FFFFFF"));
        mealsBtn.setStyle("-fx-background-color: #DA153B");
    }
    void loadFeaturedMeal(ImageView featuredCompanyImg, ImageView featuredMeal, Text featuredMealDescTxt,
                          Label featuredMealNameLbl, Label featuredMealPriceLbl, Label onlyLbl,
                          Button hsAddToCart){
        Service<Image> backgroundThreadImg = new Service<>() {
            @Override
            protected Task<Image> createTask() {
                return new Task<>() {
                    @Override
                    protected Image call() throws Exception {
                        McDatabase sql = new McDatabase();
                        sql.setRandomFeaturedMeal();
                        featuredMealName = sql.getFeaturedMealName();
                        featuredMealDesc = sql.getFeaturedMealDesc();
                        featuredMealPrice = sql.getFeaturedMealPrice();
                        featuredCompany = sql.getFeaturedCompany();
                        return sql.getFeaturedImg();
                    }
                };
            }
        };
        backgroundThreadImg.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {

                featuredMealNameLbl.setPrefWidth(230);
                featuredMealNameLbl.setAlignment(Pos.CENTER_RIGHT);
                featuredMealPriceLbl.setPrefWidth(246);
                featuredMealPriceLbl.setAlignment(Pos.CENTER_RIGHT);

                featuredMeal.setImage(backgroundThreadImg.getValue());
                featuredCompanyImg.setImage(featuredCompany);
                featuredMealNameLbl.setText(featuredMealName);
                featuredMealDescTxt.setText(featuredMealDesc);
                featuredMealPriceLbl.setText("₱" + featuredMealPrice);
                onlyLbl.setText("ONLY");
                hsAddToCart.setVisible(true);
            }
        });
        backgroundThreadImg.restart();
    }
}
