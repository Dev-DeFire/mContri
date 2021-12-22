package com.executivestrokes.mcent;
public class userprofile {
    String userName,phone, UID,image,mail,upiId;

    public userprofile() {
    }

    public userprofile(String userName, String phone, String UID,String image,String mail, String upiId) {
        this.userName = userName;
        this.phone = phone;
        this.UID = UID;
        this.image = image;
        this.mail=mail;
        this.upiId=upiId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getUpiId() {
        return upiId;
    }

    public void setUpiId(String upiId) {
        this.upiId = upiId;
    }
}