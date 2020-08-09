package com.thoughtworks.rslist.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author gaarahan
 */
@Entity
@Table(name = "trade")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeDto {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  int id;
  int amount;
  int rank;

  @ManyToOne
  RsEventDto rsEventDto;
}
