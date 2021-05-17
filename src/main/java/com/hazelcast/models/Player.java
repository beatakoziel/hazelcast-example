package com.hazelcast.models;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Player implements Serializable {
    private String firstname;
    private String surname;
    private SportClub club;
}
