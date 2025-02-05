package com.moment.moment_BE.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "photo")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Photo {

    /*
     * Doi tuong nay chua thong tin anh
     * */

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "slug")
    private String slug;

    @Column(name = "url")
    private String url;

    @Column(name = "caption")
    private String caption;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "status")
    private int status;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false, referencedColumnName = "id")
    @JsonBackReference
    private Account account;

    @OneToOne(mappedBy = "photo")
    @JsonBackReference
    private Noti noti;

    @PrePersist
    protected void onCreate() {
        this.slug = UUID.randomUUID().toString();
    }
}
