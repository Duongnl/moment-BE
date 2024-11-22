package com.moment.moment_BE.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "noti_view")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NotiView {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "viewed_at")
    private LocalDateTime viewedAt;

    @ManyToOne
    @JoinColumn(name = "noti_id", nullable = false, referencedColumnName = "id")
    @JsonBackReference
    private Noti noti;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false, referencedColumnName = "id")
    @JsonBackReference
    private Account account;
}
