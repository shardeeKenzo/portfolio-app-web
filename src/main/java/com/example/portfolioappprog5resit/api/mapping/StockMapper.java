package com.example.portfolioappprog5resit.api.mapping;

import com.example.portfolioappprog5resit.api.dto.NewStockDto;
import com.example.portfolioappprog5resit.api.dto.StockDto;
import com.example.portfolioappprog5resit.api.dto.UpdateStockDto;
import com.example.portfolioappprog5resit.domain.Stock;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StockMapper {

    StockDto toDto(Stock s);
    List<StockDto> toDtoList(List<Stock> in);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "brokerageAccounts", ignore = true)
    Stock toEntity(NewStockDto dto);

    void updateFromDto(UpdateStockDto dto, @MappingTarget Stock target);

    @AfterMapping
    default void afterCreate(NewStockDto src, @MappingTarget Stock tgt) {
        tgt.setSymbol(MappingHelpers.normalizeSymbol(src.symbol()));
        if (src.companyName() != null) {
            tgt.setCompanyName(MappingHelpers.trimOrNull(src.companyName()));
        }
        tgt.setImageURL(MappingHelpers.blankToNull(src.imageURL()));
    }

    @AfterMapping
    default void afterPatch(UpdateStockDto src, @MappingTarget Stock tgt) {
        if (src.symbol() != null) {
            tgt.setSymbol(MappingHelpers.normalizeSymbol(src.symbol()));
        }
        if (src.companyName() != null) {
            tgt.setCompanyName(MappingHelpers.trimOrNull(src.companyName()));
        }
        if (src.imageURL() != null) {
            tgt.setImageURL(MappingHelpers.blankToNull(src.imageURL()));
        }
    }
}
