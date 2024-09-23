package com.fizikovnet.hw2;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {
        task1();
        task2();
        task3();
        task4();
        task5();
        task6();
        task7();
        task8();
        task9();
    }

    /**
     * 1) Реализуйте удаление из листа всех дубликатов
     */
    private static void task1() {
        List<Integer> ints = List.of(1,3,5,1,2,3,4,6,9,8,7,0,6,3,2,1,8);
        List<Integer> result = ints.stream().distinct().toList();
        System.out.println(result);
    }

    /**
     * 2) Найдите в списке целых чисел 3-е наибольшее число
     *      (пример: 5 2 10 9 4 3 10 1 13 => 10)
     */
    private static void task2() {
        List<Integer> ints = List.of(5, 2, 10, 9, 4, 3, 10, 1, 13);
        int result = ints.stream()
                .sorted(Comparator.reverseOrder())
                .skip(2)
                .findFirst()
                .orElseThrow(RuntimeException::new);

        System.out.println(result);
    }

    /**
     * 3) Найдите в списке целых чисел 3-е наибольшее «уникальное» число
     *      (пример: 5 2 10 9 4 3 10 1 13 => 9,
     *      в отличие от прошлой задачи здесь разные 10 считает за одно число)
     */
    private static void task3() {
        List<Integer> ints = List.of(5, 2, 10, 9, 4, 3, 10, 1, 13);
        int result = ints.stream()
                .distinct()
                .sorted(Comparator.reverseOrder())
                .skip(2)
                .findFirst()
                .orElseThrow(RuntimeException::new);

        System.out.println(result);
    }

    /**
     * 4) Имеется список объектов типа Сотрудник (имя, возраст, должность),
     *      необходимо получить список имен 3 самых старших сотрудников с должностью «Инженер»,
     *      в порядке убывания возраста
     */
    private static void task4() {
        List<Employee> employees = List.of(
                new Employee("Александр", 20, "Инженер"),
                new Employee("Михаил", 25, "Мэнеджер"),
                new Employee("Алексей", 30, "Бухгалтер"),
                new Employee("Владимир", 51, "Инженер"),
                new Employee("Олег", 34, "Консультант"),
                new Employee("Иван", 33, "Бухгалтер"),
                new Employee("Матвей", 27, "Инженер"),
                new Employee("Павел", 43, "Инженер"),
                new Employee("Евгений", 35, "Продавец"));

        List<Employee> result = employees.stream()
                .filter(e -> e.getJob().equals("Инженер"))
                .sorted(Comparator.comparingInt(Employee::getAge).reversed())
                .limit(3)
                .toList();

        System.out.println(result);
    }


    /**
     * 5) Имеется список объектов типа Сотрудник (имя, возраст, должность),
     *      посчитайте средний возраст сотрудников с должностью «Инженер»
     */
    private static void task5() {
        List<Employee> employees = List.of(
                new Employee("Александр", 20, "Инженер"),
                new Employee("Михаил", 25, "Мэнеджер"),
                new Employee("Алексей", 30, "Бухгалтер"),
                new Employee("Владимир", 51, "Инженер"),
                new Employee("Олег", 34, "Консультант"),
                new Employee("Иван", 33, "Бухгалтер"),
                new Employee("Матвей", 27, "Инженер"),
                new Employee("Павел", 43, "Инженер"),
                new Employee("Евгений", 35, "Продавец"));

        double avgOfEngineers = employees.stream()
                .filter(e -> e.getJob().equals("Инженер"))
                .mapToInt(Employee::getAge)
                .average()
                .orElseThrow(RuntimeException::new);

        System.out.println("Average age of Engineers: " + avgOfEngineers);
    }

    /**
     * 6) Найдите в списке слов самое длинное
     */
    private static void task6() {
        List<String> words = List.of("bread","responsibility","definition",
                "sun", "entertainment","leadership","dad","session","physics",
                "office","woman","story","way","salad","expression",
                "mode","version","newspaper","air","river");

        String result = words.stream()
                .max(Comparator.comparingInt(String::length))
                .orElseThrow(RuntimeException::new);

        System.out.println("Longest word: " + result);
    }

    /**
     * 7) Имеется строка с набором слов в нижнем регистре, разделенных пробелом.
     *      Постройте хеш-мапы, в которой будут хранится пары:
     *      слово - сколько раз оно встречается во входной строке
     */
    private static void task7() {
        String s = "jump order age increase matter age engage accompany jump matter jump attain order age excuse retire age order engage";
        Map<String, Long> result = Stream.of(s.split("\\s"))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        System.out.println(result);
    }

    /**
     * 8) Отпечатайте в консоль слова из списка в порядке увеличения длины слова,
     *      если слова имеют одинаковую длины, то должен быть сохранен алфавитный порядок
     */
    private static void task8() {
        List<String> words = List.of("bread","responsibility","definition",
                "sun", "entertainment","leadership","dad","session","physics",
                "office","woman","story","way","salad","expression",
                "mode","version","newspaper","air","river", "apple");
        words.stream()
                .sorted(Comparator.comparing(String::length)
                        .thenComparing(Comparator.naturalOrder()))
                .forEach(System.out::println);
    }

    /**
     * 9) Имеется массив строк, в каждой из которых лежит набор из 5 слов,
     *      разделенных пробелом, найдите среди всех слов самое длинное,
     *      если таких слов несколько, получите любое из них
     */
    private static void task9() {
        String[] strings = new String[]{"direction arrival affair love quality",
                "conclusion breath son debt environment",
                "category contract flight establishment replacement",
                "thought reputation son union sector",
                "system election friendship year people"};

        String result = Arrays.stream(strings)
                .map(s -> s.split("\\s"))
                .flatMap(Arrays::stream)
                .max(Comparator.comparingInt(String::length))
                .orElseThrow(RuntimeException::new);

        System.out.println("Longest word from lists: " + result);
    }
}
