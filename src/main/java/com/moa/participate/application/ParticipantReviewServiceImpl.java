package com.moa.participate.application;


import com.moa.global.common.exception.CustomException;
import com.moa.global.common.exception.ErrorCode;
import com.moa.participate.domain.ParticipantApplication;
import com.moa.participate.domain.ParticipantReview;
import com.moa.participate.dto.ParticipantReviewCreateDto;
import com.moa.participate.dto.kafka.ParticipantReviewCreateEventDto;
import com.moa.participate.infrastructure.ParticipantApplicationRepository;
import com.moa.participate.infrastructure.ParticipantReviewRepository;
import com.moa.participate.infrastructure.kafka.producer.ParticipantReviewCreateEventProducer;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ParticipantReviewServiceImpl implements ParticipantReviewService {

	private final ModelMapper modelMapper;

	private final ParticipantReviewRepository participantReviewRepository;
	private final ParticipantApplicationRepository participantApplicationRepository;

	private final ParticipantReviewCreateEventProducer participantReviewCreateEventProducer;


	@Override
	@Transactional
	public void createParticipantReview(ParticipantReviewCreateDto dto) {
		ParticipantApplication participantApplication = participantApplicationRepository.findById(dto.getParticipantApplicationId())
			.orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST)); // 참가자 존재하지 않을 경우 예외

		// 참가자 리뷰가 이미 존재할 경우 예외
		if (participantReviewRepository.existsByParticipantApplicationAndReviewerUserUuid(participantApplication, dto.getReviewrUserUuid())) {
			throw new CustomException(ErrorCode.DUPLICATE_RESOURCE);
		}

		ParticipantReview participantReview = new ParticipantReview(dto.getReviewrUserUuid(), participantApplication.getParticipantUuid(), dto.getRating(), participantApplication);
		participantReviewRepository.save(participantReview);

		participantReviewCreateEventProducer.sendEvent(new ParticipantReviewCreateEventDto(participantReview.getReviewTargetUserUuid(), participantReview.getRating()));
	}


	@Override
	@Transactional(readOnly = true)
	public boolean isParticipantReviewExist(Long meetingId, UUID targetUuid) {
		ParticipantApplication participantApplication = participantApplicationRepository.findByMeetingIdAndParticipantUuid(meetingId, targetUuid)
			.orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST)); // 참가자 존재하지 않을 경우 예외
		return participantReviewRepository.existsByParticipantApplication(participantApplication);
	}

}
