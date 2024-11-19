package com.example.taqsit.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class ResponseData {
    private Meta _meta;
    private Object items;

    public ResponseData(Page<?> page) {
        Pageable pageable = page.getPageable();
        this._meta = pageable.isPaged() ? new Meta(pageable.getPageNumber() + 1, pageable.getPageSize(), page.getTotalElements(), page.getTotalPages()) : null;
        this.items = page.stream().map(item -> {
            try {
                Method toDto = item.getClass().getDeclaredMethod("toDto");
                return toDto.invoke(item);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                log.warn(e.getMessage());
                return item;
            }
        });
    }

    public ResponseData(Page<?> page, String dtoMethodName) {
        this._meta = new Meta(page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
        this.items = page.stream().map(item -> {
            try {
                Method toDto = item.getClass().getDeclaredMethod(dtoMethodName);
                return toDto.invoke(item);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                log.warn(e.getMessage());
                return item;
            }
        });
    }
}
