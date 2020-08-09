package com.thoughtworks.rslist.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author gaarahan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trade {
  @NotNull
  @Min(1)
  int rsEventId;
  @NotNull
  @Min(1)
  int amount;
  @NotNull
  @Min(1)
  int rank;
}
