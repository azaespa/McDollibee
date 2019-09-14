package com.mcdollibee;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class PrimaryController implements Initializable {

    //Home Section
    @FXML private ImageView featuredMeal;
    @FXML private ImageView featuredCompanyImg;
    @FXML private Text featuredMealDescTxt;
    @FXML private Label featuredMealPriceLbl;
    @FXML private Label featuredMealNameLbl;
    @FXML private Label onlyLbl;
    @FXML private Button hsAddToCart;

    //Profile Section
    @FXML private Label invalidAccountError;
    @FXML private Label fullName;
    @FXML private Label totalPriceLbl;
    @FXML private Label fullNameInfo;
    @FXML private Label addressInfo;
    @FXML private Label phoneNumberInfo;
    @FXML private TextField username;
    @FXML private TextField usernameReg;
    @FXML private TextField firstNameReg;
    @FXML private TextField lastNameReg;
    @FXML private TextField addressReg;
    @FXML private TextField phoneNumberReg;
    @FXML private PasswordField password;
    @FXML private PasswordField passwordReg;
    @FXML private AnchorPane userPane;
    @FXML private AnchorPane loginPane;
    @FXML private AnchorPane registerPane;
    @FXML private ComboBox<String> dateOrderedCombo;
    @FXML private ListView<String> orderHistoryListView;
    private Boolean isLoggedIn = false;
    private int userId;
    private int phoneNumber;
    private String firstName;
    private String lastName;
    private String address;
    private static final DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    //My Cart Section
    @FXML private ListView<String> mealCheckOutLV;
    @FXML private Label emptyCartError;
    @FXML private Label emptyCartError1;
    @FXML private Label cartRedStarsError;
    @FXML private AnchorPane ordersPane;
    @FXML private AnchorPane checkOutPane;
    @FXML private TextField firstNameOrder;
    @FXML private TextField lastNameOrder;
    @FXML private TextField addressOrder;
    @FXML private TextField phoneNumOrder;
    @FXML private TextArea noteOrder;
    private Boolean fromCart = false;
    private List<String> mealsOrdered = new ArrayList<>();

    //Meals Section
    @FXML private ListView<String> mealListView;
    @FXML private Label mealPriceLbl;
    @FXML private Label mealNameLbl;
    @FXML private Text mealDescTxt;
    @FXML private ImageView mealCompanyImg;
    @FXML private ImageView imgViewMeal;

    //Navigation Bar
    @FXML private ListView<String> addTCListView;
    @FXML private Label totalLbl;
    @FXML private Button homeBtn;
    @FXML private Button profileBtn;
    @FXML private Button myCartBtn;
    @FXML private Button mealsBtn;
    @FXML private AnchorPane homeSection;
    @FXML private AnchorPane profileSection;
    @FXML private AnchorPane myCartSection;
    @FXML private AnchorPane mealsSection;
    private int total;

    private Service<Void> backgroundThread;

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
       setHomeSection(new ActionEvent());
    }

    //Navigation Bar
    public void setHomeSection(ActionEvent event){
        Stages home = new Home();
        home.setVisible(true, false, false, false);
        home.setStage(homeSection,profileSection,myCartSection,mealsSection);
        home.loadFeaturedMeal(featuredCompanyImg,
                featuredMeal,
                featuredMealDescTxt,
                featuredMealNameLbl,
                featuredMealPriceLbl,
                onlyLbl,
                hsAddToCart);
        home.setBtn(homeBtn, profileBtn, myCartBtn, mealsBtn);
    }
    public void setProfileSection(ActionEvent event){
        Stages profile = new Profile(invalidAccountError);
        profile.setVisible(false, true, false, false);
        profile.setStage(homeSection,profileSection,myCartSection,mealsSection);
        profile.setBtn(homeBtn,profileBtn,myCartBtn,mealsBtn);
        if(isLoggedIn){
            Profile reload = new Profile();
            reload.setOrderHistoryPane(userId,dateOrderedCombo);
        }
    }
    public void setMyCartSection(ActionEvent event){
        Stages myCart = new MyCart(emptyCartError, emptyCartError1, cartRedStarsError);
        myCart.setVisible(false, false, true, false);
        myCart.setStage(homeSection, profileSection, myCartSection, mealsSection);
        myCart.setBtn(homeBtn, profileBtn, myCartBtn, mealsBtn);
        myCart.setOrdersToCart(addTCListView,mealCheckOutLV);
        if(isLoggedIn){
            firstNameOrder.setText(firstName);
            lastNameOrder.setText(lastName);
            addressOrder.setText(address);
            phoneNumOrder.setText(String.valueOf(phoneNumber));
        }
    }
    public void setMealsSection(ActionEvent event){
        Stages meals = new Meals();
        meals.setVisible(false, false, false, true);
        meals.setStage(homeSection,profileSection,myCartSection,mealsSection);
        meals.setBtn(homeBtn,profileBtn,myCartBtn,mealsBtn);
        meals.setMealsToListView(mealListView,
                mealNameLbl,
                mealPriceLbl,
                mealDescTxt,
                imgViewMeal,
                mealCompanyImg);
    }
    public void removeSelected(ActionEvent event){
        backgroundThread = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        McDatabase sql = new McDatabase();
                        int mealPrice = sql.getMealInfoPrice(addTCListView.getSelectionModel().getSelectedItem());
                        total -= mealPrice;
                        updateMessage("Total: P" + total);
                        return null;
                    }
                };
            }
        };
        backgroundThread.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                addTCListView.getItems().remove(addTCListView.getSelectionModel().getSelectedItem());
                if(myCartSection.isVisible()){
                    setMyCartSection(new ActionEvent());
                }
                totalLbl.textProperty().unbind();
            }
        });
        totalLbl.textProperty().bind(backgroundThread.messageProperty());
        backgroundThread.restart();
    }
    public void removeAll(ActionEvent event){
        addTCListView.getItems().clear();
        total = 0;
        totalLbl.setText("Total: P" + total);
        if(myCartSection.isVisible()){
            setMyCartSection(new ActionEvent());
        }
    }

    //Home Section
    public void addToCart_HS(ActionEvent event){
        McDatabase sql = new McDatabase();
        sql.setItemsToCart(featuredMealNameLbl.getText());
        addTCListView.getItems().add(sql.getFeaturedMealName());
        total += sql.getFeaturedMealPrice();
        totalLbl.setText("Total: P" + total);
        if(addTCListView.getItems().size() > 0){
            addTCListView.getSelectionModel().select(0);
        }
    }

    //Profile Section
    public void loginForm(ActionEvent event){
        backgroundThread = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        McDatabase sql = new McDatabase();
                        if(!sql.userLogin(username.getText(), password.getText())){
                            cancel();
                        } else {
                            updateMessage(sql.getFullName());
                            userId = sql.getId();
                            firstName = sql.getFirstName();
                            lastName = sql.getLastName();
                            address = sql.getAddress();
                            phoneNumber = sql.getPhoneNumber();
                        }
                        return null;
                    }
                };
            }
        };
        backgroundThread.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                userPane.setVisible(true);
                loginPane.setVisible(false);
                Profile profile = new Profile();
                profile.setOrderHistoryPane(userId,dateOrderedCombo);
                profile.setUserInfoPane(userId, fullNameInfo, addressInfo, phoneNumberInfo);
                fullName.textProperty().unbind();
                isLoggedIn = true;
                if(fromCart){
                    proceedCheckOut(new ActionEvent());
                    setMyCartSection(new ActionEvent());
                }
            }
        });
        backgroundThread.setOnCancelled(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                invalidAccountError.setVisible(true);
            }
        });
        fullName.textProperty().bind(backgroundThread.messageProperty());
        backgroundThread.restart();
    }
    public void registerForm(ActionEvent event){
        backgroundThread = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        McDatabase sql = new McDatabase();

                        if(usernameReg.getText().isEmpty() || passwordReg.getText().isEmpty() || firstNameReg.getText().isEmpty()
                                && lastNameReg.getText().isEmpty() || addressReg.getText().isEmpty() || phoneNumberReg.getText().isEmpty()){
                            cancel();
                        }else{
                            sql.userRegister(usernameReg.getText(), passwordReg.getText(), firstNameReg.getText(),
                                    lastNameReg.getText(), addressReg.getText(), Integer.parseInt(phoneNumberReg.getText()));
                        }
                        return null;
                    }
                };
            }
        };
        backgroundThread.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("McDolliRegister");
                alert.setHeaderText("Officialy McBidaman!");
                alert.setContentText("Registration Successful!");
                alert.setResizable(false);
                alert.showAndWait();
            }
        });
        backgroundThread.setOnCancelled(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("McDolliRegister");
                alert.setHeaderText("Jolly McSadness!");
                alert.setContentText("Invalid Input!");
                alert.setResizable(false);
                alert.showAndWait();
            }
        });
        backgroundThread.restart();
    }
    public void setRegisterPane(ActionEvent event){
        loginPane.setVisible(false);
        registerPane.setVisible(true);
    }
    public void setLoginPane(ActionEvent event){
        loginPane.setVisible(true);
        registerPane.setVisible(false);
    }
    public void getOrderHistory(ActionEvent event){
        totalPriceLbl.setPrefWidth(170);
        totalPriceLbl.setAlignment(Pos.CENTER_LEFT);
        Service<List<String>> backgroundThread = new Service<>() {
            @Override
            protected Task<List<String>> createTask() {
                return new Task<>() {
                    @Override
                    protected List<String> call() throws Exception {
                        McDatabase sql = new McDatabase();
                        sql.setOrderHistory(userId, dateOrderedCombo.getValue());
                        updateMessage("₱" + sql.getTotalOrderedPrice());
                        return sql.getOrderHistory();
                    }
                };
            }
        };
        backgroundThread.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                orderHistoryListView.getItems().clear();
                orderHistoryListView.getItems().addAll(backgroundThread.getValue());
                totalLbl.textProperty().unbind();
            }
        });
        totalPriceLbl.textProperty().bind(backgroundThread.messageProperty());
        backgroundThread.restart();
    }

    //My Cart Section
    public void proceedCheckOut(ActionEvent event){
        if(!addTCListView.getItems().isEmpty()){
            if(!isLoggedIn){
                ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
                ButtonType no = new ButtonType("No, thanks", ButtonBar.ButtonData.CANCEL_CLOSE);
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("McJellibery");
                alert.setHeaderText("Looks like you're not logged in yet.");
                alert.setContentText("Want to log in first and use your default address?");
                alert.getButtonTypes().setAll(yes,no);
                Optional<ButtonType> result = alert.showAndWait();
                if(result.get().equals(yes)){
                    fromCart = true;
                    setProfileSection(new ActionEvent());
                } else {
                    ordersPane.setVisible(false);
                    checkOutPane.setVisible(true);
                }
            }else {
                ordersPane.setVisible(false);
                checkOutPane.setVisible(true);
                firstNameOrder.setText(firstName);
                lastNameOrder.setText(lastName);
                addressOrder.setText(address);
                phoneNumOrder.setText(String.valueOf(phoneNumber));
            }
        } else {
            emptyCartError.setVisible(true);
        }
    }
    public void confirmOrder(ActionEvent event){
        backgroundThread = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        Date date = new Date();
                        McDatabase sql = new McDatabase();
                        mealsOrdered.addAll(addTCListView.getItems());
                        if(firstNameOrder.getText().isEmpty() || lastNameOrder.getText().isEmpty() ||
                                addressOrder.getText().isEmpty() || phoneNumOrder.getText().isEmpty() ||
                                addTCListView.getItems().isEmpty()){
                            cancel();
                        } else {
                            if(isLoggedIn){
                                sql.userOrder(userId,firstName,lastName,address,phoneNumber,noteOrder.getText(),
                                        mealsOrdered.toString(),total,sdf.format(date));
                            } else {
                                sql.userOrder(-1, firstNameOrder.getText(),lastNameOrder.getText(),
                                        addressOrder.getText(), Integer.parseInt(phoneNumOrder.getText()),
                                        noteOrder.getText(),mealsOrdered.toString(),total,sdf.format(date));
                            }
                        }
                        return null;
                    }
                };
            }
        };
        backgroundThread.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("McJellibery");
                alert.setHeaderText("Order Successful!");
                alert.setContentText("Our staff will contact you");
                alert.showAndWait();
                removeAll(new ActionEvent());
                setHomeSection(new ActionEvent());
                backBtn(new ActionEvent());
            }
        });
        backgroundThread.setOnCancelled(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                if(addTCListView.getItems().isEmpty()){
                    emptyCartError1.setVisible(true);
                } else {
                    cartRedStarsError.setVisible(true);
                }
            }
        });
        backgroundThread.restart();
    }
    public void backBtn(ActionEvent event){
        ordersPane.setVisible(true);
        checkOutPane.setVisible(false);
    }

    //Meals Section
    public void pickMealsInList(MouseEvent event){
        Meals meals = new Meals();
        meals.selectedMealInfo(mealListView,
                mealNameLbl,
                mealPriceLbl,
                mealDescTxt,
                imgViewMeal,
                mealCompanyImg);
    }
    public void addToCart_MS(ActionEvent event){
        String mnFromLView = mealListView.getSelectionModel().getSelectedItem();
        addTCListView.getItems().add(mnFromLView);
        backgroundThread = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        McDatabase sql = new McDatabase();
                        int mealPrice = sql.getMealInfoPrice(mnFromLView);
                        total += mealPrice;
                        updateMessage("Total: P" + total);
                        return null;
                    }
                };
            }
        };
        backgroundThread.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                totalLbl.textProperty().unbind();
                if(addTCListView.getItems().size() > 0){
                    addTCListView.getSelectionModel().select(0);
                }
            }
        });
        totalLbl.textProperty().bind(backgroundThread.messageProperty());
        backgroundThread.restart();
    }

    @FXML Button closeButton;
    public void closeStage(ActionEvent event){
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
