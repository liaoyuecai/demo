package com.demo.core.dto;

import com.demo.core.exception.ErrorCode;
import com.demo.core.exception.GlobalException;
import lombok.Data;
import org.springframework.data.domain.Sort;

@Data
public class PageOrder {
    String direction;
    String columnName;

    Sort.Order convert() {
        return switch (this.direction) {
            case "asc" -> new Sort.Order(Sort.Direction.ASC, this.columnName);
            case "desc" -> new Sort.Order(Sort.Direction.DESC, this.columnName);
            default ->
                    throw new GlobalException(ErrorCode.PARAMS_ERROR, "Unexpected sort order direction value: " + this.direction);
        };
    }
}
