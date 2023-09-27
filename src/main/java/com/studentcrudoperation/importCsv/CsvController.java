package com.studentcrudoperation.importCsv;

import java.io.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/csv")
public class CsvController {
    @Autowired
    CsvRepo csvRepo;

    enum HEADERS {
        id, date, open, high, low, close, volume
    }

    @PostMapping("/import")
    public List<CsvModel> importData() {
        List<CsvModel> blogPosts = new ArrayList<>();
        try (Reader in = new FileReader(getClass().getClassLoader().getResource("ACC.csv").getPath())) {
            Iterable<CSVRecord> records = CSVFormat.RFC4180.withHeader(HEADERS.class).parse(in);
            records.iterator().next();

            ExecutorService s1 = Executors.newFixedThreadPool(10);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssXXX").withZone(ZoneId.systemDefault());

            for (CSVRecord record : records) {
                CsvModel cm = new CsvModel();

                cm.setId(Long.valueOf(record.get(HEADERS.id)));
                cm.setClose(Double.valueOf(record.get(HEADERS.close)));
                String parsedDate = formatter.format(Instant.from(formatter.parse(record.get(HEADERS.date))));
                cm.setDate(parsedDate);

                cm.setHigh(Double.valueOf(record.get(HEADERS.high)));
                cm.setOpen(Double.valueOf(record.get(HEADERS.open)));
                cm.setVolume(Integer.valueOf(record.get(HEADERS.volume)));
                cm.setLow(Double.valueOf(record.get(HEADERS.low)));
                blogPosts.add(cm);

                s1.submit(() -> {
                    csvRepo.save(cm);
                });
            }

            s1.shutdown();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return blogPosts;
    }
    @GetMapping("/show")
    public List<CsvModel> getAll(){
        return csvRepo.findAll();
    }
    @PostMapping("/insert")
    public CsvModel insertData(@RequestBody CsvModel model){
        return csvRepo.save(model);
    }
    @GetMapping("/get/{id}")
    public CsvModel getById(@PathVariable(value = "id") Long id){
        return csvRepo.findById(id).get();
    }
}
