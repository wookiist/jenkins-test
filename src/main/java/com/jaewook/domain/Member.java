package com.jaewook.domain;

import java.util.Date;
import lombok.*;
import org.hibernate.validator.constraints.Email;

@Data
public class Member {
    private Integer idx;

    private String userId;

    private String userPw;

    private String userName;

    private String userEmail;

    private Boolean loginYn;

    private Integer loginCount;

    private Date regDate;

    private Date updDate;

    public Member(Integer idx, String userId, String userPw,
                  String userName, String userEmail, Boolean loginYn,
                  Integer loginCount, Date regDate, Date updDate) {
        this.idx = idx;
        this.userId = userId;
        this.userPw = userPw;
        this.userName = userName;
        this.userEmail = userEmail;
        this.loginYn = loginYn;
        this.loginCount = loginCount;
        this.regDate = regDate;
        this.updDate = updDate;
    }

    public Member(String userId, String userPw,
                  String userName, String userEmail) {
        Date now = new Date();
        this.userId = userId;
        this.userPw = userPw;
        this.userName = userName;
        this.userEmail = userEmail;
        this.loginYn = false;
        this.loginCount = 0;
        this.regDate = now;
        this.updDate = now;
    }

    public Integer getIdx() {
        return idx;
    }

    public void setIdx(Integer idx) {
        this.idx = idx;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    public String getUserPw() {
        return userPw;
    }

    public void setUserPw(String userPw) {
        this.userPw = userPw == null ? null : userPw.trim();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail == null ? null : userEmail.trim();
    }

    public Boolean getLoginYn() {
        return loginYn;
    }

    public void setLoginYn(Boolean loginYn) {
        this.loginYn = loginYn;
    }

    public Integer getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(Integer loginCount) {
        this.loginCount = loginCount;
    }

    public void incLoginCount() {
        this.loginCount += 1;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public Date getUpdDate() {
        return updDate;
    }

    public void setUpdDate(Date updDate) {
        this.updDate = updDate;
    }
}