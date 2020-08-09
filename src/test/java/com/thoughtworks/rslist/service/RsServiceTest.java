package com.thoughtworks.rslist.service;

import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.domain.Trade;
import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.domain.Vote;
import com.thoughtworks.rslist.dto.RsEventDto;
import com.thoughtworks.rslist.dto.TradeDto;
import com.thoughtworks.rslist.dto.UserDto;
import com.thoughtworks.rslist.dto.VoteDto;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.TradeRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

class RsServiceTest {
  RsService rsService;

  @Mock RsEventRepository rsEventRepository;
  @Mock UserRepository userRepository;
  @Mock VoteRepository voteRepository;
  @Mock TradeRepository tradeRepository;
  LocalDateTime localDateTime;
  ModelMapper modelMapper;
  Vote vote;
  User curUser;

  @BeforeEach
  void setUp() {
    initMocks(this);
    rsService = new RsService(rsEventRepository, userRepository, voteRepository, tradeRepository);
    localDateTime = LocalDateTime.now();
    vote = Vote.builder().voteNum(2).rsEventId(1).time(localDateTime).userId(1).build();
    modelMapper = new ModelMapper();
    curUser = new User("idolice", "female", 19, "a@b.com", "18888888888");
  }

  @Test
  void shouldVoteSuccess() {
    // given

    UserDto userDto =
        UserDto.builder()
            .voteNum(5)
            .phone("18888888888")
            .gender("female")
            .email("a@b.com")
            .age(19)
            .userName("xiaoli")
            .id(2)
            .build();
    RsEventDto rsEventDto =
        RsEventDto.builder()
            .eventName("event name")
            .id(1)
            .keyword("keyword")
            .voteNum(2)
            .user(userDto)
            .build();

    when(rsEventRepository.findById(anyInt())).thenReturn(Optional.of(rsEventDto));
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(userDto));
    // when
    rsService.vote(vote, 1);
    // then
    verify(voteRepository)
        .save(
            VoteDto.builder()
                .num(2)
                .localDateTime(localDateTime)
                .user(userDto)
                .rsEvent(rsEventDto)
                .build());
    verify(userRepository).save(userDto);
    verify(rsEventRepository).save(rsEventDto);
  }

  @Test
  void shouldThrowExceptionWhenUserNotExist() {
    // given
    when(rsEventRepository.findById(anyInt())).thenReturn(Optional.empty());
    when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
    //when&then
    assertThrows(
        RuntimeException.class,
        () -> {
          rsService.vote(vote, 1);
        });
  }

  @Test
  void shouldBuyRankSuccessWhenRankHasNotBeenBuy() {
    UserDto userDto = modelMapper.map(curUser, UserDto.class);
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(userDto));

    RsEvent rsEvent = new RsEvent("han", "key", userDto.getId());
    RsEventDto rsEventDto = modelMapper.map(rsEvent, RsEventDto.class);
    when(rsEventRepository.findById(anyInt())).thenReturn(Optional.of(rsEventDto));

    Trade trade = new Trade(rsEventDto.getId(), 1, 1);
    rsService.buy(trade, 100);

    TradeDto tradeDto = modelMapper.map(trade, TradeDto.class);
    tradeDto.setRsEventDto(rsEventDto);
    verify(tradeRepository).save(tradeDto);
    rsEventDto.setRank(1);
    verify(rsEventRepository).save(rsEventDto);
  }

  @Test
  void shouldBuyRankFailedWhenMoneyIsNotEnough() {
    UserDto userDto = modelMapper.map(curUser, UserDto.class);
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(userDto));

    RsEvent rsEvent = new RsEvent("han", "key", userDto.getId());
    RsEventDto rsEventDto = modelMapper.map(rsEvent, RsEventDto.class);
    when(rsEventRepository.findById(anyInt())).thenReturn(Optional.of(rsEventDto));

    Trade trade = new Trade(rsEventDto.getId(), 100, 1);
    TradeDto tradeDto = modelMapper.map(trade, TradeDto.class);
    when(tradeRepository.findAllByRsEventDto(rsEventDto)).thenReturn(Collections.singletonList(tradeDto));
    Trade newTrade = new Trade(rsEventDto.getId(), 1, 1);

    assertThrows(RuntimeException.class, () -> {
      rsService.buy(newTrade, 100);
    });
  }

  @Test
  void shouldBuyRankSuccessAndDeleteRsEventBeforeWhenPayMoreThanThePriceBefore() {
    UserDto userDto = modelMapper.map(curUser, UserDto.class);
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(userDto));

    RsEvent rsEvent = new RsEvent("han", "key", userDto.getId());
    RsEventDto rsEventDto = modelMapper.map(rsEvent, RsEventDto.class);
    RsEvent rsEvent2 = new RsEvent("han", "key", userDto.getId());
    RsEventDto rsEventDto2 = modelMapper.map(rsEvent, RsEventDto.class);

    when(rsEventRepository.findById(1)).thenReturn(Optional.of(rsEventDto));
    when(rsEventRepository.findById(2)).thenReturn(Optional.of(rsEventDto2));

    Trade trade = new Trade(1, 1, 1);
    TradeDto tradeDto = modelMapper.map(trade, TradeDto.class);
    when(tradeRepository.findAllByRsEventDto(rsEventDto)).thenReturn(Arrays.asList(tradeDto));

    Trade tradeNew = new Trade(2, 100, 1);
    rsService.buy(tradeNew, 2);

    int rank = rsEventRepository.findById(2).get().getRank();
    assertEquals(1, rank);
  }
}
