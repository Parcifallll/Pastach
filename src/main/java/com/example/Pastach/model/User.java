package com.example.Pastach.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.Objects;

public class User {
    private String email;
    private String nickname;

    @JsonFormat(pattern = "dd.MM.yyyy") // we should add a dependency
    private LocalDate birthday;

    public User(String email, String nickname, LocalDate birthday) {
        this.email = email;
        this.nickname = nickname;
        this.birthday = birthday;
    }

    public User() {

    }


    public String getEmail() {
        return email;
    }

    public String getNickname() {
        return nickname;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj != null && obj.getClass() == User.class) {
            User user = (User) obj;
            return (this.email.equals(user.email));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}

