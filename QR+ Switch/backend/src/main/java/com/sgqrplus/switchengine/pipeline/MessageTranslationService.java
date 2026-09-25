package com.sgqrplus.switchengine.pipeline;

import com.sgqrplus.switchengine.domain.Scheme;
import com.sgqrplus.switchengine.domain.Transaction;
import org.springframework.stereotype.Service;

/**
 * Stage D: Message Translation.
 *
 * Maps the common internal SGQR+ transaction representation into the
 * destination scheme's native message shape. Each scheme declares its own
 * messageFormat (e.g. "PAYNOW_JSON_V1", "NETS_ISO8583_LITE"); this
 * prototype renders a simple representative payload per format rather than
 * a full protocol implementation.
 */
@Service
public class MessageTranslationService {

    public String translate(Transaction transaction, Scheme destinationScheme) {
        return switch (destinationScheme.getMessageFormat()) {
            case "PAYNOW_JSON_V1" -> String.format(
                    "{\"msgType\":\"PAYNOW.QR.PUSH\",\"proxyType\":\"MERCHANT\",\"acquirerId\":%d,\"issuerId\":%d,\"amount\":%s,\"currency\":\"SGD\"}",
                    transaction.getAcquirer().getId(), transaction.getIssuer().getId(), transaction.getAmount());
            case "NETS_ISO8583_LITE" -> String.format(
                    "MTI=0200;PAN_PROXY=%d;ACQ_ID=%d;AMT=%s;CUR=702",
                    transaction.getIssuer().getId(), transaction.getAcquirer().getId(), transaction.getAmount());
            default -> String.format(
                    "{\"scheme\":\"%s\",\"acquirerId\":%d,\"issuerId\":%d,\"amount\":%s}",
                    destinationScheme.getCode(), transaction.getAcquirer().getId(),
                    transaction.getIssuer().getId(), transaction.getAmount());
        };
    }
}
