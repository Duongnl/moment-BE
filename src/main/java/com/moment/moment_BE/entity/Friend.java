package com.moment.moment_BE.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "friend")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Friend {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "status")
    private String status;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
    @JsonBackReference
    private Account user;

    @ManyToOne
    @JoinColumn(name = "friend_id", nullable = false, referencedColumnName = "id")
    @JsonBackReference
    private Account friend;

    @ManyToOne
    @JoinColumn(name = "initiator_id", nullable = false, referencedColumnName = "id")
    @JsonBackReference
    private Account initiator;



}
