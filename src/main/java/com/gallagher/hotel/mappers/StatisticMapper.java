package com.gallagher.hotel.mappers;

import com.gallagher.hotel.dto.StatisticDto;
import com.gallagher.hotel.models.Statistic;
import org.springframework.stereotype.Component;

@Component
public class StatisticMapper {

    public StatisticDto toDto(Statistic statistic) {
        if (statistic == null) {
            return null;
        }
        
        return new StatisticDto(
                statistic.getId(),
                statistic.getType(),
                statistic.getDate(),
                statistic.getValue(),
                statistic.getValueString(),
                statistic.getValueInteger(),
                statistic.getPercentageValue()
        );
    }

    public Statistic toEntity(StatisticDto dto) {
        if (dto == null) {
            return null;
        }
        
        Statistic statistic = new Statistic();
        statistic.setId(dto.getId());
        statistic.setType(dto.getType());
        statistic.setDate(dto.getDate());
        statistic.setValue(dto.getValue());
        statistic.setValueString(dto.getValueString());
        statistic.setValueInteger(dto.getValueInteger());
        statistic.setPercentageValue(dto.getPercentageValue());
        
        return statistic;
    }
} 