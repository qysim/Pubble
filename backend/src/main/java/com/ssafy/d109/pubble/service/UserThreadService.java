package com.ssafy.d109.pubble.service;

import com.ssafy.d109.pubble.dto.response.CommentResponseData;
import com.ssafy.d109.pubble.dto.response.UserThreadDto;
import com.ssafy.d109.pubble.entity.Comment;
import com.ssafy.d109.pubble.entity.User;
import com.ssafy.d109.pubble.entity.UserThread;
import com.ssafy.d109.pubble.exception.UserThread.UnauthorizedAccessException;
import com.ssafy.d109.pubble.exception.UserThread.UserThreadAlreadyLockedException;
import com.ssafy.d109.pubble.exception.UserThread.UserThreadNotFoundException;
import com.ssafy.d109.pubble.repository.CommentRepository;
import com.ssafy.d109.pubble.repository.UserThreadRepository;
import com.ssafy.d109.pubble.util.CommonUtil;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Log4j2
public class UserThreadService {

    private final UserThreadRepository userThreadRepository;
    private final CommentRepository commentRepository;
    private final CommonUtil commonUtil;


    public UserThreadService(UserThreadRepository userThreadRepository, CommentRepository commentRepository, CommonUtil commonUtil) {
        this.userThreadRepository = userThreadRepository;
        this.commentRepository = commentRepository;
        this.commonUtil = commonUtil;
    }


    public List<UserThreadDto> getAllUserThreads(Integer requirementId) {

        List<UserThread> userThreads = userThreadRepository.findAllByRequirement_requirementId(requirementId);
        return userThreads.stream()
                .map(this::convertUserThreadToDto)
                .collect(Collectors.toList());
    }


    public void lockThread(Integer requirementId, Integer userThreadId) {

        UserThread currentThread = userThreadRepository.findUserThreadByUserThreadId(userThreadId).orElseThrow(UserThreadNotFoundException::new);
        Integer threadUser = currentThread.getUser().getUserId();
        Integer currentUser = commonUtil.getUser().getUserId();

        log.info("threadUser: {}, currentUser: {}", threadUser, currentUser);

        // 해당 스레드에 대한 권한 체크
        if (!threadUser.equals(currentUser)) {
            throw new UnauthorizedAccessException();
        }

        // 이미 Lock된 스레드일 경우 예외 처리
        if (currentThread.getLockYN().equals("y")) {
            throw new UserThreadAlreadyLockedException();
        }

        currentThread.setLockYN("y");


    }


    private UserThreadDto convertUserThreadToDto(UserThread userThread) {
        UserThreadDto dto = new UserThreadDto();
        dto.setUserThreadId(userThread.getUserThreadId());
        dto.setIsLocked(userThread.getLockYN());
        List<CommentResponseData> commentDataList = commentRepository.findAllByUserThread_userThreadId(userThread.getUserThreadId()).stream()
                .map(this::convertCommentToDto)
                .collect(Collectors.toList());

        dto.setCommentList(commentDataList);

        return dto;
    }

    private CommentResponseData convertCommentToDto(Comment comment) {
        CommentResponseData dto = new CommentResponseData();
        dto.setCommentId(comment.getCommentId());
        dto.setContent(comment.getContent());
        dto.setUserId(comment.getUser().getUserId());

        return dto;
    }


}