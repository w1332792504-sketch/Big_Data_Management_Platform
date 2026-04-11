package com.datamanager.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "t_dictionary_item")
public class DictionaryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long dictionaryId;

    @Column(nullable = false)
    private String label;

    @Column(nullable = false)
    private String value;

    private Integer sort = 0;

    @Column(nullable = false)
    private Integer status = 1;
}
