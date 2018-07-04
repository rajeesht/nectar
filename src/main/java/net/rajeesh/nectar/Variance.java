package net.rajeesh.nectar;

import java.time.LocalDateTime;
import java.util.Comparator;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Variance {
    private Integer productId;
    private LocalDateTime varianceDate;
    private Double amount;

    public static class SortByProductIdThenVarianceDate implements Comparator<Variance> {
        @Override
        public int compare(Variance v1, Variance v2) {
            if (v1.productId.compareTo(v2.productId) == 0) {
                if (v1.varianceDate.isAfter(v2.varianceDate)) {
                    return 1;
                }
                else if (v1.varianceDate.isBefore(v2.varianceDate)) {
                    return -1;
                }
                else {
                    return 0;
                }
            }
            else {
                return v1.productId.compareTo(v2.productId);

            }
        }
    }
}