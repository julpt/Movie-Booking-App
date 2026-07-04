package org.example.movie_booking.model.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cinemas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cinema {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // "Cinema City AFI Cotroceni"

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String address;

}