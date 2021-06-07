package com.jaewook.controller;

import com.jaewook.domain.Member;
import com.jaewook.service.MemberService;
import com.jaewook.service.MemberServiceImpl;
import com.jaewook.util.CustomErrorType;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@RestController
@RequestMapping(value = "/members")
public class MemberController {

    private static final Logger logger = LoggerFactory.getLogger(MemberServiceImpl.class);

    @Autowired
    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @ApiOperation(value = "회원 가입", notes = "회원으로 가입합니다.")
    @RequestMapping(value = "/join", method=RequestMethod.POST)
    public ResponseEntity<?> signUp(@RequestBody Member member) {
        logger.info("[MemberController] - signUp()");
        if (!isEmailValid(member.getUserEmail())) {
            logger.error("[MemberController] - signUp() - Error 1");
            return new ResponseEntity(new CustomErrorType(
                    "Please use valid email"),
                    HttpStatus.FORBIDDEN);
        }
        if (!memberService.isMemberExist(member.getUserId())) {
            // Todo Response with Success
            memberService.join(member.getUserId(), member.getUserPw(), member.getUserName(), member.getUserEmail());
            return new ResponseEntity("Successfully Sign Up", HttpStatus.OK);

        } else {
            logger.error("[MemberController] - signUp() - Error 2");
            return new ResponseEntity(new CustomErrorType(
                    "Unable to sign up with user id " + member.getUserId() + " already in use"),
                    HttpStatus.FORBIDDEN);
        }
    }

    @ApiOperation(value = "회원 탈퇴", notes = "회원에서 탈퇴합니다.")
    @RequestMapping(value = "/withdrawl", method=RequestMethod.POST)
    public ResponseEntity<?> withDrawl(@RequestBody Member member) {
        logger.info("[MemberController] - withDrawl()");
        String memberId = member.getUserId();
        if (!memberService.isMemberExist(memberId)) {
            logger.error("[MemberController] - withDrawl() - Error 1");
            return new ResponseEntity(new CustomErrorType(
                    "Unable to find member with user id " + memberId + " not found"),
                    HttpStatus.NOT_FOUND);
        }
        Member currentMember = memberService.findById(memberId);
        if (memberService.authentication(currentMember, member.getUserPw()) == false) {
            logger.info("[MemberController] - withDrawl() - Error 2");
            return new ResponseEntity(new CustomErrorType(
                    "Invalid password with the Id"),
                    HttpStatus.FORBIDDEN);
        }
        memberService.deleteById(memberId);
        return new ResponseEntity<>("WithDrawl Successfully", HttpStatus.OK);
    }

    @ApiOperation(value = "로그인", notes = "로그인합니다.")
    @RequestMapping(value = "/login", method=RequestMethod.POST)
    public ResponseEntity<?> logIn(@RequestBody Member member) {
        logger.info("[MemberController] - logIn()");
        String memberId = member.getUserId();
        if (!memberService.isMemberExist(memberId)) {
            logger.error("[MemberController] - logIn() - Error 1");
            return new ResponseEntity(new CustomErrorType(
                    "Unable to find member with user id " + memberId + " not found"),
                    HttpStatus.NOT_FOUND);
        }
        Member currentMember = memberService.findById(memberId);
        if (currentMember.getLoginYn() == true) {
            logger.info("[MemberController] - logIn() - Error 2");
            return new ResponseEntity(new CustomErrorType(
                    "Unable to login. Please logout first"),
                    HttpStatus.FORBIDDEN);
        }
        // authentication
        if (memberService.authentication(currentMember, member.getUserPw()) == false) {
            logger.info("[MemberController] - logIn() - Error 3");
            return new ResponseEntity(new CustomErrorType(
                    "Invalid password with the Id"),
                    HttpStatus.FORBIDDEN);
        }
        // login process
        currentMember.setLoginYn(true);
        currentMember.incLoginCount();
        currentMember.setUpdDate(new Date());

        memberService.updateUserInfo(currentMember);

        // login result
        return new ResponseEntity<>("Login Successfully\nLogin Count : " + currentMember.getLoginCount(), HttpStatus.OK);
    }

    @ApiOperation(value = "로그아웃", notes = "로그아웃합니다.")
    @RequestMapping(value = "/logout", method=RequestMethod.POST)
    public ResponseEntity<?> logOut(@RequestBody Member member) {
        logger.info("[MemberController] - logOut()");
        String memberId = member.getUserId();
        if (!memberService.isMemberExist(memberId)) {
            logger.info("[MemberController] - logOut() - Error 1");
            return new ResponseEntity(new CustomErrorType(
                    "Unable to find member with user id " + memberId + " not found"),
                    HttpStatus.NOT_FOUND);
        }
        Member currentMember = memberService.findById(memberId);
        if (currentMember.getLoginYn() == false) {
            logger.info("[MemberController] - logOut() - Error 2");
            return new ResponseEntity(new CustomErrorType(
                    "Unable to logout. Please login first"),
                    HttpStatus.FORBIDDEN);
        }
        // authentication
        if (memberService.authentication(currentMember, member.getUserPw()) == false) {
            logger.info("[MemberController] - logOut() - Error 3");
            return new ResponseEntity(new CustomErrorType(
                    "Invalid password with the Id"),
                    HttpStatus.FORBIDDEN);
        }
        // logout process
        currentMember.setLoginYn(false);
        // logout result
        return new ResponseEntity<>("Logout Successfully", HttpStatus.OK);
    }

    @ApiOperation(value = "회원 정보 업데이트", notes = "회원 정보 업데이트합니다.")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResponseEntity<?> updateUserInfo(@RequestBody Member member) {
        logger.info("[MemberController] - updateUserInfo()");
        String memberId = member.getUserId();
        if (!memberService.isMemberExist(memberId)) {
            logger.error("[MemberController] - updateUserInfo() - Error");
            return new ResponseEntity(new CustomErrorType(
                    "Unable to update information with user id " + memberId + " not found"),
                    HttpStatus.NOT_FOUND);
        }
        memberService.updateUserInfo(member);

        Member currentMember = memberService.findById(memberId);
        currentMember.setUserPw("******"); // 비밀번호 노출 방지
        return new ResponseEntity<Member>(currentMember, HttpStatus.OK);
    }

    @ApiOperation(value = "회원 정보 확인", notes = "회원 정보를 확인합니다.")
    @RequestMapping(value = "/userinfo", method = RequestMethod.POST)
    public ResponseEntity<?> userInfo(@RequestBody Member member) {
        logger.info("[MemberController] - userInfo()");
        String memberId = member.getUserId();
        if (!memberService.isMemberExist(memberId)) {
            logger.error("[MemberController] - userInfo() - Error 1");
            return new ResponseEntity(new CustomErrorType(
                    "Unable to find member with user id " + memberId + " - not found"),
                    HttpStatus.NOT_FOUND);
        }
        Member currentMember = memberService.findById(memberId);
        currentMember.setUserPw("******"); // 비밀번호 노출 방지
        return new ResponseEntity<Member>(currentMember, HttpStatus.OK);
    }

    public static boolean isEmailValid(String userEmail) {
        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(userEmail);
        return matcher.matches();
    }
}
