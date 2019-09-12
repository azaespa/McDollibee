package com.mcdollibee;

import javafx.scene.image.Image;

import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

class McDatabase {
    private Connection con = null;
    private Statement stmt = null;
    private PreparedStatement pstmt = null;
    private InputStream is;

    //Home Section
    private Random random = new Random();
    private String featuredMealName;
    private String featuredMealDesc;
    private int featuredMealPrice;
    private Image featuredImg;
    private Image featuredCompany;

    //Profile Section
    private int id;
    private int phoneNumber;
    private int totalOrderedPrice;
    private String firstName;
    private String lastName;
    private String address;
    private List<String> orderHistoryList = new ArrayList<>();
    private List<String> dateOrderedList = new ArrayList<>();


    //My Cart Section
    private Image mealImage;

    //Meals Section
    private int mealPrice;
    private Image mealCompany;
    private String mealName;
    private String mealDesc;
    private List<String> mealNameList = new ArrayList<>();

    //Database Connection
    McDatabase(){
        try{
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:src\\main\\resources\\db\\mcdatabase.db");
            System.out.println("Connected Successfully!");
        }catch (Exception e){
            System.out.println("Connection Failed!" + e);
        }
    }
    void disconnect(){
        try{
            con.close();
        } catch (Exception e){
            System.out.println("Closing connection failed");
        }
    }

