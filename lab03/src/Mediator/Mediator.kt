package Mediator

abstract class Mediator {
    abstract fun notify(sender: Component, event: String, data: Any? = null)
}

abstract class Component(protected val mediator: Mediator) {
    abstract fun receive(sender: Component, message: String, data: Any? = null)

    protected fun send(event: String, data: Any? = null) {
        mediator.notify(this, event, data)
    }
}

class Client(mediator: Mediator) : Component(mediator) {
    private val orders = mutableListOf<Int>()

    fun createOrder(productId: Int, quantity: Int) {
        println("Клиент: Создаю заказ на товар $productId, количество: $quantity")
        send("CREATE_ORDER", mapOf("productId" to productId, "quantity" to quantity))
    }

    override fun receive(sender: Component, message: String, data: Any?) {
        when (message) {
            "ORDER_SHIPPED" -> {
                val orderId = data as? Int ?: return
                orders.remove(orderId)
                println("Клиент: Заказ $orderId отправлен. Спасибо!")
            }
            "ORDER_DELIVERED" -> {
                val orderId = data as? Int ?: return
                println("Клиент: Заказ $orderId доставлен. Отличный сервис!")
            }
            "ORDER_CANCELLED" -> {
                val reason = data as? String ?: "неизвестная причина"
                println("Клиент: Заказ отменен. Причина: $reason")
            }
            "STOCK_UNAVAILABLE" -> {
                val productId = data as? Int ?: return
                println("Клиент: Товар $productId отсутствует на складе")
            }
        }
    }
}

class Manager(mediator: Mediator) : Component(mediator) {
    private var isAvailable = true

    fun setAvailability(available: Boolean) {
        isAvailable = available
        println("Менеджер: Статус доступности изменен на $available")
    }

    override fun receive(sender: Component, message: String, data: Any?) {
        when (message) {
            "CREATE_ORDER" -> {
                if (!isAvailable) {
                    sender.receive(this, "ORDER_CANCELLED", "Менеджер недоступен")
                    return
                }

                val orderData = data as? Map<*, *> ?: return
                val productId = orderData["productId"] as? Int ?: return
                val quantity = orderData["quantity"] as? Int ?: return

                println("Менеджер: Обрабатываю заказ на товар $productId, количество: $quantity")

                if (productId > 1000) {
                    sender.receive(this, "ORDER_CANCELLED", "Неверный ID товара")
                    return
                }

                send("PROCESS_ORDER", orderData)
            }
            "STOCK_CHECK_RESULT" -> {
                val result = data as? Boolean ?: return
                if (result) {
                    println("Менеджер: Товар есть в наличии, подтверждаю заказ")
                    send("CONFIRM_ORDER", data)
                }
            }
        }
    }
}

class Warehouse(mediator: Mediator) : Component(mediator) {
    private val stock = mutableMapOf(
        1 to 100,
        2 to 50,
        3 to 200
    )

    override fun receive(sender: Component, message: String, data: Any?) {
        when (message) {
            "PROCESS_ORDER" -> {
                val orderData = data as? Map<*, *> ?: return
                val productId = orderData["productId"] as? Int ?: return
                val quantity = orderData["quantity"] as? Int ?: return

                println("Склад: Проверяю наличие товара $productId, требуется: $quantity")

                val available = stock[productId] ?: 0
                if (available >= quantity) {
                    stock[productId] = available - quantity
                    println("Склад: Товар $productId зарезервирован, остаток: ${stock[productId]}")
                    sender.receive(this, "STOCK_CHECK_RESULT", true)
                    send("SHIP_ORDER", orderData)
                } else {
                    println("Склад: Недостаточно товара $productId (доступно: $available, требуется: $quantity)")
                    sender.receive(this, "STOCK_CHECK_RESULT", false)
                    sender.receive(this, "STOCK_UNAVAILABLE", productId)
                }
            }
            "CONFIRM_ORDER" -> {
                val orderData = data as? Map<*, *> ?: return
                println("Склад: Заказ подтвержден, начинаю сборку")
                send("SHIP_ORDER", orderData)
            }
        }
    }

    fun checkStock(productId: Int): Int {
        return stock[productId] ?: 0
    }
}

class OrderMediator : Mediator() {
    private lateinit var client: Client
    private lateinit var manager: Manager
    private lateinit var warehouse: Warehouse

    private var orderCounter = 1

    fun initializeComponents(client: Client, manager: Manager, warehouse: Warehouse) {
        this.client = client
        this.manager = manager
        this.warehouse = warehouse
        println("Посредник: Все компоненты инициализированы")
    }

    override fun notify(sender: Component, event: String, data: Any?) {
        if (!this::client.isInitialized ||
            !this::manager.isInitialized ||
            !this::warehouse.isInitialized) {
            println("Ошибка: Не все компоненты инициализированы")
            return
        }

        val orderId = orderCounter++

        println("\nПосредник: Событие '$event' от ${sender::class.simpleName}")

        when {
            sender is Client && event == "CREATE_ORDER" -> {
                val orderData = (data as? Map<*, *>)?.toMutableMap() ?: mutableMapOf()
                orderData["orderId"] = orderId
                manager.receive(sender, event, orderData)
            }
            sender is Manager && event == "PROCESS_ORDER" -> {
                warehouse.receive(sender, event, data)
            }
            sender is Manager && event == "CONFIRM_ORDER" -> {
                warehouse.receive(sender, event, data)
            }
            sender is Warehouse && event == "SHIP_ORDER" -> {
                val orderData = data as? Map<*, *> ?: return
                val orderId = orderData["orderId"] as? Int ?: return
                client.receive(sender, "ORDER_SHIPPED", orderId)

                Thread.sleep(100)
                client.receive(sender, "ORDER_DELIVERED", orderId)
            }
            else -> {
                println("Посредник: Неизвестное событие '$event' от ${sender::class.simpleName}")
            }
        }
    }
}

fun main() {
    println("-------------------- Система управления заказами --------------------")

    val mediator = OrderMediator()

    val client = Client(mediator)
    val manager = Manager(mediator)
    val warehouse = Warehouse(mediator)

    mediator.initializeComponents(client, manager, warehouse)

    println("\n-------------------- Сценарий 1: Успешный заказ --------------------")
    client.createOrder(1, 5)

    println("\n-------------------- Сценарий 2: Недостаточно товара --------------------")
    client.createOrder(2, 100)

    println("\n-------------------- Сценарий 3: Менеджер недоступен --------------------")
    manager.setAvailability(false)
    client.createOrder(3, 10)

    println("\n-------------------- Сценарий 4: Неверный товар --------------------")
    manager.setAvailability(true)
    client.createOrder(9999, 1)
}

/*
Ответ на вопрос:
Для обеспечения безопасности при обработке сообщений между компонентами необходимо добавить:

1. Проверку авторизации - каждый компонент должен проверять, имеет ли отправитель право на выполнение операции
2. Валидацию входных данных - все данные должны проверяться на корректность и безопасность
3. Логирование всех операций - для аудита и отслеживания подозрительной активности
4. Шифрование конфиденциальных данных - при передаче личной информации
5. Ограничение частоты запросов - предотвращение DDoS-атак и злоупотреблений
6. Проверку на инъекции - защита от SQL, XSS и других инъекций
7. Механизм отката - возможность отменить ошибочные операции
8. Сессии и токены - для аутентификации и предотвращения CSRF
9. Проверку прав доступа - разные компоненты должны иметь разные уровни доступа
10. Изоляцию обработчиков - ошибки в одном компоненте не должны влиять на работу других
 */