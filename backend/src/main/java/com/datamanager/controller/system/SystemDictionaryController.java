package com.datamanager.controller.system;

import com.datamanager.dto.ApiResponse;
import com.datamanager.model.Dictionary;
import com.datamanager.model.DictionaryItem;
import com.datamanager.repository.DictionaryItemRepository;
import com.datamanager.repository.DictionaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system/dictionary")
public class SystemDictionaryController {

    @Autowired
    private DictionaryRepository dictionaryRepository;

    @Autowired
    private DictionaryItemRepository dictionaryItemRepository;

    @GetMapping
    public ApiResponse<Page<Dictionary>> getDictionaries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
        Page<Dictionary> dictionaries = dictionaryRepository.findAll(pageable);
        return ApiResponse.success(dictionaries);
    }

    @PostMapping
    public ApiResponse<Void> saveDictionary(@RequestBody Dictionary dictionary) {
        dictionaryRepository.save(dictionary);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteDictionary(@PathVariable Long id) {
        dictionaryRepository.deleteById(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/{dictionaryId}/items")
    public ApiResponse<List<DictionaryItem>> getDictionaryItems(@PathVariable Long dictionaryId) {
        List<DictionaryItem> items = dictionaryItemRepository.findByDictionaryId(dictionaryId);
        return ApiResponse.success(items);
    }

    @PostMapping("/item")
    public ApiResponse<Void> saveDictionaryItem(@RequestBody DictionaryItem item) {
        dictionaryItemRepository.save(item);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/item/{id}")
    public ApiResponse<Void> deleteDictionaryItem(@PathVariable Long id) {
        dictionaryItemRepository.deleteById(id);
        return ApiResponse.success(null);
    }
}
