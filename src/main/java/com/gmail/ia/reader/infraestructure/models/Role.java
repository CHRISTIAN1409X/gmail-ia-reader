package com.gmail.ia.reader.infraestructure.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "role_seq")
    @SequenceGenerator(
            name = "role_seq",
            sequenceName = "role_id_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(name = "name", unique = true,length = 50, nullable = false)
    private String name;
}