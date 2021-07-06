package ua.training.dto;

import java.util.List;

public class BookTranslateContainerDto {

    private List<BookTranslateDto> dtoList;

    public BookTranslateContainerDto(List<BookTranslateDto> dtoHashMap) {
        this.dtoList = dtoHashMap;
    }

    public List<BookTranslateDto> getDtoList() {
        return dtoList;
    }

    public void setDtoList(List<BookTranslateDto> dtoList) {
        this.dtoList = dtoList;
    }
}
