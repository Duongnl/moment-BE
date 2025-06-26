package com.moment.moment_BE.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fcm_token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FcmTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "token")
    private String token;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
}
