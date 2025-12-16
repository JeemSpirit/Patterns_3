package Template_Method

// Абстрактный класс оформления заказа (Template Method)
abstract class OrderProcessing {

    // Шаблонный метод — задает общий алгоритм
    fun processOrder() {
        selectProduct()
        createOrder()
        pay()
        deliver()
    }

    protected open fun selectProduct() {
        println("Выбор товара из каталога")
    }

    protected open fun createOrder() {
        println("Оформление заказа в системе")
    }

    protected abstract fun pay()

    protected abstract fun deliver()
}

// Стандартная обработка заказа
class StandardOrderProcessing : OrderProcessing() {

    override fun pay() {
        println("Оплата стандартным способом (карта или наличные)")
    }

    override fun deliver() {
        println("Стандартная доставка курьером в течение 3–5 дней")
    }
}

// Экспресс-обработка заказа
class ExpressOrderProcessing : OrderProcessing() {

    override fun pay() {
        println("Оплата моментальная (онлайн)")
    }

    override fun deliver() {
        println("Экспресс-доставка за 1 день")
    }
}

class Order(private val processor: OrderProcessing) {
    fun process() {
        processor.processOrder()
    }
}

fun main() {
    val standardOrder = Order(StandardOrderProcessing())
    val expressOrder = Order(ExpressOrderProcessing())

    standardOrder.process()
    expressOrder.process()
}
/*
 Ответ на вопрос:
 Чтобы расширить систему и добавить новый тип заказа, например "заказ с предоплатой",
 необходимо создать новый подкласс, например PrepaidOrderProcessing,
 который наследуется от Template_Method.OrderProcessing и переопределяет шаги,
 связанные с оплатой и доставкой.

 Изменения потребуются минимальные: только создание нового подкласса.
 Благодаря паттерну Template Method структура алгоритма не изменяется,
 так как метод processOrder() остается неизменным.

 Для реализации обработки предоплаты переопределяется метод pay().
 В нем будет выполнена логика проверки внесенной предоплаты.
 Дополнительно можно расширить метод deliver(), если доставка зависит от подтверждения оплаты.

 Пример:

 class PrepaidOrderProcessing : Template_Method.OrderProcessing() {
     override fun pay() {
         println("Проверка внесенной предоплаты и подтверждение платежа")
     }

     override fun deliver() {
         println("Доставка заказа после проверки предоплаты")
     }
 }
 Добавление нового типа заказа требует минимальных изменений
 и не затрагивает существующий код алгоритма, что соответствует принципу OCP.
 */
