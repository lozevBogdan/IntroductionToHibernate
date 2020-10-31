import entities.*;
import javax.persistence.EntityManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class Engine implements Runnable {

    private final EntityManager entityManager;
    private BufferedReader reader;

    public Engine(EntityManager entityManager) {

        this.reader = new BufferedReader(new InputStreamReader(System.in));
        this.entityManager = entityManager;
    }


    @Override
    public void run() {
        System.out.println("Welcome to Homework: " +
                "'Exercises: Introduction to Hibernate',");

        System.out.println(
                "Please enter which exercise you want to check (2-13) " +
                        "or STOP for exit:");
        try {
            String ex = reader.readLine();

            while (!ex.equalsIgnoreCase("STOP")) {

                switch (ex) {

                    case "2":
                        changeCasingEx2();
                        break;
                    case "3":
                        try {
                            containsEmployeeEx3();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "4":
                        employeesWithSalaryOver50000Ex4();
                        break;
                    case "5":
                        employeesFromDepartmentsEx5();
                        break;
                    case "6":
                        try {
                            addingANewAddressAndUpdatingEmployeeEx6();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "7":
                        addressWithEmployeeCountEx7();
                        break;
                    case "8":
                        try {
                            getEmployeeWithProjectEx8();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "9":
                        findLatest10ProjectsEx9();
                        break;
                    case "10":
                        increaseSalariesEx10();
                        break;
                    case "11":
                        try {
                            findEmployeesByFirstNameEx11();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "12":
                        employeesMaximumSalariesEx12();
                        break;
                case "13" :
                    try {
                        removeTownsEx13();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                    default:
                        System.out.println("Invalid exercise, try again:");
                }
                System.out.println(
                        "Please enter which exercise you want to check (2-13) " +
                                "or STOP for exit:");
                ex = reader.readLine();

            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void removeTownsEx13() throws IOException {

        System.out.print("Enter a town you want to delete: ");
        String townName = reader.readLine();
        Town town = entityManager.createQuery("SELECT t FROM Town AS t " +
                "WHERE t.name = :name",Town.class)
                .setParameter("name",townName)
                .getSingleResult();

        List<Address> addresses = entityManager
                .createQuery("SELECT a FROM Address AS a " +
                "WHERE a.town.name = :name",Address.class)
                .setParameter("name",townName)
                .getResultList();

        String print = String.format("%d address%s in %s deleted%n",
                addresses.size(),
                (addresses.size() != 1) ? "es" : "",town.getName());

        entityManager.getTransaction().begin();
        addresses.forEach(address -> {
            for (Employee employee : address.getEmployees()) {
                employee.setAddress(null);
            }
            address.setTown(null);
            entityManager.remove(address);
        });
        entityManager.remove(town);
        entityManager.getTransaction().commit();
        System.out.println(print);













//        System.out.println("Enter town which names you want to delete:");
//        String townName = reader.readLine();
//
//        Integer id = entityManager.createQuery("select t.id from Town t " +
//                "where t.name = :name",Integer.class)
//                .setParameter("name",townName)
//                .getSingleResult();
//
//        entityManager.getTransaction().begin();
//
//
//
//        entityManager.createQuery(
//                "update Employee e " +
//                       // "join Address a on e.address.id = a.id" +
//                "set e.address.id = 0 " +
//                "where e.address.town.id = :id")
//                .setParameter("id",id)
//        .executeUpdate();
//
//
//        entityManager.getTransaction().commit();
//
//        System.out.println();
//
//        entityManager.getTransaction().begin();
//
//       int effectedRows = entityManager.createQuery("delete from Address a " +
//                "where a.town.id = :name")
//                .setParameter("name",id)
//                .executeUpdate();
//        entityManager.getTransaction().commit();
//
//        String addresses = effectedRows >1 ? "addresses" : "address";
//
//        System.out.printf("%d %s in $s deleted"
//                ,effectedRows,addresses,townName);



    }

    private void employeesMaximumSalariesEx12() {

      List<String> departmentList =  entityManager
                .createQuery("select d.name from Department d " +
                "join Employee e on e.department.id = d.id " +
                "group by d.id " +
                "having max(e.salary) not between 30000 and 70000"
                        ,String.class).getResultList();

        List<BigDecimal> doubleList =  entityManager
                .createQuery("select  max(e.salary) from Department d " +
                                "join Employee e on e.department.id = d.id " +
                                "group by d.id " +
                                "having max(e.salary) not between 30000 and 70000"
                        ,BigDecimal.class).getResultList();


        for (int i = 0; i <departmentList.size(); i++) {

            System.out.printf("%s %.2f%n", departmentList.get(i),doubleList.get(i));

        }



    }

    private void findLatest10ProjectsEx9() {

        entityManager.createQuery("select p from Project p " +
                "order by p.startDate desc", Project.class)
                .setMaxResults(10)
                .getResultStream()
                .sorted((p1,p2)->p1.getName().compareTo(p2.getName()) )
                .forEach(p->{

                    DateTimeFormatter format =
                          DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                    String startDate = format.format(p.getStartDate());

                    String endDate = p.getEndDate() == null ? "null" :
                            format.format(p.getEndDate());


                    System.out.printf("Project name: %s%n",p.getName());
                    System.out.printf("\tProject Description: %s%n",p.getDescription());
                    System.out.println("\tProject Start Date: " + startDate);
                    System.out.println("\tProject Start Date: " + endDate);


                });

    }

    private void findEmployeesByFirstNameEx11() throws IOException {

        System.out.println("Enter a pattern: ");
        String input = reader.readLine();



        entityManager.createQuery("select e from Employee e " +
                "where e.firstName like :pattern",Employee.class)
                .setParameter("pattern",input+"%")
                .getResultList()
                .forEach(e-> System.out.printf("%s %s - %s - ($%.2f)%n"
                ,e.getFirstName(),e.getLastName(),e.getJobTitle(), e.getSalary()));

    }

    private void increaseSalariesEx10() {

        entityManager.getTransaction().begin();

        int effectedRows = entityManager.createQuery("update Employee e " +
                "set e.salary = e.salary*1.12 " +
                "where e.department.id in (1,2,4,11)")
                .executeUpdate();

        entityManager.getTransaction().commit();

        System.out.println("Affected rows " + effectedRows);

        List<Employee> employees = entityManager
                .createQuery("select e from Employee e " +
                "where e.department.id in (1,2,4,11)",Employee.class)
                .getResultList();

        employees.forEach(e-> System.out.printf("%s %s ($%.2f)%n"
        , e.getFirstName(),e.getLastName(),e.getSalary()));

    }

    private void getEmployeeWithProjectEx8() throws IOException {

        System.out.println("Enter valid employee id:");

        int id = Integer.parseInt(reader.readLine());

       Employee employee = entityManager.find(Employee.class,id);

        System.out.printf("%s %s - %s%n",
                employee.getFirstName(),employee.getLastName()
                ,employee.getJobTitle());

        employee.getProjects()
                .stream()
                .sorted((p1,p2)-> p1.getName().compareTo(p2.getName()))
                .forEach( e-> System.out.printf("\t%s%n",e.getName()));


    }

    private void addressWithEmployeeCountEx7() {
        List<Address> addresses = entityManager
                .createQuery("select a from Address a " +
                        "order by a.employees.size desc ",Address.class)
                .setMaxResults(10)
                .getResultList();

        addresses.forEach(a-> {
            System.out.printf("%s, %s - %d employees %n"
            ,a.getText(),
                    a.getTown() == null ? "null":
                    a.getTown().getName(),a.getEmployees().size());

        });

    }

    private void addingANewAddressAndUpdatingEmployeeEx6() throws IOException {

        Address address = createAddress("Vitoshka 15");

        System.out.println("Enter last name of employee:");
        String name = reader.readLine();

        Employee employee = entityManager
                .createQuery("select e from Employee e " +
                        "where e.lastName = :name",Employee.class)
                .setParameter("name",name)
                .getSingleResult();

        entityManager.getTransaction().begin();
        employee.setAddress(address);
        entityManager.getTransaction().commit();


    }

    private Address createAddress(String text) {

        Address address = new Address();
        address.setText(text);

        entityManager.getTransaction().begin();
        entityManager.persist(address);
        entityManager.getTransaction().commit();

        return address;

    }

    private void employeesFromDepartmentsEx5() {

        entityManager.createQuery("select e from Employee  as e " +
                "where e.department.name = 'Research and Development'" +
                "order by e.salary,e.id",Employee.class)
                .getResultStream()
                .forEach(e-> {
                    System.out.printf("%s %s from Research and Development - $%.2f%n",
                            e.getFirstName(),e.getLastName(),e.getSalary());

                });

    }

    private void employeesWithSalaryOver50000Ex4() {
        entityManager.createQuery("select e from Employee e " +
                "where e.salary > 50000",Employee.class)
                .getResultStream()
                .map(Employee::getFirstName)
                .forEach(System.out::println);



    }

    private void containsEmployeeEx3() throws IOException {
        System.out.println("Enter Employee full name:");

        String fullName = reader.readLine();

        List<Employee> employees = entityManager.createQuery("Select e from Employee as e " +
                "WHERE concat(e.firstName,' ', e.lastName) = :name",Employee.class)
                .setParameter("name",fullName)
                .getResultList();

        System.out.println(employees.size() == 0 ? "NO" : "YES");


    }

    private void changeCasingEx2() {

        List<Town> towns = entityManager
                .createQuery("select t from Town t " +
                        "where length(t.name) <= 5", Town.class)
                .getResultList();
        entityManager.getTransaction().begin();
        towns.forEach(entityManager::detach);
        for (Town town : towns) {
            town.setName(town.getName().toLowerCase());
        }
        towns.forEach(entityManager::merge);
        entityManager.flush();
        entityManager.getTransaction().commit();

        towns.forEach(t-> System.out.println(t.getName()));

    }
}






//ex2
// changeCasingEx2();

//ex3

//        try {
//            containsEmployeeEx3();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//ex4
//  employeesWithSalaryOver50000Ex4();

//ex5
//   employeesFromDepartmentsEx5();

//ex6

//        try {
//            addingANewAddressAndUpdatingEmployeeEx6();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//ex 7

//        addressWithEmployeeCountEx7();

//Ex8

//        try {
//            getEmployeeWithProjectEx8();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//Ex9

// TO DO - formating the date !!!!!!!!!!!!
//        findLatest10ProjectsEx9();

//Ex10
// increaseSalariesEx10();


//Ex11

//        try {
//            findEmployeesByFirstNameEx11();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }



//ex12

//        employeesMaximumSalariesEx12();

//Ex13

//        try {
//            removeTownsEx13();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
