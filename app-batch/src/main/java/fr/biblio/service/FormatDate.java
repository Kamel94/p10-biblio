package fr.biblio.service;

import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class FormatDate {

    public String patternDate(Date date) {

            String pattern = "dd/MM/yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

            String dateString = simpleDateFormat.format(date);

            return dateString;
    }

}
