package com.gmail.ia.reader.infraestructure.models.aux;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailContact {
    private String name;
    private String email;
}

