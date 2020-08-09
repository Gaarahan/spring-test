package com.thoughtworks.rslist.repository;

import com.thoughtworks.rslist.dto.TradeDto;
import org.springframework.data.repository.CrudRepository;

/**
 * @author gaarahan
 */
public interface TradeRepository extends CrudRepository<TradeDto, Integer> {
}
