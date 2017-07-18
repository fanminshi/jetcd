package com.coreos.jetcd.cluster;

import com.coreos.jetcd.Cluster;
import com.coreos.jetcd.data.AbstractResponse;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * MemberListResponse returned by {@link Cluster#listMember()}
 * contains a header and a list of members.
 */
public class MemberListResponse extends AbstractResponse<com.coreos.jetcd.api.MemberListResponse> {

  private final AtomicReference<List<Member>> membersRef;

  public MemberListResponse(com.coreos.jetcd.api.MemberListResponse response) {
    super(response, response.getHeader());
    membersRef = new AtomicReference<>();
  }

  /**
   * returns a list of members. empty list if none.
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
