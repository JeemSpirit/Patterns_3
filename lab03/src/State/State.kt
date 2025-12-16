package State

abstract class OrderState(protected val order: Order6) {

    abstract fun processOrder()
    abstract fun getStatus(): String

    protected fun changeState(newState: OrderState) {
        order.state = newState
    }
}

class NewState(order: Order6) : OrderState(order) {

    override fun processOrder() {
        println("Заказ отправлен в обработку")
        changeState(ProcessingState(order))
    }

    override fun getStatus(): String = "Новый"
}

class ProcessingState(order: Order6) : OrderState(order) {

    override fun processOrder() {
        println("Заказ обработан и отправлен клиенту")
        changeState(ShippedState(order))
    }

    override fun getStatus(): String = "В обработке"
}

class ShippedState(order: Order6) : OrderState(order) {

    override fun processOrder() {
        println("Заказ доставлен клиенту")
        changeState(DeliveredState(order))
    }

    override fun getStatus(): String = "Отправлен"
}

class DeliveredState(order: Order6) : OrderState(order) {

    override fun processOrder() {
        println("Доставленный заказ не может быть обработан повторно")
    }

    override fun getStatus(): String = "Доставлен"
}

class CancelledState(order: Order6) : OrderState(order) {

    override fun processOrder() {
        println("Отмененный заказ нельзя обработать")
    }

    override fun getStatus(): String = "Отменен"
}


class Order6 {

    var state: OrderState = NewState(this)

    fun process() {
        state.processOrder()
    }

    fun getStatus(): String = state.getStatus()

    fun cancel() {
        if (state is DeliveredState) {
            println("Доставленный заказ нельзя отменить")
        } else {
            println("Заказ отменен")
            state = CancelledState(this)
        }
    }
}


fun main() {
    val order = Order6()

    println(order.getStatus())
    order.process()

    println(order.getStatus())
    order.process()

    println(order.getStatus())
    order.process()

    println(order.getStatus())
    order.cancel()
}

/*
 Ответ на вопрос:
 Корректность переходов между состояниями обеспечивается тем,
 что каждый класс состояния самостоятельно определяет,
 в какое следующее состояние можно перейти через метод changeState().
 Таким образом, код переходов изолирован внутри конкретного состояния,
 и невозможные переходы просто не реализованы.
 Например, состояние State.DeliveredState не вызывает смену состояния,
 поэтому возврат в более ранние этапы исключен.

 Для управления состояниями могут быть добавлены методы:
 1. cancel() — отмена заказа (реализовано в классе Template_Method.Order).
 2. returnToPrevious() или repeatProcessing() — только если логика бизнеса допускает подобные переходы.
 3. validateTransition(newState) — метод проверки корректности перехода,
    если требуется централизованный контроль.

 Паттерн State гарантирует, что корректность переходов обеспечивается на уровне самих состояний,
 так как каждое состояние знает, куда можно переходить, а куда нельзя.
 */
