/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package com.exavalu.services;

import com.exavalu.models.Appointment;
import com.exavalu.models.Users;
import static com.exavalu.services.AdminService.close;
import com.exavalu.utils.JDBCConnectionManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.struts2.dispatcher.SessionMap;

/**
 *
 * @author anich
 */
public class LoginService {

    /**
     *
     */
    public static LoginService loginService = null;

    /**
     *
     */
    public static Logger log = Logger.getLogger(LoginService.class.getName());

    private LoginService() {
    }

    /**
     *
     * @return
     */
    public static synchronized LoginService getInstance() {
        if (loginService == null) {
            loginService=new LoginService();
        } 
            return loginService;
        
    }

    /**
     *
     * Used to check user credentials with the database.
     *
     * @param user
     * @return
     */
    public boolean doLogin(Users user) {

        boolean result = false;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
             con = JDBCConnectionManager.getConnection();
            String sql = "Select * from users right join role on role.roleId=users.roleId where emailAddress=? and password=? ";
             ps = con.prepareStatement(sql);
            String passoword = MD5.getMd5(user.getEmailAddress()+user.getPassword());
            ps.setString(1, user.getEmailAddress());
            ps.setString(2, passoword);

            System.out.println("LoginService :: " + ps);

             rs = ps.executeQuery();

            if (rs.next()) {
                user.setRoleId(rs.getString("roleId"));
                user.setDoctorId(rs.getString("doctorId"));
                user.setPatientId(rs.getString("patientId"));
                user.setUserId(rs.getString("userId"));
                user.setFirstName(rs.getString("firstName"));
                user.setLastName(rs.getString("lastName"));

                result = true;
            }

        } catch (SQLException ex) {
            if (log.isEnabledFor(Level.ERROR)) {
                String errorMessage = "Error message: " + ex.getMessage() + " | Date: " + new Date();
                log.error(errorMessage);
            }
        }finally {

            close(rs, ps, con);
        }
        return result;
    }

    /**
     *
     * Used to signup or add new user details to the database.
     *
     * @param user
     * @return
     */
    public boolean doSignUp(Users user) {

        boolean result = false;
        Connection con = null;
        PreparedStatement preparedStatement = null;
       
        String sql = "INSERT INTO users(emailAddress,password,firstName,lastName,occupation,address,gender,dateOfRegisteration)" + "VALUES(? ,? ,? ,? ,?,?,?,CURDATE())";

        try {
             con = JDBCConnectionManager.getConnection();
            String passoword = MD5.getMd5(user.getEmailAddress()+user.getPassword());
             preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, user.getEmailAddress());
            preparedStatement.setString(2, passoword);
            preparedStatement.setString(3, user.getFirstName());
            preparedStatement.setString(4, user.getLastName());
            preparedStatement.setString(5, user.getOccupation());
            preparedStatement.setString(6, user.getAddress());
            preparedStatement.setString(7, user.getGender());

            System.out.println(preparedStatement);
            int res = preparedStatement.executeUpdate();

            if (res == 1) {
                result = true;
            }

        } catch (SQLException ex) {
            if (log.isEnabledFor(Level.ERROR)) {
                String errorMessage = "Error message: " + ex.getMessage() + " | Date: " + new Date();
                log.error(errorMessage);
            }
        }finally {

            close(null, preparedStatement, con);
        }

        return result;

    }

    /**
     *
     * Used to check if an user already exsists in the database.
     *
     * @param emailAddress
     * @param sessionMap
     * @return
     */
    public boolean doExsists(String emailAddress, SessionMap sessionMap) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        boolean result = false;
        Users users = new Users();

        try {
             con = JDBCConnectionManager.getConnection();
            String sql = "Select * from users right join role on role.roleId=users.roleId where emailAddress=?";
             ps = con.prepareStatement(sql);
            ps.setString(1, emailAddress);

            System.out.println("LoginService :: " + ps);

             rs = ps.executeQuery();

            if (rs.next()) {
                users.setEmailAddress(emailAddress);
                users.setPassword(rs.getString("password"));
                users.setUserId(rs.getString("userId"));
                sessionMap.put("Patient", users);

                result = true;
            }

        } catch (SQLException ex) {
            if (log.isEnabledFor(Level.ERROR)) {
                String errorMessage = "Error message: " + ex.getMessage() + " | Date: " + new Date();
                log.error(errorMessage);
            }
        }finally {

            close(rs, ps, con);
        }

        return result;
    }

    /**
     *
     * Used to add the user's who sign up using social media to the database
     *
     * @param user
     * @return
     */
    public boolean doSocialSignUp(Users user) {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        boolean result = false;
        String sql = "INSERT INTO users(emailAddress,password,firstName,lastName,dateOfRegisteration)" + "VALUES(? ,? ,? ,? ,CURDATE())";

        try {
             con = JDBCConnectionManager.getConnection();
             String passoword = MD5.getMd5(user.getEmail()+user.getSub());

            preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, user.getEmail());
            preparedStatement.setString(2, passoword);
            preparedStatement.setString(3, user.getGiven_name());
            preparedStatement.setString(4, user.getFamily_name());

            System.out.println(preparedStatement);
            int res = preparedStatement.executeUpdate();

            if (res == 1) {
                result = true;
            }

        } catch (SQLException ex) {
            if (log.isEnabledFor(Level.ERROR)) {
                String errorMessage = "Error message: " + ex.getMessage() + " | Date: " + new Date();
                log.error(errorMessage);
            }
        }finally {

            close(null, preparedStatement, con);
        }

        return result;

    }

    

    /**
     *
     * @param appointment
     */
    public void updateUser(Appointment appointment) {
        Connection con = null;
        PreparedStatement preparedStatement = null;

        //boolean result = false;
        try {
            String sql = "update users set patientId=? where emailAddress=? and userId=?";
             con = JDBCConnectionManager.getConnection();

             preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(2, appointment.getEmailAddress());
            preparedStatement.setString(1, appointment.getPatientId());
            preparedStatement.setString(3, appointment.getUserId());

            System.out.println(preparedStatement);
             preparedStatement.executeUpdate();

//            if (res == 1) {
//               // result = true;
//            }

        } catch (SQLException ex) {
            if (log.isEnabledFor(Level.ERROR)) {
                String errorMessage = "Error message: " + ex.getMessage() + " | Date: " + new Date();
                log.error(errorMessage);
            }
        }finally {

            close(null, preparedStatement, con);
        }

    }

    /**
     *
     * Used to verify the user's who Log In using social media with the
     * information in the database.
     *
     * @param user
     * @return
     */
    public boolean doSocialLogIn(Users user) {
        boolean result = false;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        

        try {
             con = JDBCConnectionManager.getConnection();
             String passoword = MD5.getMd5(user.getEmail()+user.getSub());
            String sql = "Select * from users right join role on role.roleId=users.roleId where emailAddress=? and password=? ";
             ps = con.prepareStatement(sql);
            ps.setString(1, user.getEmail());
            ps.setString(2, passoword);

            System.out.println("LoginService :: " + ps);

             rs = ps.executeQuery();

            if (rs.next()) {
                user.setRoleId(rs.getString("roleId"));
                user.setDoctorId(rs.getString("doctorId"));
                user.setPatientId(rs.getString("patientId"));
                user.setUserId(rs.getString("userId"));
                user.setEmailAddress(rs.getString("emailAddress"));

                result = true;
            }

        } catch (SQLException ex) {
            if (log.isEnabledFor(Level.ERROR)) {
                String errorMessage = "Error message: " + ex.getMessage() + " | Date: " + new Date();
                log.error(errorMessage);
            }
        }finally {

            close(rs, ps, con);
        }
        return result;
    }

    public boolean updatePassword(Users user) {
        Connection con = null;
        PreparedStatement preparedStatement = null;
       // ResultSet rs = null;

        boolean result = false;
        try {
            String sql = "update users set password=? where emailAddress=? ";
             con = JDBCConnectionManager.getConnection();
            String passoword = MD5.getMd5(user.getEmailAddress()+user.getPassword());

             preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, passoword);
            preparedStatement.setString(2, user.getEmailAddress());
            System.out.println(preparedStatement);
            int res = preparedStatement.executeUpdate();

            if (res == 1) {
                result = true;
            }
        } catch (SQLException ex) {
            if (log.isEnabledFor(Level.ERROR)) {
                String errorMessage = "Error message: " + ex.getMessage() + " | Date: " + new Date();
                log.error(errorMessage);
            }
        }finally {

            close(null, preparedStatement, con);
        }
        return result;
    }
    
     public boolean doInternalLogin(Users user) {
         Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        boolean result = false;

        try {
             con = JDBCConnectionManager.getConnection();
            String sql = "Select * from users right join role on role.roleId=users.roleId where emailAddress=? and password=? ";
             ps = con.prepareStatement(sql);
            ps.setString(1, user.getEmailAddress());
            ps.setString(2, user.getPassword());

            System.out.println("LoginService :: " + ps);

             rs = ps.executeQuery();

            if (rs.next()) {
                user.setRoleId(rs.getString("roleId"));
                user.setDoctorId(rs.getString("doctorId"));
                user.setPatientId(rs.getString("patientId"));
                user.setUserId(rs.getString("userId"));
                user.setFirstName(rs.getString("firstName"));
                user.setLastName(rs.getString("lastName"));

                result = true;
            }

        } catch (SQLException ex) {
           if (log.isEnabledFor(Level.ERROR)) {
                String errorMessage = "Error message: " + ex.getMessage() + " | Date: " + new Date();
                log.error(errorMessage);
            }
        }finally {

            close(rs, ps, con);
        }
        return result;
    }

}
