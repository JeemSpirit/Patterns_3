package Visitor


// Элемент иерархии
interface Element {
    fun accept(visitor: Visitor)
}

// Товар
class Product(
    val name: String,
    val price: Double,
    val weight: Double
) : Element {

    override fun accept(visitor: Visitor) {
        visitor.visitProduct(this)
    }
}

// Коробка (содержит товары)
class Box(
    val items: List<Element>
) : Element {

    override fun accept(visitor: Visitor) {
        visitor.visitBox(this)
    }
}

// Базовый Visitor.Visitor
abstract class Visitor {
    abstract fun visitProduct(product: Product)
    abstract fun visitBox(box: Box)

    open fun visitDiscount(product: Product) {}
    open fun visitTax(product: Product) {}
}

// Конкретный посетитель — расчет стоимости доставки
class DeliveryCostCalculator : Visitor() {

    var totalCost = 0.0

    override fun visitProduct(product: Product) {
        totalCost += product.weight * 10
    }

    override fun visitBox(box: Box) {
        for (item in box.items) {
            item.accept(this)
        }
    }
}

// Конкретный посетитель — расчет налогов
class TaxCalculator : Visitor() {

    var totalTax = 0.0

    override fun visitProduct(product: Product) {
        totalTax += product.price * 0.2
    }

    override fun visitBox(box: Box) {
        for (item in box.items) {
            item.accept(this)
        }
    }

    override fun visitTax(product: Product) {
        totalTax += product.price * 0.2
    }
}

// Пример
fun main() {
    val p1 = Product("Телефон", 50000.0, 0.4)
    val p2 = Product("Ноутбук", 90000.0, 1.8)
    val box = Box(listOf(p1, p2))

    val deliveryVisitor = DeliveryCostCalculator()
    box.accept(deliveryVisitor)
    println("Стоимость доставки: ${deliveryVisitor.totalCost}")

    val taxVisitor = TaxCalculator()
    box.accept(taxVisitor)
    println("Налог: ${taxVisitor.totalTax}")
}

// Ответ на вопрос:
// Для расширения системы новыми типами расчетов (например, скидки)
// необходимо создать новый класс-посетитель, наследующий Visitor.Visitor,
// и реализовать в нем методы visitProduct(), visitBox(), и при необходимости
// visitDiscount() или visitTax(), если логика скидок требует их использования.
//
// При добавлении нового типа расчета не требуется изменять классы Iterator.Visitor.Product и Visitor.Box,
// так как паттерн Visitor.Visitor обеспечивает принцип открытости/закрытости:
// структура данных остается неизменной, меняются только посетители.
//
// Единственное изменение, которое требуется в коде — добавление нового класса Visitor.Visitor,
// реализующего соответствующую бизнес-логику.
// Логика скидок будет обрабатываться внутри нового посетителя,
// вызывая visitDiscount(product), если необходимо.
// Это позволяет расширять поведение системы без изменения существующих классов элементов.

