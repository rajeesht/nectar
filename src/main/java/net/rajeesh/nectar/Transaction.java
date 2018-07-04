package net.rajeesh.nectar;

import java.time.LocalDateTime;
import java.util.Comparator;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Transaction {

    private Integer productId;
    private LocalDateTime recordDate;
    private Double amount;

    public static class SortByRecordDateThenProductId implements Comparator<Transaction> {
        @Override
        public int compare(Transaction o1, Transaction o2) {
            if (o1.recordDate.isAfter(o2.recordDate)) {
                return 1;
            }
            else if (o1.recordDate.isBefore(o2.recordDate)) {
                return -1;
            }
            else {
                return o1.productId.compareTo(o2.productId);
            }

        }
    }

    public static class SortByAmount implements Comparator<Transaction> {
        @Override
        public int compare(Transaction o1, Transaction o2) {
            return o1.amount.compareTo(o2.amount);
        }
    }
}

