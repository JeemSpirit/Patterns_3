package Strategy

abstract class PaymentStrategy {
    abstract fun processPayment(amount: Double)
}

class CashPayment : PaymentStrategy() {
    override fun processPayment(amount: Double) {
        println("Обработка наличного платежа на сумму: $amount руб.")
        println("Ожидание получения наличных...")
        println("Платёж успешно завершён")
    }
}

class BankTransferPayment : PaymentStrategy() {
    override fun processPayment(amount: Double) {
        println("Обработка банковского перевода на сумму: $amount руб.")
        println("Соединение с банковской системой...")
        println("Средства успешно переведены")
    }
}

class CashOnDeliveryPayment : PaymentStrategy() {
    override fun processPayment(amount: Double) {
        println("Оформление оплаты при получении на сумму: $amount руб.")
        println("Заказ будет оплачен курьеру при доставке")
        println("Статус: ожидание доставки")
    }
}

class Order(private var paymentStrategy: PaymentStrategy) {

    fun setPaymentStrategy(strategy: PaymentStrategy) {
        this.paymentStrategy = strategy
    }

    fun processOrder(amount: Double) {
        println("Обработка заказа...")
        paymentStrategy.processPayment(amount)
        println("Заказ успешно обработан")
    }
}

fun main() {
    val order = Order(CashPayment())

    println("-----------------Тест 1: Наличный платёж--------------------------")
    order.processOrder(1500.0)

    println("\n---------------------Тест 2: Смена на банковский перевод------------------")
    order.setPaymentStrategy(BankTransferPayment())
    order.processOrder(3200.0)

    println("\n------------------Тест 3: Смена на оплату при получении--------------------")
    order.setPaymentStrategy(CashOnDeliveryPayment())
    order.processOrder(899.99)
}

/*
Инструкция по добавлению нового метода оплаты (например, криптовалюты):

1. Создать новый класс, наследующийся от PaymentStrategy
2. Реализовать метод processPayment(amount: Double) в новом классе
3. В методе processPayment описать логику обработки платежа
4. Для использования нового метода:
   - Создать экземпляр нового класса
   - Передать его в Order через конструктор или метод setPaymentStrategy()

Пример структуры нового класса:

class CryptoPayment : PaymentStrategy() {
    override fun processPayment(amount: Double) {
        // Реализация обработки криптовалютного платежа
        println("Обработка криптовалютного платежа...")
        println("Конвертация суммы в криптовалюту...")
        println("Генерация QR-кода для оплаты...")
    }
}

Использование:
val cryptoPayment = CryptoPayment()
order.setPaymentStrategy(cryptoPayment)
order.processOrder(5000.0)
*/