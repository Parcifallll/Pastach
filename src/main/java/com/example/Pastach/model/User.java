package com.example.Pastach.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"email"})
public class User {
    private String email;
    private String nickname;

    @JsonFormat(pattern = "dd.MM.yyyy") // we should add a dependency
    private LocalDate birthday;

/*    @Override
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
        return Objects.hash(email); */
}

