package com.wahana.wahanamobile.Data;

/**
 * Created by Sadewa on 8/31/2016.
 */
public class OfflineTTK {
    String ttk, sessionid, employeecode, requestid, statusdate, packagestatus, reason, receivername, receivertype, comment, statusttk, messagesoap;
    String latitude, longitude;

    public OfflineTTK(){

    }

    public OfflineTTK(String ttk){
        this.ttk = ttk;
    }

    public OfflineTTK(String statusttk, String messagesoap){
        this.statusttk = statusttk;
        this.messagesoap = messagesoap;
    }

    public OfflineTTK(String ttk, String sessioid, String requestid, String employeecode, String statusdate, String packagestatus, String reason,
                      String receivername, String receivertype, String comment, String latitude, String longitude, String statusttk, String messagesoap){
        this.ttk = ttk;
        this.sessionid = sessioid;
        this.requestid = requestid;
        this.employeecode = employeecode;
        this.statusdate = statusdate;
        this.packagestatus = packagestatus;
        this.reason = reason;
        this.receivername = receivername;
        this.receivertype = receivertype;
        this.comment = comment;
        this.latitude = latitude;
        this.longitude = longitude;
        this.statusttk = statusttk;
        this.messagesoap = messagesoap;
    }

    public String getTTK(){
        return this.ttk;
    }

    public void setTTK(String ttk){
        this.ttk = ttk;
    }

    public String getSessionid(){
        return this.sessionid;
    }

    public void setSessionid(String sessionid){
        this.sessionid = sessionid;
    }

    public String getRequestid(){
        return this.requestid;
    }

    public void setRequestid(String requestid){
        this.requestid = requestid;
    }

    public String getEmployeecode(){
        return this.employeecode;
    }

    public void setEmployeecode(String employeecode){
        this.employeecode = employeecode;
    }

    public String getStatusdate(){
        return this.statusdate;
    }

    public void setStatusdate(String statusdate){
        this.statusdate = statusdate;
    }

    public String getPackagestatus(){
        return this.packagestatus;
    }

    public void setPackagestatus(String packagestatus){
        this.packagestatus = packagestatus;
    }

    public String getReason(){
        return this.reason;
    }

    public void setReason(String reason){
        this.reason = reason;
    }

    public void setReceivername(String receivername){
        this.receivername = receivername;
    }

    public String getReceivername(){
        return this.receivername;
    }

    public String getReceivertype(){
        return this.receivertype;
    }

    public void setReceivertype(String receivertype){
        this.receivertype = receivertype;
    }

    public String getComment(){
        return this.comment;
    }

    public void setComment(String comment){
        this.comment = comment;
    }

    public String getLatitude(){
        return this.latitude;
    }

    public void setLatitude(String latitude){
        this.latitude = latitude;
    }

    public String getLongitude(){
        return this.longitude;
    }

    public void setLongitude(String longitude){
        this.longitude = longitude;
    }

    public String getStatusTTK(){
        return this.statusttk;
    }

    public void setStatusTTK(String statusttk){
        this.statusttk = statusttk;
    }

    public String getMessageSoap(){
        return this.messagesoap;
    }

    public void setMessageSoap(String messageSoap){
        this.messagesoap = messageSoap;
    }
}
