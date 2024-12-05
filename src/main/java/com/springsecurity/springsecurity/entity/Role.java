package com.springsecurity.springsecurity.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Table(name = "ROLES", uniqueConstraints = {@UniqueConstraint(columnNames = "name")})
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @Column(name = "NAME", unique = true)
    private String name;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

}