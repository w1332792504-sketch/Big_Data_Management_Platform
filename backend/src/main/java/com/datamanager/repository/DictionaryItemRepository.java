package com.datamanager.repository;

import com.datamanager.model.DictionaryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DictionaryItemRepository extends JpaRepository<DictionaryItem, Long> {
    List<DictionaryItem> findByDictionaryId(Long dictionaryId);
}
