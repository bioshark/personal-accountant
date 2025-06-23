package org.roly.personalaccountant.services;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.roly.personalaccountant.dto.MonthlyExpenses;
import org.springframework.stereotype.Service;

@Service
public class ExpenseSerializer {

    // TODO replace with jackson serializers

    public void javaSerialize(MonthlyExpenses monthlyExpenses) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(monthlyExpenses.getStartDate() + "-expense.ser"))) {
            oos.writeObject(monthlyExpenses);
            System.out.println("Object serialized successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MonthlyExpenses javaDeserialize(String expense) {
        MonthlyExpenses monthlyExpenses;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(expense))) {
            monthlyExpenses = (MonthlyExpenses) ois.readObject();
            System.out.println("Deserialized object: " + monthlyExpenses);
        } catch (IOException | ClassNotFoundException e) {
            monthlyExpenses = null;
        }
        return monthlyExpenses;
    }
}
