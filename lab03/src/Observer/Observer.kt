package Observer

interface Observer {
    fun update(order: Order)
}

interface Observable {
    fun addObserver(observer: Observer)
    fun removeObserver(observer: Observer)
    fun notifyObservers()
}

class Order(private var status: String) : Observable {
    private val observers = mutableListOf<Observer>()

    override fun addObserver(observer: Observer) {
        observers.add(observer)
    }

    override fun removeObserver(observer: Observer) {
        observers.remove(observer)
    }

    override fun notifyObservers() {
        observers.forEach { it.update(this) }
    }

    fun getStatus(): String = status

    fun setStatus(newStatus: String) {
        this.status = newStatus
        notifyObservers()
    }
}

class ClientNotification : Observer {
    override fun update(order: Order) {
        println("Клиент получил уведомление: Статус заказа изменен на '${order.getStatus()}'")
    }
}

class ManagerNotification : Observer {
    override fun update(order: Order) {
        println("Менеджер получил уведомление: Заказ перешел в статус '${order.getStatus()}'")
    }
}

class AnalyticsSystem : Observer {
    private val statusHistory = mutableListOf<String>()

    override fun update(order: Order) {
        statusHistory.add(order.getStatus())
        println("Аналитическая система: Зарегистрирован новый статус '${order.getStatus()}'. История статусов: $statusHistory")
    }
}

fun main() {
    val order = Order("Оформлен")

    val client = ClientNotification()
    val manager = ManagerNotification()
    val analytics = AnalyticsSystem()

    order.addObserver(client)
    order.addObserver(manager)
    order.addObserver(analytics)

    println("-------------------- Изменение статуса 1 --------------------")
    order.setStatus("В обработке")

    println("\n-------------------- Изменение статуса 2 --------------------")
    order.setStatus("Отправлен")

    println("\n-------------------- Изменение статуса 3 (при удалении менеджера) --------------------")
    order.removeObserver(manager)
    order.setStatus("Доставлен")
}

/*
 Вопрос: Есть ли необходимость добавления дополнительных классов или методов для обеспечения безопасности? Почему?
 Ответ: Да, необходимы. В текущей реализации есть несколько уязвимостей:
 1. публичный метод setStatus позволяет любому коду изменять статус заказа без валидации,
 2. метод getStatus возвращает прямой доступ к состоянию,
 3. отсутствует контроль над тем, кто может добавлять/удалять наблюдателей.
 Для безопасности нужны: валидация статусов, контроль доступа к методам изменения состояния, возможно использование приватных/защищенных модификаторов
 и фабричных методов для создания наблюдателей.
 */