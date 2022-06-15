package io.pp.arcade.domain.slot.controller;

import io.pp.arcade.domain.currentmatch.CurrentMatchService;
import io.pp.arcade.domain.currentmatch.dto.CurrentMatchAddDto;
import io.pp.arcade.domain.currentmatch.dto.CurrentMatchDto;
import io.pp.arcade.domain.currentmatch.dto.CurrentMatchModifyDto;
import io.pp.arcade.domain.slot.SlotService;
import io.pp.arcade.domain.slot.dto.*;
import io.pp.arcade.domain.team.TeamService;
import io.pp.arcade.domain.team.dto.TeamAddUserDto;
import io.pp.arcade.domain.team.dto.TeamDto;
import io.pp.arcade.domain.team.dto.TeamPosDto;
import io.pp.arcade.domain.team.dto.TeamRemoveUserDto;
import io.pp.arcade.domain.user.UserService;
import io.pp.arcade.domain.slot.dto.SlotFindResponseDto;
import io.pp.arcade.domain.user.dto.UserDto;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/pingpong")
public class SlotControllerImpl implements SlotController {
    private final SlotService slotService;
    private final UserService userService;
    private final TeamService teamService;
    private final CurrentMatchService currentMatchService;

    @Override
    @GetMapping(value = "/match/tables/{tableId}")
    public SlotFindResponseDto slotStatusList(Integer tableId, String type, Integer userId) {
        List<SlotStatusDto> slots;
        SlotFindStatusDto findDto = SlotFindStatusDto.builder()
                .userId(userId)
                .type(type)
                .currentTime(LocalDateTime.now())
                .build();
        slots = slotService.findSlotsStatus(findDto);
        SlotFindResponseDto responseDto = SlotFindResponseDto.builder().matchBoards(slots).build();
        return responseDto;
    }

    @Override
    @PostMapping(value = "/match/tables/{tableId}")
    public void slotAddUser(Integer tableId, SlotAddUserRequestDto addReqDto, Integer userId) {
        UserDto user = userService.findById(userId);
        //user가 매치를 이미 가지고 있는지 myTable에서 user 필터하기
        CurrentMatchDto matchDto = currentMatchService.findCurrentMatchByUserId(userId);
        if (matchDto != null) {
            throw new IllegalArgumentException("잘못된 요청입니다.");
        }

        SlotAddUserDto addDto = SlotAddUserDto.builder()
                .slotId(addReqDto.getSlotId())
                .type(addReqDto.getType())
                .joinUserPpp(user.getPpp())
                .build();
        SlotDto slot = slotService.findSlotById(addDto.getSlotId());
        TeamAddUserDto teamAddUserDto = getTeamAddUserDto(slot, addReqDto.getType(), user);

        //유저가 슬롯에 입장하면 currentMatch에 등록된다.
        CurrentMatchAddDto matchAddDto = CurrentMatchAddDto.builder()
                .slot(slot)
                .userId(userId)
                .build();
        currentMatchService.addCurrentMatch(matchAddDto);
        slotService.addUserInSlot(addDto);
        teamService.addUserInTeam(teamAddUserDto);

        slot = slotService.findSlotById(slot.getId());

        //유저가 슬롯에 꽉 차면 currentMatch가 전부 바뀐다.
        modifyUsersCurrentMatchStatus(user.getId(), slot);

    }

    @Override
    @DeleteMapping(value = "/match/tables/{tableId}")
    public void slotRemoveUser(Integer tableId, Integer slotId, Integer pUserId) {
        // slotId , tableId 유효성 검사
        UserDto user = userService.findById(pUserId);
        // 유저 조회, 슬롯 조회, 팀 조회( 슬롯에 헤드 카운트 -, 팀에서 유저 퇴장 )
        SlotDto slot = slotService.findSlotById(slotId);

        currentMatchService.removeCurrentMatch(user.getId());
        teamService.removeUserInTeam(getTeamRemoveUserDto(slot, user));
        slotService.removeUserInSlot(getSlotRemoveUserDto(slot, user));
    }

    private void modifyUsersCurrentMatchStatus(int userId, SlotDto slot) {
        TeamDto team1 = slot.getTeam1();
        TeamDto team2 = slot.getTeam2();
        Integer maxSlotHeadCount = "single".equals(slot.getType()) ? 2 : 4;
        Boolean isMatched = slot.getHeadCount().equals(maxSlotHeadCount) ? true : false;
        Boolean isImminent = slot.getTime().isBefore(LocalDateTime.now().plusMinutes(5)) ? true : false;
        CurrentMatchModifyDto matchModifyDto = CurrentMatchModifyDto.builder()
                .userId(userId)
                .isMatched(isMatched)
                .matchImminent(isImminent)
                .build();
        modifyCurrentMatch(team1.getUser1(), matchModifyDto);
        modifyCurrentMatch(team1.getUser2(), matchModifyDto);
        modifyCurrentMatch(team2.getUser1(), matchModifyDto);
        modifyCurrentMatch(team2.getUser2(), matchModifyDto);
    }

    private TeamAddUserDto getTeamAddUserDto(SlotDto slot, String reqType, UserDto user) {
        Integer teamId;
        String slotType = slot.getType();
        TeamDto team1 = slot.getTeam1();
        TeamDto team2 = slot.getTeam2();
        Integer headCount = slot.getHeadCount();
        Integer maxTeamHeadCount = "single".equals(slotType) ? 1 : 2;

        SlotFilterDto slotFilterDto = SlotFilterDto.builder()
                .slotId(slot.getId())
                .slotTime(slot.getTime())
                .slotType(slot.getType())
                .requestType(reqType)
                .userPpp(user.getPpp())
                .gamePpp(slot.getGamePpp())
                .headCount(slot.getHeadCount())
                .build();
        if (slotService.getStatus(slotFilterDto).equals("open")) {
            if (team1.getHeadCount() < maxTeamHeadCount) {
                teamId = team1.getId();
            } else {
                teamId = team2.getId();
            }
        } else {
            throw new IllegalArgumentException("잘못된 요청입니다.");
        }
        TeamAddUserDto teamAddUserDto = TeamAddUserDto.builder()
                .teamId(teamId)
                .userId(user.getId())
                .build();
        return teamAddUserDto;
    }

    private TeamRemoveUserDto getTeamRemoveUserDto(SlotDto slot, UserDto user) {
        TeamPosDto teamPos = teamService.getTeamPosNT(user, slot.getTeam1(), slot.getTeam2());
        TeamRemoveUserDto teamRemoveUserDto = TeamRemoveUserDto.builder()
                .userId(user.getId())
                .teamId(teamPos.getMyTeam().getId())
                .build();
        return teamRemoveUserDto;
    }

    private SlotRemoveUserDto getSlotRemoveUserDto(SlotDto slot, UserDto user) {
        SlotRemoveUserDto slotRemoveUserDto = SlotRemoveUserDto.builder()
                .slotId(slot.getId())
                .exitUserPpp(user.getPpp())
                .build();
        return slotRemoveUserDto;
    }

    private void modifyCurrentMatch(UserDto user, CurrentMatchModifyDto modifyDto) {
        if (user != null) {
            CurrentMatchModifyDto matchModifyDto = CurrentMatchModifyDto.builder()
                    .userId(user.getId())
                    .isMatched(modifyDto.getIsMatched())
                    .matchImminent(modifyDto.getMatchImminent())
                    .build();
            currentMatchService.modifyCurrentMatch(matchModifyDto);
        }
    }
}
