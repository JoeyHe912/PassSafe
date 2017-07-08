package com.joeyhe.passwordmanager.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

import java.util.Date;

/**
 * Created by HGY on 2017/6/26.
 */
@Entity
public class PasswordNote {
    @Id
    private Long id;
    @NotNull
    private String name;
    private String webSite;
    private String userName;
    private String password;
    private String note;
    private Date createdDate;
    private Date modifiedDate;
    private Date accessedDate;
    private Boolean notFavorite = true;
    private Boolean notLetter = true;

    @Generated(hash = 1552814791)
    public PasswordNote(Long id, @NotNull String name, String webSite,
                        String userName, String password, String note, Date createdDate,
                        Date modifiedDate, Date accessedDate, Boolean notFavorite,
                        Boolean notLetter) {
        this.id = id;
        this.name = name;
        this.webSite = webSite;
        this.userName = userName;
        this.password = password;
        this.note = note;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.accessedDate = accessedDate;
        this.notFavorite = notFavorite;
        this.notLetter = notLetter;
    }
    @Generated(hash = 1888814951)
    public PasswordNote() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
        this.notLetter = !Character.isLetter(name.charAt(0));
    }
    public String getWebSite() {
        return this.webSite;
    }
    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }
    public String getUserName() {
        return this.userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getPassword() {
        return this.password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getNote() {
        return this.note;
    }
    public void setNote(String note) {
        this.note = note;
    }
    public Date getCreatedDate() {
        return this.createdDate;
    }
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
    public Date getModifiedDate() {
        return this.modifiedDate;
    }
    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }
    public Date getAccessedDate() {
        return this.accessedDate;
    }
    public void setAccessedDate(Date accessedDate) {
        this.accessedDate = accessedDate;
    }

    public Boolean getNotFavorite() {
        return this.notFavorite;
    }

    public void setNotFavorite(Boolean notFavorite) {
        this.notFavorite = notFavorite;
    }

    public Boolean getNotLetter() {
        return this.notLetter;
    }

    public void setNotLetter(Boolean notLetter) {
        this.notLetter = notLetter;
    }
}