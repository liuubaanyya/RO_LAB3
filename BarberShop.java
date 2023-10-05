import java.util.concurrent.*;

public class BarberShop {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(2); // Два потоки: перукар і відвідувач

        Barber barber = new Barber();
        executorService.submit(barber); // Запускаємо перукаря в окремому потоці

        // Моделюємо відвідувачів, які приходять на стрижку
        for (int i = 1; i <= 5; i++) {
            Customer customer = new Customer(i, barber);
            executorService.submit(customer); // Запускаємо відвідувача в окремому потоці
        }

        executorService.shutdown(); // Завершуємо виконання потоків після завершення всіх відвідувачів
    }
}

class Barber implements Runnable {
    private BlockingQueue<Customer> customerQueue = new LinkedBlockingQueue<>();

    @Override
    public void run() {
        while (true) {
            try {
                Customer customer = customerQueue.take(); // Чекаємо, поки відвідувач прийде
                System.out.println("Перукар почав стрижку відвідувачу " + customer.getId());
                Thread.sleep(2000); // Моделюємо тривалість стрижки
                System.out.println("Перукар завершив стрижку відвідувачу " + customer.getId());
                customer.completeHaircut(); // Повідомляємо відвідувача, що стрижка завершена
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void addCustomer(Customer customer) {
        customerQueue.add(customer); // Додаємо відвідувача до черги
    }
}

class Customer implements Runnable {
    private int id;
    private Barber barber;

    public Customer(int id, Barber barber) {
        this.id = id;
        this.barber = barber;
    }

    public int getId() {
        return id;
    }

    @Override
    public void run() {
        try {
            System.out.println("Відвідувач " + id + " прийшов до перукарні");
            barber.addCustomer(this); // Додаємо себе до черги перукаря
            Thread.sleep(1000); // Моделюємо час очікування в черзі
            System.out.println("Відвідувач " + id + " почав стрижку");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void completeHaircut() {
        System.out.println("Відвідувач " + id + " завершив стрижку і покинув перукарню");
    }
}
