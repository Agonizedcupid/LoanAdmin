package com.example.loanadmin.Model;

public class AdminModel {

    private String id,memberId,password,memberType,phone;

    public AdminModel(){}

    public AdminModel(String id, String memberId, String password, String memberType, String phone) {
        this.id = id;
        this.memberId = memberId;
        this.password = password;
        this.memberType = memberType;
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMemberType() {
        return memberType;
    }

    public void setMemberType(String memberType) {
        this.memberType = memberType;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
