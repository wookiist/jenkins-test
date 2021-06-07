package com.jaewook.service;

import com.jaewook.domain.Member;

public interface MemberService {

    public int join(String userId, String userPw,
                    String userName, String userEmail);
    public void updateUserInfo(Member currentMember);
    public Member findById(String userId);
    public void deleteById(String userId);
    public boolean authentication(Member currentMember, String tempPw);
    public boolean isMemberExist(String userId);
}
