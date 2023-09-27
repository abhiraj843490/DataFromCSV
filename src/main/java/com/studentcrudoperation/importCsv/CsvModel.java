package com.studentcrudoperation.importCsv;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CsvModel {
    @Id
    private Long id;
    private String date;
    private Double open;
    private Double high;
    private Double low;
    private Double close;
    private Integer volume;
}


