package com.coreos.jetcd.cluster;

import com.coreos.jetcd.data.AbstractResponse;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * MemberAddResponse returned by {@link com.coreos.jetcd.Cluster#addMember(List)}
 * contains a header, added member, and list of members after adding the new member.
 */
public class MemberAddResponse extends AbstractResponse<com.coreos.jetcd.api.MemberAddResponse> {

  private final Member member;
  private final AtomicReference<List<Member>> membersRef;

  public MemberAddResponse(com.coreos.jetcd.api.MemberAddResponse response) {
    super(response, response.getHeader());
    member = new Member(response.getMember());
    membersRef = new AtomicReference<>();
  }

  /**
   * returns the member information for the added member.
   */
  public Member getMember() {
    return member;
  }

  /**
   * returns a list of all members after adding the new member.
   */
  public List<Member> getMembers() {
    List<Member> members = membersRef.get();
    if (members == null) {
      synchronized (membersRef) {
        members = membersRef.get();
        if (members == null) {
          members = Util.toMembers(getResponse().getMembersList());
          membersRef.lazySet(members);
        }
      }
    }

    return members;
  }
}
