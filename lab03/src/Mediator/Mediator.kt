package Mediator

// Абстрактный посредник
abstract class Mediator {
    abstract fun notify(sender: Component, event: String)
}

// Базовый компонент, знает только посредника
abstract class Component(protected val mediator: Mediator) {
    abstract fun receive(message: String)
}

// Конкретный компонент — клиент
class Client(mediator: Mediator) : Component(mediator) {

    fun createOrder() {
        mediator.notify(this, "Создать заказ")
    }

    override fun receive(message: String) {
        println("Клиент получил сообщение: $message")
    }
}

// Конкретный компонент — менеджер
class Manager(mediator: Mediator) : Component(mediator) {

    fun processOrder() {
        mediator.notify(this, "Обработать заказ")
    }

    override fun receive(message: String) {
        println("Менеджер получил сообщение: $message")
    }
}

// Конкретный компонент — склад
class Warehouse(mediator: Mediator) : Component(mediator) {

    fun shipOrder() {
        mediator.notify(this, "Отправить заказ")
    }

    override fun receive(message: String) {
        println("Склад получил сообщение: $message")
    }
}

// Конкретный посредник
class OrderMediator : Mediator() {

    lateinit var client: Client
    lateinit var manager: Manager
    lateinit var warehouse: Warehouse

    override fun notify(sender: Component, event: String) {

        when (sender) {
            client -> {
                if (event == "Создать заказ") {
                    manager.receive("Новый заказ от клиента")
                }
            }
            manager -> {
                if (event == "Обработать заказ") {
                    warehouse.receive("Подготовить заказ к отправке")
                }
            }
            warehouse -> {
                if (event == "Отправить заказ") {
                    client.receive("Ваш заказ отправлен")
                }
            }
        }
    }
}

// Точка входа
fun main() {
    val mediator = OrderMediator()

    val client = Client(mediator)
    val manager = Manager(mediator)
    val warehouse = Warehouse(mediator)

    mediator.client = client
    mediator.manager = manager
    mediator.warehouse = warehouse

    client.createOrder()
    manager.processOrder()
    warehouse.shipOrder()
}
