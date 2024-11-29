package com.moment.moment_BE.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "noti")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Noti {

    /*
     * Doi tuong nay quan ly thong bao
     * Moi nguoi lam 1 hanh dong se tao ra 1 thong bao
     * */

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "message")
    private String message;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false, referencedColumnName = "id")
    @JsonBackReference
    private Account account;

    @OneToMany(mappedBy = "noti", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<NotiView> notiViews;



}
