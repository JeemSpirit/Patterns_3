package Visitor

interface Item {
    fun accept(visitor: Visitor): Double
    fun getWeight(): Double
    fun getPrice(): Double
}

class Product(
    private val name: String,
    private val weight: Double,
    private val price: Double,
    private val isFragile: Boolean = false
) : Item {

    override fun accept(visitor: Visitor): Double {
        return visitor.visitProduct(this)
    }

    override fun getWeight(): Double = weight

    override fun getPrice(): Double = price

    fun isFragile(): Boolean = isFragile

    fun getName(): String = name
}

class Box(private val name: String) : Item {
    private val items = mutableListOf<Item>()

    fun addItem(item: Item) {
        items.add(item)
    }

    fun removeItem(item: Item) {
        items.remove(item)
    }

    fun getItems(): List<Item> = items

    override fun accept(visitor: Visitor): Double {
        return visitor.visitBox(this)
    }

    override fun getWeight(): Double {
        return items.sumOf { it.getWeight() }
    }

    override fun getPrice(): Double {
        return items.sumOf { it.getPrice() }
    }

    fun getName(): String = name
}

abstract class Visitor {
    abstract fun visitProduct(product: Product): Double
    abstract fun visitBox(box: Box): Double
}

class DeliveryCostCalculator(
    private val baseCost: Double = 100.0,
    private val costPerKg: Double = 10.0,
    private val fragileMultiplier: Double = 1.5
) : Visitor() {

    override fun visitProduct(product: Product): Double {
        var cost = baseCost + (product.getWeight() * costPerKg)
        if (product.isFragile()) {
            cost *= fragileMultiplier
        }
        println("Стоимость доставки товара '${product.getName()}': $cost руб.")
        return cost
    }

    override fun visitBox(box: Box): Double {
        var totalCost = baseCost
        var totalWeight = 0.0
        var hasFragile = false

        for (item in box.getItems()) {
            totalWeight += item.getWeight()
            if (item is Product && item.isFragile()) {
                hasFragile = true
            }
        }

        totalCost += totalWeight * costPerKg

        if (hasFragile) {
            totalCost *= fragileMultiplier
        }

        println("Стоимость доставки коробки '${box.getName()}': $totalCost руб.")
        return totalCost
    }
}

class TaxCalculator(
    private val productTaxRate: Double = 0.18,
    private val importTaxRate: Double = 0.10,
    private val isImported: Boolean = false
) : Visitor() {

    override fun visitProduct(product: Product): Double {
        var tax = product.getPrice() * productTaxRate
        if (isImported) {
            tax += product.getPrice() * importTaxRate
        }
        println("Налог на товар '${product.getName()}': $tax руб.")
        return tax
    }

    override fun visitBox(box: Box): Double {
        var totalTax = 0.0
        for (item in box.getItems()) {
            totalTax += item.accept(this)
        }
        println("Общий налог на коробку '${box.getName()}': $totalTax руб.")
        return totalTax
    }
}

fun main() {
    val laptop = Product("Ноутбук", 2.5, 50000.0, true)
    val phone = Product("Смартфон", 0.3, 30000.0)
    val book = Product("Книга", 0.5, 800.0)

    val electronicsBox = Box("Электроника")
    electronicsBox.addItem(laptop)
    electronicsBox.addItem(phone)

    val mainBox = Box("Основная коробка")
    mainBox.addItem(electronicsBox)
    mainBox.addItem(book)

    println("-------------------- Расчёт стоимости доставки --------------------")
    val deliveryCalculator = DeliveryCostCalculator()
    val totalDeliveryCost = mainBox.accept(deliveryCalculator)
    println("Общая стоимость доставки: $totalDeliveryCost руб.")

    println("\n-------------------- Расчёт налогов --------------------")
    val taxCalculator = TaxCalculator(isImported = true)
    val totalTax = mainBox.accept(taxCalculator)
    println("Общий налог: $totalTax руб.")

    println("\n-------------------- Информация о заказе --------------------")
    println("Общий вес: ${mainBox.getWeight()} кг")
    println("Общая цена товаров: ${mainBox.getPrice()} руб.")
    println("Итоговая стоимость: ${mainBox.getPrice() + totalDeliveryCost + totalTax} руб.")
}

/*
 Вопрос: Как вы будете расширять систему, добавляя новые типы расчетов (например, скидки)? Какие изменения потребуются в коде?
 Ответ: Для добавления новых типов расчетов (скидки, акции, страховка и т.д.) необходимо:
 1. Создать новый класс, наследующийся от Visitor
 2. Реализовать методы visitProduct() и visitBox() в новом классе
 3. При необходимости добавить новые методы в интерфейс Visitor (только если нужно расширить функциональность для всех посетителей)
 4. Для добавления скидок можно создать DiscountCalculator с логикой расчета скидок на основе различных условий
 Преимущество: не нужно изменять классы Product и Box, что соответствует принципу открытости/закрытости.
 */