package com.moa.participate.presentation;


import com.moa.participate.application.MeetingLikeService;
import com.moa.participate.common.ApiResult;
import com.moa.participate.dto.MeetingLikeCreateDto;
import com.moa.participate.dto.MeetingLikeDeleteDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/v1/meeting-feature/meeting")
@Slf4j
@RequiredArgsConstructor
public class MeetingLikeController {

	private final MeetingLikeService meetingLikeService;


	@Operation(summary = "모임 즐겨찾기 추가", description = "모임 즐겨찾기 추가")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
		@ApiResponse(responseCode = "409", description = "DUPLICATE_RESOURCE"),
		@ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
	})
	@PostMapping("/{meetingId}/like")
	public ResponseEntity<ApiResult<Void>> createMeetingLike(@PathVariable("meetingId") Long meetingId) {
		UUID userUuid = UUID.fromString("a642406c-6e20-11ee-b962-0242ac120002"); // TODO: 로그인 구현 후 수정
		MeetingLikeCreateDto dto = MeetingLikeCreateDto.builder()
			.meetingId(meetingId)
			.userUuid(userUuid)
			.build();
		meetingLikeService.createMeetingLike(dto);
		return ResponseEntity.ok(ApiResult.ofSuccess(null));
	}


	@Operation(summary = "모임 즐겨찾기 삭제", description = "모임 즐겨찾기 삭제")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
		@ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
	})
	@DeleteMapping("/{meetingId}/like")
	public ResponseEntity<ApiResult<Void>> deleteMeetingLike(@PathVariable("meetingId") Long meetingId) {
		UUID userUuid = UUID.fromString("a642406c-6e20-11ee-b962-0242ac120002"); // TODO: 로그인 구현 후 수정
		MeetingLikeDeleteDto dto = MeetingLikeDeleteDto.builder()
			.meetingId(meetingId)
			.userUuid(userUuid)
			.build();
		meetingLikeService.deleteMeetingLike(dto);
		return ResponseEntity.ok(ApiResult.ofSuccess(null));
	}

}
