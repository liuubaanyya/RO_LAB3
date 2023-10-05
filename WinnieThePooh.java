import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WinnieThePooh {
    private final Lock lock = new ReentrantLock();
    private final Condition honeyPotNotEmpty = lock.newCondition();
    private final Condition honeyPotNotFull = lock.newCondition();

    private int honeyPotCapacity;
    private int honeyPotContent = 0;

    public WinnieThePooh(int honeyPotCapacity) {
        this.honeyPotCapacity = honeyPotCapacity;
    }

    public void beeCollectHoney(int beeId) throws InterruptedException {
        lock.lock();
        try {
            while (honeyPotContent == honeyPotCapacity) {
                // Горщик повний, бджоли сплять
                honeyPotNotFull.await();
            }
            // Збирання меду бджолою
            System.out.println("Бджола " + beeId + " збирає мед.");
            honeyPotContent++;
            // Якщо горщик заповнений, будимо ведмедя
            if (honeyPotContent == honeyPotCapacity) {
                honeyPotNotEmpty.signal();
            }
        } finally {
            lock.unlock();
        }
    }

    public void bearEatHoney() throws InterruptedException {
        lock.lock();
        try {
            while (honeyPotContent < honeyPotCapacity) {
                // Горщик порожній, ведмідь спить
                honeyPotNotEmpty.await();
            }
            // Ведмідь їсть мед
            System.out.println("Ведмідь їсть мед.");
            honeyPotContent = 0; // Очищаємо горщик
            // Сповіщаємо бджіл, що горщик порожній і можна продовжувати збирати мед
            honeyPotNotFull.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        int honeyPotCapacity = 5;
        WinnieThePooh winnieThePooh = new WinnieThePooh(honeyPotCapacity);

        // Створення і запуск потоків бджіл
        for (int i = 1; i <= 10; i++) {
            final int beeId = i;
            new Thread(() -> {
                try {
                    while (true) {
                        winnieThePooh.beeCollectHoney(beeId);
                        Thread.sleep(1000); // Сплячка бджіл
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }

        // Створення і запуск потоку ведмедя
        new Thread(() -> {
            try {
                while (true) {
                    winnieThePooh.bearEatHoney();
                    Thread.sleep(2000); // Сплячка ведмедя
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}
