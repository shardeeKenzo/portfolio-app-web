package com.example.portfolioappprog5resit.api.mapping;

import com.example.portfolioappprog5resit.api.dto.StockDto;
import com.example.portfolioappprog5resit.domain.Stock;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StockMapper {
    public StockDto toDto(Stock s) {
        return new StockDto(
                s.getId(), s.getSymbol(), s.getCompanyName(),
                s.getCurrentPrice(), s.getSector(), s.getListedDate(), s.getImageURL()
        );
    }
    public List<StockDto> toDtoList(List<Stock> in) {
        return in.stream().map(this::toDto).toList();
    }
}
