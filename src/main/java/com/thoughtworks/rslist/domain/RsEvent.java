package com.thoughtworks.rslist.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class RsEvent implements Serializable {
  @NotNull private String eventName;
  @NotNull private String keyword;
  private int rank;
  private int voteNum;
  @NotNull private int userId;


  public RsEvent(String eventName, String keyWord, int userId) {
    this.eventName = eventName;
    this.keyword = keyWord;
    this.userId = userId;
    this.voteNum = 10;
    this.rank = 0;
  }
}
