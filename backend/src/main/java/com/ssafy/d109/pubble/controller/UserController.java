package com.ssafy.d109.pubble.controller;


import com.ssafy.d109.pubble.dto.SimpleUserInfoDto;
import com.ssafy.d109.pubble.dto.projectDto.UserInfoDto;
import com.ssafy.d109.pubble.dto.requestDto.UserSignInRequestDto;
import com.ssafy.d109.pubble.dto.responseDto.ResponseDto;
import com.ssafy.d109.pubble.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserController {


    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signin")
    public void login(@RequestBody UserSignInRequestDto dto) {


    }

    @PostMapping("/logout")
    public String logOut() {
        System.out.println("로그아웃된 상태");

        return "당신은 로그아웃된 상태입니다.";
    }



    // user info list
    private ResponseDto response;

    @GetMapping("/simple/all")
    public ResponseEntity<ResponseDto> getAllUserSimpleInfos() {
        List<SimpleUserInfoDto> simpleAllUserInfos = userService.getAllUserSimpleInfos();

        response = new ResponseDto(true, "모든 유저의 사번, 이름", simpleAllUserInfos);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/simple/{projectId}")
    public ResponseEntity<ResponseDto> getProjectUserSimpleInfos(@PathVariable Integer projectId) {
        List<SimpleUserInfoDto> simpleProjectUserInfos = userService.getProjectUserSimpleInfos(projectId);

        response = new ResponseDto(true, "햏당 프로젝트에 참여중인 유저의 사번, 이름", simpleProjectUserInfos);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/userinfo/all")
    public ResponseEntity<ResponseDto> getUserInfoAllList() {
        List<UserInfoDto> allUserInfos = userService.getAllUserInfos();

        response = new ResponseDto(true, "모든 유저의 일부 정보", allUserInfos);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/userinfo/{projectId}")
    public ResponseEntity<ResponseDto> getUserInfoProjectList(@PathVariable Integer projectId) {
        List<UserInfoDto> projectUserInfos = userService.getProjectUserInfos(projectId);

        response = new ResponseDto(true, "햏당 프로젝트에 참여중인 유저의 일부 정보", projectUserInfos);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
