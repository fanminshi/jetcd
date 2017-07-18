package com.coreos.jetcd.cluster;

import com.coreos.jetcd.data.AbstractResponse;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * MemberRemoveResponse returned by {@link com.coreos.jetcd.Cluster#removeMember(long)}
 * contains a header and a list of member the removal of the member.
 */
public class MemberRemoveResponse extends
    AbstractResponse<com.coreos.jetcd.api.MemberRemoveResponse> {

  private final AtomicReference<List<Member>> membersRef;

  public MemberRemoveResponse(com.coreos.jetcd.api.MemberRemoveResponse response) {
    super(response, response.getHeader());
    membersRef = new AtomicReference<>();
  }


  /**
   * returns a list of all members after removing the member.
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
