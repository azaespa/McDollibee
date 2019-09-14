package com.mcdollibee;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;

public class Stages{
    private boolean home;
    private boolean profile;
    private boolean cart;
    private boolean meal;
    private String red = "#DA153B";
    private String white = "#FFFFFF";

    Stages(){

    }

    public void setVisible(boolean h, boolean p, boolean c, boolean m){
        this.home = h;
        this.profile = p;
        this.cart = c;
        this.meal = m;
    }

    public void setStage(AnchorPane hs, AnchorPane ps, AnchorPane mcs, AnchorPane ms){
        hs.setVisible(home);
        ps.setVisible(profile);
        mcs.setVisible(cart);
        ms.setVisible(meal);
    }

    public void setBtn(Button hb, Button pb, Button mcb, Button mb){
        String[][] setColor = setBtnColor();
        hb.setTextFill(Paint.valueOf(setColor[0][0]));
        hb.setStyle("-fx-background-color: " + setColor[0][1]);
        pb.setTextFill(Paint.valueOf(setColor[1][0]));
        pb.setStyle("-fx-background-color: " + setColor[1][1]);
        mcb.setTextFill(Paint.valueOf(setColor[2][0]));
        mcb.setStyle("-fx-background-color: " + setColor[2][1]);
        mb.setTextFill(Paint.valueOf(setColor[3][0]));
        mb.setStyle("-fx-background-color: " + setColor[3][1]);
    }

    private String[][] setBtnColor(){
        boolean[] boolStage = {home,profile,cart,meal};
        String[][] color = new String[4][2];
        int i = 0;
        for(boolean x : boolStage){
            if(x){
                color[i][0] = red;
                color[i][1] = white;
            }else{
                color[i][0] = white;
                color[i][1] = red;
            }
            i++;
        }
        return color;
    }
    public void loadFeaturedMeal(ImageView featuredCompanyImg, ImageView featuredMeal, Text featuredMealDescTxt,
                          Label featuredMealNameLbl, Label featuredMealPriceLbl, Label onlyLbl,
                          Button hsAddToCart){}
    public void setMealsToListView(ListView<String> mealListView,
                                   Label mealNameLbl, Label mealPriceLbl,
                                   Text mealDescTxt, ImageView imgViewMeal,
                                   ImageView mealCompanyImg){ }
    public void setOrdersToCart(ListView<String> addTCListView, ListView<String> mealCheckOutLV){}
}
