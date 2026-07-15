package com.beeline.sms.entity;

import com.beeline.sms.enums.Gender;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "students",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_students_branch_studentid",
                columnNames = {"branch_id", "student_id_no"}
        )
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @Column(name = "student_id_no", nullable = false) // unique per-branch only now — see @Table constraint above
    private String studentIdNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Branch branch;

    @Builder.Default
    private LocalDate enrollmentDate = LocalDate.now();

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "telephone", length = 20)
    private String telephone;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 10)
    private Gender gender;

    @Column(name = "nic", length = 20, unique = true)
    private String nic;
}