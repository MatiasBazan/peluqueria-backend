package com.mab.peluqueria.cliente;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "clientes", uniqueConstraints = @UniqueConstraint(columnNames = "telefono"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false, unique = true, length = 30)
    private String telefono;

    @Column(length = 120)
    private String email;
}
