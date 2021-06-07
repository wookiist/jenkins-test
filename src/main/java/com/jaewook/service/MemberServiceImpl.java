package com.jaewook.service;

import com.jaewook.dao.MemberMapper;
import com.jaewook.domain.Member;
import com.jaewook.domain.MemberExample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class MemberServiceImpl implements MemberService {

    private static final Logger logger = LoggerFactory.getLogger(MemberServiceImpl.class);

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    public int join(String userId, String userPw, String userName, String userEmail) {
        logger.info("[MemberServiceImpl] - join()");
        String encodedPw = passwordEncoder.encode(userPw);
        return memberMapper.insert(new Member(userId, encodedPw, userName, userEmail));
    }

    public void updateUserInfo(Member currentMember) {
        logger.info("[MemberServiceImpl] - updateUserInfo()");
        MemberExample memberExample = new MemberExample();
        memberExample.createCriteria().andUserIdEqualTo(currentMember.getUserId());

        // password check
        if (currentMember.getUserPw() != null) {
            logger.info("[MemberServiceImpl] - updateUserInfo() - password is not null");
            currentMember.setUserPw(passwordEncoder.encode(currentMember.getUserPw()));
        }

        // Todo - unneeded info should be unavailable.
        memberMapper.updateByExampleSelective(currentMember, memberExample);
    }

    public Member findById(String userId)
    {
        logger.info("[MemberServiceImpl] - findByID()");

        MemberExample memberExample = new MemberExample();
        memberExample.createCriteria().andUserIdEqualTo(userId);
        List<Member> memberList = memberMapper.selectByExample(memberExample);

        if (!memberList.isEmpty()) {
            logger.info("findById() - not null " + memberList);
            return memberList.get(0);
        } else {
            logger.info("findById() -  null " + memberList);
            return null;
        }
    }

    public void deleteById(String userId) {
        logger.info("[MemberServiceImpl] - delete()");
        MemberExample memberExample = new MemberExample();
        memberExample.createCriteria().andUserIdEqualTo(userId);
        memberMapper.deleteByExample(memberExample);
    }

    public boolean authentication(Member currentMember, String tempPw) {
        logger.info("[MemberServiceImpl] - authentication()");
        return passwordEncoder.matches(tempPw, currentMember.getUserPw());
    }

    public boolean isMemberExist(String userId) {
        logger.info("[MemberServiceImpl] - isMemberExist()");
        if (this.findById(userId) == null) {
            return false;
        } else {
            return true;
        }
    }
}
