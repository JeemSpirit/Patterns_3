package Chain_Of_Responsibility

// Модель запроса на возврат
data class ReturnRequest(
    val amount: Double,
    val reason: String
)

// Абстрактный обработчик
abstract class Handler {

    private var next: Handler? = null

    fun setNext(handler: Handler): Handler {
        next = handler
        return handler
    }

    fun handleRequest(request: ReturnRequest) {
        if (!process(request)) {
            next?.handleRequest(request)
                ?: handleUnprocessed(request)
        }
    }

    // Возвращает true, если обработчик взял ответственность за обработку
    protected abstract fun process(request: ReturnRequest): Boolean

    // Ситуация, когда запрос не был обработан ни одним обработчиком
    open fun handleUnprocessed(request: ReturnRequest) {
        println("Запрос не был обработан: $request")
    }
}

// Конкретный обработчик — менеджер
class ManagerHandler : Handler() {

    override fun process(request: ReturnRequest): Boolean {
        return if (request.amount <= 1000) {
            println("Менеджер обработал запрос на сумму ${request.amount}")
            true
        } else {
            false
        }
    }
}

// Конкретный обработчик — руководитель
class SupervisorHandler : Handler() {

    override fun process(request: ReturnRequest): Boolean {
        return if (request.amount in 1001.0..5000.0) {
            println("Руководитель обработал запрос на сумму ${request.amount}")
            true
        } else {
            false
        }
    }
}

// Конкретный обработчик — служба поддержки
class SupportHandler : Handler() {

    override fun process(request: ReturnRequest): Boolean {
        return if (request.reason.contains("брак", ignoreCase = true)) {
            println("Служба поддержки обработала запрос по причине: ${request.reason}")
            true
        } else {
            false
        }
    }
}

// Точка входа
fun main() {
    val manager = ManagerHandler()
    val supervisor = SupervisorHandler()
    val support = SupportHandler()

    manager.setNext(supervisor).setNext(support)

    val r1 = ReturnRequest(500.0, "Не подошло")
    val r2 = ReturnRequest(3000.0, "Неполная комплектация")
    val r3 = ReturnRequest(10000.0, "Не понравилось")
    val r4 = ReturnRequest(800.0, "Бракованный товар")

    manager.handleRequest(r1)
    manager.handleRequest(r2)
    manager.handleRequest(r3)
    manager.handleRequest(r4)
}

// Ответ на вопрос:
// Если запрос не обрабатывается ни одним из обработчиков,
// то в базовом классе Handler предусмотрен метод handleUnprocessed(),
// который вызывается, когда цепочка закончилась, а запрос так и не был обработан.
// По умолчанию он выводит сообщение о том, что запрос не обработан.
//
// Для улучшения системы можно внести следующие изменения:
//
// 1) Добавить централизованный обработчик "по умолчанию",
//    который будет заниматься всеми необработанными запросами.
//    Например, логирование, отправка в отдел ручной обработки,
//    уведомление руководства.
//
// 2) Переопределить метод handleUnprocessed()
//    в конкретном обработчике или создать отдельный класс для таких ситуаций.
//
// 3) Добавить проверку корректности запросов перед запуском цепочки.
//    Неверные запросы можно отбрасывать сразу.
//
// 4) Использовать исключения или специальные статусы,
//    чтобы сообщать системе о невозможности обработки.
//
// Таким образом, система будет устойчивой, а ни один запрос не останется без внимания,
// даже если ни один обработчик не смог его обработать.
