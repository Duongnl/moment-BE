package com.moment.moment_BE.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "profile")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Profile {

    /*
     * Doi tuong nay quan ly thong tin ca nhan
     * Moi quan he 1 1 voi tai khoan
     * */

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "sex")
    private String sex;

    @Column(name = "birthday", columnDefinition = "date")
    private LocalDate birthday;

    @Column(name = "address")
    private String address;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    @JsonManagedReference
    private Account account;

}
