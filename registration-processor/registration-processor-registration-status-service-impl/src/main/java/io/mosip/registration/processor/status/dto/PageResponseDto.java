package io.mosip.registration.processor.status.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResponseDto<T> {
    private long fromRecord;
    private long toRecord;
    private long totalRecord;
    @Valid
    private List<T> data;
}
