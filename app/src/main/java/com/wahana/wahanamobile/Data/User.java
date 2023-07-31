package com.wahana.wahanamobile.Data;

/**
 * Created by Sadewa on 6/27/2016.
 */
public class User {

    int _id_user;
    String _username, _name, _maingroup, _sessioid, _employeecode, _lastLogin;
    Double _officelat, _officelong;

    public User(){

    }

    public User(String username, String name, String maingroup, String sessioid, String employeecode){
        this._username = username;
        this._name = name;
        this._maingroup = maingroup;
        this._sessioid = sessioid;
        this._employeecode = employeecode;
    }

    public User(String employeeCode, String lastLogin){
        this._lastLogin = lastLogin;
        this._employeecode = employeeCode;
    }

    public User(Double officelat, Double officelong){
        this._officelat = officelat;
        this._officelong = officelong;
    }

    public User(String sessionid){
        this._sessioid = sessionid;
    }

    public int getIDuser(){
        return this._id_user;
    }

    public void setIDuser(int id_user){
        this._id_user = id_user;
    }

    public String getUsername(){
        return this._username;
    }

    public void setUsername(String username){
        this._username = username;
    }

    public String getName(){
        return this._name;
    }

    public void setName(String name){
        this._name = name;
    }

    public String getMaingroup(){
        return this._username;
    }

    public void setMaingroup(String maingroup){
        this._maingroup = maingroup;
    }

    public String getSessionid(){
        return this._sessioid;
    }

    public void setSessionid(String sessionid){
        this._sessioid = sessionid;
    }

    public Double getOfficelat(){
        return this._officelat;
    }

    public void setOfficelat(Double officelat){
        this._officelat = officelat;
    }

    public Double getOfficelong(){
        return this._officelong;
    }

    public void setOfficelong(Double officelong){
        this._officelong = officelong;
    }

    public String getLastLogin(){
        return this._lastLogin;
    }

    public void setLastLogin(String lastLogin){
        this._lastLogin = lastLogin;
    }

    public String getEmployeecode(){
        return this._employeecode;
    }

    public void setEmployeecode(String employeecode){
        this._employeecode = employeecode;
    }
}