    //Home Section
    void setRandomFeaturedMeal(){
        try{
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS rowcount FROM meals");
            rs.next();
            int count = rs.getInt("rowcount");
            rs.close();
            int rand = 1 + random.nextInt(count);
            setFeaturedMeal(rand);
        }catch (Exception e){
            System.out.println("Error 416 " + e);
        }
    }
    private void setFeaturedMeal(int mealId){
        try{
            this.stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT meal_name,meal_price,meal_image,meal_description,company" +
                    " FROM meals WHERE meal_id=" +mealId);
            featuredMealName = rs.getString("meal_name");
            featuredMealPrice = rs.getInt("meal_price");
            featuredMealDesc = rs.getString("meal_description");
            String company = rs.getString("company");
            is = rs.getBinaryStream("meal_image");
            featuredImg = new Image(is);
            featuredCompany = setCompanyLogo(company);
            is.close();
            rs.close();
            disconnect();
        } catch (Exception e){
            System.out.println("Error 415 " + e);
        }
    }
    private Image setCompanyLogo(String company){
        try{
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT logo FROM company WHERE company='" + company + "'");
            InputStream is = rs.getBinaryStream("logo");
            return new Image(is);
        }catch (Exception e){
            System.out.println("Error 417 " + e);
            return null;
        }
    }
    void setItemsToCart(String mealName){
        try {
            this.pstmt = con.prepareStatement("SELECT meal_name, meal_price FROM meals WHERE meal_name='"+mealName+"'");
            ResultSet rs = pstmt.executeQuery();
            this.featuredMealName = rs.getString("meal_name");
            this.featuredMealPrice = rs.getInt("meal_price");
            rs.close();
            disconnect();
        } catch (Exception e){
            System.out.println("Error "+ e);
        }
    }
    int getFeaturedMealPrice() { return this.featuredMealPrice; }
    String getFeaturedMealName() { return this.featuredMealName; }
    String getFeaturedMealDesc() { return this.featuredMealDesc; }
    Image getFeaturedImg(){ return this.featuredImg; }
    Image getFeaturedCompany(){ return this.featuredCompany; }

    //Profile Section
    boolean userLogin(String username,
                      String password){
        try{
            this.pstmt = con.prepareStatement("SELECT id, first_name,last_name,address FROM user WHERE user_name='" + username +
                    "' AND password= '" + password+ "'");
            ResultSet rs = pstmt.executeQuery();
            this.id = rs.getInt("id");
            this.firstName = rs.getString("first_name");
            this.lastName = rs.getString("last_name");
            this.address = rs.getString("address");
            rs.close();
            disconnect();
            return true;
        } catch (Exception e){
            System.out.println("Error 405" + e);
            return false;
        }
    }
    void userRegister(String userName,
                      String password,
                      String firstName,
                      String lastName,
                      String address,
                      int phoneNum){
        try{
            this.pstmt = con.prepareStatement("INSERT INTO user (user_name, password, first_name, last_name," +
                    "address, phone_number) VALUES(?,?,?,?,?,?)");
            pstmt.setString(1, userName);
            pstmt.setString(2, password);
            pstmt.setString(3, firstName);
            pstmt.setString(4, lastName);
            pstmt.setString(5, address);
            pstmt.setInt(6, phoneNum);
            pstmt.executeUpdate();
            disconnect();
        }
        catch (Exception e){
            System.out.println("Error 407 " + e);
        }
    }
    void setOrderHistory(int userId, String dateOrdered){
        try{
            this.pstmt = con.prepareStatement("SELECT meal_order, total_price FROM orders " +
                    "WHERE user_id="+userId+" AND date_ordered='"+dateOrdered+"'");
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                String mealsOrdered = rs.getString("meal_order").replace("[","").replace("]","");
                List<String> temp = new ArrayList<>(Arrays.asList(mealsOrdered.split(",", -1)));
                for(String i : temp){
                    orderHistoryList.add(i.trim());
                }
                totalOrderedPrice = rs.getInt("total_price");
            }
            rs.close();
            disconnect();
        } catch (Exception e){
            System.out.println("Error 418? "+ e);
        }
    }
    void setOrderHistoryDates(int userId){
        try{
            this.pstmt = con.prepareStatement("SELECT date_ordered FROM orders WHERE user_id="+userId);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                dateOrderedList.add(rs.getString("date_ordered"));
            }
            rs.close();
            disconnect();
        }catch (Exception e){
            System.out.println("Error 419 "+ e);
        }
    }
    void setUserInfo(int userId){
        try{
            this.pstmt = con.prepareStatement("SELECT first_name, last_name, address, phone_number" +
                    " FROM user WHERE id="+userId);
            ResultSet rs = pstmt.executeQuery();
            this.firstName = rs.getString("first_name");
            this.lastName = rs.getString("last_name");
            this.address = rs.getString("address");
            this.phoneNumber = rs.getInt("phone_number");
            rs.close();
            disconnect();
        }catch (Exception e){
            System.out.println("Error 420 " + e);
        }
    }
    int getId(){ return this.id; }
    int getPhoneNumber(){ return this.phoneNumber; }
    int getTotalOrderedPrice() { return this.totalOrderedPrice; }
    String getFirstName(){ return this.firstName; }
    String getLastName(){ return this.lastName; }
    String getAddress(){ return this.address; }
    String getFullName() { return this.firstName + " " + this.lastName; }
    List<String> getOrderHistory() { return this.orderHistoryList; }
    List<String> getDateOrderedList() { return this.dateOrderedList; }

    //My Cart Section
    Image imgCheckOut(String meal_name){
        try{
            this.pstmt = con.prepareStatement("SELECT meal_image FROM meals where meal_name='"+ meal_name +"'");
            ResultSet rs = pstmt.executeQuery();
            is = rs.getBinaryStream("meal_image");
            mealImage = new Image(is);
            rs.close();
            is.close();
            return mealImage;
        } catch (Exception e){
            System.out.println("Error 412 "+ e);
            return null;
        }
    }
    void userOrder(int userId,
                   String firstName,
                   String lastName,
                   String address,
                   int phoneNum,
                   String note,
                   String mealOrder,
                   int totalPrice,
                   String dateOrdered){
        try{
            this.pstmt = con.prepareStatement("INSERT INTO orders (user_id,first_name,last_name,address,phone_number,note," +
                    "meal_order,total_price,date_ordered) VALUES (?,?,?,?,?,?,?,?,?)");
            pstmt.setInt(1, userId);
            pstmt.setString(2, firstName);
            pstmt.setString(3, lastName);
            pstmt.setString(4, address);
            pstmt.setInt(5, phoneNum);
            pstmt.setString(6, note);
            pstmt.setString(7, mealOrder);
            pstmt.setInt(8, totalPrice);
            pstmt.setString(9, dateOrdered);
            pstmt.executeUpdate();
            disconnect();
        }catch (Exception e){
            System.out.println("Error 414 " + e);
        }
    }

    //Meals Section
    void getMealInfo(String meal_name){
        try{
            this.pstmt = con.prepareStatement("SELECT meal_name, meal_price, meal_image, meal_description, company" +
                    " FROM meals WHERE meal_name='" +
                    meal_name + "'");
            ResultSet rs = pstmt.executeQuery();
            mealName = rs.getString("meal_name");
            mealPrice = rs.getInt("meal_price");
            mealDesc = rs.getString("meal_description");
            String company = rs.getString("company");
            is = rs.getBinaryStream("meal_image");
            mealImage = new Image(is);
            mealCompany = setCompanyLogo(company);
            rs.close();
            is.close();
            con.close();
        } catch (Exception e){
            System.out.println("Error 409 " + e);
        }
    }
    int getMealInfoPrice(String meal_name){
        try{
            this.pstmt = con.prepareStatement("SELECT meal_price FROM meals WHERE meal_name='"+ meal_name +"'");
            ResultSet rs = pstmt.executeQuery();
            mealPrice = rs.getInt("meal_price");
            rs.close();
            con.close();
            return mealPrice;
        }catch (Exception e){
            System.out.println("Error 411 " + e);
            return 0;
        }
    }
    String getMealName(){
        return this.mealName;
    }
    String getMealPrice(){
        return String.valueOf(this.mealPrice);
    }
    String getMealDesc(){ return this.mealDesc; }
    Image getMealImage(){
        return this.mealImage;
    }
    Image getMealCompany() {return this.mealCompany;}
    List<String> getMealList(){
        try{
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT meal_name FROM meals ORDER BY meal_name ASC");
            while (rs.next()){
                mealNameList.add(rs.getString("meal_name"));
            }
            con.close();
            return this.mealNameList;
        } catch (Exception e){
            System.out.println("Error 408 " + e);
            return null;
        }
    }
}
