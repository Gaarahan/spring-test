package com.thoughtworks.rslist.service;

import com.thoughtworks.rslist.domain.Trade;
import com.thoughtworks.rslist.domain.Vote;
import com.thoughtworks.rslist.dto.RsEventDto;
import com.thoughtworks.rslist.dto.TradeDto;
import com.thoughtworks.rslist.dto.UserDto;
import com.thoughtworks.rslist.dto.VoteDto;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.TradeRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class RsService {
  final RsEventRepository rsEventRepository;
  final UserRepository userRepository;
  final VoteRepository voteRepository;
  final TradeRepository tradeRepository;

  public RsService(RsEventRepository rsEventRepository, UserRepository userRepository, VoteRepository voteRepository, TradeRepository tradeRepository) {
    this.rsEventRepository = rsEventRepository;
    this.userRepository = userRepository;
    this.voteRepository = voteRepository;
    this.tradeRepository = tradeRepository;
  }

  public void vote(Vote vote, int rsEventId) {
    Optional<RsEventDto> rsEventDto = rsEventRepository.findById(rsEventId);
    Optional<UserDto> userDto = userRepository.findById(vote.getUserId());
    if (!rsEventDto.isPresent()
        || !userDto.isPresent()
        || vote.getVoteNum() > userDto.get().getVoteNum()) {
      throw new RuntimeException();
    }
    VoteDto voteDto =
        VoteDto.builder()
            .localDateTime(vote.getTime())
            .num(vote.getVoteNum())
            .rsEvent(rsEventDto.get())
            .user(userDto.get())
            .build();
    voteRepository.save(voteDto);
    UserDto user = userDto.get();
    user.setVoteNum(user.getVoteNum() - vote.getVoteNum());
    userRepository.save(user);
    RsEventDto rsEvent = rsEventDto.get();
    rsEvent.setVoteNum(rsEvent.getVoteNum() + vote.getVoteNum());
    rsEventRepository.save(rsEvent);
  }

  public void buy(Trade trade, int id) {
    Optional<RsEventDto> rsEventDtoOptional = rsEventRepository.findById(id);
    if (!rsEventDtoOptional.isPresent()) {
      throw new RuntimeException("invalid id");
    }

    RsEventDto rsEventDto = rsEventDtoOptional.get();
    List<TradeDto> allByRsEventDto = this.tradeRepository.findAllByRsEventDto(rsEventDto);
    if (allByRsEventDto.size() > 0) {
      List<TradeDto> biddingTrade = allByRsEventDto.stream()
          .filter(tradeDto -> tradeDto.getRank() == trade.getRank())
          .collect(Collectors.toList());
      if (biddingTrade.size() > 0){
        Stream<TradeDto> payMoreTrade = biddingTrade.stream()
            .filter(tradeDto -> tradeDto.getAmount() > trade.getAmount());
        if(payMoreTrade.count() > 0) {
          throw new RuntimeException("bidding failed");
        }
        else {
          biddingTrade.forEach(tradeDto -> this.rsEventRepository.delete(tradeDto.getRsEventDto()));
        }
      }
    }

    ModelMapper modelMapper = new ModelMapper();
    TradeDto tradeDto = modelMapper.map(trade, TradeDto.class);
    tradeDto.setRsEventDto(rsEventDto);

    this.tradeRepository.save(tradeDto);
    rsEventDto.setRank(tradeDto.getRank());
    this.rsEventRepository.save(rsEventDto);
  }
}
