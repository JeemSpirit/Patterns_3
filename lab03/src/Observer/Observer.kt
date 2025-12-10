package Observer

// Интерфейс наблюдателя
interface OrderObserver {
    fun update(orderId: Int, status: String)
}

// Конкретный наблюдатель: уведомление клиента
class ClientNotification(private val clientName: String) : OrderObserver {
    override fun update(orderId: Int, status: String) {
        println("Уведомление клиенту ($clientName): статус заказа №$orderId изменён на \"$status\".")
    }
}

// Конкретный наблюдатель: уведомление менеджера
class ManagerNotification(private val managerName: String) : OrderObserver {
    override fun update(orderId: Int, status: String) {
        println("Уведомление менеджеру ($managerName): заказ №$orderId получил статус \"$status\".")
    }
}

// Конкретный наблюдатель: аналитическая система
class AnalyticsSystem : OrderObserver {
    override fun update(orderId: Int, status: String) {
        println("Observer.AnalyticsSystem: заказ №$orderId получил статус \"$status\". Запись события выполнена.")
    }
}

// Исправленный класс (Subject)
class ObservableOrder(val id: Int) {

    private val observers = mutableListOf<OrderObserver>()

    var status: String = "Создан"
        set(value) {
            field = value
            notifyObservers()
        }

    fun addObserver(observer: OrderObserver) {
        observers.add(observer)
    }

    fun removeObserver(observer: OrderObserver) {
        observers.remove(observer)
    }

    private fun notifyObservers() {
        for (observer in observers) {
            observer.update(id, status)
        }
    }
}

fun main() {
    val order = ObservableOrder(10)

    val clientObserver = ClientNotification("Иванов Иван")
    val managerObserver = ManagerNotification("Петров Петр")
    val analyticsObserver = AnalyticsSystem()

    order.addObserver(clientObserver)
    order.addObserver(managerObserver)
    order.addObserver(analyticsObserver)

    order.status = "Оформлен"
    order.status = "В обработке"
    order.status = "Отправлен"
    order.status = "Доставлен"
    order.status = "Проверен"
}

// Ответ на вопрос:
// В рамках реализации паттерна Observer дополнительные классы или методы безопасности не требуются.
// Однако в реальной системе интернет-магазина необходимо добавить механизмы контроля доступа,
// чтобы сторонний код не мог произвольно менять статус заказа или подписываться на уведомления.
// Следует ограничить изменение статуса через специальный метод, закрыть прямой доступ к списку наблюдателей,
// а также реализовать логирование изменений и проверку корректности переходов статусов.
// Это обеспечивает безопасность данных, предотвращает несанкционированные изменения
// и гарантирует соблюдение бизнес-логики.

