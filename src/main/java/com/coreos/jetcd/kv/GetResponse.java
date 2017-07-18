package com.coreos.jetcd.kv;

import com.coreos.jetcd.api.RangeResponse;
import com.coreos.jetcd.data.AbstractResponse;
import com.coreos.jetcd.data.KeyValue;
import java.util.List;
import java.util.stream.Collectors;

public class GetResponse extends AbstractResponse<RangeResponse> {

  public GetResponse(RangeResponse rangeResponse) {
    super(rangeResponse, rangeResponse.getHeader());
  }

  /**
   * return a list of key-value pairs matched by the range request.
   */
  public List<KeyValue> getKvs() {
    return getResponse().getKvsList().stream()
        .map(KeyValue::new)
        .collect(Collectors.toList());
  }

  /**
   * more indicates if there are more keys to return in the requested range.
   */
  public boolean isMore() {
    return getResponse().getMore();
  }

  /**
   * return the number of keys within the range when requested.
   */
  public long getCount() {
    return getResponse().getCount();
  }
}
