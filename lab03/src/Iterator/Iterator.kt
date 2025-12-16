package Iterator

interface CatalogIterator<T> {
    fun hasNext(): Boolean
    fun next(): T
    fun next(count: Int): List<T>
    fun reset()
}

data class Product(
    val id: Int,
    val name: String,
    val category: String,
    val price: Double,
    val popularity: Int
)

class Catalog(private val products: List<Product>) {

    fun getCategoryIterator(): CategoryIterator {
        return CategoryIterator(products)
    }

    fun getPriceIterator(ascending: Boolean = true): PriceIterator {
        return PriceIterator(products, ascending)
    }

    fun getPopularityIterator(): PopularityIterator {
        return PopularityIterator(products)
    }

    fun filterByCategory(category: String): List<Product> {
        return products.filter { it.category == category }
    }

    fun filterByPrice(minPrice: Double, maxPrice: Double): List<Product> {
        return products.filter { it.price in minPrice..maxPrice }
    }

    fun filterByPopularity(minPopularity: Int): List<Product> {
        return products.filter { it.popularity >= minPopularity }
    }
}

class CategoryIterator(private val products: List<Product>) : CatalogIterator<Product> {
    private val categories = products.map { it.category }.distinct()
    private var currentCategoryIndex = 0
    private var currentProductIndex = 0

    override fun hasNext(): Boolean {
        return currentCategoryIndex < categories.size
    }

    override fun next(): Product {
        if (!hasNext()) throw NoSuchElementException("Нет больше товаров по категориям")

        val category = categories[currentCategoryIndex]
        val categoryProducts = products.filter { it.category == category }

        if (currentProductIndex >= categoryProducts.size) {
            currentCategoryIndex++
            currentProductIndex = 0
            return next()
        }

        val product = categoryProducts[currentProductIndex]
        currentProductIndex++

        if (currentProductIndex >= categoryProducts.size) {
            currentCategoryIndex++
            currentProductIndex = 0
        }

        return product
    }

    override fun next(count: Int): List<Product> {
        val result = mutableListOf<Product>()
        for (i in 0 until count) {
            if (hasNext()) {
                result.add(next())
            } else {
                break
            }
        }
        return result
    }

    override fun reset() {
        currentCategoryIndex = 0
        currentProductIndex = 0
    }
}

class PriceIterator(
    private val products: List<Product>,
    private val ascending: Boolean = true
) : CatalogIterator<Product> {
    private val sortedProducts = if (ascending) {
        products.sortedBy { it.price }
    } else {
        products.sortedByDescending { it.price }
    }
    private var currentIndex = 0

    override fun hasNext(): Boolean {
        return currentIndex < sortedProducts.size
    }

    override fun next(): Product {
        if (!hasNext()) throw NoSuchElementException("Нет больше товаров по цене")
        return sortedProducts[currentIndex++]
    }

    override fun next(count: Int): List<Product> {
        val result = mutableListOf<Product>()
        for (i in 0 until count) {
            if (hasNext()) {
                result.add(next())
            } else {
                break
            }
        }
        return result
    }

    override fun reset() {
        currentIndex = 0
    }
}

class PopularityIterator(private val products: List<Product>) : CatalogIterator<Product> {
    private val sortedProducts = products.sortedByDescending { it.popularity }
    private var currentIndex = 0

    override fun hasNext(): Boolean {
        return currentIndex < sortedProducts.size
    }

    override fun next(): Product {
        if (!hasNext()) throw NoSuchElementException("Нет больше товаров по популярности")
        return sortedProducts[currentIndex++]
    }

    override fun next(count: Int): List<Product> {
        val result = mutableListOf<Product>()
        for (i in 0 until count) {
            if (hasNext()) {
                result.add(next())
            } else {
                break
            }
        }
        return result
    }

    override fun reset() {
        currentIndex = 0
    }
}

fun main() {
    val products = listOf(
        Product(1, "Ноутбук", "Электроника", 50000.0, 85),
        Product(2, "Смартфон", "Электроника", 30000.0, 95),
        Product(3, "Футболка", "Одежда", 1500.0, 70),
        Product(4, "Книга", "Книги", 800.0, 60),
        Product(5, "Наушники", "Электроника", 7000.0, 80),
        Product(6, "Джинсы", "Одежда", 3500.0, 75),
        Product(7, "Учебник", "Книги", 1200.0, 55)
    )

    val catalog = Catalog(products)

    println("-------------------- Обход по категориям --------------------")
    val categoryIterator = catalog.getCategoryIterator()
    while (categoryIterator.hasNext()) {
        println(categoryIterator.next())
    }

    println("\n-------------------- Обход по цене (по возрастанию) --------------------")
    val priceIterator = catalog.getPriceIterator(true)
    priceIterator.reset()
    val firstThreeByPrice = priceIterator.next(3)
    firstThreeByPrice.forEach { println(it) }

    println("\n-------------------- Обход по популярности --------------------")
    val popularityIterator = catalog.getPopularityIterator()
    popularityIterator.reset()
    while (popularityIterator.hasNext()) {
        println(popularityIterator.next())
    }

    println("\n-------------------- Фильтрация товаров --------------------")
    println("Электроника: ${catalog.filterByCategory("Электроника").size} товаров")
    println("Дорогие товары (от 10000): ${catalog.filterByPrice(10000.0, Double.MAX_VALUE).size} товаров")
    println("Популярные товары (рейтинг >= 75): ${catalog.filterByPopularity(75).size} товаров")
}

/*
 Вопрос: Как вы будете обрабатывать ситуацию, когда в каталоге нет товаров, соответствующих определенному критерию? Какие изменения внесете в систему?
 Ответ: При отсутствии товаров, соответствующих критерию, итератор должен корректно обрабатывать эту ситуацию:
 1. hasNext() должен возвращать false,
 2. next() должен выбрасывать NoSuchElementException,
 3. можно добавить метод isEmpty() для проверки,
 4. в Catalog добавить методы проверки наличия товаров по критериям,
 5. можно реализовать Null Object Pattern, возвращая "пустой" итератор,
 6. добавить логирование для отслеживания таких ситуаций.
 */

