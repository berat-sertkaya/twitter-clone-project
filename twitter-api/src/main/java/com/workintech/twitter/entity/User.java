package com.workintech.twitter.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User extends EntityBase {

    @NotBlank(message = "Kullanıcı adı boş olamaz")
    @Size(min = 3, max = 30, message = "Kullanıcı adı 3-30 karakter olmalı")
    @Column(nullable = false, unique = true, length = 30)
    private String username;

    @NotBlank(message = "Email boş olamaz")
    @Email(message = "Geçerli bir email giriniz")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Şifre boş olamaz")
    @Column(nullable = false)
    private String password;

    @Past(message = "Doğum tarihi geçmişte olmalı")
    private LocalDate birthday;

    @Size(max = 100)
    private String location;

    @Size(max = 160, message = "Bio en fazla 160 karakter olabilir")
    private String bio;

    private String photo;

    private String banner;
}
