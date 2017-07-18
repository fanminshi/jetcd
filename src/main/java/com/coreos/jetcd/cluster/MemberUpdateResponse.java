package com.coreos.jetcd.cluster;

import com.coreos.jetcd.data.AbstractResponse;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * MemberUpdateResponse returned by {@link com.coreos.jetcd.Cluster#updateMember(long, List)}
 * contains a header and a list of members after the member update.
 */
public class MemberUpdateResponse extends
    AbstractResponse<com.coreos.jetcd.api.MemberUpdateResponse> {

  private final AtomicReference<List<Member>> membersRef;

  public MemberUpdateResponse(com.coreos.jetcd.api.MemberUpdateResponse response) {
    super(response, response.getHeader());
    membersRef = new AtomicReference<>();
  }

  /**
   * returns a list of all members after updating the member.
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
