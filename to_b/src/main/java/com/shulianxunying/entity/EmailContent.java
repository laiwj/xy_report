package com.shulianxunying.entity;

/**
 * Created by SuChang on 2017/5/26 11:28.
 */
public class EmailContent {

    public static String mail_url = "";

    private String email;
    private String subject;
    private String content;

    public EmailContent() {
    }


    public EmailContent init(String mail_url) {
        EmailContent.mail_url = mail_url;
        return this;
    }

    public EmailContent(String email, String subject, String content) {
        this.email = email;
        this.subject = subject;
        this.content = content;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
