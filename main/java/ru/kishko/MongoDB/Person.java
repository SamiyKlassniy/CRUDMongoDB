package ru.kishko.MongoDB;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bson.Document;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Person {

    private String name;
    private String surname;
    private int age;

    public Document pojoToDoc(){
        Document doc = new Document();

        doc.put("name", this.getName());
        doc.put("surname", this.getSurname());
        doc.put("age", this.getAge());

        return doc;
    }

}
