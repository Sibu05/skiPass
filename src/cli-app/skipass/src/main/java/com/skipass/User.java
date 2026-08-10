package com.skipass;

public class User {
    private String email;
    private String username;
    private char password[];

    public User(String email, String username, char passwords[]){
        this.email = email;
        this.username = username;
        this.password = passwords;

    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getEmail(){
        return email;
    }
    public String getUsername(){
        return username;
    }


}
