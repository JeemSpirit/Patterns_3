package Strategy

// Стратегия оплаты
abstract class PaymentStrategy {
    abstract fun processPayment(amount: Double)
}

// --- Конкретные стратегии ---

// Оплата кредитной картой
class CreditCardPayment(
    private val cardNumber: String,
    private val ownerName: String
) : PaymentStrategy() {

    override fun processPayment(amount: Double) {
        println("Оплата $amount руб. кредитной картой $cardNumber на имя $ownerName.")
        println("Транзакция успешно проведена.")
    }
}

// Оплата через PayPal
class PayPalPayment(
    private val email: String
) : PaymentStrategy() {

    override fun processPayment(amount: Double) {
        println("Оплата $amount руб. через PayPal аккаунт $email.")
        println("Платёж успешно подтверждён PayPal.")
    }
}

// Наличные при получении
class CashOnDeliveryPayment : PaymentStrategy() {

    override fun processPayment(amount: Double) {
        println("Оплата наличными при получении заказа.")
        println("Заказ будет оплачен курьеру при доставке.")
    }
}

// Оплата через «Сбербанк Онлайн»
class SberbankOnlinePayment(
    private val phoneNumber: String
) : PaymentStrategy() {

    override fun processPayment(amount: Double) {
        println("Отправка запроса в API Сбербанк Онлайн для номера $phoneNumber...")
        println("Сбербанк подтвердил оплату $amount руб.")
    }
}


// Класс заказа, использующий стратегию оплаты
class Order1(
    private var paymentStrategy: PaymentStrategy
) {
    fun setPaymentStrategy(strategy: PaymentStrategy) {
        this.paymentStrategy = strategy
    }

    fun checkout(amount: Double) {
        println("Начало обработки платежа...")
        paymentStrategy.processPayment(amount)
        println("Заказ успешно оплачен.\n")
    }
}


// 1. Создать новый класс, который реализует интерфейс Strategy.PaymentStrategy.
//    Например: class CryptoPayment : Strategy.PaymentStrategy { … }
//
// 2. Переопределить метод processPayment(amount: Double)
//    и реализовать в нём логику оплаты.
//
// 3. В нужный момент передать объект новой стратегии в Template_Method.Order:
//       order.setPaymentStrategy(CryptoPayment())
//
// 4. НЕ НУЖНО изменять уже существующие классы.
//    Паттерн Strategy обеспечивает расширяемость.

fun main() {

    // Заказ
    val order = Order1(CreditCardPayment("1234 5678 9012 3456", "Иван Иванов"))

    // Оплата кредиткой
    order.checkout(1500.0)

    // Клиент меняет способ оплаты на PayPal
    order.setPaymentStrategy(PayPalPayment("user@example.com"))
    order.checkout(1500.0)

    // Наличные при получении
    order.setPaymentStrategy(CashOnDeliveryPayment())
    order.checkout(1500.0)

    // Сбербанк Онлайн
    order.setPaymentStrategy(SberbankOnlinePayment("+79991234567"))
    order.checkout(1500.0)
}
