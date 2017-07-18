package com.coreos.jetcd.kv;

import com.coreos.jetcd.api.DeleteRangeResponse;
import com.coreos.jetcd.data.AbstractResponse;
import com.coreos.jetcd.data.KeyValue;
import java.util.List;
import java.util.stream.Collectors;

public class DeleteResponse extends AbstractResponse<DeleteRangeResponse> {

  public DeleteResponse(DeleteRangeResponse deleteRangeResponse) {
    super(deleteRangeResponse, deleteRangeResponse.getHeader());
  }

  /**
   * return the number of keys deleted by the delete range request.
   */
  public long getDeleted() {
    return getResponse().getDeleted();
  }

  /**
   * return previous key-value pairs.
   */
  public List<KeyValue> getPrevKvs() {
    return getResponse().getPrevKvsList().stream()
        .map(KeyValue::new)
        .collect(Collectors.toList());
  }
}
