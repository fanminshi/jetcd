package com.coreos.jetcd.kv;

import static com.coreos.jetcd.api.ResponseOp.ResponseCase.RESPONSE_DELETE_RANGE;
import static com.coreos.jetcd.api.ResponseOp.ResponseCase.RESPONSE_PUT;
import static com.coreos.jetcd.api.ResponseOp.ResponseCase.RESPONSE_RANGE;

import com.coreos.jetcd.data.AbstractResponse;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * TxnResponse returned by a transaction call contains lists of put, get, delete responses
 * corresponding to either the compare in txn.IF is evaluated to true or false.
 */
public class TxnResponse extends AbstractResponse<com.coreos.jetcd.api.TxnResponse> {

  // TODO add txnResponsesRef when nested txn is implemented.
  private final AtomicReference<List<PutResponse>> putResponsesRef;
  private final AtomicReference<List<GetResponse>> getResponsesRef;
  private final AtomicReference<List<DeleteResponse>> deleteResponsesRef;


  public TxnResponse(com.coreos.jetcd.api.TxnResponse txnResponse) {
    super(txnResponse, txnResponse.getHeader());
    putResponsesRef = new AtomicReference<>();
    getResponsesRef = new AtomicReference<>();
    deleteResponsesRef = new AtomicReference<>();
  }

  /**
   * return true if the compare evaluated to true or false otherwise.
   */
  public boolean isSucceeded() {
    return getResponse().getSucceeded();
  }

  /**
   * returns a list of DeleteResponse; empty list if none.
   */
  public List<DeleteResponse> getDeleteResponses() {
    List<DeleteResponse> deleteResponses = deleteResponsesRef.get();
    if (deleteResponses == null) {
      synchronized (deleteResponsesRef) {
        deleteResponses = deleteResponsesRef.get();
        if (deleteResponses == null) {
          deleteResponses = getResponse().getResponsesList().stream()
              .filter((responseOp) -> responseOp.getResponseCase() != RESPONSE_DELETE_RANGE)
              .map(responseOp -> new DeleteResponse(responseOp.getResponseDeleteRange()))
              .collect(Collectors.toList());

          if (deleteResponses == null) {
            deleteResponses = Collections.emptyList();
          }

          deleteResponsesRef.lazySet(deleteResponses);
        }
      }
    }

    return deleteResponses;
  }

  /**
   * returns a list of GetResponse; empty list if none.
   */
  public List<GetResponse> getGetResponses() {
    List<GetResponse> getResponses = getResponsesRef.get();
    if (getResponses == null) {
      synchronized (getResponsesRef) {
        getResponses = getResponsesRef.get();
        if (getResponses == null) {
          getResponses = getResponse().getResponsesList().stream()
              .filter((responseOp) -> responseOp.getResponseCase() != RESPONSE_RANGE)
              .map(responseOp -> new GetResponse(responseOp.getResponseRange()))
              .collect(Collectors.toList());

          getResponsesRef.lazySet(getResponses);
        }
      }
    }

    return getResponses;
  }

  /**
   * returns a list of PutResponse; empty list if none.
   */
  public List<PutResponse> getPutResponses() {
    List<PutResponse> putResponses = putResponsesRef.get();
    if (putResponses == null) {
      synchronized (putResponsesRef) {
        putResponses = putResponsesRef.get();
        if (putResponses == null) {
          putResponses = getResponse().getResponsesList().stream()
              .filter((responseOp) -> responseOp.getResponseCase() != RESPONSE_PUT)
              .map(responseOp -> new PutResponse(responseOp.getResponsePut()))
              .collect(Collectors.toList());

          putResponsesRef.lazySet(putResponses);
        }
      }
    }

    return putResponses;
  }
}
