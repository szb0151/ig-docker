package edu.au.cc.gallery;

import edu.au.cc.gallery.tools.Secrets;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import static spark.Spark.*;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;
import spark.Request;
import spark.Response;

public class UserAdmin {



   public void addRoutes() {

    get("/admin", (req,res) -> admin(req, res));
    get("/admin/addUser", (req, res) -> addUserPage(req, res));
    post("/admin/addUser/add", (req, res) -> addUser(req, res));
    get("/admin/editUser/:username", (req, res) -> editUserPage(req, res));
    post("/admin/editUser/:username", (req, res) -> editUser(req, res));
    get("/admin/deleteUser/:username", (req, res) -> deleteUserPage(req, res));
    post("/admin/deleteUser/:username", (req, res) -> deleteUser(req, res));

   }

   public String editUserPage(Request req, Response res) {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("username", req.params(":username"));
        return new HandlebarsTemplateEngine()
                .render(new ModelAndView(model, "editUser.hbs"));
   }

   public String editUser(Request req, Response res) throws SQLException {
    if (req.queryParams("password").isEmpty() && req.queryParams("fullName").isEmpty()) {
      res.redirect("/admin");
      return "";
    }
    UserAdmin.updateUserToDB(req.params(":username"), req.queryParams("password"), req.queryParams("fullName"));
    return "<!DOCTYPE html><html><head><meta charset=\"utf-8\"/></head><body><p><a href=\"/admin\">Return to Users</a></p>"
           +  "Updated user " + req.params(":username") + ".</body></html>";

   }

   public static void updateUserToDB(String username, String password, String fullName) throws SQLException {
    DB db = new DB();
    db.connect();
    String query = "select password, full_name from users where username=?";
             try {
                 ResultSet rs = db.executeQuery(query, new String[] {username});
                 if (!rs.isBeforeFirst()) {
                         System.out.println("\nNo such user.");
                 }

                 while (rs.next()) {
                         if (password.isEmpty()) {
                          password = rs.getString(2);
                         }
                         if (fullName.isEmpty()) {
                          fullName = rs.getString(3);
                         }
                         db.execute("update users set password=?, full_name=? where username=?",
                         new String[] {password, fullName, username});
                         }
                       } catch (SQLException ex) {
                         System.err.println("\nNo such user.");
                       }

   }


   public String deleteUserPage(Request req, Response res) {
        Map<String, Object> model = new HashMap<String, Object>();
	model.put("username", req.params(":username"));
        return new HandlebarsTemplateEngine()
                .render(new ModelAndView(model, "deleteUser.hbs"));
    }



   public String deleteUser(Request req, Response res) throws SQLException {
    if (req.queryParams().contains("No")) {
      res.redirect("/admin");
      return "";
    }
    UserAdmin.deleteUserInDB(req.params(":username"), req.queryParams("password"), req.queryParams("fullName"));
    return "<!DOCTYPE html><html><head><meta charset=\"utf-8\"/></head><body><p><a href=\"/admin\">Return to Users</a></p>"
           +  "Deleted user " + req.params(":username") + ".</body></html>";
   }

  public static void deleteUserInDB(String username, String password, String fullName) throws SQLException{
   DB db = new DB();
   db.connect();
   String query = "select password, full_name from users where username=?";
   ResultSet rs = db.executeQuery(query, new String[] {username});

   if (!rs.isBeforeFirst()) {
     System.out.println("\nNo such user.");
     return;
   }

   try {
     db.execute("delete from users where username=?", new String[] {username});
   } catch (SQLException ex) {
     System.err.println("\nNo such user.");
   }
  }

   public String addUser(Request req, Response res) throws SQLException {
    try {
      UserAdmin.addUserToDB(req.queryParams("username"), req.queryParams("password"), req.queryParams("fullName"));
      return "<!DOCTYPE html><html><head><meta charset=\"utf-8\"/></head><body><p><a href=\"/admin\">Return to Users</a></p>"
             + "Added user " + req.queryParams("username") + "</body></html>";
    } catch (SQLException ex) {
      return "<!DOCTYPE html><html><head><meta charset=\"utf-8\"/></head><body><p><a href=\"/admin\">Return to Users</a></p>"
             + req.queryParams("username") + " alread exists.</body></html>";
   }
   }


   public String addUserPage(Request req, Response res) {
        Map<String, Object> model = new HashMap<String, Object>();
        return new HandlebarsTemplateEngine()
                .render(new ModelAndView(model, "addUser.hbs"));
   }


   public static void addUserToDB(String username, String password, String fullName) throws SQLException {
    DB db = new DB();
    db.connect();
    db.execute("insert into users values(?, ?, ?)",
                new String[] {username, password, fullName});
   }




   public String admin(Request req, Response res) throws SQLException {
    List<String> userList = getAllUsers();
    Map<String, Object> model = new HashMap<String, Object>();
    model.put("users", userList);
    return new HandlebarsTemplateEngine()
                .render(new ModelAndView(model, "admin.hbs"));
   }

   public ArrayList getAllUsers() throws SQLException {
    DB db = new DB();
    db.connect();
    ArrayList<String> users = new ArrayList<String>();
    ResultSet rs = db.executeQuery("select username from users");
    while(rs.next()) {
        users.add(rs.getString(1));
    }
    rs.close();
    db.close();
    return users;
   }






}
