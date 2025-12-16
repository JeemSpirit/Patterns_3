package Memento

class CartMemento(val state: List<String>)

class ShoppingCart {

    private val items = mutableListOf<String>()

    fun addItem(item: String) {
        items.add(item)
        println("Добавлен товар: $item")
    }

    fun removeItem(item: String) {
        items.remove(item)
        println("Удален товар: $item")
    }

    fun showItems() {
        println("Текущие товары: $items")
    }

    fun saveState(): CartMemento {
        return CartMemento(items.toList())
    }

    fun restoreState(memento: CartMemento) {
        items.clear()
        items.addAll(memento.state)
        println("Состояние корзины восстановлено")
    }
}

class CartHistory {

    private val history = ArrayDeque<CartMemento>()

    fun save(memento: CartMemento) {
        history.addLast(memento)
    }

    fun undo(): CartMemento? {
        return if (history.isNotEmpty()) history.removeLast() else null
    }
}

fun main() {
    val cart = ShoppingCart()
    val caretaker = CartHistory()

    cart.addItem("Телефон")
    caretaker.save(cart.saveState())

    cart.addItem("Наушники")
    caretaker.save(cart.saveState())

    cart.addItem("Чехол")
    cart.showItems()

    println("Отмена последнего действия")
    val prev = caretaker.undo()
    if (prev != null) {
        cart.restoreState(prev)
    }
    cart.showItems()
}

/*
 Ответ на вопрос:
 Для хранения нескольких точек сохранения используется структура данных стек или двусторонняя очередь.
 Каждый раз при изменении корзины создается новый объект Memento.CartMemento,
 и Caretaker добавляет его в историю. Метод undo() извлекает последний сохраненный memento,
 что позволяет выполнять многоуровневую отмену действий.

 Ограничения паттерна Memento:
 1. Большой расход памяти — каждый снимок хранит полный список товаров,
    и при частых изменениях корзины количество данных растет.
 2. Сложность восстановления сложных объектов — если состояние включает большие структуры,
    копирование их в memento становится дорогостоящим.
 3. Нет централизованной валидации — Memento не контролирует корректность состояния,
    поэтому система должна гарантировать, что снимки хранятся только валидные.
 4. При слишком большом количестве снимков может потребоваться очистка устаревших состояний,
    иначе система будет потреблять слишком много ресурсов.
 */
