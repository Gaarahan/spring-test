package com.thoughtworks.rslist.repository;

import com.thoughtworks.rslist.dto.RsEventDto;
import com.thoughtworks.rslist.dto.TradeDto;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author gaarahan
 */
public interface TradeRepository extends CrudRepository<TradeDto, Integer> {
  List<TradeDto> findAllByRsEventDto(RsEventDto rsEventDto);
}
