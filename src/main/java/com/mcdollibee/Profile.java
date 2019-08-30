package com.mcdollibee;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;

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
}
