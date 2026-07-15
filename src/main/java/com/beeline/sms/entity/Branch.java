package com.beeline.sms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "branches")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String duration;

    private String schedule;

    private String color;

    @Column(nullable = false)
    private Double totalFee;

    @Column(nullable = false)
    private Integer installmentsCount;

    private String dueDayValue;

    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Student> students = new ArrayList<>();

    @ManyToMany(mappedBy = "branches")
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Staff> staffMembers = new ArrayList<>();
}
