package net.rajeesh.nectar;

import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

public class Main {

    private static final String SOLD_FILE = "sold.csv";
    private static final String USED_FILE = "used.csv";
    private static final Integer TIME_RANGE = 10;

    private final List<Transaction> soldTransactions;
    private final List<Transaction> usedTransactions;
    private final List<Variance> variances = new ArrayList<>();

    public static void main(String[] args) throws IOException, ParseException {
        new Main().process();
    }

    Main() throws IOException {
        soldTransactions = readCsvFile(SOLD_FILE);
        usedTransactions = readCsvFile(USED_FILE);
    }

    private List<Transaction> readCsvFile(String fileName) throws IOException {
        List<Transaction> transactionList = new ArrayList<>();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String csvFile = getClass().getClassLoader().getResource(fileName).getFile();
        CSVReader reader = new CSVReaderBuilder((new FileReader(csvFile))).withSkipLines(1).build();

        reader.forEach(nextLine -> {
            transactionList.add(Transaction.builder()
                    .productId(Integer.parseInt(nextLine[0]))
                    .recordDate(LocalDateTime.parse(nextLine[1], dateFormatter))
                    .amount(Double.parseDouble(nextLine[2]))
                    .build());

        });

        transactionList.sort(new Transaction.SortByRecordDateThenProductId());
        return transactionList;
    }

    private void process() {
        soldTransactions.forEach(sale -> {
            Optional<Transaction> eligibleUse = usedTransactions
                    .stream()
                    .filter(use -> isDateInRange(sale.getRecordDate(), use.getRecordDate())
                            && isSameProduct(sale.getProductId(), use.getProductId()))
                    .sorted(new Transaction.SortByAmount().reversed())
                    .findFirst();

            if (eligibleUse.isPresent()) {
                variances.add(Variance.builder()
                        .productId(sale.getProductId())
                        .varianceDate(sale.getRecordDate())
                        .amount(sale.getAmount() - eligibleUse.get().getAmount()).build());
                usedTransactions.remove(eligibleUse.get());
            }

        });

        variances.sort(new Variance.SortByProductIdThenVarianceDate());

        // List raw data
        variances.forEach(variance -> {
            System.out.println(String.format("%s,%s,%.4f",
                    variance.getProductId(), variance.getVarianceDate(), variance.getAmount()));
        });

        // List by product
        variances.stream()
                .map(variance -> new AbstractMap.SimpleEntry<>(variance.getProductId(), variance.getAmount()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Double::sum))
                .forEach((key, value) -> {
            System.out.println(String.format("%s,%.4f", key, value));
        });

        // List total spillage
        System.out.println(variances.stream().mapToDouble(each -> each.getAmount()).reduce(0.0, (a1, a2) -> a1 + a2));
    }

    private boolean isDateInRange(LocalDateTime saletime, LocalDateTime usedTime) {
        LocalDateTime startTime = saletime.minus(Duration.ofMinutes(10));
        LocalDateTime cutoffTime = saletime.plus(Duration.ofMinutes(10));
        return !(usedTime.isBefore(startTime) || usedTime.isAfter(cutoffTime));
    }

    private boolean isSameProduct(Integer first, Integer second) {
        return first.compareTo(second) == 0;
    }
}
