package com.thoughtworks.rslist.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "rsEvent")
public class RsEventDto {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;
  private int rank;
  private String eventName;
  private String keyword;
  private int voteNum;
  @ManyToOne private UserDto user;
}
