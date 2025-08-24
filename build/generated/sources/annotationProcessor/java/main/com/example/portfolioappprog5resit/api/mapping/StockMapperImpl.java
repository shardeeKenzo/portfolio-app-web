package com.example.portfolioappprog5resit.api.mapping;

import com.example.portfolioappprog5resit.api.dto.NewStockDto;
import com.example.portfolioappprog5resit.api.dto.StockDto;
import com.example.portfolioappprog5resit.api.dto.UpdateStockDto;
import com.example.portfolioappprog5resit.domain.Sector;
import com.example.portfolioappprog5resit.domain.Stock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-23T20:52:29+0200",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.14.3.jar, environment: Java 17.0.16 (SAP SE)"
)
@Component
public class StockMapperImpl implements StockMapper {

    @Override
    public StockDto toDto(Stock s) {
        if ( s == null ) {
            return null;
        }

        int id = 0;
        String symbol = null;
        String companyName = null;
        double currentPrice = 0.0d;
        Sector sector = null;
        LocalDate listedDate = null;
        String imageURL = null;

        id = s.getId();
        symbol = s.getSymbol();
        companyName = s.getCompanyName();
        currentPrice = s.getCurrentPrice();
        sector = s.getSector();
        listedDate = s.getListedDate();
        imageURL = s.getImageURL();

        StockDto stockDto = new StockDto( id, symbol, companyName, currentPrice, sector, listedDate, imageURL );

        return stockDto;
    }

    @Override
    public List<StockDto> toDtoList(List<Stock> in) {
        if ( in == null ) {
            return null;
        }

        List<StockDto> list = new ArrayList<StockDto>( in.size() );
        for ( Stock stock : in ) {
            list.add( toDto( stock ) );
        }

        return list;
    }

    @Override
    public Stock toEntity(NewStockDto dto) {
        if ( dto == null ) {
            return null;
        }

        Stock stock = new Stock();

        stock.setSymbol( dto.symbol() );
        stock.setCompanyName( dto.companyName() );
        stock.setCurrentPrice( dto.currentPrice() );
        stock.setSector( dto.sector() );
        stock.setListedDate( dto.listedDate() );
        stock.setImageURL( dto.imageURL() );

        afterCreate( dto, stock );

        return stock;
    }

    @Override
    public void updateFromDto(UpdateStockDto dto, Stock target) {
        if ( dto == null ) {
            return;
        }

        if ( dto.symbol() != null ) {
            target.setSymbol( dto.symbol() );
        }
        if ( dto.companyName() != null ) {
            target.setCompanyName( dto.companyName() );
        }
        if ( dto.currentPrice() != null ) {
            target.setCurrentPrice( dto.currentPrice() );
        }
        if ( dto.sector() != null ) {
            target.setSector( dto.sector() );
        }
        if ( dto.listedDate() != null ) {
            target.setListedDate( dto.listedDate() );
        }
        if ( dto.imageURL() != null ) {
            target.setImageURL( dto.imageURL() );
        }

        afterPatch( dto, target );
    }
}
