package com.moa.participate.presentation;


import com.moa.global.common.ApiResult;
import com.moa.global.common.SliceResponse;
import com.moa.participate.application.MeetingReviewService;
import com.moa.participate.dto.MeetingReviewCreatDto;
import com.moa.participate.dto.MeetingReviewGetDto;
import com.moa.participate.vo.request.MeetingReviewCreateRequest;
import com.moa.participate.vo.response.MeetingReviewResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/v1/meeting-feature/meeting-review")
@Slf4j
@RequiredArgsConstructor
public class MeetingReviewController {

	private final ModelMapper modelMapper;
	private final MeetingReviewService meetingReviewService;


	@Operation(summary = "모임 리뷰 생성", description = "모임 리뷰 생성")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "리뷰어와 리뷰 대상자가 같은 경우"),
		@ApiResponse(responseCode = "409", description = "이미 리뷰를 작성한 경우"),
		@ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
	})
	@PostMapping("")
	public ResponseEntity<ApiResult<Void>> createMeetingReview(@RequestBody MeetingReviewCreateRequest meetingReviewCreateRequest) {
		MeetingReviewCreatDto meetingReviewCreatDto = modelMapper.map(meetingReviewCreateRequest, MeetingReviewCreatDto.class);
		meetingReviewService.createMeetingReview(meetingReviewCreatDto);
		return ResponseEntity.ok(ApiResult.ofSuccess(null));
	}


	@Operation(summary = "모임 리뷰 조회", description = "모임 리뷰 조회")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK",
			content = @Content(schema = @Schema(implementation = MeetingReviewResponse.class))),
		@ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
	})
	@Parameters({
		@Parameter(in = ParameterIn.QUERY, name = "meetingHostUuid", description = "호스트 유저 uuid", required = true, example = "a642406c-6e20-11ee-b962-0242ac120002")
	})
	@GetMapping("")
	public ResponseEntity<ApiResult<SliceResponse<MeetingReviewResponse>>> getMeetingReviewList(@RequestParam("meetingHostUuid") UUID meetingHostUuid, Pageable pageable) {
		Slice<MeetingReviewGetDto> meetingReviewGetDtoSlice = meetingReviewService.getMeetingReviewListByHostUuid(meetingHostUuid, pageable);
		Slice<MeetingReviewResponse> meetingReviewResponseSlice = meetingReviewGetDtoSlice.map(meetingReviewGetDto -> modelMapper.map(meetingReviewGetDto, MeetingReviewResponse.class));
		return ResponseEntity.ok(ApiResult.ofSuccess(SliceResponse.of(meetingReviewResponseSlice)));
	}

}
