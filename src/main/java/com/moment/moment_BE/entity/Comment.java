package com.moment.moment_BE.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "comment")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Comment {

    /*
     * Doi tuong nay chua thong tin anh
     * */

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //    Noi dung comment
    @Column(name = "content" ,columnDefinition = "TEXT", nullable = false)
    private String content;

    //    Thoi gian comment
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    //    Trang thai
    @Column(name = "status", nullable = false)
    private String status;

    //    Khoa hoc cua comment
    @ManyToOne
    @JoinColumn(name = "photo_id", nullable = false, referencedColumnName = "id")
    @JsonBackReference
    private Photo photo ;

    //    Tai khoan comment
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false, referencedColumnName = "id")
    @JsonBackReference
    private Account account;

    //    Comment duoc tra loi
    @ManyToOne
    @JoinColumn(name = "comment_id ", nullable = true, referencedColumnName = "id")
    @JsonBackReference
    private Comment parentComment;

//    //    Tat ca cac comment da tra loi
    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<Comment> commentReplies;

}
